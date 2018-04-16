package com.example.ivanovnv.course02task03service;

import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private static final String TAG = "TagService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.Button);
        mTextView = findViewById(R.id.TextView);
        mProgressBar = findViewById(R.id.ProgressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadState();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mButton.setOnClickListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mButton = null;
        mTextView = null;
        mProgressBar = null;
    }

    private void setLoadState() {
        mButton.setEnabled(false);
        mTextView.setText(R.string.Loading);
        mProgressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "setLoadState: ");
    }

    private void setReadyState() {
        mButton.setEnabled(true);
        mTextView.setText(R.string.Ready);
        mProgressBar.setVisibility(View.GONE);
        Log.d(TAG, "setReadyState: ");
    }
}
