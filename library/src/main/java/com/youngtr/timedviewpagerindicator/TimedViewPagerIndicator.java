package com.youngtr.timedviewpagerindicator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by YoungTr on 17/3/19.
 */

public class TimedViewPagerIndicator extends View implements PageIndicator, Animator.AnimatorListener {

    private static final float SELECTED_PERCENT_START = 0.0F;
    private static final float SELECTED_PERCENT_END = 1.0F;

    private static final long ANIMATOR_DEFAULT_DURATION = 3000L;

    private static final String ANIMATOR_PROPERTY = "selectedPercent";

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mPageChangeListener;

    private final Paint mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mUnSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ObjectAnimator mScrollAnimator;

    private float mLineWidth;
    private float mGapWidth;
    private float mSelectedPercent;

    private int mCurrentPage;

    private long mAnimatorDuration = ANIMATOR_DEFAULT_DURATION;

    public TimedViewPagerIndicator(Context context) {
        this(context, null);
    }

    public TimedViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimedViewPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources res = getResources();
        final int defaultSelectedColor = res.getColor(R.color.default_selected_line_color);
        final int defaultUnSelectedColor = res.getColor(R.color.default_unselected_line_color);
        final float defaultLineWidth = res.getDimension(R.dimen.default_line_width);
        final float defaultLineHeight = res.getDimension(R.dimen.default_line_height);
        final float defaultGapWidth = res.getDimension(R.dimen.default_gap_width);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TimedViewPagerIndicator, defStyleAttr, 0);
        setSelectedColor(attributes.getColor(R.styleable.TimedViewPagerIndicator_selectedColor, defaultSelectedColor));
        setUnSelectedColor(attributes.getColor(R.styleable.TimedViewPagerIndicator_unselectedColor, defaultUnSelectedColor));
        setLineWidth(attributes.getDimension(R.styleable.TimedViewPagerIndicator_lineWidth, defaultLineWidth));
        setLineHeight(attributes.getDimension(R.styleable.TimedViewPagerIndicator_lineHeight, defaultLineHeight));
        setGapWidth(attributes.getDimension(R.styleable.TimedViewPagerIndicator_gapWidth, defaultGapWidth));
        attributes.recycle();

        initScrollAnimator();
    }

    private void initScrollAnimator() {
        mScrollAnimator = ObjectAnimator.ofFloat(this, ANIMATOR_PROPERTY, SELECTED_PERCENT_START, SELECTED_PERCENT_END);
        mScrollAnimator.setDuration(getScrollDuration());
        mScrollAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mScrollAnimator.addListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = 0;
        int dh;
        if (mViewPager != null) {
            final int count = mViewPager.getAdapter().getCount();
            dw = (int) (mLineWidth * count + mGapWidth * (count - 1));
        }
        dw += getPaddingLeft() + getPaddingRight();
        dh = (int) (getPaddingTop() + getPaddingBottom() + mSelectedPaint.getStrokeWidth());
        int measureWidth = resolveSizeAndState(dw, widthMeasureSpec, 0);
        int measureHeight = resolveSizeAndState(dh, heightMeasureSpec, 0);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mViewPager == null) {
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if (count <= 0) {
            return;
        }
        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }
        final float lineAndGapWidth = getLineWidth() + getGapWidth();
        final float indicatorWidth = lineAndGapWidth * count - getGapWidth();
        final float verticalOffset = getPaddingTop() + (getHeight() - getPaddingTop() - getPaddingBottom()) / 2.0f;
        final float horizontalOffset = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2.0f - indicatorWidth / 2.0f;


        for (int i = 0; i < count; i++) {
            float startX = horizontalOffset + lineAndGapWidth * i;
            float endX = startX + getLineWidth();
            canvas.drawLine(startX, verticalOffset, endX, verticalOffset, mUnSelectedPaint);
        }

        float selectedStartX = horizontalOffset + lineAndGapWidth * mCurrentPage;
        float selectedEndX = selectedStartX + getLineWidth() * getSelectedPercent();
        canvas.drawLine(selectedStartX, verticalOffset, selectedEndX, verticalOffset, mSelectedPaint);

    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageSelected(position);
        }
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void setSelectedColor(int selectedColor) {
        mSelectedPaint.setColor(selectedColor);
        invalidate();
    }

    private void setUnSelectedColor(int unSelectedColor) {
        mUnSelectedPaint.setColor(unSelectedColor);
        invalidate();
    }

    public float getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.mLineWidth = lineWidth;
        invalidate();
    }

    public float getGapWidth() {
        return mGapWidth;
    }

    public void setGapWidth(float gapWidth) {
        this.mGapWidth = gapWidth;
        invalidate();
    }

    public void setLineHeight(float height) {
        mUnSelectedPaint.setStrokeWidth(height);
        mSelectedPaint.setStrokeWidth(height);
        invalidate();
    }

    public float getSelectedPercent() {
        return mSelectedPercent;
    }

    public void setSelectedPercent(float selectedPercent) {
        this.mSelectedPercent = selectedPercent;
        invalidate();
    }

    private void viewPagerScrollOnce() {
        PagerAdapter adapter = mViewPager.getAdapter();
        final int count = adapter.getCount();
        if (count <= 1) {
            return;
        }
        int nextPage = mCurrentPage + 1;
        if (nextPage == count) {
            setCurrentItem(0);
        } else {
            setCurrentItem(nextPage);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void startScroll() {

        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }

        if (!mScrollAnimator.isStarted()) {
            mScrollAnimator.start();
        }

    }


    public void stopScroll() {
        if (mScrollAnimator != null) {
            setSelectedPercent(SELECTED_PERCENT_END);
            mScrollAnimator.cancel();
        }
    }

    public long getScrollDuration() {
        return mAnimatorDuration;
    }

    public void setScrollDuration(long animatorDuration) {
        this.mAnimatorDuration = animatorDuration;
        if (mScrollAnimator != null) {
            mScrollAnimator.setDuration(animatorDuration);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        viewPagerScrollOnce();
    }
}
