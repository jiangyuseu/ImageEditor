package com.baozou.imageeditor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.baozou.imageeditor.R;
import com.baozou.imageeditor.utils.DisplayUtil;

/**
 * Created by jiangyu on 2016/4/7.
 * 贴纸View
 */
public class StickerView extends View{

    //绘制line的paint
    private Paint mPaint;
    //贴纸bitmap
    private Bitmap mBitmap;
    //背景bitmap
    private Bitmap bgBitmap;
    //操作旋转的bitmap
    private Bitmap rotateController;
    //删除按钮的bitmap
    private Bitmap deleteController;
    //贴纸Bitmap长宽
    private float mBitmapHeight,mBitmapWidth;
    //操控旋转Bitmap的长宽
    private float rotateCHeight,rotateCWidth;
    //删除按钮bitmap的长宽
    private float deleteCHeight,deleteCWidth;
    //mBitmap原始坐标位置
    private float[] mCoordinateInited;
    //mBitmap的现坐标
    private float[] mPoints;
    //mBitmap的原始绘制矩形区域
    private RectF rectFBitmap;
    //mBitmap的现绘制矩形区域
    private RectF mRect;
    //mBitmap的变化matrix
    private Matrix mMatrix;
    //是否是合法的旋转事件
    private boolean isValidRotateEvent = false;
    //按下的坐标点
    private float mLastPointX,mLastPointY;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setColor(Color.WHITE);

        rotateController = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_sticker_control);
        rotateCHeight = rotateController.getHeight();
        rotateCWidth = rotateController.getWidth();

        deleteController =BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_sticker_delete);
        deleteCHeight = deleteController.getHeight();
        deleteCWidth = deleteController.getWidth();

        mPoints = new float[10];
        mRect = new RectF();

    }

    public void setBitmap(Bitmap bitmap){
        this.mBitmap = bitmap;
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        //mBitmap的初始坐标点
        float xInited = mBitmap.getWidth();
        float yInited = mBitmap.getHeight();
        mCoordinateInited = new float[]{0,0,xInited,0,xInited,yInited,0,yInited,xInited/2,yInited/2};

        rectFBitmap = new RectF(0,0,mBitmapWidth,mBitmapHeight);

        mMatrix = new Matrix();
        float dx = DisplayUtil.getDisplayWidthPixels(getContext())/2;
        float dy = DisplayUtil.getDisplayheightPixels(getContext())/2;
        mMatrix.postTranslate(dx,dy);
        //刷新
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //把经过matrix变换后的坐标赋值mPoints,mRect
        mMatrix.mapPoints(mPoints, mCoordinateInited);
        mMatrix.mapRect(mRect, rectFBitmap);
        //draw bitmap
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);

        // draw line
        canvas.drawLine(mPoints[0],mPoints[1],mPoints[2],mPoints[3],mPaint);
        canvas.drawLine(mPoints[2],mPoints[3],mPoints[4],mPoints[5],mPaint);
        canvas.drawLine(mPoints[4],mPoints[5],mPoints[6],mPoints[7],mPaint);
        canvas.drawLine(mPoints[6], mPoints[7], mPoints[0], mPoints[1], mPaint);

        //draw delete icon
        canvas.drawBitmap(deleteController, mPoints[0] - deleteCWidth / 2, mPoints[1] - deleteCHeight / 2, mPaint);

        //draw rotate icon
        canvas.drawBitmap(rotateController, mPoints[4] - rotateCWidth / 2, mPoints[5] - rotateCHeight / 2, mPaint);
    }

    /**
     * 判断是否点击处罚在rotate img区域内
     */
    private boolean isValidRotate(float dx,float dy){
        RectF rotateRectF = new RectF(mPoints[4]-rotateCWidth/2,mPoints[5]-rotateCHeight/2,mPoints[4]+rotateCWidth/2,mPoints[5]+rotateCHeight/2);
        if(rotateRectF.contains(dx,dy)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 计算旋转的角度
     */
    private float rotation(MotionEvent event) {
        float  originDegree = calculateDegree(mLastPointX, mLastPointY);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        double delta_x = x - mPoints[8];
        double delta_y = y - mPoints[9];
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isValidRotate(x,y)){
                    isValidRotateEvent = true;
                    mLastPointX = x;
                    mLastPointY = y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isValidRotateEvent){
                    mMatrix.postRotate(rotation(event),mPoints[8],mPoints[9]);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isValidRotateEvent = false;
                mLastPointY = 0;
                mLastPointX = 0;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}