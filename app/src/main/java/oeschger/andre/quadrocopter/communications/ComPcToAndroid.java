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

    private ObjectInputStream inputStream;
    private ValuesStore valuesStore;

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
                    case GroundStationMessage.GAMEPADMESSAGE:
                        handleGamepadMessage((GamepadMessage)message);
                        break;
                    case GroundStationMessage.CLOSECONNECTIONMESSAGE:
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
            case GamepadMessage.GAMEPADLEFTXAXIS:
                valuesStore.setGamePadLeftXaxis(message.getValue());
                break;

            case GamepadMessage.GAMEPADLEFTYAXIS:
                valuesStore.setGamePadLeftYaxis(message.getValue());
                break;

            case GamepadMessage.GAMEPADRIGHTXAXIS:
                valuesStore.setGamePadRightXaxis(message.getValue());
                break;

            case GamepadMessage.GAMEPADRIGHTYAXIS:
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
