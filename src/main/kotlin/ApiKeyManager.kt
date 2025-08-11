package com.cashwu

/**
 *
 * @author cash.wu
 * @since 2025/08/11
 *
 */
object ApiKeyManager {

    val openAIApiKey: String? = System.getenv("OPENAI_API_KEY")
    val googleApiKey: String? = System.getenv("GOOGLE_API_KEY")
    val ollamaBaseUrl: String? = System.getenv("OLLAMA_BASE_URL")

    // 檢查可用的供應商
    fun getAvailableProviders(): List<String> {
        val available = mutableListOf<String>()

        if (!openAIApiKey.isNullOrBlank()) {
            available.add("OpenAI")
        }

        if (!googleApiKey.isNullOrBlank()) {
            available.add("Google")
        }

        if (!ollamaBaseUrl.isNullOrBlank()) {
            available.add("Ollama")
        }

        return available
    }
}