package de.unistuttgart.vis.wearable.os.app;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityRecognitionModule;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * TODO add info text
 * @author Tobias
 *
 */
public class HARActivity extends Activity {

	final Context context = this;
	private TextView harStatusTxt;
	private Button trainBtn;
	private Button testBtn;
	private Button manageBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_har);

		harStatusTxt = (TextView) findViewById(R.id.har_textView_status_detail);
	    
		trainBtn = (Button) findViewById(R.id.button_har_train);
		// TODO no hard coded string
		if(ActivityRecognitionModule.getInstance().isTraining()) {
			trainBtn.setText("Stop training");
		} else {
			trainBtn.setText("Start training");
		}
		testBtn = (Button) findViewById(R.id.button_har_test);
		// TODO no hard coded string
		if(ActivityRecognitionModule.getInstance().isTraining()) {
			testBtn.setText("Stop recognizing");
		} else {
			testBtn.setText("Start recognizing");
		}
		manageBtn = (Button) findViewById(R.id.button_har_nnmanager);

		harStatusTxt.setText(ActivityRecognitionModule.getInstance()
				.getNeuralNetworkStatus().toString());

		switch (ActivityRecognitionModule.getInstance()
				.getNeuralNetworkStatus()) {
		case NOTINITIALIZED:
			trainBtn.setVisibility(View.INVISIBLE);;
			testBtn.setVisibility(View.INVISIBLE);
			break;
		case INITIALIZED:
			trainBtn.setVisibility(View.VISIBLE);
			testBtn.setVisibility(View.INVISIBLE);
			break;
		case TRAINED:
			trainBtn.setVisibility(View.VISIBLE);
			testBtn.setVisibility(View.VISIBLE);
			break;
		}

		trainBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ActivityRecognitionModule.getInstance().isTraining()) {
					ActivityRecognitionModule.getInstance().stopTraining();
				} else {
					Intent intent = new Intent(context,
							HARActivityTraining.class);
					startActivity(intent);
				}
			}

		});

		testBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ActivityRecognitionModule.getInstance().isRecognizing()) {
					ActivityRecognitionModule.getInstance().stopRecognition();
				} else {
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							ActivityRecognitionModule.getInstance().recognize(
									2000);
							return null;
						}
					}.execute();
				}
			}

		});

		manageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HARActivityManager.class);
				startActivity(intent);
			}

		});
	}
}
