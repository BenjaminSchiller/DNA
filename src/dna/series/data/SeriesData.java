package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import dna.io.filesystem.Dir;
import dna.io.filesystem.Suffix;
import dna.plot.Plotting;
import dna.series.Values;
import dna.series.aggdata.AggregatedSeries;
import dna.series.lists.MetricDataList;
import dna.util.Log;

public class SeriesData {

	public SeriesData(String dir, String name) {
		this.dir = dir;
		this.name = name;
		this.runs = new ArrayList<RunData>();
		this.aggregation = null;
	}

	public SeriesData(String dir, String name, int size) {
		this.dir = dir;
		this.name = name;
		this.runs = new ArrayList<RunData>(size);
		this.aggregation = null;
	}

	public SeriesData(String dir, String name, RunData[] runs) {
		this(dir, name, runs.length);
		for (RunData run : runs) {
			this.runs.add(run);
		}
		this.aggregation = null;
	}

	public SeriesData(String dir, RunData[] runs, String name,
			AggregatedSeries aggregation) {
		this(dir, name, runs);
		// this.aggregation = aggregation;
	}

	private String dir;

	public String getDir() {
		return this.dir;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	private ArrayList<RunData> runs;

	public ArrayList<RunData> getRuns() {
		return this.runs;
	}

	public RunData getRun(int index) {
		return this.runs.get(index);
	}

	public void addRun(RunData run) {
		this.runs.add(run);
	}

	private RunData aggregation;

	public RunData getAggregation() {
		return this.aggregation;
	}

	public void setAggregation(RunData aggregation) {
		this.aggregation = aggregation;
	}

	/**
	 * This method tests if two different SeriesData objects are from the same
	 * type and can be compared. Checks: - same amount of runs - same runs (uses
	 * RunData.sameType())
	 * 
	 * @author Rwilmes
	 * @date 15.07.2013
	 */
	public static boolean isComparable(SeriesData s1, SeriesData s2) {
		Log.debug("Comparing SeriesData " + s1.getName() + " and SeriesData "
				+ s2.getName());

		if (s1.getRuns().size() != s2.getRuns().size()) {
			Log.warn("different amount of runs on series " + s1.getName()
					+ " and series " + s2.getName());
			return false;
		}

		for (int i = 0; i < s1.getRuns().size(); i++) {
			RunData.isComparable(s1.getRun(i), s2.getRun(i));
		}

		return true;
	}

	public static SeriesData read(String dir, String name, boolean readValues)
			throws IOException {
		String[] runs = Dir.getRuns(dir);
		RunData[] runList = new RunData[runs.length];

		for (String run : runs) {
			int runId = Dir.getRun(run);
			runList[runId] = RunData.read(Dir.getRunDataDir(dir, runId), runId,
					readValues);
		}

		return new SeriesData(dir, name, runList);
	}

	public void compareMetrics(boolean writeValues, boolean plotMetrics)
			throws IOException, InterruptedException {
		MetricDataList exacts = new MetricDataList();
		MetricDataList heuristics = new MetricDataList();

		for (MetricData metric : this.getRuns().get(0).getBatches().get(0)
				.getMetrics().getList()) {
			if (metric.getType() != null) {
				if (metric.getType().equals("exact"))
					exacts.add(metric);
				if (metric.getType().equals("heuristic"))
					heuristics.add(metric);
			}
		}

		for (int run = 0; run < this.getRuns().size(); run++) {
			for (int batch = 0; batch < this.getRun(run).getBatches().size(); batch++) {
				for (MetricData exact : exacts.getList()) {
					MetricData exactTemp = MetricData.read(Dir
							.getMetricDataDir(Dir.getBatchDataDir(
									Dir.getRunDataDir(this.dir, run), batch),
									exact.getName()), exact.getName(), true);
					exactTemp.setType("exact");
					for (MetricData heuristic : heuristics.getList()) {
						MetricData heuristicTemp = MetricData.read(Dir
								.getMetricDataDir(
										Dir.getBatchDataDir(Dir.getRunDataDir(
												this.dir, run), batch),
										heuristic.getName()), exact.getName(),
								true);
						heuristicTemp.setType("heuristic");
						MetricData quality = MetricData.compare(exactTemp,
								heuristicTemp);
						if (writeValues)
							quality.write(Dir.getMetricDataDir(
									Dir.getBatchDataDir(
											Dir.getRunDataDir(dir, run), batch),
									heuristic.getName() + Suffix.quality));
						if (plotMetrics) {
							String metricDir1 = Dir.getMetricDataDir(Dir.getBatchDataDir(Dir.getRunDataDir(this.dir, run), batch),exact.getName());
							String metricDir2 = Dir.getMetricDataDir(Dir.getBatchDataDir(Dir.getRunDataDir(this.dir, run), batch),heuristic.getName());
							System.out.println("DIRS: " + metricDir1 + " " + metricDir2);
							
							Value[] tempValues = new Value[exact.getValues().size() + heuristic.getValues().size()];
							int counter = 0;
							for (Value v : exact.getValues().getList()) {
								tempValues[counter] = new Value(exact.getName() + "." + v,v.getValue());
								counter++;
							}
							for (Value v : heuristic.getValues().getList()) {
								tempValues[counter] = new Value(heuristic.getName() + "." + v,v.getValue());
								counter++;
							}
							// TODO INSERT PLOT
						}
					}
				}

			}
		}

	}

}
