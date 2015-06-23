package dna.graph.datastructures.hotswap;

import dna.graph.datastructures.config.DSConfig;
import dna.graph.datastructures.count.Counting;
import dna.graph.datastructures.count.OperationCounts;
import dna.metrics.algorithms.Algorithms;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.series.data.BatchData;
import dna.series.data.Value;

public aspect HotswapAspects {
	pointcut counting_batchApplication(Series s, Algorithms a) :  
		if(Hotswap.isEnabled() && Counting.isEnabled()) &&
		args(s,a) &&
		call(* SeriesGeneration.generateNextBatch(Series, Algorithms));

	BatchData around(Series s, Algorithms a) : counting_batchApplication(s, a) {
		BatchData bd = proceed(s, a);
		OperationCounts ocs = Hotswap.getCurrentOperationCounts();
		DSConfig cfg = Hotswap.recommendConfig(ocs, s.getGraph()
				.getGraphDatastructures());
		DSConfig current = DSConfig.convert(s.getGraph()
				.getGraphDatastructures());
		if (cfg != null && !current.equals(cfg)
				&& Hotswap.execute(s.getGraph(), ocs, current, cfg)) {
			bd.getValues().add(new Value("hotswap", 1.0));
		} else {
			bd.getValues().add(new Value("hotswap", 0.0));
		}
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
