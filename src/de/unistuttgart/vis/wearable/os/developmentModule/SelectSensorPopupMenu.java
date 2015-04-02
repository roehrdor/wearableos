package de.unistuttgart.vis.wearable.os.developmentModule;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import de.unistuttgart.vis.wearable.os.api.PSensor;

/**
 * @author Sophie Ogando
 */
public class SelectSensorPopupMenu extends PopupWindow {
	
	public static interface SelectedSensorChangedListener {
		public void onSelectedSensorChanged(PSensor sensor);
	}
	
	private Set<SelectedSensorChangedListener> listeners = new HashSet<SelectedSensorChangedListener>();
	
	public void addSelectedSensorChangedListener(SelectedSensorChangedListener listener) {
		listeners.add(listener);
	}
	
	public void removeSelectedSensorChangedListener(SelectedSensorChangedListener listener) {
		listeners.remove(listener);
	}

	
	protected View getPopupContent(final Context context,
			final android.widget.PopupWindow pWindow) {

		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout upperLayout = new LinearLayout(context);
		upperLayout.setOrientation(LinearLayout.VERTICAL);
		upperLayout.setGravity(Gravity.TOP);

		LinearLayout downLayout = new LinearLayout(context);
		upperLayout.setOrientation(LinearLayout.HORIZONTAL);
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

		final ListView listView = new ListView(context);

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
	
	private void onSensorChanged(PSensor sensor) {
		for(SelectedSensorChangedListener listener : listeners)
			listener.onSelectedSensorChanged(sensor);
	}
}
