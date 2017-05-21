package model;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by phamh on 5/2/2017.
 */


// class này có 2 hàm chính là lọc qua phương tiện và lọc qua thời gian
public class FilterRecords {

    public static final double DISTANCE_UNIT = 108.822;
    //hàm kiểm tra có cùng loại phương tiện không
    public static boolean checkVehicle(Record temp, Record searchCondition){
        if(temp.vehicle.equals(searchCondition.vehicle))
            return true;
        return false;
    }




    // kiểm tra thời gian có hợp lệ không
    public static boolean checkTime(Record temp, Record searchCondition){

        if( new ParseStringToDateTime().parseDateTime(temp.date, temp.time).calTotalMinute() -
                new ParseStringToDateTime().parseDateTime(searchCondition.date, searchCondition.time).calTotalMinute() >= 0)
            return true;

        return false;
    }

    public static boolean checkValidUsers(Record temp){

        if( FirebaseAuth.getInstance().getCurrentUser().getUid().equals(temp.uid))
            return false;
        return true;
    }


    // kiểm tra có nằm trong bán kính radius hay không?
    public static boolean checkRegion(Record temp, Record searchCondition, int radius){
        if(calDistance(temp.startLocation, searchCondition.startLocation) * DISTANCE_UNIT  <= radius
                && calDistance(temp.endLocation, searchCondition.endLocation) * DISTANCE_UNIT <= radius)
            return true;
        return false;
    }

    // Hàm này tính khoảng cách giữa 2 điểm
    public static double calDistance(Latlng point1, Latlng point2){
        double dPointX = point1.longitude - point2.longitude;
        double dPointY = point1.latitude - point2.latitude;
        return  Math.sqrt( ( dPointX * dPointX ) + ( dPointY * dPointY ) );
    }


    public static boolean checkAllConditions(Record temp, Record seachCondition, int radius){
        if( checkValidUsers(temp) && checkVehicle(temp, seachCondition)
                && checkTime(temp, seachCondition) && checkRegion(temp, seachCondition, radius))
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
