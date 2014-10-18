package dna.updates.generators;

import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class BatchGenerator extends ParameterList implements
		IBatchGenerator {

	public BatchGenerator(String name, Parameter... parameters) {
		super(name, parameters);
	}

}
