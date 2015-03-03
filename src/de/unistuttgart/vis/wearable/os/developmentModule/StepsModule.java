package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.activity.Activity;
import de.unistuttgart.vis.wearable.os.activityRecognition.ActivityRecognitionModule;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class StepsModule extends PopupModule1 {

	public StepsModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		TextView content = new TextView(context);
		
		

		super.createLayout(context, content, R.drawable.steps, "Steps");
	}

}
