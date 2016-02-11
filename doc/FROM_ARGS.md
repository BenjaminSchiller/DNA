# FromArgs

In DNA, instances of many components such as Metrics or GraphGenerators can be created from arguments passed down to a program.
In this document, we describe all of the existing *FromArgs* classes which allow for the instantiation over a static `parse(...)` method.

Each parse method has a similar structure:

	parse([Object obj,] XType type, String... args)

1. `Object obj` - an optional list of required objects
2. `XType type` - an enum value indicating the type of the object to be created
3. `String... args` - a list of String arguments for specifying parameters

In the following description of each helper class, we give the required object parameters (if there are any), the different types, and the required String parameters for each type.
Note that the list of types given here ight not be complete since we are are constantly adding new types and might not update the description here.

In case different number of String arguments are possible for the same type, we simply list the type multiple times.
For each type, the String parameters are given as a list of their names.

## existing FromArgs classes

All classes are located in the package `dna.util.fromArgs`.

- graphs and updates
	- WeightFromArgs
	- GraphDataStructuresFromArgs
	- GraphGeneratorFromArgs
	- BatchGeneratorFromArgs
- metrics
	- MetricFromArgs
- parallel metrics
	- PartitioningSchemeFromArgs
	- NodeAssignmentFromArgs
	- CollationFromArgs


### WeightFromArgs

- `Double
	- DoubleWeight
- `Double2d
	- Double2dWeight
- `Double3d
	- Double3dWeight
- `Int
	- IntWeight
- `Int2d
	- Int2dWeight
- `Int3d
	- Int3dWeight
- `Long
	- LongWeight
- `Long2d
	- Long2dWeight
