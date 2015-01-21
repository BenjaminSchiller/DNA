package dna.graph.generators.traffic;

import java.util.Arrays;

/**
 * Container-Klasse für Edge-Information
 * @author Maurice
 *
 */
public class EdgeContainer {
	private int from;
	private int to;
	
	/**
	 * Konstruktur für den EdgeContainer
	 * @param from - Startknoten der Kante
	 * @param to - Endknoten der Kante
	 */
	public EdgeContainer(int from, int to){
		this.from = from;
		this.to = to;
	}
	
	/**
	 * liefert den HashCode des Containers, abgeleitet aus der Array-Darstellung
	 */
	public int hashCode(){
		return Arrays.hashCode(new int[]{from,to});
	}
	
	/**
	 * vergleicht das Objekt mit einem übergebenen Objekt
	 * @return true, wenn Startknoten und Endknoten identisch, sonst false
	 */
	public boolean equals(Object obj) {
		if( obj instanceof EdgeContainer){
			EdgeContainer ec = (EdgeContainer) obj;
			return Arrays.equals(new int[]{from, to},new int[]{ec.from,ec.to});
		}
		else
			return false;
	}
	
	/**
	 * Ausgabe für die manuelle Kontrolle
	 * @return 
	 */
	public String toString(){
		return "FROM:"+from+"\tTO:"+to;
	}
	
	/**
	 * liefert den Startknoten
	 * @return
	 */
	public int getFrom(){
		return from;
	}
	
	/**
	 * liefert den Zielknoten
	 * @return
	 */
	public int getTo(){
		return to;
	}
}
