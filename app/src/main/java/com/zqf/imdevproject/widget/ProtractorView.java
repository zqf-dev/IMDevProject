package com.zqf.imdevproject.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.zqf.imdevproject.R;

import static java.lang.Float.min;

/**
 * 量角器视图
 */
public class ProtractorView extends View {
    //刻度+字体
    private Paint drawPaint = null;
    //绘制背景的
    private Paint bgPaint = null;
    //刻度的宽度
    private float linesWidth = 0f;
    //刻度的颜色
    private int linesColor = Color.BLACK;
    //值的文本颜色
    private int valuesTextColor = Color.BLACK;
    //值的文本大小
    private float valuesTextSize = 0f;
    //每两个值之间的间隔数,也指多少个最小单位，比如0cm到1cm有10个最小单位1mm
    private int intervalsBetweenValues = 0;
    //最短刻度长度为基准
    private float minLength = 0f;
    //半径
    private float radius = 0f;
    //矩形,方便定位
    private RectF rectF = new RectF();
    private float offset = 30f;

    public ProtractorView(Context context) {
        super(context);
    }

    public ProtractorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProtractorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    @SuppressLint("Recycle")
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // 获取到attrs中的自定义属性
        TypedArray mTArray = context.obtainStyledAttributes(attrs, R.styleable.ProtractorView);
        intervalsBetweenValues = mTArray.getInt(R.styleable.ProtractorView_intervalsBetweenValues, 10);
        valuesTextSize = (float) mTArray.getDimensionPixelSize(R.styleable.ProtractorView_valuesTextSize, 4);
        valuesTextColor = mTArray.getColor(R.styleable.ProtractorView_valuesTextColor, Color.BLACK);
        linesWidth = (float) mTArray.getDimensionPixelSize(R.styleable.ProtractorView_linesWidth, 1);
        linesColor = mTArray.getColor(R.styleable.ProtractorView_linesColor, Color.BLACK);
        mTArray.recycle();
        //初始化
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        bgPaint.setAntiAlias(true);
        drawPaint.setTextSize(valuesTextSize);
        drawPaint.setStrokeWidth(linesWidth);

        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);
        bgPaint.setAntiAlias(true);
    }

    /**
     * 假设中心点为 (x0, y0)，半径为 r
     * 使用以下公式计算选定角度的点的坐标：
     * x = x0 + r * cos(θ)
     * y = y0 + r * sin(θ)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float rectCenterX = getWidth() / 2f;
        float rectCenterY = getHeight() / 2f;

        float newWidth = getWidth() * 0.9f;
        float newHeight = getHeight() * 0.9f;

        rectF.set(rectCenterX - newWidth / 2f,
                rectCenterY - newHeight / 2f,
                rectCenterX + newWidth / 2f,
                rectCenterY + newHeight / 2f);
        radius = min(rectF.width() / 2f, rectF.height());
    }
}
