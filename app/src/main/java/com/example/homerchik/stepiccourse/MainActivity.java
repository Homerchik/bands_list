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
import com.example.homerchik.stepiccourse.model.HttpGetBandArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Cache smallCoverCache;
    String DATA_URL = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
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
        smallCoverCache = new Cache(cacheSize);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni.isConnected()) {
            Log.println(Log.DEBUG, ACTIVITY_SERVICE, "Network is connected");
        }


        ListView listView = (ListView) findViewById(R.id.main_acivity_lw);
        HttpGetBandArray task = new HttpGetBandArray(getBaseContext(), getLayoutInflater(), listView,
                smallCoverCache, MODEL);
        task.execute(DATA_URL);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {super.onStop();}

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



