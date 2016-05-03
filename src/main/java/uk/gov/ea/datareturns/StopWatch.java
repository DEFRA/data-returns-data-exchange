/**
 *
 */
package uk.gov.ea.datareturns;

/**
 * Extends the Spring stopwatch to provide additional convenience methods
 *
 * @author Sam Gardner-Dell
 *
 */
public class StopWatch extends org.springframework.util.StopWatch {

	/**
	 *
	 */
	public StopWatch() {
	}

	/**
	 * @param id
	 */
	public StopWatch(final String id) {
		super(id);
	}

	public void startTask(final String taskName) {
		if (super.isRunning()) {
			super.stop();
		}
		super.start(taskName);
	}
}
