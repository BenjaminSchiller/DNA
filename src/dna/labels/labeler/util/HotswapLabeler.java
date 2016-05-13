package dna.labels.labeler.util;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DArrayList;
import dna.graph.datastructures.DHashArrayList;
import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.DHashSet;
import dna.graph.datastructures.DHashTable;
import dna.graph.datastructures.DLinkedList;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.hotswap.Hotswap;
import dna.graph.generators.GraphGenerator;
import dna.labels.Label;
import dna.labels.labeler.Labeler;
import dna.metrics.IMetric;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public class HotswapLabeler extends Labeler {

	public static Label V = null;
	public static Label E = null;
	public static Label adj = null;

	public static void setV(Class<? extends IDataStructure> ds) {
		V = new Label("Hotswap", "V", getValue(ds));
	}

	public static void setE(Class<? extends IDataStructure> ds) {
		E = new Label("Hotswap", "E", getValue(ds));
	}

	public static void setAdj(Class<? extends IDataStructure> ds) {
		adj = new Label("Hotswap", "adj", getValue(ds));
	}

	public HotswapLabeler() {
		super("HotswapLabeler");
	}

	protected static String getValue(Class<? extends IDataStructure> ds) {
		if (DArray.class.equals(ds)) {
			return "A";
		} else if (DArrayList.class.equals(ds)) {
			return "AL";
		} else if (DHashSet.class.equals(ds)) {
			return "HS";
		} else if (DHashMap.class.equals(ds)) {
			return "HM";
		} else if (DHashTable.class.equals(ds)) {
			return "HT";
		} else if (DHashArrayList.class.equals(ds)) {
			return "HAL";
		} else if (DLinkedList.class.equals(ds)) {
			return "LL";
		}
		return null;
	}

	@Override
	public boolean isApplicable(GraphGenerator gg, BatchGenerator bg,
			IMetric[] metrics) {
		return Hotswap.isEnabled();
	}

	@Override
	public ArrayList<Label> computeLabels(Graph g, Batch batch,
			BatchData batchData, IMetric[] metrics) {
		ArrayList<Label> list = new ArrayList<Label>();
		if (V != null) {
			list.add(V);
			V = null;
		}
		if (E != null) {
			list.add(E);
			E = null;
		}
		if (adj != null) {
			list.add(adj);
			adj = null;
		}
		return list;
	}

}
