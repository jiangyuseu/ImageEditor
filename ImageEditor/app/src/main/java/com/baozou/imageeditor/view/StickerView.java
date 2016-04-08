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
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.baozou.imageeditor.R;
import com.baozou.imageeditor.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyu on 2016/4/7.
 * 贴纸View
 */
public class StickerView extends View {

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
    //操控旋转Bitmap的长宽
    private float rotateCHeight, rotateCWidth;
    //删除按钮bitmap的长宽
    private float deleteCHeight, deleteCWidth;

    //是否是合法的旋转事件
    private boolean isValidRotateEvent = false;
    //是否是合法的删除事件
    private boolean isValidDeleteEvent = false;
    //是否是合法的移动事件
    private boolean isValidMoveEvent = false;


    //保存贴纸列表
    private List<Bitmap> bitmapList = new ArrayList<>();
    //保存bitmap对应的原始坐标点
    private List<float[]> mCoordinateList = new ArrayList<>();
    //保存bitmap对应的现坐标点
    private List<float[]> mPointsList = new ArrayList<>();
    //保存Bitmap的原始绘制矩形
    private List<RectF> bitmapRectfList = new ArrayList<>();
    //保存Bitmap的现绘制矩形
    private List<RectF> mRectList = new ArrayList<>();
    //Bitmap的变换matrix
    private List<Matrix> mMatrixList = new ArrayList<>();
    //焦点所在的贴纸
    private int focusId = -1;

    //旋转变化之前的坐标点
    private List<Float> mLastPointXList = new ArrayList<>();
    private List<Float> mLastPointYList = new ArrayList<>();

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

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setColor(Color.WHITE);

