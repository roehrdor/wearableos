package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

import java.io.File;
import java.lang.reflect.Field;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveDownloadOperationListener;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;

public class AsyncDownloadOperations {

	/**
	 * Traverses the file list at the database directory of One Drive to find the
	 * desired file
	 */
	public LiveOperationListener liveDownloadPreparationListener = new LiveOperationListener() {

		@Override
		public void onError(LiveOperationException arg0, LiveOperation arg1) {

		}

		@Override
		public void onComplete(LiveOperation arg0) {

			JSONArray cloudFolderFiles = arg0.getResult().optJSONArray(Miscellaneous.DATA);
			JSONObject correctCloudFile = null;
			if (cloudFolderFiles.length() > 0) {
				for (int i = 0; i < cloudFolderFiles.length(); i++) {
					try {
						JSONObject cloudFolderFile = cloudFolderFiles.getJSONObject(i);

						if (cloudFolderFile.optString(Miscellaneous.NAME).equals(
								Miscellaneous.getCloudDbName())
								&& cloudFolderFile.optString(Miscellaneous.TYPE).equals("file")) {
							correctCloudFile = cloudFolderFile;

						}
					} catch (JSONException e) {

						e.printStackTrace();
					}

				}
				if (correctCloudFile != null) {

					new OneDriveAsyncDownloadTask().execute(correctCloudFile);

				}
			} else {
				Toast.makeText(OneDrive.getMainContext(),
						"Keine Datenbankdatei bei OneDrive vorhanden", Toast.LENGTH_SHORT).show();
			}
		}
	};

	public LiveOperationListener getDownloadlistener() {
		return this.downloadListener;
	}

	public LiveOperationListener getLiveDownloadPreparationListener() {
		return this.liveDownloadPreparationListener;
	}

	/**
	 * Searches the folders in the root of the One Drive for the desired folder
	 * where the database is stored
	 */
	final LiveOperationListener downloadListener = new LiveOperationListener() {

		@Override
		public void onError(LiveOperationException arg0, LiveOperation arg1) {

		}

		@Override
		public void onComplete(LiveOperation arg0) {
			JSONObject jO;
			try {
				JSONArray cloudRootFileArray = arg0.getResult().optJSONArray(Miscellaneous.DATA);
				for (int i = 0; i < cloudRootFileArray.length(); i++) {

					jO = cloudRootFileArray.getJSONObject(i);

					if (jO.optString(Miscellaneous.NAME).equals(
						Miscellaneous.getCloudDbFolderName())
							&& jO.optString(Miscellaneous.TYPE).equals("folder")) {
						// The folder was found and now the desired file is
						// searched inside of that folder
						OneDrive.getConnectClient().getAsync(jO.optString(Miscellaneous.ID) + "/files",
								getLiveDownloadPreparationListener());

						break;
					}
				}
			} catch (JSONException e) {
				Toast.makeText(OneDrive.getMainContext(), "Konnte nicht Liste der Ordner erhalten",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	public LiveOperationListener getDownloadListener() {
		return this.downloadListener;
	}

	private class OneDriveAsyncDownloadTask extends AsyncTask<JSONObject, String, Long> {

		private final ProgressDialog progressDialog;
		private boolean cancelRequest = false;
		private JSONObject downloadJsonObject = null;

		public OneDriveAsyncDownloadTask() {

			// create Progress Dialog to display the progress of upload
			progressDialog = new ProgressDialog(OneDrive.getMainContext());
			progressDialog.setMax(100);
			progressDialog.setMessage("Downloading " + Miscellaneous.getCloudDbName());
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setProgress(0);
			progressDialog.setCancelable(false);
			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							class AsyncUploadCancelTask extends AsyncTask<Void, Long, Boolean> {

								@Override
								protected Boolean doInBackground(Void... params) {
									cancelRequest = true;

									return false;
								}

							}

							new AsyncUploadCancelTask().execute(null, null, null);
						}
					});
			progressDialog.show();

		}

		@Override
		protected void onPostExecute(Long aLong) {
			super.onPostExecute(aLong);

		}

		@Override
		protected void onProgressUpdate(String... values) {
			Toast.makeText(OneDrive.getMainContext(), values[0], Toast.LENGTH_SHORT).show();
			super.onProgressUpdate(values);
		}

		@Override
		protected Long doInBackground(JSONObject... params) {
			this.downloadJsonObject = params[0];

			// The required directories are created on the
			// internal sdcard
			// TODO import downloaded archive
			File downloadDestination = new File("");
			// The download of the database file to the created
			// directory is initiated
			OneDrive.getConnectClient().downloadAsync(
					downloadJsonObject.optString(Miscellaneous.ID) + "/content", downloadDestination,
					new LiveDownloadOperationListener() {

						@Override
						public void onDownloadProgress(int totalBytes, int bytesRemaining,
								LiveDownloadOperation operation) {

							progressDialog
									.setProgress((int) (((float) (totalBytes - bytesRemaining) / (float) (totalBytes)) * 100));
							if (cancelRequest) {
								publishProgress("Download abgebrochen");
								progressDialog.dismiss();
								operation.cancel();
							}
						}

						@Override
						public void onDownloadFailed(LiveOperationException exception,
								LiveDownloadOperation operation) {
							publishProgress("Download abgebrochen");
							progressDialog.dismiss();
							Log.i("Test-App", "Nach cancel komme ich, nicht doch");
						}

						@Override
						public void onDownloadCompleted(LiveDownloadOperation operation) {
							publishProgress("Datenbank erfolgreich von One Drive heruntergeladen");
							progressDialog.dismiss();
						}

					}, null);
			return null;

		}
	}
}
