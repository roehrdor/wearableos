package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.unistuttgart.vis.wearable.os.cloud.Archiver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveUploadOperationListener;
import com.microsoft.live.OverwriteOption;

public class AsyncUploadOperations {

	/**
	 * LiveOperationListener that traverses the results received by One Drive in
	 * form of JSONObjects and creates the GarmentOS sensor archive directory and
	 * archive file at One Drive in that directory
	 */
	final LiveOperationListener listFoldersListener = new LiveOperationListener() {

		JSONObject currentJsonObject = null;
		JSONObject resultJsonObject = null;
		JSONArray resultJsonArray = null;
		JSONObject uploadJsonObject = null;

		@Override
		public void onError(LiveOperationException exception, LiveOperation operation) {
		}

		@Override
		public void onComplete(

		LiveOperation operation) {
			// Traversing the JSONObjects to identify an already existing upload
			// location
			resultJsonObject = operation.getResult();
			resultJsonArray = resultJsonObject.optJSONArray(Miscellaneous.DATA);
			if (resultJsonArray != null) {
				for (int i = 0; i < resultJsonArray.length(); i++) {
					try {
						currentJsonObject = resultJsonArray.getJSONObject(i);

						if (currentJsonObject.optString(Miscellaneous.NAME).equals(
								de.unistuttgart.vis.wearable.os.cloud.oneDrive.Miscellaneous.getCloudArchiveFolderName())) {
							uploadJsonObject = currentJsonObject;

						}
					} catch (JSONException e) {

					}

				}
				// No upload location was found, therefore it gets created first
				if (uploadJsonObject == null) {

					Map<String, String> folder = new HashMap<String, String>();
					folder.put(Miscellaneous.NAME,
							Miscellaneous.getCloudArchiveFolderName());
					folder.put(Miscellaneous.DESCRIPTION,
							"The folder for the synchronisation of the sensor files");

					OneDrive.getConnectClient().postAsync("me/skydrive/", new JSONObject(folder),
                            new LiveOperationListener() {

                                @Override
                                public void onError(LiveOperationException arg0, LiveOperation arg1) {

                                }

                                // the creation of the folder at One Drive is
                                // finished, the process continues with the
                                // upload of the sensor archive file
                                @Override
                                public void onComplete(LiveOperation arg0) {
                                    Toast.makeText(OneDrive.getMainContext(),
                                            "Directory created, \n" + "proceeding with upload...",
                                            Toast.LENGTH_SHORT).show();
                                    JSONObject resultJsonObject = arg0.getResult();
                                    new OneDriveAsyncUploadTask(OneDrive.getPassword()).execute(resultJsonObject);
                                }
                            });
					// The sensor archive file is directly uploaded to the already
					// existing directory at One Drive
				} else {
					new OneDriveAsyncUploadTask(OneDrive.getPassword()).execute(uploadJsonObject);

				}
			} else {
				Toast.makeText(OneDrive.getMainContext(),
						"Couldn't get file list \nbecause of connectivity problems,", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

	protected LiveOperationListener getListFilesListener() {
		return this.listFoldersListener;
	}

	private class OneDriveAsyncUploadTask extends AsyncTask<JSONObject, String, Boolean> {
        private String password;
		private final ProgressDialog progressDialog;
		private boolean cancelRequest = false;
		private JSONObject uploadJsonObject = null;

		public OneDriveAsyncUploadTask(String password) {
            this.password = password;
			// create Progress Dialog to display the progress of upload
			progressDialog = new ProgressDialog(OneDrive.getMainContext());
			progressDialog.setMax(100);
			progressDialog.setMessage("Uploading " +Miscellaneous.getCloudArchiveName());
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
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);



		}

		@Override
		protected void onProgressUpdate(String... values) {
			Toast.makeText(OneDrive.getMainContext(), values[0], Toast.LENGTH_SHORT).show();
			super.onProgressUpdate(values);
		}

		@Override
		protected Boolean doInBackground(JSONObject... params) {

			this.uploadJsonObject = params[0];
			// TODO upload archive
            final File file = new File(OneDrive.getMainContext().getFilesDir().getAbsolutePath()+File.separator+Miscellaneous.getCloudArchiveName()+".zip");

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

			OneDrive.getConnectClient().uploadAsync(
					uploadJsonObject.optString(Miscellaneous.UPLOAD_LOCATION),Miscellaneous.getCloudArchiveName()+".zip", file,
					OverwriteOption.Overwrite, new LiveUploadOperationListener() {

						@Override
						public void onUploadProgress(int totalBytes, int bytesRemaining, LiveOperation arg2) {
							progressDialog
									.setProgress((int) (((float) (totalBytes - bytesRemaining) / (float) (totalBytes)) * 100));
							if (cancelRequest) {
								publishProgress("Upload cancelled");
								arg2.cancel();

							}
						}

						@Override
						public void onUploadFailed(LiveOperationException arg0, LiveOperation arg1) {
                            file.delete();
							publishProgress("Upload cancelled");
							progressDialog.dismiss();

						}

						@Override
						public void onUploadCompleted(LiveOperation arg0) {
                            file.delete();
							publishProgress("Successfully uploaded sensor archive");
							progressDialog.dismiss();

						}

					}, null);
			return true;

		}
	}

}