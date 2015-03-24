package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import android.util.Log;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

public class Miscellaneous {
    static final int SIGN_IN_REQUEST_CODE_1 = 0;
    static final int SIGN_IN_REQUEST_CODE_2 = 1;
    static final int SIGN_IN__SUCCESSFUL_RESULT_CODE = 1;
    private static final int SIGN_IN_FAILED_RESULT_CODE_1 = 0;
    private static final int SIGN_IN_FAILED_RESULT_CODE_2 = -1;
    static final int ARCHIVE_CREATION_SUCCESSFUL = 3;
    static final int ARCHIVE_CREATION_FAILED = -3;
    static final int ARCHIVE_IMPORT_SUCCESSFUL = 5;
    static final int ARCHIVE_IMPORT_FAILED = -5;
    static final int REQUEST_LIMIT_REACHED_1 = 10;
    static final int REQUEST_LIMIT_REACHED_2 = 1507;
    static final int LOADING_FILES_SUCCESSFUL = 10;
    static final int LOADING_FILES_FAILED = -10;
    private static DriveFolder currentArchiveFolder = null;
	/**
	 * Request code for auto Google Play Services error resolution.
	 */
	protected static final int REQUEST_CODE_RESOLUTION = 1;

	private static boolean cancelRequest = false;

    private static final String CLOUD_ARCHIVE_FOLDER_NAME = "Garment-OS";

    private static final String CLOUD_DB_NAME = "gos_sensors";

    private static final String ZIP_MIME_TYPE = "application/x-zip";

    public static String getCloudArchiveFolderName(){
        return CLOUD_ARCHIVE_FOLDER_NAME;
    }
    public static String getCloudArchiveName(){
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
		currentArchiveFolder = driveFolder;

	}

	public static DriveFolder getCurrentCloudArchiveFolder() {
		return currentArchiveFolder;
	}

	public static Metadata getLatestMetadata(MetadataBuffer mdBuffer) {
		Metadata latestDriveFolderCandidate = null;

		for (Metadata md : mdBuffer) {

			if (latestDriveFolderCandidate == null) {

				latestDriveFolderCandidate = md;
                Log.d("gosDEBUG-Title", md.getTitle());
                Log.d("gosDEBUG-MimeType",md.getMimeType());
                Log.d("gosDEBUG-all",md.toString());

			} else {

				if (latestDriveFolderCandidate.getModifiedDate().before(
						md.getModifiedDate())) {
					latestDriveFolderCandidate = md;

				}

			}
		}
		return latestDriveFolderCandidate;

	}
}