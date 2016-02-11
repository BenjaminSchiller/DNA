package dna.util.fromArgs;

import java.io.IOException;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.canonical.CliqueGraph;
import dna.graph.generators.canonical.Grid2dGraph;
import dna.graph.generators.canonical.Grid3dGraph;
import dna.graph.generators.canonical.HoneyCombGraph;
import dna.graph.generators.canonical.HoneyCombGraph.ClosedType;
import dna.graph.generators.canonical.RingGraph;
import dna.graph.generators.canonical.RingStarGraph;
import dna.graph.generators.canonical.StarGraph;
import dna.graph.generators.evolvingNetworks.BarabasiAlbertGraph;
import dna.graph.generators.evolvingNetworks.PositiveFeedbackPreferenceGraph;
import dna.graph.generators.random.RandomGraph;
import dna.graph.generators.timestamped.TimestampedGraph;
import dna.graph.generators.timestamped.TimestampedGraph.TimestampedGraphType;
import dna.graph.generators.timestamped.TimestampedReader;
import dna.graph.generators.util.ReadableEdgeListFileGraph;

public class GraphGeneratorFromArgs {
	public static enum GraphType {
		Clique, Grid2d, Grid3d, HoneyComb, Ring, RingStar, Star, Random, BarabasiAlbert, PositiveFeedbackPreference, ReadableEdgeListFileGraph, Timestamped
	}

	public static GraphGenerator parse(GraphDataStructure gds,
			GraphType graphType, String... args) {
		switch (graphType) {
		case Clique:
			return new CliqueGraph(gds, Integer.parseInt(args[0]));
		case Grid2d:
			return new Grid2dGraph(gds, Integer.parseInt(args[0]),
					Integer.parseInt(args[1]), ClosedType.valueOf(args[2]));
		case Grid3d:
			return new Grid3dGraph(gds, Integer.parseInt(args[0]),
					Integer.parseInt(args[1]), Integer.parseInt(args[2]),
					ClosedType.valueOf(args[3]));
		case HoneyComb:
			return new HoneyCombGraph(gds, Integer.parseInt(args[0]),
					Integer.parseInt(args[1]), ClosedType.valueOf(args[2]));
		case Ring:
			return new RingGraph(gds, Integer.parseInt(args[0]));
		case RingStar:
			return new RingStarGraph(gds, Integer.parseInt(args[0]));
		case Star:
			return new StarGraph(gds, Integer.parseInt(args[0]));
		case Random:
			return new RandomGraph(gds, Integer.parseInt(args[0]),
					Integer.parseInt(args[1]));
		case BarabasiAlbert:
			return new BarabasiAlbertGraph(gds, Integer.parseInt(args[0]),
					Integer.parseInt(args[1]), Integer.parseInt(args[2]),
					Integer.parseInt(args[3]));
		case PositiveFeedbackPreference:
			return new PositiveFeedbackPreferenceGraph(gds,
					Integer.parseInt(args[0]), Integer.parseInt(args[1]),
					Integer.parseInt(args[2]));
		case ReadableEdgeListFileGraph:
			return new ReadableEdgeListFileGraph(args[0], args[1], args[2], gds);
		case Timestamped:
			if (args.length == 5) {
				String dir = args[0];
				String filename = args[1];
				String name = args[2];
				TimestampedReader reader;
				try {
					reader = new TimestampedReader(dir, filename, name);
				} catch (IOException e) {
					e.printStackTrace();
					reader = null;
				}
				return new TimestampedGraph(reader, gds,
						TimestampedGraphType.valueOf(args[3]),
						Long.parseLong(args[4]));
			} else if (args.length == 6) {
				String dir = args[0];
				String filename = args[1];
				String name = args[2];
				boolean remapIndex = Boolean.parseBoolean(args[3]);
				TimestampedReader reader = new TimestampedReader(dir, filename,
						name, remapIndex);
				return new TimestampedGraph(reader, gds,
						TimestampedGraphType.valueOf(args[4]),
						Long.parseLong(args[5]));
			} else {
				String dir = args[0];
				String filename = args[1];
				String name = args[2];
				boolean remapIndex = Boolean.parseBoolean(args[3]);
				String separator = args[4];
				String commentPrefix = args[5];
				TimestampedReader reader = new TimestampedReader(dir, filename,
						name, remapIndex, separator, commentPrefix);
				return new TimestampedGraph(reader, gds,
						TimestampedGraphType.valueOf(args[6]),
						Long.parseLong(args[7]));
			}
		default:
			throw new IllegalArgumentException("unknown graph type: "
					+ graphType);
		}
	}
}
