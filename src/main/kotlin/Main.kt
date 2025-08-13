package com.cashwu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    println("🌤️ 非同步天氣查詢工具展示")
    println("=".repeat(40))

    val weatherAgent = WeatherAgent()

    println("\n📋 測試天氣查詢功能")
    println("-".repeat(40))

    val query = "台中市今天天氣如何？"
    val response = weatherAgent.queryWeather(query)
    println("使用者：$query")
    println("天氣助手：$response")

    println("\n🎊 測試完成！")
}
