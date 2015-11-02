package oeschger.andre.quadrocopter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class hello2 extends Activity {
    public static final boolean D = BuildConfig.DEBUG; // This is automatically set when building
    private static final String TAG = "ArduinoBlinkLEDActivity"; // TAG is used to debug in Android logcat console
    private static final String ACTION_USB_PERMISSION = "oeschger.andre.quadrocopter.action.USB_PERMISSION";

    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    FileOutputStream mOutputStream;
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;
    TextView connectionStatus;
    ConnectedThread mConnectedThread;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory)
                            intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                        openAccessory(accessory);
                    else {
                        if (D)
                            Log.d(TAG, "Permission denied for accessory " + accessory);
                    }
                    mPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory)
                        intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(mAccessory))
                    closeAccessory();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);
    }



    @Override
    public void onResume() {
        super.onResume();

        if (mAccessory != null) {
            setConnectionStatus(true);
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        Log.d(TAG, "mAccessory is " + accessories);
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (mUsbManager.hasPermission(accessory))
                openAccessory(accessory);
            else {
                setConnectionStatus(false);
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        mUsbManager.requestPermission(accessory, mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        } else {
            setConnectionStatus(false);
            if (D)
                Log.d(TAG, "mAccessory is null");
        }
    }

    @Override
    public void onBackPressed() {
        if (mAccessory != null) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Activity")
                    .setMessage("Are you sure you want to close this application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else
            finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeAccessory();
        unregisterReceiver(mUsbReceiver);
    }

    private void openAccessory(UsbAccessory accessory) {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);

            mConnectedThread = new ConnectedThread(this);
            mConnectedThread.start();

            setConnectionStatus(true);

            if (D)
                Log.d(TAG, "Accessory opened");
        } else {
            setConnectionStatus(false);
            if (D)
                Log.d(TAG, "Accessory open failed");
        }
    }

    private void setConnectionStatus(boolean connected) {
        connectionStatus.setText(connected ? "Connected" : "Disconnected");
    }

    private void closeAccessory() {
        setConnectionStatus(false);

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

    public void blinkLED(View v) {
        byte buffer = (byte) 12; // Read button

        if (mOutputStream != null) {
            try {
                mOutputStream.write(buffer);
            } catch (IOException e) {
                if (D)
                    Log.e(TAG, "write failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        Activity activity;
        TextView mTextView;
        byte[] buffer = new byte[1024];
        boolean running;

        ConnectedThread(Activity activity) {
            this.activity = activity;
            mTextView = (TextView) findViewById(R.id.textView);
            running = true;
        }

        public void run() {
            //while (running) {




            String serverAddress = "192.168.2.100";
                    int serverPort = 2500;
                    Socket s;
                    ObjectOutputStream oos;

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

                        while(running){
                            try {
                                Log.d(TAG, "try read");
                                int bytes = mInputStream.read(buffer);
                                Log.d(TAG, "readbuffer: " + ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getLong());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            oos.writeObject(ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getLong());

                            oos.flush();
                        }


                        Log.d(TAG, "Client: ende");

                        oos.close();
                        Log.d(TAG, "Client: stream geschlossen");

                        s.close();
                        Log.d(TAG, "Client: socket geschlossen");

                    } catch (IOException e) {
                        running=false;
                        Log.d(TAG, "ERROR: Socket creation");
                    }


                    /*if (bytes > 3) { // The message is 4 bytes long
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long timer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getLong();
                                mTextView.setText(Long.toString(timer));
                            }
                        });
                    }*/

            //}
        }

        public void cancel() {
            running = false;
        }
    }

}