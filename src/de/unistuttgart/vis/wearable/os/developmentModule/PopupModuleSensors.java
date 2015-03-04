package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.PSensor;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public abstract class PopupModuleSensors extends BasisModule {
	
	
	
	public PopupModuleSensors(Context context) {
		super(context);
	}

	public PopupModuleSensors(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PopupModuleSensors(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private ListView listView;

	protected View getPopupContent(final Context context,
			final PopupWindow pWindow) {

		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(VERTICAL);

		LinearLayout upperLayout = new LinearLayout(context);
		upperLayout.setOrientation(VERTICAL);
		upperLayout.setGravity(Gravity.TOP);

		LinearLayout downLayout = new LinearLayout(context);
		upperLayout.setOrientation(HORIZONTAL);
		upperLayout.setGravity(Gravity.CENTER);

		// text view

		TextView textview = new TextView(context);
		textview.setText("Choose Sensor:");
		textview.setPadding(0, 0, 0, 10);
		textview.setGravity(Gravity.LEFT);

		textview.setTextColor(Color.WHITE);
		textview.setTextSize(22);

		upperLayout.addView(textview);

		// list view

		listView = new ListView(context);

		listView.setId(android.R.layout.simple_list_item_1);

		ArrayAdapter<PSensor> sensorsAdapter = new ArrayAdapter<PSensor>(
				context, listView.getId(), APIFunctions.getAllSensors());

		listView.setAdapter(sensorsAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				onSensorChanged((PSensor) listView.getItemAtPosition(position));
			     
				pWindow.dismiss();
			
			}
		});

		downLayout.addView(listView);

		layout.addView(upperLayout);
		layout.addView(downLayout);

		return layout;
	}
	
	protected void onSensorChanged(PSensor selecedSensor) {
		
	}

}

//
//
//@Override
//protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_sensorlist);
//
//    ListView listView = (ListView) findViewById(R.id.listView1);
//    sensors = APIFunctions.API_getAllSensors();
//    listViewOptions(listView);
//}
//
//private class SensorListAdapter extends ArrayAdapter<PSensor> {
//
//    private final Activity context;
//    private final PSensor[] sensors;
//    private Switch mySwitch;
//
//    public SensorListAdapter(Activity context, PSensor[] sensors) {
//        super(context, R.layout.custom_list_layout, sensors);
//        this.context = context;
//        this.sensors = sensors;
//    }