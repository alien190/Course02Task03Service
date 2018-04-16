package com.example.ivanovnv.course02task03service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This is MainActivity
 */
public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private static final String TAG = "TagService";
    Messenger mService = null;
    boolean mBound;

    /**
     * constructor
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate:");

        mButton = findViewById(R.id.Button);
        mTextView = findViewById(R.id.TextView);
        mProgressBar = findViewById(R.id.ProgressBar);

        Intent intent = new Intent(this, LoadService.class);
        startService(intent);
        bindService(intent, mConnection, 0);

    }

    /**
     * When the activity enters the Started state, the system invokes this callback
     */
    @Override
    protected void onStart() {
        super.onStart();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadState();
                Message msg = Message.obtain(null, LoadService.MSG_START_DOWNLOAD, 0, 0);
                try {
                    mService.send(msg);
                }
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * When your activity is no longer visible to the user, it has entered the Stopped state, and the system invokes the onStop() callback
     */
    @Override
    protected void onStop() {
        super.onStop();
        mButton.setOnClickListener(null);
    }

    /**
     * Called before the activity is destroyed. This is the final call that the activity receives
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity onDestroy:");
        mButton = null;
        mTextView = null;
        mProgressBar = null;
        unbindService(mConnection);
    }

    /**
     * Set Views to load state
     */
    private void setLoadState() {
        mButton.setEnabled(false);
        mTextView.setText(R.string.Loading);
        mProgressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "setLoadState: ");
    }

    /**
     * Set Views to ready state
     */
    private void setReadyState() {
        mButton.setEnabled(true);
        mTextView.setText(R.string.Ready);
        mProgressBar.setVisibility(View.GONE);
        Log.d(TAG, "setReadyState: ");
    }

    /**
     * Class for interacting with the main interface of the service
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /**
         * This is called when the connection with the service has been
         * established, giving us the object we can use to
         * interact with the service.
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            Message msg = Message.obtain(null, LoadService.MSG_CLIENT_MESSENGER, mMessenger.getBinder());
            try {
                mService.send(msg);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * This is called when the connection with the service has been
         * unexpectedly disconnected -- that is, its process crashed.
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
            setReadyState();
        }
    };

    /**
     * Handler of incoming messages from service.
     */
    class IncomingServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LoadService.MSG_LOAD_IN_PROGRESS: {  // Now load in progress
                    Log.d(TAG, "IncomingServiceHandler handleMessage: MSG_LOAD_IN_PROGRESS");
                    setLoadState();
                    break;
                }
                case LoadService.MSG_STOP_DOWNLOAD: {  // load is complete
                    Log.d(TAG, "IncomingServiceHandler handleMessage: MSG_STOP_DOWNLOAD");
                    setReadyState();
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for service to send messages to IncomingServiceHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingServiceHandler());
}
