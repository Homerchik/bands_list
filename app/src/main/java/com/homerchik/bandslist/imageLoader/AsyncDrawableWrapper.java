package com.homerchik.bandslist.imageLoader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class AsyncDrawableWrapper {
    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<HttpGetCover> bitmapWorkerTaskRef;

        public AsyncDrawable(Resources res, Bitmap bitmap, HttpGetCover task){
            super(res, bitmap);
            bitmapWorkerTaskRef = new WeakReference<>(task);
        }

        public HttpGetCover getTask(){
            return bitmapWorkerTaskRef.get();
        }
    }

    public static HttpGetCover getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getTask();
            }
        }
        return null;
    }

    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        final HttpGetCover bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            if (bitmapData.equals("") || !bitmapData.equals(url)) {
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

}
