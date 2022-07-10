package cn.duoduo.flarum.network

import android.content.Context
import cn.duoduo.flarum.App
import cn.duoduo.flarum.api.FlarumService
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    val flarumService: FlarumService by lazy {
        client.create(FlarumService::class.java)
    }

    const val BASE_URL = "https://pianyu.org/"

    private val client: Retrofit by lazy {

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(LoginInterceptor())
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(ChuckerInterceptor.Builder(App.instance as Context).build())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}