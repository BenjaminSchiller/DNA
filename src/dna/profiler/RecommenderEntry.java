package dna.profiler;

import java.util.EnumMap;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.profiler.datatypes.ComparableEntryMap;

public class RecommenderEntry implements Comparable<RecommenderEntry> {
	private ComparableEntryMap costs;
	private EnumMap<ListType, Class<? extends IDataStructure>> datastructures;
	
	public RecommenderEntry(ComparableEntryMap costs, EnumMap<ListType, Class<? extends IDataStructure>> ds) {
		this.costs = costs;
		this.datastructures = ds;
	}
	
	public ComparableEntryMap getCosts() {
		return costs;
	}
	
	public Class<? extends IDataStructure> getDatastructure(ListType lt) {
		return datastructures.get(lt);
	}
	
	public EnumMap<ListType, Class<? extends IDataStructure>> getDatastructures() {
		return datastructures;
	}
	
	public GraphDataStructure getGraphDataStructure() {
		return new GraphDataStructure(datastructures, null, null);
	}

	@Override
	public int compareTo(RecommenderEntry o) {
		return costs.compareTo(o.costs);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datastructures == null) ? 0 : datastructures.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RecommenderEntry other = (RecommenderEntry) obj;
		if (datastructures == null) {
			if (other.datastructures != null) {
				return false;
			}
		} else if (!datastructures.equals(other.datastructures)) {
			return false;
		}
		return true;
	}

}
