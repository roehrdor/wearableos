package de.unistuttgart.vis.wearable.os.app;

/**
 * Created by Lucas on 08.02.2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.unistuttgart.vis.wearable.os.R;

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

    public void saveDB(View view) {
//        String dbPath = getBaseContext().getDatabasePath(
//                DataBaseHelper.getDbName()).getAbsolutePath();
//        File dbfile = new File(dbPath);
//        File newFile = new File(currentFilePath + File.separator
//                + DataBaseHelper.getDbName());
//        InputStream is = null;
//        OutputStream os = null;
//        try {
//            is = new FileInputStream(dbfile);
//            os = new FileOutputStream(newFile);
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = is.read(buffer)) > 0) {
//                os.write(buffer, 0, length);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                is.close();
//                os.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            Toast.makeText(getBaseContext(), "Database export finished",
//                    Toast.LENGTH_SHORT).show();
//        }

    }

}

