package com.example.voicecraft;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    int images[] = {
            R.drawable.page1,
            R.drawable.page2,
            R.drawable.page3,
            R.drawable.page4,
    };
    int descriptions[] = {
            R.string.Page1,
            R.string.Page2,
            R.string.Page3,
            R.string.Page4,
    };

    public ViewPagerAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout,container,false);

        ImageView slideTitleImage = (ImageView) view.findViewById(R.id.titleImage);
        TextView slideDescription = (TextView) view.findViewById(R.id.description);

        slideTitleImage.setImageResource(images[position]);
        slideDescription.setText(descriptions[position]);

        if (position == 0) {
            slideDescription.setText(Html.fromHtml(context.getString(R.string.Page1), Html.FROM_HTML_MODE_COMPACT));
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
