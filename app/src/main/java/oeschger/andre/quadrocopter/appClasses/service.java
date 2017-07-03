package oeschger.andre.quadrocopter.appClasses;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

import oeschger.andre.quadrocopter.MainTask;


/**
 * Created by andre on 28.10.15.
 * This Service is the background service for the Quadrocopter app.
 * It managed device ressources.
 *
 */
public class service extends Service
{
    private static final String TAG = "QuadrocopterService";

    private PowerManager.WakeLock wakeLock;

    private MainTask myMainTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        startForeground(1,new Notification.Builder(getBaseContext())
                        .setContentTitle("Quadrocopter")
                        .setContentText("The Quadrocopter Service is runnig")
                        .build()
        );
    }

        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStart");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "QuadrocopterWakelockTag");
        wakeLock.acquire();

        SensorManager sensorManager;sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        Log.d(TAG, "Starting worker Threads");
        myMainTask = new MainTask(usbManager, sensorManager);
        Log.d(TAG, "Worker threads started");

        return START_STICKY; // We want this service to continue running until it is explicitly stopped, so return sticky.

    }

    @Override
    public void onDestroy() {

        if(myMainTask!=null){
            myMainTask.shutdownNow();
        }

        wakeLock.release();
        this.stopForeground(true);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

}

