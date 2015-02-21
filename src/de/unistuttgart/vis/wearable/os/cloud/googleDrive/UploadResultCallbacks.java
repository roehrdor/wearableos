package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import de.unistuttgart.vis.wearable.os.cloud.Archiver;


/**
 * Class to provide the ResultCallbacks that are done in the background to
 * handle the upload process of the database file
 * 
 */
public class UploadResultCallbacks {
	private static DriveFolder currentDBFolder = null;

	public void setCurrentCloudDBFolder(DriveFolder driveFolder) {
		currentDBFolder = driveFolder;

	}

	public static DriveFolder getCurrentCloudDBFolder() {
		return currentDBFolder;
	}

	private DriveFile currentCloudDBFile = null;

	/**
	 * Callback required to check whether the creation of the database folder at
	 * Google Drive was successful
	 */
	private DriveFile getCloudDatabaseFile() {

		return this.currentCloudDBFile;
	}

	private void setCloudDatabaseFile(DriveFile currentDriveFile) {
		this.currentCloudDBFile = currentDriveFile;
	}

	private final ResultCallback<DriveFolderResult> folderCreationCallback = new ResultCallback<DriveFolderResult>() {

		@Override
		public void onResult(DriveFolderResult result) {
			if (!result.getStatus().isSuccess()) {

				return;
			}
			Log.i("Test-App", "Zeile 395");
            // TODO proceed with upload
		}
	};

	public ResultCallback<DriveFolderResult> getFolderCreationCallback() {
		return this.folderCreationCallback;
	}

