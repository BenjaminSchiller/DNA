DNA - Implementing Metrics and their Algorithms
=====================

Here, the basics for implementing a metric and algorithms for computing it are explained.


Definition: Metrics
---------------------
In general, a *metric* maps a graph onto a certain property, i.e., *values*, *distributions*, *node-value lists*, or *node-node-value lists*.
Such a **value** is represented by a single number (*double* in DNA) that is a global characteristics of the graph like, e.g., average node degree, transitivity, or diameter.
Distributions are frequency distributions (*double[]* in DNA) of certain properties like, e.g., the degree distribution or the hop plot (distribution of shortest path lengths).
**Node-value lists** define a separate value per node (*double[]* in DNA) like, e.g., the local clustering coefficient or the betweenness centrality or a node.
**Node-node-value lists** are very specific results that assign a separate value to each pair nodes (*double[][]* in DNA) like, e.g., various similarity measures.

In DNA, we use the term metric to describe a collection of such metrics that are closely related.
For example, the degree distribution in DNA holds the degree distribution, the maximum degree, and the average degree.
Hence, a metric provides as set of

- values (*Value[]*)
- distributions (*Distribution[]*)
- node-value lists (*NodeValueList[]*)
- node-node-value lists (*NodeNodeValueList[]*)

Note that each set can be empty in case no such result is provided by the respective metric.

Hence, in DNA, a metric is a collection of graph-theoretic measures / their results.



Definition: Algorithms
---------------------
In DNA, we distinguish two main kinds of algorithms: *dynamic algorithms* and *static algorithms*.

**Static algorithms** take as input a snapshot of a graph and compute the desired metric based only on the given graph.
For each point in time of interest, the metric is re-computed using the respective graph as input.
Hence, we refer to these algorithms as *recomputation*.

**Dynamic algorithms** start with an initial graph to compute the metric and potentially auxiliary data.
They update the metric(s) for the next point in time of interests using the current snapshot as well as the changes occurring between two points in time.
Operating on this stream of updates to the graph, these algorithms take as input a specific update to the dynamic graph as well as the graph before or after the application of that update.
In DNA, we distinguish six different updates:

- the addition of a new node (NA)
- the removal of an existing node (NR)
- the change of a node's weight (NW)
- the addition of a new edge (EA)
- the removal of an existing edge (ER)
- the change of an edge's weight (EW)

An algorithm can take as input for the update of its computed metrics each update before and/or after its application to the graph.
Hence, there exists 12 possibilities for an algorithm to update the metrics based on a single update:

- Before{NA|NR|NW|EA|ER|EW)
- After{NA|NR|NW|EA|ER|EW)

In DNA, we represent consecutive updates as batches, i.e., sets of updates that occurr one after the other.
A dynamic algorithm might also take a complete batch of updates as input instead of single updates.
Then, the algorithm can update the metric(s) before or after the application of all updates of the batch to the graph.
Hence, two further possibilities exists to update a metric using a dynamic algorithm:

- BeforeBatch
- AfterBatch

Which operations are required highly depends on the algorithm:
An algorithm that does not considers weights can ignore *EW* and *NW*.
Some algorithms only consider updates before xor after their application while other require a combination of both views on the graph.




Implementation: Metrics
---------------------
In DNA, a metric (as a collection of graph-theoretic measures) must implement the interface *dna.metric.IMetric*.
The required methods provide access to the values, distribution, node-value lists, and node-node-value lists of a metric.
In addition, methods need to be implemented that state if a metric is applicable to a given graph (some metrics are only defined for directed graphs others require weighted edges) or batch (some algorithm might not work for node removals (NR) and can overwrite this method).

The implementation of other required methods is provided by the abstract class *dna.metric.Metric* and should be extended by all metrics.




Implementation: Algorithms
---------------------
Algorithms are extensions of metrics.
While the metrics hold the basic data structures that represent the results of the graph-theoretic measures, the algorithms compute / update them based on the respective input.

Algorithms are described and identified by the interfaces in the package *dna.metrics.algorithms*.

**Static algorithms** need to implement the *IRecomputation* interface that requires the algorithm to provide a method for *recomputing* the metric(s) cased on the current graph snapshot:

`public boolean recompute();`

**Dynamic algorithms** need to implement a method for computing the initial results and auxiliary data based on the initial / current graph snapshot as desined by the interface *dna.metrics.algorithms.IDynamicAlgorithm*:

`public boolean init();`

In addition, dynamic algorithms can arbitrarily combine interfaces that describe their application before or after the application of different updates:

- *dna.metrics.algorithms.IBeforeEA* : `public boolean applyBeforeUpdate(EdgeAddition ea);`
- *dna.metrics.algorithms.IAfterEA* : `public boolean applyAfterUpdate(EdgeAddition ea);`
- *dna.metrics.algorithms.IBeforeER* : `public boolean applyBeforeUpdate(EdgeRemoval er);`
- *dna.metrics.algorithms.IAfterER* : `public boolean applyAfterUpdate(EdgeRemoval er);`
- ...

There also exist two interfaces that describe the application before / after batches:

- *dna.metrics.algorithms.IBeforeBatch* `public boolean applyBeforeBatch(Batch b);`
- *dna.metrics.algorithms.IAfterBatch* `public boolean applyAfterBatch(Batch b);`




Heuristics vs. Exact Metrics
---------------------

When implementing a metric, it must be assigned a MetricType (defined in `dna.metrics.IMetric.MetricType`) which can be either `exact` or `heuristic`.

