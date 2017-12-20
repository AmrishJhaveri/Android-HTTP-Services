package com.uic.amrish.treasuryserv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.uic.amrish.common.R;

public class MainActivity extends Activity {

    private TextView statusTextView;

    private final int UPDATE_STATUS = 1;

    //private Handler uiHandler = new Handler();

    //Runnables which can be called by the service to update the status
    //Only initialized in the onCreate method
    private static Runnable runnableNotBound;
    private static Runnable runnableBoundNoClients;
    private static Runnable runnableBoundWithClients;
    private static Runnable runnableUnbound;
    private static Runnable runnableDestroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = (TextView) findViewById(R.id.status);

        runnableNotBound = new PostStatusRunnable("Service not yet bound.");
        runnableBoundNoClients = new PostStatusRunnable("Bound to client but idle");
        runnableBoundWithClients = new PostStatusRunnable("Bound to client and running an API method");
        runnableUnbound = new PostStatusRunnable("Service unbound from all clients");
        runnableDestroyed = new PostStatusRunnable("Service destroyed.");

        //uiHandler.post(getRunnableNotBound());

        //We use the lastStatus field form the Service if the Activity is created after the Service
        statusTextView.setText(TreasuryDataService.lastStatus);

    }

    private class PostStatusRunnable implements Runnable {

        private String text;

        PostStatusRunnable(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            //sets the text to the required status
            synchronized (statusTextView) {
                statusTextView.setText(text);
            }
        }
    }

    public static Runnable getRunnableNotBound() {
        return runnableNotBound;
    }

    public static Runnable getRunnableBoundNoClients() {
        return runnableBoundNoClients;
    }

    public static Runnable getRunnableBoundWithClients() {
        return runnableBoundWithClients;
    }

    public static Runnable getRunnableUnbound() {
        return runnableUnbound;
    }

    public static Runnable getRunnableDestroyed() {
        return runnableDestroyed;
    }
}
