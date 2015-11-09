package oeschger.andre.quadrocopter;

/**
 * Created by andre on 04.11.15.
 */
public interface GroundStationMessage {

    public final int GAMEPADMESSAGE = 1;
    public final int CLOSECONNECTIONMESSAGE = 2;
    public final int BATTERYSTATUSMESSAGE = 3;

    public int getMessageType();

}
