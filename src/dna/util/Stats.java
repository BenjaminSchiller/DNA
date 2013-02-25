package dna.util;

import dna.diff.Diff;
import dna.graph.Graph;
import dna.metrics.Metric;

public class Stats {
	public Stats() {
		this.name = null;
		this.memory = null;
		this.timer = new Timer("");
	}

	public Stats(String name) {
		this.name = name;
		this.memory = null;
		this.timer = new Timer("");
	}

	public Stats(Graph g) {
		this(g.toString());
	}

	public Stats(Metric m) {
		this(m.toString());
	}

	public Stats(Diff d) {
		this(d.toString());
	}

	public String toString() {
		return this.timer + "  " + this.memory;
	}

	private String name;

	private Memory memory;

	private Timer timer;

	public String getName() {
		return name;
	}

	public Memory getMemory() {
		return memory;
	}

	public Timer getTimer() {
		return timer;
	}

	public void restart() {
		this.timer.restart();
	}

	public void end() {
		this.timer.end();
		this.memory = new Memory();
		if (this.name == null) {
			System.out.println(this.toString());
		}
	}

	public static String avg(Stats[] stats) {
		long runtime = 0;
		long memory = 0;
		for (Stats s : stats) {
			runtime += s.getTimer().getDutation();
			memory += s.getMemory().getUsed();
		}
		long runtimeTotal = runtime;
		runtime /= stats.length;
		memory /= stats.length;
		return runtime + " msec  " + memory + " Mb (" + runtimeTotal + " msec)";
	}

	public static double avgRuntime(Stats[] stats) {
		return Stats.totalRuntime(stats) / stats.length;
	}

	public static double totalRuntime(Stats[] stats) {
		long runtime = 0;
		for (Stats s : stats) {
			runtime += s.getTimer().getDutation();
		}
		return (double) runtime;
	}
}
