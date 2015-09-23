package dna.project.gds.test;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.datastructures.GDS;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.canonical.CliqueGraph;
import dna.graph.generators.canonical.HoneyCombGraph;
import dna.graph.generators.canonical.HoneyCombGraph.ClosedType;
import dna.graph.generators.canonical.RingGraph;
import dna.graph.generators.random.RandomGraph;
import dna.graph.generators.util.ReadableFileGraph;
import dna.io.filter.SuffixFilenameFilter;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.evolvingNetworks.BarabasiAlbertBatch;
import dna.updates.generators.random.RandomBatch;
import dna.updates.generators.util.ReadableDirBatchGenerator;
import dna.visualization.graph.GraphVisualization;

public class TestGraphVisualization {
	public static void main(String[] args) throws IOException,
			InterruptedException {
		GraphVisualization.enable();

		// GraphGenerator gg = new RandomGraph(GDS.directedVE(IntWeight.class,
		// WeightSelection.RandPos10, Int2dWeight.class,
		// WeightSelection.RandPos10), 10, 20);
		// BatchGenerator bg = new RandomBatch(1, 1, 1, 1);
		// bg = new RandomBatch(1, 0, 1, WeightSelection.RandPos100, 1, 0, 1,
		// WeightSelection.RandPos100);
		//
		// gg = new RandomGraph(GDS.undirected(), 20, 100);
		// BatchGenerator bg1 = new BarabasiAlbertBatch(10, 2);
		// BatchGenerator bg2 = new RandomBatch(1, 2, 3, 10);

		GraphDataStructure gds = GDS.directed();

		GraphGenerator ringGG = new RingGraph(gds, 20);
		GraphGenerator cliqueGG = new CliqueGraph(gds, 10);
		GraphGenerator randomGG = new RandomGraph(gds, 20, 100);

		BatchGenerator nrBG = new RandomBatch(0, 1, 0, 0);
		BatchGenerator naBG = new RandomBatch(1, 0, 0, 0);
		BatchGenerator erBG = new RandomBatch(0, 0, 0, 10);
		BatchGenerator eaBG = new RandomBatch(0, 0, 10, 0);
		BatchGenerator baBG = new BarabasiAlbertBatch(2, 3);

		String mdDir = "/Users/benni/TUD/datasets/md/pnB_th_7_short/";
		GraphGenerator mdGG = new ReadableFileGraph(mdDir, "0.dnag",
				GDS.undirected());
		BatchGenerator mdBG = new ReadableDirBatchGenerator("MD", mdDir,
				new SuffixFilenameFilter(".dnab"));

		// apply(cliqueGG, 10, erBG);
		// apply(mdGG, 10000, mdBG);
		// apply(randomGG, 100, baBG);

		// apply(new RingGraph(gds, 20), 100, new RandomBatch(0, 0, 1, 0));
		// apply(new RandomGraph(gds, 10, 35), 100, new BarabasiAlbertBatch(1,
		// 2));
		// apply(new CliqueGraph(gds, 20), 100, new RandomBatch(0, 0, 0, 5));
//		apply(new GridGraph(gds, 5, 5), 100);
		
		apply(new HoneyCombGraph(GDS.directed(), 10, 5, ClosedType.CLOSED));
	}

	public static final long waitAfterGG = 3000;
	public static final long waitAfterBG = 1000;

	public static void apply(GraphGenerator gg, int batches,
			BatchGenerator... bgs) throws InterruptedException {
		System.out.println("generating graph");
		Graph g = gg.generate();
		Thread.sleep(waitAfterGG);
		for (BatchGenerator bg : bgs) {
			apply(g, bg, batches);
		}
	}

	public static void apply(GraphGenerator gg) {
		System.out.println("generating graph");
		Graph g = gg.generate();
	}

	public static void apply(Graph g, BatchGenerator bg, int batches)
			throws InterruptedException {
		for (int i = 0; i < batches; i++) {
			if (!bg.isFurtherBatchPossible(g)) {
				System.out.println("no further batch possible");
				break;
			}
			Batch b = bg.generate(g);
			BatchSanitization.sanitize(b);
			System.out.println("applying batch1: " + b);
			b.apply(g);
			Thread.sleep(waitAfterBG);
		}
	}
}
