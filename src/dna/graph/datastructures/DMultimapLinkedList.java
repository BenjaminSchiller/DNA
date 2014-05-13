package dna.graph.datastructures;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import dna.graph.IElement;

public class DMultimapLinkedList extends DMultimap {

	public DMultimapLinkedList(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	@Override
	protected Multimap<Integer, IElement> getInitialList(int initialSize,
			boolean firstTime) {
		return LinkedListMultimap.create();
	}

}
