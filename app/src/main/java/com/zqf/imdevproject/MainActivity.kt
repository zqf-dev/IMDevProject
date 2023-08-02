package com.zqf.imdevproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zqf.imx.IMX
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initIM()
    }

    private fun initIM() {
        thread {
            IMX.getInstance().initIMConnect()
        }
    }
}