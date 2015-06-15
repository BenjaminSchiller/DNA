package dna.profiler;

import java.io.IOException;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.metrics.algorithms.Algorithms;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public aspect CountsAspects {
	pointcut counting_init(Series s, int q, int w, boolean e, boolean r,
			boolean t, long z) : (
			args(s, q, w, e, r, t, z) &&
			call(* SeriesGeneration.generate(Series, int, int, boolean, boolean, boolean, long)));

	before(Series s, int q, int w, boolean e, boolean r, boolean t, long z) : counting_init(s, q, w, e, r, t, z) {
		Counting.init(s.getGraphGenerator().getGds());
	}

	pointcut counting_startRun(Series s, int q, int w, boolean e, boolean r,
			long t) : (
			args(s, q, w, e, r, t) &&
			call(* SeriesGeneration.generateRun(Series, int, int, boolean, boolean, long)));

	before(Series s, int q, int w, boolean e, boolean r, long t) : counting_startRun(s, q, w, e, r, t) {
		Counting.startRun();
	}

	pointcut counting_graphGeneration(GraphGenerator gg) : (
			target(gg) &&
			call(* GraphGenerator.generate()) &&
			withincode(* SeriesGeneration.computeInitialData(..)));

	Graph around(GraphGenerator gg) : counting_graphGeneration(gg) {
		Graph g = proceed(gg);
		Counting.endGraphGeneration(g);
		try {
			Counting.graphGeneration.writeValues("stats/",
					"0-graphGeneration.values");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return g;
	}

	pointcut counting_metricInit(Series s, BatchData bd, Algorithms a) : (
			args(s, bd, a) &&
			call(* SeriesGeneration.computeInitialMetrics(Series, BatchData, Algorithms)));

	BatchData around(Series s, BatchData bd, Algorithms a) : counting_metricInit(s, bd, a) {
		BatchData res = proceed(s, bd, a);
		Counting.endMetricInit(s.getGraph());
		try {
			Counting.metricInit.writeValues("stats/", "0-metricInit.values");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	pointcut counting_batchGeneration(Graph g) : (
			args(g) &&
			call(* BatchGenerator.generate(Graph)) &&
			withincode(* SeriesGeneration.computeNextBatch(..)));

	Batch around(Graph g) : counting_batchGeneration(g) {
		Batch res = proceed(g);
		Counting.endBatchGeneration(g);
		try {
			Counting.batchGeneration.getLast().writeValues("stats/",
					+res.getTo() + "-batchGeneration.values");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	pointcut counting_batchApplication(Series s, Algorithms a) : (
			args(s, a) &&
			call(* SeriesGeneration.generateNextBatch(Series, Algorithms)));

	BatchData around(Series s, Algorithms a) : counting_batchApplication(s, a) {
		BatchData res = proceed(s, a);
		Counting.endBatchApplication(s.getGraph());
		try {
			Counting.batchApplication.getLast().writeValues("stats/",
					res.getTimestamp() + "-batchApplication.values");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	/*
	 * INITIALIZATION of operation count in case a graph is generated outside of
	 * a series generation
	 */

	pointcut counting_graphGenerationSeparateInit(GraphGenerator gg) : (
			target(gg) &&
			call(* GraphGenerator.newGraphInstance()));

	before(GraphGenerator gg) : counting_graphGenerationSeparateInit(gg) {
		if (Counting.oc == null) {
			Counting.init(gg.getGds());
		}
	}
}
