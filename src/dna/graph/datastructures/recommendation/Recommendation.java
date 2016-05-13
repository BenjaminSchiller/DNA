package dna.graph.datastructures.recommendation;

import java.util.ArrayList;
import java.util.Collections;

import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.config.DSConfig;
import dna.graph.datastructures.config.DSConfigDirected;
import dna.graph.datastructures.config.DSConfigUndirected;
import dna.graph.datastructures.cost.CostFunctions;
import dna.graph.datastructures.cost.CostFunctionsS;
import dna.graph.datastructures.cost.CostFunctionsSMap;
import dna.graph.datastructures.count.OperationCount;
import dna.graph.datastructures.count.OperationCounts;
import dna.graph.datastructures.count.OperationCountsDirected;
import dna.graph.datastructures.count.OperationCountsUndirected;
import dna.util.Timer;

/**
 * 
 * generates recommendations for data structures based on an OperationCount as
 * well as recommendations for configurations based on OperationCounts
 * 
 * for recommendations, the datastructure or a config with the fastest estimated
 * datastructure(s) is computed.
 * 
 * two inputs for the recommendation are possible: static or dynamic. for the
 * static case, the input is only OperationCount or OperationCounts. for the
 * dynamic case, the input is assumed to originate from the last k batches. as
 * additional parameter, this number of batches is expected as well as the
 * current datastructure(s) as well as the number of batches after which a
 * change should amortize.
 * 
 * 
 * @author benni
 *
 */
public class Recommendation {
	public CostFunctionsSMap map;
	private ArrayList<CostFunctionsS> list;
	private ArrayList<CostFunctions> listNodes;
	private ArrayList<CostFunctions> listEdges;

	public Recommendation(CostFunctionsSMap map) {
		this.map = map;
		this.list = new ArrayList<CostFunctionsS>(map.map.size());
		this.listNodes = new ArrayList<CostFunctions>(map.map.size());
		this.listEdges = new ArrayList<CostFunctions>(map.map.size());
		for (CostFunctionsS cf : map.map.values()) {
			this.list.add(cf);
			this.listNodes.add(cf.nodes);
			this.listEdges.add(cf.edges);
		}
	}

	public Class<? extends IDataStructure> recommendFastestDatastructure(
			OperationCount oc) {
		if (oc.isNodes()) {
			Collections.sort(this.listNodes, new CostFunctionsComparator(oc));
			return this.listNodes.get(0).ds;
		} else {
			Collections.sort(this.listEdges, new CostFunctionsComparator(oc));
			return this.listEdges.get(0).ds;
		}
	}

	public DSConfig recommendFastestConfig(OperationCounts ocs) {
		if (ocs instanceof OperationCountsDirected) {
			return this.recommendFastestConfig((OperationCountsDirected) ocs);
		} else {
			return this.recommendFastestConfig((OperationCountsUndirected) ocs);
		}
	}

	public DSConfigDirected recommendFastestConfig(OperationCountsDirected ocs) {
		Class<? extends IDataStructure> V, E, in, out, neighbors;
		V = this.recommendFastestDatastructure(ocs.V);
		E = this.recommendFastestDatastructure(ocs.E);
		in = this.recommendFastestDatastructure(ocs.in);
		out = this.recommendFastestDatastructure(ocs.out);
		neighbors = this.recommendFastestDatastructure(ocs.neighbors);
		return new DSConfigDirected(V, E, in, out, neighbors);
	}

	public DSConfigUndirected recommendFastestConfig(
			OperationCountsUndirected ocs) {
		Class<? extends IDataStructure> V, E, adj;
		V = this.recommendFastestDatastructure(ocs.V);
		E = this.recommendFastestDatastructure(ocs.E);
		adj = this.recommendFastestDatastructure(ocs.adj);
		return new DSConfigUndirected(V, E, adj);
	}

	public Class<? extends IDataStructure> recommendFastestDatastructure(
			OperationCount oc, int batches, int amortization,
			Class<? extends IDataStructure> current) {
		if (oc.isNodes()) {
			Timer t = new Timer();
			Collections.sort(this.listNodes,
					new HotswapComparator(this.map.get(current).nodes, oc,
							batches, amortization));
			System.out.println("  nodes: " + t.end());
			return this.listNodes.get(0).ds;
		} else {
			Timer t = new Timer();
			HotswapComparator comp = new HotswapComparator(
					this.map.get(current).edges, oc, batches, amortization);
			Collections.sort(this.listEdges, comp);
			System.out.println("  edges: " + t.end() + " @ "
					+ comp.t.toString() + " @@ " + comp.count);
			return this.listEdges.get(0).ds;
		}
	}

	public DSConfig recommendFastestConfig(OperationCounts ocs, int batches,
			int amortization, DSConfig current) {
		if (ocs instanceof OperationCountsDirected) {
			return this.recommendFastestConfig((OperationCountsDirected) ocs,
					batches, amortization, (DSConfigDirected) current);
		} else {
			return this.recommendFastestConfig((OperationCountsUndirected) ocs,
					batches, amortization, (DSConfigUndirected) current);
		}
	}

	public DSConfigDirected recommendFastestConfig(OperationCountsDirected ocs,
			int batches, int amortization, DSConfigDirected current) {
		Class<? extends IDataStructure> V, E, in, out, neighbors;
		V = this.recommendFastestDatastructure(ocs.V, batches, amortization,
				current.V);
		E = this.recommendFastestDatastructure(ocs.E, batches, amortization,
				current.E);
		in = this.recommendFastestDatastructure(ocs.in, batches, amortization,
				current.in);
		out = this.recommendFastestDatastructure(ocs.out, batches,
				amortization, current.out);
		neighbors = this.recommendFastestDatastructure(ocs.neighbors, batches,
				amortization, current.neighbors);
		return new DSConfigDirected(V, E, in, out, neighbors);
	}

	public DSConfigUndirected recommendFastestConfig(
			OperationCountsUndirected ocs, int batches, int amortization,
			DSConfigUndirected current) {
		Class<? extends IDataStructure> V, E, adj;
		Timer t = new Timer();
		V = this.recommendFastestDatastructure(ocs.V, batches, amortization,
				current.V);
		E = this.recommendFastestDatastructure(ocs.E, batches, amortization,
				current.E);
		adj = this.recommendFastestDatastructure(ocs.adj, batches,
				amortization, current.adj);
		System.out.println("RECOMMENDATION: " + t.end());
		return new DSConfigUndirected(V, E, adj);
	}
}
