package com.cashwu

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import kotlinx.io.files.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 *
 * @author cash.wu
 * @since 2025/08/14
 *
 */
data class MeetingRecord(
    val transcription: String,
    val summary: String,
    val actionItems: List<String>
)

@Serializable
data class MeetingAnalysis(
    val summary: String,
    val actionItems: List<String> = emptyList()
)

class MeetingTranscriber(private val client: OpenAILLMClient) {

    /**
     * 處理會議錄音，生成完整的會議記錄
     */
    suspend fun processMeetingAudio(audioPath: String): MeetingRecord {
        // 第一步：轉錄音訊內容
        val transcription = transcribeWithSpeakers(audioPath)
        println(transcription)
        // 第二步：分析轉錄內容，生成摘要和待辦事項
        val analysis = analyzeMeetingContent(transcription)

        return analysis
    }

    /**
     * 轉錄音訊並識別說話者
     */
    private suspend fun transcribeWithSpeakers(audioPath: String): String {
        val response = client.execute(
            prompt = prompt("multimodel") {
                user {
                    text(
                        """
                        請轉錄這段會議錄音的內容，並盡可能識別不同的說話者。
                        請使用以下格式：

                        [說話者A]: 說話內容
                        [說話者B]: 說話內容

                        如果無法區分說話者，可以使用 [說話者1]、[說話者2] 等標記。
                    """.trimIndent()
                    )

                    // 使用 attachments 區塊處理音訊
                    attachments {
                        if (isURL(audioPath)) {
                            audio(audioPath) // 網路 URL 直接使用
                        } else {
                            audio(Path(audioPath)) // 本地檔案路徑需使用 Path
                        }
                    }
                }
            },
            model = OpenAIModels.Audio.GPT4oMiniAudio
        )

        return response.joinToString { it.content }
    }

    /**
     * 判斷路徑是否為 URL
     */
    private fun isURL(path: String): Boolean {
        return path.startsWith("http://") || path.startsWith("https://")
    }

    /**
     * 分析會議內容，生成摘要和待辦事項
     */
    private suspend fun analyzeMeetingContent(transcription: String): MeetingRecord {
        val response = client.execute(
            prompt = prompt("multimodel") {
                user {
                    text(
                        """
                        請分析以下會議轉錄內容
                        `$transcription`
                        
                        請先幫我總結相關的「會議摘要」（3-5句話），和列出相關的「待辦事項」(如果有的話)
                        並且請使用以下的 JSON 格式回答
                        {
                            "summary": "會議摘要",
                            "actionItems": ["待辦事項1", "待辦事項2"]
                        }
                    """.trimIndent()
                    )
                }
            },
            model = OpenAIModels.CostOptimized.GPT4_1Mini
        )

        // 使用 kotlinx-serialization-json 解析 AI 回應
        val fullContent = response.joinToString("") { it.content }

        // 清理 Markdown 程式碼區塊標記
        val cleanedContent = fullContent
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            val analysis = Json.decodeFromString<MeetingAnalysis>(cleanedContent)
            MeetingRecord(
                transcription = transcription,
                summary = analysis.summary,
                actionItems = analysis.actionItems,
            )
        } catch (e: Exception) {
            println(e)
            // 如果 JSON 解析失敗，回退到預設值
            MeetingRecord(
                transcription = transcription,
                summary = "解析會議摘要時發生錯誤",
                actionItems = emptyList()
            )
        }
    }
}