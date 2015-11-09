package oeschger.andre.quadrocopter;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by andre on 03.11.15.
 */
public class ComAndroidToPc implements Runnable{

    private static final String TAG = "ComAndroidToPc";

    private ObjectOutputStream outputStream;
    private ValuesStore valuesStore;

    private ArrayBlockingQueue<GroundStationMessage> queue;


    public ComAndroidToPc(ObjectOutputStream outputStream, ValuesStore valuesStore) {
        this.outputStream = outputStream;
        this.valuesStore = valuesStore;
        queue = new ArrayBlockingQueue<GroundStationMessage>(1000);
    }

    public void sentToPc(GroundStationMessage message){
        queue.add(message);
    }

    @Override
    public void run() {


        while(!Thread.currentThread().isInterrupted()){

            try {
                GroundStationMessage message = queue.take();
                outputStream.writeObject(message);
                outputStream.flush();
            }catch (IOException e) {
                Log.d(TAG, "ERROR: IO in run loop");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            outputStream.writeObject(new CloseConnectionMessage());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.d(TAG, "ERROR: IO");
        }

    }





}
