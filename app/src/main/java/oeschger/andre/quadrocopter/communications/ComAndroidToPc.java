package oeschger.andre.quadrocopter.communications;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import oeschger.andre.quadrocopter.communications.messages.CloseConnectionMessage;
import oeschger.andre.quadrocopter.communications.messages.GroundStationMessage;

/**
 * Created by andre on 03.11.15.
 */
public class ComAndroidToPc implements Runnable{

    private static final String TAG = "ComAndroidToPc";

    private Socket socket;
    private ArrayBlockingQueue<GroundStationMessage> queue;


    public ComAndroidToPc(Socket socket, int queueSize) {
        this.socket = socket;
        queue = new ArrayBlockingQueue<>(queueSize);
    }

    private boolean finished;

    public synchronized void waitForFinished() throws InterruptedException {
        if(!finished){
            wait();
        }
    }

    private synchronized void finish(){
        finished = true;
        notify();
    }

    public void sentToPc(GroundStationMessage message){
        queue.add(message);
    }

    @Override
    public void run() {

        Log.d(TAG, "started");

        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    GroundStationMessage message = queue.take();
                    outputStream.writeObject(message);
                    outputStream.flush();
                    //Log.d(TAG, "sent mesage To pc");
                } catch (IOException e) {
                    Log.e(TAG, "Could not send onject.", e);
                    break;
                } catch (InterruptedException e) {

                    try {
                        outputStream.writeObject(new CloseConnectionMessage());
                        outputStream.flush();

                    } catch (IOException e1) {
                        Log.e(TAG, "Could not send close message.", e1);
                    }

                    Log.d(TAG, "ComAndroidToPc was interrupted.");
                    break;
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "Could not open output stream.", e);
        }

        finish();
        Log.d(TAG, "ended");
    }
}
