package dna.labels.labeler.models;

import java.io.IOException;
import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.labels.Label;
import dna.labels.labeler.Labeler;
import dna.metrics.IMetric;
import dna.series.data.BatchData;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.machineLearning.ModelWrapper;

/**
 * The ModelLabeler labels a batch based on a trained machine-learning model.
 * 
 * @author Rwilmes
 *
 */
public class ModelLabeler extends Labeler {

	protected ModelWrapper model;

	public ModelLabeler(String name, String scriptPath, String featureListPath, int numberOfFeatures,
			String modelPath) {
		super(name);

		try {
			this.model = new ModelWrapper(scriptPath, featureListPath, numberOfFeatures, modelPath);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isApplicable(GraphGenerator gg, BatchGenerator bg, IMetric[] metrics) {
		return true;
	}

	@Override
	public ArrayList<Label> computeLabels(Graph g, Batch batch, BatchData batchData, IMetric[] metrics) {
		ArrayList<Label> list = new ArrayList<Label>();
		if (model != null) {
			String prediction = model.predict(batchData);

			if (!prediction.equals("0"))
				list.add(new Label(this.getName(), "class", prediction));
		}
		return list;
	}

}
