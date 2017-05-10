package model;

import static java.lang.Integer.parseInt;

/**
 * Created by phamh on 5/2/2017.
 */


// class này có 2 hàm chính là lọc qua phương tiện và lọc qua thời gian
public class SortRecords{


    public static boolean compareVehicle(Record temp, Record searchCondition){
        if(temp.vehicle.equals(searchCondition.vehicle))
            return true;
        return false;
    }

    public static DateTime parseDateTime(String date, String time){

        // date[0] là ngày
        //date[1] là tháng
        //date[2] là năm
        String[] dateStr = date.split("-");

        int day = parseInt(dateStr[0]);
        int month = parseInt(dateStr[1]);
        int year = parseInt(dateStr[2]);

        //time[0] la gio
        //time[1] la phut
        String[] timeStr = time.split(":");
        int hour = parseInt(timeStr[0]);
        int minute = parseInt(timeStr[1]);



        return new DateTime(minute, hour, day, month, year);
    }

    public static boolean compareTime(Record temp, Record searchCondition){

        if( parseDateTime(temp.date, temp.time).calTotalMinute() -
                parseDateTime(searchCondition.date, searchCondition.time).calTotalMinute() >= 0 )
            return true;

        return false;
    }

//    public double calDistanceBetweeenTwoLocations(LatLng latLng1, LatLng latLng2){
//        double dX = latLng2.longitude - latLng1.longitude;
//        double dY = latLng2.latitude - latLng1.latitude;
//
//        double distance = Math.sqrt( ( dX * dX ) + ( dY * dY ) );
//        return distance;
//    }

//    public ArrayList sortRecordsList(ArrayList arrayList){
//
//
//        return new ArrayList();
//    }
//
//

}
