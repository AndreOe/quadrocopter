package oeschger.andre.quadrocopter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by andre on 28.10.15.
 *
 * This is an empty Activity just to run once.
 * You have to run it once to allow the service to start after boot.
 */


public class hello extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        Toast.makeText(getBaseContext(), "Hello........", Toast.LENGTH_LONG).show();
    }
}