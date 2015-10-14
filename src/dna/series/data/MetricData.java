package dna.series.data;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.metrics.IMetric;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.series.lists.DistributionList;
import dna.series.lists.ListItem;
import dna.series.lists.NodeNodeValueListList;
import dna.series.lists.NodeValueListList;
import dna.series.lists.ValueList;
import dna.util.Config;
import dna.util.Log;

public class MetricData implements ListItem {

	public MetricData(String name) {
		this.name = name;
		this.values = new ValueList();
		this.distributions = new DistributionList();
		this.nodevalues = new NodeValueListList();
		this.nodenodevalues = new NodeNodeValueListList();
	}

	public MetricData(String name, IMetric.MetricType type) {
		this.name = name;
		this.type = type;
		this.values = new ValueList();
		this.distributions = new DistributionList();
		this.nodevalues = new NodeValueListList();
	}

	public MetricData(String name, IMetric.MetricType type, int sizeValues,
			int sizeDistributions) {
		this.name = name;
		this.type = type;
		this.values = new ValueList(sizeValues);
		this.distributions = new DistributionList(sizeDistributions);
		this.nodevalues = new NodeValueListList();
		this.nodenodevalues = new NodeNodeValueListList();
	}

	public MetricData(String name, IMetric.MetricType type, int sizeValues,
			int sizeDistributions, int sizeNodeValueList) {
		this.name = name;
		this.type = type;
		this.values = new ValueList(sizeValues);
		this.distributions = new DistributionList(sizeDistributions);
		this.nodevalues = new NodeValueListList(sizeNodeValueList);
		this.nodenodevalues = new NodeNodeValueListList();
	}

	public MetricData(String name, IMetric.MetricType type, int sizeValues,
			int sizeDistributions, int sizeNodeValueList,
			int sizeNodeNodeValueList) {
		this.name = name;
		this.type = type;
		this.values = new ValueList(sizeValues);
		this.distributions = new DistributionList(sizeDistributions);
		this.nodevalues = new NodeValueListList(sizeNodeValueList);
		this.nodenodevalues = new NodeNodeValueListList(sizeNodeNodeValueList);
	}

	public MetricData(String name, IMetric.MetricType type, Value[] values,
			Distr<?>[] distributions) {
		this(name, type, values.length, distributions.length);
		for (Value v : values) {
			this.values.add(v);
		}
		for (Distr<?> d : distributions) {
			this.distributions.add(d);
		}
		this.nodevalues = new NodeValueListList();
		this.nodenodevalues = new NodeNodeValueListList();
	}

	public MetricData(String name, IMetric.MetricType type, Value[] values,
			Distr<?>[] distributions, NodeValueList[] nodevalues) {
		this(name, type, values.length, distributions.length, nodevalues.length);
		for (Value v : values) {
			this.values.add(v);
		}
		for (Distr<?> d : distributions) {
			this.distributions.add(d);
		}
		for (NodeValueList n : nodevalues) {
			this.nodevalues.add(n);
		}
		this.nodenodevalues = new NodeNodeValueListList();
	}

	public MetricData(String name, IMetric.MetricType type, Value[] values,
			Distr<?>[] distributions, NodeValueList[] nodevalues,
			NodeNodeValueList[] nodenodevalues) {
		this(name, type, values.length, distributions.length, nodevalues.length);
		for (Value v : values) {
			this.values.add(v);
		}
		for (Distr<?> d : distributions) {
			this.distributions.add(d);
		}
		for (NodeValueList n : nodevalues) {
			this.nodevalues.add(n);
		}
		for (NodeNodeValueList nnvl : nodenodevalues) {
			this.nodenodevalues.add(nnvl);
		}
	}

	public MetricData(String name, IMetric.MetricType type, ValueList values,
			DistributionList distributions) {
		this.name = name;
		this.type = type;
		this.values = values;
		this.distributions = distributions;
		this.nodevalues = new NodeValueListList();
		this.nodenodevalues = new NodeNodeValueListList();
	}

	public MetricData(String name, IMetric.MetricType type, ValueList values,
			DistributionList distributions, NodeValueListList nodevalues) {
		this.name = name;
		this.type = type;
		this.values = values;
		this.distributions = distributions;
		this.nodevalues = nodevalues;
		this.nodenodevalues = new NodeNodeValueListList();
	}

