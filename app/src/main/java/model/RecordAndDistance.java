package model;

/**
 * Created by phamh on 5/4/2017.
 */

// class này gồm có record và khoảng cách từ record đó đến record searchCondition
public class RecordAndDistance {
    private Record mRecord;
    private double mDistance;

    RecordAndDistance(Record record, double distance){
        this.mRecord = record;
        this.mDistance = distance;
    }

    public Record getRecord(){
        return mRecord;
    }

    public double getDistance(){
        return mDistance;
    }



}
