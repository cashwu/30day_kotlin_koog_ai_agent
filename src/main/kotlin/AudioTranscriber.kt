package com.cashwu

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import kotlinx.io.files.Path

/**
 *
 * @author cash.wu
 * @since 2025/08/14
 *
 */
class AudioTranscriber(private val client: OpenAILLMClient) {

    /**
     * 判斷路徑是否為 URL
     */
    private fun isURL(path: String): Boolean {
        return path.startsWith("http://") || path.startsWith("https://")
    }

    /**
     * 轉錄音訊檔案內容
     * @param audioPath 音訊檔案路徑或 URL
     * @return 轉錄的文字內容
     */
    suspend fun transcribeAudio(audioPath: String): String {
        val response = client.execute(
            prompt = prompt("multimodel") {
                user {
                    text("請轉錄這段音訊的內容")
                    text("請提供準確的逐字轉錄，保持自然的句子結構，不要總結內容，直接給我音訊的逐字稿")

                    // 這是 Koog 的核心語法：在 attachments 區塊中處理音訊
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
}