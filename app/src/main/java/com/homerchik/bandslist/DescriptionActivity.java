package com.homerchik.bandslist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homerchik.bandslist.R;
import com.homerchik.bandslist.imageLoader.AsyncDrawableWrapper;
import com.homerchik.bandslist.imageLoader.Cache;
import com.homerchik.bandslist.imageLoader.HttpGetCover;
import com.homerchik.bandslist.model.Band;

public class DescriptionActivity extends AppCompatActivity {
    int WIDTH;

    private void setToolbarCaption(Toolbar tb, String text) {
        try {
            tb.setTitle(text);
            tb.setTitleTextColor(getResources().getColor(R.color.mainText));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTextViewText(int id, String data){
        TextView tw = (TextView) findViewById(id);
        if (tw != null) {
            tw.setText(data);
            tw.setMovementMethod(new ScrollingMovementMethod());
        }
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
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        for (int i = 0; i < fields.length; i++)
            setTextViewText(ids[i], fields[i]);
        WIDTH = getBaseContext().getResources().getDisplayMetrics().widthPixels;
        loadBitmap(b.getBigCoverUrl(), (ImageView) findViewById(R.id.iw_big_cover), b);
        Log.d("DEBUG", "Set up successfully");
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public void loadBitmap(String url, ImageView imageView, Band item){
            if (AsyncDrawableWrapper.cancelPotentialWork(url, imageView)) {
                final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
                final int cacheSize = maxMemory / 20;
                final HttpGetCover task = new HttpGetCover(new Cache(cacheSize), imageView, 0, WIDTH);
                final AsyncDrawableWrapper.AsyncDrawable asyncDrawable =
                    new AsyncDrawableWrapper.AsyncDrawable(null, null, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(url, item);
            }
    }
}
