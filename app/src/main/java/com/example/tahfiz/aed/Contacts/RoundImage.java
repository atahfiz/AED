package com.example.tahfiz.aed.Contacts;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by tahfiz on 4/4/2016.
 */
public class RoundImage extends Drawable {

    private final Bitmap bitmap;
    private final Paint paint;
    private final RectF rectF;
    private final int bitmapWidth;
    private final int bitmapHeight;

    public RoundImage(Bitmap bitmap){
        this.bitmap = bitmap;
        rectF = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        bitmapWidth = this.bitmap.getWidth();
        bitmapHeight = this.bitmap.getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(rectF,paint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        rectF.set(bounds);
    }

    @Override
    public void setAlpha(int alpha) {
        if (paint.getAlpha() != alpha){
            paint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return bitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmapHeight;
    }

    public void setAntiAlias(boolean antiAlias){
        paint.setAntiAlias(antiAlias);
        invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        paint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        paint.setDither(dither);
        invalidateSelf();
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
