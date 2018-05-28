package com.magical.molehitgame.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.lang.ref.WeakReference;

public abstract class MaskedImage extends AppCompatImageView {

    private Paint mPaint;
    //用于存储显示的bitmap
    private WeakReference<Bitmap> mWeakBitmap;
    private Bitmap mMaskBitmap;

    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Canvas drawCanvas = new Canvas();

    public MaskedImage(Context paramContext) {
        super(paramContext);
        initView();
    }

    public MaskedImage(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initView();
    }

    public MaskedImage(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    public void invalidate() {
        //图片变了 则释放原来的bitmap
        mWeakBitmap = null;
        if (mMaskBitmap != null) {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }

        super.invalidate();
    }

    /**
     * 获取形状
     *
     * @return Bitmap
     */
    public abstract Bitmap getShapeBitmap();

    protected void onDraw(Canvas paramCanvas) {
        if (isInEditMode() || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        //先从mWeakBitmap中获取，有则显示
        Bitmap bitmap = mWeakBitmap == null ? null : mWeakBitmap.get();
        if (bitmap == null || bitmap.isRecycled()) {

            //0.1 创建bitmap
            BitmapPool pool = Glide.get(getContext()).getBitmapPool();
            bitmap = pool.get(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            //            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            //0.2 缓存bitmap
            mWeakBitmap = new WeakReference<>(bitmap);
        }

        //1.重置画布
        drawCanvas.setBitmap(bitmap);
        //drawCanvas.drawColor(0xff000000); //加这句 图片透明的部分会有黑色背景

        //2.调用父类ImageView的onDraw方法绘制原图 这样会带有ScaleType的效果
        super.onDraw(drawCanvas);

        //3.获取形状
        if ((this.mMaskBitmap == null) || (this.mMaskBitmap.isRecycled())) {
            this.mMaskBitmap = getShapeBitmap();
        }

        //4.设置Xfermode模式
        mPaint.reset();
        mPaint.setFilterBitmap(false);
        mPaint.setXfermode(mXfermode);

        //5.绘制形状
        drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);

        //将准备好的bitmap绘制出来
        mPaint.setXfermode(null);
        paramCanvas.drawBitmap(bitmap, 0, 0, mPaint);
    }
}  