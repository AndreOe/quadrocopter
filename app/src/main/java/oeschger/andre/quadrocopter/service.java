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
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;


//import com.android.future.usb.UsbAccessory;
//import com.android.future.usb.UsbManager;



/**
 * Created by andre on 28.10.15.
 */
public class service extends Service
{
    private static final String TAG = "MyService";

    BroadcastReceiver mUsbReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        IntentFilter intentFilterDetach = new IntentFilter();
        intentFilterDetach.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");

        mUsbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                    UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (accessory != null) {

                    }
                    stopSelf();
                }
            }
        };

        registerReceiver(mUsbReceiver,intentFilterDetach);
    }

        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startForeground(1,new Notification.Builder(getBaseContext())
                        .setContentTitle("Quadrocopter")
                        .setContentText("The Service is runnig")
                        .build()
        );

        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");


            String serverAddress = "192.168.2.100";
            int serverPort = 2500;
            Socket s;
            ObjectOutputStream oos;
            UsbManager mUSBManager;
            FileInputStream mIS;
            FileOutputStream mOS;

            byte[] byteToArduino = new byte[1];
            byteToArduino[0] = 12;
            byte[] byteFromArduino = new byte[1];
            byteFromArduino[0] =0;


            mUSBManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            Log.d(TAG, "USBMANAGER is :" + mUSBManager);

            Log.d(TAG, "ACEESORIES are :" + mUSBManager.getAccessoryList());

            Log.d(TAG, "DEVICES are :" + mUSBManager.getDeviceList());

            UsbAccessory acc = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);


                ParcelFileDescriptor mFD = mUSBManager.openAccessory(acc);
                if (mFD != null) {
                    FileDescriptor fd = mFD.getFileDescriptor();
                    mIS = new FileInputStream(fd);  // use this to receive messages
                    mOS = new FileOutputStream(fd); // use this to send commands
                    try {
                        mOS.write(byteToArduino);
                        mOS.flush();
                        mIS.read(byteFromArduino);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }




                try {

                    Log.d(TAG, "Client: start");

                    s=new Socket(serverAddress,2500);
                    Log.d(TAG, "Client: verbunden");

                    oos = new ObjectOutputStream(s.getOutputStream());
                    Log.d(TAG, "Client: stream geöffnet");

                    oos.writeObject("Hallo");

                    oos.writeObject("Du");

                    oos.flush();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //oos.writeObject("Ich bin Android");byteFromArduino
                    oos.writeObject(byteFromArduino[0]);

                    oos.flush();

                    Log.d(TAG, "Client: ende");

                    oos.close();
                    Log.d(TAG, "Client: stream geschlossen");

                    s.close();
                    Log.d(TAG, "Client: socket geschlossen");

                } catch (IOException e) {
                    Log.d(TAG, "ERROR: Socket creation");
                }


            /*


       new Thread(){

            String serverAddress = "192.168.2.100";
            int serverPort = 2500;
            Socket s;
            ObjectOutputStream oos;
            UsbManager mUSBManager;
            FileInputStream mIS;
            FileOutputStream mOS;


            @Override
            public void run(){

                mUSBManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                UsbAccessory acc = (UsbAccessory)mUSBManager.getAccessoryList()[0];

                ParcelFileDescriptor mFD = mUSBManager.openAccessory(acc);
                if (mFD != null) {
                    FileDescriptor fd = mFD.getFileDescriptor();
                    mIS = new FileInputStream(fd);  // use this to receive messages
                    mOS = new FileOutputStream(fd); // use this to send commands
                }

                byte[] byteToArduino = new byte[1];
                byteToArduino[0] = 12;
                byte[] byteFromArduino = new byte[1];
                byteFromArduino[0] =0;

                try {
                    mOS.write(byteToArduino);
                    mOS.flush();
                    mIS.read(byteFromArduino);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    Log.d(TAG, "Client: start");

                    s=new Socket(serverAddress,2500);
                    Log.d(TAG, "Client: verbunden");

                    oos = new ObjectOutputStream(s.getOutputStream());
                    Log.d(TAG, "Client: stream geöffnet");

                    oos.writeObject("Hallo");

                    oos.writeObject("Du");

                    oos.flush();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //oos.writeObject("Ich bin Android");byteFromArduino
                    oos.writeObject(byteFromArduino[0]);

                    oos.flush();

                    Log.d(TAG, "Client: ende");

                    oos.close();
                    Log.d(TAG, "Client: stream geschlossen");

                    s.close();
                    Log.d(TAG, "Client: socket geschlossen");

                } catch (IOException e) {
                    Log.d(TAG, "ERROR: Socket creation");
                }


            }
        }.start();*/

        return START_STICKY; // We want this service to continue running until it is explicitly stopped, so return sticky.
    }

    public void onDestroy() {
        this.stopForeground(true);
        unregisterReceiver(mUsbReceiver);
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }


}

