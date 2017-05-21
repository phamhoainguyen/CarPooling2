package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by phamh on 5/21/2017.
 */

public class SortByTime {
    // Hàm này sắp xếp các record trong list và trả về list đc sắp xếp
    public ArrayList<Record> sortByTime(ArrayList<Record> alRecord, Record searchCondition){

        // khai báo list sẽ được trả về
        ArrayList<Record> arrayList = new ArrayList<>();

        // list các đối tượng RecordAndTime
        List<RecordAndTime> alReAndTime = new ArrayList<>();

        //Thêm các record với time tương ứng vào list
        for(Record record : alRecord){
            int time = calTime(record, searchCondition);
            alReAndTime.add(new RecordAndTime(record, time));
        }

        //Sắp xếp thời gian gần nhất đến xa nhất
        Collections.sort(alReAndTime, new RecordAndTimeComparator());

        // tách từng record trong đối tượng RecordAndTime rồi thêm vào mảng
        for(RecordAndTime rat: alReAndTime){
            arrayList.add(rat.getRecord());
        }
        return  arrayList;
    }


    // Hàm này tính thời chênh lệch giữa 2 record
    public int calTime(Record record, Record searchCondition){
        DateTime dateTime = new ParseStringToDateTime().parseDateTime(record.date, record.time);
        DateTime dateTime1 = new ParseStringToDateTime().parseDateTime(searchCondition.date, searchCondition.time);

        return dateTime.calTotalMinute() - dateTime1.calTotalMinute();
    }
}
