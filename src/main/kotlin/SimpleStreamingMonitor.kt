package com.cashwu

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.TimeSource

class SimpleStreamingMonitor {

    data class StreamingStats(
        val totalTokens: Int,
        val duration: Duration,
        val firstTokenDelay: Duration
    )

    // 監控流式處理效能
    fun Flow<String>.withPerformanceTracking(): Flow<String> = flow {
        val startTime = TimeSource.Monotonic.markNow()
        var firstTokenTime: Duration? = null
        var tokenCount = 0

        collect { token ->
            tokenCount++

            // 記錄第一個 token 的時間
            if (firstTokenTime == null) {
                firstTokenTime = startTime.elapsedNow()
            }

            emit(token)
        }

        // 輸出統計資訊
        val totalDuration = startTime.elapsedNow()
        val stats = StreamingStats(
            totalTokens = tokenCount,
            duration = totalDuration,
            firstTokenDelay = firstTokenTime ?: Duration.ZERO
        )

        println("\n=== 效能統計 ===")
        println("總 Token 數：${stats.totalTokens}")
        println("總耗時：${stats.duration}")
        println("首 Token 延遲：${stats.firstTokenDelay}")
    }
}