package com.example.ivanovnv.course02task03service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by IvanovNV on 16.04.2018.
 */

/**
 * Service for background load
 */
public class LoadService extends Service implements LoadThread.Callback{

    private boolean mIsLoadInProgress = false;
    private LoadThread mLoadThread;

    public static final int MSG_START_DOWNLOAD = 1;
    public static final int MSG_STOP_DOWNLOAD = 2;
    public static final int MSG_CLIENT_MESSENGER = 3;
    public static final int MSG_LOAD_IN_PROGRESS = 4;
    private static final String TAG = "TagService";
    Messenger mClientMessenger = null;


    /**
     * constructor
     */
    @Override
    public void onCreate() {

        Log.d(TAG, "LoadService onCreate:");

        mLoadThread = new LoadThread("LoadThread", this);
        mLoadThread.start();
        mLoadThread.getLooper();
    }


    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLIENT_MESSENGER:      // client send messenger for callbacks
                {
                    Log.d(TAG, "IncomingHandler handleMessage: MSG_CLIENT_MESSENGER");
                    mClientMessenger = new Messenger((IBinder) msg.obj);

                    if(mIsLoadInProgress) {    // if now load in progress, send message to client
                        Message repMsg = Message.obtain(null, MSG_LOAD_IN_PROGRESS);
                        try {
                            mClientMessenger.send(repMsg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case MSG_START_DOWNLOAD:  // start load command from client
                {
                    Log.d(TAG, "IncomingHandler handleMessage: MSG_START_DOWNLOAD");
                    mIsLoadInProgress = true;
                    mLoadThread.startLoad();
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());


    /**
     * called when client bind by calling bindService()
     * @param intent - intent
     * @return - instance of iBinder object
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling startService(Intent)
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * destroy service
     */
    @Override
    public void onDestroy() {
        mLoadThread = null;
        Log.d(TAG, "LoadService onDestroy:");
    }

    /**
     * callback implementation for LoadTread. This called when load process is complete
     */
    @Override
    public void onStopLoad() {

        mIsLoadInProgress = false;
        if (mClientMessenger != null) {
            Message msg = Message.obtain(null, MSG_STOP_DOWNLOAD);
            try {
                mClientMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
