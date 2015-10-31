package oeschger.andre.quadrocopter;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by andre on 28.10.15.
 */
public class autostart extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        Intent intent = new Intent(arg0,service.class);
        arg0.startService(intent);
        Log.i("Autostart", "started");
    }
}