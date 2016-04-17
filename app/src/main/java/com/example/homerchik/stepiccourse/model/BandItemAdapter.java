
package com.example.homerchik.stepiccourse.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homerchik.stepiccourse.imageLoader.Cache;
import com.example.homerchik.stepiccourse.R;
import com.example.homerchik.stepiccourse.imageLoader.AsyncDrawableWrapper;
import com.example.homerchik.stepiccourse.imageLoader.HttpGetCover;
import java.util.List;

public class BandItemAdapter extends BaseAdapter {
    Integer HEIGHT = 100;
    Integer WIDTH = 100;
    String TAG = "LIST VIEW ADAPTER says";
    Context context;
    Cache cache;
    int layoutId;
    LayoutInflater inflater;
    List<Band> data;

    public BandItemAdapter(LayoutInflater inflater, Context context, Cache cache,
                           List<Band> data, int layoutId) {
        this.cache = cache;
        this.inflater = inflater;
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

    public void loadBitmap(String url, ImageView imageView, Band item){
            if (AsyncDrawableWrapper.cancelPotentialWork(url, imageView)) {
                final HttpGetCover task = new HttpGetCover(cache, imageView, HEIGHT, WIDTH);
                final AsyncDrawableWrapper.AsyncDrawable asyncDrawable =
                    new AsyncDrawableWrapper.AsyncDrawable(null, null, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(url, item);
            }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
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
            loadBitmap(bandItem.getSmallCoverUrl(), holder.smallIW, bandItem);
        } else {
            Log.d(TAG, "No images found for the view setting default image");
            holder.smallIW.setTag(null);
            holder.smallIW.setImageBitmap(null);
        }

        return convertView;
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
}