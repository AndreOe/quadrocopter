package oeschger.andre.quadrocopter;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;

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
        while(!Thread.currentThread().isInterrupted()){

            try {

                GroundStationMessage message = (GroundStationMessage) inputStream.readObject();

                //Log.d(TAG, "Received Messagetype: " +message.getMessageType());

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
                Thread.currentThread().interrupt();
                Log.d(TAG, "ERROR ComPcToAndroid: IO");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "ComPcToAndroid ended");

    }

    private void handleGamepadMessage(GamepadMessage message){
        //Log.d(TAG, "Received: " + message.getButtonOrAxisName()+" is " + message.getValue());

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
