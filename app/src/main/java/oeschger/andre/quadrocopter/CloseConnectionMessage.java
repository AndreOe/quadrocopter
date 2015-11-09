package oeschger.andre.quadrocopter;

import java.io.Serializable;

/**
 * Created by andre on 09.11.15.
 */
public class CloseConnectionMessage implements GroundStationMessage, Serializable{
    @Override
    public int getMessageType() {
        return GroundStationMessage.CLOSECONNECTIONMESSAGE;
    }
}
