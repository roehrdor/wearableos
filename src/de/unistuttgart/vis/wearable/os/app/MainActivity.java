/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.properties.Properties;
import de.unistuttgart.vis.wearable.os.utils.Utils;

public class MainActivity extends Activity {
    private static Context context;


    /**
     * This function starts and initializes the service and the internal function service for later use.
     * Note that this function has to be called in the onCreate method of our Main Activity.
     * To assure the service keeps on running even if the starting application or component is no longer available
     * we must make sure to start the Service using the {@link android.content.ContextWrapper#startService} function
     * before we call the {@link android.content.Context#bindService} function. (The secondary function is automatically
     * called by the {@link de.unistuttgart.vis.wearable.os.handle.APIHandle}.
     */
    private void initializeServices() {
        // Create a intent for the Garment Service
        Intent serviceIntent = Utils.explicitFromImplicit(getApplicationContext(), new Intent(Properties.GARMENT_SERVICE));

        // If we were able to create the Intent
        if(serviceIntent != null) {
            // Start a service
            startService(serviceIntent);
        }

        // Create a intent for the Garment Internal Service
        Intent serviceIntentInternal = Utils.explicitFromImplicit(getApplicationContext(), new Intent(Properties.GARMENT_INTERNAL_SENSOR));

        // If we were able to create the Intent
        if(serviceIntentInternal != null) {
            // Start the service
            startService(serviceIntentInternal);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        // Start the services
        //
        initializeServices();

        setContentView(R.layout.activity_main);
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public void openPrivacyActivity(View view) {
        Intent intent = new Intent(this, PrivacyActivity.class);
        startActivity(intent);
    }

    public void openSensorsActivity(View view) {
        Intent intent = new Intent(this, SensorsActivity.class);
        startActivity(intent);
    }

    public void openAboutActivity(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void openStorageActivity(View view) {
        Intent intent = new Intent(this, Import_ExportActivity.class);
        startActivity(intent);
    }

    public void openBluetoothActivity(View view) {
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivity(intent);
    }

    public void openHARActivity(View view) {
        Intent intent = new Intent(this, HARActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
