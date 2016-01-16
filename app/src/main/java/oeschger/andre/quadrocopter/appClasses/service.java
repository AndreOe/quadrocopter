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
    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream fis;
    private FileOutputStream fos;

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
        myMainTask.shutdownNow();
    }


    private void openAccessory() {

        Log.d(TAG, "openAccessory: " + mAccessory);
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mFileDescriptor = mUsbManager.openAccessory(mAccessory);

        if (mFileDescriptor != null) {
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            fis = new FileInputStream(fd);
            fos = new FileOutputStream(fd);
            Log.d(TAG, "Accessory opened");
        }
        else {
            Log.d(TAG, "Accessory open failed");
        }

    }

    private void closeAccessory() {
        // Close all streams
        try {
            if (fis != null)
                fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fis = null;
        }
        try {
            if (fos != null)
                fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fos = null;
        }
        try {
            if (mFileDescriptor != null)
                mFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
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

        mAccessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

        if (mAccessory != null) {
            openAccessory();
            startWorkerThreads();
        }

        return START_STICKY; // We want this service to continue running until it is explicitly stopped, so return sticky.
    }

    @Override
    public void onDestroy() {

        stopWorkerThreads();
        closeAccessory();

        wakeLock.release();
        this.stopForeground(true);
        unregisterReceiver(mUsbReceiver);
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

}

