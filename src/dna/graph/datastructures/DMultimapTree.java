package dna.graph.datastructures;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import dna.graph.IElement;

public class DMultimapTree extends DMultimap {

	public DMultimapTree(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	@Override
	protected Multimap<Integer, IElement> getInitialList(int initialSize,
			boolean firstTime) {
		return TreeMultimap.create();
	}

}
