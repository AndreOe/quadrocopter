package oeschger.andre.quadrocopter;

import oeschger.andre.quadrocopter.util.LinAlgUtils;
import oeschger.andre.quadrocopter.util.ValuesStore;

/**
 * Created by andre on 16.01.16.
 */
public class NavigationSolver implements Runnable{

    private static final String TAG = "NavigationSolver";

    private final double filterCoefficient;
    private final double dt;

    private final ValuesStore valuesStore;

    private final double[] acc;
    private final double[] mag;
    private final double[] gyro;

    private final double[][] dcmAccMag;
    private final double[][] dcmCompFilter;
    private final double[][] omegaSkew;
    private final double[][] omegaDot;
    private final double[][] omegaSkewT;

    private final double[] down;
    private final double[] north;
    private final double[] east;

    public NavigationSolver(ValuesStore valuesStore, double filterCoefficient, double dt){
        this.valuesStore = valuesStore;
        this.filterCoefficient = filterCoefficient;
        this.dt = dt;
        dcmAccMag = new double[3][3];
        dcmCompFilter = new double[3][3];
        dcmCompFilter[0][0] = 1;
        dcmCompFilter[0][1] = 0;
        dcmCompFilter[0][2] = 0;
        dcmCompFilter[1][0] = 0;
        dcmCompFilter[1][1] = 1;
        dcmCompFilter[1][2] = 0;
        dcmCompFilter[2][0] = 0;
        dcmCompFilter[2][1] = 0;
        dcmCompFilter[2][2] = 1;
        omegaSkew = new double[3][3];
        omegaDot = new double[3][3];
        omegaSkewT = new double[3][3];
        down = new double[3];
        north = new double[3];
        east = new double[3];
        acc = new double[3];
        mag = new double[3];
        gyro  = new double[3];
    }

