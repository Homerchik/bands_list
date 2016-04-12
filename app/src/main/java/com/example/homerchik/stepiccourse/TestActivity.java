package com.example.homerchik.stepiccourse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by homerchik on 4/8/16.
 */
public class TestActivity extends Activity {
    public static String u = "http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/300x300";
    Context context=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        new HttpLoader().execute(u);
    }

    private class HttpLoader extends AsyncTask<Object, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bm = null;
            context = getBaseContext();
            try {
                URL url = new URL((String) params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-type", "image/jpeg");
                BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                BitmapFactory.Options bmOpts = new BitmapFactory.Options();
                bmOpts.outHeight = 100;
                bmOpts.outWidth = 100;
                bmOpts.inJustDecodeBounds = true;
                bmOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                bmOpts.inSampleSize = 3;
                bm = BitmapFactory.decodeStream(bis, null, bmOpts);
//                StringBuilder sb = new StringBuilder();
//                int a;
//                while ((a = bis.read()) != -1){
//                    sb.append(a);
//                }
//                File cacheDir = context.getCacheDir();
//                File f = File.createTempFile("ololo", null, cacheDir);
//                FileWriter fw = new FileWriter(f);
//                fw.write(sb.toString().toCharArray());
//                fw.close();
//                String path = cacheDir.getPath().concat("/ololo.tmp");
//                Log.d("DEBUG", path);
//                bm = BitmapFactory.decodeFile(path, bmOpts);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Boolean r = bitmap != null;
            Log.d("DEBUG", r.toString());
            ImageView iw = (ImageView) findViewById(R.id.mememe);
            iw.setImageBitmap(bitmap);
        }
    }
}
