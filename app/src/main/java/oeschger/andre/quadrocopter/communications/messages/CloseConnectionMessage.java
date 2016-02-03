package oeschger.andre.quadrocopter.communications.messages;

import java.io.Serializable;

/**
 * Created by andre on 09.11.15.
 */
public class CloseConnectionMessage implements GroundStationMessage{
    @Override
    public int getMessageType() {
        return GroundStationMessage.CLOSECONNECTIONMESSAGE;
    }
}
