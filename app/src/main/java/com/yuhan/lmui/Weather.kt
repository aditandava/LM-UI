package com.yuhan.lmui

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

// --- Models ---

data class WeatherData(
    val temp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val uvIndex: Double,
    val aqi: Int,
    val condition: String,
    val lastUpdated: Long,
    val locationName: String? = null
)

// OpenMeteo Weather Models
data class OpenMeteoWeather(val current: CurrentWeather)
data class CurrentWeather(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val wind_speed_10m: Double,
    val weather_code: Int
)

data class OpenMeteoAirQuality(val current: CurrentAirQuality)
data class CurrentAirQuality(val european_aqi: Int, val uv_index: Double)

// --- API Interface ---

interface WeatherApi {
    @GET("https://api.open-meteo.com/v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code"
    ): OpenMeteoWeather

    @GET("https://air-quality-api.open-meteo.com/v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "european_aqi,uv_index"
    ): OpenMeteoAirQuality
}

// --- Manager ---

class WeatherManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val api = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    suspend fun getWeatherData(lat: Double, lon: Double): WeatherData? = withContext(Dispatchers.IO) {
        try {
            val weatherResponse = api.getWeather(lat, lon)
            val airResponse = api.getAirQuality(lat, lon)

            // Strictly coordinates. No city names, no errors.
            val locationName = String.format(Locale.US, "%.2f, %.2f", lat, lon)

            val data = WeatherData(
                temp = weatherResponse.current.temperature_2m,
                humidity = weatherResponse.current.relative_humidity_2m,
                windSpeed = weatherResponse.current.wind_speed_10m,
                uvIndex = airResponse.current.uv_index,
                aqi = airResponse.current.european_aqi,
                condition = getWeatherCondition(weatherResponse.current.weather_code),
                lastUpdated = System.currentTimeMillis(),
                locationName = locationName
            )

            saveToStorage(data)
            saveLocation(lat, lon)
            data
        } catch (e: Exception) {
            e.printStackTrace()
            getCachedData()
        }
    }

    private fun saveToStorage(data: WeatherData) {
        prefs.edit().putString("cached_weather", gson.toJson(data)).apply()
    }

    fun getCachedData(): WeatherData? {
        val json = prefs.getString("cached_weather", null) ?: return null
        return try {
            val data = gson.fromJson(json, WeatherData::class.java)
            // Wipe bad data immediately
            if (data.locationName?.contains("Join", true) == true) {
                prefs.edit().remove("cached_weather").apply()
                null
            } else data
        } catch (e: Exception) {
            null
        }
    }

    private fun saveLocation(lat: Double, lon: Double) {
        prefs.edit()
            .putFloat("last_lat", lat.toFloat())
            .putFloat("last_lon", lon.toFloat())
            .apply()
    }

    fun getLastLocation(): Pair<Double, Double>? {
        if (!prefs.contains("last_lat") || !prefs.contains("last_lon")) return null
        return Pair(
            prefs.getFloat("last_lat", 0f).toDouble(),
            prefs.getFloat("last_lon", 0f).toDouble()
        )
    }

    private fun getWeatherCondition(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1, 2, 3 -> "Partly Cloudy"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rainy"
            71, 73, 75 -> "Snowy"
            80, 81, 82 -> "Rain Showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Unknown"
        }
    }

    fun getMagazineQuote(condition: String): String {
        return when {
            condition.contains("Clear", ignoreCase = true) -> "The sun is a daily reminder that we too can rise again from the darkness."
            condition.contains("Cloudy", ignoreCase = true) -> "Every cloud has a silver lining, even if it's hidden for a while."
            condition.contains("Rain", ignoreCase = true) || condition.contains("Drizzle", ignoreCase = true) -> "Life isn't about waiting for the storm to pass, it's about learning to dance in the rain."
            condition.contains("Fog", ignoreCase = true) -> "Sometimes you have to walk through the fog to see the beauty on the other side."
            condition.contains("Snow", ignoreCase = true) -> "To appreciate the beauty of a snowflake it is necessary to stand out in the cold."
            condition.contains("Thunder", ignoreCase = true) -> "The greater the storm, the brighter the rainbow."
            else -> "Nature does not hurry, yet everything is accomplished."
        }
    }
}
