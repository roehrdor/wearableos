package de.unistuttgart.vis.wearable.os.app;

import de.unistuttgart.vis.wearable.os.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.unistuttgart.vis.wearable.os.internalapi.APIFunctions;

/**
 * TODO add info text
 * @author Tobias
 *
 */
public class HARActivity extends Activity {

	final Context context = this;
	private TextView harStatusTxt;
	private Button trainBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_har);

		harStatusTxt = (TextView) findViewById(R.id.har_textView_status);
	    
		trainBtn = (Button) findViewById(R.id.button_har_train);
		// TODO no hard coded string
		if (APIFunctions.isTraining()) {
			trainBtn.setText("Stop training");
		} else {
			trainBtn.setText("Start training");
		}

		switch (APIFunctions.getNeuralNetworkStatus()) {
		case NOTINITIALIZED:
			harStatusTxt.setText(R.string.notinitialized);
			trainBtn.setVisibility(View.GONE);;
			break;
		case INITIALIZED:
			harStatusTxt.setText(R.string.initialized);
			trainBtn.setVisibility(View.VISIBLE);
			break;
		case TRAINED:
			harStatusTxt.setText(R.string.initialized);
			trainBtn.setVisibility(View.VISIBLE);
			break;
		}

		trainBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (APIFunctions.isTraining()) {
                    APIFunctions.stopTraining();
				} else {
					Intent intent = new Intent(context,
							HARActivityTraining.class);
					startActivity(intent);
				}
			}

		});
	}
	


	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onRestart() {
		super.onStart();
		this.onCreate(null);
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.NONE, Menu.NONE,
				R.string.action_settings);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = new Intent(context, HARActivityManager.class);
		startActivity(intent);
		return super.onMenuItemSelected(featureId, item);
	}
}
