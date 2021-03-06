package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.microsoft.live.*;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.cloud.Archiver;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Activity to provide functionality to upload or download an archive file to or
 * from One Drive
 *
 */
public class OneDrive extends Activity {
    private LiveAuthClient auth = null;
    public static LiveConnectClient client = null;
    private static Context context = null;
    private static String key = "";
    private boolean isExport = true;
    private ListView oneDriveFolderListView = null;
    private ArrayList<JSONObject> childrenList = null;
    private JSONObject parentDirectory = null;
    private JSONComparator jsonComparator= null;
    private OneDriveAdapter adapter = null;
    private ArrayList<String> directoryList = null;
    private TextView currentDirectoryTextView = null;
    private ProgressDialog progressDialog = null;
    private String futurePath = "";
    private static boolean cancelRequest = false;


    private static synchronized void setCancelRequest(boolean newStatus){
        cancelRequest = newStatus;
    }
    private static synchronized boolean getCancelRequest(){
        return cancelRequest;
    }

    @Override
    public void onBackPressed() {
        // Case when activity is started and no directory was selected
        if(internetAvailable()){
            if(parentDirectory == null||(parentDirectory.optString(Miscellaneous.PARENT_ID).equals(parentDirectory.optString(Miscellaneous.ID)))||parentDirectory.isNull(Miscellaneous.PARENT_ID)||client==null){
                finish();
            }
            // Case where elements of ListView where selected
            else {
                progressDialog = new ProgressDialog(getMainContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading parent directory...");
                if(directoryList.size()>0){
                    directoryList.remove(directoryList.size()-1);
                    if(directoryList.size()==0){

                        futurePath = "/";

                    }
                    else{
                        String pathString ="";
                        for(String currentDirectory:directoryList){
                            pathString+="/"+currentDirectory;
                        }
                        futurePath = pathString;

                    }
                }
                else{
                    futurePath = "/";

                }
                progressDialog.show();
                getConnectClient().getAsync(parentDirectory.optString(Miscellaneous.PARENT_ID), new LiveOperationListener() {
                    JSONObject grandParentJsonObject = null;
                    @Override
                    public void onComplete(LiveOperation operation) {

                        grandParentJsonObject = operation.getResult();
                        parentDirectory = grandParentJsonObject;
                        getArchiveList(parentDirectory);

                    }

                    @Override
                    public void onError(LiveOperationException exception, LiveOperation operation) {
                        progressDialog.dismiss();
                        Toast.makeText(getMainContext(),"Couldn't load parent directory list due to connectivity issues",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            }
        }
        else{
            Toast.makeText(getMainContext(),"Please enable WiFi or mobile data",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(internetAvailable()){
            context = OneDrive.this;
            directoryList = new ArrayList<String>();
            this.auth = new LiveAuthClient(this, Miscellaneous.CLIENT_ID);
            setContentView(getIntent().getBooleanExtra("isExport",false)?R.layout.activity_cloud_export:R.layout.activity_cloud_import);
            oneDriveFolderListView = (ListView)findViewById(R.id.listViewFileChooser);
            currentDirectoryTextView = (TextView)findViewById(R.id.textView_current_directory);
            if(!getIntent().getBooleanExtra("isExport",false)) {

                isExport = false;
            }
            key =getIntent().getBooleanExtra("encrypted",false)?getIntent().getStringExtra("key"):"";
            jsonComparator = new JSONComparator();
            childrenList = new ArrayList<JSONObject>();

            adapter = new OneDriveAdapter(getMainContext(), childrenList);
            oneDriveFolderListView.setAdapter(adapter);
            oneDriveFolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /*
                 * Checks if the user wants to browse the parent directory or a
                 * child directory
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    JSONObject curJSONObject = (JSONObject) oneDriveFolderListView.getItemAtPosition(position);

                    if (isExport) {

                        if (curJSONObject.optString(Miscellaneous.TYPE).equals("folder")) {
                            progressDialog = new ProgressDialog(getMainContext());
                            progressDialog.setMessage("Loading directory list of "+curJSONObject.optString(Miscellaneous.NAME)+"...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            directoryList.add(curJSONObject.optString(Miscellaneous.NAME));
                            String pathString ="";
                            for(String currentDirectory:directoryList){
                                pathString +="/"+currentDirectory;}
                            futurePath = pathString;

                            parentDirectory = curJSONObject;
                            getArchiveList(parentDirectory);
                        }
                    } else {
                        if (curJSONObject.optString(Miscellaneous.TYPE).equals("folder")) {
                            progressDialog = new ProgressDialog(getMainContext());
                            progressDialog.setMessage("Loading directory list of "+curJSONObject.optString(Miscellaneous.NAME)+"...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            directoryList.add(curJSONObject.optString(Miscellaneous.NAME));
                            String pathString ="";
                            for(String currentDirectory:directoryList){
                                pathString +="/"+currentDirectory;}
                            futurePath = pathString;

                            parentDirectory = curJSONObject;
                            getArchiveList(parentDirectory);
                        } else {
                            startFileImport(curJSONObject);
                        }
                    }
                }
            });

        }
        if(!internetAvailable()){
            Toast.makeText(getMainContext(),"Please enable WiFi or mobile data",Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void getArchiveList(final JSONObject currentParentDirectory) {
        getConnectClient().getAsync(currentParentDirectory==null?"me/skydrive/files":
                currentParentDirectory.optString(Miscellaneous.ID)+"/files", new LiveOperationListener() {
            @Override
            public void onComplete(LiveOperation operation) {
                if(currentParentDirectory == null){
                    getConnectClient().getAsync("me/skydrive", new LiveOperationListener() {
                        @Override
                        public void onComplete(LiveOperation operation) {
                            parentDirectory = operation.getResult();

                        }

                        @Override
                        public void onError(LiveOperationException exception, LiveOperation operation) {

                        }
                    });
                    currentDirectoryTextView.setText("/");
                }
                else{
                    currentDirectoryTextView.setText(futurePath);
                }
                childrenList.clear();
                JSONObject currentJsonObject;
                JSONArray fileListArray = operation.getResult().optJSONArray(Miscellaneous.DATA);
                for (int i = 0; i < fileListArray.length(); i++) {
                    try {
                        currentJsonObject = fileListArray.getJSONObject(i);
                        if (currentJsonObject.optString(Miscellaneous.TYPE).equals("folder")) {
                            childrenList.add(currentJsonObject);
                        } else if (currentJsonObject.optString(Miscellaneous.TYPE).equals("file")
                                && currentJsonObject.optString(Miscellaneous.NAME).endsWith(".zip")) {
                            childrenList.add(currentJsonObject);
                        }


                    } catch (JSONException e) {

                    }
                }

                Collections.sort(childrenList,jsonComparator);
                adapter.notifyDataSetChanged();
                if(progressDialog!=null){
                    progressDialog.dismiss();}
            }

            @Override
            public void onError(LiveOperationException exception, LiveOperation operation) {
                progressDialog.dismiss();
                Toast.makeText(getMainContext(),"Couldn't load parent directory list due to connectivity issues",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startFileImport(JSONObject curJSONObject) {
        new OneDriveAsyncDownloadTask().execute(curJSONObject);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (internetAvailable()) {
            if(getConnectClient()==null) {
                auth.login(this, Arrays.asList(Miscellaneous.SCOPES),
                        new LiveAuthListener() {
                            @Override
                            public void onAuthError(LiveAuthException exception, Object userState) {
                                if(exception.getError().toString().length()==0){
                                    client = null;
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Couldn't log in to One Drive, Cause: "+exception.getError(),
                                            Toast.LENGTH_SHORT).show();
                                    client = null;
                                    finish();}

                            }

                            @Override
                            public void onAuthComplete(LiveStatus status,
                                                       LiveConnectSession session, Object userState) {
                                if (status == LiveStatus.CONNECTED) {
                                    Toast.makeText(getApplicationContext(),
                                            "Signed in to One Drive", Toast.LENGTH_SHORT).show();
                                    client = new LiveConnectClient(session);
                                    progressDialog = new ProgressDialog(getMainContext());
                                    progressDialog.setMessage("Loading root directory...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    getArchiveList(parentDirectory);

                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Couldn't connect with One Drive",
                                            Toast.LENGTH_SHORT).show();
                                    client = null;
                                }
                            }
                        });
            }
            else{
                if (getConnectClient() != null
                        && !getConnectClient().getSession().isExpired()) {
                    getArchiveList(parentDirectory);
                }
            }
        }
        else{
            Toast.makeText(getMainContext(),"No internet available, activate WiFi oder mobile data",Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

//    @Override
//    protected void onStop() {
//        // suboptimal soluti
// on to handle standby
//        if (client != null) {
//            client = null;
//        }
//        super.onStop();
//    }

    @Override
    protected void onResume() {
        context = OneDrive.this;
        super.onResume();
    }

    /**
     * @return the LiveConnectClient to initiate cloud operations at One Drive
     */
    public static LiveConnectClient getConnectClient() {
        return client;
    }

    public static Context getMainContext() {

        return context;
    }

    /**
     * @return the availability of a internet connection as boolean
     */
    public boolean internetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



    /**
     * method to trigger the upload process of the sensor archive
     */
    public void upload(View view) {

        if (internetAvailable()) {

            if (getConnectClient() != null
                    && !getConnectClient().getSession().isExpired()) {

                new OneDriveAsyncUploadTask(getKey()).execute(parentDirectory);

            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "No internet connection available, please activate wifi \nor mobile data",
                        Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        }
        else{
            Toast.makeText(
                    getApplicationContext(),
                    "No internet connection available, please activate wifi \nor mobile data",
                    Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    public static String getKey(){
        return key;
    }
    private class OneDriveAsyncDownloadTask extends AsyncTask<JSONObject, String, Long> {

        private final ProgressDialog progressDialog;
        private final ProgressDialog waitForCancelDialog;
        private JSONObject downloadJsonObject = null;
        File downloadDestination = null;


        public OneDriveAsyncDownloadTask() {

            // create Progress Dialog to display the progress of upload
            waitForCancelDialog = new ProgressDialog(OneDrive.getMainContext());
            waitForCancelDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            waitForCancelDialog.setCancelable(false);
            progressDialog = new ProgressDialog(getMainContext());
            progressDialog.setMax(100);
            progressDialog.setMessage("Downloading " + Miscellaneous.getCloudArchiveName());
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

                                    setCancelStatus(true);
                                    waitForCancelDialog.setMessage("Cancelling the upload...");
                                    waitForCancelDialog.show();
                                }
                            };
                            handler.post(cancel);

                        }
                    });
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(OneDrive.getMainContext(), values[0], Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }

        @Override
        protected Long doInBackground(JSONObject... params) {
            this.downloadJsonObject = params[0];

            // The required directories are created on the
            // internal SDCard

            this.downloadDestination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"gos_sensors.zip");
            if(downloadDestination.exists()){
                downloadDestination.delete();
            }
            try {
                downloadDestination.createNewFile();
            } catch (IOException e) {
                publishProgress("Couldn't create temporary archive file");
                finish();
            }
            // The download of the archive file to the created
            // directory is initiated
            OneDrive.getConnectClient().downloadAsync(
                    downloadJsonObject.optString(Miscellaneous.ID) + "/content", downloadDestination,
                    new LiveDownloadOperationListener() {

                        @Override
                        public void onDownloadProgress(int totalBytes, int bytesRemaining,
                                                       LiveDownloadOperation operation) {

                            progressDialog
                                    .setProgress((int) (((float) (totalBytes - bytesRemaining) / (float) (totalBytes)) * 100));

                            if (getCancelRequest()) {

                                if(progressDialog!=null&&progressDialog.isShowing()){
                                    progressDialog.dismiss();}

                                operation.cancel();
                                setCancelRequest(false);
                                if(waitForCancelDialog.isShowing()){
                                    waitForCancelDialog.dismiss();
                                }
                                downloadDestination.delete();
                                publishProgress("Download cancelled");
                                finish();
                            }
                        }

                        @Override
                        public void onDownloadFailed(LiveOperationException exception,
                                                     LiveDownloadOperation operation) {
                            operation.cancel();
                            downloadDestination.delete();
                            publishProgress("Download cancelled");
                            if(progressDialog!=null&&progressDialog.isShowing()){
                                progressDialog.dismiss();}
                            if(waitForCancelDialog!=null&&waitForCancelDialog.isShowing()){
                                waitForCancelDialog.dismiss();
                            }
                            setCancelRequest(false);
                            finish();

                        }

                        @Override
                        public void onDownloadCompleted(LiveDownloadOperation operation) {

                            if (!Archiver.notEncryptedGOSFile(downloadDestination)) {

                                AlertDialog.Builder alert = new AlertDialog.Builder(OneDrive.this);
                                alert.setTitle("Please enter password:");
                                final EditText input = new EditText(OneDrive.this);
                                alert.setView(input);

                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {


                                        String key = input.getText().toString();
                                        int value = Archiver.unpackEncryptedFile(key, downloadDestination);
                                        switch (value) {
                                            case Constants.UNPACK_NO_ERROR:
                                                progressDialog.dismiss();
                                                Toast.makeText(context,
                                                        "File import finished",
                                                        Toast.LENGTH_SHORT).show();
                                                downloadDestination.delete();
                                                break;

                                            case Constants.UNPACK_INVALID_FILE:
                                                progressDialog.dismiss();
                                                Toast.makeText(context,
                                                        "Invalid File",
                                                        Toast.LENGTH_SHORT).show();
                                                downloadDestination.delete();
                                                break;

                                            case Constants.UNPACK_EXTRACTING_FAILED:
                                                progressDialog.dismiss();
                                                Toast.makeText(context,
                                                        "Extracting failed",
                                                        Toast.LENGTH_SHORT).show();
                                                downloadDestination.delete();
                                               break;

                                            case Constants.UNPACK_WRONG_KEY:
                                                progressDialog.dismiss();
                                                Toast.makeText(context,
                                                        "Wrong Password",
                                                        Toast.LENGTH_SHORT).show();
                                                downloadDestination.delete();
                                                break;


                                        }
                                        finish();
                                    }
                                });

                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        downloadDestination.delete();
                                        finish();
                                    }
                                });
                                alert.setCancelable(false);
                                alert.show();
                            } else {

                                int value = APIFunctions.unpackArchiveFile(downloadDestination);

                                switch (value) {
                                    case Constants.UNPACK_NO_ERROR:
                                       progressDialog.dismiss();
                                        Toast.makeText(context,
                                                "File import finished",
                                                Toast.LENGTH_SHORT).show();
                                        downloadDestination.delete();
                                        break;

                                    case Constants.UNPACK_INVALID_FILE:
                                        progressDialog.dismiss();
                                        Toast.makeText(context,
                                                "Invalid File",
                                                Toast.LENGTH_SHORT).show();
                                        downloadDestination.delete();
                                       break;

                                    case Constants.UNPACK_EXTRACTING_FAILED:
                                        progressDialog.dismiss();
                                        Toast.makeText(context,
                                                "Extracting failed",
                                                Toast.LENGTH_SHORT).show();
                                        downloadDestination.delete();
                                        break;

                                    case Constants.UNPACK_WRONG_KEY:
                                        progressDialog.dismiss();
                                        Toast.makeText(context,
                                                "Wrong Password",
                                                Toast.LENGTH_SHORT).show();
                                        downloadDestination.delete();
                                        break;


                                }
                            }
                          finish();
                        }

                    });
            return null;

        }
    }
    private class OneDriveAsyncUploadTask extends AsyncTask<JSONObject, String, Boolean> {
        File file = null;
        private String password;
        private final ProgressDialog progressDialog;
        private JSONObject uploadJsonObject = null;
        private final ProgressDialog waitForCancelDialog;

        public OneDriveAsyncUploadTask(String password) {
            this.password = password;
            // create Progress Dialog to display the progress of upload

            waitForCancelDialog = new ProgressDialog(OneDrive.getMainContext());
            waitForCancelDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            waitForCancelDialog.setMessage("Cancelling the upload...");
            waitForCancelDialog.setCancelable(false);

            progressDialog = new ProgressDialog(OneDrive.getMainContext());
            progressDialog.setMax(100);
            progressDialog.setMessage("Uploading " +Miscellaneous.getCloudArchiveName());
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

                                    setCancelStatus(true);
                                    waitForCancelDialog.show();
                                }
                            };
                            handler.post(cancel);

                        }
                    });
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(OneDrive.getMainContext(), values[0], Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {

            this.uploadJsonObject = params[0];

            file = new File(Environment.getExternalStorageDirectory()+File.separator+Miscellaneous.getCloudArchiveNameNoSuffix());

            if(password.equals("")){
                if(file.exists()){
                    file.delete();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    publishProgress("Couldn't create temporary archive file");
                    finish();
                }
                Archiver.createArchiveFile(file);
            }
            else{
                if(file.exists()){
                    file.delete();
                }
                if(password.equals("")){
                    Archiver.createArchiveFile(file);
                }else{
                    Archiver.createEncryptedArchiveFile(password,file);
                }

            }

            OneDrive.getConnectClient().uploadAsync(
                    uploadJsonObject.optString(Miscellaneous.UPLOAD_LOCATION),Miscellaneous.getCloudArchiveName(), file,
                    OverwriteOption.Overwrite, new LiveUploadOperationListener() {

                        @Override
                        public void onUploadProgress(int totalBytes, int bytesRemaining, LiveOperation arg2) {

                            if (getCancelRequest()) {

                                if(progressDialog!=null&&progressDialog.isShowing()){
                                    progressDialog.dismiss();}

                                arg2.cancel();
                                setCancelRequest(false);
                                if(waitForCancelDialog.isShowing()){
                                    waitForCancelDialog.dismiss();
                                }
                                file.delete();
                                publishProgress("Upload cancelled");
                                finish();
                            }
                            progressDialog
                                    .setProgress((int) (((float) (totalBytes - bytesRemaining) / (float) (totalBytes)) * 100));

                        }

                        @Override
                        public void onUploadFailed(LiveOperationException arg0, LiveOperation arg1) {
                            arg1.cancel();
                            file.delete();
                            publishProgress("Upload cancelled");
                            if(progressDialog!=null&&progressDialog.isShowing()){
                                progressDialog.dismiss();}
                            if(waitForCancelDialog!=null&&waitForCancelDialog.isShowing()){
                                waitForCancelDialog.dismiss();
                            }
                            setCancelRequest(false);
                            finish();

                        }

                        @Override
                        public void onUploadCompleted(LiveOperation arg0) {
                            file.delete();
                            publishProgress("Successfully uploaded sensor archive");
                            if(progressDialog!=null&&progressDialog.isShowing()){
                                progressDialog.dismiss();}
                            if(waitForCancelDialog!=null&&waitForCancelDialog.isShowing()){
                                waitForCancelDialog.dismiss();
                            }
                            finish();
                        }

                    }, null);
            return true;

        }
    }
    private static synchronized boolean getCancelStatus(){
        return cancelRequest;
    }
    private static synchronized void setCancelStatus(boolean newStatus){
        cancelRequest = newStatus;
    }


}
