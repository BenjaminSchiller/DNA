package dna.graph.datastructures;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import dna.graph.IElement;

public class DMultimapLinkedHash extends DMultimap {

	public DMultimapLinkedHash(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	@Override
	protected Multimap<Integer, IElement> getInitialList(int initialSize,
			boolean firstTime) {
		return LinkedHashMultimap.create();
	}

}
