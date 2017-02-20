package com.example.vadim.battery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Battery extends AppCompatActivity {
    private static final String TAG = "BATTERY";
    ProgressBar pbBattery;
    final int max = 100;
    int freeBatteryInt;
    TextView tvProc, tvFree, tvNoFree;
    MainActivity mainActivity = new MainActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        tvProc = (TextView)findViewById(R.id.tvProc);

        Intent intent = getIntent();
        String level = intent.getStringExtra("level");
        freeBatteryInt =  Integer.parseInt(level);

        int percent = mainActivity.percent(freeBatteryInt, max);
        tvProc.setText(percent + " % ");
        pbBattery = (ProgressBar)findViewById(R.id.pbBattery);
        pbBattery.setMax(max);
        pbBattery.setProgress(Integer.parseInt(level));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Battery.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.sin, R.anim.sout);
    }
}
