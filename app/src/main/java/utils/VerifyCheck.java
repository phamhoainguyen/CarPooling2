package utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Admin on 5/4/2017.
 */

public class VerifyCheck extends AsyncTask<String,Void,String> {
    final private String TAG = "VerifyCheck";
    VerifyCheckListener listener;
    public VerifyCheck(VerifyCheckListener listener)
    {
        this.listener = listener;

}
    String createURL (String pin,String requestID)
    {

        StringBuffer url = new StringBuffer(Const.VERIFY_CHECK_URL);
        url.append("pin=");
        url.append(pin);
        url.append("&request_id=");
        url.append(requestID);
        return  url.toString();

    }


    @Override
    protected String doInBackground(String... params) {
        String pin = params[0];
        String requestID = params[1];
        try {
            URL url = new URL(createURL(pin,requestID));
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
            int status = jsonData.getInt("status");
            Log.e(TAG,"onPostExecute status=" + String.valueOf(status));
            listener.onSuccessCheck(status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}