package com.homerchik.bandslist.model;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.homerchik.bandslist.DescriptionActivity;
import com.homerchik.bandslist.NoConActivity;
import com.example.homerchik.bandslist.R;
import com.homerchik.bandslist.imageLoader.Cache;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import android.view.LayoutInflater;

public class HttpGetBandArray extends AsyncTask<Object, Void, ArrayList<Band>>{
    URL url = null;
    Cache cache;
    String TAG = "Band data getter says";
    private Context c;
    private ArrayList<Band> modelRef;
    private LayoutInflater inflater;
    private ListView listView;

    public HttpGetBandArray(Context c, LayoutInflater inflater, ListView listView,
                            Cache cache, ArrayList<Band> modelRef){
        this.c = c;
        this.inflater = inflater;
        this.listView = listView;
        this.cache = cache;
        this.modelRef = modelRef;
    }

    protected ArrayList<Band> doInBackground(Object... params) {
        try {
            url = new URL((String) params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            setupConnection(con);
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<Band>>() {}.getType();
            modelRef = gson.fromJson(getData(con), collectionType);
            Collections.sort(modelRef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelRef;
    }

    @Override
    protected void onPostExecute(final ArrayList<Band> bandArr) {
        BandItemAdapter sAdapter = new BandItemAdapter(inflater, c, cache, bandArr, R.layout.band_layout);
        if (!(listView == null)) {
            listView.setAdapter(sAdapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    Band dataToSend = bandArr.get(position);
                    Intent intent = new Intent(c, DescriptionActivity.class);
                    intent.putExtra("dataChunk", dataToSend);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(c, NoConActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(intent);
                }
            }
        });
    }

    protected  void setupConnection(HttpURLConnection con){
        try {
            con.setReadTimeout(50000);
            con.setConnectTimeout(60000);
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
