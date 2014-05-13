package dna.graph.datastructures;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import dna.graph.IElement;

public class DMultimapArrayList extends DMultimap {

	public DMultimapArrayList(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	@Override
	protected Multimap<Integer, IElement> getInitialList(int initialSize,
			boolean firstTime) {
		return ArrayListMultimap.create();
	}

}
