package com.zqf.imdevproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.zqf.imdevproject.R;

public class AngleRulerView extends View {
    // 默认角度范围
    private static final float DEFAULT_MIN_ANGLE = 0f;
    private static final float DEFAULT_MAX_ANGLE = 180f;

    // 量角器默认高度和宽度
    private static final float DEFAULT_HEIGHT_DP = 100f;
    private static final float DEFAULT_WIDTH_DP = 300f;

    // 量角器的画笔
    private Paint mPaint;

    // 角度范围
    private float mMinAngle;
    private float mMaxAngle;

    // 当前选中的角度
    private float mCurrentAngle;

    // 量角器宽度和高度
    private float mViewWidth;
    private float mViewHeight;

    // 缩放因子和手指中心点
    private float mScaleFactor = 1.0f;
    private float mLastTouchX;
    private float mLastTouchY;

    public AngleRulerView(Context context) {
        this(context, null);
    }

    public AngleRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2f);

        // 获取XML布局文件中的属性
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AngleRulerView,
                defStyleAttr, 0);
        try {
            mMinAngle = a.getFloat(R.styleable.AngleRulerView_minAngle, DEFAULT_MIN_ANGLE);
            mMaxAngle = a.getFloat(R.styleable.AngleRulerView_maxAngle, DEFAULT_MAX_ANGLE);
        } finally {
            a.recycle();
        }

        // 设置当前选中的角度为最小值
        mCurrentAngle = mMinAngle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 测量View的大小
        int desiredWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_WIDTH_DP, getResources().getDisplayMetrics());
        int desiredHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_HEIGHT_DP, getResources().getDisplayMetrics());

        mViewWidth = resolveSize(desiredWidth, widthMeasureSpec);
        mViewHeight = resolveSize(desiredHeight, heightMeasureSpec);

        setMeasuredDimension((int) mViewWidth, (int) mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制量角器的刻度和标记
        float centerX = mViewWidth / 2;
        float centerY = mViewHeight / 2;
        float radius = mViewHeight / 4;

        float angleStep = (mMaxAngle - mMinAngle) / 10f;
        for (float angle = mMinAngle; angle <= mMaxAngle; angle += angleStep) {
            double radians = Math.toRadians(angle);
            float startX = (float) (centerX + (radius / 2) * Math.cos(radians));
            float startY = (float) (centerY + (radius / 2) * Math.sin(radians));
            float stopX = (float) (centerX + radius * Math.cos(radians));
            float stopY = (float) (centerY + radius * Math.sin(radians));

            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
            canvas.drawText(String.format("%.1f", angle), stopX - 20, stopY + 30, mPaint);
        }

        // 绘制选中的角度
        mPaint.setColor(Color.RED);
        double radians = Math.toRadians(mCurrentAngle);
        float startX = centerX;
        float startY = centerY;
        float stopX = (float) (centerX + radius * Math.cos(radians));
        float stopY = (float) (centerY + radius * Math.sin(radians));
        canvas.drawLine(startX, startY, stopX, stopY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 处理手指缩放和移动事件
        ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        scaleDetector.onTouchEvent(event);

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // 记录手指中心点
                mLastTouchX = getPointerX(event);
                mLastTouchY = getPointerY(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    // 单指滑动时旋转量角器
                    float currentTouchX = getPointerX(event);
                    float currentTouchY = getPointerY(event);

                    float deltaX = currentTouchX - mLastTouchX;
                    float deltaY = currentTouchY - mLastTouchY;

                    mCurrentAngle -= deltaX;
                    invalidate();

                    mLastTouchX = currentTouchX;
                    mLastTouchY = currentTouchY;
                }
                break;

            default:
                return super.onTouchEvent(event);
        }

        return true;
    }

    private float getPointerX(MotionEvent event) {
        int index = event.getActionIndex();
        return event.getX(index);
    }

    private float getPointerY(MotionEvent event) {
        int index = event.getActionIndex();
        return event.getY(index);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // 处理手指缩放事件，更新缩放因子
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            mViewWidth *= mScaleFactor;
            mViewHeight *= mScaleFactor;
            requestLayout();
            return true;
        }
    }

    // 公共方法，设置和获取当前选中的角度
    public void setCurrentAngle(float angle) {
        mCurrentAngle = angle < mMinAngle ? mMinAngle : angle > mMaxAngle ? mMaxAngle : angle;
        invalidate();
    }

    public float getCurrentAngle() {
        return mCurrentAngle;
    }
}

