package dna.graph.datastructures.cost;

import java.util.HashMap;

import dna.graph.datastructures.IDataStructure;

public class CostFunctionsSMap {
	public HashMap<Class<? extends IDataStructure>, CostFunctionsS> map;

	public CostFunctionsSMap(CostFunctionsS... c_) {
		this.map = new HashMap<Class<? extends IDataStructure>, CostFunctionsS>(
				c_.length);
		for (CostFunctionsS c : c_) {
			this.map.put(c.ds, c);
		}
	}

	public void add(CostFunctionsS c) {
		this.map.put(c.ds, c);
	}

	public CostFunctionsS get(Class<? extends IDataStructure> ds) {
		return this.map.get(ds);
	}
}
