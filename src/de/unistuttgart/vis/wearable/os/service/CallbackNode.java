/*
 * This file is part of the Garment OS Project. For any details concerning use 
 * of this project in source or binary form please refer to the provided license
 * file.
 * 
 * (c) 2014-2015 GarmentOS
 */
package de.unistuttgart.vis.wearable.os.service;

import de.unistuttgart.vis.wearable.os.api.CallbackFlags;
import de.unistuttgart.vis.wearable.os.api.IGarmentCallback;
import android.os.IBinder;
import android.os.IInterface;

/**
 * This class defines the nodes of the RemoteCallbackList. To enable callbacks
 * for specific Applications we need to store some more details about the
 * application which are stored inside this node.
 * 
 * @author roehrdor
 */
public final class CallbackNode implements IInterface {
	private int pid;
	private int uuid;
	private int callbackFlag;
	private IGarmentCallback callbackHandle;

	/**
	 * Create a new CallbackNode with the given processID, the given UUID and
	 * the given callbackHandle
	 * 
	 * @param pid
	 *            the process id of the calling application
	 * @param uuid
	 *            the uuid of the calling application
	 * @param callbackHandle
	 *            the handle to send callbacks to
	 */
	public CallbackNode(int pid, int uuid,
			IGarmentCallback callbackHandle) {
		this(pid, uuid, callbackHandle, CallbackFlags.NONE);
	}

	/**
	 * Create a new CallbackNode with the given processID, the given UUID, the
	 * given callbackHandle and the given callback flag
	 * 
	 * @param pid
	 *            the process id of the calling application
	 * @param uuid
	 *            the uuid of the calling application
	 * @param callbackHandle
	 *            the handle to send callbacks to
	 * @param callbackFlags
	 *            the callback flags
	 */
	public CallbackNode(int pid, int uuid,
			IGarmentCallback callbackHandle, int callbackFlags) {
		this.pid = pid;
		this.uuid = uuid;
		this.callbackHandle = callbackHandle;
		this.callbackFlag = callbackFlags;
	}

	/**
	 * Get the process id of the node
	 * 
	 * @return process id
	 */
	public int getPID() {
		return this.pid;
	}

	/**
	 * Get the uuid of the node
	 * 
	 * @return the uuid
	 */
	public int getUUID() {
		return this.uuid;
	}

	/**
	 * Get the callback Handle of the node
	 * 
	 * @return the callback handle
	 */
	public IGarmentCallback getCallbackHandle() {
		return this.callbackHandle;
	}
	
	public void addCallbackFlag(int flag) {
		this.callbackFlag |= flag;
	}
	
	public void toggleFlag(int flag) {
		this.callbackFlag ^= flag;
	}
	
	public void removeFlag(int flag) {
		this.callbackFlag &= (flag ^ 0xffffffff);
	}
	
	public boolean isFlagSet(int flag) {
		return (this.callbackFlag & flag) != 0x0;
	}

	@Override
	public IBinder asBinder() {
		return this.callbackHandle.asBinder();
	}
}
