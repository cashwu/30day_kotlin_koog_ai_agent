package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

/**
 *
 * @author cash.wu
 * @since 2025/08/19
 *
 */
class JokeGeneratorAgent {

    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = "你是一個專業的笑話產生助手",
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        strategy = createStrategy()
    )

    private fun createStrategy() = strategy<String, String>("joke-generator") {

        // 定義三個不同風格的笑話產生節點
        val formalJokeNode by node<String, String>("formal-joke") { topic ->
            llm.writeSession {
                updatePrompt {
                    system("你是一位優雅的幽默大師，請產生一個有趣但優雅的笑話")
                    user("主題：$topic")
                }
                val response = requestLLM()
                println("0 幽默大師: ${response.content}")
                response.content
            }
        }

        val casualJokeNode by node<String, String>("casual-joke") { topic ->
            llm.writeSession {
                updatePrompt {
                    system("你是一位輕鬆幽默的喜劇演員，請產生一個輕鬆好笑的笑話")
                    user("主題：$topic")
                }
                val response = requestLLM()
                println("1 喜劇演員: ${response.content}")
                response.content
            }
        }

        val creativeJokeNode by node<String, String>("creative-joke") { topic ->
            llm.writeSession {
                updatePrompt {
                    system("你是一位富有創意的幽默作家，請產生一個創意十足的笑話")
                    user("主題：$topic")
                }
                val response = requestLLM()
                println("2 幽默作家: ${response.content}")
                response.content
            }
        }

        // 使用 Parallel Node 同時產生三種笑話
        val bestJoke by parallel<String, String>(
            formalJokeNode, casualJokeNode, creativeJokeNode
        ) {
            selectByIndex { jokes ->
                // 使用另一個 LLM 評選最佳笑話
                llm.writeSession {
                    updatePrompt {
                        system(
                            """
                            你是一位專業的幽默評審。請從以下三個笑話中選出最好笑的一個。
                            評選標準：
                            1. 幽默程度
                            2. 創意性
                            3. 是否容易理解

                            請只回傳選中笑話的編號：0、1 或 2
                        """.trimIndent()
                        )
                        user("笑話選項：\n${jokes.mapIndexed { index, joke -> "$index: $joke" }.joinToString("\n\n")}")
                    }
                    val response = requestLLM()
                    println("評比 - ${response.content}")
                    response.content.trim().toIntOrNull() ?: 0
                }
            }
        }

        // 建立策略流程
        edge(nodeStart forwardTo bestJoke)
        edge(bestJoke forwardTo nodeFinish)
    }

    suspend fun generateJoke(topic: String): String {
        return agent.run(topic)
    }
}