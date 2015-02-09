/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 pfaehlfd, roehrdor, roehrlls
 */
package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.unistuttgart.vis.wearable.os.R;

public class MainActivity extends Activity {
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Intent intent = new Intent(this, StorageActivity.class);

        startActivity(intent);
    }

//    public void openBluetoothActivity(View view) {
//        Intent intent = new Intent(this, BluetoothActivity.class);
//
//        startActivity(intent);
//    }
//
//    public void openHARActivity(View view) {
//        Intent intent = new Intent(this, HARActivity.class);
//
//        startActivity(intent);
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
