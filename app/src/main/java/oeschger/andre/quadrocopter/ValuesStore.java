package oeschger.andre.quadrocopter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by andre on 03.11.15.
 */
public class ValuesStore {

    private AtomicInteger battery = new AtomicInteger(); //value from 0 to 1023 TODO not mapped yet, what is 0% battery ?

    //TODO check linearity
    private AtomicInteger motorFrontLeft = new AtomicInteger(); //value from 0 to 255 that results in 0 to 100 % motor speed
    private AtomicInteger motorBackLeft = new AtomicInteger();  //value from 0 to 255 that results in 0 to 100 % motor speed
    private AtomicInteger motorBackRight = new AtomicInteger(); //value from 0 to 255 that results in 0 to 100 % motor speed
    private AtomicInteger motorFrontRight = new AtomicInteger(); //value from 0 to 255 that results in 0 to 100 % motor speed

    private AtomicFloat gamePadLeftXaxis = new AtomicFloat(); //value from 0.0 to 1.0
    private AtomicFloat gamePadLeftYaxis = new AtomicFloat(); //value from 0.0 to 1.0
    private AtomicFloat gamePadRightXaxis = new AtomicFloat(); //value from 0.0 to 1.0
    private AtomicFloat gamePadRightYaxis = new AtomicFloat(); //value from 0.0 to 1.0


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
}
