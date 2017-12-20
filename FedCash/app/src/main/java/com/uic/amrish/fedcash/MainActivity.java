package com.uic.amrish.fedcash;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uic.amrish.common.DataSharingUtility;
import com.uic.amrish.common.ITreasuryServInterface;
import com.uic.amrish.common.R;

import java.util.List;

public class MainActivity extends Activity {

    private String TAG = "MainActivity";

    //Buttons
    private Button btnMonthlyCash;
    private Button btnDailyCash;
    private Button btnYearlyCash;
    private Button btnViewAll;
    private Button btnUnbindService;
    private Button btnMcSubmit;
    private Button btnDcSubmit;
    private Button btnYcSubmit;

    //Display for the response of the service call
    private TextView mcDisplay;
    private TextView dcDisplay;
    private TextView ycDisplay;

    //Layout for all 3 screens
    private LinearLayout mcLinearLayout;
    private LinearLayout dcLinearLayout;
    private LinearLayout ycLinearLayout;

    //Inputs
    private EditText mcInputYear;
    private EditText dcInputDay;
    private EditText dcInputMonth;
    private EditText dcInputYear;
    private EditText dcInputWorkingDays;
    private EditText ycInputYear;

    // Handler to post from the worker thread
    private Handler uiHandler = new Handler();

    //Threads for the service api call
    private MonthlyCashServiceThread monthlyCashServiceThread;
    private DailyCashServiceThread dailyCashServiceThread;
    private YearlyCashServiceThread yearlyCashServiceThread;

    //AIDL for calling the APIs
    private ITreasuryServInterface mITreasuryServInterface;

    //finding out whether the service is bound or not
    private Boolean isBound = false;

    //used for storing in a List for showing on the fragment
    private final String MONTHLY_CASH = "Monthly Cash";
    private final String DAILY_CASH = "Daily Cash";
    private final String YEARLY_CASH = "Yearly Cash";

    // used for error message on validation
    private final String VALIDATION_YEAR = " Year";
    private final String VALIDATION_MONTH = " Month";
    private final String VALIDATION_DAY = " Day";
    private final String VALIDATION_WORKING_DAYS = " Working Days";

    //defining limits for the validation calls
    private final int LOWER_LIMIT_DAY = 1;
    private final int UPPER_LIMIT_DAY = 31;
    private final int LOWER_LIMIT_MONTH = 1;
    private final int UPPER_LIMIT_MONTH = 12;
    private final int LOWER_LIMIT_YEAR = 2006;
    private final int UPPER_LIMIT_YEAR = 2016;
    private final int LOWER_LIMIT_WORKING_DAYS = 5;
    private final int UPPER_LIMIT_WORKING_DAYS = 25;

    //Service connection, to bound or not to
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connected");
            mITreasuryServInterface = ITreasuryServInterface.Stub.asInterface(service);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service dis-connected");
            mITreasuryServInterface = null;
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //btnTest = (Button) findViewById(R.id.btn_test);
        btnMonthlyCash = (Button) findViewById(R.id.monthly_cash);
        btnDailyCash = (Button) findViewById(R.id.daily_cash);
        btnYearlyCash = (Button) findViewById(R.id.yearly_avg);
        btnViewAll = (Button) findViewById(R.id.view_all_calls);
        btnUnbindService = (Button) findViewById(R.id.unbind_service);
        btnMcSubmit = (Button) findViewById(R.id.mc_submit);
        btnDcSubmit = (Button) findViewById(R.id.dc_submit);
        btnYcSubmit = (Button) findViewById(R.id.yc_submit);

        mcDisplay = (TextView) findViewById(R.id.mc_display);
        dcDisplay = (TextView) findViewById(R.id.dc_display);
        ycDisplay = (TextView) findViewById(R.id.yc_display);

        mcInputYear = (EditText) findViewById(R.id.mc_input_year);
        dcInputDay = (EditText) findViewById(R.id.dc_input_day);
        dcInputMonth = (EditText) findViewById(R.id.dc_input_month);
        dcInputYear = (EditText) findViewById(R.id.dc_input_year);
        dcInputWorkingDays = (EditText) findViewById(R.id.dc_input_working_days);
        ycInputYear = (EditText) findViewById(R.id.yc_input_year);

        mcLinearLayout = (LinearLayout) findViewById(R.id.mc_ll);
        dcLinearLayout = (LinearLayout) findViewById(R.id.dc_ll);
        ycLinearLayout = (LinearLayout) findViewById(R.id.yc_ll);

        /*Intent intent = new Intent(ITreasuryServInterface.class.getName());
        ResolveInfo info = getPackageManager().resolveService(intent, Context.BIND_AUTO_CREATE);
        intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));*/

