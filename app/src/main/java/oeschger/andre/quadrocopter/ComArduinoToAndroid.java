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

    public ComArduinoToAndroid(FileInputStream inputStream, ValuesStore valuesStore) {
        this.inputStream = inputStream;
        this.valuesStore = valuesStore;
    }

    @Override
    public void run() {

        int len = 0;

        while(!Thread.currentThread().isInterrupted()){
            Log.d(TAG, "try read");
            try {

                len = inputStream.read(readBuffer);
                valuesStore.setBattery(ByteBuffer.wrap(readBuffer).order(ByteOrder.LITTLE_ENDIAN).getShort());

            } catch (IOException e) {
                Thread.currentThread().interrupt();
                Log.d(TAG, "ERROR: IO");
            }


            Log.d(TAG, "read no of bytes: " + len);
            Log.d(TAG, "readbuffer: " + ByteBuffer.wrap(readBuffer).order(ByteOrder.LITTLE_ENDIAN).getShort());
        }

    }


}
