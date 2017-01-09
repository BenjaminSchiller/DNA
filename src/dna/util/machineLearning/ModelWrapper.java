package dna.util.machineLearning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.BatchData;
import dna.series.lists.BatchDataList;
import dna.test.Test.LabelMode;
import dna.util.Log;

public class ModelWrapper {

	public class LabelEntry {
		protected String attackClass;
		protected long attackClassId;
		protected String attack;
		protected long attackId;

		public LabelEntry(String attack, long attackId, String attackClass, long attackClassId) {
			this.attack = attack;
			this.attackId = attackId;
			this.attackClass = attackClass;
			this.attackClassId = attackClassId;
		}

		public String getAttackClass() {
			return attackClass;
		}

		public long getAttackClassId() {
			return attackClassId;
		}

		public String getAttack() {
			return attack;
		}

		public long getAttackId() {
			return attackId;
		}
	}

	public class TruthLabeler {

		protected String truthListPath;
		protected ArrayList<Long> timestamps;
		protected HashMap<Long, ArrayList<LabelEntry>> entries;

		public TruthLabeler(String truthListPath) {
			this.truthListPath = truthListPath;
			this.timestamps = new ArrayList<Long>();

			try {
				this.entries = readListEntries(truthListPath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (long l : this.entries.keySet()) {
				this.timestamps.add(l);
			}

			Collections.sort(this.timestamps);
		}

		protected HashMap<Long, ArrayList<LabelEntry>> readListEntries(String truthListPath) throws IOException {
			HashMap<Long, ArrayList<LabelEntry>> entryMap = new HashMap<Long, ArrayList<LabelEntry>>();

			Reader r = new Reader("", truthListPath);

			String line = "";
			while ((line = r.readString()) != null) {
				String[] splits = line.split("\t");
				long timestamp = Long.parseLong(splits[0]);
				LabelEntry label = new LabelEntry(splits[1], Long.parseLong(splits[3]), splits[2],
						Long.parseLong(splits[4]));
				if (entryMap.containsKey(timestamp)) {
					entryMap.get(timestamp).add(label);
				} else {
					ArrayList<LabelEntry> tempList = new ArrayList<LabelEntry>();
					tempList.add(label);
					entryMap.put(timestamp, tempList);
				}
			}

			return entryMap;
		}

		public void print() {
			for (Long l : this.timestamps) {
				String line = "" + l;
				ArrayList<LabelEntry> list = this.entries.get(l);
				for (LabelEntry e : list) {
					line += "\t" + e.getAttack() + "\t" + e.getAttackId();
				}
				System.out.println(line);
			}
		}

		public String getAttacksAtTimestamp(long timestamp, int lifeTime, LabelMode labelMode) {
			ArrayList<LabelEntry> foundEntries = new ArrayList<LabelEntry>();

			for (long l : this.timestamps) {
				if (l <= timestamp && ((l + lifeTime) >= timestamp)) {
					ArrayList<LabelEntry> labels = this.entries.get(l);
					for (LabelEntry e : labels) {
						if (!foundEntries.contains(e))
							foundEntries.add(e);
					}
				}
			}

			long attack = 0;
			long attackId = 0;
			long attackClassId = 0;

			ArrayList<Long> attackIds = new ArrayList<Long>();
			ArrayList<Long> attackClassIds = new ArrayList<Long>();
			for (LabelEntry e : foundEntries) {
				if (!attackIds.contains(e.getAttackId()))
					attackIds.add(e.getAttackId());
				if (!attackClassIds.contains(e.getAttackClassId()))
					attackClassIds.add(e.getAttackClassId());
			}

			if (foundEntries.size() > 0)
				attack = 1;
			for (long l : attackIds)
				attackId += l;
			for (long l : attackClassIds)
				attackClassId += l;

			switch (labelMode) {
			case attackClasses:
				return "" + attackClassId;
			case attacks:
				return "" + attackId;
			case zeroOneMode:
				return "" + attack;
			default:
				return "unknown";
			}

			// return attack + "\t" + attackId + "\t" + attackClassId;
		}
	}

	public class PredictionList {

		protected ArrayList<Long> timestamps;
		protected ArrayList<String> predictions;
		protected ArrayList<String> truths;

		public PredictionList() {
			this.timestamps = new ArrayList<Long>();
			this.predictions = new ArrayList<String>();
			this.truths = new ArrayList<String>();
		}

		public PredictionList(int size) {
			this.timestamps = new ArrayList<Long>(size);
			this.predictions = new ArrayList<String>(size);
			this.truths = new ArrayList<String>(size);
		}

		public void addPrediction(long timestamp, String prediction, String truth) {
			this.timestamps.add(timestamp);
			this.predictions.add(prediction);
			this.truths.add(truth);
		}

		public ArrayList<Long> getTimestamps() {
			return this.timestamps;
		}

		public ArrayList<String> getPredictions() {
			return this.predictions;
		}

		public ArrayList<String> getTruths() {
			return this.truths;
		}

		public String getPrediction(int index) {
			return timestamps.get(index) + "\t" + predictions.get(index) + "\t" + truths.get(index);
		}

		public int size() {
			return this.predictions.size();
		}

		public void write(String dir, String filename) throws IOException {
			Writer w = new Writer(dir, filename);
			for (int i = 0; i < size(); i++) {
				w.writeln(getPrediction(i));
			}
			w.close();
		}
	}

	public class FeatureListEntry {

		protected String name;
		protected double score;

		protected String metric;
		protected String value;

		public FeatureListEntry(String name, double score) {
			this.name = name;
			this.score = score;

			// parse metric and value
			String[] splits = name.split(metricValueDelimiter);
			this.metric = splits[0];
			this.value = splits[1];
		}

		public String getName() {
			return name;
		}

		public double getScore() {
			return score;
		}

		public String getMetric() {
			return metric;
		}

		public String getValue() {
			return value;
		}

		public double obtainValueFromBatch(BatchData b) {
			if (metric.equals("statistics")) {
				return b.getValues().get(value).getValue();
			} else {
				return b.getMetrics().get(metric).getValues().get(value).getValue();
			}
		}
	}

	// statics
	public static final long timeoutInterval = 10000;
	public static final String initMessage = "init ok";
	public static final String exitMessage = "exit";
	public static final String featureListDelimiter = "\t";
	public static final String metricValueDelimiter = "~";
	public static final String featureVectorDelimiter = "\t";

	// class fields
	protected String scriptPath;
	protected String featureListPath;
	protected String blackboxPath;

	protected ArrayList<FeatureListEntry> features;
	protected int numberOfFeatures;

	protected InputStream in;
	protected OutputStream out;

	protected BufferedReader r;

	protected ProcessBuilder interpreter;
	protected Process p;

	protected TruthLabeler truthLabeler;

	// constructor
	public ModelWrapper(String scriptPath, String featureListPath, int numberOfFeatures, String blackboxPath)
			throws IOException, InterruptedException {
		this(scriptPath, featureListPath, numberOfFeatures, blackboxPath, null);
	}

	public ModelWrapper(String scriptPath, String featureListPath, int numberOfFeatures, String blackboxPath,
			String truthListPath) throws IOException, InterruptedException {
		Log.info("setting up blackbox wrapper");
		Log.info("");
		Log.info("script:\t" + scriptPath);
		Log.info("");
		Log.info("features:\t" + featureListPath);
		Log.info("# features:\t" + numberOfFeatures);
		Log.info("");
		Log.info("blackbox:\t" + blackboxPath);
		Log.info("");
		Log.infoSep();
		Log.info("");

		this.scriptPath = scriptPath;
		this.featureListPath = featureListPath;
		this.features = readFeatureList(featureListPath);
		this.numberOfFeatures = numberOfFeatures;
		this.blackboxPath = blackboxPath;

		if (truthListPath != null)
			this.truthLabeler = new TruthLabeler(truthListPath);

		this.interpreter = new ProcessBuilder("python", scriptPath);
		this.p = interpreter.start();
		this.out = p.getOutputStream();
		this.in = p.getInputStream();

		// init the blackbox
		this.r = new BufferedReader(new InputStreamReader(this.in));
		String line = "";
		long start = System.currentTimeMillis();
		long current = System.currentTimeMillis();
		long timeout = start + timeoutInterval;
		while (current < timeout) {
			Thread.sleep(100);

			current = System.currentTimeMillis();
			line = r.readLine();

			if (line.equals(initMessage))
				break;
		}

		Log.info("init message received");

		// Log.info(blackboxPath);
		// send blackbox-path
		Log.info(exec(blackboxPath));
		Log.info("blackbox fully initialized");
		Log.info("");
	}

	protected ArrayList<FeatureListEntry> readFeatureList(String path) throws IOException {
		ArrayList<FeatureListEntry> list = new ArrayList<FeatureListEntry>();

		// read and parse features line by line
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] splits = line.split(featureListDelimiter);
			list.add(new FeatureListEntry(splits[1], Double.parseDouble(splits[0])));
		}

