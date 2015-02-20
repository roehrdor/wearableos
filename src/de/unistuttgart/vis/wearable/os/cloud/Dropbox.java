package de.unistuttgart.vis.wearable.os.cloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import de.unistuttgart.vis.wearable.os.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Martin on 18.02.2015.
 */
public class Dropbox extends Activity {
    // TODO remove when releasing source code
    private static final String APP_KEY = "hfo4su0wieruugc";
    private static final String APP_SECRET = "z82qqqj2jegxtbp";
    private static final Session.AccessType ACCESS_TYPE = Session.AccessType.AUTO;
    private Context context;
    private boolean upload = false;
    private Upload uploadTask;
    private File tmp;
    private Switch mySwitch;
    private String key;
    private boolean finished = false;
    private FileInputStream inputStream;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    private class Upload extends AsyncTask<Void, Long, Boolean> {
        private final ProgressDialog progressDialog;
        private DropboxAPI.UploadRequest request = null;

        public Upload() {
            // create Progress Dialog to display the progress of upload
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
                            upload = false;
                            new cancelUpload().execute(null, null, null);
                        }
                    });
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... params) {


            // Database Path
            tmp = new File(Dropbox.this.getFilesDir().getAbsolutePath()+File.separator+"tmp");

            if (mySwitch.isChecked()) {
                Archiver.createEncryptedArchiveFile(key, tmp);
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
                    // set request for upload to Dropbox
                    request = mDBApi.putFileOverwriteRequest("/GarmentOS/"
                                    + "NAME", inputStream, tmp.length(),
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
                        // start upload
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
            upload = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);
        context = this.getBaseContext();
        mySwitch = (Switch) findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AlertDialog.Builder alert = new AlertDialog.Builder(Dropbox.this);

                    alert.setTitle("Please enter password:");
                    final EditText input = new EditText(Dropbox.this);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            key = input.getText().toString();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    });

                    alert.show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (upload) {
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    // complete the authentication
                    mDBApi.getSession().finishAuthentication();
                    String accessToken = mDBApi.getSession()
                            .getOAuth2AccessToken();

                } catch (IllegalStateException e) {
                }
            }
            // start upload
            uploadTask = new Upload();
            uploadTask.execute(null, null, null);

        }
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
            upload = true;
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