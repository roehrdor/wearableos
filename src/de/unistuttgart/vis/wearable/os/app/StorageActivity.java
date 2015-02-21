package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 09.02.2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.cloud.Dropbox;
import de.unistuttgart.vis.wearable.os.cloud.googleDrive.GoogleDrive;

public class StorageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
    }

    public void changeToDropBox(View view) {
        Intent intent = new Intent(getBaseContext(), Dropbox.class);
        startActivity(intent);
    }
//
//    public void changeToOneDrive(View view) {
//        Intent intent = new Intent(getBaseContext(), OneDriveActivity.class);
//        startActivity(intent);
//    }
//
    public void changeToGoogleDrive(View view) {
        Intent intent = new Intent(getBaseContext(), GoogleDrive.class);
        startActivity(intent);
    }
//
//    public void changeToDBSettingsMenu(View view) {
//        Intent intent = new Intent(getBaseContext(), DbSettingsActivity.class);
//        startActivity(intent);
//    }
//    public void exportDB(View view) {
//        Intent intent = new Intent(getBaseContext(), ExportDbActivity.class);
//        startActivity(intent);
//    }
}
