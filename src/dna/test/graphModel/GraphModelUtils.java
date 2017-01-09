package dna.test.graphModel;

import dna.io.network.netflow.NetflowEvent.NetflowDirection;
import dna.io.network.netflow.NetflowEvent.NetflowEventField;
import dna.metrics.Metric;
import dna.metrics.assortativity.AssortativityU;
import dna.metrics.clustering.DirectedClusteringCoefficientU;
import dna.metrics.connectivity.StrongConnectivityR;
import dna.metrics.connectivity.WeakConnectivityR;
import dna.metrics.degree.DegreeDistributionR;
import dna.metrics.degree.WeightedDegreeDistributionMultiR;
import dna.metrics.paths.unweighted.UnweightedMultiSourceShortestPathsR;
import dna.metrics.richClub.RichClubConnectivityByDegreeU;
import dna.metrics.weights.EdgeWeightsMultiU;
import dna.metrics.weights.NodeWeightsMultiU;
import dna.util.network.NetflowAnalysis.EdgeWeightValue;
import dna.util.network.NetflowAnalysis.NodeWeightValue;

public class GraphModelUtils {

	protected static NetflowEventField[][] parseNetflowEventFields(String[] input) {
		NetflowEventField[][] edges = new NetflowEventField[input.length][];
		for (int i = 0; i < input.length; i++) {
			String[] splits = input[i].split("-");
			NetflowEventField[] fields = new NetflowEventField[splits.length];

			for (int j = 0; j < splits.length; j++) {
				if (splits[j] == null || splits[j].equals("null"))
					fields[j] = NetflowEventField.None;
				else
					fields[j] = NetflowEventField.valueOf(splits[j]);
			}

			edges[i] = fields;
		}

		return edges;
	}

	protected static EdgeWeightValue[] parseEdgeWeightValues(String[] input) {
		EdgeWeightValue[] edgeWeights = new EdgeWeightValue[input.length];
		for (int i = 0; i < input.length; i++) {
			edgeWeights[i] = EdgeWeightValue.valueOf(input[i]);
		}
		return edgeWeights;
	}

	protected static NodeWeightValue[] parseNodeWeightValues(String[] input) {
		NodeWeightValue[] nodeWeights = new NodeWeightValue[input.length];
		for (int i = 0; i < input.length; i++) {
			nodeWeights[i] = NodeWeightValue.valueOf(input[i]);
		}
		return nodeWeights;
	}

	protected static NetflowDirection[] parseEdgeDirections(String[] inputDirections) {
		NetflowDirection[] directions = new NetflowDirection[inputDirections.length];
		for (int i = 0; i < inputDirections.length; i++) {
			if (inputDirections[i].toLowerCase().equals("fw"))
				directions[i] = NetflowDirection.forward;
			if (inputDirections[i].toLowerCase().equals("bw"))
				directions[i] = NetflowDirection.backward;
		}
		return directions;
	}

	/** NODE TYPES **/
	public static final String[] hostType = new String[] { "HOST" };
	public static final String[] portType = new String[] { "PORT" };
	public static final String[] protType = new String[] { "PROT" };

	/** WEIGHTS **/
	public static final String edgeWeightString = "numberOfNetflows;Packets;Bytes";
	public static final String nodeWeightString = "numberOfNetflowsIn;numberOfNetflowsOut;PacketsIn;PacketsOut;BytesIn;BytesOut";

	/** M0 **/
	protected static final String m0EdgesString = "SrcAddress-DstAddress;DstAddress-SrcAddress";
	public static final NetflowEventField[][] m0Edges = parseNetflowEventFields(m0EdgesString.split(";"));

	protected static final String m0DirectionsString = "fw;bw";
	public static final NetflowDirection[] m0Directions = parseEdgeDirections(m0DirectionsString.split(";"));

