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

	public long getTotal() {
		return this.total / 1024 / 1024;
	}

	public long getFree() {
		return this.free / 1024 / 1024;
	}

	public long getMax() {
		return this.max / 1024 / 1024;
	}

	public long getUsed() {
		return this.used / 1024 / 1024;
	}

	public String toString() {
		return this.getUsed() + " Mb";
	}
}
