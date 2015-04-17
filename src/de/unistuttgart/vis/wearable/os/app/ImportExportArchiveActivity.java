package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.cloud.Archiver;
import de.unistuttgart.vis.wearable.os.cloud.FileAdapter;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * Activity to export or import archives of sensor data and properties that are encrypted or unencrypted
 */
public class ImportExportArchiveActivity extends Activity {

    private File currentDir;
    private ArrayList<File> folders;
    private FileAdapter adapter;
    private ListView list;
    private Button btnSave;
    private TextView currentDirectoryTextView = null;
    private boolean isExport = true;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folders = new ArrayList<File>();
        isExport = getIntent().getBooleanExtra("isExport", false);
        setContentView(isExport?R.layout.activity_cloud_export:R.layout.activity_cloud_import);
        currentDirectoryTextView = (TextView)findViewById(R.id.textView_current_directory);


        if(isExport){
            btnSave = (Button) findViewById(R.id.btn_upload);
            btnSave.setEnabled(false);
            btnSave.setText("No Write Permission");
        }

        currentDir = new File("/mnt/");
        folders.clear();
        folders.addAll(fill(currentDir));
        list = (ListView) findViewById(R.id.listViewFileChooser);
        adapter = new FileAdapter(getBaseContext(),folders);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ArrayList<File> currentList = new ArrayList<File>();
                if(!((File)list.getItemAtPosition(position)).isFile()){
                    currentList = fill(((File) list.getItemAtPosition(position)));
                    folders.clear();
                    folders.addAll(currentList);
                    adapter.notifyDataSetChanged();}
                else{
                    startFileImport((File)list.getItemAtPosition(position));
                }
            }
        });

    }

    private void startFileImport(final File archiveFile) {
        if(archiveFile.length()< Utils.availableInternalSpace()){
            if(Archiver.notEncryptedGOSFile(archiveFile)){
                int value = APIFunctions.unpackArchiveFile(archiveFile);
                switch (value) {
                    case Constants.UNPACK_NO_ERROR:
                        Toast.makeText(getBaseContext(),
                                "File import finished",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case Constants.UNPACK_INVALID_FILE:
                        Toast.makeText(getBaseContext(),
                                "Invalid File",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case Constants.UNPACK_EXTRACTING_FAILED:
                        Toast.makeText(getBaseContext(),
                                "Extracting failed",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case Constants.UNPACK_WRONG_KEY:
                        Toast.makeText(getBaseContext(),
                                "Wrong Password",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        break;

                }
            }
            else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ImportExportArchiveActivity.this);

                alert.setTitle("Please enter password:");
                final EditText input = new EditText(ImportExportArchiveActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        key = input.getText().toString();
                        int value = Archiver.unpackEncryptedFile(key, archiveFile);
                        switch (value) {
                            case Constants.UNPACK_NO_ERROR:
                                Toast.makeText(getBaseContext(),
                                        "File import finished",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            case Constants.UNPACK_INVALID_FILE:
                                Toast.makeText(getBaseContext(),
                                        "Invalid File",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            case Constants.UNPACK_EXTRACTING_FAILED:
                                Toast.makeText(getBaseContext(),
                                        "Extracting failed",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            case Constants.UNPACK_WRONG_KEY:
                                Toast.makeText(getBaseContext(),
                                        "Wrong Password",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                break;

                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
                alert.setCancelable(false);
                alert.show();
            }
        }else{
            Toast.makeText(getBaseContext(),"Not enough space available for import, aborting import",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(!currentDir.getAbsolutePath().equals("/mnt")){
            currentDir = currentDir.getParentFile();
            folders.clear();
            folders.addAll(fill(currentDir));
            adapter.notifyDataSetChanged();
        }
        else{
            super.onBackPressed();}
    }

    /**
     * Returns a list of all folders in the current folder and checks if write
     * access is provided for the current folder if exporting and checks for
     * the appropriate archive file when importing
     */
    private ArrayList<File> fill(File f) {
        ArrayList<File> dir = new ArrayList<File>();
        currentDir = f;
        currentDirectoryTextView.setText("Current Folder: " + f.getName());
        if(f.canRead()){
            if(isExport&&f.canWrite()){
                btnSave.setText("Upload SensorData");
                btnSave.setEnabled(true);
            }
            else{
                if (isExport) {
                    btnSave.setEnabled(false);
                    btnSave.setText("No Write Permission");
                }
            }
            File[] dirs = f.listFiles();
            for (File ff : dirs) {
                if (ff.isDirectory()||ff.getName().endsWith(".zip")) {
                    dir.add(ff);
                }
            }
            Collections.sort(dir);
        }


        return dir;
    }

    public void upload(View view) {

        final File tmp = new File(currentDir.getAbsolutePath() + File.separator + "gos_sensors.zip");
        if(Utils.availableExternalSpace(currentDir, getApplicationContext())){
            if(tmp.exists()){
                AlertDialog.Builder alert = new AlertDialog.Builder(ImportExportArchiveActivity.this);
                alert.setTitle("Overwrite existing archive?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (getIntent().getBooleanExtra("encrypted", false)) {
                            Archiver.createEncryptedArchiveFile(getIntent().getStringExtra("key"), tmp);
                        } else {
                            Archiver.createArchiveFile(tmp);
                        }
                        Toast.makeText(getBaseContext(), "Export finished",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getBaseContext(),"Export cancelled",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                alert.setCancelable(false);
                alert.show();
            }else {
                if (getIntent().getBooleanExtra("encrypted", false)) {
                    Archiver.createEncryptedArchiveFile(getIntent().getStringExtra("key"), tmp);
                } else {
                    Archiver.createArchiveFile(tmp);
                }
                Toast.makeText(getBaseContext(), "Export finished",
                        Toast.LENGTH_SHORT).show();
                finish();

            }}
        else{
            Toast.makeText(getBaseContext(),"Not enough space available, aborting export",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