		return list;
	}

	/** Executes a command and returns the response. **/
	public String exec(String command) {
		String str = null;

		try {
			out.write((command + "\n").getBytes());

			// try {
			out.flush();

			// } catch (Exception e) {
			//
			// }
			str = this.r.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	/** Uses the blackbox to predict the given data. **/
	public String predict(BatchData b) {
		String prediction = null;
		// System.out.println(b.getTimestamp());
		// System.out.println("values: " + b.getValues().size());
		// System.out.println("metrics: " + b.getMetrics().size());

		String featureVector = "";

		for (int i = 0; i < this.numberOfFeatures; i++) {
			FeatureListEntry e = this.features.get(i);
			// System.out.println(i + "\t" + e.getName() + " @ " +
			// e.getScore());
			// System.out.println("\t\tm: " + e.getMetric() + "\tv: " +
			// e.getValue());
			// System.out.println(i);
			double v = e.obtainValueFromBatch(b);

			if (Double.isNaN(v) || Double.isInfinite(v))
				v = 0.0;

			if (featureVector.equals(""))
				featureVector += v;
			else
				featureVector += featureVectorDelimiter + v;
		}

		// System.out.println("exec: " + featureVector);
		prediction = exec(featureVector);

		prediction = prediction.replaceAll("predicting: ", "");
		return prediction;
	}

	/** Predicts an entire run and returns a PredictionList. **/
	public PredictionList predictRun(String runDir, int lifeTime, LabelMode labelMode) throws IOException {
		Log.info("predicting run: '" + runDir + "'");
		BatchDataList list = BatchDataList.readTimestamps(runDir);
		PredictionList predictions = new PredictionList(list.size());

		Log.infoSep();
		Log.info("# batches\tpercent");

		// iterate over batches
		for (int i = 0; i < list.size(); i++) {
			BatchData b = list.get(i);
			long timestamp = b.getTimestamp();
			BatchData tempBatch = BatchData.readIntelligent(Dir.getBatchDataDir(runDir, timestamp), timestamp,
					BatchReadMode.readOnlySingleValues);

			if (tempBatch.getValues().get("nodes").getValue() == 0.0) {
				continue;
			}

			// predict batch
			String prediction = this.predict(tempBatch);

			// add prediction to list
			predictions.addPrediction(timestamp, prediction,
					"" + this.truthLabeler.getAttacksAtTimestamp(timestamp, lifeTime, labelMode));

			if (i % 1000 == 0)
				Log.info(i + "\t" + (Math.floor(10000.0 * i / list.size()) / 100.0));
		}
		Log.info((list.size() - 1) + "\t100.0");
		Log.infoSep();
		return predictions;
	}

	/** stops the blackbox **/
	public void stop() {
		try {
			exec(exitMessage);
			this.r.close();
			this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
