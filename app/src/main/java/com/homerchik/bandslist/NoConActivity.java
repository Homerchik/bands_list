package com.homerchik.bandslist;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.homerchik.bandslist.R;

public class NoConActivity extends AppCompatActivity{
    private void setToolbarCaption(Toolbar tb) {
        try {
            tb.setTitle("");
            tb.setTitleTextColor(getResources().getColor(R.color.mainText));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_con);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarCaption(toolbar);
        setSupportActionBar(toolbar);
        Button refresh_btn = (Button) findViewById(R.id.btn_refresh_con);
        View snackView = findViewById(R.id.popup_failed_con);
        final Snackbar mySnackbar;
        if (snackView != null){
            mySnackbar = Snackbar.make(snackView, R.string.refresh_failed, Snackbar.LENGTH_SHORT);
        }
        else{
            mySnackbar = null;
        }
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(intent);
                    finish();
                }
                else {
                    mySnackbar.show();
                }
            }
        });
    }
}