        //boolean value returned form the function call for success or failure.
        boolean isSuccessful = bindService(createIntentForService(), mConnection, BIND_AUTO_CREATE);

        if (!isSuccessful) {
            Toast.makeText(getApplicationContext(), "Could not bind to service!", Toast.LENGTH_SHORT).show();
        }

        btnMonthlyCash.setOnClickListener(btnMonthlyCashOnClickListener);
        btnDailyCash.setOnClickListener(btnDailyCashOnClickListener);
        btnYearlyCash.setOnClickListener(btnYearlyCashOnClickListener);
        btnViewAll.setOnClickListener(btnViewAllOnClickListener);
        btnUnbindService.setOnClickListener(btnUnbindServiceOnClickListener);
        btnMcSubmit.setOnClickListener(btnMcSubmitOnClickListener);
        btnDcSubmit.setOnClickListener(btnDcSubmitOnClickListener);
        btnYcSubmit.setOnClickListener(btnYcSubmitOnClickListener);
    }

    View.OnClickListener btnYcSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            checkServiceBound();

            //Validate the input. If valid then create a thread and make api call
            if (isValid(VALIDATION_YEAR, ycInputYear.getText().toString(), LOWER_LIMIT_YEAR, UPPER_LIMIT_YEAR)) {

                yearlyCashServiceThread = new YearlyCashServiceThread(Integer.valueOf(ycInputYear.getText().toString()));
                Thread thread = new Thread(yearlyCashServiceThread);
                thread.start();
            }
        }
    };

    View.OnClickListener btnMcSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkServiceBound();

            //Validate the input. If valid then create a thread and make api call
            if (isValid(VALIDATION_YEAR, mcInputYear.getText().toString(), LOWER_LIMIT_YEAR, UPPER_LIMIT_YEAR)) {

                monthlyCashServiceThread = new MonthlyCashServiceThread(Integer.valueOf(mcInputYear.getText().toString()));
                Thread thread = new Thread(monthlyCashServiceThread);
                thread.start();
            }
        }
    };

    View.OnClickListener btnDcSubmitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkServiceBound();

            boolean validation1 = isValid(VALIDATION_YEAR, dcInputYear.getText().toString(), LOWER_LIMIT_YEAR, UPPER_LIMIT_YEAR);
            boolean validation2 = isValid(VALIDATION_MONTH, dcInputMonth.getText().toString(), LOWER_LIMIT_MONTH, UPPER_LIMIT_MONTH);
            boolean validation3 = isValid(VALIDATION_DAY, dcInputDay.getText().toString(), LOWER_LIMIT_DAY, UPPER_LIMIT_DAY);
            boolean validation4 = isValid(VALIDATION_WORKING_DAYS, dcInputWorkingDays.getText().toString(), LOWER_LIMIT_WORKING_DAYS, UPPER_LIMIT_WORKING_DAYS);

            //Validate the input. If valid then create a thread and make api call
            if (validation1 && validation2 && validation3 && validation4) {
                dailyCashServiceThread = new DailyCashServiceThread(Integer.valueOf(dcInputDay.getText().toString()), Integer.valueOf(dcInputMonth.getText().toString()), Integer.valueOf(dcInputYear.getText().toString()), Integer.valueOf(dcInputWorkingDays.getText().toString()));
                Thread thread = new Thread(dailyCashServiceThread);
                thread.start();
            }
        }
    };

    View.OnClickListener btnMonthlyCashOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Only make the relevant layour visible
            mcLinearLayout.setVisibility(View.VISIBLE);
            dcLinearLayout.setVisibility(View.GONE);
            ycLinearLayout.setVisibility(View.GONE);
        }
    };

    View.OnClickListener btnDailyCashOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Only make the relevant layour visible
            mcLinearLayout.setVisibility(View.GONE);
            dcLinearLayout.setVisibility(View.VISIBLE);
            ycLinearLayout.setVisibility(View.GONE);
        }
    };

    View.OnClickListener btnYearlyCashOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Only make the relevant layour visible
            mcLinearLayout.setVisibility(View.GONE);
            dcLinearLayout.setVisibility(View.GONE);
            ycLinearLayout.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener btnUnbindServiceOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // If the service is bound, then unbind it.
            if (isBound) {
                unbindService(MainActivity.this.mConnection);
                isBound = false;
            }
        }
    };

    View.OnClickListener btnViewAllOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Start the 2nd activity
            Intent intent = new Intent(MainActivity.this, DisplayServiceCallsActivity.class);
            startActivity(intent);
        }
    };

    private Intent createIntentForService() {
        //Create intent to bind to the service
        ComponentName componentName = new ComponentName("com.uic.amrish.treasuryserv", "com.uic.amrish.treasuryserv.TreasuryDataService");
        Intent intent = new Intent(ITreasuryServInterface.class.getName());
        intent.setComponent(componentName);
        return intent;
    }


    private void checkServiceBound() {

        // If the service is not bound, then bind it . If unsuccessful, show a toast message.
        if (!isBound) {

            //boolean value returned form the function call for success or failure.
            boolean isSuccessful = bindService(createIntentForService(), mConnection, BIND_AUTO_CREATE);

            if (!isSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not bind to service!", Toast.LENGTH_SHORT).show();
            } else {
                isBound = true;
            }
        }
    }

    private class MonthlyCashServiceThread implements Runnable {

        private int year;

        public MonthlyCashServiceThread(int year) {
            this.year = year;
        }

        @Override
        public void run() {

            Log.i(TAG, "In run before bindService");
            List returnValue = null;
            try {
                if (isBound) {
                    // make api call
                    returnValue = mITreasuryServInterface.monthlyCash(year);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "In run after bindService:" + returnValue);

            if (returnValue != null) {
                //Mapping to Gson
                //Gson gson = new Gson();
                //List<MonthlyCash> monthlyCashList = Arrays.asList(gson.fromJson(returnValue, MonthlyCash[].class));
                //adding to LinkedHashMap

                //Make to a String so can be shown easily.
                final String display = numbersInString(returnValue);

                // add it to the List
                DataSharingUtility.addData(MONTHLY_CASH + "(" + year + ")", display);

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mcDisplay) {
                            mcDisplay.setText(display);
                        }
                    }
                });
            }

        }
    }

    //Make a String from the List<String>
    private String numbersInString(List numbers) {
        String tempStr = "";
        for (Object temp : numbers) {
            tempStr += "\n" + (String) temp;
        }

        return tempStr;
    }

    //Thread for Daily Cash
    private class DailyCashServiceThread implements Runnable {

        private int day;
        private int month;
        private int year;
        private int workingDays;

        public DailyCashServiceThread(int day, int month, int year, int workingDays) {
            this.day = day;
            this.month = month;
            this.year = year;
            this.workingDays = workingDays;
        }

        @Override
        public void run() {

            Log.i(TAG, "In run before bindService");
            List<String> returnValue = null;
            try {
                if (isBound) {
                    //make api call
                    returnValue = mITreasuryServInterface.dailyCash(day, month, year, workingDays);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "In run after bindService:" + returnValue);

            if (returnValue != null) {
                //Mapping to Gson
                //List<DailyCash> dailyCashList = Arrays.asList(gson.fromJson(returnValue, DailyCash[].class));
                //adding to LinkedHashMap


                final String display = numbersInString(returnValue);
                //Add to list
                DataSharingUtility.addData(DAILY_CASH + "(" + year + "," + month + "," + day + "," + workingDays + ")", display);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (dcDisplay) {
                            dcDisplay.setText(display);
                        }
                    }
                });
            }

        }
    }

    //Thread for yearly cash
    private class YearlyCashServiceThread implements Runnable {

        private int year;

        public YearlyCashServiceThread(int year) {
            this.year = year;
        }

        @Override
        public void run() {

            Log.i(TAG, "In run before bindService");
            double returnValue = 0.0;
            try {
                if (isBound) {
                    returnValue = mITreasuryServInterface.yearlyCash(year);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "In run after bindService:" + returnValue);


            //Mapping to Gson
            //Gson gson = new Gson();
            //YearlyCash yearlyCashAvg = gson.fromJson(returnValue, YearlyCash.class);
            //adding to LinkedHashMap


            final String display = Double.toString(returnValue);
            //add to list
            DataSharingUtility.addData(YEARLY_CASH + "(" + year + ")", display);
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (ycDisplay) {
                        ycDisplay.setText(display);
                    }
                }
            });

        }
    }

    //common validation for all number fields
    private boolean isValid(String variableName, String inputNumber, int lowerLimit, int upperLimit) {
        String regex = "\\d+";
        boolean error = true;
        if (inputNumber.matches(regex)) {
            int tempYear = Integer.valueOf(inputNumber);
            if (tempYear >= lowerLimit && tempYear <= upperLimit) {
                error = false;
            }
        }
        if (error) {
            Toast.makeText(getApplicationContext(), "Input" + variableName + " is invalid.", Toast.LENGTH_SHORT).show();
        }

        return !error;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "destroyed");
        //if bound to service then unbind from it.
        if (isBound) {
            unbindService(this.mConnection);
            isBound = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        //if not bound to the service then bind to it.
        if (!isBound) {
            bindService(createIntentForService(), mConnection, BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        /*if (isBound) {
            unbindService(this.mConnection);
            isBound = false;
        }*/
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}