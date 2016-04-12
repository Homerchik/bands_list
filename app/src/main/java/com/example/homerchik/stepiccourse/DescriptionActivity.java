package com.example.homerchik.stepiccourse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homerchik.stepiccourse.model.Band;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by homerchik on 4/9/16.
 */
public class DescriptionActivity extends AppCompatActivity {

    private void setToolbarCaption(Toolbar tb, String text) {
        try {
            tb.setTitle(text);
            tb.setTitleTextColor(getResources().getColor(R.color.headerColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTextViewText(int id, String data){
        TextView tw = (TextView) findViewById(id);
        tw.setText(data);
        tw.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);
        Band b = (Band) getIntent().getExtras().getSerializable("dataChunk");
        String[] fields = {b.getStringGenres(), b.getStringAlbums(getBaseContext()),
                b.getStringTracks(getBaseContext()), b.getStringDescription()};
        int[] ids = {R.id.tw_genres, R.id.tw_albums, R.id.tw_songs, R.id.tw_desc};
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarCaption(toolbar, b.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        upArrow.setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.DST_IN);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        for (int i = 0; i < fields.length; i++)
            setTextViewText(ids[i], fields[i]);
        new HttpGetBigCover().execute(b.getBigCoverUrl(), this);
        Log.d("DEBUG", "Set up successfully");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class HttpGetBigCover extends AsyncTask<Object, Void, Bitmap> {
        private URL url = null;
        private String fileName;
        private Context context;
        BitmapFactory.Options opts = new BitmapFactory.Options();

        private void setImageOpts() {
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inSampleSize = 1;
//            opts.inScaled = true;
            opts.inPreferQualityOverSpeed = false;
        }

        private Bitmap getImageBitmap() {
            Bitmap bm = null;
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.connect();
                InputStream is = con.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis, null, opts);
                ImageView iw = (ImageView) findViewById(R.id.iw_big_cover);
                int w = getBaseContext().getResources().getDisplayMetrics().widthPixels;
                int h = bm.getHeight() * w/bm.getWidth();
                Log.d("WIDTH", String.valueOf(w));
                Log.d("WIDTH", String.valueOf(h));
                bm = Bitmap.createScaledBitmap(bm, w, h, false);
                bm = Bitmap.createBitmap(bm, 0, bm.getHeight() - h, w, iw.getHeight());
                bis.close();
                is.close();
                con.disconnect();
            } catch (Exception e) {
                Log.e("DEBUG", "Error getting bitmap", e);
            }
            return bm;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {

            try {
                url = new URL((String) params[0]);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            context = (Context) params[1];
            setImageOpts();
//            String filename = fileName;//Band.getFilenameFromUrl(u);
//            File cache = getCacheDir();
//            ArrayList<File> files = new ArrayList<>();
//            Pattern p = Pattern.compile(fileName);
//            for (File ff : cache.listFiles()) {
//                Matcher m = p.matcher(ff.toString());
//                if (m.matches()) {
//                    Log.d("DEBUG", ff.getAbsolutePath());
//                    files.add(ff);
//                }
//            }
//            if (!files.isEmpty()) {
//                return BitmapFactory.decodeFile(files.get(0).getAbsolutePath(), opts);
//            } else {
              return getImageBitmap();
//            }
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            Log.d("DEBUG", url.toString());
            if (bm != null) {
                Log.d("DEBUG", "Bitmap setted ok");
                ImageView iw = (ImageView) findViewById(R.id.iw_big_cover);
                iw.setImageBitmap(bm);
            } else {
                Log.d("DEBUG", "No bitmap found.");
            }
        }
    }
}
