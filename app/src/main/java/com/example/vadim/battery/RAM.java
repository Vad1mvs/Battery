package com.example.vadim.battery;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RAM extends AppCompatActivity {
    private static final String TAG = "RAM";

    ProgressBar pbRAM;
    TextView tvProc, tvFree, tvNoFree, tvTotal;
    int freeRAMInt, totalRAMInt ;
    MainActivity mainActivity = new MainActivity();

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;

    ListView list;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ram);

        tvProc = (TextView)findViewById(R.id.tvProc);
        tvFree = (TextView)findViewById(R.id.tvFree);
        tvNoFree = (TextView)findViewById(R.id.tvNoFree);
        tvTotal = (TextView)findViewById(R.id.tvTotal);
        list = (ListView)findViewById(R.id.list);
        Intent intent = getIntent();
        String freeRamInt = intent.getStringExtra("freeRam");
        String totalRamInt = intent.getStringExtra("totalRam");
        totalRAMInt = Integer.parseInt(totalRamInt);
        freeRAMInt =  Integer.parseInt(freeRamInt);
        Log.d(TAG, freeRamInt + " / "+ totalRamInt);

        pbRAM = (ProgressBar)findViewById(R.id.pbRAM);
        pbRAM.setMax(Integer.parseInt(totalRamInt));
        pbRAM.setProgress(Integer.parseInt(freeRamInt));
        packageManager = getPackageManager();

        new LoadApplications().execute();


        int percent = mainActivity.percent(freeRAMInt, totalRAMInt);
        tvProc.setText(percent + " % ");
        double freeD = freeRAMInt;
        double freeNoD = totalRAMInt - freeRAMInt;
        double totalD = totalRAMInt;

        tvNoFree.setText("Occupied:" + String.format("%.3f",freeD / 1024 )+ " GB");
        tvFree.setText("Free: " + String.format("%.3f",freeNoD / 1024 )+ " GB");
        tvTotal.setText("Total: " + String.format("%.3f",totalD / 1024 )+ " GB");





    }

    private void displayAboutDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");
        builder.setMessage("Message");

        builder.setPositiveButton("Know More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://stacktips.com"));
                startActivity(browserIntent);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No Thanks!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }


    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(RAM.this, R.layout.snippet_list_row, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            list.setAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(RAM.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RAM.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.sin, R.anim.sout);
    }
}