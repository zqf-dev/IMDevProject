package com.zqf.imdevproject

import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initIM()
    }

    private fun initIM() {
        val img = findViewById<ImageView>(R.id.pdfiv)
        PDFUtil.mHandlePdf(img)
    }
}