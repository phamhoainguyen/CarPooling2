package model;

import java.util.Comparator;

/**
 * Created by phamh on 5/4/2017.
 */


// tao class implements Comparator de sap sep theo distance
public class RecordAndDistanceComparator implements Comparator<RecordAndDistance> {

    @Override
    public int compare(RecordAndDistance recordAndDistance, RecordAndDistance t1) {
        double dis1 = recordAndDistance.getDistance();
        double dis2 = t1.getDistance();
        if (dis1 > dis2)
            return 1;
        else if (dis1 == dis2)
            return 0;
        else
            return -1;
    }
}