package com.cloudminds.switcherdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcelable;
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

public class Switcher extends View implements GestureDetector.OnGestureListener {
    private static final String TAG = "Switcher";
    private List<String> mItems = new ArrayList<>();
    private List<Integer> mItemWidths = new ArrayList<>();
    private List<Integer> mIntegralDistance = new ArrayList<>(); //before certain index , the sum distance include margin and item width
    private SparseArray<Integer> mItemCenterPositions = new SparseArray<>();
    private TextPaint textPaint;
    private TextPaint selectedPaint;
    private int selectedColor;
    private float textSize;
    private int textColor;
    private int mTextHeight;
    //private int mSelectedIndex;
    private int mCenterIndex;
    private int mItemsCount;
    private int mItemMargin;
    private int mScrollDistance;
    private float mStartX;
    private boolean hasInit;

    private Rect rect = new Rect();

    private boolean mFling = false;

    private OnItemSelectedListener mOnItemSelectedListener;
    private OverScroller mScroller;
    private GestureDetectorCompat mGestureDetectorCompat;
    private int mLastSelectedIndex;

    @Override
    public boolean onDown(MotionEvent e) {
        if (!mScroller.isFinished()) {
            return false;
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
        int mXMove = (int) e.getRawX();
        mScrollDistance = reviseGap(mXMove);
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
        void onItemChanged(int lastPos, int currentPos);

        void onItemSelected(Switcher view, int position);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
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
        mCenterIndex = 2;
        mLastSelectedIndex = mCenterIndex;
    }

    private void initAttrs(AttributeSet attrs) {
        textSize = 50;
        selectedColor = getResources().getColor(R.color.switcher_mode_text_selected);
        textColor = getResources().getColor(R.color.switcher_mode_text);
        mItemMargin = 84;
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        selectedPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(selectedColor);
        selectedPaint.setTextSize(textSize);
    }

    public void setData(List<String> items) {
        mItems = items;
        mItemsCount = items.size();
        invalidate();
    }

    private void calcFirstItemPos(int centerIndex) {
        mStartX = getWidth() / 2 - mItemWidths.get(centerIndex) / 2 - mIntegralDistance.get(centerIndex);
    }

    public void calcItemsWidth(List<String> items) {
        int temp = 0;
        for (String item : items) {
            textPaint.getTextBounds(item, 0, item.length(), rect);
            mItemWidths.add(rect.width());
            mIntegralDistance.add(temp);
            temp = temp + rect.width() + mItemMargin;
        }
        mTextHeight = rect.height();
    }

    public void calcItemCenterPositions() {
        for (int i = 0; i < mItemsCount; i++) {
            int centerPos = (int) (mStartX + mIntegralDistance.get(i) + mItemWidths.get(i) / 2);
            mItemCenterPositions.put(i, centerPos);
        }
    }

    public void updateItemCenterPositions(int offset) {
        for (int i = 0; i < mItemsCount; i++) {
            int centerPos = mItemCenterPositions.get(i) - offset;
            mItemCenterPositions.put(i, centerPos);
        }
    }

    private void initPosition ( ) {
        if (hasInit) {
            return;
        }
        calcItemsWidth(mItems);
        calcFirstItemPos(mCenterIndex);
        calcItemCenterPositions();
        hasInit = true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mItemsCount == 0) {
            return;
        }
        initPosition();
        canvas.drawCircle(getWidth() / 2 + getScrollX(), getHeight() / 2 - 1.5f * mTextHeight, 12, selectedPaint);
        float mItemY =  getHeight() / 2;
        for (int i = 0; i < mItemsCount; i++) {
            float mItemX = mStartX + mIntegralDistance.get(i);
            canvas.drawText(mItems.get(i), mItemX, mItemY, i != mCenterIndex ? textPaint : selectedPaint);
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
            invalidate();
        } else {
            if (mScrollDistance != 0) {
                updateItemCenterPositions(mScrollDistance);
                mScrollDistance = 0;
                Log.i(TAG, "reviseGap getScrollX: " + getScrollX());
                Log.i(TAG, "reviseGap after: " + mItemCenterPositions.toString());
                Log.i(TAG, " ");
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(this, mCenterIndex);
                    mOnItemSelectedListener.onItemChanged(mLastSelectedIndex, mCenterIndex);
                }
            }
        }
    }

    private void autoSettle(int dx) {
        int sx = getScrollX();
        Log.i(TAG, "autoSettle sx: " + sx + " ,dx:" + dx);
        mScroller.startScroll(sx, 0, dx, 0);
        invalidate();
    }

    private int reviseGap(int gap) {
        int distance = 0;
        Log.i(TAG, "reviseGap before: " + mItemCenterPositions.toString());
        for (int i = 0; i < mItemsCount; i++) {
            if (Math.abs(gap - (mItemCenterPositions.get(i))) <= (mItemWidths.get(i) + mItemMargin) / 2) {
                distance = mItemCenterPositions.get(i) - mItemCenterPositions.get(mCenterIndex);
                mCenterIndex = i;
                return distance;
            }
        }
        Log.i(TAG, "reviseGap2: " + distance);
        return distance;
    }

}

