package model;

/**
 * Created by phamh on 5/21/2017.
 */

public class RecordAndTime {

    private Record mRecord;
    private int mTime;

    RecordAndTime(Record record, int time){
        this.mRecord = record;
        this.mTime = time;
    }

    public Record getRecord(){
        return mRecord;
    }

    public int getTime(){
        return mTime;
    }


}
