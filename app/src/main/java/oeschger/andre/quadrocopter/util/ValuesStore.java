package oeschger.andre.quadrocopter.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by andre on 03.11.15.
 */
public class ValuesStore {

    private AtomicInteger battery = new AtomicInteger(); //value from 0 to 1023 TODO not mapped yet, what is 0% battery ?

    //TODO check battery linearity
    private AtomicInteger motorFrontLeft = new AtomicInteger(); //value from 0 to 255 that results in 0 to 100 % motor speed
    private AtomicInteger motorBackLeft = new AtomicInteger();  //value from 0 to 255 that results in 0 to 100 % motor speed
    private AtomicInteger motorBackRight = new AtomicInteger(); //value from 0 to 255 that results in 0 to 100 % motor speed
    private AtomicInteger motorFrontRight = new AtomicInteger(); //value from 0 to 255 that results in 0 to 100 % motor speed

    private AtomicFloat gamePadLeftXaxis = new AtomicFloat(); //value from 0.0 to 1.0
    private AtomicFloat gamePadLeftYaxis = new AtomicFloat(); //value from 0.0 to 1.0
    private AtomicFloat gamePadRightXaxis = new AtomicFloat(); //value from 0.0 to 1.0
    private AtomicFloat gamePadRightYaxis = new AtomicFloat(); //value from 0.0 to 1.0

    private AtomicFloat sensorGyroscopeX = new AtomicFloat();
    private AtomicFloat sensorGyroscopeY = new AtomicFloat();
    private AtomicFloat sensorGyroscopeZ = new AtomicFloat();

    private AtomicFloat sensorAccelerometerX = new AtomicFloat();
    private AtomicFloat sensorAccelerometerY = new AtomicFloat();
    private AtomicFloat sensorAccelerometerZ = new AtomicFloat();

    private AtomicFloat sensorMagnetometerX = new AtomicFloat();
    private AtomicFloat sensorMagnetometerY = new AtomicFloat();
    private AtomicFloat sensorMagnetometerZ = new AtomicFloat();

    private AtomicFloat sensorAtmosphericPressure = new AtomicFloat();

    private AtomicFloat roll = new AtomicFloat();
    private AtomicFloat pitch = new AtomicFloat();
    private AtomicFloat yaw = new AtomicFloat();




    //Battery
    public short getBattery() {
        return (short)battery.get();
    }

    public void setBattery(short newValue) {
        battery.set(newValue);
    }


    //Motors
    public byte getMotorFrontLeft() {
        return (byte)motorFrontLeft.get();
    }

    public void setMotorFrontLeft(byte newValue) {
        motorFrontLeft.set(newValue);
    }

    public byte getMotorBackLeft() {
        return (byte)motorBackLeft.get();
    }

    public void setMotorBackLeft(byte newValue) {
        motorBackLeft.set(newValue);
    }

    public byte getMotorBackRight() {
        return (byte)motorBackRight.get();
    }

    public void setMotorBackRight(byte newValue) {
        motorBackRight.set(newValue);
    }

    public byte getMotorFrontRight() {
        return (byte)motorFrontRight.get();
    }

    public void setMotorFrontRight(byte newValue) {
        motorFrontRight.set(newValue);
    }


    //Gamepad
    public float getGamePadLeftXaxis(){
        return gamePadLeftXaxis.get();
    }

    public void setGamePadLeftXaxis(float newValue){
        gamePadLeftXaxis.set(newValue);
    }

    public float getGamePadLeftYaxis() {
        return gamePadLeftYaxis.get();
    }

    public void setGamePadLeftYaxis(float newValue) {
        gamePadLeftYaxis.set(newValue);
    }

    public float getGamePadRightXaxis() {
        return gamePadRightXaxis.get();
    }

    public void setGamePadRightXaxis(float newValue) {
        gamePadRightXaxis.set(newValue);
    }

    public float getGamePadRightYaxis() {
        return gamePadRightYaxis.get();
    }

    public void setGamePadRightYaxis(float newValue) {
        gamePadRightYaxis.set(newValue);
    }


    //Sensors
    public float getSensorGyroscopeX() {return sensorGyroscopeX.get();}

    public void setSensorGyroscopeX(float newValue){sensorGyroscopeX.set(newValue);}

    public float getSensorGyroscopeY() {return sensorGyroscopeY.get();    }

    public void setSensorGyroscopeY(float newValue) {sensorGyroscopeY.set(newValue);}

    public float getSensorGyroscopeZ() {
        return sensorGyroscopeZ.get();
    }

    public void setSensorGyroscopeZ(float newValue) {sensorGyroscopeZ.set(newValue);}

    public float getSensorAccelerometerX() {
        return sensorAccelerometerX.get();
    }

    public void setSensorAccelerometerX(float newValue) {sensorAccelerometerX.set(newValue);}

    public float getSensorAccelerometerY() {
        return sensorAccelerometerY.get();
    }

    public void setSensorAccelerometerY(float newValue) {sensorAccelerometerY.set(newValue);}

    public float getSensorAccelerometerZ() {
        return sensorAccelerometerZ.get();
    }

    public void setSensorAccelerometerZ(float newValue) {sensorAccelerometerZ.set(newValue);}

    public float getSensorMagnetometerX() {return sensorMagnetometerX.get();}

    public void setSensorMagnetometerX(float newValue) {sensorMagnetometerX.set(newValue);}

    public float getSensorMagnetometerY() {return sensorMagnetometerY.get(); }

    public void setSensorMagnetometerY(float newValue) {sensorMagnetometerY.set(newValue);}

    public float getSensorMagnetometerZ() {
        return sensorMagnetometerZ.get();
    }

    public void setSensorMagnetometerZ(float newValue) {sensorMagnetometerZ.set(newValue);}

    public float getSensorAtmosphericPressure() {return sensorAtmosphericPressure.get(); }

    public void setSensorAtmosphericPressure(float newValue) {sensorAtmosphericPressure.set(newValue);}


    //Orientation
    public float getRoll() {return roll.get(); }

    public void setRoll(float newValue) {roll.set(newValue);}

    public float getPitch() {return pitch.get(); }

    public void setPitch(float newValue) {pitch.set(newValue);}

    public float getYaw() {return yaw.get(); }

    public void setYaw(float newValue) {yaw.set(newValue);}
}
