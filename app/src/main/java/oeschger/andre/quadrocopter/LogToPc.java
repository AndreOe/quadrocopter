package oeschger.andre.quadrocopter;

import android.util.Log;

import oeschger.andre.quadrocopter.communications.ComAndroidToPc;
import oeschger.andre.quadrocopter.communications.messages.LogMessage;
import oeschger.andre.quadrocopter.util.ValuesStore;

/**
 * Created by andre on 23.01.16.
 */
public class LogToPc implements Runnable{

    private static final String TAG = "LogToPc";

    private ValuesStore valuesStore;
    private ComAndroidToPc comAndroidToPc;

    public LogToPc(ValuesStore valuesStore,ComAndroidToPc comAndroidToPc){
        this.valuesStore = valuesStore;
        this.comAndroidToPc = comAndroidToPc;
    }

    @Override
    public void run() {
        comAndroidToPc.sentToPc(new LogMessage(valuesStore));
        //Log.d(TAG, "LogMessage generated");
    }
}
