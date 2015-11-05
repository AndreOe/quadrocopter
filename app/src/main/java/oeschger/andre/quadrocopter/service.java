package oeschger.andre.quadrocopter;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


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

    ThreadGroup tg;
    Thread myMainThread;

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

        Log.d(TAG, "Starting Threads");

        tg = new ThreadGroup("My ThreadGroup");

        myMainThread = new Thread(tg, new MainTask(tg,fis,fos));
        myMainThread.start();
        Log.d(TAG, "Threads started");


    }

    private void stopWorkerThreads(){
        // Cancel any thread currently running a connection
        if (tg != null) {
            tg.interrupt();
            tg = null;
        }
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

        mAccessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

        if (mAccessory != null) {
            openAccessory();
            startWorkerThreads();
        }

        return START_STICKY; // We want this service to continue running until it is explicitly stopped, so return sticky.
    }

    public void onDestroy() {

        stopWorkerThreads();
        closeAccessory();

        this.stopForeground(true);
        unregisterReceiver(mUsbReceiver);
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

}

