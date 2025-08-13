package com.cashwu

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

@Serializable
data class GeocodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)

@Serializable
data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val current: CurrentWeather
)

@Serializable
data class CurrentWeather(
    val temp: Double,
    val humidity: Int,
    val weather: List<WeatherDescription>
)

@Serializable
data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String
)

object WeatherTool : SimpleTool<WeatherTool.Args>() {

    // 步驟 1：定義參數結構
    @Serializable
    data class Args(
        val city: String,           // 必填：城市名稱
        val country: String = "TW"  // 選填：國家代碼，預設台灣
    ) : ToolArgs

    override val argsSerializer = Args.serializer()

    // 步驟 2：設定工具描述
    override val descriptor = ToolDescriptor(
        name = "get_weather",
        description = "查詢指定城市的天氣資訊",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "city",
                description = "城市名稱（支援中文或英文）",
                type = ToolParameterType.String
            )
        ),
        optionalParameters = listOf(
            ToolParameterDescriptor(
                name = "country",
                description = "國家代碼，預設為 TW（台灣）",
                type = ToolParameterType.String
            )
        )
    )

    // 步驟 3：實作非同步執行邏輯
    override suspend fun doExecute(args: Args): String {
        return try {
            // 設定 5 秒超時，避免無限等待
            withTimeout(5000) {
                fetchWeatherData(args.city, args.country)
            }
        } catch (_: TimeoutCancellationException) {
            "⏱️ 天氣查詢超時，請稍後再試"
        } catch (e: Exception) {
            "❌ 無法獲取 ${args.city} 的天氣資訊：${e.message}"
        }
    }

    // 步驟 4：實作真實的天氣資料獲取
    private suspend fun fetchWeatherData(city: String, country: String): String {
        println("呼叫天氣工具, $city, $country")
        try {
            // 第 1 步：使用 Geocoding API 獲取城市座標
            val coordinates = getCoordinates(city, country)

            // 第 2 步：使用座標調用 One Call API 獲取天氣
            return callWeatherApi(coordinates.lat, coordinates.lon, city)

        } catch (e: Exception) {
            throw Exception("無法獲取 $city 的天氣資訊：${e.message}")
        }
    }

    /**
     * 使用 Geocoding API 將城市名稱轉換為座標
     */
    private suspend fun getCoordinates(city: String, country: String): GeocodingResponse {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val encodedCity = URLEncoder.encode(city, "UTF-8")
        val url = "https://api.openweathermap.org/geo/1.0/direct?q=$encodedCity,$country&limit=1&appid=${ApiKeyManager.openWeatherApiKey}"

        println(url)
        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body.string()
                    parseCoordinates(body, city)
                } else {
                    throw Exception("Geocoding API 調用失敗，狀態碼：${response.code}")
                }
            }
        }
    }

    /**
     * 解析 Geocoding API 回應獲取座標
     */
    private fun parseCoordinates(response: String, city: String): GeocodingResponse {
        val json = Json { ignoreUnknownKeys = true }
        val locations = json.decodeFromString<List<GeocodingResponse>>(response)

        if (locations.isNotEmpty()) {
            return locations[0]
        } else {
            throw Exception("找不到城市 $city 的地理位置")
        }
    }

    /**
     * 調用 OpenWeather One Call API 獲取天氣資料
     */
    private suspend fun callWeatherApi(lat: Double, lon: Double, cityName: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val url = "https://api.openweathermap.org/data/3.0/onecall?lat=$lat&lon=$lon&appid=${ApiKeyManager.openWeatherApiKey}&units=metric&lang=zh_tw&exclude=minutely,hourly,daily,alerts"

        println(url)
        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body.string()
                    parseWeatherData(body, cityName)
                } else {
                    throw Exception("天氣 API 調用失敗，狀態碼：${response.code}")
                }
            }
        }
    }

    /**
     * 解析天氣 API 回應
     */
    private fun parseWeatherData(response: String, cityName: String): String {
        try {
            val json = Json { ignoreUnknownKeys = true }
            val weather = json.decodeFromString<WeatherResponse>(response)

            val temperature = weather.current.temp.toInt()
            val humidity = weather.current.humidity
            val description = weather.current.weather.firstOrNull()?.description ?: "無資料"

            return """
            🌤️ $cityName 天氣
            🌡️ 溫度：${temperature}°C
            ☁️ $description
            💧 濕度：${humidity}%
            即時天氣資料來自 OpenWeather
        """.trimIndent()

        } catch (e: Exception) {
            throw Exception("解析天氣資料失敗：${e.message}")
        }
    }
}