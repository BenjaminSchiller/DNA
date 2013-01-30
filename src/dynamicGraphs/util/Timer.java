package dynamicGraphs.util;

public class Timer {
	private String name;

	private long start;

	private long duration;

	public Timer(String name) {
		this.name = name;
		this.start = System.currentTimeMillis();
		this.duration = 0;
	}

	public void restart() {
		this.start = System.currentTimeMillis();
	}

	public String end() {
		this.duration += System.currentTimeMillis() - this.start;
		return this.toString();
	}

	public RunTime getRuntime() {
		return new RunTime(this.name, this.duration);
	}

	public long getDutation() {
		return this.duration;
	}

	public String toString() {
		return this.duration + " msec";
	}
}
