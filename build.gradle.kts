plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.cashwu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // 只需要這個 Koog 套件
    implementation("ai.koog:koog-agents:0.3.0")

    // HTTP 客戶端 - 用於 API 調用
    implementation("com.squareup.okhttp3:okhttp:5.1.0")

    // JSON 序列化 - 用於 API 回應解析
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.53.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}