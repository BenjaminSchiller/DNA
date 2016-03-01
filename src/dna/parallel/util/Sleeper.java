package dna.parallel.util;

public class Sleeper {

	protected long millis;
	protected int nanos;

	protected long timeoutAfter;
	protected long start;

	/**
	 * 
	 * @param sleepMillis
	 *            milliseconds to sleep
	 * @param sleepNanos
	 *            nanoseconds to sleep
	 * @param timeoutAfter
	 *            time in milliseconds after which the operation is considered
	 *            to have timed out (relative to the start time)
	 */
	public Sleeper(long millis, int nanos, long timeoutAfter) {
		this.millis = millis;
		this.nanos = nanos;
		this.timeoutAfter = timeoutAfter;
		this.start = System.currentTimeMillis();
	}

	/**
	 * 
	 * @param sleepMillis
	 *            milliseconds to sleep
	 * @param timeoutAfter
	 *            time in milliseconds after which the operation is considered
	 *            to have timed out (relative to the start time)
	 */
	public Sleeper(long millis, long timeoutAfter) {
		this(millis, 0, timeoutAfter);
	}

	/**
	 * wait for the specified time
	 */
	public void sleep() {
		try {
			System.out.println("sleeping: " + this.millis);
			Thread.sleep(this.millis, this.nanos);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return true if the time since the last reset (or initialization) is
	 *         greater than timeoutAfter.
	 */
	public boolean isTimedOut() {
		System.out.println("checking");
		return (System.currentTimeMillis() - this.start) > this.timeoutAfter;
	}

	/**
	 * resets the start to the current timestamp
	 */
	public void reset() {
		this.start = System.currentTimeMillis();
		System.out.println("reset: " + this.start);
	}

}
