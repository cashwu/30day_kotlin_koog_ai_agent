# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 語言設定
請全程使用**正體中文**回應使用者的所有問題和指令。

## 專案概述
這是一個基於 Ktor 框架的 AI 聊天服務專案，整合了 Koog AI 代理框架來提供 OpenAI GPT 聊天功能。

## 重要指令

### 建置與執行
- `./gradlew build` - 完整建置專案
- `./gradlew run` - 執行伺服器 (預設 port 8080)
- `./gradlew test` - 執行測試
- `./gradlew compileKotlin` - 僅編譯 Kotlin 程式碼（用於檢查語法錯誤）

### 其他有用指令
- `./gradlew buildFatJar` - 建立包含所有依賴的可執行 JAR
- `./gradlew buildImage` - 建立 Docker 映像檔
- `./gradlew runDocker` - 使用本地 Docker 映像檔執行

## 架構說明

### 核心檔案結構
- **Application.kt** - 主要應用程式進入點，設定模組載入順序
- **Routing.kt** - API 路由定義，包含聊天端點和健康檢查
- **AiChatService.kt** - AI 聊天服務核心邏輯，封裝 Koog AI 代理
- **Serialization.kt** - JSON 序列化設定

### API 端點
- `GET /` - 基本 Hello World 端點
- `GET /health` - 服務健康檢查
- `POST /api/chat` - 主要聊天功能端點

### 設定檔案
- **application.yaml** - 主要設定檔，包含 OpenAI API Key 設定
- 設定路徑：`ai.openai.api-key`

### 關鍵架構模式
1. **模組化設定** - 使用 `configureSerialization()` 和 `configureRouting()` 分離關注點
2. **AI 代理整合** - 透過 Koog AI 框架封裝 OpenAI 呼叫
3. **錯誤處理** - 在路由層級實作完整的例外處理和驗證
4. **資料驗證** - 聊天訊息長度限制 (1000 字元) 和空值檢查

### 重要注意事項
- ContentNegotiation 插件只在 Serialization.kt 中安裝一次，避免重複註冊
- 測試可能因為缺少測試設定檔而失敗，但這不影響主要應用程式功能
- OpenAI API Key 從環境設定載入，確保不會硬編碼敏感資訊

### 依賴關係
- **Koog AI** (`ai.koog:koog-agents:0.3.0`) - 核心 AI 代理框架
- **Ktor** - Web 框架和伺服器
- **kotlinx.serialization** - JSON 處理