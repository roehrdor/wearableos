package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
import de.unistuttgart.vis.wearable.os.api.APIFunctions;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class StepsModule extends PopupModuleDate {

	public StepsModule(Context context, AttributeSet attrs) {
		super(context, attrs);

		TextView content = new TextView(context);
		
		
          content.setText(APIFunctions.getCurrentActivity().toString());
          
        

		super.createLayout(context, content, R.drawable.steps, "Steps");
	}

}
