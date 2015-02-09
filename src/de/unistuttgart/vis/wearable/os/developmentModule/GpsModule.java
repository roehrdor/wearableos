package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class GpsModule extends PopupModule1 {

	public GpsModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		
		LinearLayout content = new LinearLayout(context);
		Button button = new Button(context);
		
		button.setText("show in map");
		button.setBackgroundColor(Color.parseColor("#606060"));
		button.setTextColor(Color.WHITE);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
				openMapsActivity();
				
			}
		});
		
		content.addView(button);
		

		super.createLayout(context, content, R.drawable.gps, "GPS");
	}
	
	public void openMapsActivity() {
		
//		Context context = getContext();
//		
//		Intent intent = new Intent(context, MapsActivity.class);
//
//		context.startActivity(intent);
		
	}
}
