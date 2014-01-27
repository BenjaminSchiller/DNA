package dna.util;

public class Stats {
	private long startTime;

	private long endTime;

	private long totalTime;

	private long totalMemory;

	public Stats() {
		this.startTime = System.currentTimeMillis();
	}

	public void end() {
		this.endTime = System.currentTimeMillis();
		this.totalTime = this.endTime - this.startTime;
		this.totalMemory = Runtime.getRuntime().totalMemory();
		System.out.println("time: " + this.totalTime + " msec / "
				+ (this.totalTime / 1000) + " sec");
		System.out.println("memory: " + (this.totalMemory / 1024 / 1024)
				+ " MB");
	}
}
