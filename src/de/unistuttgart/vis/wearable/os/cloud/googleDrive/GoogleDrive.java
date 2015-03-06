package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import android.app.Activity;
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
    private static String password ="";
    private boolean isExport = true;
    private boolean isConnected = false;
    private static DriveId currentDirectoryId = null;
    private ListView googleDriveFolderListView = null;
    private ArrayList<Metadata> metadataArrayList = null;
    private GoogleDriveAdapter adapter = null;
    // Thanks to very limited access for developers this is necessary to be able to get the parent directory
    private Stack<DriveId> directoryHistory = null;
    private DriveFile currentCloudDBFile = null;
    private static final int SIGN_IN_REQUEST_CODE = 0;
    private static final int REQUEST_LIMIT_REACHED = 10;
    private float currentCloudArchiveFileSize = 0.0f;

    /**
     * Google API client, luckily it only stores the local information about the
     * state of the Google Drive, so every time a change is made online,
     * requestSync has to be executed
     */
    private static GoogleApiClient mGoogleApiClient = null;

    private DownloadResultCallbacks downloadCallbackHelper = new DownloadResultCallbacks();

    private UploadResultCallbacks uploadCallbackHelper = new UploadResultCallbacks();

    public static String getPassword(){
        return password;
    }

    public static DriveFolder getDefaultCloudArchiveFolder() {
        return defaultCloudArchiveFolder;
    }

    public static void setDefaultCloudArchiveFolder(DriveFolder defaultCloudArchiveFolder) {
        GoogleDrive.defaultCloudArchiveFolder = defaultCloudArchiveFolder;
    }

    @Override
    public void onBackPressed() {
        // TODO handle case where currentParentId is null
        if(currentDirectoryId.equals(Drive.DriveApi.getRootFolder(getGoogleApiClient()).getDriveId())){
        super.onBackPressed();}
        else {
            currentDirectoryId = directoryHistory.pop();
            getArchiveList(currentDirectoryId);
        }
    }

    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    /**
     * Establishes the connection to Google Drive before doing any actions, the
     * internal state of the client still has to be updated afterwards
     */
    public void signInToGoogleDrive() {
        if (getGoogleApiClient() == null) {
            // It's hilarious, but the app can only access self created folders
            setGoogleApiClient(new GoogleApiClient.Builder(this)
                    .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build());
            if (!getGoogleApiClient().isConnected()) {
                Toast.makeText(this, "Connecting with Google Drive",
                        Toast.LENGTH_SHORT).show();
                getGoogleApiClient().connect();
            } else {
                checkForFolderInGoogleDrive();
            }

        } else if (!getGoogleApiClient().isConnected()) {
            Toast.makeText(this, "Connecting with Google Drive",
                    Toast.LENGTH_SHORT).show();
            getGoogleApiClient().connect();
        } else {
            checkForFolderInGoogleDrive();
        }
    }

    /**
     * Method to initiate the download or upload process
         **/
     public void startImportExport(View view) {

         setMode(isExport?'u':'d');

            signInToGoogleDrive();

    }

    /**
     * Called before uploading the database to Google Drive to prevent the
     * creation of duplicate folders
     */
    public void checkForFolderInGoogleDrive() {
        if (getGoogleApiClient() != null && getGoogleApiClient().isConnected()) {
            // Search for folder and upload the file
            DriveFolder drFolder = Drive.DriveApi
                    .getRootFolder(getGoogleApiClient());
            Query query = new Query.Builder().addFilter(
                    Filters.and(Filters.eq(SearchableField.TITLE,
                                    Miscellaneous.getCloudArchiveFolderName()), Filters.eq(
                                    SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE),
                            Filters.eq(SearchableField.TRASHED, false)))
                    .build();
            if (getMode() == 'u') {
                drFolder.queryChildren(getGoogleApiClient(), query)
                        .setResultCallback(
                                this.uploadCallbackHelper
                                        .getFolderQueryResultCallback());
            } else if (getMode() == 'd') {
                drFolder.queryChildren(getGoogleApiClient(), query)
                        .setResultCallback(
                                downloadCallbackHelper
                                        .getFolderQueryResultCallback());
            }
        }
    }

    public void setAdapter(GoogleDriveAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        directoryHistory = new Stack<DriveId>();
        metadataArrayList = new ArrayList<Metadata>();
        isExport = getIntent().getBooleanExtra("isExport",false);
        setContentView(getIntent().getBooleanExtra("isExport",false)?R.layout.activity_cloud_export:R.layout.activity_cloud_import);
        googleDriveFolderListView = (ListView)findViewById(R.id.listView1);

        setGoogleApiClient(new GoogleApiClient.Builder(this)
                .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build());

        password=getIntent().getBooleanExtra("encrypted",false)?getIntent().getStringExtra("key"):"";
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
                        Log.d("gosDEBUG","Click in Export");

                        if (curMetadata.isFolder()) {
                            directoryHistory.push(currentDirectoryId);
                            currentDirectoryId = curMetadata.getDriveId();
                            getArchiveList(currentDirectoryId);
                        }
                    } else {
                        Log.d("gosDEBUG","Click in Import");
                        if (curMetadata.isFolder()) {
                            directoryHistory.push(currentDirectoryId);
                            currentDirectoryId = curMetadata.getDriveId();
                            getArchiveList(currentDirectoryId);
                        } else {
                            Log.d("gosDEBUG","starting import");
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

                        if (arg0.isSuccess()) {
                            Toast.makeText(
                                    getBaseContext(),
                                    "Synchronisation with Google Drive successful",
                                    Toast.LENGTH_SHORT).show();

                            currentDirectoryId = Drive.DriveApi.getRootFolder(getGoogleApiClient()).getDriveId();
                            searchForDefaultDirectory();

                        } else {
                            // Too many requests, thankfully this can't be avoided or duplicate files will be uploaded
                            //if(arg0.getStatusCode()==REQUEST_LIMIT_REACHED){
                                Toast.makeText(
                                        getBaseContext(),
                                        "Synchronisation with Google Drive successful",
                                        Toast.LENGTH_SHORT).show();

                                currentDirectoryId = Drive.DriveApi.getRootFolder(getGoogleApiClient()).getDriveId();
                                searchForDefaultDirectory();
                            //}
                            //else{
                           // Toast.makeText(
                           //         getBaseContext(),
                           //         "Synchronisation with Google Drive failed, "
                           //                 + "\nlogging in again is necessary\n"+arg0.getStatusMessage()+"\n"+arg0.getStatusCode(),
                           //         Toast.LENGTH_SHORT).show();
                          //  setGoogleApiClient(null);}
                        }
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
        /* TODO handle the combination of request and result codes correctly
         to only execute this after selecting the desired google account*/
            //if(requestCode==SIGN_IN_REQUEST_CODE&& resultCode == Activity.RESULT_OK){
             //   if(!isConnected){
                    isConnected=true;
                getGoogleApiClient().connect();
    //}
           // }
    }

    public void setMetadataArrayList(ArrayList<Metadata> metadataArrayList) {
        this.metadataArrayList = metadataArrayList;
    }

    public void setListViewEntries(ArrayList<Metadata> childrenEntries){
        setMetadataArrayList(childrenEntries);
        adapter.clear();
        adapter.addAll(metadataArrayList);
        adapter.notifyDataSetChanged();

    }

    public void loadDriveFolderContents(DriveId parentDirectory) {

    }
    public void startFileImport(DriveId zipFileId){
        Log.d("gosDEBUG","starting import");
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
        uploadCallbackHelper.setCurrentCloudDBFolder(Drive.DriveApi.getFolder(getGoogleApiClient(),currentDirectoryId));
        Drive.DriveApi.getFolder(getGoogleApiClient(),currentDirectoryId).queryChildren(
                GoogleDrive
                        .getGoogleApiClient(),
                query).setResultCallback(
                fileQueryResultCallback);
        //startFileExport(currentDirectoryId);
    }
    protected void getArchiveList(DriveId parentDirectory){
        final DriveId curParentDirectory = parentDirectory;
        final ArrayList<Metadata> archiveChildren = new ArrayList<Metadata>();

        final boolean resultAvailable = false;


        Drive.DriveApi.requestSync(
                GoogleDrive.getGoogleApiClient()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
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
                                        Log.d("gosDEBUG", "Finished loading directories and zip files");
                                        if (metadataBufferResult.getStatus().isSuccess()) {
                                            Log.d("gosDEBUG","Files in this directory: "+metadataBufferResult.getMetadataBuffer().getCount());
                                            for (Metadata currentFolder : metadataBufferResult.getMetadataBuffer()) {
                                                if (currentFolder.isFolder() || (!currentFolder.isFolder()&&currentFolder.getTitle().endsWith(".zip")) ||
                                                        (!currentFolder.isFolder()&&currentFolder.getMimeType().equals(Miscellaneous.getZipMimeType())))
                                                    archiveChildren.add(currentFolder);
                                            }
                                            Collections.sort(archiveChildren, new MetadataComparator());
                                            setListViewEntries(archiveChildren);

                                        }
                                    }
                                });
            }
        });



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
        public void onResult(DriveApi.MetadataBufferResult metadataBufferResult) {
            if(metadataBufferResult.getStatus().isSuccess()){
                Log.d("gosDEBUG","Number of default directories: "+metadataBufferResult.getMetadataBuffer().getCount());
                // Create default directory
                if(metadataBufferResult.getMetadataBuffer().getCount()==0){

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(Miscellaneous.getCloudArchiveFolderName())
                            .build();
                    Drive.DriveApi.getFolder(getGoogleApiClient(),currentDirectoryId).createFolder(getGoogleApiClient(),changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                        @Override
                        public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                            getArchiveList(currentDirectoryId);
                        }
                    });
                }
                else{
                    getArchiveList(currentDirectoryId);
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
                    Log.i("Test-App",
                            "Number of files fulfilling constraints: "
                                    + fileMetadataBuffer.getCount());
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
                    if (fileMetadataBuffer != null
                            && !fileMetadataBuffer.isClosed()) {
                        fileMetadataBuffer.close();
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

            new AsyncDriveFileUploadTask('u', GoogleDrive.getPassword()).execute(cloudDBFolderContents);

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

            new AsyncDriveFileUploadTask('o', GoogleDrive.getPassword()).execute(existingFileContents);

        }

    };
    private class AsyncDriveFileUploadTask extends
            AsyncTask<DriveContents, String, Boolean> {
        private File file = null;
        private String password;
        private char mode = '_';
        private final ProgressDialog progressDialog;
        private boolean cancelRequest = false;

        public AsyncDriveFileUploadTask(char mode, String password) {
            this.mode = mode;
            this.password = password;
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

            if(password.equals("")){
                if(file.exists()){
                    file.delete();
                }

                Archiver.createArchiveFile(file);
            }
            else{
                if(file.exists()){
                    file.delete();
                }

                Archiver.createEncryptedArchiveFile(password,file);
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
                                    progressDialog.dismiss();
                                    publishProgress("Upload cancelled");
                                    break;
                                } catch (IOException e) {
                                    progressDialog.dismiss();
                                    publishProgress("Upload cancelled");
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    progressDialog.dismiss();
                    publishProgress("Upload cancelled");

                } finally {
                    try {
                        fileInputStream.close();
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        progressDialog.dismiss();
                    } catch (IOException e) {
                        progressDialog.dismiss();
                        publishProgress("Upload cancelled");

                    }
                }
                if (!cancelRequest) {
                    progressDialog.dismiss();


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
                progressDialog.dismiss();
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
        String password="";

        public AsyncDriveFileDownloadTask(String password) {
            this.password = password;
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

                                Log.i("gosDEBUG", "Writing Byte 340");
                            } else if (streamStatus != -1 && cancelRequest) {
                                contentInputStream.close();
                                fileOutputStream.close();
                                progressDialog.dismiss();
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

                            progressDialog.dismiss();
                            return true;

                        } catch (IOException e) {
                            progressDialog.dismiss();
                            publishProgress("Download failed...");
                            e.printStackTrace();

                        }
                    }

                }

            }
            catch (Exception e) {
                progressDialog.dismiss();
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
            if(result){
                Log.d("gosDEBUG","Download successful");
                if(password.equals("")){
                    APIFunctions.unpackArchiveFile(downloadDestination);
                }
                else{
                    // TODO handle encryption
                    //APIFunctions.unpackEncryptedFile(password,downloadDestination);
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
            if (!arg0.getStatus().isSuccess()) {
                Toast.makeText(
                        GoogleDrive.getMainContext(),
                        "Synchronisation failed, \nsigning in again is necessary",
                        Toast.LENGTH_SHORT).show();

            }
            Toast.makeText(GoogleDrive.getMainContext(),
                    "Finished synchronising the upload",
                    Toast.LENGTH_SHORT).show();
            getArchiveList(currentDirectoryId);

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
                Log.i("gosDEBUG", "Couldn't get contents");
                return;
            }
            final DriveContents existingFileContents = arg0.getDriveContents();
            Log.i("gosDEBUG", "Content available: " + (existingFileContents != null));
            new AsyncDriveFileDownloadTask(GoogleDrive.getPassword()).execute(existingFileContents);

        }

    };
}
