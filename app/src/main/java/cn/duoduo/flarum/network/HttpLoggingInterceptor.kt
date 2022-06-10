package cn.duoduo.flarum.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HttpLoggingInterceptor : Interceptor {
    @Throws(IOException::class)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("OkHttp", "url = ${request.url()}")
        return chain.proceed(request)
    }
}