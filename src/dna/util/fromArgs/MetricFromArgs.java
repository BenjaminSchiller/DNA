package dna.util.fromArgs;

import dna.metrics.Metric;
import dna.metrics.assortativity.AssortativityR;
import dna.metrics.assortativity.AssortativityU;
import dna.metrics.centrality.BetweennessCentralityR;
import dna.metrics.centrality.BetweennessCentralityU;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.metrics.clustering.UndirectedClusteringCoefficientU;
import dna.metrics.connectivity.WCBasicR;
import dna.metrics.connectivity.WCBasicU;
import dna.metrics.connectivity.WCSimpleR;
import dna.metrics.connectivity.WCSimpleU;
import dna.metrics.connectivity.WeakConnectivityB;
import dna.metrics.connectivity.WeakConnectivityR;
import dna.metrics.connectivity.WeakConnectivityU;
import dna.metrics.degree.DegreeDistributionR;
import dna.metrics.degree.DegreeDistributionU;
import dna.metrics.motifs.UndirectedMotifsR;
import dna.metrics.motifs.UndirectedMotifsU;
import dna.metrics.paths.unweighted.UnweightedAllPairsShortestPathsR;
import dna.metrics.paths.unweighted.UnweightedAllPairsShortestPathsU;
import dna.metrics.richClub.RichClubConnectivityByDegreeR;
import dna.metrics.richClub.RichClubConnectivityByDegreeU;

public class MetricFromArgs {
	public static enum MetricType {
		DegreeDistributionR, DegreeDistributionU, UndirectedClusteringCoefficientR, UndirectedClusteringCoefficientU, PartitionedUndirectedClusteringCoefficientR, UnweightedAllPairsShortestPathsR, UnweightedAllPairsShortestPathsU, WeakConnectivityR, WeakConnectivityU, WeakConnectivityB, UndirectedMotifsR, UndirectedMotifsU, AssortativityR, AssortativityU, BetweennessCentralityR, BetweennessCentralityU, RichClubConnectivityByDegreeR, RichClubConnectivityByDegreeU, WCSimpleR, WCSimpleU, WCBasicR, WCBasicU
	}

	public static Metric[] parse(String[] metricTypes) {
		MetricType[] types = new MetricType[metricTypes.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = MetricType.valueOf(metricTypes[i]);
		}
		return parse(types);
	}

	public static Metric[] parse(MetricType[] metricTypes) {
		Metric[] metrics = new Metric[metricTypes.length];
		for (int i = 0; i < metrics.length; i++) {
			metrics[i] = parse(metricTypes[i]);
		}
		return metrics;
	}

	public static Metric parse(MetricType metricType, String... args) {
		return parse(new String[0], metricType, args);
	}

	public static Metric parse(String[] nodeTypes, MetricType metricType,
			String... args) {
		switch (metricType) {
		case DegreeDistributionR:
			return new DegreeDistributionR(nodeTypes);
		case DegreeDistributionU:
			return new DegreeDistributionU(nodeTypes);
		case UndirectedClusteringCoefficientR:
			return new UndirectedClusteringCoefficientR(nodeTypes);
		case UndirectedClusteringCoefficientU:
			return new UndirectedClusteringCoefficientU();
		case UnweightedAllPairsShortestPathsR:
			return new UnweightedAllPairsShortestPathsR(nodeTypes);
		case UnweightedAllPairsShortestPathsU:
			return new UnweightedAllPairsShortestPathsU(nodeTypes);
		case WeakConnectivityB:
			return new WeakConnectivityB();
		case WeakConnectivityR:
			return new WeakConnectivityR();
		case WeakConnectivityU:
			return new WeakConnectivityU();
		case UndirectedMotifsR:
			return new UndirectedMotifsR();
		case UndirectedMotifsU:
			return new UndirectedMotifsU();
		case AssortativityR:
			return new AssortativityR(nodeTypes);
		case AssortativityU:
			return new AssortativityU(nodeTypes);
		case BetweennessCentralityR:
			return new BetweennessCentralityR(nodeTypes);
		case BetweennessCentralityU:
			return new BetweennessCentralityU(nodeTypes);
		case RichClubConnectivityByDegreeR:
			return new RichClubConnectivityByDegreeR();
		case RichClubConnectivityByDegreeU:
			return new RichClubConnectivityByDegreeU();
		case PartitionedUndirectedClusteringCoefficientR:
		case WCSimpleR:
			return new WCSimpleR();
		case WCSimpleU:
			return new WCSimpleU();
		case WCBasicR:
			return new WCBasicR();
		case WCBasicU:
			return new WCBasicU();
		default:
			throw new IllegalArgumentException("unknown metric type: "
					+ metricType);
		}
	}

}
