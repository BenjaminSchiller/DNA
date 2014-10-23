package mydna;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

import mydna.mymetric.nodeweight.NodeWeightsR;
import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.edges.DirectedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.Weight.WeightSelection;
import dna.io.GraphWriter;
import dna.metrics.Metric;
import dna.metrics.MetricNotApplicableException;
import dna.metrics.centrality.BetweennessCentralityR;
import dna.metrics.clustering.DirectedClusteringCoefficientR;
import mydna.mymetric.apsp.DoubleWeightedAllPairsShortestPathsR;
import mydna.mymetric.degree.DegreeDistributionR;
import mydna.mymetric.degree.DegreeDistributionU;
import dna.plot.Plotting;
import dna.series.AggregationException;
import dna.series.Series;
import dna.series.data.SeriesData;
import dna.updates.generators.BatchGenerator;
import dna.util.Config;
import dna.util.Stats;

public class Controller {
	
	public static final GraphDataStructure gds = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList, DHashMap.class,
					ListType.GlobalEdgeList, DHashMap.class,
					ListType.LocalEdgeList, DHashMap.class,
					ListType.LocalNodeList, DHashMap.class),
			DirectedWeightedNode.class, DirectedEdge.class , Double3dWeight.class, WeightSelection.Rand,Double3dWeight.class,WeightSelection.Rand);
	
	public static void main(String[] args) throws IOException, InterruptedException, AggregationException, MetricNotApplicableException {
		
		final DateTime initDateTime = new DateTime(2014,9,30,6,0,0);
		
		final int stepSize = 5;
		
		// Auswahl des Modells und Modus
		final int modell= 2; // 0 = Kreuzungsmodell, 1 = Wegemodell, 2 = Sensormodell
		final int modus = 2; // 0 = Schritte der Länge stepSize, 1 = TagesSchritte, 2 = Simulation
		final double treshold = 80;
		
		// Parameter für Tages-Modus
		final boolean[] daySelection = new boolean[] {true,true,true,true,true,false,false}; // Montag, Dienstag, Mittwoch, Donnerstag, Freitag, Samstag, Sonntag
		final int timeRange = 5; // Radius um die Startzeit;
		final DateTime holidayStart = new DateTime(2014,9,26,6,0,0);
		final int observationWeeks = 1;
		
		
		
		// TrafficUpdate für Simulation
		final double initCount = 1;
		final double initLoad = 0.2;
		final double updateCount =3;
		final double updateLoad =0.8;
		final double initUtilization = 0.2;
		final double updateUtilization = 0.8;
		final Integer[] affectedNodes = new Integer[] {661};
		final int sleepTillTimestamp = 1;
		
		// Auswahl zwischen count/load-Update oder utilization-Update
		final TrafficUpdate trafficUpdateCL = new TrafficUpdate(initCount,initLoad,updateCount,updateLoad, sleepTillTimestamp,Arrays.asList(affectedNodes));
		final TrafficUpdate trafficUpdateUti = new TrafficUpdate(initUtilization, updateUtilization, sleepTillTimestamp,Arrays.asList(affectedNodes));
		final TrafficUpdate trafficUpdate =trafficUpdateCL;
		
		//Verwende reale Maximalwerte oder Dummy-Maximalwerte
		final boolean dummyMax = true;
		DB db = new DB(gds, initDateTime, stepSize, daySelection,timeRange,treshold,trafficUpdate,dummyMax);
		Stats stats = new Stats();
		
		
		GraphGenerator[] generators = new GraphGenerator[] {new TrafficCrossroadGraphGenerator("CrossroadGraph", gds,db,0,modus,initDateTime,stepSize,timeRange,trafficUpdate), new TrafficInputWayGraphGenerator("InputWayGraph", gds,db,0,modus,initDateTime,stepSize,timeRange,trafficUpdate,treshold),new TrafficSensorGraphGenerator("SensorGraph", gds,db,0,modus,initDateTime,stepSize,timeRange,trafficUpdate,treshold)};
		BatchGenerator[] batchGenerators = new BatchGenerator[] {new TrafficCrossroadBatchGenerator("CrossroadBatch",db,initDateTime,stepSize,modus,holidayStart,daySelection,trafficUpdate), new TrafficInputWayBatchGenerator("InputWayBatch",db,initDateTime,stepSize,modus,holidayStart,daySelection,treshold,trafficUpdate),new TrafficSensorBatchGenerator("SensorBatch",db,initDateTime,stepSize,modus,holidayStart,daySelection,treshold,timeRange,trafficUpdate)};

		
		int versuch = 7;
		String data = "data/versuch"+versuch+"/";
		String name = "Versuch "+versuch;
		String plots = "data/versuch"+versuch+"-plots/";
		
		String data_old = "data/versuch1/";
		String name_old = "Versuch 1";
		
		String vergleich = "data/vergleich_plots/";
		
		GraphGenerator gg = generators[modell];
		BatchGenerator bg = batchGenerators[modell];
		Metric[] metrics = new Metric[] {new DegreeDistributionR(),new BetweennessCentralityR(), new DirectedClusteringCoefficientR(), new DoubleWeightedAllPairsShortestPathsR()};

		Series s = new Series(gg, bg, metrics, data, name);
		SeriesData sd_new = null;
		
		
		
		if(modus == 2){
			sd_new = s.generate(1, 1);
		}
		else {
			//Plotting.plot(s.generate(1, Helpers.weekToDay(observationWeeks, daySelection)), plots);
			Plotting.plot(s.generate(1,5), plots);
		}
		Plotting.plot(sd_new, plots);
		
		//SeriesData sd_old = Series.get(data_old, name_old);
	
		
		Config.overwrite("GNUPLOT_DATA_IN_SCRIPT", "false");
		//Plotting.plotDistributions(new SeriesData[] { sd, sd }, plots);
		//
		//Plotting.plot(new SeriesData[]{sd_new,sd_old}, vergleich);


		stats.end();
		db.disconnect();
	}
}


