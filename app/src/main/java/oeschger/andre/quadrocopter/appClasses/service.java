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
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import oeschger.andre.quadrocopter.MainTask;


/**
 * Created by andre on 28.10.15.
 */
public class service extends Service
{
    private static final String TAG = "MyService";

    private UsbAccessory mAccessory;
    private ParcelFileDescriptor.AutoCloseInputStream fis;
    private ParcelFileDescriptor.AutoCloseOutputStream fos;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private SensorManager sensorManager;

    private MainTask myMainTask;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null) {
                    stopSelf();
                }
            }
        }
    };

    private void startWorkerThreads(){

        //TODO handle disconnect from accestion

        Log.d(TAG, "Starting Threads");
        myMainTask = new MainTask(fis,fos,sensorManager);
        Log.d(TAG, "Threadstarted");


    }

    private void stopWorkerThreads(){
        // Cancel any thread currently running a connection
        if(myMainTask!=null){
            myMainTask.shutdownNow();
        }
    }


    private boolean openAccessory() {

        boolean connected = false;

        Log.d(TAG, "openAccessory");

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbAccessory[] accessoryList = manager.getAccessoryList();

        Log.d(TAG,"accessoryList created");

        if (accessoryList != null){
            Log.d(TAG,"accessoryList is");
            Log.d(TAG,accessoryList.toString());
            Log.d(TAG,"printing accessoryList");
            for(int i = 0;i<accessoryList.length;i++){
                Log.d(TAG,accessoryList[i].toString());
            }

            Log.d(TAG,"accessories printed");
        } else{
            Log.d(TAG,"accessoryList is null");
        }

        if(accessoryList != null && accessoryList[0] != null) {
            Log.d(TAG,"They are not null");
            ParcelFileDescriptor pfd = manager.openAccessory(accessoryList[0]);
            fos = new ParcelFileDescriptor.AutoCloseOutputStream(pfd);
            fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd);
            connected = true;
        }
        return connected;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        IntentFilter intentFilterDetach = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver,intentFilterDetach);

        startForeground(1,new Notification.Builder(getBaseContext())
                        .setContentTitle("Quadrocopter")
                        .setContentText("The Service is runnig")
                        .build()
        );
    }

        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStart");

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "QuadrocopterWakelockTag");
        wakeLock.acquire();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        boolean success = openAccessory();

        if(success){
            startWorkerThreads();
            return START_STICKY; // We want this service to continue running until it is explicitly stopped, so return sticky.
        }else{
            stopSelf();
            return START_NOT_STICKY;
        }

    }

    @Override
    public void onDestroy() {

        stopWorkerThreads();

        wakeLock.release();
        this.stopForeground(true);
        unregisterReceiver(mUsbReceiver);
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

}

