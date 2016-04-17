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
//            opts.outWidth = width;
//            opts.outHeight = heigth;
            opts.inPreferQualityOverSpeed = false;
        }

//    private Bitmap getImageBitmap() {
//            Bitmap bm = null;
//            try {
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.connect();
//                InputStream is = con.getInputStream();
//                BufferedInputStream bis = new BufferedInputStream(is);
//                bm = BitmapFactory.decodeStream(bis, null, opts);
//                ImageView iw = (ImageView) findViewById(R.id.iw_big_cover);
//                int w = getBaseContext().getResources().getDisplayMetrics().widthPixels;
//                Log.d("WIDTH", String.valueOf(w));
//                Log.d("WIDTH", String.valueOf(h));
//
//                bis.close();
//                is.close();
//                con.disconnect();
//            } catch (Exception e) {
//                Log.e("DEBUG", "Error getting bitmap", e);
//            }
//            return bm;
//        }

        private Bitmap getBitmap(String url) {
            setImageOpts();
            Bitmap bm = null;
            try {
                this.url = new URL(url);
                HttpURLConnection con = (HttpURLConnection) this.url.openConnection();
                bm = BitmapFactory.decodeStream(
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
            if (bm != null){
                if (width == heigth && width != 0)
                    bm = Bitmap.createScaledBitmap(bm, width, heigth, false);
                else if (width != 0){
                    int h = bm.getHeight() * width/bm.getWidth();
                    bm = Bitmap.createScaledBitmap(bm, width, h, false);
                    bm = Bitmap.createBitmap(bm, 0, bm.getHeight() - h, width, imageViewRef.get().getHeight());
                }
            }
            return bm;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            String url = (String) params[0];
            bandItem = (Band) params[1];

            Bitmap bitmap = cache.getBitmapFromMemCache(bandItem.getName());
            if (bitmap == null) {
                bitmap = getBitmap(url);
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
