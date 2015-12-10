package dna.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dna.graph.datastructures.GDS;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.DNAGraphFactory.DNAGraphType;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.random.RandomGraph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight.WeightSelection;
import dna.metrics.Metric;
import dna.metrics.degree.DegreeDistributionR;
import dna.metrics.motifs.DirectedMotifsR;
import dna.metrics.paths.UnweightedAllPairsShortestPathsR;
import dna.metrics.similarityMeasures.jaccard.JaccardR;
import dna.metrics.workload.Workload;
import dna.metrics.workload.Operation;
import dna.metrics.workload.WorkloadMetric;
import dna.metrics.workload.operations.MetricComputation;
import dna.series.Series;
import dna.series.data.SeriesData;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.util.EmptyBatch;
import dna.util.Config;
import dna.util.IOUtils;

@RunWith(Parameterized.class)
public class GraphDatabaseWorkloadTester {

	private String delimiter = "//";
	private TestGDS testGds;

	private enum TestGDS {
		DIRECTEDGDB(), DIRECTEDVGDB(), DIRECTEDEGDB(), DIRECTEDVEGDB(), UNDIRECTEDGDB(), UNDIRECTEDVGDB(), UNDIRECTEDEGDB(), UNDIRECTEDVEGDB();

		public GraphDataStructure getGDS(DNAGraphType graphtype) {
			switch (this) {
			case DIRECTEDEGDB:
				if (graphtype == DNAGraphType.DNA)
					return GDS.gds(DirectedNode.class, DirectedEdge.class, null, null, IntWeight.class,
							WeightSelection.RandPos100);
				return GDS.directedEGDB(IntWeight.class, WeightSelection.RandPos100);
			case DIRECTEDGDB:
				if (graphtype == DNAGraphType.DNA)
					return GDS.gds(DirectedNode.class, DirectedEdge.class);
				return GDS.directedGDB();
			case DIRECTEDVEGDB:
				if (graphtype == DNAGraphType.DNA)
					return GDS.gds(DirectedNode.class, DirectedEdge.class, DoubleWeight.class,
							WeightSelection.RandTrim1, IntWeight.class, WeightSelection.RandPos100);
				return GDS.directedVEGDB(DoubleWeight.class, WeightSelection.RandTrim1, IntWeight.class,
						WeightSelection.RandPos100);
			case DIRECTEDVGDB:
				if (graphtype == DNAGraphType.DNA)
					return GDS.gds(DirectedNode.class, DirectedEdge.class, DoubleWeight.class,
							WeightSelection.RandTrim1, null, null);
				return GDS.directedVGDB(DoubleWeight.class, WeightSelection.RandTrim1);
			case UNDIRECTEDEGDB:
				if (graphtype == DNAGraphType.DNA)
					return GDS.gds(UndirectedNode.class, UndirectedEdge.class, null, null, IntWeight.class,
							WeightSelection.RandPos100);
				return GDS.undirectedEGDB(IntWeight.class, WeightSelection.RandPos100);
			case UNDIRECTEDGDB:
				if (graphtype == DNAGraphType.DNA)
					return GDS.gds(UndirectedNode.class, UndirectedEdge.class);
				return GDS.undirectedGDB();
			case UNDIRECTEDVEGDB:
				if (graphtype == DNAGraphType.DNA)
					return GDS.gds(UndirectedNode.class, UndirectedEdge.class, DoubleWeight.class,
							WeightSelection.RandTrim1, IntWeight.class, WeightSelection.RandPos100);
				return GDS.undirectedVEGDB(DoubleWeight.class, WeightSelection.RandTrim1, IntWeight.class,
						WeightSelection.RandPos100);
			case UNDIRECTEDVGDB:
				if (graphtype == DNAGraphType.DNA)
					return GDS.gds(UndirectedNode.class, UndirectedEdge.class, DoubleWeight.class,
							WeightSelection.RandTrim1, null, null);
				return GDS.undirectedVGDB(DoubleWeight.class, WeightSelection.RandTrim1);
			default:
				return null;
			}
		}
	}

	@Parameters(name = "{index}: Test graph database {0} with {1}")
	public static ArrayList<Object> data() {
		ArrayList<Object> result = new ArrayList<>();
		for (TestGDS gds : TestGDS.values()) {
			if (gds.getGDS(DNAGraphType.DNA).createsDirected()) {
				result.add(new Object[] { gds });
			}
		}

		return result;
	}

	public GraphDatabaseWorkloadTester(TestGDS testgds) {
		this.testGds = testgds;
		delimiter = IOUtils.getPathDelimiterForOS() + IOUtils.getPathDelimiterForOS();
	}

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void Workload() throws Exception {
		// Config.zipRuns();
		int seriesRuns = 1;
		int count = 0;
		SeriesData[] sd = new SeriesData[seriesRuns * (DNAGraphType.values().length - 1)];
		for (DNAGraphType graphType : DNAGraphType.values()) {
			if (graphType == DNAGraphType.CONFIG)
				continue;
			Config.overwrite("GF_GRAPHTYPE", graphType.toString());
			Config.overwrite("GF_GDB_OPERATIONS_PER_COMMIT", "1000");

			String data = "data" + delimiter + graphType.toString() + delimiter;
			int runs = 1;

			int roundsPerWorkload = 1;
			int overallRounds = 1;
			WorkloadMetric w = getWorkloads(roundsPerWorkload);
			int batches = w.workloads.length * roundsPerWorkload * overallRounds - 1;

			for (int i = (count * seriesRuns); i < (seriesRuns * (count + 1)); i++) {
				GraphGenerator gg = new RandomGraph(testGds.getGDS(graphType), 1000, 1000);
				BatchGenerator bg = new EmptyBatch();
				Metric[] metrics = new Metric[] { w };

				Series s = new Series(gg, bg, metrics,
						data + (i % seriesRuns) + delimiter + this.testGds.toString() + delimiter,
						graphType.toString());
				sd[i] = s.generate(runs, batches);
			}
			count++;
		}

		// comparison

		count = 1;
		for (int i = 0; i < sd.length; i++) {
			for (int j = count; j < sd.length; j++) {
				if (i == j)
					continue;
				assertTrue(SeriesData.equal(sd[i], sd[j]));
			}
			count++;
		}
	}

	public static WorkloadMetric getWorkloads(int roundsPerWorkload) {
		int k = 1000;

		LinkedList<Operation> op = new LinkedList<Operation>();
		// metrics
		 op.add(new MetricComputation(2 * k, new DegreeDistributionR()));
		 op.add(new MetricComputation(1, new DirectedMotifsR()));
		 op.add(new MetricComputation(4, new UnweightedAllPairsShortestPathsR()));
		 op.add(new MetricComputation(1, new JaccardR()));

		return new WorkloadMetric(toArray(op, false, roundsPerWorkload));
	}

	public static Workload[] toArray(LinkedList<Operation> op, boolean addCumulative, int roundsPerWorkload) {
		Operation[] op_ = new Operation[op.size()];
		Workload[] w = new Workload[addCumulative ? op.size() + 1 : op.size()];
		int index = 0;
		for (Operation o : op) {
			op_[index] = o;
			w[index++] = new Workload(roundsPerWorkload, o);
		}
		if (addCumulative)
			w[op.size()] = new Workload(roundsPerWorkload, op_);
		return w;
	}
}
