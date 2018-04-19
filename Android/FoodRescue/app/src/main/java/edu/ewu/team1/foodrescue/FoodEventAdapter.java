package edu.ewu.team1.foodrescue;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class FoodEventAdapter implements ListAdapter {
    private ArrayList<FoodEvent> events;
    private ArrayList<DataSetObserver> observers;
    private Context context;

    public FoodEventAdapter(ArrayList<FoodEvent> events, Context context) {
        this.events = events;
        observers = new ArrayList<>();
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.notification_item, parent);
        }
        FoodEvent event = events.get(position);
        ((TextView) convertView.findViewById(R.id.textViewTitle)).setText(event.title);
        ((TextView) convertView.findViewById(R.id.textViewBody)).setText(event.body);
        LatLng loc = new LatLng(event.getLat(), event.getLng());
        //Map
        MapView mapView = convertView.findViewById(R.id.mapViewEater);
        mapView.getMapAsync(googleMap -> {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17.0f));
            googleMap.addMarker(new MarkerOptions().position(loc).title(event.title));
        });

        convertView.findViewById(R.id.buttonClear).setOnClickListener(v -> {
            events.remove(position);
            for(DataSetObserver o : observers){
                o.notifyAll();
            }
        });

        convertView.findViewById(R.id.buttonDirections).setOnClickListener(v -> {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + event.getLat() + "," + event.getLng()));
            context.startActivity(intent);
        });
        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return events.get(position).timestamp;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
