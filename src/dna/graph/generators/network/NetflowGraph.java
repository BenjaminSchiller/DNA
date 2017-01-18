package dna.graph.generators.network;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.network.UpdateEvent;
import dna.io.network.NetworkEvent;
import dna.io.network.netflow.NetflowEvent.NetflowDirection;
import dna.io.network.netflow.NetflowEvent.NetflowEventField;
import dna.io.network.netflow.NetflowEventReader;
import dna.io.network.netflow.darpa.DarpaNetflowReader;
import dna.updates.batch.Batch;
import dna.util.network.NetflowAnalysis.EdgeWeightValue;
import dna.util.network.NetflowAnalysis.NodeWeightValue;

public class NetflowGraph extends NetworkGraph {

	private NetflowEventReader reader;
	private NetflowBatch batchGenerator;

	private int batchLengthSeconds;
	private int edgeLifeTimeSeconds;

	private NetflowEventField[][] edges;
	private NetflowDirection[] edgeDirections;
	private EdgeWeightValue[] edgeWeights;
	private NodeWeightValue[] nodeWeights;

	private DateTime initDateTime;
	private DateTime firstDateTime;

	private Graph graph;

	public NetflowGraph(GraphDataStructure gds, long timestampInit, long maximumTimestamp, String dir, String filename,
			int batchLengthSeconds, int edgeLifeTimeSeconds, NetflowEventField[][] edges,
			NetflowDirection[] edgeDirections, EdgeWeightValue[] edgeWeights, NodeWeightValue[] nodeWeights)
			throws IOException, ParseException {
		this(gds, timestampInit, maximumTimestamp, dir, filename, batchLengthSeconds, edgeLifeTimeSeconds, true, true,
				0, edges, edgeDirections, edgeWeights, nodeWeights);
	}

	public NetflowGraph(GraphDataStructure gds, long timestampInit, long maximumTimestamp, String dir, String filename,
			int batchLengthSeconds, int edgeLifeTimeSeconds, boolean removeZeroDegreeNodes,
			boolean removeZeroWeightEdges, int dataOffsetSeconds, NetflowEventField[][] edges,
			NetflowDirection[] edgeDirections, EdgeWeightValue[] edgeWeights, NodeWeightValue[] nodeWeights)
			throws IOException, ParseException {
		super("NetflowGraph", gds, timestampInit);

		this.batchLengthSeconds = batchLengthSeconds;
		this.edgeLifeTimeSeconds = edgeLifeTimeSeconds;
		this.edges = edges;
		this.edgeDirections = edgeDirections;
		this.edgeWeights = edgeWeights;
		this.nodeWeights = nodeWeights;

		this.initDateTime = new DateTime(TimeUnit.SECONDS.toMillis(this.timestampInit));
		this.firstDateTime = new DateTime(TimeUnit.SECONDS.toMillis(this.timestampInit - edgeLifeTimeSeconds));

		this.reader = new DarpaNetflowReader(dir, filename);
		this.reader.setBatchIntervalSeconds(batchLengthSeconds);
		this.reader.setEdgeLifeTimeSeconds(edgeLifeTimeSeconds);
		this.reader.setRemoveZeroDegreeNodes(removeZeroDegreeNodes);
		this.reader.setRemoveZeroWeightEdges(removeZeroWeightEdges);
		this.reader.setDataOffset(this.reader.getDataOffset() + dataOffsetSeconds);
		this.reader.setMinimumTimestamp(this.firstDateTime);
		this.reader.setMaximumTimestamp(new DateTime(TimeUnit.SECONDS.toMillis(maximumTimestamp)));

		this.batchGenerator = new NetflowBatch("temp", reader, edges, edgeDirections, edgeWeights, nodeWeights);

		this.graph = generateGraph();
	}

	private Graph generateGraph() {
		Graph g = this.newGraphInstance();
		g.setTimestamp(TimeUnit.MILLISECONDS.toSeconds(this.firstDateTime.getMillis()));

		DateTime simTime = this.firstDateTime;

		while (simTime.isBefore(this.initDateTime)) {
			ArrayList<NetworkEvent> events = this.reader.getEventsUntil(simTime);
			if (events.size() > 0) {
				Batch b = this.batchGenerator.craftBatch(g, simTime, events, new ArrayList<UpdateEvent>(0), null);
				b.apply(g);
			}
			simTime = simTime.plusSeconds(this.batchLengthSeconds);
		}

		ArrayList<NetworkEvent> events = this.reader.getEventsUntil(this.initDateTime);
		if (events.size() > 0) {
			Batch b = this.batchGenerator.craftBatch(g, this.initDateTime, events, new ArrayList<UpdateEvent>(0), null);
			b.apply(g);
		}

		g.setTimestamp(TimeUnit.MILLISECONDS.toSeconds(this.initDateTime.getMillis()));

		
		// return graph
		return g;
	}

	@Override
	public Graph generate() {
		return this.graph;
	}

	public NetflowEventReader getReader() {
		return this.reader;
	}

	public NetflowBatch getBatchGenerator() {
		return this.batchGenerator;
	}
}
