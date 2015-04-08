package cz.kinst.jakub.diploma.offloading.utils;


import com.google.common.eventbus.EventBus;

/**
 * Provider of Google Guava EventBus
 * {@see https://code.google.com/p/guava-libraries/wiki/EventBusExplained}
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class BusProvider {
	private static final EventBus sBus = new EventBus();


	/**
	 * Get app-wide singleton {@link com.google.common.eventbus.EventBus} instance
	 * {@see https://code.google.com/p/guava-libraries/wiki/EventBusExplained}
	 *
	 * @return EventBus singleton instance
	 */
	public static EventBus get() {
		return sBus;
	}
}
