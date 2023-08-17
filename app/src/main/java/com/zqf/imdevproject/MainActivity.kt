package com.zqf.imdevproject

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnLongPressListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.zqf.imdevproject.http.OkHttpAnalyze
import com.zqf.imx.IMClient
import com.zqf.imx.utils.IMXConst

class MainActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener, OnLongPressListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNetty()
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
        OkHttpAnalyze.testReq()
    }

    private fun initPDF() {
//        val img = findViewById<ImageView>(R.id.pdfiv)
//        PDFUtil.mHandlePdf(this, img)
        val pdf = findViewById<PDFView>(R.id.pdfview)
        pdf.fromFile(PDFUtil.getPdfFile())
            .defaultPage(0)
            .onPageChange(this)
            .enableAnnotationRendering(true)
            .onLoad(this)
            .spacing(10) // in dp
            .onPageError(this)
            .pageFitPolicy(FitPolicy.BOTH)
            .onLongPress(this)
            .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun loadComplete(nbPages: Int) {

    }

    override fun onPageError(page: Int, t: Throwable?) {

    }

    override fun onLongPress(e: MotionEvent?) {
        Log.e("TAG", e.toString())
    }
}