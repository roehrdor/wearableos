package de.unistuttgart.vis.wearable.os.cloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.properties.Properties;
import de.unistuttgart.vis.wearable.os.utils.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 18.02.2015.
 */
public class Dropbox extends Activity {
    // TODO remove when releasing source code
    private static final String APP_KEY = "hfo4su0wieruugc";
    private static final String APP_SECRET = "z82qqqj2jegxtbp";
    private static final Session.AccessType ACCESS_TYPE = Session.AccessType.AUTO;
    private Context context;
    private Button button;
    private Upload uploadTask;
    private Download downloadTask;
    private File tmp;
    private boolean finished = false;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;
    private ListView list;
    private ArrayAdapter adapter;
    private DropboxAPI.Entry currentEntry;
    private String currentFilePath;
    private String currentDir;
    private List<String> dir;
    private Integer[] images;
    private TextView pathView;
    private String key;
    private boolean abort = false;
    private ProgressDialog progressDialog;
    int startCount = 0;


    private DropboxAPI<AndroidAuthSession> mDBApi;


    /**
     * Async Task for the upload
     */
    private class Upload extends AsyncTask<Void, Long, Boolean> {
        private DropboxAPI.UploadRequest request = null;

        public Upload() {
            // create Progress Dialog to display the progress of connectionEstablished
            progressDialog = new ProgressDialog(Dropbox.this);
            progressDialog.setMax(100);
            progressDialog
                    .setMessage("Uploading File...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           final Handler handler = new Handler();

                           final Runnable cancel = new Runnable() {
                                public void run() {
                                    try {
                                        abort = true;
                                        if (request!= null) {
                                            request.abort();
                                        }
                                        if (inputStream!=null){
                                            inputStream.close();
                                        }
                                        mDBApi.getSession().unlink();
                                        Toast.makeText(context,
                                                "File-Upload cancelled",
                                                Toast.LENGTH_SHORT).show();
                                        tmp.delete();
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        // could not abort without exception
                                        Toast.makeText(context,
                                                "File-Upload cancelled",
                                                Toast.LENGTH_SHORT).show();
                                        tmp.delete();
                                        finish();
                                    }
                                }
                           };
                           handler.post(cancel);
                        }
                    });
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... params) {


            //  Path
            tmp = new File(Dropbox.this.getFilesDir().getAbsolutePath()+File.separator+"tmp.zip");

            if (getIntent().getBooleanExtra("encrypted",false)) {
                Archiver.createEncryptedArchiveFile(getIntent().getStringExtra("key"), tmp);
            } else {
                Archiver.createArchiveFile(tmp);
            }

            inputStream = null;
            if (tmp.exists() && tmp.canRead()) {
                try {
                    inputStream = new FileInputStream(tmp);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

                try {
                    // set request for connectionEstablished to Dropbox
                    request = mDBApi.putFileRequest(currentDir + File.separator + "gos-sensors.zip", inputStream, tmp.length(), null, true,
                            new ProgressListener() {

                                @Override
                                public long progressInterval() {
                                    return 100;
                                }

                                @Override
                                public void onProgress(long bytes, long total) {
                                    publishProgress(bytes);
                                }

                            });

                    if (request != null) {
                        // start connectionEstablished
                        request.upload();
                        finished = true;
                        return true;

                    }

                } catch (DropboxException e) {
                    // if dropboxconnection is unlinked
                    abort = true;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Long... progress) {

            // set progress in percent
            int percent = (int) (100.0 * (double) progress[0] / tmp.length() + 0.5);
            progressDialog.setProgress(percent);


        }

        @Override
        protected void onPostExecute(final Boolean result) {

            tmp.delete();
            // print result
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (finished) {
                        Toast.makeText(context,
                                "File uploaded",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            });
        }
    }

    /**
     * AsyncTask for the download
     */
    private class Download extends AsyncTask<Void, Long, Boolean> {
        long totalSize;
        File file;

        @Override
        protected Boolean doInBackground(Void... params) {

            // TODO add right pathView
            file = new File(Properties.exportDirectory, "tmp.zip");
            outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                DropboxAPI.DropboxFileInfo info = mDBApi.getFile(currentFilePath, null,
                        outputStream, new ProgressListener() {

                            @Override
                            public long progressInterval() {
                                return 100;
                            }

                            @Override
                            public void onProgress(long bytes, long total) {
                                publishProgress(bytes,total);
                            }
                        });
                if (info!=null){
                    finished=true;
                    totalSize = info.getFileSize();
                }

            } catch (DropboxException e) {


            } catch (FileNotFoundException e) {

            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            // print result
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (outputStream != null) {


                        if (!Archiver.notEncryptedGOSFile(file) && !abort) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(Dropbox.this);

                            alert.setTitle("Please enter password:");
                            final EditText input = new EditText(Dropbox.this);
                            alert.setView(input);

                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    key = input.getText().toString();
                                    int value = Archiver.unpackEncryptedFile(key, file);
                                    switch (value) {
                                        case Constants.UNPACK_NO_ERROR:
                                            Toast.makeText(context,
                                                    "File import finished",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                            file.delete();
                                            break;
                                        case Constants.UNPACK_INVALID_FILE:
                                            Toast.makeText(context,
                                                    "Invalid File",
                                                    Toast.LENGTH_SHORT).show();
                                            file.delete();
                                            finish();
                                            break;
                                        case Constants.UNPACK_EXTRACTING_FAILED:
                                            Toast.makeText(context,
                                                    "Extracting failed",
                                                    Toast.LENGTH_SHORT).show();
                                            file.delete();
                                            finish();
                                            break;
                                        case Constants.UNPACK_WRONG_KEY:
                                            Toast.makeText(context,
                                                    "Wrong Password",
                                                    Toast.LENGTH_SHORT).show();
                                            file.delete();
                                            finish();
                                            break;

                                    }
                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finished = false;
                                    file.delete();
                                    finish();
                                }
                            });
                            alert.setCancelable(false);
                            alert.show();
                        } else if (!abort) {
                            int value = APIFunctions.unpackArchiveFile(file);

                            switch (value) {
                                case Constants.UNPACK_NO_ERROR:
                                    Toast.makeText(context,
                                            "File import finished",
                                            Toast.LENGTH_SHORT).show();
                                    file.delete();
                                    finish();
                                    break;
                                case Constants.UNPACK_INVALID_FILE:
                                    Toast.makeText(context,
                                            "Invalid File",
                                            Toast.LENGTH_SHORT).show();
                                    file.delete();
                                    finish();
                                    break;
                                case Constants.UNPACK_EXTRACTING_FAILED:
                                    Toast.makeText(context,
                                            "Extracting failed",
                                            Toast.LENGTH_SHORT).show();
                                    file.delete();
                                    finish();
                                    break;
                                case Constants.UNPACK_WRONG_KEY:
                                    Toast.makeText(context,
                                            "Wrong Password",
                                            Toast.LENGTH_SHORT).show();
                                    file.delete();
                                    finish();
                                    break;

                            }
                        }
                    }
                }
            });
            return;
        }

        @Override
        protected void onProgressUpdate(Long... progress) {

            progressDialog.setProgress((int)(progress[0]*100/progress[1]));

        }

        public Download() {
            // create Progress Dialog to display the progress of connectionEstablished
            progressDialog = new ProgressDialog(Dropbox.this);
            progressDialog.setMax(100);
            progressDialog
                    .setMessage("Downloading File...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finished =false;
                            abort = true;
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(context,
                                            "File-Download cancelled",
                                            Toast.LENGTH_SHORT).show();
                                }

                            });
                            finish();
                        }
                    });
            progressDialog.show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // set text view for current path
        pathView = (TextView) findViewById(R.id.textView_current_directory);

        // set images for ListView
        images = new Integer[2];
        images[0] = R.drawable.folder;
        images[1] = R.drawable.file;

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // set the right content layout for import or export
        if (getIntent().getBooleanExtra("isExport",false)){
            setContentView(R.layout.activity_cloud_export);
            button = (Button) findViewById(R.id.btn_upload);
        } else {
            setContentView(R.layout.activity_cloud_import);
        }
        context = this.getBaseContext();

        // start Dropbox Authentication if internet is avaible
        if (internetAvailable()) {
            // register keys
            if (mDBApi==null) {
                AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
                AndroidAuthSession session = new AndroidAuthSession(appKeys,
                        ACCESS_TYPE);
                // start Dropbox authentication
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                if (!mDBApi.getSession().authenticationSuccessful()) {
                    mDBApi.getSession().startAuthentication(Dropbox.this);
                }
            }

        } else {
            Toast.makeText(getBaseContext(),
                    "Please activate WIFI or mobile-data connection",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

    }



    private void setFileList(final String path) {
        //get path List from Android
        AsyncTask<String,String,String> aTask = new AsyncTask<String,String,String>() {

            @Override
            protected String doInBackground(String... params) {
                DropboxAPI.Entry entry = null;
                try {
                    entry = mDBApi.metadata(path, 1000, null, true, null);
                    currentEntry = entry;
                    currentDir = entry.path;
                } catch (DropboxException e) {
                    e.printStackTrace();
                }
                dir=new ArrayList<String>();

                if(entry != null) {
                    for (DropboxAPI.Entry ent : entry.contents) {

                        if (!getIntent().getBooleanExtra("isExport", false)) {
                            if (ent.isDir || ent.path.endsWith(".zip")) {
                                String[] dirs = ent.path.split("/");
                                dir.add(new String(dirs[dirs.length-1]));

                            }
                        } else {
                            if (ent.isDir) {
                                String[] dirs = ent.path.split("/");
                                dir.add(new String(dirs[dirs.length-1]));

                            }
                        }

                    }
                }
                if (dir.size()==0){
                    dir.add("No Content");
                }

                // show list in the Activity
                runOnUiThread(new Runnable() {
                    public void run() {

                        list = (ListView) findViewById(R.id.listViewFileChooser);
                        String[] tmpNames = new String[dir.size()];
                        tmpNames = dir.toArray(tmpNames);
                        if (dir.get(0).equals("No Content")) {
                            list.setAdapter(new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_list_item_1, tmpNames));

                        } else {
                            adapter = new StorageAdapter(Dropbox.this, tmpNames, images);
                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                /*
                                 * Checks if the user wants to browse the parent directory or a
                                 * child directory
                                 */
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    if (!internetAvailable()){
                                        Toast.makeText(getBaseContext(),"Please activate wifi or mobile Data",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    if (list.getItemAtPosition(position).toString().endsWith(".zip")) {
                                        finished = false;
                                        abort = false;
                                        currentFilePath = currentEntry.path + File.separator + list.getItemAtPosition(position).toString();
                                        downloadTask = new Download();
                                        downloadTask.execute(null, null, null);
                                    } else {
                                        setFileList(currentEntry.path + File.separator + list.getItemAtPosition(position).toString());
                                        pathView.setText("Current Folder: " + currentEntry.path + File.separator + list.getItemAtPosition(position).toString());
                                    }

                                }
                            });
                        }
                        if (getIntent().getBooleanExtra("isExport", false)) {
                            button.setVisibility(View.VISIBLE);
                        }

                    }
                });

                return null;
            }
        };
        aTask.execute(null, null, null);
    }


    @Override
    protected void onResume() {
       startCount++;
       super.onResume();
        pathView = (TextView) findViewById(R.id.textView_current_directory);
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // complete the authentication
                mDBApi.getSession().finishAuthentication();
                if (getIntent().getBooleanExtra("isExport",false)) {
                    button.setVisibility(View.INVISIBLE);
                }
                if(list==null) {
                    pathView.setText("Current Folder: /");
                    setFileList("/");
                }
            } catch (IllegalStateException e) {
            }

        } else {
            if (startCount>1){
                finish();
            }
        }


    }

    @Override
    public void onBackPressed() {
        if (!internetAvailable()){
            Toast.makeText(getBaseContext(),"Please activate wifi or mobile Data",Toast.LENGTH_SHORT).show();
            finish();
        }
        if (currentEntry== null){
            super.onBackPressed();
        }
        if (currentEntry!=null&&!currentEntry.path.equals("/")) {
            setFileList(currentEntry.parentPath());
            pathView.setText("Current Folder: "+currentEntry.parentPath());
        } else {
            super.onBackPressed();
        }

    }


    /**
     * starts session for authentication on dropbox
     *
     * @param view
     */
    public void upload(View view) {
        if (!internetAvailable()){
            Toast.makeText(getBaseContext(),"Please activate wifi or mobile Data",Toast.LENGTH_SHORT).show();
            finish();
        }
            finished = false;
            abort = true;
            uploadTask = new Upload();
            uploadTask.execute(null, null, null);
    }



    /**
     * check if internet connection is available
     *
     * @return
     */
    public boolean internetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}