package model;

/**
 * Created by phamh on 5/3/2017.
 */

public class DateTime {
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    DateTime(int minute, int hour, int day, int month, int year){
        this.mMinute = minute;
        this.mHour = hour;
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;
    }

    public int calTotalMinute(){
        int totalMinute = 0;
        for(int i = 1; i < mMonth; i++){
            if(i == 1 || i == 3 || i == 5 || i == 7 || i == 1 || i == 8 || i == 10 || i == 12){
                totalMinute += 60 * 24 * 31;
            }
            else if(i == 2){
                if(mYear % 400 == 0 || ((mYear % 4 == 0) && (mYear % 100 != 0))){
                    totalMinute += 60 * 24 * 29;
                }
                else {
                    totalMinute += 60 * 24 * 28;
                }
            }

            else {
                totalMinute += 60 * 24 * 30;
            }
        }

        totalMinute += mMinute + 60 * mHour + 60 * 24 * (mDay - 1);
        return totalMinute;
    }
}
