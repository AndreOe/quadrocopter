package oeschger.andre.quadrocopter;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by andre on 03.11.15.
 */
public class ComAndroidToPc implements Runnable{

    private static final String TAG = "ComAndroidToPc";

    private ObjectOutputStream outputStream;
    private ValuesStore valuesStore;

    public ComAndroidToPc(ObjectOutputStream outputStream, ValuesStore valuesStore) {
        this.outputStream = outputStream;
        this.valuesStore = valuesStore;
    }

    @Override
    public void run() {

        while(!Thread.currentThread().isInterrupted()){

            try {
                outputStream.writeObject(valuesStore.getBattery());
                outputStream.flush();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                Log.d(TAG, "ERROR: IO");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }





}
