package dna.graph.datastructures.count;

import dna.graph.datastructures.DataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.IReadable;

public aspect CountingOperationsAspects {
	
	pointcut size(DataStructure list) : 
		if(Counting.isEnabled()) &&
		call(* IDataStructure+.size()) &&
		target(list);

	after(DataStructure list) : size(list) {
		switch (list.listType) {
		case GlobalEdgeList:
			Counting.oc.E.SIZE++;
			break;
		case GlobalNodeList:
			Counting.oc.V.SIZE++;
			break;
		case LocalEdgeList:
			((OperationCountsUndirected) Counting.oc).adj.SIZE++;
			break;
		case LocalInEdgeList:
			((OperationCountsDirected) Counting.oc).in.SIZE++;
			break;
		case LocalNodeList:
			((OperationCountsDirected) Counting.oc).neighbors.SIZE++;
			break;
		case LocalOutEdgeList:
			((OperationCountsDirected) Counting.oc).out.SIZE++;
			break;
		default:
			System.out.println("UNKNOWN SIZE");
			break;
		}
	}

	pointcut add(DataStructure list) : 
		if(Counting.isEnabled()) &&
		call(* IDataStructure+.add(..)) &&
		target(list);

	boolean around(DataStructure list) : add(list) {
		boolean res = proceed(list);

		if (res) {
			if (list.listType.equals(ListType.GlobalEdgeList)) {
				Counting.oc.E.ADD_SUCCESS++;
			} else if (list.listType.equals(ListType.GlobalNodeList)) {
				Counting.oc.V.ADD_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalEdgeList)) {
				((OperationCountsUndirected) Counting.oc).adj.ADD_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalInEdgeList)) {
				((OperationCountsDirected) Counting.oc).in.ADD_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalNodeList)) {
				((OperationCountsDirected) Counting.oc).neighbors.ADD_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalOutEdgeList)) {
				((OperationCountsDirected) Counting.oc).out.ADD_SUCCESS++;
			} else {
				System.out.println("UNKNOWN ADD_SUCCESS");
			}
		} else {
			if (list.listType.equals(ListType.GlobalEdgeList)) {
				Counting.oc.E.ADD_FAILURE++;
			} else if (list.listType.equals(ListType.GlobalNodeList)) {
				Counting.oc.V.ADD_FAILURE++;
			} else if (list.listType.equals(ListType.LocalEdgeList)) {
				((OperationCountsUndirected) Counting.oc).adj.ADD_FAILURE++;
			} else if (list.listType.equals(ListType.LocalInEdgeList)) {
				((OperationCountsDirected) Counting.oc).in.ADD_FAILURE++;
			} else if (list.listType.equals(ListType.LocalNodeList)) {
				((OperationCountsDirected) Counting.oc).neighbors.ADD_FAILURE++;
			} else if (list.listType.equals(ListType.LocalOutEdgeList)) {
				((OperationCountsDirected) Counting.oc).out.ADD_FAILURE++;
			} else {
				System.out.println("UNKNOWN ADD_FAILURE");
			}
		}

		return res;
	}

	pointcut init(DataStructure list) : 
		if(Counting.isEnabled()) &&
		call(* IDataStructure+.init(..)) &&
		target(list);

	after(DataStructure list) : init(list) {
		if (Counting.oc == null) {
			return;
		}
		switch (list.listType) {
		case GlobalEdgeList:
			Counting.oc.E.INIT++;
			break;
		case GlobalNodeList:
			Counting.oc.V.INIT++;
			break;
		case LocalEdgeList:
			((OperationCountsUndirected) Counting.oc).adj.INIT++;
			break;
		case LocalInEdgeList:
			((OperationCountsDirected) Counting.oc).in.INIT++;
			break;
		case LocalNodeList:
			((OperationCountsDirected) Counting.oc).neighbors.INIT++;
			break;
		case LocalOutEdgeList:
			((OperationCountsDirected) Counting.oc).out.INIT++;
			break;
		default:
			System.out.println("UNKNOWN INIT");
			break;
		}
	}

	pointcut iterator(DataStructure list) : 
		if(Counting.isEnabled()) &&
		call(* DataStructure+.iterator_()) &&
		target(list);

	after(DataStructure list) : iterator(list) {
		switch (list.listType) {
		case GlobalEdgeList:
			Counting.oc.E.ITERATE++;
			break;
		case GlobalNodeList:
			Counting.oc.V.ITERATE++;
			break;
		case LocalEdgeList:
			((OperationCountsUndirected) Counting.oc).adj.ITERATE++;
			break;
		case LocalInEdgeList:
			((OperationCountsDirected) Counting.oc).in.ITERATE++;
			break;
		case LocalNodeList:
			((OperationCountsDirected) Counting.oc).neighbors.ITERATE++;
			break;
		case LocalOutEdgeList:
			((OperationCountsDirected) Counting.oc).out.ITERATE++;
			break;
		default:
			System.out.println("UNKNOWN ITERATE");
			break;
		}
	}

	pointcut randomElement(DataStructure list) : 
		if(Counting.isEnabled()) &&
		call(* IReadable+.getRandom(..)) &&
		target(list);

	after(DataStructure list) : randomElement(list) {
		switch (list.listType) {
		case GlobalEdgeList:
			Counting.oc.E.RANDOM_ELEMENT++;
			break;
		case GlobalNodeList:
			Counting.oc.V.RANDOM_ELEMENT++;
			break;
		case LocalEdgeList:
			((OperationCountsUndirected) Counting.oc).adj.RANDOM_ELEMENT++;
			break;
		case LocalInEdgeList:
			((OperationCountsDirected) Counting.oc).in.RANDOM_ELEMENT++;
			break;
		case LocalNodeList:
			((OperationCountsDirected) Counting.oc).neighbors.RANDOM_ELEMENT++;
			break;
		case LocalOutEdgeList:
			((OperationCountsDirected) Counting.oc).out.RANDOM_ELEMENT++;
			break;
		default:
			System.out.println("UNKNOWN RANDOM_ELEMENT");
			break;
		}
	}

	pointcut contains(DataStructure list) : 
		if(Counting.isEnabled()) &&
		call(* IDataStructure	+.contains(..)) &&
		target(list);

	boolean around(DataStructure list) : contains(list) {
		boolean res = proceed(list);

		if (res) {
			if (list.listType.equals(ListType.GlobalEdgeList)) {
				Counting.oc.E.CONTAINS_SUCCESS++;
			} else if (list.listType.equals(ListType.GlobalNodeList)) {
				Counting.oc.V.CONTAINS_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalEdgeList)) {
				((OperationCountsUndirected) Counting.oc).adj.CONTAINS_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalInEdgeList)) {
				((OperationCountsDirected) Counting.oc).in.CONTAINS_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalNodeList)) {
				((OperationCountsDirected) Counting.oc).neighbors.CONTAINS_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalOutEdgeList)) {
				((OperationCountsDirected) Counting.oc).out.CONTAINS_SUCCESS++;
			} else {
				System.out.println("UNKNOWN CONTAINS_SUCCESS");
			}
		} else {
			if (list.listType.equals(ListType.GlobalEdgeList)) {
				Counting.oc.E.CONTAINS_FAILURE++;
			} else if (list.listType.equals(ListType.GlobalNodeList)) {
				Counting.oc.V.CONTAINS_FAILURE++;
			} else if (list.listType.equals(ListType.LocalEdgeList)) {
				((OperationCountsUndirected) Counting.oc).adj.CONTAINS_FAILURE++;
			} else if (list.listType.equals(ListType.LocalInEdgeList)) {
				((OperationCountsDirected) Counting.oc).in.CONTAINS_FAILURE++;
			} else if (list.listType.equals(ListType.LocalNodeList)) {
				((OperationCountsDirected) Counting.oc).neighbors.CONTAINS_FAILURE++;
			} else if (list.listType.equals(ListType.LocalOutEdgeList)) {
				((OperationCountsDirected) Counting.oc).out.CONTAINS_FAILURE++;
			} else {
				System.out.println("UNKNOWN CONTAINS_FAILURE");
			}
		}

		return res;
	}

	pointcut getX(DataStructure list) : 
		if(Counting.isEnabled()) &&
		call(* IDataStructure	+.get(..)) &&
		target(list);

	Object around(DataStructure list) : getX(list) {
		Object res = proceed(list);

		if (res != null) {
			if (list.listType.equals(ListType.GlobalEdgeList)) {
				Counting.oc.E.GET_SUCCESS++;
			} else if (list.listType.equals(ListType.GlobalNodeList)) {
				Counting.oc.V.GET_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalEdgeList)) {
				((OperationCountsUndirected) Counting.oc).adj.GET_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalInEdgeList)) {
				((OperationCountsDirected) Counting.oc).in.GET_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalNodeList)) {
				((OperationCountsDirected) Counting.oc).neighbors.GET_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalOutEdgeList)) {
				((OperationCountsDirected) Counting.oc).out.GET_SUCCESS++;
			} else {
				System.out.println("UNKNOWN GET_SUCCESS");
			}
		} else {
			if (list.listType.equals(ListType.GlobalEdgeList)) {
				Counting.oc.E.GET_FAILURE++;
			} else if (list.listType.equals(ListType.GlobalNodeList)) {
				Counting.oc.V.GET_FAILURE++;
			} else if (list.listType.equals(ListType.LocalEdgeList)) {
				((OperationCountsUndirected) Counting.oc).adj.GET_FAILURE++;
			} else if (list.listType.equals(ListType.LocalInEdgeList)) {
				((OperationCountsDirected) Counting.oc).in.GET_FAILURE++;
			} else if (list.listType.equals(ListType.LocalNodeList)) {
				((OperationCountsDirected) Counting.oc).neighbors.GET_FAILURE++;
			} else if (list.listType.equals(ListType.LocalOutEdgeList)) {
				((OperationCountsDirected) Counting.oc).out.GET_FAILURE++;
			} else {
				System.out.println("UNKNOWN GET_FAILURE");
			}
		}

		return res;
	}

	pointcut remove(DataStructure list) : 
		if(Counting.isEnabled()) &&
		call(* IDataStructure	+.remove(..)) &&
		target(list);

	boolean around(DataStructure list) : remove(list) {
		boolean res = proceed(list);

		if (res) {
			if (list.listType.equals(ListType.GlobalEdgeList)) {
				Counting.oc.E.REMOVE_SUCCESS++;
			} else if (list.listType.equals(ListType.GlobalNodeList)) {
				Counting.oc.V.REMOVE_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalEdgeList)) {
				((OperationCountsUndirected) Counting.oc).adj.REMOVE_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalInEdgeList)) {
				((OperationCountsDirected) Counting.oc).in.REMOVE_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalNodeList)) {
				((OperationCountsDirected) Counting.oc).neighbors.REMOVE_SUCCESS++;
			} else if (list.listType.equals(ListType.LocalOutEdgeList)) {
				((OperationCountsDirected) Counting.oc).out.REMOVE_SUCCESS++;
			} else {
				System.out.println("UNKNOWN REMOVE_SUCCESS");
			}
		} else {
			if (list.listType.equals(ListType.GlobalEdgeList)) {
				Counting.oc.E.REMOVE_FAILURE++;
			} else if (list.listType.equals(ListType.GlobalNodeList)) {
				Counting.oc.V.REMOVE_FAILURE++;
			} else if (list.listType.equals(ListType.LocalEdgeList)) {
				((OperationCountsUndirected) Counting.oc).adj.REMOVE_FAILURE++;
			} else if (list.listType.equals(ListType.LocalInEdgeList)) {
				((OperationCountsDirected) Counting.oc).in.REMOVE_FAILURE++;
			} else if (list.listType.equals(ListType.LocalNodeList)) {
				((OperationCountsDirected) Counting.oc).neighbors.REMOVE_FAILURE++;
			} else if (list.listType.equals(ListType.LocalOutEdgeList)) {
				((OperationCountsDirected) Counting.oc).out.REMOVE_FAILURE++;
			} else {
				System.out.println("UNKNOWN REMOVE_FAILURE");
			}
		}

		return res;
	}
}
