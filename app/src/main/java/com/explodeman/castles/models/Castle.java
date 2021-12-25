package com.explodeman.castles.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


@Entity(indices = {@Index(value = {"id"},
        unique = true)})
public class Castle implements Parcelable, ClusterItem {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String directory;

    @Embedded
    private LatLng geo;

    @Ignore
    private double distance;

    public Castle() {
    }

    @Ignore
    private Castle(String id, String name, String directory, LatLng geo) {
        this.id = id;
        this.name = name;
        this.directory = directory;
        this.geo = geo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public LatLng getGeo() {
        return geo;
    }

    public void setGeo(LatLng geo) {
        this.geo = geo;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(directory);
        dest.writeParcelable(geo, PARCELABLE_WRITE_RETURN_VALUE);
    }

    public static final Creator<Castle> CREATOR = new Creator<Castle>() {
        @Override
        public Castle createFromParcel(Parcel source) {
            String id = source.readString();
            String name = source.readString();
            String directory = source.readString();
            LatLng  geo = source.readParcelable(LatLng.class.getClassLoader());

            return new Castle(id, name, directory, geo);
        }

        @Override
        public Castle[] newArray(int size) {
            return new Castle[size];
        }
    };

    @NonNull
    @Override
    public LatLng getPosition() {
        return geo;
    }

    @Nullable
    @Override
    public String getTitle() {
        return name;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return "snippet";
    }
}
