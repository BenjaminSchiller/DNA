package dna.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import dna.datastructures.GraphDataStructure;
import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.EdgeWeightUpdate;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.NodeWeightUpdate;
import dna.updates.Update.UpdateType;
import dna.util.Config;
import dna.util.Log;

public class BatchReader<N extends Node, E extends Edge> {

	private GraphDataStructure ds;

	public BatchReader(GraphDataStructure ds) {
		this.ds = ds;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Batch read(String dir, String filename, Graph g) {
		Reader reader = null;
		// TODO add from/to for batch to IO
		Batch b = new Batch(this.ds, 0, 0);
		try {
			reader = new Reader(dir, filename);

			String line = null;
			while ((line = reader.readString()) != null) {
				String[] temp = line.split(Config.get("UPDATE_DELIMITER1"));
				System.out.println(line + " => " + temp[0] + " / " + temp[1]);
				switch (UpdateType.valueOf(temp[0])) {
				case EdgeAddition:
					b.add(new EdgeAddition<E>((E) ds.newEdgeInstance(temp[1], g)));
					break;
				case EdgeRemoval:
					b.add(new EdgeRemoval<E>((E) g.getEdge(ds.newEdgeInstance(
							temp[1], g))));
					break;
				case EdgeWeightUpdate:
					String[] temp1 = temp[1].split(Config
							.get("UPDATE_DELIMITER2"));
					b.add(new EdgeWeightUpdate<E>((E) g.getEdge(ds.newEdgeInstance(
							temp1[0], g)), Double.parseDouble(temp1[1])));
					break;
				case NodeAddition:
					b.add(new NodeAddition<E>(ds.newNodeInstance(Integer
							.parseInt(temp[1]))));
					break;
				case NodeRemoval:
					b.add(new NodeRemoval<E>(g.getNode(Integer
							.parseInt(temp[1]))));
					break;
				case NodeWeithUpdate:
					String[] temp2 = temp[1].split(Config
							.get("UPDATE_DELIMITER2"));
					b.add(new NodeWeightUpdate<E>(g.getNode(Integer
							.parseInt(temp2[0])), Double.parseDouble(temp2[1])));
					break;
				default:
					Log.error("unknown update type: " + temp[0]);
				}

			}
			b.print();
			return b;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
