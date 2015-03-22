package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import java.io.*;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import de.unistuttgart.vis.wearable.os.cloud.Archiver;

/**
 * Class to provide the ResultCallbacks that are done in the background to
 * handle the download process of the sensor archive file.
 * Because of missing functions for an easy download via the SDK this will need the use of an InputStream of
 * the DriveFiles content to the OutputStream of the file on the SDCard
 *
 * 
 */
public class DownloadResultCallbacks {
	private static DriveFolder currentArchiveFolder = null;

	public static void setCurrentCloudArchiveFolder(DriveFolder driveFolder) {
		currentArchiveFolder = driveFolder;

	}

	public static DriveFolder getCurrentCloudArchiveFolder() {
		return currentArchiveFolder;

	}

	private static DriveFile currentCloudArchiveFile = null;

	private static long currentCloudArchiveFileSize = 0;

	private static long getCurrentCloudArchiveFileSize() {
		return currentCloudArchiveFileSize;
	}

	private static void setCurrentCloudArchiveFileSize(long currentCloudArchiveFileSize) {
		DownloadResultCallbacks.currentCloudArchiveFileSize = currentCloudArchiveFileSize;
	}

	public static void setCurrentCloudArchiveFile(DriveFile driveFile) {
		currentCloudArchiveFile = driveFile;

	}

	public static DriveFile getCurrentCloudArchiveFile() {
		return currentCloudArchiveFile;

	}

	/**
	 * Callback required to check whether the archive folder is already present
	 * at Google Drive
	 */
	private final ResultCallback<MetadataBufferResult> folderQueryResultCallback = new ResultCallback<MetadataBufferResult>() {
		Metadata cloudFolderMetaData = null;

		@Override
		public void onResult(MetadataBufferResult arg0) {
			MetadataBuffer folderMetadataBuffer = null;
			if (!arg0.getStatus().isSuccess()) {
				Toast.makeText(GoogleDrive.getMainContext(), "Couldn't request directory",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				try {
					folderMetadataBuffer = arg0.getMetadataBuffer();
					Log.i("gosDEBUG", "Line 245" + " Amount of directories satisfying constraints: "
							+ folderMetadataBuffer.getCount());
					if (folderMetadataBuffer.getCount() > 0) {
						cloudFolderMetaData = Miscellaneous.getLatestMetadata(folderMetadataBuffer);
					}

					// New directory is created
					if (cloudFolderMetaData == null) {
						Toast.makeText(GoogleDrive.getMainContext(),
								"No archive file available", Toast.LENGTH_SHORT).show();
						return;
					} else {

						Log.i("gosDEBUG", "Line 93");

						setCurrentCloudArchiveFolder(Drive.DriveApi.getFolder(
                                GoogleDrive.getGoogleApiClient(), cloudFolderMetaData.getDriveId()));
						Log.i("gosDEBUG", "Id is: " + getCurrentCloudArchiveFolder().getDriveId()
								+ " and the name is: " + cloudFolderMetaData.getTitle());
						Query query = new Query.Builder().addFilter(
								Filters.and(Filters.eq(SearchableField.TITLE, Miscellaneous.getCloudArchiveName()),
										Filters.eq(SearchableField.MIME_TYPE, Miscellaneous.getZipMimeType()),
										Filters.eq(SearchableField.TRASHED, false))).build();

						getCurrentCloudArchiveFolder()
								.queryChildren(GoogleDrive.getGoogleApiClient(), query).setResultCallback(
										getFileQueryResultCallback());

					}
				} finally {
					if (folderMetadataBuffer != null && !folderMetadataBuffer.isClosed()) {
						folderMetadataBuffer.close();
					}
				}
			}
		}
	};

	public ResultCallback<MetadataBufferResult> getFolderQueryResultCallback() {
		return this.folderQueryResultCallback;
	}

	public ResultCallback<MetadataBufferResult> getFileQueryResultCallback() {
		return this.fileQueryResultCallback;
	}

	/**
	 * Looks for the latest archive file to download it, even though the api sometimes prevents a reliable detection,
     * through a previous call of the requestSync method this is circumvented
	 */
	private final ResultCallback<MetadataBufferResult> fileQueryResultCallback = new ResultCallback<MetadataBufferResult>() {

		Metadata cloudFileMetaData = null;

		@Override
		public void onResult(MetadataBufferResult arg0) {
			MetadataBuffer fileMetadataBuffer = null;

			if (!arg0.getStatus().isSuccess()) {
				Toast.makeText(GoogleDrive.getMainContext(),
						"Couldn't make a request for the latest \n" + "archive file in Google Drive",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				try {
					fileMetadataBuffer = arg0.getMetadataBuffer();
					Log.i("gosDEBUG", "Line 133 " + fileMetadataBuffer.getCount());
					if (fileMetadataBuffer.getCount() > 0) {
						cloudFileMetaData = Miscellaneous.getLatestMetadata(fileMetadataBuffer);
					}

					if (cloudFileMetaData == null) {
						Toast.makeText(GoogleDrive.getMainContext(),
								"No archive file for download available", Toast.LENGTH_SHORT).show();
						return;
					} else {

						Log.i("gosDEBUG", "Line 155");
						setCurrentCloudArchiveFileSize(cloudFileMetaData.getFileSize());
						final DriveFile cloudArchiveFile = Drive.DriveApi.getFile(
								GoogleDrive.getGoogleApiClient(), cloudFileMetaData.getDriveId());
						setCurrentCloudArchiveFile(cloudArchiveFile);
						cloudArchiveFile.open(GoogleDrive.getGoogleApiClient(),
                                DriveFile.MODE_READ_ONLY, null).setResultCallback(readExistingFileCallback);

					}

				} finally {
					if (fileMetadataBuffer != null && !fileMetadataBuffer.isClosed()) {
						fileMetadataBuffer.close();
					}
				}
			}

		}

	};
	/**
	 * Reads the contents of an existing DriveFile an writes it to the local
	 * sensor files via its OutputStream
	 */
	private final ResultCallback<DriveContentsResult> readExistingFileCallback = new ResultCallback<DriveContentsResult>() {

		@Override
		public void onResult(DriveContentsResult arg0) {

			if (!arg0.getStatus().isSuccess()) {
				Log.i("gosDEBUG", "Couldn't get contents");
				return;
			}
			final DriveContents existingFileContents = arg0.getDriveContents();
			Log.i("gosDEBUG", "Content available: " + (existingFileContents != null));
			new AsyncDriveFileDownloadTask(GoogleDrive.getPassword()).execute(existingFileContents);

		}

	};

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
            // TODO use internal path or make temp path
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
			double totalBytes = getCurrentCloudArchiveFileSize();
			int currentBytes = 0;

			try {
                // TODO handle downloaded archive

                    {
                    downloadDestination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"gos_sensors.zip");

                    BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(downloadDestination));

					// TODO handle the downloaded file via import or replace sensor files

                    // TODO outputStream to file object of archive

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
                if(password.equals("")){
                    Archiver.unpack(downloadDestination);
                }
                else{
                    Archiver.unpackEncryptedFile(password,downloadDestination);
                }
			}
			else{
				// TODO Handle data loss
			}

		}
	}
}
