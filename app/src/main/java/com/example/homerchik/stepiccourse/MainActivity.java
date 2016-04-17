package com.example.homerchik.stepiccourse;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.homerchik.stepiccourse.model.Band;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Bitmap mPlaceHolderBitmap = null;
    private String PROTOCOL = "http";
    private String HOST = "download.cdn.yandex.net";
    private String FILE = "/mobilization-2016/artists.json";
    private ArrayList<Band> model = null;

    private void setToolbarCaption(Toolbar tb) {
        try {
            tb.setTitle(getString(R.string.mainActivityCaption));
            tb.setTitleTextColor(getResources().getColor(R.color.headerColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBitmap(String url, ImageView imageView, Object ... params){
            if (cancelPotentialWork(url, imageView)) {
                final HttpGetCover task = new HttpGetCover(imageView);
                final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(params);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarCaption(toolbar);
        setSupportActionBar(toolbar);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni.isConnected()) {
            Log.println(Log.DEBUG, ACTIVITY_SERVICE, "Network is connected");
        }
        try {
            URL url = new URL(PROTOCOL, HOST, -1, FILE);
            new HttpGetBandObjects().execute(url, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView lw = (ListView) findViewById(R.id.main_acivity_lw);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Band dataToSend = model.get(position);
                Intent intent = new Intent(getBaseContext(), DescriptionActivity.class);
                intent.putExtra("dataChunk", dataToSend);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getBaseContext().startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {super.onStop();}

    static class AsyncDrawable extends BitmapDrawable{
        private final WeakReference<HttpGetCover> bitmapWorkerTaskRef;

        public AsyncDrawable(Resources res, Bitmap bitmap, HttpGetCover task){
            super(res, bitmap);
            bitmapWorkerTaskRef = new WeakReference<>(task);
        }

        public HttpGetCover getTask(){
            return bitmapWorkerTaskRef.get();
        }


    }

    public class HttpGetBandObjects
            extends AsyncTask<Object, Void, ArrayList<Band>> {
        URL url = null;
        String TAG = "Band data getter says";
        private Context c;

        protected ArrayList<Band> doInBackground(Object... inData) {
            try {
                url = (URL) inData[0];
                c = getBaseContext();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                setupConnection(con);
                Gson gson = new Gson();
                Type collectionType = new TypeToken<ArrayList<Band>>() {}.getType();
                model = gson.fromJson(getData(con), collectionType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return model;
        }

        @Override
        protected void onPostExecute(ArrayList<Band> a) {
            CustomAdapter sAdapter = new CustomAdapter(c, a, R.layout.band_layout);
            ListView lw = (ListView) findViewById(R.id.main_acivity_lw);
            if (!(lw == null)) {
                lw.setAdapter(sAdapter);
            }
        }

        protected  void setupConnection(HttpURLConnection con){
            try {
                con.setReadTimeout(10000);
                con.setConnectTimeout(15000);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();
            }
            catch (Exception e){
                Log.d(TAG, "Error establishing connection");
            }
        }

        protected String getData(HttpURLConnection con){
            StringBuilder response = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                response.append(br.readLine());
                br.close();
            }
            catch (Exception e){
                Log.d(TAG, "Error getting data");
            }
            con.disconnect();
            return response.toString();
        }

    }



    public class CustomAdapter extends BaseAdapter {
        String TAG = "LIST VIEW ADAPTER says";
        Context context;
        int layoutId;
        List<Band> data;

        public CustomAdapter(Context context, List<Band> data, int layoutId) {
            this.layoutId = layoutId;
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(layoutId, null);

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Band bandItem = data.get(position);

            holder.bandName.setText(bandItem.getName());
            holder.bandGenres.setText(bandItem.getStringGenres());
            holder.bandAlbums.setText(bandItem.getStringAlbums(context));
            holder.bandSongs.setText(bandItem.getStringTracks(context));
            holder.position = position;

            if (bandItem.getSmallCoverUrl() != null) {
                holder.smallIW.setTag(bandItem.getSmallCoverUrl());
                loadBitmap(bandItem.getSmallCoverUrl(), holder.smallIW, bandItem, context);
//                new HttpGetCover().execute(
//                        bandItem, context, holder.smallIW);
            } else {
                Log.d(TAG, "No images found for the view setting default image");
                holder.smallIW.setTag(null);
                holder.smallIW.setImageBitmap(null);
            }

            return convertView;
        }
    }

    private class ViewHolder {
        public final ImageView smallIW;
        public final TextView bandName;
        public final TextView bandGenres;
        public final TextView bandAlbums;
        public final TextView bandSongs;
        public int position;

        public ViewHolder(View row) {
            smallIW = (ImageView) row.findViewById(R.id.iw_small_cover);
            bandName = (TextView) row.findViewById(R.id.band_name);
            bandGenres = (TextView) row.findViewById(R.id.band_genres);
            bandAlbums = (TextView) row.findViewById(R.id.band_albums);
            bandSongs = (TextView) row.findViewById(R.id.band_songs);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class HttpGetCover extends AsyncTask<Object, Void, Bitmap> {
        String data="";
        private URL url;
        private String logTag = "HttpBitmapLoader";
        private final WeakReference<ImageView> imageViewRef;
        private Context context;
        private Band bandItem;
        BitmapFactory.Options opts = new BitmapFactory.Options();

        public HttpGetCover(ImageView imageView){
            imageViewRef = new WeakReference<>(imageView);
        }

        private void setImageOpts() {
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inSampleSize = 1;
            opts.inScaled = true;
            opts.outWidth = 100;
            opts.outHeight = 100;
            opts.inPreferQualityOverSpeed = false;
        }

        private Bitmap getBitmap(String url) {
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

        private void saveImage(File dir, String name, Bitmap bmp) {
            FileOutputStream out = null;
            File f;
            try {
                f = File.createTempFile(name, null, dir);
                out = new FileOutputStream(f);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            catch (Exception e) {
                Log.d(logTag, "Error saving image occured!");
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    public FilenameFilter getFilterByFilename(final String filename){
        return new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String pattern = "[A-z0-9.,/]*(".concat(filename).concat(")[0-9]*(.tmp)");
                Matcher p = Pattern.compile(pattern).matcher(name);
                return p.matches();
            }
        };
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        bandItem = (Band) params[0];
        context = (Context) params[1];
        String filename = bandItem.getSmallCoverFileMask();
        File[] files = context.getCacheDir().listFiles(getFilterByFilename(filename));
        if (files.length != 0) {
            return BitmapFactory.decodeFile(files[0].getAbsolutePath(), opts);
        } else {
            return getAndSaveBitmap();
        }
    }

    private Bitmap getAndSaveBitmap() {
        setImageOpts();
        Bitmap bm = getBitmap(bandItem.getSmallCoverUrl());
        saveImage(context.getCacheDir(), bandItem.getSmallCoverFileMask(), bm);
        return bm;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (bitmap != null) {
            final ImageView imageView = imageViewRef.get();
            final HttpGetCover bitmapWorkerTask =
                    getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
}



