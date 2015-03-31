DNA - Naming Conventions
=====================

Some coding / naming conventions for working with / contributing to DNA:


Metrics
---------------------
Commonly, multiple implementations of the same metric *MyMetric* exist.
The most common differences are:

- only applicable to *Directed* (Directed) or *Undirected* (Undirected) graphs or not restricted ()
- only applicable to *Int Weighted* (IntWeighted) or *Double Weighted* (DoubleWeighted) nodes or edges (in case it is clear if nodes, edges, or both are weighted there is no need to specify, otherwise, use (IntWeightedNode) or (DoubleWeightedEdge) instead), (Unweighted) should be used to indicate that weights are not considered
- complete *Re-computation* (R) after each batch, application of *Updates* (U), or application of *Batches* (B)
- computing *Exact* () results or applying a *Heuristic* (H) to approximate the values

It is recommended to implement an abstract Metric *MyMetric* that contains all data structures shared by all implementation like values, distributions, node-value-lists, or auxiliary results.
This metric should be names *MyMetric* and already implement all shared methods (mainly leaving out the actual computation / update / approximation of the desired results).
The general naming convention for specific metric implementation is as follows:

- *${Directed|Undirected|}${|Unweighted|IntWeighted|DoubleWeighted}$MetricName${R|U|B}${|H}*

The components of a metric implementation's name (given name as well as class name) are therefore:

1. restrictions to the graph type: *${Directed|Undirected|}*
2. restrictions to the existance of weights: *${|IntWeighted|DoubleWeighted}*
1. metric name *$MetricName*
1. computation type *${R|U|B}*
1. exact or heuristic *${|H}*

Assuming a metric that is not restricted to a specific graph type as default, the first part of the name is left blank.
Similarly, in case there are no restrictions on the weights and their type, the second parts is omitted as well.
Similarly, an exact computation does not need to be further specified since this assumed as the default case.

General examples are:

- *MyMetricR* - exact re-computation of MyMetric for all graphs
- *DirectedMyMetricU* - exact update application of MyMetric for directed graphs
- *DirectedIntWeightedMyMetricR* - exact update application of MyMetric for directed graphs with int weights (for either nodes or edges)
- - *UndirectedDoubleWeightedEdgeMyMetricR* - exact recomputation of MyMetric for undirected graphs with double weights for edges
- *UndirectedMyMetricRH* - approximation of MyMetric using re-computation for undirected graphs

Please note that in some cases UndirectedX does not neccessarily mean that this metric can ONLY be applied to undirected graphs.
It can also mean that the metric considers undirected structures.
For example, an undirected triangle a<=>b<=>c<=>a can also be considered in a directed graph.

An explicit example are the different implementations of clustering coefficients:

- *ClusteringCoefficient* - abstract general class providing data structures
- *DirectedClusteringCoefficient* - abstract class for counting cirected triangles (only applicable to directed graphs)
- *DirectedClusteringCoefficientR* - counting directed triangles using a re-computation
- *DirectedClusteringCoefficientU* - counting directed triangles using updates
- *UndirectedClusteringCoefficient* - abstract class for counting undirected triangles (applicable to directed and undirected graphs)
- *UndirectedClusteringCoefficientB* - counting undirected triangles using batches
- *UndirectedClusteringCoefficientR* - counting undirected triangles using a re-computation
- *UndirectedClusteringCoefficientU* - counting undirected triangles using updates


Metric Parameters
---------------------

As names for the parameters of a metric, use names in analogy to *Java* class names, e.g., **FirstProperty** and **SecondProperyOfTheMetric** but not *first:Property*, *Second_Property*, or *SECOND_PROPERTY*.


Metric Results
---------------------
There are four kinds of results that a metric can provide:

1. **Value** - single scalar value *v*, e.g., average degree
2. **Distribution** - (frequency) distribution *P(x=k)*, e.g., in-degree distribution
3. **NodeValueList** - one scalar value per vertex *P(v)*, e.g., local clustering coefficient
4. **NodeNodeValueList** - one scalar value per vertex pair *P(v,w)*, e.g., similarity measures

Their names should all be given similar to *Java* class names, e.g., **ThisValuesName**. You should not use names like *thisValuesName* or *THIS_VALUES_NAME*.

Do not append *Distribution*, *Value*, *NodeValueList*, or *NodeNodeValueList* to the name of a result.

For values that are "summaries" of distribution, simply append descriptions like *Min*, *Max*, *Avg*, or *Med* to the name instead of prefixing them.

Prefix binned distribution with *Binned*.

Do not abbreviate names as it makes understanding their meaning clearer, e.g., use *GlobalClusteringCoefficient* instead of *GlobalCC*.

This leads to the following naming scheme:

	{Binned|}NameOfResult{Min|Max|Avg|Med|}

Some Examples:

- Metric: **DegreeDistribution**
	- distributions: *InDegree*, *OutDegree*, *Degree*
	- values: *InDegreeMin*, *InDegreeMax*, *OutDegreeMin*, *OutDegreeMax*, *DegreeMin*, *DegreeMax*
- Metric: **ClusteringCoefficient**
	- distributions: *BinnedLocalClusteringCoefficient*
	- values: *GlobalClusteringCoefficient*, *AverageClusteringCoefficient*
	- nodeValueLists: *LocalClusteringCoefficient*

Please note: while the *AverageClusteringCoefficient* is simply the average of the *LocalClusteringCoefficient*, it is a specific name and is therefore not called the *LocalClusteringCoefficientAvg*.


Graph Generators
---------------------

The graph generator for a graph *GraphName* should start with the resulting graph structure (*Directed* for directed graphs only, *Undirected* for undirected graphs only and blank for any graph type).
The class name should end with *Generator* while the internal name omits this addition.

- class name: *${Directed|Undirected|}$GraphNameGenerator*
- internal name: *${Directed|Undirected|}$GraphName*

Implementations of a graph generator that do not produce a graph on their own do not add the postfix *Generator*.
For example, the graph generator *GraphWeights* takes another graph generator as input and only adds node and/or edge weights to the generated graph.

While it is convention to end the *GraphName* by *Graph* (e.g., *RandomGraph*, *EmptyGraph*), it is not required when the name of the specific graph is generally used without it (e.g., *Clique*, *Ring*).



Batch Generators
---------------------

- ${Directed|Undirected|}$BatchName



