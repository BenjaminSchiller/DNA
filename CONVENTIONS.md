DNA - Conventions
=====================

Some coding / naming conventions for working with / contributing to DNA:


Metrics
---------------------
Commonly, multiple implementations of the same metric *MyMetric* exist.
The most common differences are:

- only applicable to *Directed* (Directed) or *Undirected* (Undirected) graphs or not restricted ()
- complete *Re-computation* (R) after each batch or application of *Updates* (U)
- computing *Exact* () results or applying a *Heuristic* (H) to approximate the values

It is recommended to implement an abstract Metric *MyMetric* that contains all data structures shared by all implementation like values, distributions, node-value-lists, or auxiliary results.
This metric should be names *MyMetric* and already implement all shared methods (mainly leaving out the actual computation / update / approximation of the desired results).
The general naming convention for specific metric implementation is as follows:

- ${Directed|Undirected|}$MetricName${R|U}${|H}

The components of a metric implementation's name are therefore:

1. restrictions to the graph type: ${Directed|Undirected|}
1. metric name $MetricName
1. computation type ${R|U}
1. exact or heuristic ${|H}

Assuming a metric that is not restricted to a specific graph type as default, the first part of the name is left blank.
Similarly, an exact computation does not need to be further specified since this assumed as the default case.

General examples are:

- MyMetricR - exact re-computation of MyMetric for all graphs
- DirectedMyMetricU - exact update application of MyMetric for directed graphs
- UndirectedMyMetricRH - approximation of MyMetric using re-computation for undirected graphs

Please note that in some cases UndirectedX does not neccessarily mean that this metric can ONLY be applied to undirected graphs.
It can also mean that the metric considers undirected structures.
For example, an undirected triangle a<=>b<=>c<=>a can also be considered in a directed graph.

An explicit example are the different implementations of clustering coefficients:

- ClusteringCoefficient - abstract general class providing data structures
- DirectedClusteringCoefficient - abstract class for counting cirected triangles (only applicable to directed graphs)
- DirectedClusteringCoefficientR - counting directed triangles using a re-computation
- DirectedClusteringCoefficientU - counting directed triangles using updates
- UndirectedClusteringCoefficient - abstract class for counting undirected triangles (applicable to directed and undirected graphs)
- UndirectedClusteringCoefficientR - counting undirected triangles using a re-computation
- UndirectedClusteringCoefficientU - counting undirected triangles using updates



Graph Generators
---------------------

- ${Directed|Undirected|}$GraphName



Batch Generators
---------------------

- ${Directed|Undirected|}$BatchName



