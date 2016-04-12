package com.example.homerchik.stepiccourse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.homerchik.stepiccourse.model.Band;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private String EXCEPTION_TAG = "EXCEPTION CAUGHT";
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
    public void onStop() {
        super.onStop();
    }

    public class HttpGetBandObjects
            extends AsyncTask<Object, Void, ArrayList<Band>> {

        private Context c;

        protected ArrayList<Band> doInBackground(Object... inData) {
            try {
                URL url1 = (URL) inData[0];
                c = getBaseContext();
                HttpURLConnection con = (HttpURLConnection) url1.openConnection();
                con.setReadTimeout(10000);
                con.setConnectTimeout(15000);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();
                Gson gson = new Gson();
                StringBuilder response = new StringBuilder();
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(con.getInputStream()));
                try {
                    response.append(br.readLine());
                } finally {
                    br.close();
                    con.disconnect();
                }
                Type collectionType = new TypeToken<ArrayList<Band>>() {
                }.getType();
                model = gson.fromJson(response.toString(), collectionType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return model;
        }

        @Override
        protected void onPostExecute(ArrayList<Band> a) {
            CustomAdapter sAdapter = new CustomAdapter(c, a, R.layout.band_layout);
            ListView lw = (ListView) findViewById(R.id.main_acivity_lw);
            try {
                lw.setAdapter(sAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class HttpGetSmallCover extends AsyncTask<Object, Void, Bitmap> {
        private URL url;
        private ImageView iV;
        private String fileName;
        BitmapFactory.Options opts = new BitmapFactory.Options();

        private void setImageOpts() {
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inSampleSize = 1;
            opts.inScaled = true;
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
                bis.close();
                is.close();
                con.disconnect();
            } catch (Exception e) {
                Log.e("DEBUG", "Error getting bitmap", e);
            }
            return bm;
        }

        private void saveImage(File dir, String name, Bitmap bmp) {
            FileOutputStream out = null;
            File f;
            try {
                f = File.createTempFile(name, null, dir);
                out = new FileOutputStream(f);
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            iV.setVisibility(View.INVISIBLE);
        }

        protected URL stringToURL(String url){
            URL converted = null;
            try{
                converted = new URL(url);
            }
            catch (MalformedURLException e){
                Log.d(EXCEPTION_TAG, "Wrong formatted URL got");
                e.printStackTrace();
            }
            return converted;
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
            url = stringToURL((String) params[0]);
            fileName = (String) params[1];
            iV = (ImageView) params[2];
            setImageOpts();
            String filename = fileName;//Band.getFilenameFromUrl(u);
            File[] files = getCacheDir().listFiles(getFilterByFilename(filename));
            if (files.length != 0) {
                return BitmapFactory.decodeFile(files[0].getAbsolutePath(), opts);
            } else {
                return getAndSaveBitmap(getCacheDir(), filename);
            }
        }

        private Bitmap getAndSaveBitmap(File cache, String fname) {
            Bitmap bm = null;
            try {
                bm = getImageBitmap();
                saveImage(cache, fname, bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            Log.d("DEBUG", url.toString());
            if (bm != null) {
                Log.d("DEBUG", "Bitmap setted ok");
                iV.setImageBitmap(bm);
                iV.setVisibility(View.VISIBLE);
            } else {
                Log.d("DEBUG", "No bitmap found.");
            }
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
                new HttpGetSmallCover().execute(
                        bandItem.getSmallCoverUrl(), bandItem.getSmallCoverFileMask(), holder.smallIW);
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
            smallIW = (ImageView) row.findViewById(R.id.small_cover_iw);
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
}
