package dna.latex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/** A TexTable for single scalar values. **/
public class SingleScalarTexTable extends TexTable {

	// constructor
	public SingleScalarTexTable(TexFile parent, String[] headRow,
			SimpleDateFormat dateFormat, String scaling,
			HashMap<Long, Long> mapping, TableFlag... flags) throws IOException {
		super(parent, headRow, dateFormat, flags);
		this.scaling = scaling;
		this.map = mapping;
		this.begin(headRow);
	}
}
