package com.ou.mqtt_test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    //public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    public static final String BROKER_URL = "tcp://192.168.137.119:1883";

    public static final String TOPIC = "test";
    private SensorManager mSensorManager;
    private Sensor acc,gyro;
    JSONArray jarray;
    private MqttClient client;
    MqttTopic sensorTopic;
    boolean acc1=false,gyro1=false;

    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1=(Button)findViewById(R.id.button);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(SensorListener, acc, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(SensorListener, gyro, SensorManager.SENSOR_DELAY_GAME);
        jarray = new JSONArray();
        try {
            client = new MqttClient(BROKER_URL, MqttClient.generateClientId(), new MemoryPersistence());
            //client = new MqttClient(BROKER_URL,"123456");
            sensorTopic = client.getTopic(TOPIC);

            client.connect();





        } catch (MqttException e) {
            Log.e("Mqtt",e.toString());
        }


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    final MqttMessage message = new MqttMessage(String.valueOf("Hello").getBytes());
                sensorTopic.publish(message);

                } catch (MqttException e) {
                    Log.e("Mqtt",e.toString());
                }
            }
        });
    }
    private SensorEventListener SensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            try {

                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
//                        acc1 = !acc1;
//                        if(acc1 && gyro1) {
                            Log.e("ACCELEROMETER ", "X " + event.values[0] + " " + "Y " + event.values[1] + " " + "Z " + event.values[2] + " ");
                            jarray.put(0, event.values[0]);
                            jarray.put(1, event.values[1]);
                            jarray.put(2, event.values[2]);

                        //}
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        Log.e("GYROSCOPE ", "X "+event.values[0]+" "+"Y "+event.values[1]+" "+"Z "+event.values[2]+" ");
                        jarray.put(3,event.values[0]);
                        jarray.put(4,event.values[1]);
                        jarray.put(5,event.values[2]);
                        gyro1=true;
                        break;

                }
            if (jarray.get(0) !=null && jarray.get(3) !=null){
                String data=jarray.toString();
                final MqttMessage message = new MqttMessage(data.substring(1,data.length()-1).getBytes());
                //Log.e("sent ", jarray.toString());
                sensorTopic.publish(message);
                jarray = new JSONArray();
            }
            }
            catch (JSONException | MqttException e){
                Log.e("Exception",e.toString());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //Log.d("MY_APP", sensor.toString() + " - " + accuracy);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        try {
            client.disconnect();
            mSensorManager.unregisterListener(SensorListener);
        } catch (MqttException e) {
            Log.e("Mqtt",e.toString());
        }
    }
}
