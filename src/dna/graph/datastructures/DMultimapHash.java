package dna.graph.datastructures;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dna.graph.IElement;

public class DMultimapHash extends DMultimap {

	public DMultimapHash(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	@Override
	protected Multimap<Integer, IElement> getInitialList(int initialSize,
			boolean firstTime) {
		return HashMultimap.create();
	}

}
