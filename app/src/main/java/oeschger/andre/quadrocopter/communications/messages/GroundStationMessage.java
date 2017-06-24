package oeschger.andre.quadrocopter.communications.messages;

import java.io.Serializable;

/**
 * Created by andre on 04.11.15.
 */
public interface GroundStationMessage extends Serializable{

    public final int GAMEPAD_MESSAGE = 1;
    public final int CLOSE_CONNECTION_MESSAGE = 2;
    public final int BATTERY_STATUS_MESSAGE = 3;
    public final int LOG_MESSAGE = 4;

    public int getMessageType();
}
