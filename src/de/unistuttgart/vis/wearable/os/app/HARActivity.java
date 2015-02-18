package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
import android.os.Bundle;

import de.unistuttgart.vis.wearable.os.R;

/**
 * Created by Lucas on 11.02.2015.
 */
public class HARActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_har);
    }
}