	protected static final String m0EdgeWeightString = edgeWeightString;
	public static final EdgeWeightValue[] m0EdgeWeights = parseEdgeWeightValues(m0EdgeWeightString.split(";"));

	protected static final String m0NodeWeightString = nodeWeightString;
	public static final NodeWeightValue[] m0NodeWeights = parseNodeWeightValues(m0NodeWeightString.split(";"));

	public static final Metric[] m0AllMetrics = new Metric[] { new DegreeDistributionR(), new AssortativityU(),
			new DirectedClusteringCoefficientU(), new UnweightedMultiSourceShortestPathsR(100),
			new RichClubConnectivityByDegreeU(), new WeakConnectivityR(), new StrongConnectivityR(),
			new WeightedDegreeDistributionMultiR(0, 1.0), new WeightedDegreeDistributionMultiR(1, 1.0),
			new WeightedDegreeDistributionMultiR(2, 1000.0), new NodeWeightsMultiU(0, 1.0),
			new NodeWeightsMultiU(1, 1.0), new NodeWeightsMultiU(2, 1000.0), new NodeWeightsMultiU(3, 1.0),
			new NodeWeightsMultiU(4, 1.0), new NodeWeightsMultiU(5, 1000.0), new EdgeWeightsMultiU(0, 1.0),
			new EdgeWeightsMultiU(1, 1.0), new EdgeWeightsMultiU(2, 1000.0) };

	/** M1 **/
	protected static final String m1EdgesString = "SrcAddress-DstPort-DstAddress;DstAddress-SrcPort-SrcAddress";
	public static final NetflowEventField[][] m1Edges = parseNetflowEventFields(m1EdgesString.split(";"));

	protected static final String m1DirectionsString = "fw;bw";
	public static final NetflowDirection[] m1Directions = parseEdgeDirections(m1DirectionsString.split(";"));

	protected static final String m1EdgeWeightString = edgeWeightString;
	public static final EdgeWeightValue[] m1EdgeWeights = parseEdgeWeightValues(m1EdgeWeightString.split(";"));

	protected static final String m1NodeWeightString = nodeWeightString;
	public static final NodeWeightValue[] m1NodeWeights = parseNodeWeightValues(m1NodeWeightString.split(";"));

	public static final Metric[] m1allMetrics = new Metric[] { new DegreeDistributionR(hostType),
			new DegreeDistributionR(portType), new AssortativityU(), new DirectedClusteringCoefficientU(),
			new UnweightedMultiSourceShortestPathsR(100), new RichClubConnectivityByDegreeU(), new WeakConnectivityR(),
			new StrongConnectivityR(), new WeightedDegreeDistributionMultiR(0, 1.0),
			new WeightedDegreeDistributionMultiR(hostType, 0, 1.0),
			new WeightedDegreeDistributionMultiR(portType, 0, 1.0), new WeightedDegreeDistributionMultiR(1, 1.0),
			new WeightedDegreeDistributionMultiR(hostType, 1, 1.0),
			new WeightedDegreeDistributionMultiR(portType, 1, 1.0), new WeightedDegreeDistributionMultiR(2, 1000.0),
			new WeightedDegreeDistributionMultiR(hostType, 2, 1000.0),
			new WeightedDegreeDistributionMultiR(portType, 2, 1000.0), new NodeWeightsMultiU(0, 1.0),
			new NodeWeightsMultiU(1, 1.0), new NodeWeightsMultiU(2, 1000.0), new NodeWeightsMultiU(3, 1.0),
			new NodeWeightsMultiU(4, 1.0), new NodeWeightsMultiU(5, 1000.0), new EdgeWeightsMultiU(0, 1.0),
			new EdgeWeightsMultiU(1, 1.0), new EdgeWeightsMultiU(2, 1000.0) };

	/** M2 **/
	protected static final String m2EdgesString = "SrcAddress-DstPort-DstAddress;SrcAddress-DstAddress;DstAddress-SrcPort-SrcAddress;DstAddress-SrcAddress";
	public static final NetflowEventField[][] m2Edges = parseNetflowEventFields(m2EdgesString.split(";"));

