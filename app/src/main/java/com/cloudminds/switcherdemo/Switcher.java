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
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.OverScroller;
import java.util.ArrayList;
import java.util.List;

public class Switcher extends View implements GestureDetector.OnGestureListener{
    private static final String TAG = "Switcher";
    private List<String> mItems = new ArrayList<>();
    private List<Integer> mItemWidths = new ArrayList<>();
    private SparseArray<Integer> mItemCenterPositions = new SparseArray<>();
    private TextPaint textPaint;
    private TextPaint selectedPaint;
    private float selectedTextSize;
    private int selectedColor;
    private float textSize;
    private int textColor;
    private int mSelectedTextWidth;
    private int mSelectedTextHeight;
    private int mTextHeight;
    private int mSelectedIndex;
    private int mItemsCount;
    private int mItemMargin;
    private int mScrollDistance;
    private float mSelectedX;
    private float mSelectedY;

    private Rect rect = new Rect();

    private boolean mFling = false;

    private OnItemSelectedListener mOnItemSelectedListener;
    private OverScroller mScroller;
    private GestureDetectorCompat mGestureDetectorCompat;

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
        float mXMove = e.getRawX();
        int scrolledX = (int) (mXMove - getWidth()/2);
        Log.i(TAG, "onSingleTapUp: ");
        mScrollDistance = reviseGap(scrolledX);
        autoSettle(mScrollDistance);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Switcher view, int position);
    }

    public Switcher(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSwitcher();
        initAttrs(attrs);
    }

    private void initSwitcher() {
        mScroller = new OverScroller(getContext());
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), this);
        setFocusable(true);
        mSelectedIndex = 3;
    }

    private void initAttrs(AttributeSet attrs) {
        selectedTextSize = 60;
        textSize = 50;
        selectedColor = getResources().getColor(R.color.switcher_mode_text_selected);
        textColor = getResources().getColor(R.color.switcher_mode_text);
        mItemMargin = 84;
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        selectedPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(selectedColor);
        selectedPaint.setTextSize(selectedTextSize);
    }


    public void setData(List<String> items) {
        mItems = items;
        mItemsCount = items.size();
        invalidate();
    }

    public void calcItemsWidth(List<String> items) {
        for(String item : items) {
            textPaint.getTextBounds(item, 0, item.length(), rect);
            mItemWidths.add(rect.width());
        }
        mTextHeight = rect.height();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mItemsCount == 0) {
            return;
        }
        calcItemsWidth(mItems);
        String selectedStr = mItems.get(mSelectedIndex);
        selectedPaint.getTextBounds(selectedStr, 0, selectedStr.length(), rect);
        mSelectedTextWidth = rect.width();
        mSelectedTextHeight = rect.height();
        canvas.drawCircle(getWidth() / 2  + getScrollX(), getHeight() / 2 - mSelectedTextHeight, 12, selectedPaint);
        mSelectedX = getWidth() / 2 - mSelectedTextWidth / 2 + getScrollX();
        mSelectedY = getHeight() / 2 + mSelectedTextHeight / 2;
        canvas.drawText(selectedStr, mSelectedX, mSelectedY, selectedPaint);
        mItemCenterPositions.put(mSelectedIndex, getWidth() / 2);
        float otherX, otherY;
        otherX = mSelectedX;
        otherY = getHeight() / 2 + mTextHeight / 2;
        for (int i = mSelectedIndex - 1; i >= 0; i --) {
            otherX = otherX - mItemMargin - mItemWidths.get(i);
            canvas.drawText(mItems.get(i), otherX, otherY, textPaint);
            mItemCenterPositions.put(i, (int) (otherX + mItemWidths.get(i) / 2));
        }
        otherX = mSelectedX + mSelectedTextWidth;
        for (int i = mSelectedIndex + 1; i < mItemsCount; i++) {
            otherX = otherX + mItemMargin + mItemWidths.get(i);
            canvas.drawText(mItems.get(i), otherX - mItemWidths.get(i), otherY, textPaint);
            mItemCenterPositions.put(i, (int) (otherX - mItemWidths.get(i) / 2));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mItems == null || mItems.size() == 0 || !isEnabled()) {
            return false;
        }
        boolean ret = mGestureDetectorCompat.onTouchEvent(event);
        if (!mFling && MotionEvent.ACTION_UP == event.getAction()) {
            //autoSettle();
            ret = true;
        }
        return ret || super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            if (mScroller.getCurrX() == getScrollX()
                    && mScroller.getCurrY() == getScrollY()) {
                postInvalidate();
            }
        }
        
        if(mScroller.isFinished()) {
            Log.i(TAG, "computeScroll: ");
        }
    }

    private void autoSettle(int dx) {
        mScroller.forceFinished(true);
        int sx = getScrollX();
        Log.i(TAG, "autoSettle sx: " + sx + " ,dx:" + dx);
        mScroller.startScroll(sx, 0, dx , 0, 1200);
        postInvalidate();
    }

    private int reviseGap(int gap) {
        Log.i(TAG, "refreshCenter: " + gap);
        if (Math.abs(gap) < (mItemWidths.get(mSelectedIndex) + mItemMargin) / 2) {
            return 0;
        }
        int centerX = getWidth()/2;
        for (int i = 0; i < mItemsCount; i++) {
            if (Math.abs(centerX + gap - (mItemCenterPositions.get(i) - getScrollX())) <= (mItemWidths.get(i) + mItemMargin) /2) {
                mSelectedIndex = i;
                return gap > 0 ? Math.abs(mItemCenterPositions.get(i) - centerX - getScrollX()) : -Math.abs(mItemCenterPositions.get(i) - centerX - getScrollX());
            }
        }
        return 0;
    }

}

