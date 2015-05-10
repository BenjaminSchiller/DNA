package dna.metrics.patternEnum.patterncounter;

import java.util.HashMap;
import java.util.List;

import dna.metrics.patternEnum.datastructures.SmallGraph;

public interface IPatternCounter {
	int incrementCounterFor(SmallGraph graph);
	int decrementCounterFor(SmallGraph graph);
	int getTotalMotifCount();
	List<MotifType> getOrderedMotifs();
	HashMap<MotifType, Integer> getMotifCounter();
}
