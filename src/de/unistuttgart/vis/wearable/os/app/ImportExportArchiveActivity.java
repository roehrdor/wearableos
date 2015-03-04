package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.cloud.Archiver;
import de.unistuttgart.vis.wearable.os.cloud.StorageAdapter;

public class ImportExportArchiveActivity extends Activity {

    private File currentDir;
    private List<String> folders;
    private ArrayAdapter<String> adapter;
    private ListView list;
    private String currentFilePath;
    private Button btnSave;
    private TextView text;
    private boolean isExport = true;
    private boolean archiveExists = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_db);

        text = (TextView) findViewById(R.id.textView1);

        btnSave = (Button) findViewById(R.id.button1);
        isExport = getIntent().getBooleanExtra("isExport", false);
        if (!isExport) {
            btnSave.setText("Import");
            text.setText(getResources().getString(R.string.textView_text_no_archive_for_import));
        }

        btnSave.setEnabled(false);
        currentDir = new File("/mnt/");
        folders = fill(currentDir);
        list = (ListView) findViewById(R.id.listView1);
        Integer[] img = new Integer[2];
        img[0]= R.drawable.folder;
        img[1]= R.drawable.file;
        String[] names = new String[folders.size()];
        names = folders.toArray(names);
        adapter = new StorageAdapter(ImportExportArchiveActivity.this,names,img);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            /*
             * Checks if the user wants to browse the parent directory or a
             * child directory
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (list.getItemAtPosition(position).toString()
                        .equals("Parent Directory")) {
                    folders = fill(new File(currentFilePath.substring(0,
                            currentFilePath.lastIndexOf(File.separator))));

                    adapter = new ArrayAdapter<String>(getBaseContext(),
                            android.R.layout.simple_list_item_1, folders);
                    list.setAdapter(adapter);
                } else {
                    folders = fill(new File(currentFilePath + File.separator
                            + list.getItemAtPosition(position)));
                    adapter = new ArrayAdapter<String>(getBaseContext(),
                            android.R.layout.simple_list_item_1, folders);
                    list.setAdapter(adapter);
                }
            }
        });

    }

    /**
     * Returns a list of all folders in the current folder and checks if write
     * access is provided for the current folder if exporting and checks for
     * the appropriate archive file when importing
     */
    private List<String> fill(File f) {
        File[] dirs = f.listFiles();
        currentFilePath = f.getAbsolutePath();
        this.setTitle("Current Folder: " + f.getName());
        List<String> dir = new ArrayList<String>();
        try {
            for (File ff : dirs) {
                if (ff.isDirectory())
                    dir.add(ff.getName());
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        if (!f.getName().equalsIgnoreCase("mnt")) {
            dir.add(0, "Parent Directory");
        }
        if (isExport) {
            if (f.canWrite() && f.canRead()) {
                btnSave.setEnabled(true);
                text.setVisibility(View.INVISIBLE);
            } else {
                btnSave.setEnabled(false);
                text.setVisibility(View.VISIBLE);
            }


        }
        if (!isExport) {
            archiveExists = false;
            if (f.canRead()) {
                for (File currentFile : f.listFiles()) {
                    if (currentFile.canRead() && currentFile.isFile() && currentFile.getName().equals("gos_sensors.zip")) {
                        archiveExists = true;
                        break;
                    }
                }
            }
            if (archiveExists) {
                btnSave.setEnabled(true);
                text.setVisibility(View.INVISIBLE);
            }
            if (!archiveExists) {
                btnSave.setEnabled(false);
                text.setVisibility(View.VISIBLE);
            }
        }

        return dir;
    }

    public void saveSensorArchive(View view) {
        if (isExport) {
            File tmp = new File(currentFilePath + File.separator + "gos_sensors.zip");
            if (getIntent().getBooleanExtra("encrypted", false)) {
                Archiver.createEncryptedArchiveFile(getIntent().getStringExtra("key"), tmp);
            } else {
                Archiver.createArchiveFile(tmp);
            }
            Toast.makeText(getBaseContext(), "Archive export finished",
                    Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else{
            // TODO Handle import
        }
    }
}
