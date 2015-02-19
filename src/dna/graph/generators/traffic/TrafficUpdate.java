package dna.graph.generators.traffic;

import java.util.List;

public class TrafficUpdate {
	
	// Werte für die Initialiserung in NORMALIZATION
	private double initCount;
	private double initLoad;
	
	// Werte für das UPDATE in NORMALIZATION
	private double updateCount;
	private double updateLoad;
	
	// Werte in FINAL_VALUE
	private double initUtilization;
	private double updateUtilization;
	
	// Knoten für die im Update ein neuer Wert übernommen wird
	private List<Integer> nodesToUpdate;
	
	public final static int NORMALIZATION = 0; 	// für jeden Knoten werden getrennt Count- und Load-Werte bestimmt, aus denen das Knotengewicht durch Normalisierung bestimmt werden kann
	public final static int FINAL_VALUE = 1; // für jeden Knoten wird ein Knotengewicht als Auslastung übergeben
	
	private int modus; // Entweder NORMALITAT oder Final-Value
	private int sleeptillUpdate; // Anzahl der vergangenen Batches bis das Update angewendet wird
	
	/**
	 * Konstruktur für den Modus mit festgelegten Count und Load-Wert, 
	 * die Berechnung des Knotengewichts erfolgt als normierter Wert mit dem maximalen Count-Wert
	 * @param initCount Count-Wert für die Initialisierung
	 * @param initLoad Load-Wert für die Initialisierung
	 * @param updateCount - Count-Wert für das Update
	 * @param updateLoad - Load-Wert für das Update
	 * @param sleeptillUpdate - Dauer des Aggregationszeitraums (Anzahl Batches)
	 * @param affectedNodes - Knoten für die eine Aktualisierung vorgenommen werden soll
	 */
	public TrafficUpdate(double initCount,double initLoad,double updateCount,double updateLoad,int sleeptillUpdate, List<Integer> affectedNodes){
		this.initCount=initCount;
		this.initLoad=initLoad;
		this.updateCount=updateCount;
		this.updateLoad=updateLoad;
		this.nodesToUpdate = affectedNodes;
		this.modus=NORMALIZATION;
		this.sleeptillUpdate = sleeptillUpdate;
	}
	
	/**
	 * Konstruktur für den Modus mit übergebenen normalisierten Werten,
	 * es erfolgte keine weitere Normalisierung mittels Count-Werten
	 * count und load werden als Dummy auf den Knoten mitgeführt
	 * @param initUtilization - Knotengewicht bei der Initialisierung
	 * @param updateUtilization - Knotengewicht für das Update
	 * @param sleepTilleTimestamp - Dauer des Aggregationszeitraums (Anzahl Batches)
	 * @param affectedNodes - Knoten für die eine Aktualisierung vorgenommen werden soll
	 */
	public TrafficUpdate(double initUtilization, double updateUtilization,int sleepTilleTimestamp,List<Integer> affectedNodes){
		this.initUtilization = initUtilization;
		this.updateUtilization = updateUtilization;
		this.nodesToUpdate = affectedNodes;
		this.modus=FINAL_VALUE;
	}
	
	/**
	 * liefert den Modus für den das Objekt erstellt wurde
	 * @return 0 = Normalization, 1 = Final_Values als Konstanten
	 */
	public int getModus(){
		return modus;
	}
	
	/**
	 * liefert den Count-Wert für die Initialisierung
	 * @return
	 */
	public double getInitCount(){
		return initCount;
	}
	
	/**
	 * liefert den Load-Wert für die Initialisierung
	 * @return
	 */
	public double getInitLoad(){
		return initLoad;
	}
	
	/**
	 * liefert den Count-Wert für das Update
	 * @return
	 */
	public double getUpdateCount(){
		return updateCount;
	}
	
	/**
	 * liefert den Load-Wert für das Update
	 * @return
	 */
	public double getUpdateLoad(){
		return updateLoad;
	}
	
	/**
	 * prüft ob der übergebene Knoten für das Update berücksichtigt werden soll
	 * @param nodeID - globale ID des Knoten
	 * @return
	 */
	public boolean isAffected(int nodeID){
		return nodesToUpdate.contains(nodeID);
	}
	/**
	 * prüft ob das Update angewendet werden soll
	 * @param timeStamp - aktuelle Zeitschritt
	 * @return
	 */
	public boolean changeToUpdate(int timeStamp){
		return sleeptillUpdate<=timeStamp;
	}
	
	/**
	 * liefert das initiale Knotengewicht
	 * @return
	 */
	public double getInitUtilization(){
		return initUtilization;
	}
	
	/**
	 * liefert das Knotengewicht im Update
	 * @return
	 */
	public double getUpdateUtilization(){
		return updateUtilization;
	}
	
	/**
	 * liefert den Zeitraum für die Aggregation
	 * @return
	 */
	public int getSleepTillUpdate(){
		return sleeptillUpdate;
	}
}
