package de.unistuttgart.vis.wearable.os.developmentModule;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
//import com.garmentos.sensor.SensorType;
//import com.garmentos.sensor.VirtualSensor;
//import com.garmentos.garmentOSLib.SettingsModule;
import android.content.Context;

import android.graphics.Color;

public abstract class BasisModule extends GridLayout {

	private enum State {
		PLAYING, PAUSE
	};

	private static final int BUTTON_SIZE = 90;
	private static final int TITLE_TEXT_SIZE = 20;

	protected String sensorName;
	//protected SensorType sensorType;
	protected Boolean raw;
	//protected SettingsModule settingsModule = com.garmentos.garmentOSLib.SettingsModule.getInstance();
	protected int numberOfValues;
	protected String beginTime;
	protected String endTime;
	protected TextView content;
	//protected VirtualSensor virtualSensor;

	public BasisModule(Context context) {
		super(context);
	}

	public BasisModule(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BasisModule(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private ImageButton firstButton;
	private ImageButton secondButton;

	protected void createLayout(Context context, View content, int imageResId,
			String title) {
		setColumnCount(3);
		setRowCount(2);

		setAlignmentMode(ALIGN_BOUNDS);

		// add Title row
		LinearLayout titleLinearLayout = new LinearLayout(context);
		LayoutParams params = new LayoutParams();
		params.columnSpec = spec(0, 2);
		params.rowSpec = spec(0);
		params.setGravity(Gravity.CENTER_HORIZONTAL);

		titleLinearLayout.setLayoutParams(params);

		TextView titleTextView = new TextView(context);
		titleLinearLayout.addView(titleTextView);
		titleTextView.setTextSize(TITLE_TEXT_SIZE);
		titleTextView.setText(title);
		
		titleLinearLayout.setBackgroundColor(Color.parseColor("#c0d6e4"));

		this.addView(titleLinearLayout);

		// add right button column
		LinearLayout rightLinearLayout = new LinearLayout(context);
		rightLinearLayout.setOrientation(LinearLayout.VERTICAL);
		params = new LayoutParams();
		params.setGravity(Gravity.NO_GRAVITY);
		params.columnSpec = spec(2);
		params.rowSpec = spec(0, 2);

		
		rightLinearLayout.setLayoutParams(params);
		
		rightLinearLayout.setBackgroundColor(Color.parseColor("#c0d6e4"));

		this.addView(rightLinearLayout);

		// add Buttons
		firstButton = new ImageButton(context);
		firstButton.setMinimumWidth(BUTTON_SIZE);
		firstButton.setMinimumHeight(BUTTON_SIZE);
		firstButton.setMaxHeight(BUTTON_SIZE);
		firstButton.setMaxWidth(BUTTON_SIZE);
		firstButton.setBackgroundColor(Color.parseColor("#606060"));
		firstButton.setImageResource(android.R.drawable.ic_menu_more);
		rightLinearLayout.addView(firstButton);
		

		firstButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {

						System.out.println("clicked");
						final PopupWindow pwindow = new PopupWindow(view.getContext());
						
						pwindow.setHeight(BasisModule.this.getHeight() * 2);
						pwindow.setWidth((int)(BasisModule.this.getWidth()* 1.1));
						
//						pwindow.setContentView(BasisModule.this.getPopupContent(item.getItemId(), view.getContext()));
						
//						pwindow.update();
						
					//	pwindow.setBackgroundDrawable(null);
					
						pwindow.setFocusable(true);
						
						pwindow.setAnimationStyle(-1);
						
						pwindow.setContentView(getPopupContent(view.getContext(), pwindow));
						
						
						
						pwindow.showAsDropDown(firstButton);
						
//						return true;

					}
				});
//
//				popup.show(); // showing popup menu
//			}
////		});


		secondButton = new ImageButton(context);
		secondButton.setMinimumWidth(BUTTON_SIZE);
		secondButton.setMinimumHeight(BUTTON_SIZE);
		secondButton.setMaxHeight(BUTTON_SIZE);
		secondButton.setMaxWidth(BUTTON_SIZE);
		secondButton.setBackgroundColor(Color.parseColor("#606060"));
		secondButton.setImageResource(android.R.drawable.ic_media_pause);
		rightLinearLayout.addView(secondButton);

		secondButton.setOnClickListener(new View.OnClickListener() {

			private State state = State.PAUSE;

			@Override
			public void onClick(View view) {
				if (state == State.PAUSE) {
					secondButton
							.setImageResource(android.R.drawable.ic_media_play);
					state = State.PLAYING;
				} else {
					secondButton
							.setImageResource(android.R.drawable.ic_media_pause);
					state = State.PAUSE;
				}

			}
		});

		ImageView imageView = new ImageView(context);
		imageView.setImageResource(imageResId);

		params = new LayoutParams(spec(1), spec(0));
		params.setGravity(Gravity.CENTER_VERTICAL);
		params.width = 100;
		params.height = 100;
		imageView.setLayoutParams(params);

		this.addView(imageView);
		
		this.setBackgroundColor(Color.parseColor("#c0d6e4"));

		params = new LayoutParams(spec(1), spec(1));
		params.setGravity(Gravity.CENTER);
		content.setLayoutParams(params);

		this.addView(content);
	}
	
	protected abstract View getPopupContent(final Context context, final PopupWindow pWindow);


}
