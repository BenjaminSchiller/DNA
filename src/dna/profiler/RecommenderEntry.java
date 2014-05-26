package dna.profiler;

import java.util.Comparator;
import java.util.EnumMap;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;
import dna.profiler.datatypes.ComparableEntryMap;

public class RecommenderEntry implements Cloneable {
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

	public void resetCosts() {
		for (ProfilerDataType pdt : ProfilerDataType.values()) {
			ComparableEntryMap costs = ProfilerMeasurementData.getMap(pdt);
			this.setCosts(pdt, costs);
		}
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

	/**
	 * Compare this RecommenderEntry to another one, based on the costs for a
	 * specific ProfilerDataType. If the costs are equal, the comparision is
	 * done based on the used data structures. If they are also equal, both
	 * RecommenderEntries must be equal.
	 * 
	 * If the data structures would not be taken into account, this could cause
	 * problems for insertions into a TreeSet/Map, as this only checks through
	 * this comparison where to insert a new entry. If this method returns 0,
	 * the current entry in the tree would get overwritten, even if the data
	 * structures are different.
	 * 
	 * @param o
	 * @param pdt
	 * @return
	 */
	public int compareToOther(RecommenderEntry o, ProfilerDataType pdt) {
		ComparableEntryMap myCosts = costs.get(pdt);
		ComparableEntryMap otherCosts = o.getCosts(pdt);
		int res = myCosts.compareTo(otherCosts);
		if (res == 0) {
			if (getDatastructures().equals(o.getDatastructures())) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return res;
		}
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

	@Override
	public RecommenderEntry clone() {
		RecommenderEntry res = new RecommenderEntry(datastructures.clone());
		for (ProfilerDataType pdt : ProfilerDataType.values()) {
			res.setCosts(pdt, this.getCosts(pdt));
		}
		return res;
	}

}
