package utils;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Admin on 5/4/2017.
 */

public class VerifyRequest extends AsyncTask<String,Void,String> {
    final private String TAG = "VerifyRequest";
    VerifyRequestListener listener;
    public  VerifyRequest(VerifyRequestListener listener)
    {
        this.listener = listener;

    }
    String createURL (String phone)
    {
        String regionPhone = "84" + phone;
        StringBuffer url = new StringBuffer(Const.VERIFY_REQUEST_URL);
        url.append("number=");
        url.append(regionPhone);
        return  url.toString();

    }


    @Override
    protected String doInBackground(String... params) {
        String phone = params[0];
        try {
            URL url = new URL(createURL(phone));
            InputStream stream = url.openConnection().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuffer result = new StringBuffer();
            while((line = reader.readLine()) != null)
            {
                result.append(line + "\n");
            }
            return result.toString();

        }
        catch (MalformedURLException e)
        {

        }
        catch(IOException e)
        {

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonData = new JSONObject(s);
            String requestID = jsonData.getString("request_id");
            Log.e(TAG,"onPostExecute requestID=" + requestID);
            int status = jsonData.getInt("status");
            Log.e(TAG,"onPostExecute status=" + String.valueOf(status));
            listener.onSuccessRequest(requestID,status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}