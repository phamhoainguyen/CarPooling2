package utils;


import android.util.Log;
import android.util.Patterns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


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
