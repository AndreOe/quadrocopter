package oeschger.andre.quadrocopter;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by andre on 05.11.15.
 */

/* Important WLAN power optimisation has to be switched off in Android 4.2
 * otherwise jou will experience lag when display is turned off.
 */



public class MainTask implements Runnable{

    private static final String TAG = "MainTask";

    private String serverAddress = "192.168.2.107";
    private int serverPort = 2500;
    private Socket s;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private FileInputStream accessoryInputStream;
    private FileOutputStream accessoryOutputStream;

    ValuesStore valuesStore;

    ComAndroidToPc comAndroidtoPc ;
    ComArduinoToAndroid comArduinoToAndroid;
    ComAndoidToArduino comAndoidToArduino;
    ComPcToAndroid comPcToAndroid;

    ThreadGroup tg;
    Thread t1,t2,t3,t4;


    public MainTask(ThreadGroup tg, FileInputStream fis, FileOutputStream fos) {
        this.accessoryInputStream = fis;
        this.accessoryOutputStream = fos;
        this.tg = tg;
    }

    private void initCommunicationToPC(){
        try {
            s = new Socket(serverAddress,serverPort);
            inputStream = new ObjectInputStream(s.getInputStream());
            outputStream = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            Log.d(TAG, "ERROR: IO");
        }

        comAndroidtoPc = new ComAndroidToPc(outputStream,valuesStore);
        comPcToAndroid = new ComPcToAndroid(inputStream, valuesStore);

        t1 = new Thread(tg, comAndroidtoPc);
        t2 = new Thread(tg, comPcToAndroid);

        t1.start();
        t2.start();
    }

    private void initCommunicationToArduino(){
        comArduinoToAndroid = new ComArduinoToAndroid(accessoryInputStream,valuesStore, comAndroidtoPc);
        comAndoidToArduino = new ComAndoidToArduino(accessoryOutputStream,valuesStore);

        t3 = new Thread(tg, comArduinoToAndroid);
        t4 = new Thread(tg, comAndoidToArduino);

        t3.start();
        t4.start();
    }

    @Override
    public void run(){
        valuesStore = new ValuesStore();



        initCommunicationToPC();
        initCommunicationToArduino();

        while(!Thread.currentThread().isInterrupted()){

            try {
                Thread.sleep(500);//TODO is this a good time ?
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if(!t1.isAlive() || !t2.isAlive()){
                t1.interrupt();
                t2.interrupt();
                if(s != null){

                    try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        s = null;
                    }
                }
                initCommunicationToPC();
            }

            if(!t3.isAlive() || !t4.isAlive()){
                t1.interrupt();
                t2.interrupt();
                //TODO implement acessory reconnect
            }

        }



    }

}
