package dna.util.fromArgs;

import dna.metrics.Metric;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.metrics.clustering.UndirectedClusteringCoefficientU;
import dna.metrics.degree.DegreeDistributionR;
import dna.metrics.degree.DegreeDistributionU;
import dna.metrics.paths.UnweightedAllPairsShortestPathsR;
import dna.metrics.paths.UnweightedAllPairsShortestPathsU;

public class MetricFromArgs {
	public static enum MetricType {
		DegreeDistributionR, DegreeDistributionU, UndirectedClusteringCoefficientR, UndirectedClusteringCoefficientU, UnweightedAllPairsShortestPathsR, UnweightedAllPairsShortestPathsU
	}

	public static Metric parse(MetricType metricType, String... args) {
		switch (metricType) {
		case DegreeDistributionR:
			return new DegreeDistributionR();
		case DegreeDistributionU:
			return new DegreeDistributionU();
		case UndirectedClusteringCoefficientR:
			return new UndirectedClusteringCoefficientR();
		case UndirectedClusteringCoefficientU:
			return new UndirectedClusteringCoefficientU();
		case UnweightedAllPairsShortestPathsR:
			return new UnweightedAllPairsShortestPathsR();
		case UnweightedAllPairsShortestPathsU:
			return new UnweightedAllPairsShortestPathsU();
		default:
			throw new IllegalArgumentException("unknown metric type: "
					+ metricType);
		}
	}

}
