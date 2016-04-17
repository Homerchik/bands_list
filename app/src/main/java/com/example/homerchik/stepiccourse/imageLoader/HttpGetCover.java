package com.example.homerchik.stepiccourse.imageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.homerchik.stepiccourse.model.Band;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpGetCover extends AsyncTask<Object, Void, Bitmap> {
    private Cache cache;
    private final WeakReference<ImageView> imageViewRef;
    int heigth;
    int width;
    String data="";
    URL url;
    String logTag = "HttpBitmapLoader";
    Band bandItem;
    BitmapFactory.Options opts = new BitmapFactory.Options();

    public HttpGetCover(Cache cache, ImageView imageView, int heigth, int width){
        this.heigth = heigth;
        this.width = width;
        this.cache = cache;
        imageViewRef = new WeakReference<>(imageView);
    }

        private void setImageOpts() {
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inSampleSize = 1;
            opts.inScaled = true;
            opts.outWidth = width;
            opts.outHeight = heigth;
            opts.inPreferQualityOverSpeed = false;
        }

        private Bitmap getBitmap(String url) {
            setImageOpts();
            try {
                this.url = new URL(url);
                HttpURLConnection con = (HttpURLConnection) this.url.openConnection();
                return BitmapFactory.decodeStream(
                    new BufferedInputStream(con.getInputStream()), null, opts);
            }
            catch (MalformedURLException e){
                Log.d(logTag, "Malformed url - ".concat(url));
                e.printStackTrace();
            }
            catch (IOException e){
                Log.d(logTag, "I/O exception during connection caught");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            bandItem = (Band) params[0];

            Bitmap bitmap = cache.getBitmapFromMemCache(bandItem.getName());
            if (bitmap == null) {
                bitmap = getBitmap(bandItem.getSmallCoverUrl());
            }
            cache.addBitmapToMemCache(bandItem.getName(), bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (bitmap != null) {
                final ImageView imageView = imageViewRef.get();
                final HttpGetCover bitmapWorkerTask =
                        AsyncDrawableWrapper.getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
