package de.unistuttgart.vis.wearable.os.developmentModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityEnum;
import de.unistuttgart.vis.wearable.os.app.SensorDetailActivity;
import android.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public abstract class PopupModuleActivities extends BasisModule {
	public PopupModuleActivities(Context context) {
		super(context);
	}

	public PopupModuleActivities(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PopupModuleActivities(Context context, AttributeSet attrs) {
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
		textview.setText("Choose Activity:");
		textview.setPadding(0, 0, 0, 10);
		textview.setGravity(Gravity.LEFT);

		textview.setTextColor(Color.WHITE);
		textview.setTextSize(22);

		upperLayout.addView(textview);

		// list view

		listView = new ListView(context);

		listView.setId(android.R.layout.simple_list_item_1);

		ArrayAdapter<ActivityEnum> enumAdapter = new ArrayAdapter<ActivityEnum>(
				context, listView.getId(), ActivityEnum.values());

		listView.setAdapter(enumAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				String  itemValue = (String) listView.getItemAtPosition(position);
			     
				pWindow.dismiss();
			
			}
		});

		downLayout.addView(listView);

		layout.addView(upperLayout);
		layout.addView(downLayout);

		return layout;
	}

}