package model;

import java.util.Comparator;

/**
 * Created by phamh on 5/21/2017.
 */

public class RecordAndTimeComparator implements Comparator<RecordAndTime> {

    @Override
    public int compare(RecordAndTime recordAndTime, RecordAndTime t1) {
        int dis1 = recordAndTime.getTime();
        int dis2 = t1.getTime();
        if (dis1 > dis2)
            return 1;
        else if (dis1 == dis2)
            return 0;
        else
            return -1;
    }
}