	public MetricData(String name, IMetric.MetricType type, ValueList values,
			DistributionList distributions, NodeValueListList nodevalues,
			NodeNodeValueListList nodenodevalues) {
		this.name = name;
		this.type = type;
		this.values = values;
		this.distributions = distributions;
		this.nodevalues = nodevalues;
		this.nodenodevalues = nodenodevalues;
	}

	private NodeValueListList nodevalues;

	public NodeValueListList getNodeValues() {
		return this.nodevalues;
	}

	private NodeNodeValueListList nodenodevalues;

	public NodeNodeValueListList getNodeNodeValues() {
		return this.nodenodevalues;
	}

	String name;

	public String getName() {
		return this.name;
	}

	private IMetric.MetricType type;

	public IMetric.MetricType getType() {
		return this.type;
	}

	public void setType(IMetric.MetricType type) {
		this.type = type;
	}

	private ValueList values;

	public ValueList getValues() {
		return this.values;
	}

	private DistributionList distributions;

	public DistributionList getDistributions() {
		return this.distributions;
	}

	public void write(String dir) throws IOException {
		if (Config.getBoolean("GENERATION_WRITE_VALUES")) {
			if (this.values.size() > 0)
				this.values.write(dir, Files.getValuesFilename(Config
						.get("METRIC_DATA_VALUES")));
		}
		if (Config.getBoolean("GENERATION_WRITE_DISTRIBUTONS")) {
			this.distributions.write(dir);
		}
		if (Config.getBoolean("GENERATION_WRITE_NVL")) {
			this.nodevalues.write(dir);
		}
		if (Config.getBoolean("GENERATION_WRITE_NNVL")) {
			this.nodenodevalues.write(dir);
		}

	}

	/**
	 * Reads a MetricData object from filesystem. Convention: MetricData objects
	 * carry their MetricType as .MetricType behind their name.
	 * 
	 * @param dir
	 *            Directory the object will be read from
	 * @param name
	 *            Name of the returned MetricData object
	 * @param batchReadMode
	 *            Specifies what data should be read.
	 * @return Resulting MetricData object
	 * @throws IOException
	 *             Thrown by dna/io/reader which is created in order to read the
	 *             data.
	 */
	public static MetricData read(String dir, String name,
			BatchReadMode batchReadMode) throws IOException {
		boolean readSingleValues;
		boolean readDistributions;
		boolean readNodeValues;
		switch (batchReadMode) {
		case readAllValues:
			readSingleValues = true;
			readDistributions = true;
			readNodeValues = true;
			break;
		case readOnlySingleValues:
			readSingleValues = true;
			readDistributions = false;
			readNodeValues = false;
			break;
		case readOnlyDistAndNvl:
			readSingleValues = false;
			readDistributions = true;
			readNodeValues = true;
			break;
		case readNoValues:
			readSingleValues = false;
			readDistributions = false;
			readNodeValues = false;
			break;
		default:
			readSingleValues = true;
			readDistributions = true;
			readNodeValues = true;
			break;
		}
		String tempName = name;
		String[] temp = dir.split("\\" + Config.get("FILE_NAME_DELIMITER"));
		IMetric.MetricType tempType = IMetric.MetricType.unknown;
		try {
			if (temp[temp.length - 1].equals(IMetric.MetricType.exact.name()
					+ "/")) {
				tempType = IMetric.MetricType.exact;
				tempName = name.replace(Config.get("FILE_NAME_DELIMITER")
						+ IMetric.MetricType.exact.name(), "");
			}
			if (temp[temp.length - 1].equals(IMetric.MetricType.heuristic
					.name() + "/")) {
				tempType = IMetric.MetricType.heuristic;
				tempName = name.replace(Config.get("FILE_NAME_DELIMITER")
						+ IMetric.MetricType.heuristic.name(), "");
			}
			if (temp[temp.length - 1].equals(IMetric.MetricType.quality.name()
					+ "/")) {
				tempType = IMetric.MetricType.quality;
			}
		} catch (IndexOutOfBoundsException e) {
			Log.warn("Attempting to read metric " + name + " at " + dir
					+ " ! No MetricType detected!");
		}
		ValueList values = ValueList.read(dir,
				Files.getValuesFilename(Config.get("METRIC_DATA_VALUES")),
				readSingleValues);
		DistributionList distributions = DistributionList.read(dir,
				readDistributions);
		NodeValueListList nodevalues = NodeValueListList.read(dir,
				readNodeValues);
		NodeNodeValueListList nodenodevalues = NodeNodeValueListList.read(dir,
				readNodeValues);
		return new MetricData(tempName, tempType, values, distributions,
				nodevalues, nodenodevalues);
	}

