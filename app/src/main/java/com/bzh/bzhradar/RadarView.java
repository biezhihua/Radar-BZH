package com.bzh.bzhradar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　音悦台 版权所有(c) 2015 <br>
 * <b>作者</b>：　　　别志华 biezhihua@163.com<br>
 * <b>创建日期</b>：　15-12-7 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */
public class RadarView extends View {

    // 各个顶角的名称
    private String[] mTitles;

    // 各项数值的数组，必须和mTitle.length()相等
    private int[] mValues;

    // 默认最大值
    private int mDefaultMaxValue = 100;

    // 默认参数
    private int mNormalPolygonVertexNumber; // 正多边形顶角个数
    private double mOffsetAngle; // 偏移角度
    private float mMaxRadius; // 最大半径
    private int mCenterX;    // 横纵中心坐标
    private int mCenterY;

    // 绘制正多边形相关
    private Path mNormalPolygonPath;
    private Paint mNormalPolygonPaint;

    // 绘制各个角的文字画笔
    private Paint mTextPaint;
    private Canvas canvasRotate;
    private float degrees;

    private Path mRegionPath;
    private Paint mCirclePaint;
    private Paint mValuePaint;

    public RadarView(Context context) {
        super(context);
        init(context);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RadarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {

        // 绘制正多边形路径
        mNormalPolygonPath = new Path();
        mRegionPath = new Path();
        // 绘制正多边形路径画笔
        mNormalPolygonPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mNormalPolygonPaint.setColor(Color.GRAY);
        mNormalPolygonPaint.setStrokeWidth(3);
        mNormalPolygonPaint.setStyle(Paint.Style.STROKE);

        // 文字画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mValuePaint.setStyle(Paint.Style.STROKE);
        mValuePaint.setStrokeWidth(3);
        mValuePaint.setStrokeCap(Paint.Cap.ROUND);
        mValuePaint.setColor(Color.BLUE);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int saveID_1 = canvas.saveLayer(0, 0, mCenterX * 2, mCenterY * 2, null, Canvas.ALL_SAVE_FLAG);

        // 重置画布中心为屏幕中心
        canvas.translate(mCenterX, mCenterY);

        // 设置旋转角度
        setCanvasRotate(canvas);

        drawNormalPolygon(canvas);

        drawCenterToVertexConnectLine(canvas);

        drawEachVertexText(canvas);

        drawRegion(canvas);

        canvas.restoreToCount(saveID_1);

    }

    // 画区域
    private void drawRegion(Canvas canvas) {
        for (int i = 0; i < mNormalPolygonVertexNumber; i++) {

            // 计算出值占总值的百分比
            float precent = mValues[i] * 1.0f / mDefaultMaxValue;

            // 偏移角度
            double offsetAngle = mOffsetAngle * i;

            float pointX = (float) (mMaxRadius * Math.cos(offsetAngle) * precent);
            float pointY = (float) (mMaxRadius * Math.sin(offsetAngle) * precent);
            if (0 == i) {
                mRegionPath.reset();
                mRegionPath.moveTo(pointX, pointY);
            } else {
                mRegionPath.lineTo(pointX, pointY);
            }
            canvas.drawCircle(pointX, pointY, 8, mCirclePaint);
        }

        mRegionPath.close();
        canvas.drawPath(mRegionPath, mValuePaint);

        mValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mValuePaint.setAlpha(127);
        canvas.drawPath(mRegionPath, mValuePaint);
    }

    // 画各个角对应的文字
    // 默认从最右方开始
    private void drawEachVertexText(Canvas canvas) {
        // 文字尺寸
        mTextPaint.setTextSize(mMaxRadius * 0.15F);

        // 文字高度
        float textHeight = (mTextPaint.descent() - mTextPaint.ascent());

        for (int i = 0; i < mNormalPolygonVertexNumber; i++) {

            // 偏移角度
            double offsetAngle = mOffsetAngle * i;

            // 下一个点位置
            float nextVertexX = (float) (mMaxRadius * Math.cos(offsetAngle));
            float nextVertexY = (float) (mMaxRadius * Math.sin(offsetAngle));

            // 文字宽度
            float textWidth = mTextPaint.measureText(mTitles[i]);

            int saveLayerLength = (int) Math.sqrt(textWidth * textWidth + textHeight * textHeight);

            int layerCenterX = 0;
            int layerCenterY = 0;

            if (offsetAngle > 0 && offsetAngle < Math.PI / 2) {
                // 第四象限
                layerCenterX = (int) (nextVertexX + textWidth / 2);
                layerCenterY = (int) (nextVertexY + textHeight);
            } else if (offsetAngle > Math.PI / 2 && offsetAngle < Math.PI) {
                // 第三象限
                layerCenterX = (int) (nextVertexX - textWidth / 2);
                layerCenterY = (int) (nextVertexY + textHeight);
            } else if (offsetAngle > Math.PI && offsetAngle < Math.PI * 3 / 2) {
                // 第二象限
                layerCenterX = (int) (nextVertexX - textWidth / 2);
                layerCenterY = (int) (nextVertexY - (Math.abs(mTextPaint.ascent())) / 2);
            } else if (offsetAngle > Math.PI * 3 / 2 && offsetAngle < Math.PI * 2) {
                // 第一象限
                layerCenterX = (int) (nextVertexX + textWidth / 2);
                layerCenterY = (int) (nextVertexY - textHeight / 2);
            } else if (offsetAngle == 0) {
                // X轴正方向
                layerCenterX = (int) (nextVertexX + textWidth);
                layerCenterY = (int) (nextVertexY + (Math.abs(mTextPaint.ascent())) / 2);
            } else if (offsetAngle == Math.PI / 2) {
                // Y轴负方向
                layerCenterX = (int) (nextVertexX);
                layerCenterY = (int) (nextVertexY + textHeight);
            } else if (offsetAngle == Math.PI) {
                // X轴负方向
                layerCenterX = (int) (nextVertexX - textWidth);
                layerCenterY = (int) (nextVertexY + (Math.abs(mTextPaint.ascent())) / 2);
            } else if (offsetAngle == Math.PI * 3 / 2) {
                // Y轴正方向
                layerCenterX = (int) (nextVertexX);
                layerCenterY = (int) (nextVertexY - (Math.abs(mTextPaint.ascent())) / 2);
            }

            int layer = canvas.saveLayer(layerCenterX - saveLayerLength, layerCenterY - saveLayerLength, layerCenterX + saveLayerLength, layerCenterY + saveLayerLength, null, Canvas.ALL_SAVE_FLAG);
            canvas.translate(layerCenterX, layerCenterY);
            canvas.rotate(-degrees);
            canvas.drawText(mTitles[i], 0, 0, mTextPaint);
            canvas.restoreToCount(layer);

        }
    }

    // 绘制中心到各个角的连接线
    private void drawCenterToVertexConnectLine(Canvas canvas) {

        for (int i = 0; i < mNormalPolygonVertexNumber; i++) {

            float nextVertexX = (float) (mMaxRadius * Math.cos(mOffsetAngle * i));
            float nextVertexY = (float) (mMaxRadius * Math.sin(mOffsetAngle * i));

            canvas.drawLine(0, 0, nextVertexX, nextVertexY, mNormalPolygonPaint);
        }
    }

    // 绘制正多边形
    private void drawNormalPolygon(Canvas canvas) {

        // 获取蜘蛛丝之间的间距
        float gap = mMaxRadius / (mNormalPolygonVertexNumber - 1);

        // 由内向外绘制蜘蛛丝
        // 跳过中心点的绘制
        for (int i = 1; i < mNormalPolygonVertexNumber; i++) {
            // 获取绘制半径
            float radius = i * gap;

            // 根据角度计算结果，绘制正多边形
            for (int j = 0; j < mNormalPolygonVertexNumber; j++) {
                //　移动到横轴起始点
                if (0 == j) {
                    mNormalPolygonPath.reset();
                    mNormalPolygonPath.moveTo(radius, 0);
                } else {
                    // 根据三角形斜边计算公式计算出下一个顶点的位置
                    float nextVertexX = (float) (radius * Math.cos(mOffsetAngle * j));
                    float nextVertexY = (float) (radius * Math.sin(mOffsetAngle * j));
                    mNormalPolygonPath.lineTo(nextVertexX, nextVertexY);
                }
            }// end for draw normal polygon
            mNormalPolygonPath.close();// 闭合路径
            canvas.drawPath(mNormalPolygonPath, mNormalPolygonPaint);

        } // end for


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMaxRadius = Math.min(w, h) / 2 * 0.8F;// 获取最大半径
        mCenterX = w / 2;
        mCenterY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setTitles(String[] mTitles) {
        this.mTitles = mTitles;
        mNormalPolygonVertexNumber = mTitles.length; // 默认顶角数
        mOffsetAngle = Math.PI * 2 / mNormalPolygonVertexNumber;
        postInvalidate();
    }

    public void setCanvasRotate(Canvas canvas) {

        if (3 == mNormalPolygonVertexNumber) {
            degrees = (float) (360 / 6 / 2);
            canvas.rotate(degrees);
        } else if (4 == mNormalPolygonVertexNumber) {
            degrees = (float) (360 / 4 / 2);
            canvas.rotate(degrees);
        } else if (5 == mNormalPolygonVertexNumber) {
            degrees = -(float) (360 / 5 / 2 / 2);
            canvas.rotate(degrees);
        } else if (6 == mNormalPolygonVertexNumber) {
            degrees = 0;
        } else if (7 == mNormalPolygonVertexNumber) {
            degrees = (float) (360 / 7 / 2 / 2);
            canvas.rotate(degrees);
        }


    }

    public void setValues(int[] mValues) {
        this.mValues = mValues;
    }
}

