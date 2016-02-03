package oeschger.andre.quadrocopter.communications;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import oeschger.andre.quadrocopter.util.ValuesStore;

/**
 * Created by andre on 03.11.15.
 */
public class ComAndoidToArduino implements Runnable{

    private static final String TAG = "ComAndoidToArduino";

    private FileOutputStream outputStream;
    private ValuesStore valuesStore;
    private byte[] writeBuffer = new byte[4];
    private int updateTime;

    public ComAndoidToArduino(FileOutputStream outputStream, ValuesStore valuesStore, int updateTime) {
        this.outputStream = outputStream;
        this.valuesStore = valuesStore;
        this.updateTime = updateTime;
    }

    @Override
    public void run() {

        Log.d(TAG, "started");

        while(!Thread.currentThread().isInterrupted()){

            /* TODO change this back
            writeBuffer[0] = valuesStore.getMotorFrontLeft(); // pin4
            writeBuffer[1] = valuesStore.getMotorBackLeft(); // pin7
            writeBuffer[2] = valuesStore.getMotorBackRight(); // pin8
            writeBuffer[3] = valuesStore.getMotorFrontRight(); // pin12
            */

            writeBuffer[0] = map(valuesStore.getGamePadLeftXaxis(),-1.0,1.0,0.0,255.0); // pin4
            writeBuffer[1] = map(valuesStore.getGamePadLeftYaxis(),-1.0,1.0,0.0,255.0); // pin7
            writeBuffer[2] = map(valuesStore.getGamePadRightXaxis(),-1.0,1.0,0.0,255.0); // pin8
            writeBuffer[3] = map(valuesStore.getGamePadRightYaxis(),-1.0,1.0,0.0,255.0); // pin12

            try {
                outputStream.write(writeBuffer);
                outputStream.flush();
            } catch (IOException e) {
                Log.d(TAG, "ERROR: IO in run loop");
                break;
            }

            try {
                Thread.currentThread().sleep(updateTime); //TODO is 10 ms ok ?
            } catch (InterruptedException e) {
                Log.d(TAG, "ERROR: interrupted");
                break;
            }


        }

        try {
            outputStream.close();
        } catch (IOException e) {
            Log.d(TAG, "ERROR: close outputStream");
        }

        Log.d(TAG, "ended");


    }

    private byte map(double x, double in_min, double in_max, double out_min, double out_max) {

        byte myByte = (byte)((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);

        //Log.d(TAG, "transformed " + x + " to " + myByte);

        return myByte;
    }


}
