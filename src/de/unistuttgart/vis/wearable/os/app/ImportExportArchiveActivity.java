package de.unistuttgart.vis.wearable.os.app;

import android.app.Activity;
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

public class ImportExportArchiveActivity extends Activity {

    private File currentDir;
    private ArrayList<File> folders;
    private FileAdapter adapter;
    private ListView list;
    private Button btnSave;
    private TextView text;
    private TextView currentDirectoryTextView = null;
    private boolean isExport = true;
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folders = new ArrayList<File>();
        isExport = getIntent().getBooleanExtra("isExport", false);
        password=getIntent().getBooleanExtra("encrypted",false)?getIntent().getStringExtra("key"):"";
        setContentView(isExport?R.layout.activity_cloud_export:R.layout.activity_cloud_import);
        currentDirectoryTextView = (TextView)findViewById(R.id.textView_current_directory);


        if(isExport){
            text = (TextView) findViewById(R.id.textView8);
            btnSave = (Button) findViewById(R.id.button1);
            btnSave.setEnabled(false);
        }

        currentDir = new File("/mnt/");
        folders.clear();
        folders.addAll(fill(currentDir));
        list = (ListView) findViewById(R.id.listView1);
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

    private void startFileImport(File archiveFile) {
        if(password.equals("")){
            Toast.makeText(getBaseContext(),"Importing archive...",Toast.LENGTH_SHORT).show();
            APIFunctions.unpackArchiveFile(archiveFile);
        }
        else{
            // TODO recognize if zip is encrypted by using mime-type
            //APIFunctions.unpackEncryptedFile(password, downloadDestination);
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
            btnSave.setEnabled(true);
            text.setVisibility(View.INVISIBLE);}
        File[] dirs = f.listFiles();
               for (File ff : dirs) {
                if (ff.isDirectory()||ff.getName().endsWith(".zip"))
                    dir.add(ff);
            }
        Collections.sort(dir);
        }
        else{
            if(isExport){
            btnSave.setEnabled(false);
            text.setVisibility(View.VISIBLE);}
        }

        return dir;
    }

    public void upload(View view) {

            File tmp = new File(currentDir.getAbsolutePath() + File.separator + "gos_sensors.zip");
            if (getIntent().getBooleanExtra("encrypted", false)) {
                Archiver.createEncryptedArchiveFile(getIntent().getStringExtra("key"), tmp);
            } else {
                Archiver.createArchiveFile(tmp);
            }
            Toast.makeText(getBaseContext(), "Exporting sensor archive...",
                    Toast.LENGTH_SHORT).show();

    }
}
