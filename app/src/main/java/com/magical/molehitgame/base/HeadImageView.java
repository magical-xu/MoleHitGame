package com.magical.molehitgame.base;

import android.content.Context;
import android.util.AttributeSet;
import com.bumptech.glide.Glide;

public class HeadImageView extends CircularImage {

    public HeadImageView(Context context) {
        this(context, null);
    }

    public HeadImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 直接调用mXCRoundRectImageView的setHeadImageUrl方法
     */
    public void setHeadImageUrl(final String url) {

        Glide.with(getContext()).load(url).into(this);
    }
}
