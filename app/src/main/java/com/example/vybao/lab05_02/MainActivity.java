package com.example.vybao.lab05_02;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ProgressBar pbWaiting;
    private TextView tvTopCaption;
    private EditText etInput;
    private Button btnExecute;
    private int globalValue, accum;
    private long startTime;
    private final String PATIENCE = "Some important data is being collected now.\nPlease be patient...wait...";
    private Handler handler;
    private Runnable fgRunnable, bgRunnable;
    private Thread testThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewByIds();
        initVariables();

        // Handle onClickListenner
        btnExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etInput.getText().toString();
                Toast.makeText(MainActivity.this, "You said: " + input, Toast.LENGTH_SHORT).show();
                Log.i("Thread in onClick :",Thread.currentThread().getId() + " " );

            }
        });
        Log.i("Thread in main :",Thread.currentThread().getId() + " " );

        // Start Thread
        testThread.start();
        pbWaiting.incrementProgressBy(0);
    }

    private void findViewByIds() {
        tvTopCaption = (TextView) findViewById(R.id.tv_top_caption);
        pbWaiting = (ProgressBar) findViewById(R.id.pb_waiting);
        etInput = (EditText) findViewById(R.id.et_input);
        btnExecute = (Button) findViewById(R.id.btn_execute);

    }

    private void initVariables() {
        globalValue = 0;
        accum = 0;
        startTime = System.currentTimeMillis();
        handler = new Handler();

        fgRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // Calculate new value
                    int progressStep = 5;
                    double totalTime = (System.currentTimeMillis() - startTime) / 1000;
                    synchronized (this) {
                        globalValue += 100;
                    }

                    // Update UI
                    tvTopCaption.setText(PATIENCE + totalTime + " - " + globalValue);
                    pbWaiting.incrementProgressBy(progressStep);
                    accum += progressStep;

                    if (accum >= pbWaiting.getMax()) {
                        tvTopCaption.setText(getString(R.string.bg_work_is_over));
                        pbWaiting.setVisibility(View.GONE);
                    }
                    Log.i("Thread in fgRunable :",Thread.currentThread().getId() + " " );

                } catch (Exception e) {
                    Log.e("fgRunnable", e.getMessage());
                }
            }
        };

        bgRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 20; i++) {
                        //Sleep 1 second
                        Thread.sleep(1000);

                        // Now talk to main thread
                        // Optionally change some global variable such as : globalValue

                        synchronized (this) {
                            globalValue += 1;
                        }
                        Log.i("Thread in for loop :",Thread.currentThread().getId() + "count" + i );

                        handler.post(fgRunnable);
                    }
                } catch (Exception e) {
                    Log.e("bgRunnable", e.getMessage());
                }
            }
        };

        testThread = new Thread(bgRunnable);

    }
}
