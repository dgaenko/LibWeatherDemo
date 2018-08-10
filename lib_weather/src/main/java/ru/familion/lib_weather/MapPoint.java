package ru.familion.lib_weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class map object location
 */
public class MapPoint implements Parcelable {

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public MapPoint() {
    }

    public MapPoint(String location) {
        String[] arr = location.split(",");
        if (arr.length > 0) {
            latitude = Double.parseDouble(arr[0]);
            longitude = Double.parseDouble(arr[1]);
        }
    }

    public MapPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toLatLngString() {
        return latitude + "," + longitude;
    }


    protected MapPoint(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapPoint> CREATOR = new Creator<MapPoint>() {
        @Override
        public MapPoint createFromParcel(Parcel in) {
            return new MapPoint(in);
        }

        @Override
        public MapPoint[] newArray(int size) {
            return new MapPoint[size];
        }
    };

}
