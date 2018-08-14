package com.cloudminds.switcherdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Switcher extends View implements GestureDetector.OnGestureListener {
    private static final String TAG = "Switcher";

    private Context context;
    private List<String> mItems = new ArrayList<String>();//数据源字符串数组
    private int[] mPositions;//数据源字符串数组

    private int anInt;//每个字母所占的大小；
    private TextPaint textPaint;
    private boolean firstVisible = true;
    private int width;//控件宽度
    private int height;//控件高度
    private int mTextPadding;
    private Paint selectedPaint;//被选中文字的画笔
    private float downX;
    private float anOffset;
    private float selectedTextSize;
    private int selectedColor;
    private float textSize;
    private int textColor;
    private Rect rect = new Rect();

    private int textWidth = 0;
    private int textHeight = 0;
    private int centerTextHeight = 0;
    private int centerTextWidth = 0;
    private int mCenterIndex = -1;
    private int mLastSelectedIndex;
    private List<ModeView> modeViews = new ArrayList<ModeView>();
    private Scroller mScroller;
    private OnItemSelectedListener mOnItemSelectedListener;
    private GestureDetectorCompat mGestureDetectorCompat;
    private boolean mFling = false;
    private int mMaxLeftScrollDistance;
    private int mMaxRightScrollDistance;
    private int startPos;
    private int centerPos;

    @Override
    public boolean onDown(MotionEvent e) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(false);
        }
        mFling = false;
        if (null != getParent()) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        playSoundEffect(SoundEffectConstants.CLICK);
        mLastSelectedIndex = mCenterIndex;
        refreshCenter((int) e.getRawX());
        autoSettle();
        return true;
    }

    private void refreshCenter(int pos) {
        Log.i(TAG, "sain refreshCenter: "+ pos);
        Log.i(TAG, "refreshCenter before: " + Arrays.toString(mPositions));
        int size  = mItems.size();
        for (int i = 0; i < size; i++ ) {
            if (Math.abs(pos - mPositions[i]) <= modeViews.get(i).getWidth() / 2 + mTextPadding / 2) {
                mCenterIndex = i;
                return;
            }
        }
    }

    private void autoSettle() {
        int dx = mPositions[mCenterIndex] - mPositions[mLastSelectedIndex];
        anOffset = dx;
        mScroller.forceFinished(true);
        int startX = getScrollX();
        Log.i(TAG, "sain autoSettle dx: " + dx + " ,getScrollX: " + startX);
        if (dx == 0) {
            return;
        }
        mScroller.startScroll(0, 0,  dx, 0, 1500);
        invalidate();
        startPos = startPos - dx;
        updatePosArray(mPositions);
        Log.i(TAG, "sain refreshCenter after: " + Arrays.toString(mPositions));

        if (null != mOnItemSelectedListener) {
            mOnItemSelectedListener.onItemSelected(this, mCenterIndex);
            mOnItemSelectedListener.onItemChanged(mLastSelectedIndex, mCenterIndex);
        }
    }
    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mFling = true;
        return true;
    }

    public interface OnItemSelectedListener {
        void onItemChanged(int lastPos, int currentPos);

        void onItemSelected(Switcher view, int position);
    }

    public Switcher(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mScroller = new Scroller(context);
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), this);
        initAttrs(attrs);
        initPaint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mItems == null || mItems.size() == 0 || !isEnabled()) {
            return false;
        }
        boolean ret = mGestureDetectorCompat.onTouchEvent(event);
        return ret;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        selectedPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(selectedColor);
        selectedPaint.setTextSize(selectedTextSize);

    }


    /**
     * 初始化属性
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs) {
     /*   TintTypedArray tta = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.HorizontalselectedView);
        //两种字体颜色和字体大小
        seeSize = tta.getInteger(R.styleable.HorizontalselectedView_HorizontalselectedViewSeesize, 5);
        selectedTextSize = tta.getFloat(R.styleable.HorizontalselectedView_HorizontalselectedViewSelectedTextSize, 50);
        selectedColor = tta.getColor(R.styleable.HorizontalselectedView_HorizontalselectedViewSelectedTextColor, context.getResources().getColor(android.R.color.black));
        textSize = tta.getFloat(R.styleable.HorizontalselectedView_HorizontalselectedViewTextSize, 40);
        textColor = tta.getColor(R.styleable.HorizontalselectedView_HorizontalselectedViewTextColor, context.getResources().getColor(android.R.color.darker_gray));*/
        selectedTextSize = 50;
        selectedColor = context.getResources().getColor(R.color.switcher_mode_text_selected);
        textSize = 50;
        textColor = context.getResources().getColor(R.color.switcher_mode_text);
        mTextPadding = 84;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String selectedStr = mItems.get(mCenterIndex);
        selectedPaint.getTextBounds(selectedStr, 0, selectedStr.length(), rect);

        //3从矩形区域中读出文本内容的宽高
        centerTextWidth = rect.width();
        centerTextHeight = rect.height();
        Log.i(TAG, "onDraw getScaleX: " + getScaleX());
        canvas.drawText(selectedStr, getWidth() / 2 - centerTextWidth / 2  + anOffset, getHeight() / 2 + centerTextHeight / 2, selectedPaint);//绘制被选中文字，注意点是y坐标
        if (mCenterIndex >= 0 && mCenterIndex <= mItems.size() - 1) {
            for (int i = 0; i < mItems.size(); i++) {
                textWidth = modeViews.get(i).getWidth();
                String title = mItems.get(i);
                if (i != mCenterIndex) {
                    canvas.drawText(title, calcDrawCoordinateX(i, mCenterIndex) + anOffset,  getHeight() / 2 + centerTextHeight / 2, textPaint);
                }
            }
        }

        if (firstVisible) {//第一次绘制的时候得到控件 宽高；
            mPositions = new int[mItems.size()];
            centerPos = getWidth() / 2 - centerTextWidth / 2;
            startPos = centerPos - mCenterIndex * mTextPadding - getTotalWidthBeforeIndex(mCenterIndex);
            updatePosArray(mPositions);
            firstVisible = false;
        }
    }


    private void updatePosArray(int[] array) {
        for (int i = 0; i < mItems.size(); i++) {
            textWidth = modeViews.get(i).getWidth();
            array[i] = startPos + i*mTextPadding + getTotalWidthBeforeIndex(i) + textWidth/2;
        }
    }

    private int getTotalWidthBeforeIndex(int index) {
        int result = 0;
        for (int i = 0; i < index; i++) {
            result += modeViews.get(i).getWidth();
        }
        return result;
    }

    private int calcDrawCoordinateX(int index, int middle) {
        int sign = (index > middle ? 1 : -1);
        int bettweenRangWidths = 0;
        int x = 0;
        if(sign < 0) {
            for(int i = index ; i < middle; i++) {
                bettweenRangWidths = bettweenRangWidths + modeViews.get(i).getWidth();
            }
            x = getWidth()/2 - (centerTextWidth / 2  + mTextPadding * Math.abs(index - middle) + bettweenRangWidths);
        } else {
            for(int i = middle +1; i < index; i++) {
                bettweenRangWidths = bettweenRangWidths + modeViews.get(i).getWidth();
            }
            x = getWidth()/2 + centerTextWidth / 2  + mTextPadding * Math.abs(index - middle) + bettweenRangWidths ;
        }
        return x;
    }



    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            Log.i(TAG, "sain computeScroll: " + mScroller.getCurrX());
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            if (mScroller.getCurrX() == getScrollX()
                    && mScroller.getCurrY() == getScrollY() ) {
                postInvalidate();
            }
        }
    }



    /**
     * 设置个数据源
     *
     */
    public void setData(List<String> strs) {
        this.mItems = strs;
        mCenterIndex = 0;
        initModeViews(strs);
        invalidate();
    }

    public void initModeViews(List<String> strs) {
        for (String str : strs) {
            ModeView  modeView = new ModeView(str, strs.indexOf(str));
            modeViews.add(modeView);
        }
    }

    class ModeView {

        private String mTitle;
        private int mWidth;
        private int mIndex;

        private ModeView(String str, int index)
        {
            mTitle = str;
            mIndex = index;
            textPaint.getTextBounds(mTitle,0, mTitle.length(), rect);
            mWidth = rect.width();
        }


        private ModeView(String str, boolean selected)
        {
            mTitle = str;
            textPaint.getTextBounds(mTitle,0, mTitle.length(), rect);
            mWidth = rect.width();
        }

        public int getWidth() {
            return mWidth;
        }
        public String getmTitle() {
            return mTitle;
        }
    }
}

