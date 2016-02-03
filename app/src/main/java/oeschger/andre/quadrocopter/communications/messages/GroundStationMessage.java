package oeschger.andre.quadrocopter.communications.messages;

import java.io.Serializable;

/**
 * Created by andre on 04.11.15.
 */
public interface GroundStationMessage extends Serializable{

    public final int GAMEPADMESSAGE = 1;
    public final int CLOSECONNECTIONMESSAGE = 2;
    public final int BATTERYSTATUSMESSAGE = 3;
    public final int LOGMESSAGE = 4;

    public int getMessageType();

}
