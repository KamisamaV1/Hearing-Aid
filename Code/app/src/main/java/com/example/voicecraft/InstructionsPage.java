package com.example.voicecraft;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class InstructionsPage extends AppCompatActivity {

    ViewPager mSlideViewPager;
    LinearLayout mDotLayout;
    Button backBtn, nextBtn, skipBtn;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setVisibility(View.INVISIBLE);
        nextBtn = findViewById(R.id.nextBtn);
        skipBtn = findViewById(R.id.skipBtn);


        backBtn.setOnClickListener(v -> {
            if(getItem(0)>0){
                mSlideViewPager.setCurrentItem(getItem(-1), true);
            }
        });
        nextBtn.setOnClickListener(v -> {
            if (getItem(0) < 3){
                mSlideViewPager.setCurrentItem(getItem(1), true);
            }else {
                Intent i = new Intent(InstructionsPage.this, HearingTestActivity.class);
                startActivity(i);
                finish();
            }
        });
        skipBtn.setOnClickListener(v -> {
            Intent i = new Intent(InstructionsPage.this, HearingTestActivity.class);
            startActivity(i);
            finish();
        });

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        viewPagerAdapter = new ViewPagerAdapter(this);

        mSlideViewPager.setAdapter(viewPagerAdapter);
        setUpIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

    }

    public void setUpIndicator(int position){
        dots = new TextView[4];
        mDotLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.gainsboro, getApplicationContext().getTheme()));  //inactive dots
            mDotLayout.addView(dots[i]);

        }

        dots[position].setTextColor(getResources().getColor(R.color.prussian_blue, getApplicationContext().getTheme()));  //active dots
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);
            if (position > 0){
                backBtn.setVisibility(View.VISIBLE);
            }else {
                backBtn.setVisibility(View.INVISIBLE);
            }
            if(position == 3){
                nextBtn.setText("Start Test");
                skipBtn.setVisibility(View.INVISIBLE);
            }else {
                nextBtn.setText("Next");
                skipBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private int getItem(int i){
        return mSlideViewPager.getCurrentItem() + i;
    }
}