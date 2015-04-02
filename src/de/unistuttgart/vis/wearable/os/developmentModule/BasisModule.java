package de.unistuttgart.vis.wearable.os.developmentModule;

import de.unistuttgart.vis.wearable.os.R;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//import com.garmentos.sensor.SensorType;
//import com.garmentos.sensor.VirtualSensor;
//import com.garmentos.garmentOSLib.SettingsModule;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;


/**
 * @author Sophie Ogando
 */
public abstract class BasisModule extends GridLayout {
	public enum State {
		PLAYING, PAUSE
	};

	private static final int BUTTON_SIZE = 90;
	private static final int TITLE_TEXT_SIZE = 20;

	protected String sensorName;
	// protected SensorType sensorType;
	protected Boolean raw;
	// protected SettingsModule settingsModule =
	// com.garmentos.garmentOSLib.SettingsModule.getInstance();
	protected int numberOfValues;
	protected String beginTime;
	protected String endTime;
	protected TextView content;
	
	private PopupWindow popupWindow;

	private String overrideTitle;

	public BasisModule(Context context) {
		super(context);
	}

	public BasisModule(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		useAttributes(context, attrs);
	}

	public BasisModule(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		useAttributes(context, attrs);
	}
	
	private void useAttributes(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes
				(attrs, R.styleable.Module, 0, 0);
		
		try{
			overrideTitle = a.getString(R.styleable.Module_title);
		} finally {
			a.recycle();
		}
		
	}

	private ImageButton popupButton;
	private ImageButton secondButton;

	protected void createLayout(Context context, View content, int imageResId,
			String title) {
		setColumnCount(3);
		setRowCount(2);

		setAlignmentMode(ALIGN_BOUNDS);
		
		if(overrideTitle != null)
			title = overrideTitle;

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
		popupButton = new ImageButton(context);
		popupButton.setMinimumWidth(BUTTON_SIZE);
		popupButton.setMinimumHeight(BUTTON_SIZE);
		popupButton.setMaxHeight(BUTTON_SIZE);
		popupButton.setMaxWidth(BUTTON_SIZE);
		popupButton.setBackgroundColor(Color.parseColor("#606060"));
		popupButton.setImageResource(android.R.drawable.ic_menu_more);
		rightLinearLayout.addView(popupButton);

		popupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {

				System.out.println("clicked");
				final android.widget.PopupWindow pwindow = new android.widget.PopupWindow(view.getContext());

				pwindow.setHeight(BasisModule.this.getHeight() * 2);
				pwindow.setWidth((int) (BasisModule.this.getWidth() * 1.1));

				pwindow.setFocusable(true);

				pwindow.setAnimationStyle(-1);

				pwindow.setContentView(popupWindow.getPopupContent(view.getContext(),
						pwindow));

				pwindow.showAsDropDown(popupButton);

			}
		});
		
		//popup button only visible when popupWindow is set
		popupButton.setVisibility(INVISIBLE);

		secondButton = new ImageButton(context);
		secondButton.setMinimumWidth(BUTTON_SIZE);
		secondButton.setMinimumHeight(BUTTON_SIZE);
		secondButton.setMaxHeight(BUTTON_SIZE);
		secondButton.setMaxWidth(BUTTON_SIZE);
		secondButton.setBackgroundColor(Color.parseColor("#606060"));
		secondButton.setImageResource(android.R.drawable.ic_media_pause);
		rightLinearLayout.addView(secondButton);

		secondButton.setOnClickListener(new View.OnClickListener() {

			private State state = State.PLAYING;

			@Override
			public void onClick(View view) {
				if (state == State.PLAYING) {
					secondButton
							.setImageResource(android.R.drawable.ic_media_play);
					state = State.PAUSE;
					
				} else {
					secondButton
							.setImageResource(android.R.drawable.ic_media_pause);
					state = State.PLAYING;
				}

				OnPauseButton(state);
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
	
	public void setPopupWindow(PopupWindow popupWindow) {
		this.popupWindow = popupWindow;
		//set popupButton visibility according to popupWindow != null
		popupButton.setVisibility(popupWindow != null ? VISIBLE : INVISIBLE);
	}
	
	public PopupWindow getPopupWindow() {
		return popupWindow;
	}
	

	protected void OnPauseButton(State state) {
		
	}

}
