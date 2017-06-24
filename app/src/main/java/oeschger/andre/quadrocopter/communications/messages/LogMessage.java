package oeschger.andre.quadrocopter.communications.messages;

import oeschger.andre.quadrocopter.util.ValuesStore;

/**
 * Created by andre on 23.01.16.
 */
public class LogMessage implements GroundStationMessage{

    private final short battery;

    private final byte motorFrontLeft;
    private final byte motorBackLeft;
    private final byte motorBackRight;
    private final byte motorFrontRight;

    private final float sensorGyroscopeX;
    private final float sensorGyroscopeY;
    private final float sensorGyroscopeZ;

    private final float sensorAccelerometerX;
    private final float sensorAccelerometerY;
    private final float sensorAccelerometerZ;

    private final float sensorMagnetometerX;
    private final float sensorMagnetometerY;
    private final float sensorMagnetometerZ;

    private final float sensorAtmosphericPressure;

    private final float roll;
    private final float pitch;
    private final float yaw;


    public LogMessage(ValuesStore valuesStore) {

        this.battery = valuesStore.getBattery();

        this.motorFrontLeft = valuesStore.getMotorFrontLeft();
        this.motorBackLeft = valuesStore.getMotorBackLeft();
        this.motorBackRight = valuesStore.getMotorBackRight();
        this.motorFrontRight = valuesStore.getMotorFrontRight();

        this.sensorGyroscopeX = valuesStore.getSensorGyroscopeX();
        this.sensorGyroscopeY = valuesStore.getSensorGyroscopeY();
        this.sensorGyroscopeZ = valuesStore.getSensorGyroscopeZ();

        this.sensorAccelerometerX = valuesStore.getSensorAccelerometerX();
        this.sensorAccelerometerY = valuesStore.getSensorAccelerometerY();
        this.sensorAccelerometerZ = valuesStore.getSensorAccelerometerZ();

        this.sensorMagnetometerX = valuesStore.getSensorMagnetometerX();
        this.sensorMagnetometerY = valuesStore.getSensorMagnetometerY();
        this.sensorMagnetometerZ = valuesStore.getSensorMagnetometerZ();

        this.sensorAtmosphericPressure = valuesStore.getSensorAtmosphericPressure();

        this.roll = valuesStore.getRoll();
        this.pitch = valuesStore.getPitch();
        this.yaw = valuesStore.getYaw();
    }

    @Override
    public int getMessageType() {
        return GroundStationMessage.LOG_MESSAGE;
    }

    public short getBattery(){ return battery;}

    public byte getMotorFrontLeft() {
        return motorFrontLeft;
    }

    public byte getMotorBackLeft() {
        return motorBackLeft;
    }

    public byte getMotorBackRight() {
        return motorBackRight;
    }

    public byte getMotorFrontRight() {
        return motorFrontRight;
    }

    public float getSensorGyroscopeX() {
        return sensorGyroscopeX;
    }

    public float getSensorGyroscopeY() {
        return sensorGyroscopeY;
    }

    public float getSensorGyroscopeZ() {
        return sensorGyroscopeZ;
    }

    public float getSensorAccelerometerX() {
        return sensorAccelerometerX;
    }

    public float getSensorAccelerometerY() {
        return sensorAccelerometerY;
    }

    public float getSensorAccelerometerZ() {
        return sensorAccelerometerZ;
    }

    public float getSensorMagnetometerX() {
        return sensorMagnetometerX;
    }

    public float getSensorMagnetometerY() {
        return sensorMagnetometerY;
    }

    public float getSensorMagnetometerZ() {
        return sensorMagnetometerZ;
    }

    public float getSensorAtmosphericPressure() {
        return sensorAtmosphericPressure;
    }

    public float getRoll() {
        return roll;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }
}
