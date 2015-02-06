package dna.visualization.demo;

import java.io.File;
import java.util.ArrayList;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.canonical.CliqueGraph;
import dna.graph.generators.canonical.RingGraph;
import dna.graph.generators.evolvingNetworks.BarabasiAlbertGraph;
import dna.graph.generators.random.RandomGraph;
import dna.graph.generators.util.ReadableFileGraph;
import dna.io.filter.SuffixFilenameFilter;
import dna.metrics.Metric;
import dna.metrics.assortativity.AssortativityR;
import dna.metrics.assortativity.AssortativityU;
import dna.metrics.centrality.BetweennessCentralityR;
import dna.metrics.centrality.BetweennessCentralityU;
import dna.metrics.clustering.DirectedClusteringCoefficientR;
import dna.metrics.clustering.DirectedClusteringCoefficientU;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.metrics.clustering.UndirectedClusteringCoefficientU;
import dna.metrics.connectivity.StrongConnectivityR;
import dna.metrics.connectivity.StrongConnectivityU;
import dna.metrics.connectivity.WeakConnectivityR;
import dna.metrics.connectivity.WeakConnectivityU;
import dna.metrics.degree.DegreeDistributionR;
import dna.metrics.degree.DegreeDistributionU;
import dna.metrics.motifs.DirectedMotifsR;
import dna.metrics.motifs.DirectedMotifsU;
import dna.metrics.motifs.UndirectedMotifsR;
import dna.metrics.motifs.UndirectedMotifsU;
import dna.metrics.paths.UnweightedAllPairsShortestPathsR;
import dna.metrics.paths.UnweightedAllPairsShortestPathsU;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.evolvingNetworks.BarabasiAlbertBatch;
import dna.updates.generators.evolvingNetworks.RandomGrowth;
import dna.updates.generators.random.RandomBatch;
import dna.updates.generators.util.BatchRepetition;
import dna.updates.generators.util.LegacyReadableDirBatchGenerator;

public class Demo {

	public static enum CFG {
		config_1024_x_640, config_1280_x_800, config_1600_x_1000, config_1920_x_1200
	}

	// 1920 x 1200
	// 1600 x 1000
	// 1280 x 800
	// 1024 x 640

	public enum GDS {
		undirected, directed
	}

	public enum GG {
		RANDOM_100_500, RANDOM_1k_10k, BARABASI_ALBERT_1k_2, BARABASI_ALBERT_5k_5, FILE, RING_100, RING_1k, CLIQUE_100, CLIQUE_500
	};

	public enum BG {
		RANDOM_0_0_10_0, RANDOM_0_0_100_20, BARABASI_ALBERT_GROWTH_10_2, BARABASI_ALBERT_GROWTH_50_5, DIR, ALTERNATING_5_50EA_40ER, RANDOM_GROWTH_10_5, RANDOM_GROWTH_100_10
	};

	public enum METRIC {
		DegreeDistributionR, DegreeDistributionU, AssortativityR, AssortativityU, BetweennessCentralityR, BetweennessCentralityU, DirectedClusteringCoefficientR, DirectedClusteringCoefficientU, UndirectedClusteringCoefficientR, UndirectedClusteringCoefficientU, StrongConnectivityR, StrongConnectivityU, WeakConnectivityR, WeakConnectivityU, DirectedMotifsR, DirectedMotifsU, UndirectedMotifsR, UndirectedMotifsU, UnweightedAllPairsShortestPathsR, UnweightedAllPairsShortestPathsU
	};

	public static Metric getMetric(METRIC m) {
		switch (m) {
		case DegreeDistributionR:
			return new DegreeDistributionR();
		case DegreeDistributionU:
			return new DegreeDistributionU();
		case AssortativityR:
			return new AssortativityR();
		case AssortativityU:
			return new AssortativityU();
		case BetweennessCentralityR:
			return new BetweennessCentralityR();
		case BetweennessCentralityU:
			return new BetweennessCentralityU();
		case DirectedClusteringCoefficientR:
			return new DirectedClusteringCoefficientR();
		case DirectedClusteringCoefficientU:
			return new DirectedClusteringCoefficientU();
		case DirectedMotifsR:
			return new DirectedMotifsR();
		case DirectedMotifsU:
			return new DirectedMotifsU();
		case StrongConnectivityR:
			return new StrongConnectivityR();
		case StrongConnectivityU:
			return new StrongConnectivityU();
		case UndirectedClusteringCoefficientR:
			return new UndirectedClusteringCoefficientR();
		case UndirectedClusteringCoefficientU:
			return new UndirectedClusteringCoefficientU();
		case UndirectedMotifsR:
			return new UndirectedMotifsR();
		case UndirectedMotifsU:
			return new UndirectedMotifsU();
		case UnweightedAllPairsShortestPathsR:
			return new UnweightedAllPairsShortestPathsR();
		case UnweightedAllPairsShortestPathsU:
			return new UnweightedAllPairsShortestPathsU();
		case WeakConnectivityR:
			return new WeakConnectivityR();
		case WeakConnectivityU:
			return new WeakConnectivityU();
		default:
			return null;
		}
	}

