package dna.graph.datastructures.hotswap;

import dna.graph.datastructures.config.DSConfig;
import dna.graph.datastructures.config.DSConfigUndirected;
import dna.graph.datastructures.count.Counting;
import dna.graph.datastructures.count.OperationCounts;
import dna.graph.datastructures.count.OperationCountsUndirected;
import dna.metrics.algorithms.Algorithms;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.BatchData;
import dna.series.data.Value;
import dna.util.Timer;

public aspect HotswapAspects {
	pointcut counting_batchApplication(Series s, Algorithms a) :  
		if(Hotswap.isEnabled() && Counting.isEnabled()) &&
		args(s,a) &&
		call(* SeriesGeneration.generateNextBatch(Series, Algorithms));

	BatchData around(Series s, Algorithms a) : counting_batchApplication(s, a) {
		BatchData bd = proceed(s, a);
		Timer t = new Timer();
		OperationCounts ocs = Hotswap.getCurrentOperationCounts();
		System.out.println("getCounts: " + t.end());
		t = new Timer();

		DSConfig cfg = Hotswap.recommendConfig(ocs, s.getGraph()
				.getGraphDatastructures());
		System.out.println("recommendation: " + t.end());
		t = new Timer();

		DSConfig current = DSConfig.convert(s.getGraph()
				.getGraphDatastructures());
		System.out.println("conversion: " + t.end());
		t = new Timer();

		System.out.println("OCS(" + s.getGraph().getTimestamp() + "): V="
				+ ocs.V.getSum() + " E=" + ocs.E.getSum());

		if (Hotswap.preventUnusedChanges) {
			if (ocs.V.getSum() <= 3) {
				cfg.V = current.V;
			}
			if (ocs.E.getSum() <= 3) {
				cfg.E = current.E;
			}
			if (cfg instanceof DSConfigUndirected
					&& ((OperationCountsUndirected) ocs).adj.getSum() == 0) {
				((DSConfigUndirected) cfg).adj = ((DSConfigUndirected) current).adj;
			}
		}

		if (cfg != null && !current.equals(cfg)
				&& Hotswap.execute(s.getGraph(), ocs, current, cfg)) {
			bd.getValues().add(new Value("hotswap", 1.0));
		} else {
			bd.getValues().add(new Value("hotswap", 0.0));
		}
		System.out.println(t.end());
		t = new Timer();

		return bd;
	}

	pointcut addStats() : 
		if(Hotswap.isEnabled() && Counting.isEnabled()) &&
		call(* SeriesGeneration.generateInitialData(..));

	BatchData around() : addStats() {
		BatchData bd = proceed();
		bd.getValues().add(new Value("hotswap", 0.0));
		return bd;
	}
}
