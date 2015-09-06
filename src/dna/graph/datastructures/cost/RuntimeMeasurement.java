package dna.graph.datastructures.cost;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GDS;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.IEdgeListDatastructureReadable;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.datastructures.count.Counting;
import dna.graph.datastructures.count.OperationCount.Operation;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.random.RandomGraph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.io.Reader;
import dna.io.Writer;
import dna.io.filter.LineCountFilter;
import dna.io.filter.SuffixFilenameFilter;
import dna.series.Aggregation;
import dna.series.aggdata.AggregatedValue;
import dna.util.Config;
import dna.util.Timer;

/**
 * 
 * measures the runtime of all operations under consideration of specified
 * datastructures. for each measurement, an empty list is created and a total of
 * steps * stepSize elements is added one after the other. every time stepSize
 * many elements have been added, each operation is executed stepSize many times
 * and the respective runtime is recorded.
 * 
 * these measurements are based on the assumption / idea that the runtime growth
 * during the addition of "only" stepSize many elements can be approximated well
 * by a linear function. therefore, the operations are not executed after each
 * element addition which would add overhead to the measurements. still, such a
 * measurement could be done with stepSize = 1.
 * 
 * for each measurement, the recorded runtimes are written to a file.
 * 
 * to aggregate runtimes of multiple runs, execute it with repetiation = 0.
 * 
 * @author benni
 *
 */
public class RuntimeMeasurement {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// args = new String[] { "dna.graph.datastructures.DArray",
		// "dna.graph.edges.DirectedEdge", "10", "100", "2",
		// "measurements/", "20", "20", "4", "measurements-init/", "0" };
		if (args.length != 12) {
			System.err.println("11	 arguments required");
			System.err
					.println("  0  - datastructure class (compl. class name)");
			System.err.println("       e.g., dna.graph.datastructures.DArray");
			System.err.println("  1  - element to use (compl. class name)");
			System.err.println("       e.g., dna.graph.nodes.UndirectedNode");
			System.err.println("  2  - stepSize");
			System.err.println("  3  - steps");
			System.err.println("  4  - repetitions");
			System.err.println("  5  - parallel executions (runtime averaged)");
			System.err.println("  6  - dir where to store the measurements");
			System.err.println("  7  - INIT-stepSize");
			System.err.println("  8  - INIT-steps");
			System.err.println("  9  - INIT-repetitions");
			System.err
					.println("  10  - INIT-dir where to store the measurements");
			System.err
					.println("  11 - min line count for aggregation (0 to disable check)");
			return;
		}
		Class<? extends IDataStructure> ds = (Class<? extends IDataStructure>) Class
				.forName(args[0]);
		Class<? extends IElement> dt = (Class<? extends IElement>) Class
				.forName(args[1]);
		int stepSize = Integer.parseInt(args[2]);
		int steps = Integer.parseInt(args[3]);
		int repetitions = Integer.parseInt(args[4]);
		int parallel = Integer.parseInt(args[5]);
		String dir = args[6];
		int stepSize_ = Integer.parseInt(args[7]);
		int steps_ = Integer.parseInt(args[8]);
		int repetitions_ = Integer.parseInt(args[9]);
		String dir_ = args[10];
		int minLineCount = Integer.parseInt(args[11]);

		for (int i = 0; i < repetitions_; i++) {
			RuntimeMeasurement m_ = new RuntimeMeasurement(ds, dt, stepSize_,
					steps_, 1, dir_);
			m_.execute();
		}

		for (int i = 0; i < repetitions; i++) {
			RuntimeMeasurement m = new RuntimeMeasurement(ds, dt, stepSize,
					steps, parallel, dir);
			m.execute();
			// m.aggregate(minLineCount);
			System.gc();
		}

