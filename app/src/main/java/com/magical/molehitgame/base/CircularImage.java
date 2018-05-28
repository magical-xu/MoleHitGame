package com.magical.molehitgame.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

public class CircularImage extends MaskedImage {

    public CircularImage(Context paramContext) {
        super(paramContext);
        initView(paramContext);
    }

    public CircularImage(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initView(paramContext);
    }

    public CircularImage(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initView(paramContext);
    }

    private void initView(Context context) {
        //由于原来用的都是CenterCrop类型，故默认均为此类型
        setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public Bitmap getShapeBitmap() {
        int width = getWidth();
        int height = getHeight();

        BitmapPool pool = Glide.get(getContext()).getBitmapPool();
        Bitmap localBitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        //        Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
        //        Bitmap localBitmap = Bitmap.createBitmap(width, height, localConfig);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        localPaint.setColor(Color.BLACK);
        RectF localRectF = new RectF(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(),
                height - getPaddingBottom());
        localCanvas.drawOval(localRectF, localPaint);
        return localBitmap;
    }
}