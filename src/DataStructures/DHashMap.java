package DataStructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import Utils.Rand;
import Graph.IElement;
import Graph.Nodes.Node;

public class DHashMap extends DataStructureReadable implements INodeListDatastructureReadable {
	private HashMap<Integer, IElement> list;
	private int maxNodeIndex;
	
	public DHashMap(Class<? extends IElement> dT) {
		this.init(dT, defaultSize);
	}
	
	public void init(Class<? extends IElement> dT, int initialSize) {
		this.dataType = dT;
		this.list = new HashMap<>(initialSize);
		this.maxNodeIndex = -1;
	}
	
	public boolean add(IElement element) {
		if (element instanceof Node) return this.add((Node) element);
		throw new RuntimeException("Can't handle element of type " + element.getClass() + " here");
	}
	
	public boolean add(Node element) {
		super.canAdd(element);
		
		if (!this.list.containsKey(element.getIndex())) {
			this.list.put(element.getIndex(), element);
			if (element.getIndex() > this.maxNodeIndex) {
				this.maxNodeIndex = element.getIndex();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean contains(IElement element) {
		if (element instanceof Node) return this.contains((Node) element);
		throw new RuntimeException("Can't handle element of type " + element.getClass() + " here");
	}	
	
	@Override
	public boolean contains(Node element) {
		return list.containsValue(element);
	}

	@Override
	public int size() {
		return list.size();
	}
	
	public Node get(Node e) {
		return (Node) this.list.get(e);
	}
	
	@Override
	public boolean remove(IElement element) {
		if ( element instanceof Node ) return this.remove((Node) element);
		throw new RuntimeException("Can't handle element of type " + element.getClass() + " here");
	}

	@Override
	public IElement getRandom() {
		int index = Rand.rand.nextInt(this.list.size());
		int counter = 0;
		for (IElement node : this.list.values()) {
			if (counter == index) {
				return (Node) node;
			}
			counter++;
		}
		return null;
	}

	@Override
	public Collection<IElement> getElements() {
		return this.list.values();
	}

	@Override
	public Iterator iterator() {
		return this.list.values().iterator();
	}

	@Override
	public Node get(int element) {
		return (Node) this.list.get(element);
	}

	@Override
	public boolean remove(Node element) {
		if (this.list.remove(element.getIndex()) == null) {
			return false;
		}
		if (element.getIndex() == this.maxNodeIndex) {
			int max = this.maxNodeIndex - 1;
			while (!this.list.containsKey(max) && max >= 0) {
				max--;
			}
			this.maxNodeIndex = max;
		}
		return true;
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}
}