        rotateController = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_sticker_control);
        rotateCHeight = rotateController.getHeight();
        rotateCWidth = rotateController.getWidth();

        deleteController = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_sticker_delete);
        deleteCHeight = deleteController.getHeight();
        deleteCWidth = deleteController.getWidth();

    }

    /**
     * 增加贴纸
     */
    public void addBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        if (mBitmap == null) {
            return;
        }
        bitmapList.add(mBitmap);
        //有几个bitmap，就对应着几个lastPoint的值,对应着PointList的size
        mLastPointXList.add(0f);
        mLastPointYList.add(0f);
        mPointsList.add(new float[10]);
        mRectList.add(new RectF());

        float[] mCoordinateInited = new float[]{0, 0, mBitmap.getWidth(), 0, mBitmap.getWidth(), mBitmap.getHeight(), 0, mBitmap.getHeight(), mBitmap.getWidth() / 2, mBitmap.getHeight() / 2};
        mCoordinateList.add(mCoordinateInited);

        RectF rectFBitmap = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        bitmapRectfList.add(rectFBitmap);

        Matrix mMatrix = new Matrix();
        mMatrix.postTranslate(DisplayUtil.getDisplayWidthPixels(getContext()) / 2, DisplayUtil.getDisplayheightPixels(getContext()) / 2);
        mMatrixList.add(mMatrix);

        //添加贴纸后，获得焦点
        focusId = bitmapList.size() - 1;

        //刷新
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmapList == null) {
            return;
        }
        if (bitmapList.size() == 0) {
            return;
        }

        if (mMatrixList.size() > 0 && mCoordinateList.size() > 0 && bitmapRectfList.size() > 0) {
            for (int i = 0; i < bitmapList.size(); i++) {
                //把经过matrix变换后的坐标赋值mPoints,mRect
                mMatrixList.get(i).mapPoints(mPointsList.get(i), mCoordinateList.get(i));
                mMatrixList.get(i).mapRect(mRectList.get(i), bitmapRectfList.get(i));

                //draw bitmap
                canvas.drawBitmap(bitmapList.get(i), mMatrixList.get(i), mPaint);

            }

            // draw line
            canvas.drawLine(mPointsList.get(focusId)[0], mPointsList.get(focusId)[1], mPointsList.get(focusId)[2], mPointsList.get(focusId)[3], mPaint);
            canvas.drawLine(mPointsList.get(focusId)[2], mPointsList.get(focusId)[3], mPointsList.get(focusId)[4], mPointsList.get(focusId)[5], mPaint);
            canvas.drawLine(mPointsList.get(focusId)[4], mPointsList.get(focusId)[5], mPointsList.get(focusId)[6], mPointsList.get(focusId)[7], mPaint);
            canvas.drawLine(mPointsList.get(focusId)[6], mPointsList.get(focusId)[7], mPointsList.get(focusId)[0], mPointsList.get(focusId)[1], mPaint);

            //draw delete icon
            canvas.drawBitmap(deleteController, mPointsList.get(focusId)[0] - deleteCWidth / 2, mPointsList.get(focusId)[1] - deleteCHeight / 2, mPaint);

            //draw rotate icon
            canvas.drawBitmap(rotateController, mPointsList.get(focusId)[4] - rotateCWidth / 2, mPointsList.get(focusId)[5] - rotateCHeight / 2, mPaint);
        }
    }

    /**
     * 判断是否点击触发在rotate img区域内
     */
    private boolean isValidRotate(float dx, float dy) {
        for (int i = 0; i < bitmapList.size(); i++) {
            RectF rotateRectF = new RectF(mPointsList.get(i)[4] - rotateCWidth / 2, mPointsList.get(i)[5] - rotateCHeight / 2, mPointsList.get(i)[4] + rotateCWidth / 2, mPointsList.get(i)[5] + rotateCHeight / 2);
            if (rotateRectF.contains(dx, dy)) {
                focusId = i;
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否点击触发在delete img区域内
     */
    private boolean isValidDeleteEvent(float dx, float dy) {
        for (int i = 0; i < bitmapList.size(); i++) {
            RectF deleteRectF = new RectF(mPointsList.get(i)[0] - deleteCWidth / 2, mPointsList.get(i)[1] - deleteCHeight / 2, mPointsList.get(i)[0] + deleteCWidth / 2, mPointsList.get(i)[1] + deleteCHeight / 2);
            if (deleteRectF.contains(dx, dy)) {
                focusId = i;
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否点击触发在Bitmap区域内
     */
    private boolean isValidMoveEvent(float dx, float dy) {
        for (int i = 0; i < bitmapList.size(); i++) {
            RectF bitmapRectF = new RectF(mPointsList.get(i)[0], mPointsList.get(i)[1], mPointsList.get(i)[4], mPointsList.get(i)[5]);
            if (bitmapRectF.contains(dx, dy)) {
                focusId = i;
                return true;
            }
        }
        return false;
    }

    /**
     * 删除操作
     */
    private void doDelete() {
        addBitmap(null);
        isValidDeleteEvent = false;
        postInvalidate();
    }

    /**
     * 计算旋转的角度
     */
    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(mLastPointXList.get(focusId), mLastPointYList.get(focusId));
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        double delta_x = x - mPointsList.get(focusId)[8];
        double delta_y = y - mPointsList.get(focusId)[9];
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 计算触摸点距离中心点的长度
     */
    private double caculateLength(float x, float y) {
        float ex = x - mPointsList.get(focusId)[8];
        float ey = y - mPointsList.get(focusId)[9];
        return Math.sqrt(ex * ex + ey * ey);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isValidRotate(x, y)) {
                    isValidRotateEvent = true;
                    mLastPointXList.set(focusId, x);
                    mLastPointYList.set(focusId, y);
                }
                if (isValidDeleteEvent(x, y)) {
                    isValidDeleteEvent = true;
                }
                if (isValidMoveEvent(x, y)) {
                    isValidMoveEvent = true;
                    mLastPointXList.set(focusId, x);
                    mLastPointYList.set(focusId, y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isValidRotateEvent) {
                    //旋转 & 放大
                    mMatrixList.get(focusId).postRotate(rotation(event), mPointsList.get(focusId)[8], mPointsList.get(focusId)[9]);
                    double orginLength = caculateLength(mPointsList.get(focusId)[0], mPointsList.get(focusId)[1]);
                    double nowLength = caculateLength(event.getX(), event.getY());
                    float scale = (float) (nowLength / orginLength);
                    mMatrixList.get(focusId).postScale(scale, scale, mPointsList.get(focusId)[8], mPointsList.get(focusId)[9]);
                    mLastPointXList.set(focusId, x);
                    mLastPointYList.set(focusId, y);
                    postInvalidate();
                }
                if (isValidMoveEvent) {
                    //移动图片操作
                    mMatrixList.get(focusId).postTranslate(x - mLastPointXList.get(focusId), y - mLastPointYList.get(focusId));
                    mLastPointXList.set(focusId, x);
                    mLastPointYList.set(focusId, y);
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isValidRotateEvent = false;
                isValidMoveEvent = false;
                mLastPointXList.set(focusId, 0f);
                mLastPointYList.set(focusId, 0f);
                break;
            case MotionEvent.ACTION_UP:
                isValidRotateEvent = false;
                isValidMoveEvent = false;
                if (isValidDeleteEvent) {
                    //删除操作
                    doDelete();
                }
                break;
        }
        return true;
    }
}
