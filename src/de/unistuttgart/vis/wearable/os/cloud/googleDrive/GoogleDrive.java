package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.cloud.Archiver;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;
import de.unistuttgart.vis.wearable.os.utils.Constants;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * Activity to provide functionality to upload or download an archive file to or
 * from Google Drive
 *
 */
public class GoogleDrive extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static Context context;
    private static DriveFolder defaultCloudArchiveFolder;
    private static char mode = 'n';
    private static String key ="";
    private boolean isExport = true;
    private boolean isConnected = false;
    private static DriveId currentDirectoryId = null;
    private ListView googleDriveFolderListView = null;
    private ArrayList<Metadata> metadataArrayList = null;
    private GoogleDriveAdapter adapter = null;
    // Thanks to very limited access for developers this is necessary to be able to get the parent directory,
    // updates of directory contents will be ignored
    private Stack<DriveId> directoryHistory = null;
    private Stack<String>directoryNameHistory = null;
    private DriveFile currentCloudDBFile = null;
    private float currentCloudArchiveFileSize = 0.0f;
    private MetadataBuffer fileListBuffer = null;
    private ProgressDialog progressDialog= null;
    private String currentPath = "";
    private String futurePath ="";
    private TextView currentPathTextView = null;

    /**
     * Google API client, luckily it only stores the local information about the
     * state of the Google Drive, so every time a change is made online,
     * requestSync has to be executed
     */
    private static GoogleApiClient mGoogleApiClient = null;

    public static DriveFolder getDefaultCloudArchiveFolder() {
        return defaultCloudArchiveFolder;
    }

    public static void setDefaultCloudArchiveFolder(DriveFolder defaultCloudArchiveFolder) {
        GoogleDrive.defaultCloudArchiveFolder = defaultCloudArchiveFolder;
    }

    @Override
    public void onBackPressed() {
        if(internetAvailable()){
        if(currentDirectoryId==null||currentDirectoryId.equals(Drive.DriveApi.getRootFolder(getGoogleApiClient()).getDriveId())
                ||directoryHistory.size()==0||directoryNameHistory.size()==0){
            if(fileListBuffer!=null&&!fileListBuffer.isClosed()){
                fileListBuffer.release();
            }
        super.onBackPressed();}
        else {
            currentDirectoryId = directoryHistory.pop();
            futurePath = directoryNameHistory.pop();
            progressDialog = new ProgressDialog(getMainContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading parent directory...");
            progressDialog.show();
            getArchiveList(currentDirectoryId);
        }}
        else{
            Toast.makeText(getMainContext(),"Please enable WiFi or mobile data",Toast.LENGTH_SHORT).show();
        }
    }

    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public void setAdapter(GoogleDriveAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        directoryHistory = new Stack<DriveId>();
        directoryNameHistory = new Stack<String>();
        metadataArrayList = new ArrayList<Metadata>();
        isExport = getIntent().getBooleanExtra("isExport",false);
        key = getIntent().getStringExtra("key")==null?"":getIntent().getStringExtra("key");

        setContentView(getIntent().getBooleanExtra("isExport",false)?R.layout.activity_cloud_export:R.layout.activity_cloud_import);
        currentPathTextView = (TextView)findViewById(R.id.textView_current_directory);
        googleDriveFolderListView = (ListView)findViewById(R.id.listViewFileChooser);

        setGoogleApiClient(new GoogleApiClient.Builder(this)
                .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build());
        context = this;

        if(internetAvailable()){
            adapter = new GoogleDriveAdapter(this,metadataArrayList);
            googleDriveFolderListView.setAdapter(adapter);
            googleDriveFolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /*
                 * Checks if the user wants to browse the parent directory or a
                 * child directory
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Metadata curMetadata = (Metadata) googleDriveFolderListView.getItemAtPosition(position);
                    if (isExport) {


                        if (curMetadata.isFolder()) {
                            directoryHistory.push(currentDirectoryId);
                            futurePath = curMetadata.getTitle();
                            currentDirectoryId = curMetadata.getDriveId();
                            progressDialog = new ProgressDialog(getMainContext());
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading directories of "+curMetadata.getTitle());
                            progressDialog.show();
                            getArchiveList(currentDirectoryId);
                        }
                    } else {

                        if (curMetadata.isFolder()) {
                            directoryHistory.push(currentDirectoryId);
                            futurePath = curMetadata.getTitle();
                            currentDirectoryId = curMetadata.getDriveId();
                            progressDialog = new ProgressDialog(getMainContext());
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading directories of "+curMetadata.getTitle());
                            progressDialog.show();
                            getArchiveList(currentDirectoryId);
                        } else {

                            startFileImport(curMetadata.getDriveId());
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

    public static Context getMainContext() {

        return context;
    }

    /**
     * method to return the network status represented by a boolean
     */
    public boolean internetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        if (arg0.hasResolution()) {
            try {
                arg0.startResolutionForResult(this,
                        Miscellaneous.REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(arg0.getErrorCode(), this, 0)
                    .show();
        }
        Toast.makeText(this, "Connection not successful, please select an account", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onConnected(Bundle arg0) {

        Toast.makeText(this, "In Google Drive", Toast.LENGTH_SHORT).show();
        Drive.DriveApi.requestSync(getGoogleApiClient()).setResultCallback(
                new ResultCallback<Status>() {

                    @Override
                    public void onResult(Status arg0) {
                        if(isFileRequestValid(arg0)==1){
                            Toast.makeText(
                                    getBaseContext(),
                                    "Synchronisation with Google Drive successful",
                                    Toast.LENGTH_SHORT).show();
                            currentDirectoryId = Drive.DriveApi.getRootFolder(getGoogleApiClient()).getDriveId();
                            futurePath = "/";
                            searchForDefaultDirectory();
                        }
                        else if(isFileRequestValid(arg0)==0){
                            Toast.makeText(
                                    getBaseContext(),
                                    "Synchronisation with Google Drive successful, due to technical limitations the following list may be deprecated",
                                    Toast.LENGTH_SHORT).show();

                            currentDirectoryId = Drive.DriveApi.getRootFolder(getGoogleApiClient()).getDriveId();
                            futurePath="/";
                            searchForDefaultDirectory();
                        }
                        else{
                           Toast.makeText(
                                    getBaseContext(),
                                    "Synchronisation with Google Drive failed, "
                                            + "\nlogging in again is necessary\n"+arg0.getStatusMessage()+"\n"+arg0.getStatusCode(),
                                    Toast.LENGTH_SHORT).show();
                            setGoogleApiClient(null);}
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    @Override
    protected void onStop() {
        if (getGoogleApiClient() != null) {
            getGoogleApiClient().disconnect();
            setGoogleApiClient(null);
        }

        super.onStop();
    }

    public static char getMode() {
        return mode;
    }

    public static void setMode(char mode) {
        GoogleDrive.mode = mode;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getGoogleApiClient()==null) {
            if (internetAvailable()) {

                setGoogleApiClient(new GoogleApiClient.Builder(this)
                        .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build());
                getGoogleApiClient().connect();
            }
            else{
                Toast.makeText(getMainContext(),"No internet available, activate WiFi oder mobile data",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            if (internetAvailable()) {

                if(!getGoogleApiClient().isConnected()){
                    getGoogleApiClient().connect();
                }
            }
            else{
                Toast.makeText(getMainContext(),"No internet available, activate WiFi oder mobile data",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == Miscellaneous.SIGN_IN_REQUEST_CODE_1 || requestCode == Miscellaneous.SIGN_IN_REQUEST_CODE_2) && (resultCode == Miscellaneous.SIGN_IN__SUCCESSFUL_RESULT_CODE_1||resultCode == Miscellaneous.SIGN_IN__SUCCESSFUL_RESULT_CODE_2)) {
            if (!isConnected) {

                isConnected = true;
                getGoogleApiClient().connect();
            } else {
                Toast.makeText(getMainContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                onBackPressed();

            }
        }
        else {
            switch (resultCode){
                case Miscellaneous.SIGN_IN__CANCELLED_RESULT_CODE_1:{
                    Toast.makeText(getMainContext(),isExport?"Export cancelled":"Import cancelled",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public void setMetadataArrayList(ArrayList<Metadata> metadataArrayList) {
        this.metadataArrayList = metadataArrayList;
    }

    public void loadDriveFolderContents(DriveId parentDirectory) {

    }
    public void startFileImport(DriveId zipFileId){

        final DriveFile cloudArchiveFile = Drive.DriveApi.getFile(getGoogleApiClient(), zipFileId);
        cloudArchiveFile.getMetadata(getGoogleApiClient()).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
            @Override
            public void onResult(DriveResource.MetadataResult metadataResult) {
                currentCloudArchiveFileSize = metadataResult.getMetadata().getFileSize();
                cloudArchiveFile.open(GoogleDrive.getGoogleApiClient(),
                        DriveFile.MODE_READ_ONLY, null).setResultCallback(readExistingFileCallback);
            }
        });
    }

    public void startFileExport(DriveId targetDirectoryId){

    }
    public void upload(View view){
        Query query = new Query.Builder()
                .addFilter(
                        Filters.and(
                                Filters.eq(
                                        SearchableField.TITLE,
                                        Miscellaneous.getCloudArchiveName()+".zip"),
                                Filters.eq(
                                        SearchableField.TRASHED,
                                        false),
                                Filters.eq(
                                        SearchableField.MIME_TYPE,
                                        Miscellaneous.getZipMimeType())
                        ))
                .build();

        Drive.DriveApi.getFolder(getGoogleApiClient(),currentDirectoryId).queryChildren(
                GoogleDrive
                        .getGoogleApiClient(),
                query).setResultCallback(
                fileQueryResultCallback);
        }
    protected void getArchiveList(DriveId parentDirectory){
        final DriveId curParentDirectory = parentDirectory;
        final boolean resultAvailable = false;

        Drive.DriveApi.requestSync(
                GoogleDrive.getGoogleApiClient()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if(isFileRequestValid(status)>=0){
                    DriveFolder parentDriveFolder = Drive.DriveApi.getFolder(
                            GoogleDrive.getGoogleApiClient(),
                            curParentDirectory);
                    Query archiveConstraint = new Query.Builder().addFilter(
                            Filters.eq(SearchableField.TRASHED, false))
                            .build();
                    parentDriveFolder.queryChildren(GoogleDrive.getGoogleApiClient(), archiveConstraint)
                            .setResultCallback(
                                    new ResultCallback<DriveApi.MetadataBufferResult>() {
                                        @Override
                                        public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
                                            try {

                                                if (isFileRequestValid(metadataBufferResult.getStatus()) >= 0) {
                                                    metadataArrayList.clear();
                                                    if (fileListBuffer != null && !fileListBuffer.isClosed()) {
                                                        fileListBuffer.release();
                                                    }

                                                    fileListBuffer = metadataBufferResult.getMetadataBuffer();


                                                    for (Metadata currentFolder : metadataBufferResult.getMetadataBuffer()) {
                                                        if (currentFolder.isFolder() || (!currentFolder.isFolder() && currentFolder.getTitle().endsWith(".zip")) ||
                                                                (!currentFolder.isFolder() && currentFolder.getMimeType().equals(Miscellaneous.getZipMimeType())))
                                                            metadataArrayList.add(currentFolder);
                                                    }
                                                    Collections.sort(metadataArrayList, new MetadataComparator());
                                                    if(!currentPath.equals("")){
                                                    directoryNameHistory.push(currentPath);}
                                                    currentPath = futurePath;
                                                    stopLoadingScreen(metadataBufferResult.getStatus().getStatusCode());
                                                    adapter.notifyDataSetChanged();
                                                    currentPathTextView.setText(currentPath);
                                                } else {
                                                    stopLoadingScreen(metadataBufferResult.getStatus().getStatusCode());
                                                    Toast.makeText(getMainContext(), "Couldn't get file list", Toast.LENGTH_SHORT).show();
                                                }
                                            } finally {
                                                if (metadataBufferResult.getMetadataBuffer() != null) {

                                                }
                                            }
                                        }
                                    });
                }
                else{
                    stopLoadingScreen(status.getStatusCode());
                    Toast.makeText(getMainContext(),"Couldn't get file list due to connection problems.",Toast.LENGTH_SHORT).show();
                }
            }
                                                                                          }

        );
    }
public void searchForDefaultDirectory(){
    Query query = new Query.Builder().addFilter(
            Filters.and(Filters.eq(SearchableField.TITLE,
                            Miscellaneous.getCloudArchiveFolderName()), Filters.eq(
                            SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE),
                    Filters.eq(SearchableField.TRASHED, false)))
            .build();
    Drive.DriveApi.getFolder(getGoogleApiClient(),currentDirectoryId).queryChildren(getGoogleApiClient(),query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
        @Override
        public void onResult(final DriveApi.MetadataBufferResult metadataBufferResult) {
            try{
            if(metadataBufferResult.getStatus().isSuccess()){

                // Create default directory
                if(metadataBufferResult.getMetadataBuffer().getCount()==0){

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(Miscellaneous.getCloudArchiveFolderName())
                            .build();
                    Drive.DriveApi.getFolder(getGoogleApiClient(),currentDirectoryId).createFolder(getGoogleApiClient(),changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                        @Override
                        public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                            progressDialog = new ProgressDialog(getMainContext());
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading root Directory...");
                            progressDialog.show();
                            getArchiveList(currentDirectoryId);

                        }
                    });
                }
                else{
                    progressDialog = new ProgressDialog(getMainContext());
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Loading root Directory...");
                    progressDialog.show();
                    getArchiveList(currentDirectoryId);
                }
            }
        }
        finally {
                if (metadataBufferResult.getMetadataBuffer() != null) {
                    //metadataBufferResult.getMetadataBuffer().release();
                }
            }
        }
    });


}
    /**
     * Looks for the latest archive file to overwrite it with the local one,
     * though internal problems with google's drive api prevent a reliable
     * detection, through a previous call of the requestSync method this is
     * circumvented
     */
    private ResultCallback<DriveApi.MetadataBufferResult> fileQueryResultCallback = new ResultCallback<DriveApi.MetadataBufferResult>() {

        Metadata cloudFileMetaData = null;

        @Override
        public void onResult(DriveApi.MetadataBufferResult arg0) {

            if (!arg0.getStatus().isSuccess()) {
                Toast.makeText(
                        GoogleDrive.getMainContext(),
                        "Couldn't make a request for \n"
                                + "the latest sensor archive",
                        Toast.LENGTH_SHORT).show();
            } else {
                MetadataBuffer fileMetadataBuffer = null;

                try {
                    fileMetadataBuffer = arg0.getMetadataBuffer();

                    if (fileMetadataBuffer.getCount() > 0) {
                        cloudFileMetaData = Miscellaneous
                                .getLatestMetadata(fileMetadataBuffer);
                            currentCloudDBFile = Drive.DriveApi.getFile(getGoogleApiClient(),cloudFileMetaData.getDriveId());
                    }

                    if (cloudFileMetaData == null) {

                        // Already in archive folder of Google Drive, uploading file

                        Drive.DriveApi.newDriveContents(
                                GoogleDrive.getGoogleApiClient())
                                .setResultCallback(newDBCreateCallback);

                    } if(cloudFileMetaData!=null) {

                        final DriveFile cloudArchiveFile = currentCloudDBFile;
                        setCloudArchiveFile(cloudArchiveFile);
                        cloudArchiveFile.open(
                                GoogleDrive.getGoogleApiClient(),
                                DriveFile.MODE_WRITE_ONLY, null)
                                .setResultCallback(
                                        overwriteExistingFileCallback);

                    }

                } finally {
                    if (fileMetadataBuffer != null) {

                    }
                }
            }

        }

    };
    private final ResultCallback<DriveApi.DriveContentsResult> newDBCreateCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult arg0) {
            if (!arg0.getStatus().isSuccess()) {
                Toast.makeText(GoogleDrive.getMainContext(),
                        "Couldn't write file to google drive",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            final DriveContents cloudDBFolderContents = arg0.getDriveContents();


            new AsyncDriveFileUploadTask('u',key).execute(cloudDBFolderContents);

        }
    };
    private void setCloudArchiveFile(DriveFile currentDriveFile) {
        this.currentCloudDBFile = currentDriveFile;
    }
    /**
     * Overwrites contents of existing DriveFile via its associated contents and
     * the provided OutputStream where data of the new archive file is written
     * to
     */
    private final ResultCallback<DriveApi.DriveContentsResult> overwriteExistingFileCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult arg0) {

            if (!arg0.getStatus().isSuccess()) {

                return;
            }

            final DriveContents existingFileContents = arg0.getDriveContents();

            new AsyncDriveFileUploadTask('o', key).execute(existingFileContents);

        }

    };
    private class AsyncDriveFileUploadTask extends
            AsyncTask<DriveContents, String, Boolean> {
        private File file = null;
        private char mode = '_';
        private final ProgressDialog progressDialog;
        private boolean cancelRequest = false;

        public AsyncDriveFileUploadTask(char mode, String password) {
            this.mode = mode;
            // create Progress Dialog to display the progress of upload
            progressDialog = new ProgressDialog(
                    GoogleDrive.getMainContext());
            progressDialog.setMax(100);
            progressDialog
                    .setMessage("Uploading " + Miscellaneous.getCloudArchiveName());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            class AsyncUploadCancelTask extends
                                    AsyncTask<Void, Long, Boolean> {

                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    cancelRequest = true;

                                    return false;
                                }

                            }

                            new AsyncUploadCancelTask().execute(null, null,
                                    null);
                        }
                    });

            progressDialog.show();


        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            file.delete();


        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(GoogleDrive.getMainContext(), values[0],
                    Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(DriveContents... params) {
            // Archive path

            file = new File(getApplicationContext().getFilesDir().getAbsolutePath()+File.separator+Miscellaneous.getCloudArchiveName()+".zip");

            if(key.equals("")){
                if(file.exists()){
                    file.delete();
                }

                Archiver.createArchiveFile(file);
            }
            else{
                if(file.exists()){
                    file.delete();
                }

                Archiver.createEncryptedArchiveFile(key,file);
            }
            try {

                DriveContents driveFileContents = params[0];
                BufferedInputStream fileInputStream = new BufferedInputStream(
                        new FileInputStream(file));
                double totalBytes = file.length();
                double currentBytes = 0.0f;
                // Overwrite file.
                // TODO actually use a buffer to prevent overheating the device
                BufferedOutputStream fileOutputStream = new BufferedOutputStream(
                        driveFileContents.getOutputStream());

                int fileByte = 0;

                try {
                    while (fileByte != -1) {

                        fileByte = fileInputStream.read();

                        if (fileByte != -1) {
                            if ((!cancelRequest)) {
                                currentBytes++;

                                progressDialog
                                        .setProgress((int) (100 * (currentBytes / totalBytes)));

                                fileOutputStream.write(fileByte);

                            } else {
                                try {
                                    fileInputStream.close();
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                    driveFileContents.discard(GoogleDrive.getGoogleApiClient());
                                    if(progressDialog.isShowing()){
                                        progressDialog.dismiss();}
                                    publishProgress("Upload cancelled");
                                    break;
                                } catch (IOException e) {
                                    if(progressDialog.isShowing()){
                                        progressDialog.dismiss();}
                                    publishProgress("Upload cancelled");
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();}
                    publishProgress("Upload cancelled");

                } finally {
                    try {
                        fileInputStream.close();
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();}
                    } catch (IOException e) {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();}
                        publishProgress("Upload cancelled");

                    }
                }
                if (!cancelRequest) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();}


                    MetadataChangeSet fileUploadChangeSet = new MetadataChangeSet.Builder()
                            .setMimeType(Miscellaneous.getZipMimeType())
                            .setTitle(Miscellaneous.getCloudArchiveName()+".zip").build();
                    if (mode == 'u') {
                        Drive.DriveApi
                                .getFolder(
                                        GoogleDrive
                                                .getGoogleApiClient(),
                                        currentDirectoryId)
                                .createFile(
                                        GoogleDrive
                                                .getGoogleApiClient(),
                                        fileUploadChangeSet, driveFileContents)
                                .setResultCallback(
                                        afterFileCreationCallback);
                    } else {
                        driveFileContents.commit(GoogleDrive.getGoogleApiClient(), fileUploadChangeSet).setResultCallback(
                                afterFileOverWriteCallback);


                    }

                } else {
                    if (driveFileContents.getOutputStream() != null) {
                        driveFileContents.discard(GoogleDrive.getGoogleApiClient());
                    }
                }
            } catch (IOException e) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();}
                publishProgress("Upload cancelled");

                e.printStackTrace();
            }

            return null;

        }

    }
    private class AsyncDriveFileDownloadTask extends AsyncTask<DriveContents, String, Boolean> {

        private final ProgressDialog progressDialog;
        private boolean cancelRequest = false;
        File downloadDestination = null;
        private boolean finished = false;
        public AsyncDriveFileDownloadTask() {
            // create Progress Dialog to display the progress of upload
            progressDialog = new ProgressDialog(GoogleDrive.getMainContext());
            progressDialog.setMax(100);
            progressDialog.setMessage("Downloading " + Miscellaneous.getCloudArchiveName());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            class AsyncDownloadCancelTask extends AsyncTask<Void, Long, Boolean> {

                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    cancelRequest = true;
                                    return false;
                                }

                            }

                            new AsyncDownloadCancelTask().execute(null, null, null);
                        }
                    });
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(DriveContents... params) {
            DriveContents existingFileContents = params[0];

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            double totalBytes = currentCloudArchiveFileSize;
            int currentBytes = 0;

            try {

                {
                    downloadDestination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"gos_sensors.zip");

                    BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(downloadDestination));

                    // Overwrite the local sensor files
                    BufferedInputStream contentInputStream = new BufferedInputStream(existingFileContents.getInputStream());

                    int streamStatus = 0;

                    try {
                        while (streamStatus != -1) {
                            streamStatus = contentInputStream.read(buffer, 0, bufferSize);
                            if (streamStatus != -1 && !cancelRequest) {
                                fileOutputStream.write(buffer, 0, streamStatus);
                                currentBytes+=streamStatus;
                                progressDialog.setProgress((int) ((currentBytes / totalBytes) * 100));

                                } else if (streamStatus != -1 && cancelRequest) {
                                contentInputStream.close();
                                fileOutputStream.close();

                                break;
                            }
                        }
                    } catch (IOException e) {

                        publishProgress("Couldn't access file...");

                    } finally {
                        try {

                            contentInputStream.close();
                            fileOutputStream.close();
                            publishProgress("Successfully downloaded file");


                            return true;

                        } catch (IOException e) {

                            publishProgress("Download failed...");
                            e.printStackTrace();

                        }
                    }

                }

            }
            catch (Exception e) {

                publishProgress("Couldn't access the file...");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(GoogleDrive.getMainContext(), values[0], Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();}
            if(result){
                if (!Archiver.notEncryptedGOSFile(downloadDestination)) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(GoogleDrive.this);
                    alert.setTitle("Please enter password:");
                    final EditText input = new EditText(GoogleDrive.this);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String key = input.getText().toString();
                            int value =Archiver.unpackEncryptedFile(key, downloadDestination);
                            switch (value) {
                                case Constants.UNPACK_NO_ERROR:
                                    Toast.makeText(context,
                                            "File import finished",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;
                                case Constants.UNPACK_INVALID_FILE:
                                    Toast.makeText(context,
                                            "Invalid File",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;
                                case Constants.UNPACK_EXTRACTING_FAILED:
                                    Toast.makeText(context,
                                            "Extracting failed",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;
                                case Constants.UNPACK_WRONG_KEY:
                                    Toast.makeText(context,
                                            "Wrong Password",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                    break;

                            }
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finished = false;
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
                            Toast.makeText(context,
                                    "File import finished",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case Constants.UNPACK_INVALID_FILE:
                            Toast.makeText(context,
                                    "Invalid File",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case Constants.UNPACK_EXTRACTING_FAILED:
                            Toast.makeText(context,
                                    "Extracting failed",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case Constants.UNPACK_WRONG_KEY:
                            Toast.makeText(context,
                                    "Wrong Password",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            break;

                    }
                }
            }
            else{
                // TODO Handle data loss
            }

        }
    }
    /**
     * Shows a toast message depending on the success and finishes the overall
     * upload process through the call of finishGoogleDriveUpload
     */
    private final ResultCallback<DriveFolder.DriveFileResult> afterFileCreationCallback = new ResultCallback<DriveFolder.DriveFileResult>() {

        @Override
        public void onResult(DriveFolder.DriveFileResult arg0) {

            if (!arg0.getStatus().isSuccess()) {
                Toast.makeText(GoogleDrive.getMainContext(),
                        "Upload failed, signing in again is necessary",
                        Toast.LENGTH_SHORT).show();


            } if(arg0.getStatus().isSuccess()) {
                Toast.makeText(GoogleDrive.getMainContext(),
                        "Upload successful", Toast.LENGTH_SHORT).show();

            }
            if (GoogleDrive.getGoogleApiClient() != null) {
                Drive.DriveApi.requestSync(
                        GoogleDrive.getGoogleApiClient())
                        .setResultCallback(afterUploadSyncCallback);
            }
        }
    };
    /**
     * Handles the upload to an existing content of a DriveFile by overwriting
     * it via the provided OutputStream
     */
    private final ResultCallback<Status> afterFileOverWriteCallback = new ResultCallback<Status>() {

        @Override
        public void onResult(Status arg0) {
            if (!arg0.isSuccess()) {
                Toast.makeText(GoogleDrive.getMainContext(),
                        "Upload failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GoogleDrive.getMainContext(),
                        "Upload successful", Toast.LENGTH_SHORT).show();
            }
            Drive.DriveApi
                    .requestSync(GoogleDrive.getGoogleApiClient())
                    .setResultCallback(afterUploadSyncCallback);
        }
    };
    /**
     * Called after the synchronization with google drive
     */
    private final ResultCallback<Status> afterUploadSyncCallback = new ResultCallback<Status>() {

        @Override
        public void onResult(Status arg0) {
            if(isFileRequestValid(arg0)<0){
                Toast.makeText(
                    GoogleDrive.getMainContext(),
                    "Synchronisation failed, \nsigning in again is necessary",
                    Toast.LENGTH_SHORT).show();}
            else{
            Toast.makeText(GoogleDrive.getMainContext(),
                    "Finished synchronising the upload",
                    Toast.LENGTH_SHORT).show();
                progressDialog = new ProgressDialog(getMainContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading directories of "+currentPath+"...");
                progressDialog.show();
            getArchiveList(currentDirectoryId);
            }
        }
    };
    /**
     * Reads the contents of an existing DriveFile an writes it to the local
     * sensor files via its OutputStream
     */
    private final ResultCallback<DriveApi.DriveContentsResult> readExistingFileCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult arg0) {

            if (!arg0.getStatus().isSuccess()) {

                return;
            }
            final DriveContents existingFileContents = arg0.getDriveContents();

            new AsyncDriveFileDownloadTask().execute(existingFileContents);

        }

    };

    /**
     *
     * @param result The status obtained when logging in to Google Drive
     * @return Indicates if the logging process was successful, even if a delay between deletions may occur due to limited sync rates for developers
     */
    public int isFileRequestValid(Status result){
        if (result.isSuccess()) {
            return 1;
        } else {
            // Too many requests, this can't be avoided or duplicate files will be uploaded
            if(result.getStatusCode() == Miscellaneous.REQUEST_LIMIT_REACHED_1||result.getStatusCode()== Miscellaneous.REQUEST_LIMIT_REACHED_2||
                    result.getStatusMessage().equals("Sync request rate limit exceeded.")){
                return 0;
            }
            else{
            return -1;
            }
        }
    }
private void stopLoadingScreen(int resultCode){
    if(progressDialog!=null){
    progressDialog.dismiss();}
   }
}
