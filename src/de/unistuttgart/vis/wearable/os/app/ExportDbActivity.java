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

public class ExportDbActivity extends Activity {

    private File currentDir;
    private List<String> folders;
    private ArrayAdapter<String> adapter;
    private ListView list;
    private String currentFilePath;
    private Button btnSave;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_db);
        text = (TextView) findViewById(R.id.textView1);
        btnSave = (Button) findViewById(R.id.button1);
        btnSave.setEnabled(false);
        currentDir = new File("/mnt/");
        folders = fill(currentDir);
        list = (ListView) findViewById(R.id.listView1);
        adapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, folders);
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
     * access is provided for the current folder
     */
    private List<String> fill(File f) {
        File[] dirs = f.listFiles();
        currentFilePath = f.getAbsolutePath();
        if (f.canWrite() && f.canRead()) {
            btnSave.setEnabled(true);
            text.setVisibility(View.INVISIBLE);
        } else {
            btnSave.setEnabled(false);
            text.setVisibility(View.VISIBLE);
        }
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
        if (!f.getName().equalsIgnoreCase("mnt"))
            dir.add(0, "Parent Directory");
        return dir;

    }

    public void saveSensorArchive(View view) {

        File tmp = new File(currentFilePath+File.separator+"gos_sensors.zip");
        if (getIntent().getBooleanExtra("encrypted",false)){
            Archiver.createEncryptedArchiveFile(getIntent().getStringExtra("key"),tmp);
        } else {
            Archiver.createArchiveFile(tmp);
        }
        Toast.makeText(getBaseContext(), "Database export finished",
                Toast.LENGTH_SHORT).show();
    }

}

