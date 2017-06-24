package oeschger.andre.quadrocopter.communications;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;

import oeschger.andre.quadrocopter.util.ValuesStore;
import oeschger.andre.quadrocopter.communications.messages.GamepadMessage;
import oeschger.andre.quadrocopter.communications.messages.GroundStationMessage;

/**
 * Created by andre on 03.11.15.
 */
public class ComPcToAndroid implements Runnable{

    private static final String TAG = "ComPcToAndroid";

    private final ObjectInputStream inputStream;
    private final ValuesStore valuesStore;

    public ComPcToAndroid(ObjectInputStream inputStream, ValuesStore valuesStore) {
        this.inputStream = inputStream;
        this.valuesStore = valuesStore;
    }


    @Override
    public void run() {

        Log.d(TAG, "started");

        while(!Thread.currentThread().isInterrupted()){

            try {

                GroundStationMessage message = (GroundStationMessage) inputStream.readObject();

                switch (message.getMessageType()){
                    case GroundStationMessage.GAMEPAD_MESSAGE:
                        handleGamepadMessage((GamepadMessage)message);
                        break;
                    case GroundStationMessage.CLOSE_CONNECTION_MESSAGE:
                        handleCloseConnectionMessage();
                        break;
                    default:
                        throw new IllegalArgumentException("GroundStationMessage type: " + message.getMessageType());
                }

            } catch (IOException e) {
                Log.d(TAG, "ERROR: IO in run loop");
                break;
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "ERROR: class not found");
                break;
            }

        }

        try {
            inputStream.close();
        } catch (IOException e) {
            Log.d(TAG, "ERROR: close inputStream");
        }

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
                Log.d(TAG, "Unused Key: "+message.getButtonOrAxisName());
        }
    }

    private void handleCloseConnectionMessage(){
        Thread.currentThread().interrupt();
    }




}
