package com.zqf.imdevproject

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.gzdsy.faceauthsdk.utils.FaceCheckSDK
import com.zqf.imdevproject.network.HttpCallback
import com.zqf.imdevproject.network.HttpMethod
import com.zqf.imdevproject.network.RequestHelper
import com.zqf.imx.IMClient

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun initFaceAuth() {
        val url = "https://cloud-auth-demo.gcongo.com.cn/face/getCurrentChannel"
        val token =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJNVFU1TXprelp6YzRPalZsTlRWak5tTmlabVZpWm1Ka1lqWmpNalEwWXpOaU16RXhNVFE1T1RZMk5qaE9UVHBhUmtFPTZWN0ZYTlAwT1lLNSIsImlzcyI6ImQ5NjI1NWZkNjdmYTFjMmM1MzBlNzcxNzU1ZTdjOTdjIiwic3ViIjoiQVBQIiwiYXVkIjoiZHN5X3VPNXBmRmw5MXljbSIsImlhdCI6MTY5MjkzNDE1MSwiZXhwIjoxNjkzMDIwNTUxfQ.quk862UWqkyXVB0ZUB4Ep8RiyPASG5LKG9a5LVpD7Xw"
        val idNo = "522724199209020015"
        val name = "张庆发"
        FaceCheckSDK.newInstance().getCurrentCertifyChannel(this, url,
            token, "", idNo, name,
            object : FaceCheckSDK.ResultCallback {
                override fun onSuccess() {

                }

                override fun onFail(p0: String?) {

                }

            })
    }

    private fun initSocket() {
        Thread {
            IMClient.getInstance().initSocketConnect()
        }.start()
    }

    private fun initNetty() {
        IMClient.getInstance().initIMX()
    }

    private fun initOkhttp() {
        //OkHttpAnalyze.testReq()
        val headMap: HashMap<String, String> = HashMap()
        headMap["type"] = "list"
        RequestHelper.Builder(HttpMethod.GET, "http://www.baidu.com")
            .header(headMap)
            .callback(object : HttpCallback.StringCallback() {
                override fun onFailure(code: Int, errorMessage: String?) {
                    Log.e(TAG, errorMessage!!)
                }

                override fun onResponse(response: String?) {
                    Log.e(TAG, "response: $response")
                }

                override fun onAfter() {

                }
            }).execute()
    }

    private fun initPDF() {
//        val img = findViewById<ImageView>(R.id.pdfiv)
//        PDFUtil.mHandlePdf(this, img)
//        val pdf = findViewById<PDFView>(R.id.pdfview)
//        pdf.fromFile(PDFUtil.getPdfFile())
//            .defaultPage(0)
//            .onPageChange(this)
//            .enableAnnotationRendering(true)
//            .onLoad(this)
//            .spacing(10) // in dp
//            .onPageError(this)
//            .pageFitPolicy(FitPolicy.BOTH)
//            .onLongPress(this)
//            .load()
    }
}