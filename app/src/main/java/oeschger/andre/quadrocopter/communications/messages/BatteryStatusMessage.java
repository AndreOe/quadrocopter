package oeschger.andre.quadrocopter.communications.messages;

/**
 * Created by andre on 09.11.15.
 */
public class BatteryStatusMessage implements GroundStationMessage{

    private short value;

    public BatteryStatusMessage(short value) {
        this.value = value;
    }

    public short getValue(){
        return value;
    }

    @Override
    public int getMessageType() {
        return GroundStationMessage.BATTERY_STATUS_MESSAGE;
    }
}
