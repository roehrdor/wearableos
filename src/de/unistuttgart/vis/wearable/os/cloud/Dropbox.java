package de.unistuttgart.vis.wearable.os.cloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import de.unistuttgart.vis.wearable.os.utils.Utils;

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
    private List<String> fileList;
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


    private DropboxAPI<AndroidAuthSession> mDBApi;


    private class Upload extends AsyncTask<Void, Long, Boolean> {
        private final ProgressDialog progressDialog;
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
                            class cancelUpload extends
                                    AsyncTask<Void, Long, Boolean> {

                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    request.abort();
                                    finished = false;
                                    return false;
                                }

                            }
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            new cancelUpload().execute(null, null, null);
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
                    request = mDBApi.putFileOverwriteRequest(currentDir+File.separator+"gos-sensors.zip", inputStream, tmp.length(),
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
                    e.printStackTrace();
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
                    progressDialog.dismiss();
                    if (finished) {
                        Toast.makeText(context,
                                "File uploaded",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(context,
                                "File upload failed",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            });
        }
    }

    private class Download extends AsyncTask<Void, Long, Boolean> {
        private final ProgressDialog progressDialog;
        DropboxAPI.DropboxFileInfo info= null;
        long totalSize;
        File file;

        @Override
        protected Boolean doInBackground(Void... params) {

            // TODO add right path
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
                                if (!isCancelled())
                                    publishProgress(bytes, total);
                                else {
                                    if (outputStream != null) {
                                        try {
                                            outputStream.close();
                                            finished =false;
                                        } catch (IOException e) {
                                        }
                                    }
                                }
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
                        // TODO look for encryption
                        APIFunctions.unpackArchiveFile(file);
                        outputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            file.delete();
            // print result
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (finished) {
                        Toast.makeText(context,
                                "File import finished",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(context,
                                "File import failed",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            });
        }

        @Override
        protected void onProgressUpdate(Long... progress) {
            // set progress in percent
            int percent = (int) (100.0 * (double) progress[0]/totalSize + 0.5);
            progressDialog.setProgress(percent);

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
                            class cancelDownload extends
                                    AsyncTask<Void, Long, Boolean> {

                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    finished = false;
                                    return false;
                                }

                            }
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            new cancelDownload().execute(null, null, null);
                        }
                    });
            progressDialog.show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        images = new Integer[2];
        images[0] = R.drawable.folder;
        images[1] = R.drawable.file;
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("isExport",false)){
            setContentView(R.layout.activity_dropbox_export);
            button = (Button) findViewById(R.id.button1);
        } else {
            setContentView(R.layout.activity_dropbox_import);
        }
        context = this.getBaseContext();

        if (internetAvailable()) {
            // register keys
            AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(appKeys,
                    ACCESS_TYPE);
            // start Dropbox authentication
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            mDBApi.getSession().startOAuth2Authentication(Dropbox.this);

        } else {
            Toast.makeText(getBaseContext(),
                    "Please activate WIFI or mobile-data connection",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void setFileList(final String path) {
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
                runOnUiThread(new Runnable(){
                    public void run() {

                        list = (ListView) findViewById(R.id.listView1);
                        String [] tmpNames = new String[dir.size()];
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
                                    if (list.getItemAtPosition(position).toString().endsWith(".zip")){
                                        finished =false;
                                        currentFilePath = currentEntry.path + File.separator + list.getItemAtPosition(position).toString();
                                        downloadTask = new Download();
                                        downloadTask.execute(null,null,null);
                                    } else {
                                        setFileList(currentEntry.path + File.separator + list.getItemAtPosition(position).toString());
                                    }

                                }
                            });
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
       super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // complete the authentication
                mDBApi.getSession().finishAuthentication();
                String accessToken = mDBApi.getSession()
                        .getOAuth2AccessToken();
                setFileList("/");
            } catch (IllegalStateException e) {
            }

        }


    }

    @Override
    public void onBackPressed() {
        if (currentEntry== null){
            super.onBackPressed();
        }
        if (currentEntry!=null&&!currentEntry.path.equals("/")) {
            setFileList(currentEntry.parentPath());
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
        if (list==null){
            Toast.makeText(getApplicationContext(),"Please accept Authentication",Toast.LENGTH_LONG).show();
            onBackPressed();
        } else {
            finished = false;
            uploadTask = new Upload();
            uploadTask.execute(null, null, null);
        }
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