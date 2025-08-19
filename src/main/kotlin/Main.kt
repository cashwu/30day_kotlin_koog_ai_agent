package com.cashwu

import ai.koog.agents.memory.model.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val secureStorageManager = SecureStorageManager()

    println("=== 加密存儲的 memory ===")

    // 建立安全的記憶體提供者
    val secureMemoryProvider = secureStorageManager.createSecureMemoryProvider()

    // 定義使用者資訊概念
    val userInfoConcept = Concept(
        "user-info",
        "使用者的基本資訊，包含姓名和偏好",
        FactType.SINGLE
    )

    // 使用者記憶體主題
    val userSubject = object : MemorySubject() {
        override val name: String = "user"
        override val promptDescription: String = "使用者的個人資訊和偏好設定"
        override val priorityLevel: Int = 1
    }

    val name = "cash"
    println("把使用者的姓名 $name 儲存到 memory")

    secureMemoryProvider.save(
        fact = SingleFact(
            concept = userInfoConcept,
            value = name,
            timestamp = System.currentTimeMillis()
        ),
        subject = userSubject,
        scope = MemoryScope.Product("secure-chat")
    )

    val userMemories = secureMemoryProvider.load(
        concept = userInfoConcept,
        subject = userSubject,
        scope = MemoryScope.Product("secure-chat")
    )

    val nameFromMemory = userMemories.firstOrNull()?.let { memory ->
        when (memory) {
            is SingleFact -> memory.value
            else -> null
        }
    }

    println("從 memory 載入使用者的姓名: $nameFromMemory")
}