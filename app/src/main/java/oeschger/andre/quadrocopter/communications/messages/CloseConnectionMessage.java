package oeschger.andre.quadrocopter.communications.messages;

/**
 * Created by andre on 09.11.15.
 */
public class CloseConnectionMessage implements GroundStationMessage{
    @Override
    public int getMessageType() {
        return GroundStationMessage.CLOSE_CONNECTION_MESSAGE;
    }
}