    private void calculateOrientation(){



        final float gyroX = valuesStore.getSensorGyroscopeX();
        //Log.d(TAG,"gyroX"+gyroX);
        final float gyroY = valuesStore.getSensorGyroscopeY();
        //Log.d(TAG,"gyroY"+gyroY);
        final float gyroZ = valuesStore.getSensorGyroscopeZ();
        //Log.d(TAG,"gyroZ"+gyroZ);

        final float accX = valuesStore.getSensorAccelerometerX();
        //Log.d(TAG,"accX"+accX);
        final float accY = valuesStore.getSensorAccelerometerY();
        //Log.d(TAG,"accY"+accY);
        final float accZ = valuesStore.getSensorAccelerometerZ();
        //Log.d(TAG,"accZ"+accZ);

        final float magX = valuesStore.getSensorMagnetometerX();
        //Log.d(TAG,"magX"+magX);
        final float magY = valuesStore.getSensorMagnetometerY();
        //Log.d(TAG,"magY"+magY);
        final float magZ = valuesStore.getSensorMagnetometerZ();
        //Log.d(TAG,"magZ"+magZ);


        acc[0] = -accY; //creates array and maps to North East Down
        //Log.d(TAG,"acc[0]"+acc[0]);
        acc[1] = -accX;
        //Log.d(TAG,"acc[1]"+acc[1]);
        acc[2] = accZ;
        //Log.d(TAG,"acc[2]"+acc[2]);

        mag[0] = -magY; //creates array and maps to North East Down
        //Log.d(TAG,"mag[0]"+mag[0]);
        mag[1] = -magX;
        //Log.d(TAG,"mag[1]"+mag[1]);
        mag[2] = magZ;
        //Log.d(TAG,"mag[2]"+mag[2]);


        gyro[0] = gyroY; //creates array and maps to North East Down
        //Log.d(TAG,"gyro[0]"+gyro[0]);
        gyro[1] = gyroX;
        //Log.d(TAG,"gyro[1]"+gyro[1]);
        gyro[2] = -gyroZ;
        //Log.d(TAG,"gyro[2]"+gyro[2]);


        down[0] = acc[0];
        //Log.d(TAG,"down[0]"+down[0]);
        down[1] = acc[1];
        //Log.d(TAG,"down[1]"+down[1]);
        down[2] = acc[2];
        //Log.d(TAG,"down[2]"+down[2]);

        north[0] = mag[0];
        //Log.d(TAG,"north[0]"+north[0]);
        north[1] = mag[1];
        //Log.d(TAG,"north[1]"+north[1]);
        north[2] = mag[2];
        //Log.d(TAG,"north[2]"+north[2]);

        LinAlgUtils.crossProduct(down, north, east);
        //Log.d(TAG,"east"+east);
        LinAlgUtils.crossProduct(east, down, north);
        //Log.d(TAG,"north"+north);


        LinAlgUtils.normalize(north);
        //Log.d(TAG,"north"+north);
        LinAlgUtils.normalize(east);
        //Log.d(TAG,"east"+east);
        LinAlgUtils.normalize(down);
        //Log.d(TAG,"down"+down);

        dcmAccMag[0][0]= north[0];
        //Log.d(TAG,"dcmAccMag[0][0]"+dcmAccMag[0][0]);
        dcmAccMag[1][0]= north[1];
        //Log.d(TAG,"dcmAccMag[1][0]"+dcmAccMag[1][0]);
        dcmAccMag[2][0]= north[2];
        //Log.d(TAG,"dcmAccMag[2][0]"+dcmAccMag[2][0]);

        dcmAccMag[0][1]= east[0];
        //Log.d(TAG,"dcmAccMag[0][1]"+dcmAccMag[0][1]);
        dcmAccMag[1][1]= east[1];
        //Log.d(TAG,"dcmAccMag[1][1]"+dcmAccMag[1][1]);
        dcmAccMag[2][1]= east[2];
        //Log.d(TAG,"dcmAccMag[2][1]"+dcmAccMag[2][1]);

        dcmAccMag[0][2]= down[0];
        //Log.d(TAG,"dcmAccMag[0][2]"+dcmAccMag[0][2]);
        dcmAccMag[1][2]= down[1];
        //Log.d(TAG,"dcmAccMag[1][2]"+dcmAccMag[1][2]);
        dcmAccMag[2][2]= down[2];
        //Log.d(TAG,"dcmAccMag[2][2]"+dcmAccMag[2][2]);

        LinAlgUtils.crossProductSkewMatrix(gyro, omegaSkew);
        //Log.d(TAG,"omegaSkew"+omegaSkew);

        LinAlgUtils.transpose(omegaSkew, omegaSkewT);

        LinAlgUtils.matrixMultiply(omegaSkewT, dcmCompFilter, omegaDot);
        //Log.d(TAG,"omegaDot"+omegaDot);



        for(int i = 0;i<3;i++){
            for(int j=0;j<3;j++){
                dcmCompFilter[i][j] = filterCoefficient * (dcmCompFilter[i][j] + omegaDot[i][j]*dt) + (1.0f-filterCoefficient) * dcmAccMag[i][j];
                //Log.d(TAG,"dcmCompFilter["+i+"]"+"["+j+"]"+dcmCompFilter[i][j]);
            }
        }

        down[0] = dcmCompFilter[0][2];
        //Log.d(TAG,"down[0]"+down[0]);
        down[1] = dcmCompFilter[1][2];
        //Log.d(TAG,"down[1]"+down[1]);
        down[2] = dcmCompFilter[2][2];
        //Log.d(TAG,"down[2]"+down[2]);

        north[0] = dcmCompFilter[0][0];
        //Log.d(TAG,"north[0]"+north[0]);
        north[1] = dcmCompFilter[1][0];
        //Log.d(TAG,"north[1]"+north[1]);
        north[2] = dcmCompFilter[2][0];
        //Log.d(TAG,"north[2]"+north[2]);

        LinAlgUtils.crossProduct(down, north, east);
        //Log.d(TAG,"east"+east);
        LinAlgUtils.crossProduct(east, down, north);
        //Log.d(TAG,"north "+north );

        LinAlgUtils.normalize(north);
        //Log.d(TAG,"north "+north );
        LinAlgUtils.normalize(east);
        //Log.d(TAG,"east"+east);
        LinAlgUtils.normalize(down);
        //Log.d(TAG,"down"+down);

        dcmCompFilter[0][0]= north[0];
        //Log.d(TAG,"dcmCompFilter[0][0]"+dcmCompFilter[0][0]);
        dcmCompFilter[1][0]= north[1];
        //Log.d(TAG,"dcmCompFilter[1][0]"+dcmCompFilter[1][0]);
        dcmCompFilter[2][0]= north[2];
        //Log.d(TAG,"dcmCompFilter[2][0]"+dcmCompFilter[2][0]);

        dcmCompFilter[0][1]= east[0];
        //Log.d(TAG,"dcmCompFilter[0][1]"+dcmCompFilter[0][1]);
        dcmCompFilter[1][1]= east[1];
        //Log.d(TAG,"dcmCompFilter[1][1]"+dcmCompFilter[1][1]);
        dcmCompFilter[2][1]= east[2];
        //Log.d(TAG,"dcmCompFilter[2][1]"+dcmCompFilter[2][1]);

        dcmCompFilter[0][2]= down[0];
        //Log.d(TAG,"dcmCompFilter[0][2]"+dcmCompFilter[0][2]);
        dcmCompFilter[1][2]= down[1];
        //Log.d(TAG,"dcmCompFilter[1][2]"+dcmCompFilter[1][2]);
        dcmCompFilter[2][2]= down[2];
        //Log.d(TAG,"dcmCompFilter[2][2]"+dcmCompFilter[2][2]);


        //TODO maybe take calc from Grimm, Fichter
        final float roll = (float) Math.atan2(dcmCompFilter[2][1],dcmCompFilter[2][2]);
        final float pitch = (float) Math.atan2(-dcmCompFilter[2][0], Math.sqrt(dcmCompFilter[0][0]*dcmCompFilter[0][0]+dcmCompFilter[1][0]*dcmCompFilter[1][0]));
        final float yaw = (float) Math.atan2(dcmCompFilter[1][0],dcmCompFilter[0][0]);

        valuesStore.setRoll(roll);
        valuesStore.setPitch(pitch);
        valuesStore.setYaw(yaw);

    }

    @Override
    public void run() {
        calculateOrientation();
    }
}
