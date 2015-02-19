package dna.graph.generators.traffic;

public enum TrafficModi {
	/**
	 * Kontinuierlicher Modus, bei dem ausgehend von einem Startzeitpunkt, 
	 * in jedem Batch ein Zeitschritt festgelegter Dauer addiert wird
	 */
	Continuous, 
	/**
	 * Tagesmodus, bei dem ausgehend von einem Startzeitpunkt und eines
	 * Zeitintervall, für jeden der durch daySelection ausgewählten Tage
	 * das entsprechende Intervall berechnet wird
	 */
	DayTimeRange, 
	/**
	 * Statischer Simulationsmodus, bei dem die Daten anhand des TrafficUpdate-
	 * Objekt geändert werden
	 */
	Simulation,
	/**
	 * Aggregationmodus für den Vergleich, bei dem für den Batch die Tage eines
	 * festgelegten Zeitraums aggregiert werden, und dann ein Vergleichsmaß (z.b.
	 * Durchschnitt) berechnet wird
	 */
	Aggregation
}
