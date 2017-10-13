package com.axis.colorpickerlib;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DDA_Admin on 20/5/16.
 */
public class ColorPickerDataManager {
    Context mContext;
    String localjson;

    public ColorPickerDataManager(Context context) {
        mContext=context;
        getService();

    }

    void getService(){

        // ======= get colors from service using volley  ========================


        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url ="http://4axisappserver.com/admin/api/v1/colors/get/all";

        //post api key
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("apiKey", "akl8t3QesGhD3eEHdDFZEyCzYhQRgXsrXtXPt7fn3RUIcCd07Y");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // create json to save data =========================
                        //JSONObject parent = new JSONObject();
                        //JSONArray dataArray = new JSONArray();

                        Log.d("GetJson","true"+ response.toString());
                        localjson=response.toString();
                        writeToFile(localjson);

                        /*try {
                            parent.put("data", dataArray);
                            localjson=parent.toString(2);
                            writeToFile(localjson);
                            //Log.d("output", localjson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
//====================================================
                       // adapter=new CustomBaseAdapter(mContext, rowItems);
                        //adapter.setCallBack(tempCall);
                        //listView.setAdapter(adapter);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("LOG", "Error: " + error.getMessage());
            }
        }){
            // Passing some request headers (when sending api key)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(jsonObjReq);


    }



    private void writeToFile(String data) {

        String state;
        state=Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)){

            File Root=Environment.getExternalStorageDirectory();
            File Dir=new File(Root,"Android/data/com.axis.drawingdesk.v3/files/colors");
            if(!Dir.exists()){
                Dir.mkdirs();
                Log.e("Exception", "dir not exist");
            }

            File file=new File(Dir,"colorjson.txt");
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileOutputStream fos=new FileOutputStream(file);
                fos.write(data.getBytes());
                fos.close();
                //Toast.makeText(mContext.getApplicationContext(), "save", Toast.LENGTH_LONG).show();
                Log.e("Exception", "file save");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else{
            Log.e("Exception", "sd card not found");
        }


    }

}