	/**
	 * Looks for the latest database file to overwrite it with the local one,
	 * though internal problems with google's drive api prevent a reliable
	 * detection, through a previous call of the requestSync methode this is
	 * circumvented
	 */
	private ResultCallback<MetadataBufferResult> fileQueryResultCallback = new ResultCallback<MetadataBufferResult>() {

		Metadata cloudFileMetaData = null;

		@Override
		public void onResult(MetadataBufferResult arg0) {

			if (!arg0.getStatus().isSuccess()) {
				Toast.makeText(
						GoogleDrive.getMainContext(),
						"Couldn't make a request for \n"
								+ "the latest sensor archive",
						Toast.LENGTH_SHORT).show();
			} else {
				MetadataBuffer fileMetadataBuffer = null;
				Log.i("Test-App", "Zeile 90");
				try {
					fileMetadataBuffer = arg0.getMetadataBuffer();
					Log.i("Test-App",
							"Number of files fulfilling contraints: "
									+ fileMetadataBuffer.getCount());
					if (fileMetadataBuffer.getCount() > 0) {
						cloudFileMetaData = Miscellaneous
								.getLatestMetadata(fileMetadataBuffer);
					}

					if (cloudFileMetaData == null) {
						Log.i("Test-App", "Zeile 317");
						// Schon in GarmentOS Datenbank Ordner, Datei nun
						// erstellen

						Drive.DriveApi.newDriveContents(
								GoogleDrive.getGoogleApiClient())
								.setResultCallback(getNewDBCreateCallback());

					} else {

						Log.i("Test-App", "Zeile 310");

						final DriveFile cloudDatabaseFile = Drive.DriveApi
								.getFile(GoogleDrive
										.getGoogleApiClient(),
										cloudFileMetaData.getDriveId());
						setCloudDatabaseFile(cloudDatabaseFile);
						cloudDatabaseFile.open(
                                GoogleDrive.getGoogleApiClient(),
                                DriveFile.MODE_WRITE_ONLY, null)
								.setResultCallback(
										getOverwriteExistingFileCallback());

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

	/**
	 * Overwrites contents of existing DriveFile via its associated contents and
	 * the provided OutputStream where data of the new database file is written
	 * to
	 */
	private final ResultCallback<DriveContentsResult> overwriteExistingFileCallback = new ResultCallback<DriveContentsResult>() {

		@Override
		public void onResult(DriveContentsResult arg0) {

			if (!arg0.getStatus().isSuccess()) {
				Log.i("Test-App", "Konnte nicht Contents erhalten");
				return;
			}

			final DriveContents existingFileContents = arg0.getDriveContents();
			Log.i("Test.App", "Der Inhalt ist vorhanden: "
					+ (existingFileContents != null));

			new AsyncDriveFileUploadTask('o', GoogleDrive.getPassword()).execute(existingFileContents);

		}

	};

	public ResultCallback<DriveContentsResult> getOverwriteExistingFileCallback() {
		return this.overwriteExistingFileCallback;
	}

	public ResultCallback<MetadataBufferResult> getFileQueryResultCallback() {
		return this.fileQueryResultCallback;
	}

	/**
	 * Shows a toast message depending on the success and finishes the overall
	 * upload process through the call of finishGoogleDriveUpload
	 */
	private final ResultCallback<DriveFileResult> afterFileCreationCallback = new ResultCallback<DriveFileResult>() {

		@Override
		public void onResult(DriveFileResult arg0) {
			//
			if (!arg0.getStatus().isSuccess()) {
				Toast.makeText(GoogleDrive.getMainContext(),
						"Upload fehlgeschlagen, Anmeldung erneut notwendig",
						Toast.LENGTH_SHORT).show();
				GoogleDrive.setGoogleApiClient(null);
			} else {
				Toast.makeText(GoogleDrive.getMainContext(),
						"Upload erfolgreich", Toast.LENGTH_SHORT).show();
				if (GoogleDrive.getGoogleApiClient() != null) {
					Drive.DriveApi.requestSync(
							GoogleDrive.getGoogleApiClient())
							.setResultCallback(getAfteruploadsynccallback());
				}
			}
		}
	};

	public ResultCallback<DriveFileResult> getAfterfilecreationcallback() {
		return this.afterFileCreationCallback;
	}

	/**
	 * Called after the synchronization with google drive
	 */
	private final ResultCallback<Status> afterUploadSyncCallback = new ResultCallback<Status>() {

		@Override
		public void onResult(Status arg0) {
			if (!arg0.getStatus().isSuccess()) {
				Toast.makeText(
						GoogleDrive.getMainContext(),
						"Synchronisation fehlgeschlagen, \nerneute Anmeldung erforderlich",
						Toast.LENGTH_SHORT).show();
				GoogleDrive.setGoogleApiClient(null);
			}
			Toast.makeText(GoogleDrive.getMainContext(),
					"Synchronisation des Uploads abgeschlossen",
					Toast.LENGTH_SHORT).show();

		}
	};

	public ResultCallback<Status> getAfteruploadsynccallback() {
		return this.afterUploadSyncCallback;
	}

	/**
	 * Handles the upload to an existing content of a DriveFile by overwriting
	 * it via the provided OutputStream
	 */
	private final ResultCallback<Status> afterFileOverWriteCallback = new ResultCallback<Status>() {

		@Override
		public void onResult(Status arg0) {
			if (!arg0.isSuccess()) {
				Toast.makeText(GoogleDrive.getMainContext(),
						"Upload fehlgeschlagen", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(GoogleDrive.getMainContext(),
						"Upload abgeschlossen", Toast.LENGTH_SHORT).show();
			}
			Drive.DriveApi
					.requestSync(GoogleDrive.getGoogleApiClient())
					.setResultCallback(getAfteruploadsynccallback());
		}
	};

	public ResultCallback<Status> getAfterFileOverWriteCallback() {
		return this.afterFileOverWriteCallback;
	}

	/**
	 * Callback required to check whether the database folder is already present
	 * at Google Drive
	 */
	private final ResultCallback<MetadataBufferResult> folderQueryResultCallback = new ResultCallback<MetadataBufferResult>() {
		Metadata cloudFolderMetaData = null;

		@Override
		public void onResult(MetadataBufferResult arg0) {
			if (!arg0.getStatus().isSuccess()) {
				Log.i("Test-App", "Zeile 242 Couldn't request directory information");
				return;
			} else {

				MetadataBuffer folderMetadataBuffer = null;
				Log.i("Test-App", "Zeile 314");
				try {
					folderMetadataBuffer = arg0.getMetadataBuffer();
					Log.i("Test-App", "Zeile 245"
							+ " Number of directories fulfilling constraints: "
							+ folderMetadataBuffer.getCount());
					if (folderMetadataBuffer.getCount() > 0) {
						cloudFolderMetaData = Miscellaneous
								.getLatestMetadata(folderMetadataBuffer);
					}

					// New folder is created
					if (cloudFolderMetaData == null) {
						setCurrentCloudDBFolder(null);
						Log.i("Test-App", "Zeile 260");
						MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
								.setTitle(Miscellaneous.getCloudDbFolderName())
								.build();
						Drive.DriveApi
								.getRootFolder(
										GoogleDrive
												.getGoogleApiClient())
								.createFolder(
										GoogleDrive
												.getGoogleApiClient(),
										changeSet)
								.setResultCallback(getFolderCreationCallback());
						// Using existing folder for upload
					} else {

						Log.i("Test-App", "Zeile 267");

						setCurrentCloudDBFolder(Drive.DriveApi.getFolder(
								GoogleDrive.getGoogleApiClient(),
								cloudFolderMetaData.getDriveId()));
						Log.i("Test-App",
								"Id ist: "
										+ getCurrentCloudDBFolder()
												.getDriveId()
										+ " und der Name ist: "
										+ cloudFolderMetaData.getTitle());
						Query query = new Query.Builder()
								.addFilter(
										Filters.and(
												Filters.eq(
														SearchableField.TITLE,
														Miscellaneous.getCloudDbName()),

												Filters.eq(
														SearchableField.TRASHED,
														false),

														Filters.eq(
																SearchableField.MIME_TYPE,
																Miscellaneous.getOctetStreamMimeType())))
								.build();

						getCurrentCloudDBFolder()
								.queryChildren(
										GoogleDrive
												.getGoogleApiClient(),
										query).setResultCallback(
										new UploadResultCallbacks()
												.getFileQueryResultCallback());

					}
				} finally {
					if (folderMetadataBuffer != null
							&& !folderMetadataBuffer.isClosed()) {
						folderMetadataBuffer.close();
					}
				}
			}
		}
	};

	public ResultCallback<MetadataBufferResult> getFolderQueryResultCallback() {
		return this.folderQueryResultCallback;
	}

	/**
	 * Handles the upload of a new content of the future drive file to an
	 * existing DriveFolder and creating the corresponding DriveFile for the
	 * database file after that
	 */
	private final ResultCallback<DriveContentsResult> newDBCreateCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

		@Override
		public void onResult(DriveContentsResult arg0) {
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

	public ResultCallback<DriveContentsResult> getNewDBCreateCallback() {
		return this.newDBCreateCallback;
	}

	private class AsyncDriveFileUploadTask extends
			AsyncTask<DriveContents, String, Boolean> {
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
					.setMessage("Uploading " + Miscellaneous.getCloudDbName());
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

			// TODO handle data loss

		}

		@Override
		protected void onProgressUpdate(String... values) {
			Toast.makeText(GoogleDrive.getMainContext(), values[0],
					Toast.LENGTH_SHORT).show();
			super.onProgressUpdate(values);
		}

		@Override
		protected Boolean doInBackground(DriveContents... params) {
			// Database Path

			File file = new File(GoogleDrive.getMainContext().getFilesDir().getAbsolutePath()+File.separator+Miscellaneous.getCloudDbName());

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
									publishProgress("Upload abgebrochen");
									break;
								} catch (IOException e) {
									progressDialog.dismiss();
									publishProgress("Upload abgebrochen");
								}
							}
						}
					}
				} catch (IOException e) {
					progressDialog.dismiss();
					publishProgress("Upload abgebrochen");

				} finally {
					try {
						fileInputStream.close();
						fileOutputStream.flush();
						fileOutputStream.close();
						progressDialog.dismiss();
					} catch (IOException e) {
						progressDialog.dismiss();
						publishProgress("Upload abgebrochen");

					}
				}
				if (!cancelRequest) {
					progressDialog.dismiss();
					Log.i("Test-App", "Zeile 368");

					MetadataChangeSet fileUploadChangeSet = new MetadataChangeSet.Builder()
							.setMimeType(Miscellaneous.getOctetStreamMimeType())
							.setTitle(Miscellaneous.getCloudDbName()).build();
					if (mode == 'u') {
						Drive.DriveApi
								.getFolder(
										GoogleDrive
												.getGoogleApiClient(),
										getCurrentCloudDBFolder().getDriveId())
								.createFile(
										GoogleDrive
												.getGoogleApiClient(),
										fileUploadChangeSet, driveFileContents)
								.setResultCallback(
										getAfterfilecreationcallback());
					} else {
                        driveFileContents.commit(GoogleDrive.getGoogleApiClient(), fileUploadChangeSet).setResultCallback(
                                getAfterFileOverWriteCallback());
                        //getCloudDatabaseFile().commit(
                          //      GoogleDrive.getGoogleApiClient(),
                            //    driveFileContents, fileUploadChangeSet);

					}

				} else {
					if (driveFileContents.getOutputStream() != null) {
						driveFileContents.discard(GoogleDrive.getGoogleApiClient());
					}
				}
			} catch (IOException e) {
				progressDialog.dismiss();
				publishProgress("Upload abgebrochen");

				e.printStackTrace();
			}

			return null;

		}

	}

}
