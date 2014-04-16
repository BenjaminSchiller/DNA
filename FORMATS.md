DNA - Formats
============

In this document, the (supported) file formats for batches and graphs are described.
As a prerequesite for that, the String formats for nodes, edges, and weight are described as they are used for representing graphs and batches.

Each implementation of nodes, edges, and weights has a constructor that takes a single String parameters and parses it under the assumption that it is a correct String representation of that class.
Each class also provides a method that returns its parseable String representation (either *asString()* or *getStringRepresentaton()*).

Please note that all these formats are only referring to the basic, already implemented versions of nodes, edges, and weights.
Extensions of these basic classes could use a different representation.



Nodes
---------------------
The String representation of an **unweighted node** (directed or undirected) is simply its index:

	${index}

Examples are:

	1
	516
	9562

These basic nodes are implemented in:

- *dna.graph.nodes.DirectedNode*
- *dna.graph.nodes.UndirectedNode*

For **weighted nodes**, the string representation of their weight is simply added to the index, separated by "@":

	${index}@${weight}

Examples are (for more details regarding the weights, please check the weights section):

	1@15
	516@83.4326
	9562@4;6;29

These weighted versions of directed and undirected nodes are implemented in:

- *dna.graph.nodes.DirectedWeightedNode*
- *dna.graph.nodes.UndirectedWeightedNode*



Edges
---------------------
The String representation of an **unweighted edge** consists of the two nodes connected by that edge, separated by "->" or "<->":

	${src-index}->${dst-index}
	${node1-index}<->${node2-index}

For a directed edge, the first node's index denoted the edge's source, the second node's index its destination.
For an undirected edge, the order of two nodes' indices is not important, by convention, the first index is the smaller one.

Examples are:

	1->4
	5->2
	15<->17
	186<->981

These basic edges are implemented in:

- *dna.graph.edges.DirectedEdge*
- *dna.graph.edges.UndirectedEdge*

For **weighted edges**, the String representation of their weight is simply added to the basic String representation, separated by "@":

	${index-src}->${index-dst}@{weight}
	${index-node1}<->${index-node2}@{weight}

Examples are (for more details regarding the weights, please check the weights section):

	1->4@4.5;0.0013
	5->2@3;2
	15<->17@1935
	186<->981@0.0150;5.2

These weighted versions of directed and undirected edges are implemented in:

- *dna.graph.edges.DirectedWeightedNode*
- *dna.graph.edges.UndirectedWeightedNode*




Weights
---------------------
So far, only very simple **weights** have been implemented.
Each weight consists of one, two, or three numbers, either int, long, or double.
In case of multiple numbers, they are separated by ";".
Hence, the general format of these basic weights is as follows:

	${w1}
	${w1};${w2}
	${w1};${w2};${w3}

xamples are:

	1.4
	47;31;95
	0.3;1.52;8.773
	62;23

The following weights have been implemented so far:

- int weight: *dna.graph.weights.IntWeight*
- int weight (2d): *dna.graph.weights.Int2dWeight*
- int weight (3d): *dna.graph.weights.Int3dWeight*
- long weight: *dna.graph.weights.LongWeight*
- long weight (2d): *dna.graph.weights.Long2dWeight*
- long weight (3d): *dna.graph.weights.Long3dWeight*
- double weight: *dna.graph.weights.DoubleWeight*
- double weight (2d): *dna.graph.weights.Double2dWeight*
- double weight (3d): *dna.graph.weights.Double3dWeight*



Graphs
---------------------
The format of a graph consists of certain keywords followed by the respective entry.
Each keyword starts with ">>> " and cannot be different than expected.
The general format is given in the following:

	>>> DNA Graph
	${name}
	>>> Data Structures
	${graphDataStructures}
	>>> Nodes
	${nodeCount}
	>>> Edges
	${edgeCount}
	>>> Timestamp
	${timestamp}
	>>> List of Nodes
	${nodeList}
	>>> List of Edges
	${edgeList}

Each entry has to be present and cannot be left out, i.e., the respective line is expected and the content must be there.
The expected content is the following:

- **name** - the name of the graph
- **graphDataStructures** - gds used for the graph (for details, see below)
- **nodeCount** - number of nodes in the graph
- **edgeCount** - number of edges in the graph
- **timestamp** - timestamp associated to the graph
- **nodeList** - list of nodes, one line per node (in its String representation)
- **edgeList** - list of edges, one line per edge (in its String representation)

The number of nodes and edges should denote the number of nodes/edges given in the respective list later.
In the current implementation, this is not required but giving precise values here makes thie initialization of data structures easier.
The order of nodes and edges in the respective list is not important.

In the following examples, the graph data structures (GDS) is left out for simplicity.
Please see below for its correct format.
When reading a graph from a file AND giving a GDS, the one given in the file is ignored.
In that case, leaving this line blank (but not omitting it completely) would not be a problem.

An example of a directed graph with 4 nodes connected in a ring topology is given below:

	>>> DNA Graph
	Ring Graph Topology
	>>> Data Structures
	...
	>>> Nodes
	4
	>>> Edges
	4
	>>> Timestamp
	0
	>>> List of Nodes
	0
	1
	2
	3
	>>> List of Edges
	0->1
	1->2
	2->3
	3->0

