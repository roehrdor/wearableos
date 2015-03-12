/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.api;

/**
 * <p>
 * As provided by the
 * {@link APIFunctions#registerCallback(IGarmentCallback, int)} the
 * developer has the ability to register for several different events that are
 * recognized during the runtime of the GarmentOS Service.
 * </p>
 * <p>
 * This class provides the flags for these events the developer can register for
 * and will get call backed by the service. As the developer wishes, we can
 * register for none, one or multiple events, depending on his needs. <br/>
 * Multiple flags can be provided by either calling the
 * {@link CallbackFlags#multipleCallbacks(int...)} function or just putting them
 * togehter using the binary or operator. <br/>
 * Registering for {@link CallbackFlags#NONE} has the same effect as
 * unregistering for all or all registered flags
 * </p>
 * <p>
 * <ul>
 * <li>{@link CallbackFlags#NONE} - register for none or unregister for all
 * callbacks, no further callback will be made</li>
 * <li>{@link CallbackFlags#SENSOR_ADDED} - register for a callback if a new
 * sensor has been added to the device</li>
 * <li>{@link CallbackFlags#SENSOR_REMOVED} - register for a callback if a new
 * sensor has been removed from the device</li>
 * <li>{@link CallbackFlags#GESTURE_PERFORMED} - register for a callback if a
 * gesture has been recognized</li>
 * <li>{@link CallbackFlags#STEP_MADE} - register for a callback if a step has
 * been made</li>
 * <li>{@link CallbackFlags#PRESSURE_DETECTED} - register for a callback if a
 * pressure event has been detected</li>
 * <li>{@link CallbackFlags#ACTIVITY_CHANGED} - register for a callback if the
 * activity has changed</li>
 * <li>{@link CallbackFlags#EMOTION_CHANGED} - register for a callback if the
 * emotion has changed</li>
 * <li>{@link CallbackFlags#BODY_TEMPERATURE_CHANGED} - register for a callback
 * if the body temperature has changed</li>
 * <li>{@link CallbackFlags#HEART_RATE_CHANGED} - register for a callback if the
 * heart rate has changed</li>
 * <li>{@link CallbackFlags#PULSE_CHANGED} - register for a callback if the
 * pulse has changed</li>
 * <li>{@link CallbackFlags#SKINCONDUCTANCE_CHANGED} - register for a callback
 * if the skin conductance has changed</li>
 * </ul>
 * </p>
 * 
 * @author roehrdor
 * @date 2014-11-24
 * @version 1.0
 */
public final class CallbackFlags {
	/**
	 * There shall be no object of this class
	 */
	private CallbackFlags() {
	}

	public static final int NONE = 0x0;
	public static final int SENSOR_ADDED = 0x1;
	public static final int SENSOR_REMOVED = 0x2;
	public static final int GESTURE_PERFORMED = 0x4;
	public static final int STEP_MADE = 0x8;
	public static final int PRESSURE_DETECTED = 0x10;
	public static final int ACTIVITY_CHANGED = 0x20;
	public static final int EMOTION_CHANGED = 0x40;
	public static final int BODY_TEMPERATURE_CHANGED = 0x80;
	public static final int HEART_RATE_CHANGED = 0x100;
	public static final int PULSE_CHANGED = 0x200;
	public static final int SKINCONDUCTANCE_CHANGED = 0x400;
    public static final int VALUE_CHANGED = 0x800;

	/**
	 * <p>
	 * Create a single flag value from multiple single flags. This function can
	 * be used since
	 * {@link APIFunctions#registerCallback(IGarmentCallback, int)} needs
	 * to be called with a single flag value. Instead of calling this function
	 * multiple times the developer can call this function to put multiple flags
	 * together.
	 * </p>
	 * <p>
	 * This function puts the binary or values together
	 * </p>
	 * 
	 * @param flags
	 *            the flags that shall be put into a single flag value
	 * @return the single flag value for all the given flags
	 */
	public static int multipleCallbacks(int... flags) {
		int flag = 0x0;
		for (int i : flags)
			flag |= i;
		return flag;
	}
}
