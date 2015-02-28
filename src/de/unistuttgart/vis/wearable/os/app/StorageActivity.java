package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 09.02.2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.cloud.Dropbox;
import de.unistuttgart.vis.wearable.os.cloud.googleDrive.GoogleDrive;
import de.unistuttgart.vis.wearable.os.cloud.oneDrive.OneDrive;

public class StorageActivity extends Activity {
    private boolean isExport = false;
    private Switch mySwitch;
    private boolean encrypted;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isExport = getIntent().getBooleanExtra("isExport", false);
        setContentView(R.layout.activity_storage);
        mySwitch = (Switch) findViewById(R.id.switch5);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AlertDialog.Builder alert = new AlertDialog.Builder(StorageActivity.this);

                    alert.setTitle("Please enter password:");
                    final EditText input = new EditText(StorageActivity.this);
                    encrypted = true;
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            key = input.getText().toString();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            encrypted =false;
                            buttonView.setChecked(false);
                            return;
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                }
            }
        });
    }

    public void changeToDropBox(View view) {
        Intent intent = new Intent(getBaseContext(), Dropbox.class);
        intent.putExtra("isExport",isExport);
        if (encrypted){
            intent.putExtra("key", key);
            intent.putExtra("encrypted",encrypted);
        }

        startActivity(intent);
    }

    public void changeToOneDrive(View view) {
        Intent intent = new Intent(getBaseContext(), OneDrive.class);
        intent.putExtra("isExport",isExport);
        if (encrypted){
            intent.putExtra("key", key);
            intent.putExtra("encrypted",encrypted);
        }
        startActivity(intent);
  }

    public void changeToGoogleDrive(View view) {
        Intent intent = new Intent(getBaseContext(), GoogleDrive.class);
        intent.putExtra("isExport",isExport);
        if (encrypted){
            intent.putExtra("key", key);
            intent.putExtra("encrypted",encrypted);
        }
        startActivity(intent);
    }

    public void export(View view) {
        Intent intent = new Intent(getBaseContext(), ExportDbActivity.class);
        intent.putExtra("isExport",isExport);
        if (encrypted){
            intent.putExtra("key", key);
            intent.putExtra("encrypted",encrypted);
        }
        startActivity(intent);
    }
}
