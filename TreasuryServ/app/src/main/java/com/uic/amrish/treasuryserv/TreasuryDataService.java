package com.uic.amrish.treasuryserv;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uic.amrish.common.ITreasuryServInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TreasuryDataService extends Service {

    private String TAG = "TreasuryDataService";

    Handler handler = new Handler(Looper.getMainLooper());

    //static Integer noOfClientsBound=new Integer(0);

    static String lastStatus="Service not yet bound.";


    public TreasuryDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "in onBind");
        //updateDisplay("Bound to client but idle");
        //Post to the Main Activity to show the status message
        handler.post(MainActivity.getRunnableBoundNoClients());
        //keep the last status stored for the activity. If the service started without the activity.
        lastStatus="Bound to client but idle";
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        //updateDisplay("Service unbound from all clients");
        //Post to the Main Activity to show the status message
        handler.post(MainActivity.getRunnableUnbound());
        //keep the last status stored for the activity. If the service started without the activity.
        lastStatus="Service unbound from all clients";
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        //updateDisplay("Service destroyed.");
        Log.i(TAG, "onDestroy");
        //Post to the Main Activity to show the status message
        handler.post(MainActivity.getRunnableDestroyed());
        //keep the last status stored for the activity. If the service started without the activity.
        lastStatus="Service destroyed.";
        super.onDestroy();
    }

    private void decrementClients(){

        /*synchronized (noOfClientsBound) {
            Log.i(TAG,"noOfClientsBound decr Before:"+noOfClientsBound);
            noOfClientsBound--;
            if (noOfClientsBound == 0) {*/
                handler.post(MainActivity.getRunnableBoundNoClients());
                lastStatus="Bound to client but idle";
            /*}
            Log.i(TAG,"noOfClientsBound decr After:"+noOfClientsBound);
        }*/
    }

    private void incrementClients(){
        /*synchronized (noOfClientsBound){
            Log.i(TAG,"noOfClientsBound incr Before:"+noOfClientsBound);
            noOfClientsBound++;
            if (noOfClientsBound == 1) {*/
                handler.post(MainActivity.getRunnableBoundWithClients());
                lastStatus="Bound to client and running an API method";
            /*}
            Log.i(TAG,"noOfClientsBound incr After:"+noOfClientsBound);

        }*/

    }

    /*private void updateDisplay(String status){

        Message msg=getUiHandler().obtainMessage(UPDATE_STATUS);
        msg.obj=status;
        getUiHandler().sendMessage(msg);

    }*/
    public ITreasuryServInterface.Stub mBinder = new ITreasuryServInterface.Stub() {

        @Override
        public double yearlyCash(int year) throws RemoteException {
            Log.i(TAG, "In yearlyCash");
            //updateDisplay("Bound to client and running an API method");
            //handler.post(MainActivity.getRunnableBoundWithClients());
            incrementClients();

            //SELECT AVG("open_today") avg FROM t1 WHERE ("date" >= '2010-01-01' AND "date" <= '2010-12-31') and "open_mo" = "open_today" and "is_total"="1"
            String url = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=SELECT%20AVG(%22open_today%22)%20avg%20FROM%20t1%20WHERE%20(%22date%22%20%3E=%20%27" + year + "-01-01%27%20AND%20%22date%22%20%3C=%20%27" + year + "-12-31%27)%20and%20%22open_mo%22%20=%20%22open_today%22%20and%20%22is_total%22=%221%22";

            //get the response data in json format
            String data = getJSON(url, 3000);
            Log.i(TAG, data);

            // get the required data
            double avgData = Double.valueOf((String)getArrayFromJson(data,"avg").get(0));

            /*try {
                Thread.sleep(150000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            Log.i(TAG, "After call to responseToMonthlyCash");

            decrementClients();
            return avgData;
        }

        @Override
        public List<String> monthlyCash(int year) throws RemoteException {
            Log.i(TAG, "In monthlyCash");
            //updateDisplay("Bound to client and running an API method");
            //handler.post(MainActivity.getRunnableBoundWithClients());
            incrementClients();

            //SELECT "open_today" FROM t1 WHERE ("date" >= '2010-01-01' AND "date" <= '2010-12-31') and "open_mo" = "open_today" and "is_total"="1"
            String url = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=SELECT%20%22open_today%22%20FROM%20t1%20WHERE%20(%22date%22%20%3E%20%27" + year + "-01-01%27%20AND%20%22date%22%20%3C%20%27" + year + "-12-31%27)%20and%20%22open_mo%22%20=%20%22open_today%22%20and%20%22is_total%22=%221%22";

            //get the json data
            String data = getJSON(url, 3000);


            Log.i(TAG, data);

            //get the data in the required format
            List<String> dataList = getArrayFromJson(data,"open_today");

            /*try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            Log.i(TAG, "After call to responseToMonthlyCash");
            handler.post(MainActivity.getRunnableBoundNoClients());

            decrementClients();
            return dataList;
        }

        @Override
        public List<String> dailyCash(int day, int month, int year, int workingDays) throws RemoteException {
            Log.i(TAG, "In dailyCash");
            //updateDisplay("Bound to client and running an API method");
            //handler.post(MainActivity.getRunnableBoundWithClients());
            incrementClients();

            //select day,open_today from t1 where rowid >=( SELECT MIN("rowid") FROM t1 WHERE "date" >= '2015-5-5' and "is_total"="1" ) and "is_total"="1" limit 5;
            // 1-12 should be 01-12
            String url = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=%20select%20day,open_today%20from%20t1%20where%20%20rowid%20%3E=(%20SELECT%20MIN(%22rowid%22)%20FROM%20t1%20WHERE%20%22date%22%20%3E=%20%27" + year + "-" + addZero(month) + "-" + addZero(day) + "%27%20and%20%22is_total%22=%221%22%20)%20%20and%20%22is_total%22=%221%22%20limit%20" + workingDays + ";%20";

            String data = getJSON(url, 3000);
            Log.i(TAG, data);

            List<String> dataList = getArrayFromJson(data,"open_today");

/*
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
*/
            Log.i(TAG, "After call to responseToDailyCash");
            handler.post(MainActivity.getRunnableBoundNoClients());

            decrementClients();
            return dataList;
        }
    };

    private List getArrayFromJson(String data,String parameterName){
        //Using a Json parser to parse the array of entries in the required format
        JsonParser parser=new JsonParser();
        JsonArray array = (JsonArray) parser.parse(data);

        List<String> dataList=new ArrayList<>();

        //iterating over the entries to get just the data
        for(int i=0;i<array.size();i++){
            Log.i(TAG,((JsonObject)array.get(i)).get(parameterName)+"");
            dataList.add(((JsonObject)array.get(i)).get(parameterName)+"");
        }

        return dataList;
    }

    public String getJSON(String url, int timeout) {

        HttpURLConnection c = null;
        try {
            // Create the HTTP connection
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();

            //GET method of HTTP is beign used
            c.setRequestMethod("GET");
            //c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            //Response code for finding if successful
            int status = c.getResponseCode();

            switch (status) {
                //if successful, read the data in BufferedReader
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    //close the connection
                    c.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();

                }
            }
        }
        return null;
    }

    //needed to add 0 to the input data. eg: "01" instead of 1
    private String addZero(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }
}