	/**
	 * Tests if two MetricData objects are of the same type. Therefore it checks
	 * if they got the same distributions and metrics.
	 * 
	 * @returns true - when they are of the same type, else false
	 * 
	 * @author Rwilmes
	 * @date 24.06.2013
	 */
	public static boolean isSameType(MetricData m1, MetricData m2) {
		// if(m1.getName().equals(m2.getName()))
		// return false;

		ValueList list1 = m1.getValues();
		ValueList list2 = m2.getValues();

		DistributionList dlist1 = m1.getDistributions();
		DistributionList dlist2 = m2.getDistributions();

		if (list1.size() != list2.size()) {
			Log.warn("different amount of values on metric " + m1.getName()
					+ " and metric " + m2.getName());
			return false;
		}

		if (dlist1.size() != dlist2.size()) {
			Log.warn("different amount of distributions on metric "
					+ m1.getName() + " and metric " + m2.getName());
			return false;
		}

		for (String k : list1.getNames()) {
			if (!list1.get(k).getName().equals(list2.get(k).getName())) {
				Log.warn("different values on metric " + m1.getName()
						+ " and metric " + m2.getName());
				return false;
			}
		}

		for (String k : dlist1.getNames()) {
			if (!dlist1.get(k).getName().equals(dlist2.get(k).getName())) {
				Log.warn("different distributions on metric " + m1.getName()
						+ " and metric " + m2.getName());
				return false;
			}
		}

		return true;
	}

	/**
	 * Tests if two MetricData objects can be compared, which means one is
	 * 'exact' and the other one is 'heuristic'. In addition there has to be at
	 * least on similarity.
	 * 
	 * @returns true - when they are of the same type, else false
	 * 
	 * @author Rwilmes
	 * @date 03.08.2013
	 */
	public static boolean isComparable(MetricData m1, MetricData m2) {
		if (m1.getType().equals("exact") && !m2.getType().equals("heuristic")) {
			Log.warn("Metrics " + m1.getName() + " & " + m2.getName()
					+ " can't be compared. Type failure");
			return false;
		}

		if (m2.getType().equals("exact") && !m1.getType().equals("heuristic")) {
			Log.warn("Metrics " + m1.getName() + " & " + m2.getName()
					+ " can't be compared. Type failure");
			return false;
		}

		int similarities = 0;

		// check for same values
		for (String value : m1.getValues().getNames()) {
			if (m2.getValues().get(value) != null) {
				similarities++;
			}
		}
		// check for same distributions
		for (String distribution : m1.getDistributions().getNames()) {
			if (m2.getDistributions().get(distribution) != null) {
				similarities++;
			}
		}
		// check for same nodevaluelists
		for (String nodevalue : m1.getNodeValues().getNames()) {
			if (m2.getNodeValues().get(nodevalue) != null) {
				similarities++;
			}
		}
		// check for same nodenodevaluelists
		for (String nodenodevalue : m1.getNodeNodeValues().getNames()) {
			if (m2.getNodeNodeValues().get(nodenodevalue) != null) {
				similarities++;
			}
		}

		if (similarities > 0)
			return true;
		else
			return false;
	}

