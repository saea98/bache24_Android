package com.cmi.bache24.data.remote;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by omar on 5/29/16.
 */
public class ServiceConnector {
    public enum RequestMethod
    {
        GET,
        POST
    }

    public int responseCode=0;
    String urlString = "";
    //public String message;
    //public String response;

    private static final String POST_PARAMS = "userName=Pankaj";
    //private static final String POST_PARAMS = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";


    public ServiceConnector(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public JSONObject Execute(String urlParam, String token, RequestMethod method, Map<String,Object> dataToSend ) throws Exception
    {
        urlString = urlParam;

        switch (method)
        {
            case GET:
            {
            }
            case POST:
            {

                String encodedStr = getEncodedData(dataToSend);

                try{
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    //connection.setConnectTimeout(20000);
                    //connection.setReadTimeout(15000);
                    connection.setRequestProperty("User-Agent", "");
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection.setRequestProperty("token", token);


                    // For POST only - START
                    connection.setDoOutput(true);
                    OutputStream os = connection.getOutputStream();
//                    os.write(POST_PARAMS.getBytes());
                    os.write(encodedStr.getBytes());
                    os.flush();
                    os.close();
                    // For POST only - END


                    responseCode = connection.getResponseCode();
                    System.out.println("POST Response Code : " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        // otherwise, if any other status code is returned, or no status
                        // code is returned, do stuff in the else block

                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                connection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // print result
                        System.out.println(response.toString());

                        JSONObject result = new JSONObject(response.toString());

                        return result;

                    } else {
                        // Server returned HTTP error code.
                        System.out.println("POST request not worked");
                    }



                }
                catch (MalformedURLException e) {
                    // ...
                    e.printStackTrace();
                }catch (IOException e) {
                    // writing exception to log
                    e.printStackTrace();
                }

                break;
            }
        }


        return null;
    }




    private String getEncodedData(Map<String,Object> data) {
        StringBuilder sb = new StringBuilder();
        for(String key : data.keySet()) {
            String value = null;
            try {
                if (data.get(key) != null) {
                    value = URLEncoder.encode(data.get(key).toString(), "UTF-8");

                    if(sb.length()>0)
                        sb.append("&");

                    sb.append(key + "=" + value);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
