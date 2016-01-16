package oeschger.andre.quadrocopter.appClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by andre on 28.10.15.
 *
 * This is an empty Activity just to run once.
 * You have to run it once to allow the service to start after boot.
 */


public class hello extends Activity {

    private static final String USB_ACCESSORY_ATTACHED = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction() != null && getIntent().getAction().equals(USB_ACCESSORY_ATTACHED)) {
            Intent service = new Intent(this, service.class);
            service.putExtras(getIntent());
            startService(service);

            /*Intent launch = new Intent(this, MainActivity.class);
            launch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launch);*/
        }
        this.finish();
    }
}