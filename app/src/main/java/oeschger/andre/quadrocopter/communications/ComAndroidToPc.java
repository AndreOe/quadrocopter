package oeschger.andre.quadrocopter.communications;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;

import oeschger.andre.quadrocopter.communications.messages.CloseConnectionMessage;
import oeschger.andre.quadrocopter.communications.messages.GroundStationMessage;

/**
 * Created by andre on 03.11.15.
 */
public class ComAndroidToPc implements Runnable{

    private static final String TAG = "ComAndroidToPc";

    private ObjectOutputStream outputStream;
    private ArrayBlockingQueue<GroundStationMessage> queue;


    public ComAndroidToPc(ObjectOutputStream outputStream, int queueSize) {
        this.outputStream = outputStream;
        queue = new ArrayBlockingQueue<GroundStationMessage>(queueSize);
    }

    public void sentToPc(GroundStationMessage message){
        queue.add(message);
    }

    @Override
    public void run() {

        Log.d(TAG, "started");

        while(!Thread.currentThread().isInterrupted()){

            try {
                GroundStationMessage message = queue.take();
                outputStream.writeObject(message);
                outputStream.flush();
                //Log.d(TAG, "sent mesage To pc");
            }catch (IOException e) {
                Log.d(TAG, "ERROR: IO in run loop");
                break;
            }catch (InterruptedException e) {
                Log.d(TAG, "ERROR: interrupted");
                break;
            }
        }

        try {
            outputStream.writeObject(new CloseConnectionMessage());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.d(TAG, "ERROR: IO send close message");
        }

        Log.d(TAG, "ended");
    }
}
