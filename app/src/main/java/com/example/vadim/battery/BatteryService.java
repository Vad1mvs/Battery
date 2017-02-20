package com.example.vadim.battery;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BatteryService extends Service {
    private static final String TAG = "MAIN";
    ExecutorService es;

    public int onStartCommand(Intent intent, int flags, int startId) {
        int load ;

        try {


            load = intent.getIntExtra(MainActivity.LOAD, 1);

            BatteryService.MyRun mr = new BatteryService.MyRun(load);
            es.execute(mr);
        }  catch (NullPointerException e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // Register Receiver.
        es = Executors.newFixedThreadPool(2);
    }

    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    class MyRun implements Runnable {

        int load;

        public MyRun(int load) {
            this.load = load;
        }

        public void run() {
            try {


                Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                intent.putExtra(MainActivity.LOAD, load);
                sendBroadcast(intent);
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }

    }

}

