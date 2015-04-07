package cz.kinst.jakub.diploma.offloading.model;

import java.io.Serializable;

/**
 * NFP Data containing information of particular backend performance on a device.
 * This is abstract class. For simple performance metrics, use {@link SingleValueNFPData}
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public abstract class NFPData implements Serializable {
	private long timestamp;


	/**
	 * Get time of creation
	 *
	 * @return time of creation in ms
	 */
	public long getTimestamp() {
		return timestamp;
	}


	/**
	 * Set time of creation
	 *
	 * @param timestamp time of creation in ms
	 */
	public void setTimestamp(long timestamp) {

		this.timestamp = timestamp;
	}
}
