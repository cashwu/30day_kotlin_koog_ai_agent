package com.cashwu

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import com.cashwu.ResearchPlanExecuteAgentTool.WebSearchTool.*
import kotlinx.serialization.Serializable

/**
 *
 * @author cash.wu
 * @since 2025/08/18
 *
 */
class ResearchPlanExecuteAgentTool {

    class WebSearchTool : SimpleTool<Args>() {
        @Serializable
        class Args(val query: String) : ToolArgs

        override val argsSerializer = Args.serializer()
        override val descriptor = ToolDescriptor("web_search", "搜尋網路資訊")

        override suspend fun doExecute(args: Args): String {
            // 模擬網路搜尋
            println("🔍 搜尋：${args.query}")
            return when {
                args.query.contains("市場") -> "市場調查顯示：產品需求正在增長，競爭對手較少"
                args.query.contains("技術") -> "技術分析：使用 Kotlin 和 AI 整合具有良好前景"
                args.query.contains("成本") -> "成本分析：預估開發成本約 100 萬，維護成本每月 5 萬"
                else -> "找到相關資訊：${args.query} 的搜尋結果"
            }
        }
    }

    class DataAnalysisTool : SimpleTool<DataAnalysisTool.Args>() {
        @Serializable
        class Args(val data: String, val analysisType: String) : ToolArgs

        override val argsSerializer = Args.serializer()
        override val descriptor = ToolDescriptor("data_analysis", "資料分析工具")

        override suspend fun doExecute(args: Args): String {
            println("📊 分析資料：${args.analysisType}")
            return when (args.analysisType) {
                "趨勢" -> "趨勢分析：市場呈現上升趨勢，成長率約 15%"
                "風險" -> "風險評估：主要風險為技術變化快速，建議持續關注"
                "預測" -> "預測結果：預計 6 個月內可完成開發，12 個月內回收成本"
                else -> "分析完成：${args.data} 的 ${args.analysisType} 分析結果"
            }
        }
    }

    class ProjectExecutionTool : SimpleTool<ProjectExecutionTool.Args>() {
        @Serializable
        class Args(val taskDescription: String, val priority: String) : ToolArgs

        override val argsSerializer = Args.serializer()
        override val descriptor = ToolDescriptor(
            "execute_task",
            "執行專案任務。當需要實際執行計劃中的具體任務時使用此工具，例如：建立團隊、開發功能、設計架構等"
        )

        override suspend fun doExecute(args: Args): String {
            println("⚡ 執行任務：${args.taskDescription}（優先級：${args.priority}）")
            return "任務已開始執行：${args.taskDescription}，預計完成時間根據優先級${args.priority}進行排程"
        }
    }

    class QualityCheckTool : SimpleTool<QualityCheckTool.Args>() {
        @Serializable
        class Args(val item: String, val criteria: String) : ToolArgs

        override val argsSerializer = Args.serializer()
        override val descriptor = ToolDescriptor(
            "quality_check",
            "進行品質檢查。每當任務執行完成後，必須使用此工具檢查輸出品質，例如：功能測試、程式碼審查、系統驗證等"
        )

        override suspend fun doExecute(args: Args): String {
            println("✅ 品質檢查：${args.item}")
            return "品質檢查通過：${args.item} 符合 ${args.criteria} 標準"
        }
    }
}