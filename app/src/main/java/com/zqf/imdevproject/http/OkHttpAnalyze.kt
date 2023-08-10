package com.zqf.imdevproject.http

import android.util.Log
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class OkHttpAnalyze {
    companion object {
        private const val url = "http://www.baidu.com"
        fun testReq() {
            //创建客户端
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .build()

            //配置URL
            val request: Request = Request.Builder()
                .url(url)
                .build()

            //回调
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //失败
                    Log.e("TAG", e.printStackTrace().toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    //成功
                    Log.e("TAG", response.body!!.string())
                }
            })
        }
    }
}