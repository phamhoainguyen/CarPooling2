package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by phamh on 5/4/2017.
 */

public class SortByDistance {

    // Hàm này sắp xếp các record trong list và trả về list đc sắp xếp
    public ArrayList<Record> sortByDistance(ArrayList<Record> alRecord, Record searchCondition){

        // khai báo list sẽ được trả về
        ArrayList<Record> arrayList = new ArrayList<>();

        // list các đối tượng RecordAndDistance
        List<RecordAndDistance> alReAndDis = new ArrayList<>();

        //Thêm các record với distance tương ứng vào list
        for(Record record : alRecord){
            double dis = calDistance(record, searchCondition);
            alReAndDis.add(new RecordAndDistance(record, dis));
        }

        //Sắp xếp alReAndDis theo thứ tự chênh lệch xa dần
        Collections.sort(alReAndDis, new RecordAndDistanceComparator());

        // tách từng record trong đối tượng RecordAndDistance rồi thêm vào mảng
        for(RecordAndDistance rad: alReAndDis){
            arrayList.add(rad.getRecord());
            rad.getDistance();
            System.out.print(rad.getDistance() + "\n");
        }
        return  arrayList;
    }


    // Hàm này tính khoảng tổng chênh lệch giữa 2 điểm xuất phát và 2 điểm kết thúc
    public double calDistance(Record record, Record searchCondition){

//        double dStartX = record.startLocation.longitude - searchCondition.startLocation.longitude;
//        double dStartY = record.startLocation.latitude - searchCondition.startLocation.latitude;
//        double distanceBetweenTwoStartLocations = Math.sqrt( ( dStartX * dStartX ) + ( dStartY * dStartY ) );
//
//
//        double dEndX = record.endLocation.longitude - searchCondition.endLocation.longitude;
//        double dEndY = record.endLocation.latitude - searchCondition.endLocation.latitude;
//        double distanceBetweenTwoEndLocations = Math.sqrt( ( dEndX * dEndX ) + ( dEndY * dEndY ) );

        // tính tổng chênh lệch để sắp xếp record
        return  SortRecords.calDistance(record.startLocation, searchCondition.startLocation)
                + SortRecords.calDistance(record.endLocation, searchCondition.endLocation);
    }
}
