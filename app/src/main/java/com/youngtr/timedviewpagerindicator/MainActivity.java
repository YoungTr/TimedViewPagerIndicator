package com.youngtr.timedviewpagerindicator;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youngtr.timedviewpagerindicator.example.R;

public class MainActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private TimedViewPagerIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (TimedViewPagerIndicator) findViewById(R.id.indicator);
        mViewPager.setAdapter(new IndicatorPagerAdapter());
        mIndicator.setViewPager(mViewPager);
    }

    class IndicatorPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Strings.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText(Strings.values()[position].getStr());
            textView.setTextColor(Color.BLACK);
            container.addView(textView);
            return textView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
