package com.example.ivanovnv.course02task03service;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by IvanovNV on 16.04.2018.
 */

/**
 * Thread with Looper for background load
 */
public class LoadThread extends HandlerThread {
    private static final String TAG = "TagService";
    private Handler mMainHandler;
    private Handler mThreadHandler;
    private final static int THREAD_START_LOAD = 101;
    private Callback mCallback = null;

    /**
     * constructor
     * @param name
     */
    public LoadThread(String name, Callback callback) {
        super(name);
        mCallback = callback;
    }

    /**
     * Call back method that can be explicitly overridden if needed to execute some setup before Looper loops.
     */
    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        Log.d(TAG, "onLooperPrepared, thread id:" + Thread.currentThread().getId());
        mMainHandler = new Handler(Looper.getMainLooper());
        mThreadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case THREAD_START_LOAD: {       //command to start load
                        Log.d(TAG, "LoadThread StartLoad, thread id:" + Thread.currentThread().getId());
                        try {
                            TimeUnit.MILLISECONDS.sleep(5000);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        Log.d(TAG, "LoadThread CompleteLoad, thread id:" + Thread.currentThread().getId());
                        mMainHandler.post(new Runnable() {  // execute callback for result
                            @Override
                            public void run() {
                                mCallback.onStopLoad();
                            }
                        });
                        break;
                    }
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    /**
     * public method for start load
     */
    public void startLoad() {
        mThreadHandler.obtainMessage(THREAD_START_LOAD).sendToTarget();
    }

    /**
     * interface for callbacks
     */
    public interface Callback {
        void onStopLoad();
    }
}
