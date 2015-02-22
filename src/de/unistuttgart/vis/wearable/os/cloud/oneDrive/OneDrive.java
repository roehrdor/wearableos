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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveStatus;
import de.unistuttgart.vis.wearable.os.R;


/**
 * Activity to provide functionality to upload or download a database file to or
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = OneDrive.this;
        this.auth = new LiveAuthClient(this, Miscellaneous.CLIENT_ID);
        setContentView(R.layout.activity_one_drive);
        switchUseEncryption = (Switch)findViewById(R.id.switch4);
        switchUseEncryption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AlertDialog.Builder alert = new AlertDialog.Builder(OneDrive.this);

                    alert.setTitle("Please enter password:");
                    final EditText input = new EditText(OneDrive.this);
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
                        "In One Drive angemeldet", Toast.LENGTH_SHORT).show();
                client = new LiveConnectClient(session);
                client.getAsync("me/skydrive" + "/files",
                        asyncUploadOperations.getListFilesListener());

            } else {
                Toast.makeText(getApplicationContext(),
                        "Konnte sich nicht mit One Drive verbinden",
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
                        "In One Drive angemeldet", Toast.LENGTH_SHORT).show();
                client = new LiveConnectClient(session);
                client.getAsync("me/skydrive/files",
                        asyncDownloadOperations.getDownloadListener());

            } else {
                Toast.makeText(getApplicationContext(),
                        "Konnte sich nicht mit One Drive verbinden",
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

    /**
     * method to trigger the upload process of the database
     */
    public void uploadDB(View view) {

            if (internetAvailable()) {

                if (getConnectClient() != null
                        && !getConnectClient().getSession().isExpired()) {

                    client.getAsync("me/skydrive" + "/files",
                            asyncUploadOperations.getListFilesListener());

                } else {
                    Toast.makeText(this, "Verbinde mit One Drive...",
                            Toast.LENGTH_SHORT).show();
                    signInToOneDrive('u');
                }

            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Keine Internetverbindung vorhanden, bitte aktivieren Sie entweder WiFi \noder Mobile Daten",
                        Toast.LENGTH_SHORT).show();
            }
    }




    /**
     * method to trigger the download of the database
     */
    public void downloadDB(View view) {
        if (internetAvailable()) {
            if (client != null && !client.getSession().isExpired()) {
                client.getAsync("me/skydrive/files",
                        asyncDownloadOperations.getDownloadListener());
            } else {
                Toast.makeText(this, "Verbinde mit One Drive...",
                        Toast.LENGTH_SHORT).show();
                signInToOneDrive('d');
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Keine Internetverbindung vorhanden, bitte aktivieren Sie entweder WiFi \noder Mobile Daten",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public static String getPassword(){
        return password;
    }
}
