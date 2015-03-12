/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.api;

/**
 * This class represents a result object from an asynchronous API function call.
 * Therefore instances of this class will be set as first parameter to any
 * function in the {@link APIFunctionsAsync} class. The result will be saved as
 * attribute and may be elected by calling the
 * {@link AsyncResultObject#getObject()} function provided by this class. We use
 * an object to an object here since parameters (references) are passed by value
 * but we need to modify the underlying object.
 * 
 * @author roehrdor
 */
public class AsyncResultObject {
	private Object obj;

	/**
	 * Get the resulting object
	 * 
	 * @return the result
	 */
	public Object getObject() {
		return this.obj;
	}

	/**
	 * Set the resulting object
	 * 
	 * @param obj
	 *            the result
	 */
	public void setObject(Object obj) {
		this.obj = obj;
	}
}
