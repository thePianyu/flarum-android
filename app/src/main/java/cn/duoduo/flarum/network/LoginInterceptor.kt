package cn.duoduo.flarum.network

import android.content.Context
import cn.duoduo.flarum.App
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class LoginInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var token = ""

        val sp = App.instance?.getSharedPreferences("Flarum", Context.MODE_PRIVATE)
        if (sp != null) {
            token = sp.getString("TOKEN", "") ?: ""
        }

        var request: Request = chain.request()
        request = request.newBuilder()
            .addHeader("Authorization", "Token $token")
            .build()

        return chain.proceed(request)
    }
}