- `Long3d
	- Long3dWeight

The implementations of all weights can be found in `dna.graph.weights`.

### GraphDataStructuresFromArgs

- `Directed`
	- directed graph, unweighted
- `DirectedV` weightTypeV weightSelectionV
	- directed graph, weighted edges
- `DirectedE` weightTypeE weightSelectionE
	- directed graph, weighted edges
- `DirectedVE` weightTypeV weightSelectionV weightTypeE weightSelectionE
	- directed graph, weighted vertices and edges
- `Undirected`
	- undirected graph, unweighted
- `UndirectedV` weightTypeV weightSelectionV
	- undirected graph, weighted edges
- `UndirectedE` weightTypeE weightSelectionE
	- undirected graph, weighted edges
- `UndirectedVE` weightTypeV weightSelectionV weightTypeE weightSelectionE
	- undirected graph, weighted vertices and edges

The weightType parameter is used to get the class of the weight using `WeightFromArgs`.

The weightSelection parameter determines how the weight of a new unspecified weight object is selected.
The folowing values are possible and are defined in the enum `dna.graph.weights.Weight.WeightSelection`:

- `None`
- `NaN` (only doubles)
- `One` (1)
- `Zero` (0)
- `Rand` (random value, [0,1] for doubles)
- `RandTrim1` (double only, [0,1] trimmed after first digit)
- `RandTrim2` (double only, [0,1] trimmed after second digit)
- `RandTrim3` (double only, [0,1] trimmed after third digit)
- `Min` (min value, depending on type)
- `Max` (max value, depending on type)
- `RandPos` (abs(rand))
- `RandNeg` (-1 * abs(rand))
- `RandPos100` (int only, rand from [0,99))
- `RandPos10` (int only, rand from [0,10))

### GraphGeneratorFromArgs

For each type, a GraphDataStructures object (gds) must be given.

- `Clique` nodes
	- clique or complete graph
- `Grid2d` x y closedType
	- x*y nodes, connected as a 2d-grid
	- closedType determines if / how to connect start and end nodes
		- OPEN: no connections
		- CLOSED: closed to a ring
		- MOEBIUS: closed to a moebius stripe
- `Grid3d` x y z closedType
	- x*y*z nodes, connected as a 3d-grid
- `HoneyComb` x y closedType
	- x*y nodes, connected as a honey comb
- `Ring` nodes
	- ring graph
- `RingStar` nodes
	- ring graph with a dedicated central node
- `Star` nodes
	- start graph
- `Random` nodes edges
	- random graph (erdos renyi)
- `BarabasiAlbert` startNodes startEdges nodesToAdd edgesPerNode
	- starts with random graph and grown with preferential attachement
- `PositiveFeedbackPreference` startNodes startEdges nodesToAdd
	- PFP model
- `ReadableEdgeListFileGraph` dir filename separator
	- reading an edge list in the form `src` `separator` `dst` per line
- `Timestamped` dir filename name timestampedGraphType parameter
	- timestamped graph where the appearance time of each edge is specified
	- format: `src` `sep` `dst` `sep` `timestamp`
	- timestampedGraphType determined how many lines to process:
		- TIMESTAMP: read all lines with timestamp <= *parameter*
		- EDGE_COUNT: read the first *parameter* lines
- `Timestamped` dir filename name remapIndex timestampedGraphType parameter
	- default is true, node indexes (or string) are mappes to 0,1,2,...
- `Timestamped` dir filename name remapIndex separator commentPrefix timestampedGraphType parameter
	- separator for file format is specified (default is ',')
	- line prefix for comments (will be skipped) is specified (default is '%')

### BatchGeneratorFromArgs

For each type, a GraphGenerator (gg) must be given.

- `BarabasiAlbert` nodes edgesPerNode
	- adding nodes with preferential attachment
- `PositiveFeedbackPreference` nodes
	- adding nodes with the PFP model
	- delta = 0.048
	- p = 0.3
	- q = 0.1
- `PositiveFeedbackPreference` nodes delta p q
- `RandomGrowth` nodes edgesPerNode
	- growing graph by adding nodes with edges to random others
- `RandomScaling` growthFactor shrinkFactor
	- add fraction of growthFactor random nodes and edges
	- remove fraction of shrinkFactor random nodes and edges
- `RandomScaling` growthFactorV shrinkFactorV growthFactorE shrinkFactorE
	- separate growth and shrink parameters for nodes and edges
- `Random` na nr ea er
	- adding na new nodes
	- removing nr existing nodes
	- adding ea random edges between existing nodes
	- removing er random edges
- `RandomW` na nr nw weightSelectionV ea er ew weightSelectionE
	- changes the weight of nw randomly selected nodes
	- changes the weight of ew randomly selected edges
	- weights are selected with weightSelectionX
- `RandomEdgeExchange` exchanges maxFails
	- exchanges start and end points of exchanges many pairs of randomly selected edges
	- abort after unsuccessfully determining new pair maxFails times
- `Timestamped` timestampedBatchType parameter
	- timestampedBatchType determines how many lines to process
		- TIMESTAMP_INTERVAL: process lines until currentTimestamp + parameter
		- EDGE_COUNT: process parameter many lines
		- BATCH_SIZE: process lines until batch (node addition + edge additions) has size parameter
- `Timestamped` timestampedBatchType parameter maxTImestamp
	- stop after reading timestamp maxTimestamp

### MetricFromArgs

- `DegreeDistributionR`
- `DegreeDistributionU`
- `UndirectedClusteringCoefficientR`
- `UndirectedClusteringCoefficientU`
- `PartitionedUndirectedClusteringCoefficientR`
- `UnweightedAllPairsShortestPathsR`
- `UnweightedAllPairsShortestPathsU`
- `WeakConnectivityB`
- `WeakConnectivityR`
- `WeakConnectivityU`
- `UndirectedMotifsR`
- `UndirectedMotifsU`
- `AssortativityR`
- `AssortativityU`
- `BetweennessCentralityR`
- `BetweennessCentralityU`
- `RichClubConnectivityByDegreeR`
- `RichClubConnectivityByDegreeU`

### PartitioningSchemeFromArgs

For each type, the partitionType (OVERLAPPING, NON\_OVERLAPPING, NODE\_CUT) and and the partitionCount must be specified.

- `BFS`
- `DFS`
- `EQUAL_SIZE`
- `LPA`
- `RANDOM`

### NodeAssignmentFromArgs

- `FIRST_EDGE`
- `RANDOM`
- `ROUND_ROBIN`

### CollationFromArgs

- `UndirectedClusteringCoefficientCollationNonOverlapping`
- `UndirectedClusteringCoefficientCollationOverlapping`