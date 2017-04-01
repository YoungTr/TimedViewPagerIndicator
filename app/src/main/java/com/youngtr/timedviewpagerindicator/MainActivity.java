package com.youngtr.timedviewpagerindicator;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.youngtr.timedviewpagerindicator.example.R;

public class MainActivity extends AppCompatActivity {

    private final int[] mImages = {R.mipmap.image1, R.mipmap.image2, R.mipmap.image3};

    private ViewPager mViewPager;
    private TimedViewPagerIndicator mIndicator;

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (TimedViewPagerIndicator) findViewById(R.id.indicator);
        mViewPager.setAdapter(new IndicatorPagerAdapter());
        mIndicator.setViewPager(mViewPager);
        mIndicator.startScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIndicator.stopScroll();
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mIndicator.stopScroll();
        }
        if (action == MotionEvent.ACTION_UP) {
            mIndicator.startScroll();
        }
        return super.dispatchTouchEvent(ev);
    }

    class IndicatorPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource(mImages[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
