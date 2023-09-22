package com.zqf.imdevproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class TestProtView extends View {
    private Paint backgroundPaint;
    private Paint scalePaint;
    private Paint textPaint;
    private float scaleWidth;
    private int scaleColor;
    private int textColor;
    private float textSize;

    public TestProtView(Context context) {
        this(context, null);
    }

    public TestProtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        // 初始化背景画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);

        // 初始化刻度画笔
        scalePaint = new Paint();
        scalePaint.setStrokeWidth(scaleWidth);
        scalePaint.setColor(scaleColor);

        // 初始化文本画笔
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 2f - scaleWidth / 2f;
        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(rectF, 0, 180, true, backgroundPaint);
        for (int angle = 0; angle <= 180; angle += 10) {
            float startAngle = angle;
            float endAngle = angle;
            float startX = (float) (centerX + radius * Math.cos(Math.toRadians(startAngle)));
            float startY = (float) (centerY - radius * Math.sin(Math.toRadians(startAngle)));
            float stopX = (float) (centerX + (radius - scaleWidth) * Math.cos(Math.toRadians(endAngle)));
            float stopY = (float) (centerY - (radius - scaleWidth) * Math.sin(Math.toRadians(endAngle)));
            canvas.drawLine(startX, startY, stopX, stopY, scalePaint);
            if (angle % 30 == 0) {
                float textX = (float) (centerX + (radius - scaleWidth - textSize) * Math.cos(Math.toRadians(angle)));
                float textY = (float) (centerY - (radius - scaleWidth - textSize) * Math.sin(Math.toRadians(angle)));
                canvas.save();
                canvas.rotate(-angle, textX, textY);
                canvas.drawText(String.valueOf(angle), textX, textY, textPaint);
                canvas.restore();
            }
        }
    }
}
