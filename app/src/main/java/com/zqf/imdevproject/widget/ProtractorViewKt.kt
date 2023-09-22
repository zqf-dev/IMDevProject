package com.zqf.imdevproject.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.zqf.imdevproject.R
import java.lang.Float.min
import java.lang.Math.*

class ProtractorViewKt @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private val TAG = ProtractorViewKt::class.java.canonicalName

    //刻度跟字
    private val drawPaint = Paint()

    //背景
    private val bgPaint = Paint()

    //刻度的宽度
    private var linesWidth = 0f

    //刻度的颜色
    private var linesColor = Color.BLACK

    //值的文本颜色
    private var valuesTextColor = Color.BLACK

    //值的文本大小
    private var valuesTextSize = 0f

    //每两个值之间的间隔数,也指多少个最小单位，比如0cm到1cm有10个最小单位1mm
    private var intervalsBetweenValues = 0

    //最短刻度长度为基准
    private var minLength = 0f

    //半径
    private var radius = 0f

    //矩形,方便定位
    private var rectF = RectF()

    private val offset = 30f

    init {
        val array = context!!.obtainStyledAttributes(attrs, R.styleable.ProtractorView)
        intervalsBetweenValues = array.getInt(R.styleable.ProtractorView_intervalsBetweenValues, 10)
        valuesTextSize =
            array.getDimensionPixelSize(R.styleable.ProtractorView_valuesTextSize, 4).toFloat()
        valuesTextColor = array.getColor(R.styleable.ProtractorView_valuesTextColor, Color.BLACK)
        linesWidth = array.getDimensionPixelSize(R.styleable.ProtractorView_linesWidth, 1).toFloat()
        linesColor = array.getColor(R.styleable.ProtractorView_linesColor, Color.BLACK)
        array.recycle()
        initView()
    }

    private fun initView() {
        bgPaint.color = Color.WHITE
        bgPaint.style = Paint.Style.FILL
        bgPaint.isAntiAlias = true

        drawPaint.color = Color.BLACK
        drawPaint.isAntiAlias = true
        drawPaint.textSize = valuesTextSize
        drawPaint.strokeWidth = linesWidth

    }

    /**
     * 假设中心点为 (x0, y0)，半径为 r
     * 使用以下公式计算选定角度的点的坐标：
     * x = x0 + r * cos(θ)
     * y = y0 + r * sin(θ)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //外部矩形x、y轴的中心点坐标（rectCenterX，rectCenterY）
        val rectCenterX = width / 2f
        val rectCenterY = height / 2f

        //绘制量角器所使用的底板矩形宽高
        val newWidth = width * 0.9f
        val newHeight = height * 0.9f
        Log.e(TAG, "rectCenterX: $rectCenterX")
        Log.e(TAG, "rectCenterY: $rectCenterY")
        Log.e(TAG, "newWidth: $newWidth")
        Log.e(TAG, "newHeight: $newHeight")
        rectF.set(
            rectCenterX - newWidth / 2f,
            rectCenterY - newHeight / 2f,
            rectCenterX + newWidth / 2f,
            rectCenterY + newHeight / 2f
        )
        Log.e(TAG, "rectF: $rectF")
        radius = min(rectF.width() / 2f, rectF.height())
        minLength = radius / 14f
        val centerX = width / 2f
        val centerY = rectF.height()

        drawPaint.color = linesColor
        canvas?.apply {
            //先画半圆
            save()
            clipOutRect(0f, centerY + offset, width.toFloat(), height.toFloat())
            drawCircle(centerX, centerY, radius, bgPaint)
            restore()
            //1°等于PI/180
            val unit = PI / 180
            var angle = 0f
            while (angle <= 180f) {
                var needDrawText = false
                val startX = centerX + radius * cos(angle * unit).toFloat()
                val startY = centerY - radius * sin(angle * unit).toFloat()
                var endX: Float
                var endY: Float
                var lineWidth: Float
                if (angle % (intervalsBetweenValues / 2f) == 0f) {
                    if (angle % intervalsBetweenValues == 0f) {
                        //画长线
                        lineWidth = minLength * 2f
                        needDrawText = true
                    } else {
                        //画中线
                        lineWidth = minLength * 1.5f
                    }
                } else {
                    //画短线
                    lineWidth = minLength
                }
                endX = centerX + ((radius - lineWidth) * cos(angle * unit).toFloat())
                endY = centerY - ((radius - lineWidth) * sin(angle * unit).toFloat())
                drawLine(startX, startY, endX, endY, drawPaint)
                if (needDrawText) {
                    drawPaint.color = valuesTextColor
                    val startTextX =
                        centerX + ((radius - lineWidth * 1.5f) * cos(angle * unit).toFloat())
                    val startTextY =
                        centerY - ((radius - lineWidth * 1.5f) * sin(angle * unit).toFloat())
                    val valueString = angle.toInt().toString() + "°"
                    val textWidth: Float = drawPaint.measureText(valueString)
                    val textHeight: Float = drawPaint.descent() - drawPaint.ascent()
                    val textCenterX = startTextX + textWidth / 2 * cos(angle * unit).toFloat()
                    val textCenterY = startTextY - textHeight / 2 * sin(angle * unit).toFloat()
                    val textX = textCenterX - textWidth / 2
                    val textY = textCenterY + textHeight / 2
                    // 绘制旋转的文本
                    save()
                    rotate(90f - angle, textCenterX, textCenterY)
                    drawText(
                        valueString,
                        textX,
                        textY,
                        drawPaint
                    )
                    restore()
                    drawPaint.color = linesColor
                }
                angle++
            }
        }
    }
}
