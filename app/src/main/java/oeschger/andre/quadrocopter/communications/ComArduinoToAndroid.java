package oeschger.andre.quadrocopter.communications;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import oeschger.andre.quadrocopter.util.ValuesStore;

/**
 * Created by andre on 03.11.15.
 */
public class ComArduinoToAndroid implements Runnable{

    private static final String TAG = "ComArduinoToAndroid";

    private FileInputStream inputStream;
    private ValuesStore valuesStore;
    private byte[] readBuffer = new byte[2];

    public ComArduinoToAndroid(FileInputStream inputStream, ValuesStore valuesStore) {
        this.inputStream = inputStream;
        this.valuesStore = valuesStore;
    }

    @Override
    public void run() {

        Log.d(TAG, "started");

        int len = 0;

        while(!Thread.currentThread().isInterrupted()){

            try {

                len = inputStream.read(readBuffer);
                valuesStore.setBattery(ByteBuffer.wrap(readBuffer).order(ByteOrder.LITTLE_ENDIAN).getShort());
                //toPc.sentToPc(new BatteryStatusMessage(valuesStore.getBattery()));
                //TODO better add LogClass

            } catch (IOException e) {
                Log.d(TAG, "ERROR: IO");
                break;
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            Log.d(TAG, "ERROR: IO close inputStream");
        }

        Log.d(TAG, "ended");

    }
}
