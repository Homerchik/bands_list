package com.homerchik.bandslist.model;

import android.content.Context;

import com.example.homerchik.bandslist.R;

import java.io.Serializable;
import java.util.ArrayList;

public class Band implements Serializable, Comparable{
    private static final long serialVersionUID = 0L;

    class Cover implements Serializable{
        private static final long serialVersionUID = 1L;
        private String big;
        private String small;

        @Override
        public String toString() {
            return "Cover{" +
                    "big='" + big + '\'' +
                    ", small='" + small + '\'' +
                    '}';
        }
    }

    public String getBig() {
        return cover.big;
    }

    public String getSmall() {
        return cover.small;
    }

    private int id;
    private String name = "No connection";
    private ArrayList<String> genres;
    private int tracks;
    private int albums;
    private String link;
    private String description;
    private Cover cover;

    public String getStringGenres() {
        StringBuilder genresSB = new StringBuilder();
        for (String s: genres){
            genresSB.append(s.concat(", "));
        }
        int index = genresSB.lastIndexOf(",");
        if (index != -1){
            genresSB.deleteCharAt(index);
            genresSB.deleteCharAt(index);
        }
        return genresSB.toString();
    }

    public String getStringTracks(Context c) {
        return c.getResources().getQuantityString(R.plurals.track_plurals, this.tracks, this.tracks);
    }

    public String getStringAlbums(Context c) {
        return c.getResources().getQuantityString(R.plurals.album_plurals, this.albums, this.albums);
    }

    public String getStringLink() {
        return link;
    }

    public String getStringDescription() {
        StringBuilder desc = new StringBuilder(description);
        desc.replace(0, 1, desc.subSequence(0, 1).toString().toUpperCase());
        return desc.toString();
    }

    public String getSmallCoverUrl() {
        return getSmall();
    }

    public String getBigCoverUrl() {
        return getBig();
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return "Band{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genres=" + genres.toString() +
                ", tracks=" + tracks +
                ", albums=" + albums +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", cover=" + cover +
                '}';
    }

    @Override
    public int compareTo(Object another) {
        return this.getName().compareTo(((Band)another).getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Band)) return false;
        Band band = (Band) o;
        return (id == band.id && name.equals(band.name));

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + (genres != null ? genres.hashCode() : 0);
        result = 31 * result + tracks;
        result = 31 * result + albums;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
