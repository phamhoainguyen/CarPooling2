package utils;


import android.util.Patterns;


/**
 * Created by Admin on 4/13/2017.
 */

public class Utils {
    public static boolean isValidEmail (String email)
    {
        return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public  static  boolean isValidPhone (String phone)
    {
        return phone.matches("^09[0-9]{8}|01[0-9]{9}$");

    }




}
