package com.homerchik.bandslist.imageLoader;

import android.graphics.Bitmap;
import android.util.LruCache;

public class Cache {
    private LruCache<String, Bitmap> mMemoryCache;

    public Cache(int cacheSize){
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }
}
