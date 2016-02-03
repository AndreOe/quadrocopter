package oeschger.andre.quadrocopter;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import oeschger.andre.quadrocopter.communications.ComAndoidToArduino;
import oeschger.andre.quadrocopter.communications.ComAndroidToPc;
import oeschger.andre.quadrocopter.communications.ComArduinoToAndroid;
import oeschger.andre.quadrocopter.communications.ComPcToAndroid;
import oeschger.andre.quadrocopter.util.SensorEventReceiver;
import oeschger.andre.quadrocopter.util.ValuesStore;

/**
 * Created by andre on 05.11.15.
 */

/* Maybe important WLAN power optimisation has to be switched off in Android 4.2
 * otherwise jou will experience lag when display is turned off.
 * Switch Android processor scheduler to performance.
 */



public class MainTask implements Runnable{

    // Constants

    private final String serverAddress = "192.168.0.7";
    private final int serverPort = 2500;

    private final int TO_PC_MESSAGE_QUEUE_SIZE = 1000;

    private final int UPDATE_TIME_TO_ARDUINO = 10; //Milliseconds
    private final int UPDATE_TIME_NAVIGATION_SOLVER = 10; //Milliseconds
    private final int UPDATE_TIME_LOG_TO_PC = 10; //Milliseconds

    private final double MILLISECONDS_TO_SECONDS = 1.0/1000.0;

    private final int NUMBEROFTHREADS = 7;

    private final float ORIENTATION_FILTER_COEFFICIENT = 0.98f;

    private static final String TAG = "MainTask";

    //-----------------------------------------------------------
    private Socket s;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private FileInputStream accessoryInputStream;
    private FileOutputStream accessoryOutputStream;

    private ValuesStore valuesStore;

    private ScheduledThreadPoolExecutor executor;

    private ComAndroidToPc comAndroidToPc;

    private Future<?> comAndroidToPcFuture;
    private Future<?> comPcToAndroidFuture;
    private Future<?> comArduinoToAndroidFuture;
    private Future<?> comAndoidToArduinoFuture;
    private Future<?> sensorEventReceiverFuture;
    private Future<?> navigationSolverFuture;
    private Future<?> logToPcFuture;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;
    private Sensor sensorGyroscope;
    private Sensor sensorAtmosphericPressure;

    private SensorEventReceiver sensorEventReceiver;
    //private Handler sensorEventHandler;

    private NavigationSolver navigationSolver;

    private LogToPc logToPc;

    public MainTask(FileInputStream fis, FileOutputStream fos, SensorManager sensorManager) {
        this.accessoryInputStream = fis;
        this.accessoryOutputStream = fos;
        this.sensorManager = sensorManager;
        executor = new ScheduledThreadPoolExecutor(NUMBEROFTHREADS);
        executor.execute(this);
    }

    public void shutdownNow(){
        executor.shutdownNow();
    }

    private void initCommunicationWithPC(){
        try {
            s = new Socket(serverAddress,serverPort);
            inputStream = new ObjectInputStream(s.getInputStream());
            outputStream = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            Log.d(TAG, "ERROR: IO");
        }

        comAndroidToPc = new ComAndroidToPc(outputStream,TO_PC_MESSAGE_QUEUE_SIZE);

        comAndroidToPcFuture = executor.submit(comAndroidToPc);
        comPcToAndroidFuture = executor.submit(new ComPcToAndroid(inputStream, valuesStore));

    }

    private void initCommunicationWithArduino(){

        comArduinoToAndroidFuture = executor.submit(new ComArduinoToAndroid(accessoryInputStream, valuesStore));
        comAndoidToArduinoFuture = executor.submit(new ComAndoidToArduino(accessoryOutputStream,valuesStore, UPDATE_TIME_TO_ARDUINO));
    }

    private void initSensorEventReceiver(){
        Log.d(TAG, "initSensorEventReceiver");
        sensorEventReceiver = new SensorEventReceiver("MySensorReceiverThread",valuesStore);
        Log.d(TAG, "sensorEventReceiver created");
        //sensorEventReceiverFuture = executor.submit(sensorEventReceiver);
        Log.d(TAG, "sensorEventReceiver submitted");

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorAtmosphericPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        Log.d(TAG, "sensors created");
        sensorManager.registerListener(sensorEventReceiver, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventReceiver,sensorMagnetometer,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventReceiver,sensorGyroscope,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventReceiver,sensorAtmosphericPressure,SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(TAG, "listeners registred");
    }

    private void initNavigationSolver(){
        navigationSolver = new NavigationSolver(valuesStore,ORIENTATION_FILTER_COEFFICIENT,(float)(UPDATE_TIME_NAVIGATION_SOLVER*MILLISECONDS_TO_SECONDS));
        navigationSolverFuture = executor.scheduleAtFixedRate(navigationSolver,10000,UPDATE_TIME_NAVIGATION_SOLVER,TimeUnit.MILLISECONDS);
        //navigationSolverFuture = executor.scheduleAtFixedRate(navigationSolver,10,30,TimeUnit.SECONDS);
    }

    private void initLogToPc(){
        logToPc = new LogToPc(valuesStore,comAndroidToPc);
        logToPcFuture = executor.scheduleAtFixedRate(logToPc,0,UPDATE_TIME_LOG_TO_PC,TimeUnit.MILLISECONDS);
    }



    @Override
    public void run(){
        valuesStore = new ValuesStore();

        initCommunicationWithPC();
        initCommunicationWithArduino();
        initSensorEventReceiver();
        initNavigationSolver();
        initLogToPc();

        Log.d(TAG, "Initialized all");

        while(!Thread.currentThread().isInterrupted()){

            try {
                Thread.sleep(5000);//TODO is this a good time ?
            } catch (InterruptedException e) {
                Log.d(TAG, "ERROR: interrupted");
                break;
            }

            if(comAndroidToPcFuture.isDone() || comPcToAndroidFuture.isDone()){

                comAndroidToPcFuture.cancel(true);
                comPcToAndroidFuture.cancel(true);

                if(s != null){

                    try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        s = null;
                    }
                }
                initCommunicationWithPC();
                initLogToPc();
            }

            if(comArduinoToAndroidFuture.isDone() || comAndoidToArduinoFuture.isDone()){
                comArduinoToAndroidFuture.cancel(true);
                comAndoidToArduinoFuture.cancel(true);
                //TODO put accessory connect stuff here
                //TODO implement acessory reconnect
            }

        }

        //TODO think about stopping navigationSolver
        //TODO stop motors no diconnect, maybe send is alive flag with read timeout. Also on arduino.
        //TODO stop logToPC

        sensorManager.unregisterListener(sensorEventReceiver);
        //sensorEventReceiver.getLooper().quit();
        Log.d(TAG, "sensorEventReceiver unregistered and quit Looper");
    }

}