	protected static final String m2DirectionsString = "fw;fw;bw;bw";
	public static final NetflowDirection[] m2Directions = parseEdgeDirections(m2DirectionsString.split(";"));

	protected static final String m2EdgeWeightString = edgeWeightString;
	public static final EdgeWeightValue[] m2EdgeWeights = parseEdgeWeightValues(m2EdgeWeightString.split(";"));

	protected static final String m2NodeWeightString = nodeWeightString;
	public static final NodeWeightValue[] m2NodeWeights = parseNodeWeightValues(m2NodeWeightString.split(";"));

	public static final Metric[] m2allMetrics = m1allMetrics;

	/** M3 **/
	protected static final String m3EdgesString = "SrcAddress-Protocol-DstAddress;DstAddress-Protocol-SrcAddress";
	public static final NetflowEventField[][] m3Edges = parseNetflowEventFields(m3EdgesString.split(";"));

	protected static final String m3DirectionsString = "fw;bw";
	public static final NetflowDirection[] m3Directions = parseEdgeDirections(m3DirectionsString.split(";"));

	protected static final String m3EdgeWeightString = edgeWeightString;
	public static final EdgeWeightValue[] m3EdgeWeights = parseEdgeWeightValues(m3EdgeWeightString.split(";"));

	protected static final String m3NodeWeightString = nodeWeightString;
	public static final NodeWeightValue[] m3NodeWeights = parseNodeWeightValues(m3NodeWeightString.split(";"));

	public static final Metric[] m3allMetrics = new Metric[] { new DegreeDistributionR(hostType),
			new DegreeDistributionR(protType), new AssortativityU(), new DirectedClusteringCoefficientU(),
			new UnweightedMultiSourceShortestPathsR(100), new RichClubConnectivityByDegreeU(), new WeakConnectivityR(),
			new StrongConnectivityR(), new WeightedDegreeDistributionMultiR(0, 1.0),
			new WeightedDegreeDistributionMultiR(hostType, 0, 1.0),
			new WeightedDegreeDistributionMultiR(protType, 0, 1.0), new WeightedDegreeDistributionMultiR(1, 1.0),
			new WeightedDegreeDistributionMultiR(hostType, 1, 1.0),
			new WeightedDegreeDistributionMultiR(protType, 1, 1.0), new WeightedDegreeDistributionMultiR(2, 1000.0),
			new WeightedDegreeDistributionMultiR(hostType, 2, 1000.0),
			new WeightedDegreeDistributionMultiR(protType, 2, 1000.0), new NodeWeightsMultiU(0, 1.0),
			new NodeWeightsMultiU(1, 1.0), new NodeWeightsMultiU(2, 1000.0), new NodeWeightsMultiU(3, 1.0),
			new NodeWeightsMultiU(4, 1.0), new NodeWeightsMultiU(5, 1000.0), new EdgeWeightsMultiU(0, 1.0),
			new EdgeWeightsMultiU(1, 1.0), new EdgeWeightsMultiU(2, 1000.0) };

	/** GRAPH MODELS **/
	public static final GraphModel model0 = new GraphModel("m0", m0Edges, m0Directions, m0EdgeWeights, m0NodeWeights,
			m0AllMetrics);
	public static final GraphModel model1 = new GraphModel("m1", m1Edges, m1Directions, m1EdgeWeights, m1NodeWeights,
			m1allMetrics);
	public static final GraphModel model2 = new GraphModel("m2", m2Edges, m2Directions, m2EdgeWeights, m2NodeWeights,
			m2allMetrics);
	public static final GraphModel model3 = new GraphModel("m3", m3Edges, m3Directions, m3EdgeWeights, m3NodeWeights,
			m3allMetrics);

}
