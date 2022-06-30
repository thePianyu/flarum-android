package cn.duoduo.flarum.network

import cn.duoduo.flarum.api.FlarumService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    val flarumService: FlarumService by lazy {
        client.create(FlarumService::class.java)
    }

    private const val BASE_URL = "https://pianyu.org/"

    private val client: Retrofit by lazy {

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}