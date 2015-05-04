package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import android.util.Log;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

/**
 * Helper class for needed Constants for Google Drive, including Integer constants to
 * determine successful log-ins and a method to get the latest directory satisfying specific constraints
 */
public class Miscellaneous {

	/**
	 * Request, result and resolution codes for Google Play Services error resolution.
	 */
	protected static final int SIGN_IN_REQUEST_CODE_1 = 0;
	protected static final int SIGN_IN_REQUEST_CODE_2 = 1;
	protected static final int SIGN_IN__SUCCESSFUL_RESULT_CODE_1 = 1;
	protected static final int SIGN_IN__CANCELLED_RESULT_CODE_1 = 0;
	protected static final int SIGN_IN__SUCCESSFUL_RESULT_CODE_2 = -1;
	protected static final int REQUEST_LIMIT_REACHED_1 = 10;
	protected static final int REQUEST_LIMIT_REACHED_2 = 1507;
	protected static final int REQUEST_CODE_RESOLUTION = 1;

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

	/**
	 *
	 * @param mdBuffer The buffer of results of the query to check for
	 *                    the availability of the "Garment-OS" directory in the Google Drive of the user
	 * @return The metadata of the latest directory satisfying the query or null if no directory is available
	 */
	public static Metadata getLatestMetadata(MetadataBuffer mdBuffer) {
		Metadata latestDriveFolderCandidate = null;

		for (Metadata currentMetadata : mdBuffer) {

			if (latestDriveFolderCandidate==null||latestDriveFolderCandidate.getModifiedDate().before(
					currentMetadata.getModifiedDate())) {
				latestDriveFolderCandidate = currentMetadata;
			}
		}
		return latestDriveFolderCandidate;
	}
}