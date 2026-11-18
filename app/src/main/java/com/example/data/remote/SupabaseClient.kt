package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class SupabaseFarmBoundaryDto(
    val id: String,
    val farmer_id: String,
    val farm_name: String,
    val polygon_coordinates: List<SupabaseCoordDto>,
    val area_acres: Double,
    val area_hectares: Double,
    val center_lat: Double,
    val center_lng: Double,
    val created_at: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseCoordDto(
    val lat: Double,
    val lng: Double
)

@JsonClass(generateAdapter = true)
data class SupabaseOperatorLocationDto(
    val id: String,
    val name: String,
    val role_type: String, // 'drone_operator' | 'drone_mitra'
    val lat: Double,
    val lng: Double,
    val drone_type: String = "quadcopter",
    val is_available: Boolean = true,
    val heading_degrees: Float = 0f,
    val updated_at: String? = null
)

interface SupabasePostgrestApi {
    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("rest/v1/farm_boundaries")
    suspend fun upsertFarmBoundary(
        @Body body: SupabaseFarmBoundaryDto
    ): Response<List<SupabaseFarmBoundaryDto>>

    @GET("rest/v1/farm_boundaries?order=created_at.desc")
    suspend fun getFarmBoundariesForFarmer(
        @Query("farmer_id") farmerIdFilter: String
    ): Response<List<SupabaseFarmBoundaryDto>>

    @GET("rest/v1/operator_locations?select=*")
    suspend fun getOperatorLocations(): Response<List<SupabaseOperatorLocationDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("rest/v1/operator_locations")
    suspend fun upsertOperatorLocation(
        @Body body: SupabaseOperatorLocationDto
    ): Response<List<SupabaseOperatorLocationDto>>
}

object SupabaseClient {

    fun isConfigured(): Boolean {
        val url = BuildConfig.SUPABASE_URL.trim()
        val key = BuildConfig.SUPABASE_ANON_KEY.trim()
        return url.startsWith("https://") &&
            !url.contains("your-project") &&
            key.isNotBlank() &&
            key != "MY_SUPABASE_ANON_KEY"
    }

    fun realtimeWebSocketUrl(): String {
        val base = BuildConfig.SUPABASE_URL.trim().trimEnd('/')
        val wsBase = base.replace("https://", "wss://").replace("http://", "ws://")
        return "$wsBase/realtime/v1/websocket?apikey=${BuildConfig.SUPABASE_ANON_KEY.trim()}&vsn=1.0.0"
    }

    val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val key = BuildConfig.SUPABASE_ANON_KEY.trim()
                val req = chain.request().newBuilder()
                    .header("apikey", key)
                    .header("Authorization", "Bearer $key")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .build()
    }

    val api: SupabasePostgrestApi? by lazy {
        if (!isConfigured()) return@lazy null
        val baseUrl = BuildConfig.SUPABASE_URL.trim().trimEnd('/') + "/"
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabasePostgrestApi::class.java)
    }
}
