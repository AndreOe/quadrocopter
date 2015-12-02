package oeschger.andre.quadrocopter;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by andre on 03.11.15.
 */
public class ComArduinoToAndroid implements Runnable{

    private static final String TAG = "ComArduinoToAndroid";

    private FileInputStream inputStream;
    private ValuesStore valuesStore;
    private byte[] readBuffer = new byte[2];
    ComAndroidToPc toPc;

    public ComArduinoToAndroid(FileInputStream inputStream, ValuesStore valuesStore, ComAndroidToPc toPc) {
        this.inputStream = inputStream;
        this.valuesStore = valuesStore;
        this.toPc = toPc;
    }

    @Override
    public void run() {

        int len = 0;

        while(!Thread.currentThread().isInterrupted()){
            //Log.d(TAG, "try read");
            try {

                len = inputStream.read(readBuffer);
                valuesStore.setBattery(ByteBuffer.wrap(readBuffer).order(ByteOrder.LITTLE_ENDIAN).getShort());
                toPc.sentToPc(new BatteryStatusMessage(valuesStore.getBattery()));
                //TODO better add LogClass

            } catch (IOException e) {
                Thread.currentThread().interrupt();
                Log.d(TAG, "ERROR ComArduinoToAndroid: IO");
            }


           // Log.d(TAG, "read no of bytes: " + len);
            //Log.d(TAG, "readbuffer: " + ByteBuffer.wrap(readBuffer).order(ByteOrder.LITTLE_ENDIAN).getShort());
        }

        Log.d(TAG, "ComArduinoToAndroid ended");

    }


}
