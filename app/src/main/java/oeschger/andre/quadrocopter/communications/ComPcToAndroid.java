package oeschger.andre.quadrocopter.communications;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

import oeschger.andre.quadrocopter.util.ValuesStore;
import oeschger.andre.quadrocopter.communications.messages.GamepadMessage;
import oeschger.andre.quadrocopter.communications.messages.GroundStationMessage;

/**
 * Created by andre on 03.11.15.
 */
public class ComPcToAndroid implements Runnable{

    private static final String TAG = "ComPcToAndroid";

    private final Socket socket;
    private final ValuesStore valuesStore;


    public ComPcToAndroid(Socket socket, ValuesStore valuesStore) {
        this.socket = socket;
        this.valuesStore = valuesStore;
        this.finished = false;
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

    @Override
    public void run() {

        Log.d(TAG, "started");

        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            while (!Thread.currentThread().isInterrupted()) {

                GroundStationMessage message;

                try {
                    message = (GroundStationMessage) inputStream.readObject();
                } catch (IOException e) {
                    Log.e(TAG, "ERROR: IO in run loop", e);
                    break;
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "ERROR: class not found", e);
                    break;
                }

                switch (message.getMessageType()) {
                    case GroundStationMessage.GAMEPAD_MESSAGE:
                        handleGamepadMessage((GamepadMessage) message);
                        break;
                    case GroundStationMessage.CLOSE_CONNECTION_MESSAGE:
                        handleCloseConnectionMessage();
                        break;
                    default:
                        throw new IllegalArgumentException("GroundStationMessage type: " + message.getMessageType());
                }

            }

        } catch (IOException e) {
            Log.e(TAG, "Could not open input stream.", e);
        }

        finish();
        Log.d(TAG, "ended");
    }

    private void handleGamepadMessage(GamepadMessage message){

        switch (message.getButtonOrAxisName()){
            case GamepadMessage.GAMEPAD_LEFT_X_AXIS:
                valuesStore.setGamePadLeftXaxis(message.getValue());
                break;

            case GamepadMessage.GAMEPAD_LEFT_Y_AXIS:
                valuesStore.setGamePadLeftYaxis(message.getValue());
                break;

            case GamepadMessage.GAMEPAD_RIGHT_X_AXIS:
                valuesStore.setGamePadRightXaxis(message.getValue());
                break;

            case GamepadMessage.GAMEPAD_RIGHT_Y_AXIS:
                valuesStore.setGamePadRightYaxis(message.getValue());
                break;

            default:
                Log.d(TAG, "Unused Key: " + message.getButtonOrAxisName());
        }
    }

    private void handleCloseConnectionMessage() {
        Log.d(TAG, "Received Close Connection Message");
        Thread.currentThread().interrupt();
    }

}
