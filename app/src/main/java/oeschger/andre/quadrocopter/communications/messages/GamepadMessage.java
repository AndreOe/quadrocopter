package oeschger.andre.quadrocopter.communications.messages;

/**
 * Created by andre on 04.11.15.
 */
public class GamepadMessage implements GroundStationMessage{

    public static final String GAMEPAD_LEFT_X_AXIS = "x";
    public static final String GAMEPAD_LEFT_Y_AXIS = "y";
    public static final String GAMEPAD_RIGHT_X_AXIS = "rx";
    public static final String GAMEPAD_RIGHT_Y_AXIS = "ry";

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
        return GAMEPAD_MESSAGE;
    }
}
