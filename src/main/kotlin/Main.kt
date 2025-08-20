package com.cashwu

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.embeddings.local.OllamaEmbeddingModels
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.ollama.client.OllamaClient
import org.jetbrains.annotations.ApiStatus

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
// Kotlin 實作
    val kotlinCode = """
        fun fibonacci(n: Int): Int {
            return if (n <= 1) n else fibonacci(n - 1) + fibonacci(n - 2)
        }
    """.trimIndent()

    // Python 實作
    val pythonCode = """
        def fibonacci(n):
            if n <= 1:
                return n
            else:
                return fibonacci(n-1) + fibonacci(n-2)
    """.trimIndent()

    // Java 的泡沫排序（不同演算法）
    val javaCode = """
        public static int bubbleSort(int[] arr) {
            int n = arr.length;
            for (int i = 0; i < n-1; i++) {
                for (int j = 0; j < n-i-1; j++) {
                    if (arr[j] > arr[j+1]) {
                        int temp = arr[j];
                        arr[j] = arr[j+1];
                        arr[j+1] = temp;
                    }
                }
            }
            return arr;
        }
    """.trimIndent()

    // 建立 OpenAI 用戶端
    val client = OpenAILLMClient(ApiKeyManager.openAIApiKey!!)

    // 建立 embedder，使用 TextEmbedding3Small 模型
    val embedder = LLMEmbedder(client, OpenAIModels.Embeddings.TextEmbedding3Small)

    // 產生embedding向量
    val kotlinEmbedding = embedder.embed(kotlinCode)
    val pythonEmbedding = embedder.embed(pythonCode)
    val javaEmbedding = embedder.embed(javaCode)

    // 計算相似度
    val kotlinPythonDiff = embedder.diff(kotlinEmbedding, pythonEmbedding)
    val kotlinJavaDiff = embedder.diff(kotlinEmbedding, javaEmbedding)

    println("Kotlin 與 Python 的差異：$kotlinPythonDiff")
    println("Kotlin 與 Java 的差異：$kotlinJavaDiff")

    // 判斷最相似的程式碼
    if (kotlinPythonDiff < kotlinJavaDiff) {
        println("Kotlin 程式碼與 Python 實作較相似")
        println("原因：兩者都是 Fibonacci 遞迴實作")
    } else {
        println("Kotlin 程式碼與 Java 實作較相似")
    }
}