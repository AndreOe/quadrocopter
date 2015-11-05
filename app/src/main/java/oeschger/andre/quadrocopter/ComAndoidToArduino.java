package oeschger.andre.quadrocopter;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by andre on 03.11.15.
 */
public class ComAndoidToArduino implements Runnable{

    private static final String TAG = "ComAndoidToArduino";

    private FileOutputStream outputStream;
    private ValuesStore valuesStore;
    private byte[] writeBuffer = new byte[4];

    public ComAndoidToArduino(FileOutputStream outputStream, ValuesStore valuesStore) {
        this.outputStream = outputStream;
        this.valuesStore = valuesStore;
    }

    @Override
    public void run() {



        while(!Thread.currentThread().isInterrupted()){

            /*
            writeBuffer[0] = valuesStore.getMotorFrontLeft(); // pin4
            writeBuffer[1] = valuesStore.getMotorBackLeft(); // pin7
            writeBuffer[2] = valuesStore.getMotorBackRight(); // pin8
            writeBuffer[3] = valuesStore.getMotorFrontRight(); // pin12
            */

            writeBuffer[0] = map(valuesStore.getGamePadLeftXaxis(),-1.0,1.0,0.0,255.0); // pin4
            writeBuffer[1] = map(valuesStore.getGamePadLeftYaxis(),-1.0,1.0,0.0,255.0); // pin7
            writeBuffer[2] = map(valuesStore.getGamePadRightXaxis(),-1.0,1.0,0.0,255.0); // pin8
            writeBuffer[3] = map(valuesStore.getGamePadRightYaxis(),-1.0,1.0,0.0,255.0); // pin12

            //Log.d(TAG, "try write");
            try {
                outputStream.write(writeBuffer);
                outputStream.flush();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                Log.d(TAG, "ERROR: IO");
            }
            //Log.d(TAG, "written");

            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


    }

    private byte map(double x, double in_min, double in_max, double out_min, double out_max) {

        byte myByte = (byte)((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);

        //Log.d(TAG, "transformed " + x + " to " + myByte);

        return myByte;
    }


}
