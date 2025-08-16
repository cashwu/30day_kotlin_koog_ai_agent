package com.cashwu

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val apiKey = environment.config.property("ai.openai.api-key").getString()
    val aiChatService = AiChatService(apiKey)

    routing {

        get("/") {
            call.respondText("Hello World!")
        }

        // 聊天端點
        post("/api/chat") {
            try {
                val request = call.receive<ChatRequest>()

                // 驗證請求
                if (request.message.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "訊息不能為空")
                    )
                    return@post
                }

                if (request.message.length > 1000) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "訊息長度不能超過 1000 字元")
                    )
                    return@post
                }

                val response = aiChatService.chat(request)
                call.respond(response)

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "處理請求時發生錯誤：${e.message}")
                )
            }
        }
    }
}