	/**
	 * Compares an 'exact' and a 'heuristic' MetricData object. Returns a new
	 * MetricData object containing the quality of the heuristic in every
	 * comparable value, distribution or nodevaluelist.
	 * 
	 * @param m1
	 *            First MetricData object for comparison
	 * 
	 * @param m2
	 *            Second MetricData object for comparison
	 * 
	 * @returns MetricData object containing the heuristics quality
	 * 
	 * @author Rwilmes
	 * @date 08.08.2013
	 */
	public static MetricData compare(MetricData m1, MetricData m2) {
		// check if comparable
		if (!isComparable(m1, m2)) {
			Log.warn("Failed attempt to compare metrics " + m1.getName()
					+ " & " + m2.getName()
					+ "! Returning empty MetricData object");
			return new MetricData(null);
		}

		// let m1 be the 'exact' metric
		if (m1.getType() != IMetric.MetricType.exact) {
			MetricData temp = m1;
			m1 = m2;
			m2 = temp;
		}

		// check similarities
		ValueList similarValues = new ValueList();
		for (String value : m1.getValues().getNames()) {
			if (m2.getValues().get(value) != null) {
				similarValues.add(m2.getValues().get(value));
			}
		}

		DistributionList similarDistributions = new DistributionList();
		for (String distribution : m1.getDistributions().getNames()) {
			if (m2.getDistributions().get(distribution) != null) {
				similarDistributions.add(m2.getDistributions()
						.get(distribution));
			}
		}

		NodeValueListList similarNodeValues = new NodeValueListList();
		for (String nodevalue : m1.getNodeValues().getNames()) {
			if (m2.getNodeValues().get(nodevalue) != null) {
				similarNodeValues.add(m2.getNodeValues().get(nodevalue));
			}
		}

		// compare values
		ValueList comparedValues = new ValueList();
		for (String value : similarValues.getNames()) {
			comparedValues.add(compareValues(m1.getValues().get(value), m2
					.getValues().get(value)));
		}

		// compare distributions
		DistributionList comparedDistributions = new DistributionList();
		for (String distribution : similarDistributions.getNames()) {
			compareDistributionsAndAddToList(comparedDistributions, m1
					.getDistributions().get(distribution), m2
					.getDistributions().get(distribution));
		}

		// compare nodevaluelists
		NodeValueListList comparedNodeValues = new NodeValueListList();
		for (String nodevalue : similarNodeValues.getNames()) {
			comparedNodeValues.add(compareNodeValueLists(m1.getNodeValues()
					.get(nodevalue), m2.getNodeValues().get(nodevalue)));
		}

		// TODO: compare nodenodevaluelists
		return new MetricData(m2.getName()
				+ Config.get("SUFFIX_METRIC_QUALITY"),
				IMetric.MetricType.quality, comparedValues,
				comparedDistributions, comparedNodeValues);
	}

	/** Compares the two values and returns a quality value. **/
	private static Value compareValues(Value v1, Value v2) {
		double d1 = v1.getValue();
		double d2 = v2.getValue();
		double quality = 0;
		double delta = Math.abs(d1 - d2);

		if (d1 == 0 || d2 == 0) {
			quality = delta;
		} else {
			quality = d2 / d1;
		}
		return new Value(v1.getName() + Config.get("SUFFIX_METRIC_QUALITY"),
				quality);
	}

	/**
	 * Compares the two distributions and adds an absolute and a relative
	 * quality distribution to the distribution-list.
	 **/
	private static void compareDistributionsAndAddToList(DistributionList list,
			Distr<?> d1, Distr<?> d2) {
		// compare
		if (d1.getDistrType().equals(d2.getDistrType()))
			Distr.compareDistributionsAndAddToList(list, d1, d2);
	}

	/** Compares two nodevaluelists and returns a quality nodevaluelists. **/
	private static NodeValueList compareNodeValueLists(NodeValueList n1,
			NodeValueList n2) {
		double[] values1 = n1.getValues();
		double[] values2 = n2.getValues();
		double[] qualities = new double[values1.length];

		for (int i = 0; i < values1.length; i++) {
			double v1 = values1[i];
			double v2 = values2[i];
			double quality = 0;
			double delta = Math.abs(v1 - v2);

			if (v1 == 0 || v2 == 0) {
				quality = delta;
			} else {
				if (v1 > v2)
					quality = v2 / v1;
				else
					quality = v1 / v2;
			}
			qualities[i] = quality;
		}
		return new NodeValueList(n1.getName()
				+ Config.get("SUFFIX_METRIC_QUALITY"), qualities);
	}

	/**
	 * Counts the similarities between two MetricData objects. Every Value,
	 * Distribution or NodeValueList of the same name counts as one similarity.
	 * 
	 * @param m1
	 *            First MetricData object
	 * @param m2
	 *            Second MetricData object
	 * @return Amount of Similarities.
	 */
	public static int countSimilarities(MetricData m1, MetricData m2) {
		int similarities = 0;

		// count similarities
		for (String value : m1.getValues().getNames()) {
			if (m2.getValues().get(value) != null) {
				similarities++;
			}
		}
		for (String distribution : m1.getDistributions().getNames()) {
			if (m2.getDistributions().get(distribution) != null) {
				similarities++;
			}
		}
		for (String nodevalue : m1.getNodeValues().getNames()) {
			if (m2.getNodeValues().get(nodevalue) != null) {
				similarities++;
			}
		}
		for (String nodenodevalue : m1.getNodeNodeValues().getNames()) {
			if (m2.getNodeNodeValues().get(nodenodevalue) != null) {
				similarities++;
			}
		}
		return similarities;
	}
}
