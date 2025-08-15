package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.memory.feature.AgentMemory
import ai.koog.agents.memory.model.Concept
import ai.koog.agents.memory.model.FactType
import ai.koog.agents.memory.model.MemoryScope
import ai.koog.agents.memory.model.MemorySubject
import ai.koog.agents.memory.model.SingleFact
import ai.koog.agents.memory.providers.LocalFileMemoryProvider
import ai.koog.agents.memory.providers.LocalMemoryConfig
import ai.koog.agents.memory.storage.SimpleStorage
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.rag.base.files.JVMFileSystemProvider
import kotlin.io.path.Path

/**
 *
 * @author cash.wu
 * @since 2025/08/15
 *
 */
class PersonalizedGreeter {

    // 配置 memory provider
    private val memoryProvider = LocalFileMemoryProvider(
        config = LocalMemoryConfig("personalized-greeter"),
        storage = SimpleStorage(JVMFileSystemProvider.ReadWrite),
        fs = JVMFileSystemProvider.ReadWrite,
        root = Path("memory/user-data"),
    )

    // 定義使用者資訊概念
    private val userInfoConcept = Concept(
        "user-info",
        "使用者的基本資訊，包含姓名和偏好",
        FactType.SINGLE
    )

    // 使用者記憶體主題
    private val userSubject = object : MemorySubject() {
        override val name: String = "user"
        override val promptDescription: String = "使用者的個人資訊和偏好設定"
        override val priorityLevel: Int = 1
    }

    // 創建具備記憶體功能的 Agent
    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = createSystemPrompt(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    ) {
        // 安裝記憶體功能
        install(AgentMemory) {
            memoryProvider = this@PersonalizedGreeter.memoryProvider
            agentName = "personalized-greeter"        // Agent 識別名稱
            featureName = "personalized-greeter"      // 功能名稱
            organizationName = "demo-app"             // 組織名稱
            productName = "greeting-service"          // 產品名稱
        }
    }

    private fun createSystemPrompt() = """
        你是一個友善的個人化助手。

        核心能力：
        - 記住使用者的姓名和偏好
        - 提供個人化的問候和服務
        - 在初次見面時主動詢問並記住使用者資訊

        行為準則：
        - 如果知道使用者姓名，要親切地稱呼他們
        - 如果是新使用者，要禮貌地詢問姓名並記住
        - 始終保持友善和專業的態度
        - 使用正體中文回應
    """.trimIndent()

    /**
     * 處理使用者互動的主要方法
     */
    suspend fun greetUser(userInput: String): PersonalizedResponse {

        try {
            // 1. 嘗試從記憶體載入使用者資訊
            val userName = loadUserName()

            // 2. 根據是否有記憶決定回應方式
            val enhancedInput = if (userName != null) {
                // 有記憶：提供個人化上下文
                "使用者姓名：$userName\n使用者說：$userInput"
            } else {
                // 無記憶：正常處理
                userInput
            }

            // 3. 處理請求
            val response = agent.run(enhancedInput)

            // 4. 嘗試從回應中學習新資訊
            learnFromInteraction(userInput, response)

            return PersonalizedResponse(
                response = response,
                hasMemory = userName != null,
                userName = userName
            )

        } catch (e: Exception) {
            return PersonalizedResponse(
                response = "很抱歉，系統暫時無法處理您的請求。",
                hasMemory = false,
                error = e.message
            )
        }
    }

    /**
     * 從記憶體載入使用者姓名
     */
    private suspend fun loadUserName(): String? {
        return try {

            val userMemories = memoryProvider.load(
                userInfoConcept,
                userSubject,
                MemoryScope.Product("personalized-service")
            )

            userMemories.firstOrNull()?.let { memory ->
                when (memory) {
                    is SingleFact -> memory.value
                    else -> null
                }
            }
        } catch (e: Exception) {
            println("⚠️ 載入使用者記憶時發生錯誤: ${e.message}")
            null
        }
    }

    /**
     * 從互動中學習新資訊
     */
    private suspend fun learnFromInteraction(
        userInput: String,
        response: String
    ) {
        try {
            // 簡單的姓名識別邏輯
            when {
                userInput.contains("我是") || userInput.contains("我叫") -> {
                    val possibleName = extractNameFromInput(userInput)
                    if (possibleName != null) {
                        saveUserName(possibleName)
                    }
                }

                response.contains("請問您的姓名") || response.contains("可以告訴我您的名字") -> {
                    // AI 正在詢問姓名，暫不學習
                }
            }
        } catch (e: Exception) {
            println("⚠️ 學習過程中發生錯誤: ${e.message}")
        }
    }

    /**
     * 從使用者輸入中提取姓名
     */
    private fun extractNameFromInput(input: String): String? {
        val patterns = listOf(
            Regex("我是\\s*([^\\s,，]{2,4})"),
            Regex("我叫\\s*([^\\s,，]{2,4})"),
            Regex("叫我\\s*([^\\s,，]{2,4})")
        )

        for (pattern in patterns) {
            pattern.find(input)?.let { matchResult ->
                return matchResult.groupValues[1]
            }
        }
        return null
    }

    /**
     * 將使用者姓名儲存到記憶體
     */
    private suspend fun saveUserName(userName: String) {
        try {
            memoryProvider.save(
                SingleFact(
                    concept = userInfoConcept,
                    value = userName,
                    timestamp = System.currentTimeMillis()
                ),
                userSubject,
                MemoryScope.Product("personalized-service")
            )

            println("🧠 已記住使用者姓名：$userName")

        } catch (e: Exception) {
            println("⚠️ 儲存使用者資訊時發生錯誤: ${e.message}")
        }
    }
}

/**
 * 個人化回應資料類別
 */
data class PersonalizedResponse(
    val response: String,
    val hasMemory: Boolean,
    val userName: String? = null,
    val error: String? = null
)