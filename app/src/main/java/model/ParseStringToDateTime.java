package model;

import static java.lang.Integer.parseInt;

/**
 * Created by phamh on 5/21/2017.
 */

public class ParseStringToDateTime {
    // hàm tách thời gian từ chuỗi
    public DateTime parseDateTime(String date, String time){

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
}
