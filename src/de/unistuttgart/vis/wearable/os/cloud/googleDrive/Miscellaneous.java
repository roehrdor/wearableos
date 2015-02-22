package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import android.util.Log;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

public class Miscellaneous {
	private static DriveFolder currentDBFolder = null;
	/**
	 * Request code for auto Google Play Services error resolution.
	 */
	protected static final int REQUEST_CODE_RESOLUTION = 1;

	private static boolean cancelRequest = false;

    private static final String CLOUD_DB_FOLDER_NAME = "Garment-OS";

    private static final String CLOUD_DB_NAME = "gos_sensors";

    private static final String ZIP_MIME_TYPE = "application/x-zip";

    public static String getCloudDbFolderName(){
        return CLOUD_DB_FOLDER_NAME;
    }
    public static String getCloudDbName(){
        return CLOUD_DB_NAME;
    }

    public static String getZipMimeType(){
        return ZIP_MIME_TYPE;
    }

	public boolean getCancelRequestStatus() {
		return cancelRequest;
	}

	public void setCancelRequest(boolean newCancelStatus) {
		cancelRequest = newCancelStatus;
	}

	public static void setCurrentCloudDBFolder(DriveFolder driveFolder) {
		currentDBFolder = driveFolder;

	}

	public static DriveFolder getCurrentCloudDBFolder() {
		return currentDBFolder;
	}

	public static Metadata getLatestMetadata(MetadataBuffer mdBuffer) {
		Metadata latestDriveFolderCandidate = null;

		for (Metadata md : mdBuffer) {

			if (latestDriveFolderCandidate == null) {

				latestDriveFolderCandidate = md;
				Log.i("Test-App",
						"Zeile 34, weise Wert an aktuellen Ordner zu "
								+ md.getContentAvailability());

			} else {

				if (latestDriveFolderCandidate.getModifiedDate().before(
						md.getModifiedDate())) {
					latestDriveFolderCandidate = md;
					Log.i("Test-App",
							"Zeile 43, weise Wert an aktuellen Ordner zu");
				}

			}
		}
		return latestDriveFolderCandidate;

	}
}