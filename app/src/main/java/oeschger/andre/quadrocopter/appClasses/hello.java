package oeschger.andre.quadrocopter.appClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import oeschger.andre.quadrocopter.R;

/**
 * Created by andre on 28.10.15.
 *
 * This is an empty Activity just to run once.
 * You have to run it once to allow the service to start after boot.
 */


public class hello extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent service = new Intent(this, service.class);

        setContentView(R.layout.activity_main);

        final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startService(service);
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopService(service);
            }
        });


        //this.finish();
    }
}