	public static final int[] B = new int[] { 10, 20, 50, 100, 200, 500, 1000,
			10000 };

	public static final int[] R = new int[] { 1, 2, 5, 10, 20 };

	public static final int[] T = new int[] { 1000, 2000, 5000, 100, 200, 500 };

	public GG gg;
	public GDS gds;
	public BG bg;

	public File file;
	public File dir;

	public ArrayList<METRIC> metrics;

	public int batches;
	public int runs;
	public int time;

	public CFG cfg;

	public Demo(GG gg, GDS gds, BG bg, ArrayList<METRIC> metrics, int batches,
			int runs, int time) {
		this.gg = gg;
		this.gds = gds;
		this.bg = bg;
		this.metrics = metrics;
		this.batches = batches;
		this.runs = runs;
		this.time = time;
	}

	public GraphGenerator getGG() {
		switch (gg) {
		case RANDOM_100_500:
			return new RandomGraph(this.getGDS(), 100, 500);
		case RANDOM_1k_10k:
			return new RandomGraph(this.getGDS(), 1000, 10000);
		case BARABASI_ALBERT_1k_2:
			return new BarabasiAlbertGraph(this.getGDS(), 20, 40, 980, 2);
		case BARABASI_ALBERT_5k_5:
			return new BarabasiAlbertGraph(this.getGDS(), 50, 250, 4950, 5);
		case CLIQUE_100:
			return new CliqueGraph(this.getGDS(), 100);
		case CLIQUE_500:
			return new CliqueGraph(this.getGDS(), 500);
		case RING_100:
			return new RingGraph(this.getGDS(), 100);
		case RING_1k:
			return new RingGraph(this.getGDS(), 1000);
		case FILE:
			try {
				return new ReadableFileGraph(this.file.getParent() + "/",
						this.file.getName(), this.getGDS());
			} catch (Exception e) {
				e.printStackTrace();
			}
		default:
			return null;
		}
	}

	public GraphDataStructure getGDS() {
		switch (gds) {
		case directed:
			return dna.graph.datastructures.GDS.directed();
		case undirected:
			return dna.graph.datastructures.GDS.undirected();
		default:
			return null;
		}
	}

	public BatchGenerator getBG() {
		switch (bg) {
		case RANDOM_0_0_100_20:
			return new RandomBatch(0, 0, 100, 20);
		case RANDOM_0_0_10_0:
			return new RandomBatch(0, 0, 10, 0);
		case ALTERNATING_5_50EA_40ER:
			return new BatchRepetition(5, new RandomBatch(0, 0, 50, 40),
					new RandomBatch(0, 0, 0, 40));
		case RANDOM_GROWTH_100_10:
			return new RandomGrowth(100, 10);
		case RANDOM_GROWTH_10_5:
			return new RandomGrowth(10, 5);
		case BARABASI_ALBERT_GROWTH_10_2:
			return new BarabasiAlbertBatch(10, 2);
		case BARABASI_ALBERT_GROWTH_50_5:
			return new BarabasiAlbertBatch(50, 5);
		case DIR:
			return new LegacyReadableDirBatchGenerator(this.dir.getName(),
					this.dir.getAbsolutePath() + "/", new SuffixFilenameFilter(
							".batch"));
		default:
			return null;
		}
	}

	public Metric[] getMetrics() {
		Metric[] m = new Metric[this.metrics.size()];
		for (int i = 0; i < m.length; i++) {
			m[i] = getMetric(this.metrics.get(i));
		}
		return m;
	}

	public int getBatches() {
		return this.batches;
	}

	public int getRuns() {
		return this.runs;
	}

	public int getTime() {
		return this.time;
	}

	public String getName() {
		return this.gg + " - " + this.bg;
	}

	public String getDataDir() {
		return "data/" + this.gg + "--" + this.bg + "/";
	}

	public String getRunDir() {
		return this.getDataDir() + "run.0/";
	}

	public String getPlotDir() {
		return "data/" + this.gg + "--" + this.bg + "--plots/";
	}

	public String getTexDir() {
		return "data/" + this.gg + "--" + this.bg + "--tex/";
	}

	public String getTexFilename() {
		return "__analysis.tex";
	}

	public String getCfg() {
		return "config/vis/" + this.cfg + ".cfg";
	}
}
