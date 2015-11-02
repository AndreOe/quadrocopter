package oeschger.andre.quadrocopter;

import android.app.Notification;
import android.app.PendingIntent;
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;



/**
 * Created by andre on 28.10.15.
 */
public class service extends Service
{
    private static final String TAG = "MyService";

    private String serverAddress = "192.168.2.100";
    private int serverPort = 2500;
    private Socket s;
    private ObjectOutputStream oos;


    private UsbAccessory mAccessory;
    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;
    private UsbManager mUsbManager;

    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;

    //byte[] byteToArduino = new byte[4];
    //byte[] byteFromArduino = new byte[2];

    ConnectedThread mConnectedThread;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null) {
                    closeAccessory();
                }
                stopSelf();
            }
        }
    };


    private void openAccessory() {

        Log.d(TAG, "openAccessory: " + mAccessory);
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mFileDescriptor = mUsbManager.openAccessory(mAccessory);

        if (mFileDescriptor != null) {
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);

            mConnectedThread = new ConnectedThread();
            mConnectedThread.start();
            Log.d(TAG, "Accessory opened");
        }
        else {
            Log.d(TAG, "Accessory open failed");
        }

    }

    private void closeAccessory() {

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Close all streams
        try {
            if (mInputStream != null)
                mInputStream.close();
        } catch (Exception ignored) {
        } finally {
            mInputStream = null;
        }
        try {
            if (mOutputStream != null)
                mOutputStream.close();
        } catch (Exception ignored) {
        } finally {
            mOutputStream = null;
        }
        try {
            if (mFileDescriptor != null)
                mFileDescriptor.close();
        } catch (IOException ignored) {
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
        }

        return START_STICKY; // We want this service to continue running until it is explicitly stopped, so return sticky.
    }

    public void onDestroy() {
        this.stopForeground(true);
        unregisterReceiver(mUsbReceiver);
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }


    private class ConnectedThread extends Thread {
        byte[] readBuffer = new byte[2];
        byte[] writeBuffer = new byte[4];

        boolean running;

        ConnectedThread() {
            running = true;
        }

        public void run() {
            try {

                Log.d(TAG, "Client: start");

                s=new Socket(serverAddress,2500);
                Log.d(TAG, "Client: verbunden");

                oos = new ObjectOutputStream(s.getOutputStream());
                Log.d(TAG, "Client: stream geöffnet");

                oos.writeObject("Hallo");

                oos.writeObject("Du");

                oos.flush();

                //oos.writeObject("Ich bin Android");byteFromArduino

                writeBuffer[0] = (byte)0xFF; // pin4
                writeBuffer[1] = (byte)0x0F; // pin7
                writeBuffer[2] = (byte)0x00; // pin8
                writeBuffer[3] = (byte)0xF0; // pin12

                while(running){

                    Log.d(TAG, "try write");
                    mOutputStream.write(writeBuffer);
                    Log.d(TAG, "written");
                    mOutputStream.flush();

                    Log.d(TAG, "try read");
                    int len = mInputStream.read(readBuffer);
                    Log.d(TAG, "read no of bytes: " + len);
                    Log.d(TAG, "readbuffer: " + ByteBuffer.wrap(readBuffer).order(ByteOrder.LITTLE_ENDIAN).getShort());

                    oos.writeObject(ByteBuffer.wrap(readBuffer).order(ByteOrder.LITTLE_ENDIAN).getShort());
                    oos.flush();
                }


                Log.d(TAG, "Client: ende");

                oos.close();
                Log.d(TAG, "Client: stream geschlossen");

                s.close();
                Log.d(TAG, "Client: socket geschlossen");

            } catch (IOException e) {
                running=false;
                Log.d(TAG, "ERROR: IO");
            }
        }

        public void cancel() {
            running = false;
        }
    }

}