An exact metric is assumed to compute the exect and correct values for the metric while a heuristic is assumed to apply an approximate computation approach which can result in an exact value but might not.
Assume the correct value of some property to be *12.332516*, then an exact metric is assumed to actually output *12.332516* (except for rounding-based imprecisions).
In contrast, a heuristics could also return *12*, *11.24*, or *15*, always depending on the applied heuristic and the properties of the dynamic graph that is analyzed.

When computing two exact metrics / algorithms that are comparable (they compute the same metric) at the same time (i.e., in the same series), their results are automatically compared after each batch and errors and warning are output in case their values do not match.
This is helpful when developing new algorithms, e.g., for automatically comparing the results of a well-known (assumed to be correct) snapshot-based algorithm with a newly developed stream-based algorithm.

When computing an exact and a heuristic version of the same metric, their values are still compared but no error output in case they differ as this is assumed to happen.
In that case, the quality of the heuristic is computed relative to the results of the exact algorithm.
For example: assume the correct value to be *2.0* and the approximation to be *1.8*, then the quality of that value is computed as *1.8/2.0 = 0.9* or 90%.
These qualities are stores in a separate metric of MetricType `quality` and can be plotted to show the quality of a heuristic over time.




Example
---------------------
As an example, we consider algorithms that compute the all-pairs-shortest-paths (APSP) between all node pairs of the network.
We consider dynamic as well as static algorithms that compute the APSP in unweighted as well as graph with int-weighted edges.

The measures computed by all such algorithms are the same:

- *characteristic path length* - the average length of shortest paths
- *diameter* - the maximum length of a shortest path
- *hop plot* - the frequency distribution of shortest paths

All these measures need to be computed, independent of the graph and its specific properties.
Hence, we implemented their provision and representation by a class *dna.metrics.paths.AllPairsShortestPaths*:

	@Override
	public Value[] getValues() {
		Value v1 = new Value("existingPaths", this.apsp.getDenominator());
		Value v2 = new Value("possiblePaths", this.g.getNodeCount()
				 * (this.g.getNodeCount() - 1));
		Value v3 = new Value("characteristicPathLength",
				this.apsp.computeAverage());
		Value v4 = new Value("diameter", this.apsp.getMax());

		return new Value[] { v1, v2, v3, v4 };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] { this.apsp };
	}

In addition to providing these measures, the metric defines its equality to the values computed by other algorithms as follows:

	@Override
	public boolean equals(IMetric m) {
		return this.isComparableTo(m)
				&& ArrayUtils.equals(this.apsp.getLongValues(),
						((AllPairsShortestPaths) m).apsp.getLongValues(),
						"APSP");
	}

This basic metric (mainly holding the results) is then extended by the two different kinds of algorithms for the different kinds of graphs (unweighted and int-weighted edges).
The extension for int-weighted edges (*dna.metrics.paths.IntWeightedAllPairsShortestPaths*) can obviously only be compared with itself and its extensions and is only applicable to weighted graphs with int-weighted edges:

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof IntWeightedAllPairsShortestPaths;
	}
	
	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& g.getGraphDatastructures().isEdgeWeightType(IntWeight.class);
	}

The extension for unweighted graphs (*dna.metrics.paths.UnweightedAllPairsShortestPaths*) is comparable to itself and its children and can be applied to any graph (in weighted graphs, these weight are simply disregarded):

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof UnweightedAllPairsShortestPaths;
	}
	
	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

Both classes are only abstract super-classes for the actual algorithms.
In both cases, a static algorithm is implemented that implements the *IRecomputation* interface (*IntWeightedAllPairsShortestPathsR* and *UnweightedAllPairsShortestPathsR*):

	public class IntWeightedAllPairsShortestPathsR extends
			IntWeightedAllPairsShortestPaths implements IRecomputation {

		@Override
		public boolean recompute() {...}
		
		...
	}

In addition, dynamic algorithms applied after the updates are implemented (*IntWeightedAllPairsShortestPathsU* and *UnweightedAllPairsShortestPathsU*):

	public class IntWeightedAllPairsShortestPathsU extends
			IntWeightedAllPairsShortestPaths implements IAfterNA, IAfterNR,
			IAfterEA, IAfterER, IAfterEW {

		@Override
		public boolean init() {...}

		@Override
		public boolean applyAfterUpdate(EdgeRemoval er) {...}

		@Override
		public boolean applyAfterUpdate(EdgeAddition ea) {...}

		@Override
		public boolean applyAfterUpdate(EdgeWeight ew) {...}

		@Override
		public boolean applyAfterUpdate(NodeRemoval nr) {...}

		@Override
		public boolean applyAfterUpdate(NodeAddition na) {...}
		
		...

	}

Oviously, the unweighted version does not implement the interface *IAfterEW* as it does not consider weights at all.

In summary, the results provided by all algorithms are provided by a basic metric implementation (*AllPairsShortestPaths*).
It is extended by two classes that provide comparability to their respective children and define the kind of graph they are applicable to.
Each of these sub-classes (*IntWeightedAllPairsShortestPaths* and *UnweightedAllPairsShortestPaths*) is then extended by the different algorithms defined by the respective interfaces.

	AllPairsShortestPaths
		-| IntWeightedAllPairsShortestPaths
		    -| IntWeightedAllPairsShortestPathsR
		    -| IntWeightedAllPairsShortestPathsU
		-| UnweightedAllPairsShortestPaths
		    -| UnweightedAllPairsShortestPathsR
		    -| UnweightedAllPairsShortestPathsU

