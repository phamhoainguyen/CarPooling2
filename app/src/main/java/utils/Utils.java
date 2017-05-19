package utils;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.util.Patterns;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import model.Route;


/**
 * Created by Admin on 4/13/2017.
 */

public class Utils {

    public static String currentUserAddress = null;


    public static boolean isValidEmail (String email)
    {
        return  Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public  static  boolean isValidPhone (String phone)
    {
        return phone.matches("^09[0-9]{8}|01[0-9]{9}$");

    }


    public static String getCompleteAddressString(Activity activity, double LATITUDE, double LONGITUDE) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        return address + ", " + city + ", " + state + ", " + country;
    }

    public static BitmapDescriptor createPureTextIcon(String text) {

        Paint textPaint = new Paint(); // Adapt to your needs

        textPaint.setTextSize(25);
        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();
        int width = (int) (textWidth);
        int height = (int) ((textHeight));

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.translate(0, height);

        // For development only:
        // Set a background in order to see the
        // full size and positioning of the bitmap.
        // Remove that for a fully transparent icon.
        canvas.drawColor(Color.WHITE);

        canvas.drawText(text, 0, 0, textPaint);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
        return icon;
    }


    public static LatLng getCenterPointOfRoute(Route route){

        int i = (int)route.points.size() / 2;
        return route.points.get(i);
    }

}
