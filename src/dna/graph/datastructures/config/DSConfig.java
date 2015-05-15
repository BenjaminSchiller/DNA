package dna.graph.datastructures.config;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;

public abstract class DSConfig {

	public Class<? extends IDataStructure> V;
	public Class<? extends IDataStructure> E;

	public DSConfig(Class<? extends IDataStructure> V,
			Class<? extends IDataStructure> E) {
		this.V = V;
		this.E = E;
	}

	public abstract GraphDataStructure getGDS();

	public abstract String getStimpleName(String sep);

}
