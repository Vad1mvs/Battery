package com.example.vadim.battery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Storage extends AppCompatActivity {
    private static final String TAG = "Storage";
    ProgressBar pbStorage;
    int freeStorageInt, totalStorageInt ;
    TextView tvProc, tvFree, tvNoFree, tvTotal;
    MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        tvProc = (TextView)findViewById(R.id.tvProc);
        tvFree = (TextView)findViewById(R.id.tvFree);
        tvNoFree = (TextView)findViewById(R.id.tvNoFree);
        tvTotal = (TextView)findViewById(R.id.tvTotal);

        Intent intent = getIntent();
        String freeStorage = intent.getStringExtra("freeStorage");
        String totalStorage = intent.getStringExtra("totalStorage");
        totalStorageInt = Integer.parseInt(totalStorage);
        freeStorageInt =  Integer.parseInt(freeStorage);

        pbStorage = (ProgressBar)findViewById(R.id.pbStorage);
        pbStorage.setMax(totalStorageInt);
        pbStorage.setProgress(totalStorageInt - freeStorageInt);

        int percent = mainActivity.percent(freeStorageInt, totalStorageInt);
        tvProc.setText(percent + " % ");
        double freeD = freeStorageInt;
        double freeNoD = totalStorageInt - freeStorageInt;
        double totalD = totalStorageInt;

        tvNoFree.setText("Occupied memory:" + String.format("%.2f",freeNoD / 1024 )+ " GB");
        tvFree.setText("Free memory: " + String.format("%.2f",freeD / 1024 )+ " GB");
        tvTotal.setText("Total: " + String.format("%.2f",totalD / 1024 )+ " GB");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Storage.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.sin, R.anim.sout);
    }
}
