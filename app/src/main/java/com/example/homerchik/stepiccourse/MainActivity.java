package com.example.homerchik.stepiccourse;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.homerchik.stepiccourse.imageLoader.Cache;
import com.example.homerchik.stepiccourse.model.Band;
import com.example.homerchik.stepiccourse.model.BandItemAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Cache cache;
    private String DATA_URL = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
    private ArrayList<Band> MODEL = null;

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

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        cache = new Cache(cacheSize);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni.isConnected()) {
            Log.println(Log.DEBUG, ACTIVITY_SERVICE, "Network is connected");
        }
        try {
            URL url = new URL(DATA_URL);
            new HttpGetBandObjects().execute(url, cache);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView lw = (ListView) findViewById(R.id.main_acivity_lw);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Band dataToSend = MODEL.get(position);
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

    public class HttpGetBandObjects
            extends AsyncTask<Object, Void, ArrayList<Band>> {
        URL url = null;
        Cache cache;
        String TAG = "Band data getter says";
        private Context c;

        protected ArrayList<Band> doInBackground(Object... inData) {
            try {
                url = (URL) inData[0];
                cache = (Cache) inData[1];
                c = getBaseContext();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                setupConnection(con);
                Gson gson = new Gson();
                Type collectionType = new TypeToken<ArrayList<Band>>() {}.getType();
                MODEL = gson.fromJson(getData(con), collectionType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return MODEL;
        }

        @Override
        protected void onPostExecute(ArrayList<Band> bandArr) {
            BandItemAdapter sAdapter = new BandItemAdapter(getLayoutInflater(), c, cache, bandArr, R.layout.band_layout);
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



