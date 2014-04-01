package dna.profiler;

import java.util.Comparator;
import java.util.EnumMap;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;
import dna.profiler.datatypes.ComparableEntryMap;

public class RecommenderEntry {
	private EnumMap<ProfilerDataType, ComparableEntryMap> costs;
	private EnumMap<ListType, Class<? extends IDataStructure>> datastructures;

	public RecommenderEntry(
			EnumMap<ListType, Class<? extends IDataStructure>> ds) {
		this.costs = new EnumMap<ProfilerDataType, ComparableEntryMap>(
				ProfilerDataType.class);
		this.datastructures = ds;
	}

	public void setCosts(ProfilerDataType pdt, ComparableEntryMap newCosts) {
		costs.put(pdt, newCosts);
	}

	public ComparableEntryMap getCosts(ProfilerDataType pdt) {
		return costs.get(pdt);
	}

	public Class<? extends IDataStructure> getDatastructure(ListType lt) {
		return datastructures.get(lt);
	}

	public EnumMap<ListType, Class<? extends IDataStructure>> getDatastructures() {
		EnumMap<ListType, Class<? extends IDataStructure>> res = GraphDataStructure
				.fillUpWithFallback(datastructures);
		return res;
	}

	public GraphDataStructure getGraphDataStructure() {
		return new GraphDataStructure(datastructures, null, null);
	}

	public int compareToOther(RecommenderEntry o, ProfilerDataType pdt) {
		ComparableEntryMap myCosts = costs.get(pdt);
		ComparableEntryMap otherCosts = o.getCosts(pdt);
		return myCosts.compareTo(otherCosts);
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

	public static Comparator<? super RecommenderEntry> getComparator(
			ProfilerDataType entryType) {
		return new RecommenderEntryComparator(entryType);
	}

}
