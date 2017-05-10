package model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Admin on 4/20/2017.
 */

public class Record implements Serializable {

    public String uid;
    public String name;
    public String wayPoint;
    public String vehicle;
    public String date;
    public String time;
    public int sit;
    public Boolean luggage;
    public String price;
    public Latlng endLocation;
    public String origin;
    public String destination;
    public Latlng startLocation;
    public Record()
    {
        endLocation = new Latlng();
        startLocation = new Latlng();
    }


}
