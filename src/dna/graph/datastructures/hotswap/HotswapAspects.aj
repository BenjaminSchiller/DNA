package dna.graph.datastructures.hotswap;

import dna.graph.datastructures.count.Counting;
import dna.metrics.algorithms.Algorithms;
import dna.series.Series;
import dna.series.SeriesGeneration;

public aspect HotswapAspects {
	pointcut counting_batchApplication(Series s, Algorithms a) :  
		if(Hotswap.isEnabled() && Counting.isEnabled()) &&
		args(s,a) &&
		call(* SeriesGeneration.generateNextBatch(Series, Algorithms));

	after(Series s, Algorithms a) : counting_batchApplication(s, a) {
		System.out.println("hotswap end...");
		Hotswap.check();
	}
}
