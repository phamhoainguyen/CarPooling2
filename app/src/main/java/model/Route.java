package model;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class Route {
    public String distance;
    public String duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}
