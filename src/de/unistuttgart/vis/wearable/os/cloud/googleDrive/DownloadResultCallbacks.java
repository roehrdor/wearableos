package de.unistuttgart.vis.wearable.os.cloud.googleDrive;

import java.io.BufferedInputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Class to provide the ResultCallbacks that are done in the background to
 * handle the download process of the database file because of missing functions
 * for an easy download via the SDK this will need the use of an InputStream of
 * the DriveFiles content to the OutputStream of the file on the SDCard
 *
 * 
 */
public class DownloadResultCallbacks {
	private static DriveFolder currentDBFolder = null;

	public static void setCurrentCloudDBFolder(DriveFolder driveFolder) {
		currentDBFolder = driveFolder;

	}

	public static DriveFolder getCurrentCloudDBFolder() {
		return currentDBFolder;

	}

	private static DriveFile currentCloudDBFile = null;

	private static long currentCloudDBFileSize = 0;

	private static long getCurrentCloudDBFileSize() {
		return currentCloudDBFileSize;
	}

	private static void setCurrentCloudDBFileSize(long currentCloudDBFileSize) {
		DownloadResultCallbacks.currentCloudDBFileSize = currentCloudDBFileSize;
	}

	public static void setCurrentCloudDBFile(DriveFile driveFile) {
		currentCloudDBFile = driveFile;

	}

	public static DriveFile getCurrentCloudDBFile() {
		return currentCloudDBFile;

	}

