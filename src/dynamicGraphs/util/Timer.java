package dynamicGraphs.util;

public class Timer {
	private long start;

	private long duration;

	public Timer() {
		this.restart();
		this.duration = 0;
	}
	
	public void restart(){
		this.start = System.currentTimeMillis();
	}
	
	public String end(){
		this.duration += System.currentTimeMillis() - this.start;
		return this.toString();
	}
	
	public long getDutation(){
		return this.duration;
	}
	
	public String toString() {
		return this.duration + " msec";
	}
}
