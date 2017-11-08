package com.ou.activityrecording;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button b1;
    boolean start=false;
    private SensorManager mSensorManager;
    private Sensor acc,gyro;
    AlertDialog.Builder alertDialog;
    boolean acc1=true,gyro1=false;
    ArrayList<Float> x_acc,y_acc,z_acc,x_gyro,y_gyro,z_gyro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1=(Button)findViewById(R.id.button);









        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                start=!start;
//                b1.setText(start ? "Stop" : "Start");
//                //recordData(start);
                handler.postDelayed(start_thread,5000);

            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    }

    Handler handler = new Handler();
    Runnable start_thread = new Runnable() {
        @Override
        public void run() {
           recordData(true);
            handler.postDelayed(stop_thread, 300000);
        }
    };
    Runnable stop_thread = new Runnable() {
        @Override
        public void run() {
            recordData(false);
        }
    };

    private SensorEventListener accSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType())
            {
                case Sensor.TYPE_ACCELEROMETER:
                    //acc1 = !acc1;
                    if(acc1) {

                        //Log.e("ACCELEROMETER ", "X " + event.values[0] + " " + "Y " + event.values[1] + " " + "Z " + event.values[2] + " ");
                        x_acc.add(event.values[0]);
                        y_acc.add(event.values[1]);
                        z_acc.add(event.values[2]);
                        acc1=false;
                        gyro1=true;
                    }
                    break;

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //Log.d("MY_APP", sensor.toString() + " - " + accuracy);
        }
    };
    private SensorEventListener gyroSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType())
            {
                case Sensor.TYPE_GYROSCOPE:
                    if(gyro1) {
                        //Log.e("GYROSCOPE ", "X " + event.values[0] + " " + "Y " + event.values[1] + " " + "Z " + event.values[2] + " ");
                        x_gyro.add(event.values[0]);
                        y_gyro.add(event.values[1]);
                        z_gyro.add(event.values[2]);
                        gyro1 = false;
                        acc1=true;
                    }
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //Log.d("MY_APP", sensor.toString() + " - " + accuracy);
        }
    };

    public void recordData(boolean b){

        if(b){
            if (acc != null && gyro != null) {
                x_acc=new ArrayList<>();
                y_acc=new ArrayList<>();
                z_acc=new ArrayList<>();
                x_gyro=new ArrayList<>();
                y_gyro=new ArrayList<>();
                z_gyro=new ArrayList<>();
                mSensorManager.registerListener(accSensorListener, acc, SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(gyroSensorListener, gyro, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
        else{
            if (acc != null && gyro != null) {
                mSensorManager.unregisterListener(accSensorListener);
                mSensorManager.unregisterListener(gyroSensorListener);
                Log.e( "Size ","acc "+x_acc.size()+" gyro "+x_gyro.size());

                alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Activity Name");
                alertDialog.setMessage("Enter Name");

                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                write("Standing"+input.getText().toString());

                            }
                        });

                alertDialog.show();


            }
        }

    }



    public void write(String activityName)
    {
        try{
        CSVWriter writer1,writer2;
        FileWriter mFileWriter1,mFileWriter2;
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName1 = activityName+"_Acc"+".csv";
            String fileName2 = activityName+"_Gyro"+".csv";
            String filePath1 = baseDir + File.separator + fileName1;
            String filePath2 = baseDir + File.separator + fileName2;
//            String filePath1 = baseDir + File.separator+"MyActivityData"+ File.separator + fileName1;
//            String filePath2 = baseDir + File.separator+"MyActivityData"+ File.separator + fileName2;
            File f1 = new File(filePath1 );
            File f2 = new File(filePath1 );
            if(f1.exists() && !f1.isDirectory()){
                mFileWriter1 = new FileWriter(filePath1 , true);
                writer1 = new CSVWriter(mFileWriter1);
                mFileWriter2 = new FileWriter(filePath2, true);
                writer2 = new CSVWriter(mFileWriter2);
            }
        else {
                writer1 = new CSVWriter(new FileWriter(filePath1));
                writer2 = new CSVWriter(new FileWriter(filePath2));
            }

            for(int i=0; i<x_acc.size();i++) {
                String[] acc_data = {x_acc.get(i).toString(), y_acc.get(i).toString(),z_acc.get(i).toString()};
                writer1.writeNext(acc_data);
            }
            writer1.close();

            for(int i=0; i<x_gyro.size();i++) {
                String[] acc_data = {x_gyro.get(i).toString(), y_gyro.get(i).toString(),z_gyro.get(i).toString()};
                writer2.writeNext(acc_data);
            }
            writer2.close();
        }
        catch (Exception e){

        }

    }



}
