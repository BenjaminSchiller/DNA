package dna.graph.datastructures.count;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.metrics.algorithms.Algorithms;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public aspect CountingAspects {

	pointcut counting_init(Series s, int q, int w, boolean e, boolean r,
			boolean t, long z) : 
				if(Counting.isEnabled()) &&
				args(s, q, w, e, r, t, z) &&
				call(* SeriesGeneration.generate(Series, int, int, boolean, boolean, boolean, long));

	before(Series s, int q, int w, boolean e, boolean r, boolean t, long z) : counting_init(s, q, w, e, r, t, z) {
		Counting.init(s.getGraphGenerator().getGds());
	}

	pointcut counting_startRun(Series s, int q, int w, boolean e, boolean r,
			long t) :  
				if(Counting.isEnabled()) &&
				args(s, q, w, e, r, t) &&
				call(* SeriesGeneration.generateRun(Series, int, int, boolean, boolean, long));

	before(Series s, int q, int w, boolean e, boolean r, long t) : counting_startRun(s, q, w, e, r, t) {
		Counting.startRun();
	}

	pointcut counting_graphGeneration_NEW(GraphGenerator gg, Series s,
			Algorithms a) :  
		if(Counting.isEnabled()) &&
		target(gg) &&
		call(* GraphGenerator.generate()) &&
		cflow(call(BatchData SeriesGeneration.computeInitialData(Series, Algorithms)) && args(s,a));

	Graph around(GraphGenerator gg, Series s, Algorithms a) : counting_graphGeneration_NEW(gg, s, a) {
		Graph g = proceed(gg, s, a);
		Counting.endGraphGeneration(g);
		return g;
	}

	pointcut counting_metricInit(Series s, BatchData bd, Algorithms a) :  
		if(Counting.isEnabled()) &&
		args(s, bd, a) &&
		call(* SeriesGeneration.computeInitialMetrics(Series, BatchData, Algorithms));

	BatchData around(Series s, BatchData bd, Algorithms a) : counting_metricInit(s, bd, a) {
		BatchData res = proceed(s, bd, a);
		Counting.endMetricInit(s.getGraph());
		return res;
	}

	pointcut counting_batchGeneration(Graph g, Series s, Algorithms a) :  
		if(Counting.isEnabled()) &&
		args(g) &&
		call(* BatchGenerator.generate(Graph)) &&
		cflow(call(* SeriesGeneration.computeNextBatch(Series, Algorithms)) && args(s, a));

	Batch around(Graph g, Series s, Algorithms a) : counting_batchGeneration(g, s, a) {
		Batch res = proceed(g, s, a);
		Counting.endBatchGeneration(g);
		return res;
	}

	pointcut counting_batchApplication(Series s, Algorithms a) :  
		if(Counting.isEnabled()) &&
		args(s, a) &&
		call(* SeriesGeneration.generateNextBatch(Series, Algorithms));

	BatchData around(Series s, Algorithms a) : counting_batchApplication(s, a) {
		BatchData res = proceed(s, a);
		Counting.endBatchApplication(s.getGraph());
		return res;
	}

	/*
	 * WRITING all operation counts for finished batch
	 */

	pointcut counting_batchWrite(String dir) :
		if(Counting.isEnabled()) &&
		args(dir) &&
		call(* BatchData.write(String));

	after(String dir) : counting_batchWrite(dir) {
		System.out.println("WROTE batch to dir: " + dir);
		try {
			if (Counting.batchGeneration.size() == 0) {
				Counting.graphGeneration.writeValues(dir,
						Counting.graphGenerationFilename);
				Counting.metricInit.writeValues(dir,
						Counting.metricInitFilename);
				System.out.println("  " + dir
						+ Counting.graphGenerationFilename);
				System.out.println("  " + dir + Counting.metricInitFilename);
			} else {
				Counting.batchGeneration.getLast().writeValues(dir,
						Counting.batchGenerationFilename);
				Counting.batchApplication.getLast().writeValues(dir,
						Counting.batchApplicationFilename);
				System.out.println("  " + dir
						+ Counting.batchGenerationFilename);
				System.out.println("  " + dir
						+ Counting.batchApplicationFilename);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * INITIALIZATION of operation count in case a graph is generated outside of
	 * a series generation
	 */

	pointcut counting_graphGenerationSeparateInit(GraphGenerator gg) :  
		if(Counting.isEnabled()) &&
		target(gg) &&
		call(* GraphGenerator.newGraphInstance());

	before(GraphGenerator gg) : counting_graphGenerationSeparateInit(gg) {
		if (Counting.oc == null) {
			Counting.init(gg.getGds());
		}
	}
}
