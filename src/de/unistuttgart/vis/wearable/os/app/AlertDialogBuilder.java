package de.unistuttgart.vis.wearable.os.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogBuilder {
	
	AlertDialog.Builder alertDialog;

	/**
	 * TODO
	 * @param context
	 * @param title
	 * @param message
	 * @param button
	 */
	public AlertDialogBuilder(Context context, int title, int message, int button) {
		alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.setCancelable(false);
		alertDialog.show();
	}
}
