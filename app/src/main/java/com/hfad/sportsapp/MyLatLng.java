package com.hfad.sportsapp;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MyLatLng implements Serializable {

    private double latitude;
    private double longitude;

    MyLatLng () {}
    MyLatLng(double x, double y) {
        latitude = x;
        longitude = y;
    }
//    MyLatLng(double x, double y) {
//        latLng = new LatLng(x, y);
//    }
    MyLatLng(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//        out.writeDouble(latLng.latitude);
//        out.writeDouble(latLng.longitude);
//    }

//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//        latLng = new LatLng(in.readDouble(), in.readDouble());
//    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
