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
public class MainTask implements Runnable{

    private static final String TAG = "MainTask";

    private String serverAddress = "192.168.2.102";
    private int serverPort = 2500;
    private Socket s;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private FileInputStream accessoryInputStream;
    private FileOutputStream accessoryOutputStream;

    ValuesStore valuesStore;

    ThreadGroup tg;
    Thread t1,t2,t3,t4;


    public MainTask(ThreadGroup tg, FileInputStream fis, FileOutputStream fos) {
        this.accessoryInputStream = fis;
        this.accessoryOutputStream = fos;
        this.tg = tg;
    }

    @Override
    public void run(){
        valuesStore = new ValuesStore();

        try {
            s = new Socket(serverAddress,serverPort);
            inputStream = new ObjectInputStream(s.getInputStream());
            outputStream = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            Log.d(TAG, "ERROR: IO");
        }

        ComAndroidToPc toPc = new ComAndroidToPc(outputStream,valuesStore);

        t1 = new Thread(tg, new ComArduinoToAndroid(accessoryInputStream,valuesStore, toPc));
        t2 = new Thread(tg, new ComAndoidToArduino(accessoryOutputStream,valuesStore));
        t3 = new Thread(tg, new ComPcToAndroid(inputStream, valuesStore));
        t4 = new Thread(tg, toPc);

        System.out.println(t1);
        System.out.println(accessoryInputStream);
        System.out.println(valuesStore);
        System.out.println(tg);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        //TODO close everything
        /*if(s != null){

            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                s = null;
            }
        }*/

    }

}
