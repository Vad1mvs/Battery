package com.example.vadim.battery;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "com.example.vadim.battery";
    public static final String LOAD = "load";

    private TextView tvBattery, tvRam, tvStorage, tvUsedStorage, tvUsedRam, tvOptimising;
    int level, resultRam , resultStorage , freeRamInt, totalRamInt, freeStorageInt, totalStorageInt , batteryLevel;
    ProgressBar pbBattery, pbRAM, pbStorage;
    Handler handler;
    final int max = 100;
    Context context;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, BatteryService.class));
        handler = new Handler();

        pbBattery = (ProgressBar)findViewById(R.id.pbBattery);
        pbBattery.setMax(max);
        pbBattery.setProgress(0);

        pbRAM = (ProgressBar)findViewById(R.id.pbRAM);
        pbRAM.setMax( totalRamMemorySize());
        pbRAM.setProgress(0);

        pbStorage = (ProgressBar)findViewById(R.id.pbStorage);
        pbStorage.setMax(totalStorage());
        pbStorage.setProgress(0);

        tvBattery = (TextView)findViewById(R.id.tvBattery);
        tvRam = (TextView)findViewById(R.id.tvRam);
        tvStorage = (TextView)findViewById(R.id.tvStorage);
        tvUsedStorage = (TextView)findViewById(R.id.tvUsedStorage);
        tvUsedRam = (TextView)findViewById(R.id.tvUsedRam);
        tvOptimising = (TextView)findViewById(R.id.tvOptimising);


        resultRam =  percent(freeRamMemorySize(), totalRamMemorySize());
        tvRam.setText(resultRam + " % " );
        resultStorage =  percent( freeStorage(), totalStorage());
        tvStorage.setText(resultStorage + " % " );

        double freeStorage = freeStorage();
        double freeRam = freeRamMemorySize();

        tvUsedStorage.setText(String.format("%.2f",freeStorage / 1024 )+ " GB Free");
        tvUsedRam.setText(String.format("%.2f",freeRam / 1024) + " GB Free");


        freeRamInt = freeRamMemorySize();
        totalRamInt = totalRamMemorySize();
        freeStorageInt = freeStorage();
        totalStorageInt = totalStorage();

        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCache(context);
                Toast.makeText(getApplicationContext(), "Cache cleared " , Toast.LENGTH_LONG).show();

            }
        });


        pbBattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Battery.class);
                pbBattery.setProgress(level);
                intent.putExtra("level", String.valueOf(level));
                startActivity(intent);
                overridePendingTransition(R.anim.fin, R.anim.fout);
            }
        });

        pbRAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RAM.class);
                intent.putExtra("freeRam", String.valueOf(freeRamInt));
                intent.putExtra("totalRam", String.valueOf(totalRamInt));
                startActivity(intent);
                overridePendingTransition(R.anim.fin, R.anim.fout);
            }
        });

        pbStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Storage.class);
                intent.putExtra("freeStorage", String.valueOf(freeStorageInt));
                intent.putExtra("totalStorage", String.valueOf(totalStorageInt));
                startActivity(intent);
                overridePendingTransition(R.anim.fin, R.anim.fout);
            }
        });


        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {


                level = intent.getIntExtra("level", 0);
                tvBattery.setText(level + " % ");
                batteryLevel = level;

            }
        };

        Log.d(TAG, freeRamInt + " / " +totalRamInt + " / " +freeStorageInt + " / " +totalStorageInt + " / " + batteryLevel );
        IntentFilter intFilt = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(br, intFilt);


        // ----- update every second -----

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(updateProgress);
            }
        });
        t.start();
    }

    Runnable updateProgress = new Runnable() {
        @Override
        public void run() {

            pbBattery.setProgress(level);
            pbRAM.setProgress( freeRamMemorySize());
            pbStorage.setProgress((totalStorage() - freeStorage()));
        }
    };

    Runnable show = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(show, 1000);
        }
    };



    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();
    }


    public int percent(int free, int total){
        int per = ((free * 100)/ total);

        return per;
    }

    // ------ find free and total RAM ------


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public int totalRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        int availableMegs = (int) (mi.totalMem / 1048576L);
        return availableMegs;
    }

    public int  freeRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        int availableMegs = (int) (mi.availMem / 1048576L);
        return availableMegs;
    }

    // -----find free and total Storage -----

    public int freeStorage(){
        File path = Environment.getDataDirectory();
        StatFs stat2 = new StatFs(path.getPath());
        long blockSize2 = stat2.getBlockSize();
        long availableBlocks2 = stat2.getAvailableBlocks();
        int freeStor = (int) ((availableBlocks2 * blockSize2) / 1048576);
        return freeStor;
    }


    public int totalStorage(){
        File path2 = Environment.getDataDirectory();
        StatFs stat3 = new StatFs(path2.getPath());
        long blockSize3 = stat3.getBlockSize();
        long totalBlocks3 = stat3.getBlockCount();
        int  totalStor = (int) ((totalBlocks3 * blockSize3) / 1048576);
        return totalStor;
    }

    // ----- Clear cache -----

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
