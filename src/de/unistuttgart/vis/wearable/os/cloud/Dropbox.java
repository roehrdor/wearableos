package de.unistuttgart.vis.wearable.os.cloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.*;

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
    private boolean connectionEstablished = false;
    private Upload uploadTask;
    private Download downloadTask;
    private File tmp;
    private boolean finished = false;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;


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
                            connectionEstablished = false;
                            new cancelUpload().execute(null, null, null);
                        }
                    });
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... params) {


            // Database Path
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
                    request = mDBApi.putFileOverwriteRequest("/Garment-OS/"
                                    + "gos_sensors.zip", inputStream, tmp.length(),
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
            connectionEstablished = false;
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
                    } else {
                        Toast.makeText(context,
                                "File upload failed",
                                Toast.LENGTH_SHORT).show();
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
            DropboxAPI.Entry data= null;
            try {
                 data = mDBApi.metadata("/Garment-OS/"
                        + "gos_sensors.zip", 1, null, false, null);
            } catch (DropboxException e) {
                e.printStackTrace();
            }
            if (data==null||!data.fileName().equals("gos_sensors.zip")||data.isDeleted) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"No SensorData available! Please first upload SensorData",Toast.LENGTH_LONG).show();

                    }

                });
                return null;
            }
            // TODO add right path
            file = new File("tmp");
            outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/Garment-OS/"
                                + "gos_sensors.zip", null,
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
            connectionEstablished = false;

            // print result
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (finished) {
                        Toast.makeText(context,
                                "File import finished",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,
                                "File import failed",
                                Toast.LENGTH_SHORT).show();
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
                            connectionEstablished = false;
                            new cancelDownload().execute(null, null, null);
                        }
                    });
            progressDialog.show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);
        context = this.getBaseContext();
        button = (Button) findViewById(R.id.button1);
        if(!getIntent().getBooleanExtra("isExport",false)) {
            button.setText("Import SensorData");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connectionEstablished) {
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    // complete the authentication
                    mDBApi.getSession().finishAuthentication();
                    String accessToken = mDBApi.getSession()
                            .getOAuth2AccessToken();

                } catch (IllegalStateException e) {
                }
                // start
                if (getIntent().getBooleanExtra("isExport",false)) {
                    uploadTask = new Upload();
                    uploadTask.execute(null, null, null);
                } else {
                    downloadTask = new Download();
                    downloadTask.execute(null,null,null);
                }
            }


        }
        finished = false;
    }

    /**
     * starts session for authentication on dropbox
     *
     * @param view
     */
    public void upload(View view) {

        if (internetAvailable()) {
            // register keys
            AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(appKeys,
                    ACCESS_TYPE);
            connectionEstablished = true;
            // start Dropbox authentication
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            mDBApi.getSession().startOAuth2Authentication(Dropbox.this);

        } else {
            Toast.makeText(getBaseContext(),
                    "Please activate WIFI or mobile-data connection",
                    Toast.LENGTH_SHORT).show();
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