	/**
	 * Callback required to check whether the database folder is already present
	 * at Google Drive
	 */
	private final ResultCallback<MetadataBufferResult> folderQueryResultCallback = new ResultCallback<MetadataBufferResult>() {
		Metadata cloudFolderMetaData = null;

		@Override
		public void onResult(MetadataBufferResult arg0) {
			MetadataBuffer folderMetadataBuffer = null;
			if (!arg0.getStatus().isSuccess()) {
				Toast.makeText(GoogleDrive.getMainContext(), "Konnte Ordner nicht abfragen",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				try {
					folderMetadataBuffer = arg0.getMetadataBuffer();
					Log.i("Test-App", "Zeile 245" + " Anzahl Ordner, die Kriterien erf�llen: "
							+ folderMetadataBuffer.getCount());
					if (folderMetadataBuffer.getCount() > 0) {
						cloudFolderMetaData = Miscellaneous.getLatestMetadata(folderMetadataBuffer);
					}

					// New folder is created
					if (cloudFolderMetaData == null) {
						Toast.makeText(GoogleDrive.getMainContext(),
								"Keine Datenbank zum Download vorhanden", Toast.LENGTH_SHORT).show();
						return;
					} else {

						Log.i("Test-App", "Zeile 267");

						setCurrentCloudDBFolder(Drive.DriveApi.getFolder(
								GoogleDrive.getGoogleApiClient(), cloudFolderMetaData.getDriveId()));
						Log.i("Test-App", "Id ist: " + getCurrentCloudDBFolder().getDriveId()
								+ " und der Name ist: " + cloudFolderMetaData.getTitle());
						Query query = new Query.Builder().addFilter(
								Filters.and(Filters.eq(SearchableField.TITLE, Miscellaneous.getCloudDbName()),
										Filters.eq(SearchableField.MIME_TYPE, Miscellaneous.getZipMimeType()),
										Filters.eq(SearchableField.TRASHED, false))).build();

						getCurrentCloudDBFolder()
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

	public ResultCallback<MetadataBufferResult> getFolderqueryresultcallback() {
		return this.folderQueryResultCallback;
	}

	public ResultCallback<MetadataBufferResult> getFileQueryResultCallback() {
		return this.fileQueryResultCallback;
	}

	/**
	 * Looks for the latest database file to download it, though internal problems
	 * with google's drive API prevent a reliable detection, through a previous
	 * call of the requestSync method this is circumvented
	 */
	private final ResultCallback<MetadataBufferResult> fileQueryResultCallback = new ResultCallback<MetadataBufferResult>() {

		Metadata cloudFileMetaData = null;

		@Override
		public void onResult(MetadataBufferResult arg0) {
			MetadataBuffer fileMetadataBuffer = null;

			if (!arg0.getStatus().isSuccess()) {
				Toast.makeText(GoogleDrive.getMainContext(),
						"Konnte keine Abfrage f�r die aktuellste \n" + "Datenbankdatei in Google Drive machen",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				try {
					fileMetadataBuffer = arg0.getMetadataBuffer();
					Log.i("Test-App", "Zeile 133 " + fileMetadataBuffer.getCount());
					if (fileMetadataBuffer.getCount() > 0) {
						cloudFileMetaData = Miscellaneous.getLatestMetadata(fileMetadataBuffer);
					}

					if (cloudFileMetaData == null) {
						Toast.makeText(GoogleDrive.getMainContext(),
								"Keine Datenbank zum Download verf�gbar", Toast.LENGTH_SHORT).show();
						return;
					} else {

						Log.i("Test-App", "Zeile 155");
						setCurrentCloudDBFileSize(cloudFileMetaData.getFileSize());
						final DriveFile cloudDatabaseFile = Drive.DriveApi.getFile(
								GoogleDrive.getGoogleApiClient(), cloudFileMetaData.getDriveId());
						setCurrentCloudDBFile(cloudDatabaseFile);
						cloudDatabaseFile.open(GoogleDrive.getGoogleApiClient(),
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
	 * database file via its OutputStream
	 */
	private final ResultCallback<DriveContentsResult> readExistingFileCallback = new ResultCallback<DriveContentsResult>() {

		@Override
		public void onResult(DriveContentsResult arg0) {

			if (!arg0.getStatus().isSuccess()) {
				Log.i("Test-App", "Konnte nicht Contents erhalten");
				return;
			}
			final DriveContents existingFileContents = arg0.getDriveContents();
			Log.i("Test.App", "Der Inhalt ist vorhanden: " + (existingFileContents != null));
			new AsyncDriveFileDownloadTask(GoogleDrive.getPassword()).execute(existingFileContents);

		}

	};

	private class AsyncDriveFileDownloadTask extends AsyncTask<DriveContents, String, Boolean> {

		private final ProgressDialog progressDialog;
		private boolean cancelRequest = false;

		public AsyncDriveFileDownloadTask(String password) {
			// create Progress Dialog to display the progress of upload
			progressDialog = new ProgressDialog(GoogleDrive.getMainContext());
			progressDialog.setMax(100);
			progressDialog.setMessage("Downloading " + Miscellaneous.getCloudDbName());
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
			//File localDBFile = new File(APIFunctions.getDatabasePathPreference()+File.separator+DataBaseHelper.getDbName());
			double totalBytes = getCurrentCloudDBFileSize();
			int currentBytes = 0;

			try {
                // TODO handle downloaded archive
				//if (localDBFile.exists() && !localDBFile.canWrite() && !cancelRequest) {
				//	progressDialog.dismiss();
//
//					publishProgress("Konnte nicht auf lokale Datenbank zugreifen");
//					return null;
//				} else
                    {
					// Deletes the local database file and creates it to create
					// an exact copy of the DriveFile
					//localDBFile.delete();
					// TODO handle the downloaded file via import or replace sensor files
					//new File(APIFunctions.getDatabasePathPreference()).mkdirs();
                    // TODO outputstream to file object of archive
					//BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(localDBFile));

					// Overwrite the local file.
					BufferedInputStream contentInputStream = new BufferedInputStream(existingFileContents.getInputStream());

					int fileByte = 0;

					try {
						while (fileByte != -1) {
							fileByte = contentInputStream.read();
							if (fileByte != -1 && !cancelRequest) {
								currentBytes++;
								progressDialog.setProgress((int) ((currentBytes / totalBytes) * 100));
								//fileOutputStream.write(fileByte);
								Log.i("Test-App", "Schreibe Byte 340");
							} else if (fileByte != -1 && cancelRequest) {
								contentInputStream.close();
								//fileOutputStream.flush();
								//fileOutputStream.close();
								//localDBFile.delete();
								progressDialog.dismiss();
								break;
							}
						}
					} catch (IOException e) {

						publishProgress("Zugriff auf die Datei fehlgeschlagen...");

					} finally {
						try {

							contentInputStream.close();
							//fileOutputStream.flush();
							//fileOutputStream.close();
							publishProgress("Datenbank erfolgreich heruntergeladen");

							progressDialog.dismiss();
							return true;

						} catch (IOException e) {
							progressDialog.dismiss();
							publishProgress("Download fehlgeschlagen...");
							e.printStackTrace();

						}
					}

				}

			}
            catch (Exception e) {
				progressDialog.dismiss();
				publishProgress("Zugriff auf die Datei fehlgeschlagen...");
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

			}
			else{
				// TODO Handle data loss
			}

		}
	}
}
