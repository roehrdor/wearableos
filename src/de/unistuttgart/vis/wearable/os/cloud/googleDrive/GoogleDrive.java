package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import de.unistuttgart.vis.wearable.os.R;


/**
 * Activity to provide functionality to upload or download an archive file to or
 * from Google Drive
 *
 */
public class GoogleDrive extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {



    private static Context context;
    private static DriveFolder currentCloudArchiveFolder;
    private static char mode = 'n';
    private static String password ="";
    private Switch switchUseEncryption = null;
    /**
     * Google API client, luckily it only stores the local information about the
     * state of the Google Drive online, so every time a change is made online,
     * requestSync has to be executed
     */
    private static GoogleApiClient mGoogleApiClient = null;

    private DownloadResultCallbacks downloadCallbackHelper = new DownloadResultCallbacks();

    private UploadResultCallbacks uploadCallbackHelper = new UploadResultCallbacks();

    public static String getPassword(){
        return password;
    }

    public static DriveFolder getCurrentCloudArchiveFolder() {
        return currentCloudArchiveFolder;
    }

    public static void setCurrentCloudArchiveFolder(DriveFolder currentCloudArchiveFolder) {
        GoogleDrive.currentCloudArchiveFolder = currentCloudArchiveFolder;
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
     * Method to initiate the download process, as no easy to use method is
     * provided via the api, the later implementation has to be done reading
     * from the contents of the DriveFile stored online via an InputStream to
     * the database file at the desired local directory represented by an
     * Outputstream TODO
     */
    public void downloadArchive(View view) {
        setMode('d');
        signInToGoogleDrive();

    }

    public void uploadArchive(View view) {

            setMode('u');
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_drive);
        switchUseEncryption = (Switch)findViewById(R.id.switch2);
        switchUseEncryption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AlertDialog.Builder alert = new AlertDialog.Builder(GoogleDrive.this);

                    alert.setTitle("Please enter password:");
                    final EditText input = new EditText(GoogleDrive.this);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            password = input.getText().toString();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            buttonView.setChecked(false);
                            password="";
                            return;
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                }
            }
        });

        context = this;

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
        Toast.makeText(this, "Connection not successful", Toast.LENGTH_SHORT)
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
                            checkForFolderInGoogleDrive();
                        } else {
                            Toast.makeText(
                                    getBaseContext(),
                                    "Synchronisation with Google Drive failed, "
                                            + "\nlogging in again is necessary",
                                    Toast.LENGTH_SHORT).show();
                            setGoogleApiClient(null);
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
}