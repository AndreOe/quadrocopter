package oeschger.andre.quadrocopter;

import java.io.Serializable;

/**
 * Created by andre on 04.11.15.
 */
public class GamepadMessage implements GroundStationMessage, Serializable {

    public static final String GAMEPADLEFTXAXIS = "x";
    public static final String GAMEPADLEFTYAXIS = "y";
    public static final String GAMEPADRIGHTXAXIS = "rx";
    public static final String GAMEPADRIGHTYAXIS = "ry";

    private String buttonOrAxisName;
    private float value;

    public GamepadMessage(String buttonOrAxisName, float value) {
        this.buttonOrAxisName = buttonOrAxisName;
        this.value = value;
    }

    public String getButtonOrAxisName(){
        return buttonOrAxisName;
    }
    public float getValue(){
        return value;
    }

    @Override
    public int getMessageType() {
        return GroundStationMessage.GAMEPADMESSAGE;
    }
}
