package oeschger.andre.quadrocopter;

import java.io.Serializable;

/**
 * Created by andre on 09.11.15.
 */
public class BatteryStatusMessage implements GroundStationMessage, Serializable {

    private short value;

    public BatteryStatusMessage(short value) {
        this.value = value;
    }

    public float getValue(){
        return value;
    }

    @Override
    public int getMessageType() {
        return GroundStationMessage.BATTERYSTATUSMESSAGE;
    }
}
