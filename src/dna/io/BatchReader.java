package dna.io;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
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
import dna.util.MathHelper;

/**
 * A batch reader to read in a written batch
 * 
 * @author Nico
 * 
 * @param <N>
 *            Node type to be read in
 * @param <E>
 *            Edge type to be read in
 * @param <T>
 *            Weight type to be read in (can be of type double, int,...) -- use
 *            ? to ignore
 */
public class BatchReader<N extends Node, E extends Edge, T> {

	private GraphDataStructure ds;

	public BatchReader(GraphDataStructure ds) {
		this.ds = ds;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Batch read(String dir, String filename, Graph g) {
		Reader reader = null;
		// TODO add from/to for batch to IO

		ByteArrayInputStream byteInputStream;
		ObjectInputStream objectInputStream;
		T deserializedWeight;

		Batch b = new Batch(this.ds, 0, 0);
		try {
			reader = new Reader(dir, filename);

			String line = null;
			while ((line = reader.readString()) != null) {
				String[] temp = line.split(Config.get("UPDATE_DELIMITER1"));
				System.out.println(line + " => " + temp[0] + " / " + temp[1]);
				switch (UpdateType.valueOf(temp[0])) {
				case EdgeAddition:
					b.add(new EdgeAddition<E>((E) ds
							.newEdgeInstance(temp[1], g)));
					break;
				case EdgeRemoval:
					b.add(new EdgeRemoval<E>((E) g.getEdge(ds.newEdgeInstance(
							temp[1], g))));
					break;
				case EdgeWeightUpdate:
					String[] temp1 = temp[1].split(Config
							.get("UPDATE_DELIMITER2"));

					// Parse second element correctly
					byteInputStream = new ByteArrayInputStream(
							temp1[1].getBytes());
					objectInputStream = new ObjectInputStream(byteInputStream);
					deserializedWeight = (T) objectInputStream.readObject();

					b.add(new EdgeWeightUpdate<E, T>((E) g.getEdge(ds
							.newEdgeInstance(temp1[0], g)), deserializedWeight));
					break;
				case NodeAddition:
					b.add(new NodeAddition<E>(ds.newNodeInstance(MathHelper
							.parseInt(temp[1]))));
					break;
				case NodeRemoval:
					b.add(new NodeRemoval<E>(g.getNode(MathHelper
							.parseInt(temp[1]))));
					break;
				case NodeWeithUpdate:
					String[] temp2 = temp[1].split(Config
							.get("UPDATE_DELIMITER2"));

					// Parse second element correctly
					byteInputStream = new ByteArrayInputStream(
							temp2[1].getBytes());
					objectInputStream = new ObjectInputStream(byteInputStream);
					deserializedWeight = (T) objectInputStream.readObject();

					b.add(new NodeWeightUpdate<E, T>(g.getNode(MathHelper
							.parseInt(temp2[0])), deserializedWeight));
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
		} catch (ClassNotFoundException e) {
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
