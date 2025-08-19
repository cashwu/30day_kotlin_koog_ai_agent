package com.cashwu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val jokeGenerator = JokeGeneratorAgent()

    println("🎭 AI 笑話產生器啟動！")

    val topics = listOf("程式設計師", "貓咪")

    for (topic in topics) {
        println("\n📝 正在為主題「$topic」產生笑話...")
        val result = jokeGenerator.generateJoke(topic)
        println("🎉 最佳笑話：$result")
        println("-".repeat(50))
    }
}