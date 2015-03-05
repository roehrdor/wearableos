package de.unistuttgart.vis.wearable.os.app;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityRecognitionModule;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

@SuppressWarnings("unused")
public class HARActivity extends Activity {

	final Context context = this;
	private TextView harStatusTxt;
	private Button trainBtn;
	private Button testBtn;
	private Button manageBtn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder
		.setTitle("disabled").setPositiveButton("ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				HARActivity.this.finish();
			}
			
		}).create().show();
	}
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_har);
//
//		harStatusTxt = (TextView) findViewById(R.id.har_textView_status_detail);
//	    
//		trainBtn = (Button) findViewById(R.id.button_har_train);
//		testBtn = (Button) findViewById(R.id.button_har_test);
//		manageBtn = (Button) findViewById(R.id.button_har_nnmanager);
//
//		harStatusTxt.setText(ActivityRecognitionModule.getInstance()
//				.getNeuralNetworkStatus().toString());
//
//		switch (ActivityRecognitionModule.getInstance()
//				.getNeuralNetworkStatus()) {
//		case NOTINITIALIZED:
//			trainBtn.setVisibility(TRIM_MEMORY_UI_HIDDEN);
//			testBtn.setVisibility(TRIM_MEMORY_UI_HIDDEN);
//		case INITIALIZED:
//			trainBtn.setVisibility(DEFAULT_KEYS_DIALER);
//			testBtn.setVisibility(TRIM_MEMORY_UI_HIDDEN);
//		case TRAINED:
//			trainBtn.setVisibility(DEFAULT_KEYS_DIALER);
//			testBtn.setVisibility(DEFAULT_KEYS_DIALER);
//		}
//
//		trainBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (ActivityRecognitionModule.getInstance().isTraining()) {
//					ActivityRecognitionModule.getInstance().stopTraining();
//				} else {
//					Intent intent = new Intent(context,
//							HARActivityTraining.class);
//					startActivity(intent);
//				}
//			}
//
//		});
//
//		testBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (ActivityRecognitionModule.getInstance().isRecognizing()) {
//					ActivityRecognitionModule.getInstance().stopRecognition();
//				} else {
//					new AsyncTask<Void, Void, Void>() {
//						@Override
//						protected Void doInBackground(Void... params) {
//							ActivityRecognitionModule.getInstance().recognize(
//									2000);
//							return null;
//						}
//					}.execute();
//				}
//			}
//
//		});
//
//		manageBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(context, HARActivityManager.class);
//				startActivity(intent);
//			}
//
//		});
//	}
}