		if (repetitions == 0) {
			System.out.println("aggregation only for minLineCount = "
					+ minLineCount);
			RuntimeMeasurement m = new RuntimeMeasurement(ds, dt, stepSize,
					steps, 1, dir);
			m.aggregate(minLineCount);
		}
	}

	public void aggregate(int minLineCount) throws NumberFormatException,
			IOException {
		for (Operation o : Operation.values()) {
			aggregate(dir, ds, dt, o, stepSize, minLineCount);
		}
	}

	public static GraphDataStructure getGDS(Class<? extends IElement> dt) {
		if (DirectedEdge.class.isAssignableFrom(dt)) {
			return GDS.directed();
		} else if (UndirectedEdge.class.isAssignableFrom(dt)) {
			return GDS.undirected();
		} else if (DirectedNode.class.isAssignableFrom(dt)) {
			return GDS.directed();
		} else if (UndirectedNode.class.isAssignableFrom(dt)) {
			return GDS.undirected();
		} else {
			System.err.println("cannot get GDS for dt = " + dt);
			return null;
		}
	}

	public static final String suffix = ".dat";
	public static final String delimiter = "\t";
	public static final String aggFilename = "agg.aggr";

	public Class<? extends IDataStructure> ds;
	public Class<? extends IElement> dt;

	public int stepSize;
	public int steps;
	public int parallel;

	public String dir;

	public RuntimeMeasurement(Class<? extends IDataStructure> dataStructure,
			Class<? extends IElement> dt, int stepSize, int steps,
			int parallel, String mainDataDir) {
		this.ds = dataStructure;
		this.dt = dt;
		this.stepSize = stepSize;
		this.steps = steps;
		this.parallel = parallel;
		this.dir = mainDataDir;
	}

	public void execute() throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException,
			SecurityException, IOException {
		if (Edge.class.isAssignableFrom(this.dt)) {
			this.executeEdge();
		} else if (Node.class.isAssignableFrom(this.dt)) {
			this.executeNode();
		} else {
			System.err.println("cannot perform measurements for " + this.dt);
		}
	}

	public static String getDataDir(String mainDataDir,
			Class<? extends IDataStructure> ds, Class<? extends IElement> dt,
			Operation o) {
		return mainDataDir + ds.getSimpleName() + "-" + dt.getSimpleName()
				+ "/" + o.toString() + "/";
	}

	public static String getNextFilename(String mainDataDir,
			Class<? extends IDataStructure> ds, Class<? extends IElement> dt,
			Operation o) {
		int index = 0;
		String dir = getDataDir(mainDataDir, ds, dt, o);
		while ((new File(dir + index + suffix)).exists()) {
			index++;
		}
		return index + suffix;
	}

	public static Writer nextWriter(String mainDataDir,
			Class<? extends IDataStructure> ds, Class<? extends IElement> dt,
			Operation o) throws IOException {
		return new Writer(getDataDir(mainDataDir, ds, dt, o), getNextFilename(
				mainDataDir, ds, dt, o));
	}

	/**
	 * 
	 * writes the given list of results for the specified operation into a file.
	 * using the invert flag, the lines can be written in reverse order.
	 * 
	 * @param o
	 *            operation
	 * @param lines
	 *            ordered list of lines
	 * @param invert
	 *            if set true, last line is written first
	 * @throws IOException
	 */
	public void write(Operation o, LinkedList<String> lines, boolean invert)
			throws IOException {
		Writer w = nextWriter(dir, ds, dt, o);

		while (!lines.isEmpty()) {
			if (invert) {
				w.writeln(lines.pollLast());
			} else {
				w.writeln(lines.pollFirst());
			}
		}
		w.close();
	}

	public static ArrayList<AggregatedValue> read(String mainDataDir,
			Class<? extends IDataStructure> ds, Class<? extends IElement> dt,
			Operation o) throws NumberFormatException, IOException {
		ArrayList<AggregatedValue> readValues = new ArrayList<AggregatedValue>();
		Reader r = new Reader(getDataDir(mainDataDir, ds, dt, o), aggFilename);
		String line = null;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("AGGREGATED_DATA_DELIMITER"));
			double[] values = new double[temp.length - 1];
			for (int i = 1; i < temp.length; i++) {
				values[i - 1] = Double.parseDouble(temp[i]);
			}
			readValues.add(new AggregatedValue(temp[0], values));
		}
		r.close();
		return readValues;
	}

	protected Node[] getNodesToAdd() {
		GraphGenerator gg = new RandomGraph(getGDS(this.dt), (this.steps + 1)
				* this.stepSize, 0);
		Counting.init(gg.getGds());
		IGraph g = gg.generate();
		Node[] nodes = new Node[g.getNodeCount()];
		int index = 0;
		for (IElement n_ : g.getNodes()) {
			nodes[index++] = (Node) n_;
		}
		return nodes;
	}

	protected Edge[] getEdgesToAdd() {
		GraphGenerator gg = new RandomGraph(getGDS(this.dt), (this.steps + 1)
				* Math.max(this.stepSize / 10, 1), (this.steps + 1)
				* this.stepSize);
		Counting.init(gg.getGds());
		IGraph g = gg.generate();
		Edge[] edges = new Edge[g.getEdgeCount()];
		int index = 0;
		for (IElement e_ : g.getEdges()) {
			edges[index++] = (Edge) e_;
		}
		return edges;
	}

	protected void executeNode() throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException,
			SecurityException, IOException {

		Timer t = new Timer();
		System.out.print(this.ds.getSimpleName() + " - "
				+ this.dt.getSimpleName() + " (" + this.steps + " x "
				+ this.stepSize + "): ");

		LinkedList<String> lines_INIT, lines_ADD_SUCCESS, lines_ADD_FAILURE, lines_RANDOM_ELEMENT, lines_SIZE, lines_ITERATE, lines_CONTAINS_SUCCESS, lines_CONTAINS_FAILURE, lines_GET_SUCCESS, lines_GET_FAILURE, lines_REMOVE_SUCCESS, lines_REMOVE_FAILURE;
		lines_INIT = new LinkedList<String>();
		lines_ADD_SUCCESS = new LinkedList<String>();
		lines_ADD_FAILURE = new LinkedList<String>();
		lines_RANDOM_ELEMENT = new LinkedList<String>();
		lines_SIZE = new LinkedList<String>();
		lines_ITERATE = new LinkedList<String>();
		lines_CONTAINS_SUCCESS = new LinkedList<String>();
		lines_CONTAINS_FAILURE = new LinkedList<String>();
		lines_GET_SUCCESS = new LinkedList<String>();
		lines_GET_FAILURE = new LinkedList<String>();
		lines_REMOVE_SUCCESS = new LinkedList<String>();
		lines_REMOVE_FAILURE = new LinkedList<String>();

		/**
		 * INIT
		 */
		for (int i = 0; i < steps; i++) {
			Timer t_INIT = new Timer();
			for (int l = 0; l < this.parallel; l++) {
				for (int j = 0; j < stepSize; j++) {
					IDataStructure list = ds.getConstructor(ListType.class,
							ds.getClass()).newInstance(ListType.GlobalNodeList,
							dt);
					list.init(dt, i * stepSize + j, true);
				}
			}
			t_INIT.end();
			lines_INIT.add(((i + 1) * stepSize) + delimiter
					+ t_INIT.getDutation() / parallel);
		}

		Random rand = new Random();
		Node[] nodesToAdd = this.getNodesToAdd();
		IDataStructure[] lists = new IDataStructure[parallel];
		for (int l = 0; l < lists.length; l++) {
			lists[l] = ds.getConstructor(ListType.class, ds.getClass())
					.newInstance(ListType.GlobalNodeList, dt);
		}
		// IDataStructure list = ds.getConstructor(ListType.class,
		// ds.getClass())
		// .newInstance(ListType.GlobalNodeList, dt);

		for (int i = 0; i < steps; i++) {
			/**
			 * ADD_SUCCESS
			 */
			Timer t_ADD_SUCCESS = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].add(nodesToAdd[i * stepSize + j]);
				}
			}
			t_ADD_SUCCESS.end();

			/**
			 * ADD_FAILURE
			 */
			Timer t_ADD_FAILURE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].add(nodesToAdd[i * stepSize + j]);
				}
			}
			t_ADD_FAILURE.end();

			Node[] existingNodes = new Node[stepSize];
			for (int j = 0; j < stepSize; j++) {
				existingNodes[j] = nodesToAdd[rand.nextInt(lists[0].size())];
			}
			Node[] nonExistingNodes = new Node[stepSize];
			for (int j = 0; j < stepSize; j++) {
				nonExistingNodes[j] = nodesToAdd[(i + 1) * stepSize + j];
			}

			/**
			 * RANDOM_ELEMENT
			 */
			Timer t_RANDOM_ELEMENT = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					((INodeListDatastructureReadable) lists[l]).getRandom();
				}
			}
			t_RANDOM_ELEMENT.end();

			/**
			 * SIZE
			 */
			Timer t_SIZE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].size();
				}
			}
			t_SIZE.end();

			/**
			 * ITERATE
			 */
			Timer t_ITERATE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					for (IElement n_ : lists[l]) {
						@SuppressWarnings("unused")
						Node n = (Node) n_;
					}
				}
			}
			t_ITERATE.end();

			/**
			 * CONTAINS_SUCCESS
			 */
			Timer t_CONTAINS_SUCCESS = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].contains(existingNodes[j]);
				}
			}
			t_CONTAINS_SUCCESS.end();

			/**
			 * CONTAINS_FAILURE
			 */
			Timer t_CONTAINS_FAILURE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].contains(nonExistingNodes[j]);
				}
			}
			t_CONTAINS_FAILURE.end();

			/**
			 * GET_SUCCESS
			 */
			Timer t_GET_SUCCESS = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					((INodeListDatastructureReadable) lists[l])
							.get(existingNodes[j].getIndex());
				}
			}
			t_GET_SUCCESS.end();

			/**
			 * GET_FAILURE
			 */
			Timer t_GET_FAILURE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					((INodeListDatastructureReadable) lists[l])
							.get(nonExistingNodes[j].getIndex());
				}
			}
			t_GET_FAILURE.end();

			lines_ADD_SUCCESS.add(lists[0].size() + delimiter
					+ t_ADD_SUCCESS.getDutation() / parallel);
			lines_ADD_FAILURE.add(lists[0].size() + delimiter
					+ t_ADD_FAILURE.getDutation() / parallel);
			lines_RANDOM_ELEMENT.add(lists[0].size() + delimiter
					+ t_RANDOM_ELEMENT.getDutation() / parallel);
			lines_SIZE.add(lists[0].size() + delimiter + t_SIZE.getDutation()
					/ parallel);
			lines_ITERATE.add(lists[0].size() + delimiter
					+ t_ITERATE.getDutation() / parallel);
			lines_CONTAINS_SUCCESS.add(lists[0].size() + delimiter
					+ t_CONTAINS_SUCCESS.getDutation() / parallel);
			lines_CONTAINS_FAILURE.add(lists[0].size() + delimiter
					+ t_CONTAINS_FAILURE.getDutation() / parallel);
			lines_GET_SUCCESS.add(lists[0].size() + delimiter
					+ t_GET_SUCCESS.getDutation() / parallel);
			lines_GET_FAILURE.add(lists[0].size() + delimiter
					+ t_GET_FAILURE.getDutation() / parallel);
		}

		ArrayList<Node> nodes = new ArrayList<Node>(lists[0].size());
		for (IElement n_ : lists[0]) {
			nodes.add((Node) n_);
		}
		Collections.shuffle(nodes);
		Node[] nodesToRemove = new Node[lists[0].size()];
		for (int i = 0; i < nodes.size(); i++) {
			nodesToRemove[i] = nodes.get(i);
		}

		Node[] nonExistingNodes = new Node[stepSize];
		for (int j = 0; j < stepSize; j++) {
			nonExistingNodes[j] = nodesToAdd[nodesToAdd.length - 1 - j];
		}

		for (int i = 0; i < steps; i++) {
			/**
			 * REMOVE_FAILURE
			 */
			Timer t_REMOVE_FAILURE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].remove(nonExistingNodes[j]);
				}
			}
			t_REMOVE_FAILURE.end();

			/**
			 * REMOVE_SUCCESS
			 */
			Timer t_REMOVE_SUCCESS = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].remove(nodesToRemove[i * stepSize + j]);
				}
			}
			t_REMOVE_SUCCESS.end();

			for (int j = 0; j < stepSize; j++) {
				nonExistingNodes[j] = nodesToRemove[i * stepSize + j];
			}

			lines_REMOVE_SUCCESS.add((lists[0].size() + stepSize) + delimiter
					+ t_REMOVE_SUCCESS.getDutation() / parallel);
			lines_REMOVE_FAILURE.add((lists[0].size() + stepSize) + delimiter
					+ t_REMOVE_FAILURE.getDutation() / parallel);
		}

		this.write(Operation.INIT, lines_INIT, false);
		this.write(Operation.ADD_SUCCESS, lines_ADD_SUCCESS, false);
		this.write(Operation.ADD_FAILURE, lines_ADD_FAILURE, false);
		this.write(Operation.RANDOM_ELEMENT, lines_RANDOM_ELEMENT, false);
		this.write(Operation.SIZE, lines_SIZE, false);
		this.write(Operation.ITERATE, lines_ITERATE, false);
		this.write(Operation.CONTAINS_SUCCESS, lines_CONTAINS_SUCCESS, false);
		this.write(Operation.CONTAINS_FAILURE, lines_CONTAINS_FAILURE, false);
		this.write(Operation.GET_SUCCESS, lines_GET_SUCCESS, false);
		this.write(Operation.GET_FAILURE, lines_GET_FAILURE, false);
		this.write(Operation.REMOVE_FAILURE, lines_REMOVE_FAILURE, true);
		this.write(Operation.REMOVE_SUCCESS, lines_REMOVE_SUCCESS, true);

		t.end();
		System.out.println(t.toString());
	}

	protected void executeEdge() throws IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		Timer t = new Timer();
		System.out.print(this.ds.getSimpleName() + " - "
				+ this.dt.getSimpleName() + " (" + this.steps + " x "
				+ this.stepSize + "): ");

		LinkedList<String> lines_INIT, lines_ADD_SUCCESS, lines_ADD_FAILURE, lines_RANDOM_ELEMENT, lines_SIZE, lines_ITERATE, lines_CONTAINS_SUCCESS, lines_CONTAINS_FAILURE, lines_GET_SUCCESS, lines_GET_FAILURE, lines_REMOVE_SUCCESS, lines_REMOVE_FAILURE;
		lines_INIT = new LinkedList<String>();
		lines_ADD_SUCCESS = new LinkedList<String>();
		lines_ADD_FAILURE = new LinkedList<String>();
		lines_RANDOM_ELEMENT = new LinkedList<String>();
		lines_SIZE = new LinkedList<String>();
		lines_ITERATE = new LinkedList<String>();
		lines_CONTAINS_SUCCESS = new LinkedList<String>();
		lines_CONTAINS_FAILURE = new LinkedList<String>();
		lines_GET_SUCCESS = new LinkedList<String>();
		lines_GET_FAILURE = new LinkedList<String>();
		lines_REMOVE_SUCCESS = new LinkedList<String>();
		lines_REMOVE_FAILURE = new LinkedList<String>();

		/**
		 * INIT
		 */
		for (int i = 0; i < steps; i++) {
			Timer t_INIT = new Timer();
			for (int l = 0; l < this.parallel; l++) {
				for (int j = 0; j < stepSize; j++) {
					IDataStructure list = ds.getConstructor(ListType.class,
							ds.getClass()).newInstance(ListType.GlobalEdgeList,
							dt);
					list.init(dt, i * stepSize + j, true);
				}
			}
			t_INIT.end();
			lines_INIT.add(((i + 1) * stepSize) + delimiter
					+ t_INIT.getDutation() / parallel);
		}

		Random rand = new Random();
		Edge[] edgesToAdd = this.getEdgesToAdd();
		IDataStructure[] lists = new IDataStructure[this.parallel];
		for (int l = 0; l < lists.length; l++) {
			lists[l] = ds.getConstructor(ListType.class, ds.getClass())
					.newInstance(ListType.GlobalEdgeList, dt);
		}
		// IDataStructure list = ds.getConstructor(ListType.class,
		// ds.getClass())
		// .newInstance(ListType.GlobalEdgeList, dt);

		for (int i = 0; i < steps; i++) {
			/**
			 * ADD_SUCCESS
			 */
			Timer t_ADD_SUCCESS = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].add(edgesToAdd[i * stepSize + j]);
				}
			}
			t_ADD_SUCCESS.end();

			/**
			 * ADD_FAILURE
			 */
			Timer t_ADD_FAILURE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].add(edgesToAdd[i * stepSize + j]);
				}
			}
			t_ADD_FAILURE.end();

			Edge[] existingEdges = new Edge[stepSize];
			for (int j = 0; j < stepSize; j++) {
				existingEdges[j] = edgesToAdd[rand.nextInt(lists[0].size())];
			}

			Edge[] nonExistingEdges = new Edge[stepSize];
			for (int j = 0; j < stepSize; j++) {
				nonExistingEdges[j] = edgesToAdd[(i + 1) * stepSize + j];
			}

			/**
			 * RANDOM_ELEMENT
			 */
			Timer t_RANDOM_ELEMENT = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					((INodeListDatastructureReadable) lists[l]).getRandom();
				}
			}
			t_RANDOM_ELEMENT.end();

			/**
			 * SIZE
			 */
			Timer t_SIZE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].size();
				}
			}
			t_SIZE.end();

			/**
			 * ITERATE
			 */
			Timer t_ITERATE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					for (IElement e_ : lists[l]) {
						@SuppressWarnings("unused")
						Edge e = (Edge) e_;
					}
				}
			}
			t_ITERATE.end();

			/**
			 * CONTAINS_SUCCESS
			 */
			Timer t_CONTAINS_SUCCESS = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].contains(existingEdges[j]);
				}
			}
			t_CONTAINS_SUCCESS.end();

			/**
			 * CONTAINS_FAILURE
			 */
			Timer t_CONTAINS_FAILURE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].contains(nonExistingEdges[j]);
				}
			}
			t_CONTAINS_FAILURE.end();

			/**
			 * GET_SUCCESS
			 */
			Timer t_GET_SUCCESS = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					((IEdgeListDatastructureReadable) lists[l])
							.get(existingEdges[j]);
				}
			}
			t_GET_SUCCESS.end();

			/**
			 * GET_FAILURE
			 */
			Timer t_GET_FAILURE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					((IEdgeListDatastructureReadable) lists[l])
							.get(nonExistingEdges[j]);
				}
			}
			t_GET_FAILURE.end();

			lines_ADD_SUCCESS.add(lists[0].size() + delimiter
					+ t_ADD_SUCCESS.getDutation() / parallel);
			lines_ADD_FAILURE.add(lists[0].size() + delimiter
					+ t_ADD_FAILURE.getDutation() / parallel);
			lines_RANDOM_ELEMENT.add(lists[0].size() + delimiter
					+ t_RANDOM_ELEMENT.getDutation() / parallel);
			lines_SIZE.add(lists[0].size() + delimiter + t_SIZE.getDutation()
					/ parallel);
			lines_ITERATE.add(lists[0].size() + delimiter
					+ t_ITERATE.getDutation() / parallel);
			lines_CONTAINS_SUCCESS.add(lists[0].size() + delimiter
					+ t_CONTAINS_SUCCESS.getDutation() / parallel);
			lines_CONTAINS_FAILURE.add(lists[0].size() + delimiter
					+ t_CONTAINS_FAILURE.getDutation() / parallel);
			lines_GET_SUCCESS.add(lists[0].size() + delimiter
					+ t_GET_SUCCESS.getDutation() / parallel);
			lines_GET_FAILURE.add(lists[0].size() + delimiter
					+ t_GET_FAILURE.getDutation() / parallel);
		}

		ArrayList<Edge> edges = new ArrayList<Edge>(lists[0].size());
		for (IElement e_ : lists[0]) {
			edges.add((Edge) e_);
		}
		Collections.shuffle(edges);
		Edge[] edgesToRemove = new Edge[lists[0].size()];
		for (int i = 0; i < edges.size(); i++) {
			edgesToRemove[i] = edges.get(i);
		}

		Edge[] nonExistingEdges = new Edge[stepSize];
		for (int j = 0; j < stepSize; j++) {
			nonExistingEdges[j] = edgesToAdd[edgesToAdd.length - 1 - j];
		}

		for (int i = 0; i < steps; i++) {
			/**
			 * REMOVE_FAILURE
			 */
			Timer t_REMOVE_FAILURE = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].remove(nonExistingEdges[j]);
				}
			}
			t_REMOVE_FAILURE.end();

			/**
			 * REMOVE_SUCCESS
			 */
			Timer t_REMOVE_SUCCESS = new Timer();
			for (int j = 0; j < stepSize; j++) {
				for (int l = 0; l < lists.length; l++) {
					lists[l].remove(edges.get(i * stepSize + j));
				}
			}
			t_REMOVE_SUCCESS.end();

			for (int j = 0; j < stepSize; j++) {
				nonExistingEdges[j] = edgesToRemove[i * stepSize + j];
			}

			lines_REMOVE_SUCCESS.add((lists[0].size() + stepSize) + delimiter
					+ t_REMOVE_SUCCESS.getDutation() / parallel);
			lines_REMOVE_FAILURE.add((lists[0].size() + stepSize) + delimiter
					+ t_REMOVE_FAILURE.getDutation() / parallel);
		}

		this.write(Operation.INIT, lines_INIT, false);
		this.write(Operation.ADD_SUCCESS, lines_ADD_SUCCESS, false);
		this.write(Operation.ADD_FAILURE, lines_ADD_FAILURE, false);
		this.write(Operation.RANDOM_ELEMENT, lines_RANDOM_ELEMENT, false);
		this.write(Operation.SIZE, lines_SIZE, false);
		this.write(Operation.ITERATE, lines_ITERATE, false);
		this.write(Operation.CONTAINS_SUCCESS, lines_CONTAINS_SUCCESS, false);
		this.write(Operation.CONTAINS_FAILURE, lines_CONTAINS_FAILURE, false);
		this.write(Operation.GET_SUCCESS, lines_GET_SUCCESS, false);
		this.write(Operation.GET_FAILURE, lines_GET_FAILURE, false);
		this.write(Operation.REMOVE_FAILURE, lines_REMOVE_FAILURE, true);
		this.write(Operation.REMOVE_SUCCESS, lines_REMOVE_SUCCESS, true);

		t.end();
		System.out.println(t.toString());
	}

	public static void aggregate(String mainDataDir,
			Class<? extends IDataStructure> ds, Class<? extends IElement> dt,
			Operation o, int stepSize, int minLineCount)
			throws NumberFormatException, IOException {
		String dir = getDataDir(mainDataDir, ds, dt, o);

		// assume missing values as zero
		boolean assumeZero = false;

		// get filenames
		String[] filenames = null;
		if (minLineCount == 0) {
			filenames = (new File(dir)).list(new SuffixFilenameFilter(".dat"));
		} else {
			filenames = (new File(dir)).list(new LineCountFilter(minLineCount,
					Integer.MAX_VALUE, null, ".dat"));
		}

		if (filenames.length == 0) {
			System.out.println("cannot aggregate 0 files, exiting...");
			return;
		}

		System.out.println("aggregating " + o + " (" + filenames.length
				+ " files)");

		// runtimes[i] contains runtimes of file[i]
		long[][] runtimes = new long[filenames.length][];

		// index of file with most runtimes
		int maxFile = 0;

		// calculate stepsize from read files
		long calcStepSize = 0;
		long step1 = 0;
		boolean flag = false;
		boolean flag2 = false;

		/*
		 * READ RUNTIME-FILES
		 */
		for (int i = 0; i < filenames.length; i++) {
			ArrayList<Long> runtimeList = new ArrayList<Long>();
			Reader r = new Reader(dir, filenames[i]);
			String line = null;

			// read runtimes
			while ((line = r.readString()) != null) {
				if (line.length() == 0) {
					continue;
				}
				if (!line.contains("\t")) {
					continue;
				}

				if (line.split("\t").length < 2) {
					System.out.println("problem for " + dir + filenames[i]
							+ " '" + line + "'");
					continue;
				}

				try {
					// parse runtime
					runtimeList.add(Long.parseLong(line.split("\t")[1]));

					// parse steps
					if (flag2) {
						calcStepSize = Long.parseLong(line.split("\t")[0])
								- step1;
						flag2 = false;
					}
					if (!flag) {
						step1 = Long.parseLong(line.split("\t")[0]);
						calcStepSize = step1;
						flag = true;
						flag2 = true;
					}
				} catch (Exception e) {
					e.printStackTrace(System.out);
					System.out.println("exiting aggregtion!");
				}
			}

			// convert list to array
			runtimes[i] = new long[runtimeList.size()];
			for (int j = 0; j < runtimeList.size(); j++) {
				runtimes[i][j] = runtimeList.get(j);
			}

			// keep track of which file has the most runtimes
			if (runtimes[i].length > runtimes[maxFile].length)
				maxFile = i;

			r.close();
		}

		/*
		 * AGGREGATION
		 */
		ArrayList<AggregatedValue> aggValues = new ArrayList<AggregatedValue>();

		// iterate over data -> aggregate
		for (int step = 0; step < runtimes[maxFile].length; step++) {
			// only take multiples of stepsize
			if (((step * calcStepSize) % stepSize) == 0) {
				ArrayList<Long> runtimeList = new ArrayList<Long>();

				// gather runtimes
				for (int j = 0; j < runtimes.length; j++) {
					// if step in bounds, add
					if (runtimes[j].length > step) {
						runtimeList.add(runtimes[j][step]);
					} else {
						if (assumeZero)
							runtimeList.add(0L);
					}
				}

				// convert list to array
				long[] runtimeArray = new long[runtimeList.size()];
				for (int j = 0; j < runtimeList.size(); j++) {
					runtimeArray[j] = runtimeList.get(j);
				}

				// aggregate
				aggValues.add(new AggregatedValue(""
						+ (step1 + step * calcStepSize), Aggregation
						.aggregate(runtimeArray)));
			}
		}

		/*
		 * WRITE
		 */
		new File(dir + aggFilename).delete();
		Writer w = new Writer(dir, aggFilename, true);
		// for each value write line
		for (AggregatedValue a : aggValues) {
			// write line
			w.write(a.getName() + "\t" + a.getAvg() + "\t" + a.getMin() + "\t"
					+ a.getMax() + "\t" + a.getMedian() + "\t"
					+ a.getVariance() + "\t" + a.getVarianceLow() + "\t"
					+ a.getVarianceUp() + "\t" + a.getConfidenceLow() + "\t"
					+ a.getConfidenceUp() + "\n");
		}
		w.close();
	}
}
