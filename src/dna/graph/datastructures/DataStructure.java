package dna.graph.datastructures;

import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

/**
 * Base class for storing IElements
 * 
 * @author Nico
 * 
 */
public abstract class DataStructure implements IDataStructure {
	public enum Base {
		Degree, NodeSize, EdgeSize;

		@Override
		public String toString() {
			switch (this) {
			case Degree:
				return "d";
			case EdgeSize:
				return "E";
			case NodeSize:
				return "N";
			default:
				return "";
			}
		}
	}

	public enum AccessType {
		Init(true), Add(true), ContainsSuccess(false), ContainsFailure(false), GetSuccess(
				false), GetFailure(false), Random(false), RemoveSuccess(true), RemoveFailure(
				true), Size(false), Iterator(false);

		private boolean isAllowedOnEmpty = false;

		private AccessType(boolean allowedOnEmpty) {
			this.isAllowedOnEmpty = allowedOnEmpty;
		}

		public boolean isAllowedOnEmpty() {
			return this.isAllowedOnEmpty;
		}
	}

	public enum ListType {
		GlobalNodeList(Node.class, Base.NodeSize, null), GlobalEdgeList(
				Edge.class, Base.EdgeSize, null), LocalNodeList(Node.class,
				Base.Degree, GlobalNodeList), LocalEdgeList(Edge.class,
				Base.Degree, GlobalEdgeList), LocalInEdgeList(Edge.class,
				Base.Degree, LocalEdgeList), LocalOutEdgeList(Edge.class,
				Base.Degree, LocalEdgeList);

		private Class<? extends IElement> storedSuperClass;
		private Base listBase;
		private ListType fallbackListType;

		private ListType(Class<? extends IElement> superClass, Base base,
				ListType fallback) {
			this.storedSuperClass = superClass;
			this.listBase = base;
			this.fallbackListType = fallback;
		}

		public Class<? extends IElement> getStoredClass() {
			return this.storedSuperClass;
		}

		public Base getBase() {
			return this.listBase;
		}

		public ListType getFallback() {
			return this.fallbackListType;
		}

		public static boolean hasValue(String s) {
			for (ListType l : values()) {
				if (s.equals(l.toString()))
					return true;
			}
			return false;
		}

		public Class<?> getRequiredType() {
			if (this.storedSuperClass == Node.class) {
				return INodeListDatastructure.class;
			} else if (this.storedSuperClass == Edge.class) {
				return IEdgeListDatastructure.class;
			}
			return null;
		}
	}

	protected final Class<? extends IElement> dataType;
	public final ListType listType;
	protected final int defaultSize = 10;

	private static boolean overrideContainsCheck = false;

	public DataStructure(ListType lt, Class<? extends IElement> dT) {
		this.listType = lt;
		dataType = dT;
		this.init(dT, defaultSize, true);
	}

	public void reinitializeWithSize(int reinitSize) {
		if (reinitSize < 1) {
			reinitSize = 1;
		}
		this.init(this.dataType, reinitSize, false);
	}

	public final boolean add(Node n) {
		canAdd(n);
		if (!overrideContainsCheck && this.contains(n))
			return false;
		return this.add_(n);
	}

	protected abstract boolean add_(Node n);

	public final boolean add(Edge e) {
		canAdd(e);
		if (!overrideContainsCheck && this.contains(e))
			return false;
		return this.add_(e);
	}

	protected abstract boolean add_(Edge e);

	public boolean canAdd(IElement element) {
		if (!dataType.isInstance(element))
			throw new RuntimeException("Datatype to be stored here: "
					+ dataType.getName() + ", datatype tried to be stored: "
					+ element.getClass().getName());
		return true;
	}

	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof DataStructure)) {
			return false;
		}

		DataStructure thatCasted = (DataStructure) that;
		if (this.size() != thatCasted.size())
			return false;
		if (this.size() == 0)
			return true;

		if (this instanceof IReadable) {
			return ((IReadable) this).dataEquals((IReadable) thatCasted);
		} else {
			return true;
		}
	}

	public int getDefaultSize() {
		return this.defaultSize;
	}

	public Class<? extends IElement> getDataType() {
		return this.dataType;
	}

	@Override
	public boolean canStore(Class<? extends IElement> o) {
		return dataType.isAssignableFrom(o);
	}

	protected abstract Iterator<IElement> iterator_();

	public Iterator<IElement> iterator() {
		return this.iterator_();
	}

	public static void disableContainsOnAddition() {
		overrideContainsCheck = true;
	}

	public static void enableContainsOnAddition() {
		overrideContainsCheck = false;
	}
}
