package dna.util;

public class Memory {
	public Memory() {
		// System.gc();
		this.total = Runtime.getRuntime().totalMemory();
		this.free = Runtime.getRuntime().freeMemory();
		this.max = Runtime.getRuntime().maxMemory();
		this.used = this.total - this.free;
	}

	private long total;

	private long free;

	private long max;

	private long used;

	public double getTotal() {
		return (double) this.total / 1024.0 / 1024.0;
	}

	public double getFree() {
		return (double) this.free / 1024.0 / 1024.0;
	}

	public double getMax() {
		return (double) this.max / 1024.0 / 1024.0;
	}

	public double getUsed() {
		return (double) this.used / 1024.0 / 1024.0;
	}

	public String toString() {
		return (double) this.getUsed() + " Mb";
	}
}
