package edu.ewu.team1.foodrescue;

import android.support.annotation.NonNull;

public class FoodEvent implements Comparable<FoodEvent> {
    String title;
    String body;
    private String lat;
    private String lng;
    long timestamp;

    public FoodEvent(String title, String body, String lat, String lng, long timestamp) {
        this.title = title;
        this.body = body;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public FoodEvent(String parseMe) {
        String[] data = parseMe.split(",");
        title = data[0];
        body = data[1];
        lat = data[2];
        lng = data[3];
        timestamp = Long.parseLong(data[4]);
    }

    @Override
    public String toString() {
        return title + "," + body + "," + lat + "," + lng + "," + timestamp + "\n";
    }

    @Override
    public int compareTo(@NonNull FoodEvent o) {
        if (this.timestamp - o.timestamp == 0) return 0;
        if ((this.timestamp - o.timestamp) < 0) return -1;
        return 1;
    }

    public double getLat() {
        return Double.parseDouble(lat);
    }

    public double getLng() {
        return Double.parseDouble(lng);
    }
}
