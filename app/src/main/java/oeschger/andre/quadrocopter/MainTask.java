package oeschger.andre.quadrocopter;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
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

    private final boolean CONNECT_TO_ACCESSORY = false;

    private final String serverAddress = "192.168.2.106";
    private final int serverPort = 2500;
    private final int SOCKET_CONNECT_TIMEOUT = 1000; //Milliseconds

    private final int TO_PC_MESSAGE_QUEUE_SIZE = 1000;

    private final int UPDATE_TIME_TO_ARDUINO = 10; //Milliseconds
    private final int UPDATE_TIME_NAVIGATION_SOLVER = 10; //Milliseconds
    private final int UPDATE_TIME_LOG_TO_PC = 100; //Milliseconds

    private final double MILLISECONDS_TO_SECONDS = 1.0/1000.0;

    private final int NUMBER_OF_THREADS = 7;

    private final float ORIENTATION_FILTER_COEFFICIENT = 0.98f;

    private static final String TAG = "QuadrocopterMainTask";

    //-----------------------------------------------------------
    private Socket socket;

    private ParcelFileDescriptor accessoryFileDescriptor;
    private ParcelFileDescriptor.AutoCloseInputStream accessoryInputStream;
    private ParcelFileDescriptor.AutoCloseOutputStream accessoryOutputStream;

    private final ValuesStore valuesStore;

    private final ScheduledThreadPoolExecutor executor;

    private Future<?> comAndroidToPcFuture;
    private Future<?> comPcToAndroidFuture;
    private Future<?> comArduinoToAndroidFuture;
    private Future<?> comAndoidToArduinoFuture;
    private Future<?> sensorEventReceiverFuture;
    private Future<?> navigationSolverFuture;
    private Future<?> logToPcFuture;

    ComAndroidToPc comAndroidToPc;
    ComPcToAndroid comPcToAndroid;

    private final SensorManager sensorManager;
    private final UsbManager usbManager;


    private SensorEventReceiver sensorEventReceiver;
    //private Handler sensorEventHandler;

    public MainTask(UsbManager usbManager, SensorManager sensorManager) {

        this.usbManager = usbManager;
        this.sensorManager = sensorManager;

        socket = new Socket();
        valuesStore = new ValuesStore();

        executor = new ScheduledThreadPoolExecutor(NUMBER_OF_THREADS);
        executor.execute(this);
    }

    public void shutdownNow(){
        executor.shutdownNow();
    }

    private void initCommunicationWithPC() throws IOException {
        Log.d(TAG, "initCommunicationWithPC");

        socket.connect(new InetSocketAddress(serverAddress, serverPort), SOCKET_CONNECT_TIMEOUT);

        comAndroidToPc = new ComAndroidToPc(socket, TO_PC_MESSAGE_QUEUE_SIZE);
        comPcToAndroid = new ComPcToAndroid(socket, valuesStore);

        comAndroidToPcFuture = executor.submit(comAndroidToPc);
        comPcToAndroidFuture = executor.submit(comPcToAndroid);

        logToPcFuture = executor.scheduleAtFixedRate(new LogToPc(valuesStore, comAndroidToPc), 0, UPDATE_TIME_LOG_TO_PC, TimeUnit.MILLISECONDS);
    }

    private void initCommunicationWithArduino(){

        Log.d(TAG, "initCommunicationWithArduino");
        UsbAccessory[] accessoryList = usbManager.getAccessoryList();
        Log.d(TAG,"accessoryList created");

        if (accessoryList != null){
            Log.d(TAG,"printing accessoryList");
            for(UsbAccessory a :accessoryList){
                Log.d(TAG,a.toString());
            }
            Log.d(TAG,"accessories printed");
        } else{
            Log.d(TAG,"accessoryList is null");
        }

        if(accessoryList != null && accessoryList[0] != null) {
            Log.d(TAG,"accessoryList and accessoryList[0] are not null");
            accessoryFileDescriptor = usbManager.openAccessory(accessoryList[0]);
            accessoryOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(accessoryFileDescriptor);
            accessoryInputStream = new ParcelFileDescriptor.AutoCloseInputStream(accessoryFileDescriptor);

        }

        comArduinoToAndroidFuture = executor.submit(new ComArduinoToAndroid(accessoryInputStream, valuesStore));
        comAndoidToArduinoFuture = executor.submit(new ComAndoidToArduino(accessoryOutputStream,valuesStore, UPDATE_TIME_TO_ARDUINO));
    }

    private void initSensorEventReceiver(){
        Log.d(TAG, "initSensorEventReceiver");
        sensorEventReceiver = new SensorEventReceiver("MySensorReceiverThread",valuesStore);
        Log.d(TAG, "sensorEventReceiver created");
        //sensorEventReceiverFuture = executor.submit(sensorEventReceiver);
        //Log.d(TAG, "sensorEventReceiver submitted");

        Sensor sensorAccelerometer= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor sensorAtmosphericPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        Log.d(TAG, "sensors created");
        sensorManager.registerListener(sensorEventReceiver, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventReceiver,sensorMagnetometer,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventReceiver,sensorGyroscope,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventReceiver,sensorAtmosphericPressure,SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(TAG, "listeners registred");
    }

    private void initNavigationSolver(){
        NavigationSolver navigationSolver = new NavigationSolver(valuesStore, ORIENTATION_FILTER_COEFFICIENT, (float) (UPDATE_TIME_NAVIGATION_SOLVER * MILLISECONDS_TO_SECONDS ));
        navigationSolverFuture = executor.scheduleAtFixedRate(navigationSolver, 10000, UPDATE_TIME_NAVIGATION_SOLVER, TimeUnit.MILLISECONDS);
        //navigationSolverFuture = executor.scheduleAtFixedRate(navigationSolver,10,30,TimeUnit.SECONDS);
    }

    @Override
    public void run(){
        initSensorEventReceiver();
        initNavigationSolver();

        while(!Thread.currentThread().isInterrupted()){

            try {
                Thread.sleep(5000);//TODO is this a good time ?
            } catch (InterruptedException e) {
                Log.d(TAG, "ERROR: interrupted");
                break;
            }

            //we are not connected yet
            if(!socket.isConnected()){
                try {
                    initCommunicationWithPC();
                } catch (IOException e) {
                    Log.e(TAG, "Could not initialze communication to pc.", e);
                }
            }


            if(socket.isConnected()){
                //If something is wrong with the communication to the ground station.
                if (comAndroidToPcFuture.isDone() || comPcToAndroidFuture.isDone()) {

                    closeCommunicationWithPc();

                    socket = new Socket();

                }

            }


            if(CONNECT_TO_ACCESSORY){
                initCommunicationWithArduino();

                //If something is wrong with the communication to the arduino.
                if(comArduinoToAndroidFuture.isDone() || comAndoidToArduinoFuture.isDone()){
                    comArduinoToAndroidFuture.cancel(true);
                    comAndoidToArduinoFuture.cancel(true);
                    //TODO put accessory connect stuff here
                    //TODO implement acessory reconnect

                    try {
                        accessoryFileDescriptor.close();
                    } catch (IOException e) {
                        Log.d(TAG, "Could not close Accessory ParcableFileDescriptor", e);
                    }
                }

            }

        }

        closeCommunicationWithPc();

        //TODO think about stopping navigationSolver
        //TODO stop motors no diconnect, maybe send is alive flag with read timeout. Also on arduino.

        sensorManager.unregisterListener(sensorEventReceiver);
        //sensorEventReceiver.getLooper().quit();
        Log.d(TAG, "sensorEventReceiver unregistered and quit Looper");
    }

    private void closeCommunicationWithPc(){
        comAndroidToPcFuture.cancel(true);
        comPcToAndroidFuture.cancel(true);
        logToPcFuture.cancel(true);


        try {
            Log.d(TAG, "Wait until send and receive tasks have finished");
            comAndroidToPc.waitForFinished();
            comPcToAndroid.waitForFinished();
            Log.d(TAG, "Send and receive tasks have finished");
        } catch (InterruptedException e) {
            Log.d(TAG, "Interrupted during wait until send and receive tasks have finished", e);
        }

        try {
            Log.d(TAG, "Closing socket.");
            socket.close();
            Log.d(TAG, "Socket closed.");
        } catch (IOException e) {
            Log.e(TAG, "could not close socket.", e);
        }
    }

}