An example of an undirected graph with 5 nodes connected in a star topology is given below:

	>>> DNA Graph
	Star Graph Topology
	>>> Data Structures
	...
	>>> Nodes
	5
	>>> Edges
	4
	>>> Timestamp
	0
	>>> List of Nodes
	0
	1
	2
	3
	4
	>>> List of Edges
	0<->1
	0<->2
	0<->3
	0<->4

Reader and writer for this format are provided by:

- *dna.io.GraphReader*
- *dna.io.GraphWriter*



Updates
---------------------
There are six different types of updates:

- **NA** - node addition
- **NR** - node removal
- **NW** - node weight
- **EA** - edge addition
- **ER** - edge removal
- **EW** - edge weight

The general format is:

	${keyword}_${update}

The keyword is *NA*, *NR*, *NW*, *EA*, *ER*, or *EW*.
It is followed by a String representation of the respective update, separated by "_".

The specific format of the three node updates is the following:

	NA_${new-node-string-representation}
	NR_${node-index}
	NW_${node-index}:${new-weight-string-representation}

Analogously, the format of the edge updates is as follows:

	EA_${new-edge-string-representation}
	ER_${node1-index}-${node2-index}
	EW_${node1-index}-${node2-index}:${new-weight-string-representation}

Please note that the *new-node/edge/weight-string-representation* is the respctive string representation as described above which is required to create a new instance of the object.
The *node/node1/node2-index* is only the (int) index of the respective node.
Also, the *${node1-index}-${node2-index}*, used to identify an existing edge (for removal of chaninging its weight) is no the edges String representation (which would contain either "->" or "<->" instead of the separator "-").

The following examples of updates are mixed from directed and undirected networks as well as different weight types.
Hence, they could not be combined in the same network and yre only used as examples:

	# add new unweighted node with index 45
	NA_45
	# add new node with index 2, weighted with double-2d value
	NA_2@73.2;26.7
	
	# remove node with index 2
	NR_2
	# remove node with index 93
	NR_93
	
	# change the weight of node 26 to 54 (int weight)
	NW_26:54
	# change weight of node 718 to 1.3;66.2;0.12 (double-3d)
	NW_718:1.3;66.2;0.12
	
	# add new directed, unweighted edge from node 1 to 2
	EA_1->2
	# add new undirected edge between node 4 and 6, weighted with 63;92 (int-2d)
	EA_4<->6@63;92
	
	# directed case: remove edge from 4 to 7
	# undirected case: remove edge between 4 and 7
	ER_4-7
	# directed case: remove edge from 38 to 23
	# undirected case: remove edge between 38 and 23
	ER_38-23
	
	# directed case: change weight of edge 55->27 to 1.52 (double)
	# undirected case: change weight of edgem 55<->27 to 1.52 (double)
	EW_55-27:1.52
	# directed case: change weight of edge 8->41 to 19;22 (int-2d)
	# undirected case: change weight of edge 8<->41 to 19;22 (int-2d)
	EW_8-41:19;22



Batches
---------------------
The format of a batch consists of certain keywords followed by the respective entry, similar to the format of a graph.
The general format is given in the following:

	>>> From
	${from}
	>>> To
	${to}
	>>> List of Updates
	${updateList}

Each entry has to be present and cannot be left out, i.e., the respective line is expected and the content must be there.
The expected content is the following:

- **from** - timestamp of the graph on which the batch can be applied
- **to** - timestamp of the graph after applying the batch
- **updateList** - list of all updates, single line per update

An example of a batch that adds unweighted nodes 112, 145, and 250 and removes the nodes 4, 7, 13, and 19 is given below.
The batch is applied to the graph at timestamp 22674 and transforms it to timestamp 22675.

	>>> From
	22674
	>>> To
	22675
	>>> List of Updates
	NA_112
	NA_145
	NA_250
	NR_4
	NR_7
	NR_13
	NR_19

Reader and writer for this format are provided by:

- *dna.io.BatchReader*
- *dna.io.BatchWriter*


Graph Data Structures
---------------------

*TBD*






Additional Graph Reader - Edge List
---------------------
In adition to the basic graph reader and writer, an edge list reader is implemented in:

- *dna.io.EdgeListGraphReader*
- *dna.io.EdgeListGraphWriter*

Provided with a String separator as a parameter, they allow to read and write a graph in the following format:

	${node1}${separator}${node2}

An example of such a file, using "\t" (tab) as separator is:

	1	3
	5	9
	9	3

Note that using this reader, *${node1}* and *${node2}* are mapped to a consecutive index, i.e.,

	1 => 0
	3 => 1
	5 => 2
	9 => 3

Hence, the example given above would result in a graph with nodes 0, 1, 2, and 3 and edges (0,1), (2,3), and (3,1).
The same graph would be read from the following example

	a	b
	c	d
	d	b

Please note that the GDS given to the reader or writer determines if the edges are interpreted as directed of undirected.

