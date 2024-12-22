package com.example.jetpackcompose.api

import android.util.Log
import com.example.jetpackcompose.data.ForecastData
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WeatherApiService {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(WeatherApi::class.java)

    interface WeatherApi {
        @GET("weather")
        suspend fun fetchWeather(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): retrofit2.Response<WeatherData>

        @GET("forecast")
        suspend fun fetchForecast(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): retrofit2.Response<ForecastData>
    }

    suspend fun fetchWeather(city: String, apiKey: String): WeatherData? {
        return try {
            withContext(Dispatchers.Default) {
                val response = api.fetchWeather(city, apiKey)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("WeatherApiService", "Failed to fetch data: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error fetching data: ${e.message}")
            null
        }
    }

    /**
     * Fetches the weather forecast data for a given city using the OpenWeather API
     *
     * @param city The name of the city for which the forecast data should be retrieved
     * @param apiKey The API key required for authentication with the OpenWeather API
     * @return An instance of ForecastData containing the forecast information, or null if the request fails
     */
    suspend fun fetchForecast(city: String, apiKey: String): ForecastData? {
        return try {
            withContext(Dispatchers.IO) { // Use Dispatchers.IO for I/O-bound tasks such as network calls
                val response = api.fetchForecast(city, apiKey)
                if (response.isSuccessful) {
                    response.body() // Return the forecast data if successful
                } else {
                    Log.e("WeatherApiService", "Failed to fetch forecast data: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            // Log any exceptions that occur during the request
            Log.e("WeatherApiService", "Error fetching forecast data: ${e.message}")
            null
        }
    }
}
