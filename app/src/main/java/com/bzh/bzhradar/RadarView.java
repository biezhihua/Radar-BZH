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

    private String[] mTitles = {"别", "志", "华", "胡", "玉", "琼"};

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

        mNormalPolygonVertexNumber = 6; // 默认顶角数
        mOffsetAngle = Math.PI * 2 / mNormalPolygonVertexNumber;


        // 绘制正多边形路径
        mNormalPolygonPath = new Path();

        // 绘制正多边形路径画笔
        mNormalPolygonPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mNormalPolygonPaint.setColor(Color.GRAY);
        mNormalPolygonPaint.setStrokeWidth(3);
        mNormalPolygonPaint.setStyle(Paint.Style.STROKE);


        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.BLACK);

        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawNormalPolygon(canvas);

        drawCenterToVertexConnectLine(canvas);

        drawEachVertexText(canvas);

    }

    // 画各个角对应的文字
    // 默认从最右方开始
    private void drawEachVertexText(Canvas canvas) {
        int save = canvas.save();
        mTextPaint.setTextSize(mMaxRadius / (mNormalPolygonVertexNumber - 1) * 0.8F);
        float textHeight = (mTextPaint.descent() - mTextPaint.ascent());

        for (int i = 0; i < mNormalPolygonVertexNumber; i++) {

            double offsetAngle = mOffsetAngle * i;

            float nextVertexX = (float) (mCenterX + mMaxRadius * Math.cos(offsetAngle));
            float nextVertexY = (float) (mCenterY + mMaxRadius * Math.sin(offsetAngle));

            float textWidth = mTextPaint.measureText(mTitles[i]);

            if (offsetAngle > 0 && offsetAngle < Math.PI / 2) {
                // 第四象限
            } else if (offsetAngle > Math.PI / 2 && offsetAngle < Math.PI) {
                // 第三象限
            } else if (offsetAngle > Math.PI && offsetAngle < Math.PI * 3 / 2) {
                // 第二象限

            } else if (offsetAngle > Math.PI * 3 / 2 && offsetAngle < Math.PI * 2) {
                // 第一象限
                canvas.drawText(mTitles[i], nextVertexX - textWidth / 2, nextVertexY - textHeight / 2, mTextPaint);
            }


        }

        canvas.restoreToCount(save);
    }

    // 绘制中心到各个角的连接线
    private void drawCenterToVertexConnectLine(Canvas canvas) {
        int save = canvas.save();

        for (int i = 0; i < mNormalPolygonVertexNumber; i++) {

            float nextVertexX = (float) (mCenterX + mMaxRadius * Math.cos(mOffsetAngle * i));
            float nextVertexY = (float) (mCenterY + mMaxRadius * Math.sin(mOffsetAngle * i));

            canvas.drawLine(mCenterX, mCenterY, nextVertexX, nextVertexY, mNormalPolygonPaint);
        }

        canvas.restoreToCount(save);
    }

    // 绘制正多边形
    private void drawNormalPolygon(Canvas canvas) {
        int save = canvas.save();

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
                    mNormalPolygonPath.moveTo(mCenterX + radius, mCenterY);
                } else {
                    // 根据三角形斜边计算公式计算出下一个顶点的位置
                    float nextVertexX = (float) (mCenterX + radius * Math.cos(mOffsetAngle * j));
                    float nextVertexY = (float) (mCenterY + radius * Math.sin(mOffsetAngle * j));
                    mNormalPolygonPath.lineTo(nextVertexX, nextVertexY);
                }
            }// end for draw normal polygon
            mNormalPolygonPath.close();// 闭合路径
            canvas.drawPath(mNormalPolygonPath, mNormalPolygonPaint);

        } // end for

        canvas.restoreToCount(save);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMaxRadius = Math.min(w, h) / 2 * 0.8F;// 获取最大半径
        mCenterX = w / 2;
        mCenterY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
