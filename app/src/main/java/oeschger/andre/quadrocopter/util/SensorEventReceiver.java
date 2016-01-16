package oeschger.andre.quadrocopter.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by andre on 16.01.16.
 */
public class SensorEventReceiver extends HandlerThread implements SensorEventListener{

    private static final String TAG = "SensorEventReceiver";

    private Handler sensorEventHandler;
    private ValuesStore valuesStore;

    public SensorEventReceiver(String name, ValuesStore valuesStore){
        super(name);
        this.valuesStore = valuesStore;
        sensorEventHandler = new Handler(getLooper());
    }

    public Handler getSensorEventHandler(){
        return sensorEventHandler;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.d(TAG, "onSensorChanged runs in: " + Thread.currentThread().getName());

        switch(event.sensor.getType()){
            case Sensor.TYPE_GYROSCOPE :        valuesStore.setSensorGyroscopeX(event.values[0]);
                                                valuesStore.setSensorGyroscopeY(event.values[1]);
                                                valuesStore.setSensorGyroscopeZ(event.values[2]);
                                                break;

            case Sensor.TYPE_ACCELEROMETER :    valuesStore.setSensorAccelerometerX(event.values[0]);
                                                valuesStore.setSensorAccelerometerY(event.values[1]);
                                                valuesStore.setSensorAccelerometerZ(event.values[2]);
                                                break;

            case Sensor.TYPE_MAGNETIC_FIELD :   valuesStore.setSensorMagnetometerX(event.values[0]);
                                                valuesStore.setSensorMagnetometerY(event.values[1]);
                                                valuesStore.setSensorMagnetometerZ(event.values[2]);
                                                break;

            case Sensor.TYPE_PRESSURE :         valuesStore.setSensorAtmosphericPressure(event.values[0]);
                                                break;


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
