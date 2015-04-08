package cz.kinst.jakub.diploma.udpbroadcast.jdeecoextension;

import cz.cuni.mff.d3s.deeco.scheduler.CurrentTimeProvider;

/**
 * Default current time provider using {@link System#currentTimeMillis()} method
 */
public class DefaultCurrentTimeProvider implements CurrentTimeProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getCurrentMilliseconds() {
		return System.currentTimeMillis();
	}
}
