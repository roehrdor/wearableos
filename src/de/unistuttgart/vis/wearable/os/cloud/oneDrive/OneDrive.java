package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.*;


import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveStatus;
import de.unistuttgart.vis.wearable.os.R;


/**
 * Activity to provide functionality to upload or download an archive file to or
 * from One Drive
 *
 */
public class OneDrive extends Activity {
    private LiveAuthClient auth = null;
    public static LiveConnectClient client = null;
    private static Context context = null;
    private AsyncDownloadOperations asyncDownloadOperations = new AsyncDownloadOperations();
    private AsyncUploadOperations asyncUploadOperations = new AsyncUploadOperations();
    private Switch switchUseEncryption = null;
    private static String password = "";
    private Button button = null;
    private boolean isExport = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = OneDrive.this;
        this.auth = new LiveAuthClient(this, Miscellaneous.CLIENT_ID);
        setContentView(R.layout.activity_one_drive);
        button = (Button) findViewById(R.id.button1);
        if(!getIntent().getBooleanExtra("isExport",false)) {
            button.setText("Import SensorData");
            isExport = false;
        }
        password=getIntent().getBooleanExtra("encrypted",false)?getIntent().getStringExtra("key"):"";
    }

    @Override
    protected void onStop() {
        // suboptimal solution to handle standby
        if (client != null) {
            client = null;
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        context = OneDrive.this;
        super.onResume();
    }

    /**
     * Handles the sign in process to One Drive
     *
     */
    public void signInToOneDrive(char mode) {
        if (mode == 'u') {
            auth.login(this, Arrays.asList(Miscellaneous.SCOPES),
                    signInListenerUpload);
        } else if (mode == 'd') {
            auth.login(this, Arrays.asList(Miscellaneous.SCOPES),
                    signInListenerDownload);
        }
    }

    /**
     * Handles the sign-in process before beginning any other operations
     */
    final LiveAuthListener signInListenerUpload = new LiveAuthListener() {

        @Override
        public void onAuthError(LiveAuthException exception, Object userState) {

            Toast.makeText(getApplicationContext(), exception.getError(),
                    Toast.LENGTH_SHORT).show();
            client = null;

        }

        @Override
        public void onAuthComplete(LiveStatus status,
                                   LiveConnectSession session, Object userState) {
            if (status == LiveStatus.CONNECTED) {
                Toast.makeText(getApplicationContext(),
                        "Signed in to One Drive", Toast.LENGTH_SHORT).show();
                client = new LiveConnectClient(session);
                client.getAsync("me/skydrive" + "/files",
                        asyncUploadOperations.getListFilesListener());

            } else {
                Toast.makeText(getApplicationContext(),
                        "Couldn't connect with One Drive",
                        Toast.LENGTH_SHORT).show();
                client = null;
            }
        }
    };
    /**
     * Handles the sign-in process before beginning any other operations
     */
    final LiveAuthListener signInListenerDownload = new LiveAuthListener() {

        @Override
        public void onAuthError(LiveAuthException exception, Object userState) {

            Toast.makeText(getApplicationContext(), exception.getError(),
                    Toast.LENGTH_SHORT).show();
            client = null;

        }

        @Override
        public void onAuthComplete(LiveStatus status,
                                   LiveConnectSession session, Object userState) {
            if (status == LiveStatus.CONNECTED) {
                Toast.makeText(getApplicationContext(),
                        "Signed in to One Drive", Toast.LENGTH_SHORT).show();
                client = new LiveConnectClient(session);
                client.getAsync("me/skydrive/files",
                        asyncDownloadOperations.getDownloadListener());

            } else {
                Toast.makeText(getApplicationContext(),
                        "Couldn't connect with One Drive",
                        Toast.LENGTH_SHORT).show();
                client = null;
            }
        }
    };

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

    public void startImportExport(View view){
        if(isExport){
            uploadArchive();
        }
        else{
            downloadArchive();
        }
    }

    /**
     * method to trigger the upload process of the sensor archive
     */
    public void uploadArchive() {

            if (internetAvailable()) {

                if (getConnectClient() != null
                        && !getConnectClient().getSession().isExpired()) {

                    client.getAsync("me/skydrive/files",
                            asyncUploadOperations.getListFilesListener());

                } else {
                    Toast.makeText(this, "Connecting with One Drive...",
                            Toast.LENGTH_SHORT).show();
                    signInToOneDrive('u');
                }

            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "No internet connection available, please activate wifi \nor mobile data",
                        Toast.LENGTH_SHORT).show();
            }
    }




    /**
     * method to trigger the download of the sensor archive
     */
    public void downloadArchive() {
        if (internetAvailable()) {
            if (client != null && !client.getSession().isExpired()) {
                client.getAsync("me/skydrive/files",
                        asyncDownloadOperations.getDownloadListener());
            } else {
                Toast.makeText(this, "Connecting with One Drive...",
                        Toast.LENGTH_SHORT).show();
                signInToOneDrive('d');
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "No internet connection available, please activate wifi \n" +
                            "or mobile data",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public static String getPassword(){
        return password;
    }
}
