package com.example.test1706.MapBox;

import com.example.test1706.MapBox.Distance;
import com.example.test1706.MapBox.Duration;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
