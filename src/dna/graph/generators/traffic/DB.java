package dna.graph.generators.traffic;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;

import sun.security.action.GetIntegerAction;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.INode;
import dna.graph.nodes.Node;
import dna.graph.weights.Double3dWeight;

public class DB {
	private static Connection con = null;
	private static String dbHost = "127.0.0.1"; // Hostname
	private static String dbPort = "3306";      // Port -- Standard: 3306
	private static String dbName = "dbName";   // Datenbankname
	private static String dbUser = "dbUser";     // Datenbankuser
	private static String dbPass = "dbPass";      // Datenbankpasswort

	
	private GraphDataStructure gds;
	private int stepSize;
	public DateTime initDateTime;
	private HashMap<Integer,double[]> maxValuesCrossroad;
	private HashMap<Integer,double[]> maxValuesInputWays;
	private HashMap<Integer,double[]> maxValuesSensors;
	private HashMap<Integer,SensorModelNode> sensorModelNodes;
	private HashMap<Integer,CrossroadWeight> crossroadModelNodes;
	public HashMap<Integer, int[]> inputWays;
	public HashMap<InputWay, Integer> inputWaysToID;
	public HashMap<InputWay,List<int[]>> inputWayConnections; //Key: ToCrossroad,ToWay - Value: List<FromCrossroad,FromWay>
	public boolean[] days;
	public int timeRange;
	private double treshold;
	private TrafficUpdate trafficUpdate;
	private HashMap<EdgeContainer,Edge> disabledEdges;
	private HashMap<Integer, HashMap<EdgeContainer, Edge>> disabledEdgesInputWay;
	private boolean dummyMax;
	private boolean backupWays = false;
	private boolean improvedMax = true;
	private boolean newMaxValues = false;
	
	/**
	 * Konstruktur der Datenbank, die Login-Daten werden entweder aus einer Txt-Datei gelesen,
	 * oder falls diese nicht vorhanden ist, aus den Parametern übernommen.
	 * @param gds, Datenstruktur, welcher der Verwendung von DNA zugrunde liegt (für Knotentypen, Kantentypen ..)
	 * @param initTDateTime, Startzeitpunkt für die Modi mit realen Daten
	 * @param stepSize, Schrittweite für den kontinuerlichen Modus
	 * @param daySelection, Boolean-Array mit 7 Einträgen für die Wochentage
	 * @param timeRange, Intervalllänge für den Tages und Aggregationsmodus
	 * @param treshold, Schwellwert für die Überlastungserkennung
	 * @param trafficUpdate, statische Daten für den Simulationsmodus
	 * @param dummyMax, Verwendung von synthetischen Max-Werten oder realen Max-Werten
	 */
	public DB(GraphDataStructure gds, DateTime initTDateTime, int stepSize, boolean[] daySelection , int timeRange, double treshold,TrafficUpdate trafficUpdate,boolean dummyMax) {
		try {
			FileReader fr = new FileReader("db.txt");
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			String[] values;
			while (line != null) {
				values = line.split(" ");
				if(values.length != 2)
					System.out.println("Falsche Anzahl an Parametern");
				switch (values[0]) {
					case "dbHost":	
						dbHost = values[1];
						break;
					case "dbPort":
						dbPort = values[1];
						break;
					case "dbName":
						dbName = values[1];
						break;
					case "dbUser":
						dbUser = values[1];
						break;
					case "dbPass":
						dbPass = values[1];
						break;
					default:
						break;
				}
				line = br.readLine();
			}
			br.close();
			System.out.println("Datenbank-Logindaten aus Datei gelesen");
		} catch (FileNotFoundException e1) {
			System.out.println("Keine Daten für Datenbank gefunden - verwende Dummy-Daten");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("Versuche zur Datenbank zu verbinden");
	        Class.forName("com.mysql.jdbc.Driver");
	        con = DriverManager.getConnection("jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName,dbUser,dbPass);
		} catch (ClassNotFoundException e) {
	        System.out.println("Treiber nicht gefunden");
	    } catch (SQLException e) {
	        System.out.println("Verbindung nicht moglich");
	        System.out.println("SQLException: " + e.getMessage());
	        System.out.println("SQLState: " + e.getSQLState());
	        System.out.println("VendorError: " + e.getErrorCode());
	    }
		this.gds = gds;
		this.stepSize=stepSize;
		this.initDateTime = initTDateTime;
		this.days = daySelection;
		this.timeRange = timeRange;
		this.maxValuesInputWays = new HashMap<>();
		this.maxValuesCrossroad = new HashMap<>();
		this.maxValuesSensors = new HashMap<>();
		this.sensorModelNodes = new HashMap<>();
		this.crossroadModelNodes = new HashMap<>();
		this.inputWayConnections = new HashMap<>();
		this.inputWaysToID = new HashMap<>();
		this.inputWays= new HashMap<>();
		this.treshold = treshold;
		this.trafficUpdate = trafficUpdate;
		this.disabledEdges = new HashMap<>();
		this.disabledEdgesInputWay = new HashMap<>();
		this.dummyMax = dummyMax;
		getInputWays();
		getMaximalWeightCrossroad();
		getMaximalWeightInputWay();
		getMaximalWeightSensor();
		loadFromWays();
		
	}
	/**
	 * schreibt das maximale Gewicht für eine Kreuzung in die Tabelle mw_MaxValues_Crossroad,
	 * die Daten sind auf 1x Stepsize berechnet, ACHTUNG: Datenbankabfrage nicht mehr durchführbar, da zu viele Daten
	 * @param crossroadID
	 * @return
	 */
	public double[] writeMaximalWeightsCrossroad(int crossroadID) {
		System.out.println("Schreibe reale Maximalwerte (Dauer 1x stepSize) für Kreuzung mit ID " +crossroadID);
		if(crossroadID==43 || crossroadID == 44 || crossroadID == 63 || crossroadID == 99 || crossroadID == 147 || crossroadID == 93) {
			return new double[]{0.0,0.0};
		}
		String crossroadName = getCrossroadName(crossroadID);
		double count = 0;
		double load = 0;
		ResultSet rs;
		String selectStmt = null;
		try {
			String sensorsOfCrossroad = "SELECT ID AS SENSOR_ID, CSVOFFSET, REALNAME, CROSSROAD_ID FROM jee_crmodel_SensorDim S WHERE S.CROSSROAD_ID="+crossroadID;
			String sensorWaysOfCrossroad = "SELECT * FROM mw_SensorWays SW WHERE crossroadID = "+crossroadID+" AND wayID IS NOT NULL";
			String eventData = "SELECT DATETIME, ID AS EVENT_ID, cr_count AS COUNT_VALUE, cr_load AS LOAD_VALUE, RE.CSVOFFSET FROM jee_trafficlight_rawevents RE WHERE CROSSROAD = '"+crossroadName+"'";
			String crossroadWeight = "SELECT EVENT_ID,DATETIME,COUNT_VALUE, LOAD_VALUE,sensorName as SENSOR_NAME, sensorID as SENSOR_ID, SENSORS.CSVOFFSET, crossroadID as CROSSROAD_ID, crossroadName as CROSSROAD_NAME FROM (SELECT sensorID,sensorName, CSVOFFSET,crossroadID,crossroadName FROM ("+sensorsOfCrossroad+") SENSORS RIGHT JOIN ("+sensorWaysOfCrossroad+") SENSORS_MAPPED ON SENSORS.SENSOR_ID = SENSORS_MAPPED.sensorID) SENSORS LEFT JOIN ("+eventData+") EVENT_DATA on SENSORS.CSVOFFSET = EVENT_DATA.CSVOFFSET";
			selectStmt = "SELECT * FROM (SELECT SUM(COUNT_VALUE) AS ANZAHL, SUM(LOAD_VALUE)/COUNT(LOAD_VALUE) as BELEGUNG, DATETIME,CROSSROAD_ID, CROSSROAD_NAME FROM (SELECT CROSSROAD_WEIGHT.*,mw_SensorConnection.FRONT_BACK,mw_SensorConnection.FROM_DIRECTION, mw_SensorConnection.TO_LEFT,mw_SensorConnection.TO_STRAIGHT,mw_SensorConnection.TO_RIGHT FROM ("+crossroadWeight+") CROSSROAD_WEIGHT JOIN mw_SensorConnection ON CROSSROAD_WEIGHT.SENSOR_ID = mw_SensorConnection.SENSOR_ID) RESULT WHERE FRONT_BACK = 0  GROUP BY DATETIME ORDER BY ANZAHL DESC LIMIT 1) GROUPED";
			// COUNT
			Statement stmt = con.createStatement();
			rs= stmt.executeQuery(selectStmt);
			if(rs.first() && rs.getTime("DATETIME")!=null){
				String inserTableSQL = "REPLACE INTO mw_MaxValues_Crossroad VALUES (?,?,?,?,?,?,DEFAULT)";
				PreparedStatement insertStmt = con.prepareStatement(inserTableSQL);
				insertStmt.setInt(1, rs.getInt("CROSSROAD_ID"));
				insertStmt.setString(2, rs.getString("CROSSROAD_NAME"));
				insertStmt.setInt(3, rs.getInt("ANZAHL"));
				insertStmt.setInt(4, rs.getInt("BELEGUNG"));
				insertStmt.setTimestamp(5, rs.getTimestamp("DATETIME"));
				insertStmt.setInt(6, 0);
				System.out.println(insertStmt);
				insertStmt.executeUpdate();
				count = rs.getDouble("ANZAHL");
				return new double[]{count,load};
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException bei " +crossroadID+"\t\t"+selectStmt);
		}
		return new double[]{0.0,0.0};
	}
	
	/**
	 * aktualisiert die maximalen Wert der Kreuzung, sofern dies nötig ist
	 * - wird nur ausgeführt, wenn newMaxValues auf TRUE gesetzt ist 
	 * @param crossroadID - ID der Kreuzung
	 * @param count - neuer Count-Wert
	 * @param load - neuer Load-Wert
	 * @param time - Beginn der Aggregation
	 * @param timeRange - Zeitintervall der Aggregation
	 * @return
	 */
	public boolean setMaximalWeightsCrossroadImproved(int crossroadID, double count, double load, DateTime time, int timeRange){
		String inserTableSQL = "UPDATE mw_MaxValues_CrossroadImproved SET COUNT_VALUE = ?, LOAD_VALUE = ?, DATETIME = ?,timeRange = ? WHERE CROSSROAD_ID ="+crossroadID+" AND COUNT_VALUE<"+count;
		try {
			PreparedStatement insertStmt = con.prepareStatement(inserTableSQL);
			insertStmt.setDouble(1,count);
			insertStmt.setDouble(2, load);
			insertStmt.setTimestamp(3, Timestamp.valueOf(toSql(time)));
			insertStmt.setInt(4, timeRange*2);
			if(newMaxValues)
				insertStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Aktualisiert die maximalen Werte des Einfahrtsweges, sofern dies nötig ist 
	 * - wird nur ausgeführt, wenn newMaxValues auf TRUE gesetzt ist 
	 * @param inputWayID - globale ID des Einfahrtsweges
	 * @param count - neuer Count-Wert
	 * @param load - neuer Load-Wert
	 * @param time Beginn der Aggregation
	 * @param timeRange Zeitintervall der Aggregation
	 * @return
	 */
	public boolean setMaximalWeightsInputWayImproved(int inputWayID, double count, double load, DateTime time, int timeRange){
		String inserTableSQL = "UPDATE mw_MaxValues_InputWaysImproved SET COUNT_VALUE = ?, LOAD_VALUE = ?, DATETIME = ?, timeRange = ? WHERE INPUT_WAY_ID ="+inputWayID+" AND COUNT_VALUE<"+count;
		try {
			PreparedStatement insertStmt = con.prepareStatement(inserTableSQL);
			insertStmt.setDouble(1,count);
			insertStmt.setDouble(2, load);
			insertStmt.setTimestamp(3, Timestamp.valueOf(toSql(time)));
			insertStmt.setInt(4, timeRange*2);
			if(newMaxValues)
				insertStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * schreibt einen neuen Zufallswert für die Kreuzung in mw_MaxValues_Crossroad_Random
	 * @param crossroadID - globale ID der Kreuzung
	 * @param random - Count-Wert als Zufallswert
	 */
	public void writeMaximalWeightsCrossroadsRandom(int crossroadID,double random){
		java.sql.PreparedStatement stmt;
		try {
			String inserTableSQL = "REPLACE INTO mw_MaxValues_Crossroad_Random VALUES (?,?)";
			stmt = con.prepareStatement(inserTableSQL);
			stmt.setInt(1, crossroadID);
			stmt.setDouble(2, random);
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * schreibt das maximale Gewicht (nur Count) für einen Einfahrtsweg in die Tabelle mw_MaxValues_InputWays,
	 * die Daten sind auf 1x Stepsize berechnet, ACHTUNG: Datenbankabfrage nicht mehr durchführbar, da zu viele Daten
	 * @param inputwayID globale ID des Einfahrtsweges
	 * @return
	 */
	public double[] writeMaximalWeightsInputWays(int inputwayID) {
		System.out.println("Prüfe ID MaximalWeightsInputWays " +inputwayID);
		int count = 0;
		int load = 0;
		ResultSet rs;
		int[] inputData = inputWays.get(inputwayID);
		int crossroadID = inputData[1];
		System.out.println("InputWay:\t"+inputwayID+"CrossroadID:\t"+crossroadID);
		String selectStmt = null;
		try {
			selectStmt = "SELECT SUM(cr_count) as ANZAHL,SUM(cr_load)/COUNT(cr_load) AS BELEGUNG, DATETIME,SENSORS_ON_WAY.* FROM (SELECT * FROM (SELECT ID AS SENSOR_ID, REALNAME AS SENSOR_NAME, CSVOFFSET, wayID AS WAY_ID, CROSSROAD_ID, crossroadName AS CROSSROAD_NAME FROM (SELECT ID, CSVOFFSET, REALNAME, CROSSROAD_ID FROM jee_crmodel_SensorDim S WHERE S.CROSSROAD_ID='"+crossroadID+"') GLOBALSENSORS RIGHT JOIN (SELECT * FROM mw_SensorWays WHERE crossroadID = '"+crossroadID+"' AND wayID IS NOT NULL) SENSORS_MAPPED ON GLOBALSENSORS.ID = SENSORS_MAPPED.sensorID) SENSORS JOIN (SELECT * FROM mw_InputWaysGlobal IWG WHERE IWG.ID = '"+inputwayID+"') INPUT_WAYS ON SENSORS.WAY_ID = INPUT_WAYS.wayID) SENSORS_ON_WAY LEFT JOIN jee_trafficlight_rawevents RE ON RE.CSVOFFSET = SENSORS_ON_WAY.CSVOFFSET AND RE.CROSSROAD = SENSORS_ON_WAY.CROSSROAD_NAME GROUP BY DATETIME ORDER BY ANZAHL DESC LIMIT 1";
			// COUNT
			Statement stmt = con.createStatement();
			rs= stmt.executeQuery(selectStmt);
			if(rs.first() && rs.getTime("DATETIME")!=null){
				String inserTableSQL = "REPLACE INTO mw_MaxValues_InputWays VALUES (?,?,?,?,?,?,?,DEFAULT)";
				java.sql.PreparedStatement insertStmt = con.prepareStatement(inserTableSQL);
				insertStmt.setInt(1,inputwayID);
				insertStmt.setInt(2, rs.getInt("CROSSROAD_ID"));
				insertStmt.setString(3, rs.getString("CROSSROAD_NAME"));
				insertStmt.setInt(4, rs.getInt("ANZAHL"));
				insertStmt.setInt(5, rs.getInt("BELEGUNG"));
				insertStmt.setTimestamp(6, rs.getTimestamp("DATETIME"));
				insertStmt.setInt(7, 0);
				System.out.println(insertStmt);
				insertStmt.executeUpdate();
				count = rs.getInt("ANZAHL");
				return new double[]{count,load};
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException in writeMaximalWeightsInputWays mit ID"+ inputwayID+"\t\t"+selectStmt);
		}
		return new double[]{count,load};
	}
	
	/**
	 * berechnet das Gewicht (als CrossroadWeight-Objekt) für die Kreuzung
	 * @param crossroadID - globale ID der Kreuzung
	 * @param from - Startzeitpunkt
	 * @param to - Endzeitpunkt
	 * @param timestamp - Zeitstempel für den das Gewicht berechnet wird
	 * @return
	 */
	public CrossroadWeight getCrossroadWeight(int crossroadID, DateTime from, DateTime to,int timestamp) {
		String crossroadName = getCrossroadName(crossroadID);
		if(!maxValuesCrossroad.containsKey(crossroadID)){
			maxValuesCrossroad.put(crossroadID, getMaximalWeightCrossroad(crossroadID) );
		}
		CrossroadWeight crw = new CrossroadWeight(crossroadID, crossroadName,treshold);
		try {
			// Wählt alle Sensoren aus, die zur Kreuzung gehören
			String sensors = "SELECT ID as SENSOR_ID, CSVOFFSET, REALNAME, CROSSROAD_ID FROM jee_crmodel_SensorDim S WHERE S.CROSSROAD_ID='"+crossroadID+"'"; 
			// Wählt die Wege der Kreuzung aus, um die Sensoren darauf zu mappen
			String sensorWays = "SELECT * FROM mw_SensorWays SW WHERE crossroadID ='"+crossroadID+"' AND wayID IS NOT NULL";
			// Lädt die entsprechenden Sensordaten für die definierten Zeitraum
			String eventData = "SELECT ID as EVENT_ID,cr_count as COUNT_VALUE, cr_load as LOAD_VALUE, RE.CSVOFFSET,DATETIME FROM jee_trafficlight_rawevents RE WHERE RE.DATETIME >= '"+toSql(from)+"'AND RE.DATETIME < '"+toSql(to)+"' AND CROSSROAD = '"+crossroadName+"'";
			// Filtert aus den Sensordaten die Daten für die Sensoren raus
			String crossroadWeight ="SELECT EVENT_ID,DATETIME,COUNT_VALUE, LOAD_VALUE,sensorName as SENSOR_NAME, sensorID as SENSOR_ID, SENSORS.CSVOFFSET, crossroadID as CROSSROAD_ID, crossroadName as CROSSROAD_NAME FROM (SELECT sensorID,sensorName, CSVOFFSET,crossroadID,crossroadName FROM ("+sensors+") SENSORS RIGHT JOIN ("+sensorWays+") SENSORS_MAPPED ON SENSORS.SENSOR_ID = SENSORS_MAPPED.sensorID) SENSORS LEFT JOIN ("+eventData+") EVENT_DATA on SENSORS.CSVOFFSET = EVENT_DATA.CSVOFFSET";
			// Mapped die Sensoren auf die Richtung des Einfahrtsweges für die Gruppierung
			String resultData = "SELECT CROSSROAD_WEIGHT.*,mw_SensorConnection.FRONT_BACK,mw_SensorConnection.FROM_DIRECTION, mw_SensorConnection.TO_LEFT,mw_SensorConnection.TO_STRAIGHT,mw_SensorConnection.TO_RIGHT FROM ("+crossroadWeight+") CROSSROAD_WEIGHT JOIN mw_SensorConnection ON CROSSROAD_WEIGHT.SENSOR_ID = mw_SensorConnection.SENSOR_ID";
			// Kombiniert die ermittelten Sensorwert mit den Einfahrtswegen (aus OSM) der Kreuzung und summiert die Werte auf
			String selectStmt = "SELECT SUM(COUNT_VALUE)/COUNT(DISTINCT DATETIME) as ANZAHL, SUM(LOAD_VALUE)/COUNT(LOAD_VALUE) as BELEGUNG ,FROM_DIRECTION, OSMWAY_ID FROM ("+resultData+") RESULT LEFT JOIN (SELECT * FROM mw_CrossroadWays WHERE TYPE = '0')CRW on RESULT.FROM_DIRECTION = CRW.DIRECTION AND RESULT.CROSSROAD_ID = CRW.CROSSROAD_ID WHERE FRONT_BACK = 0 GROUP BY OSMWAY_ID"; 
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			System.out.println(selectStmt);
			while (rs.next()) {
				int wayID = rs.getInt("OSMWAY_ID");
				double[] maxValue =  getMaximalWeightInputWayFromMap(wayID,crossroadID); //Index 0 = MaxCount, Index 1 = MaxLoad
				if(maxValue == null){
					maxValue = new double[]{Double.MAX_VALUE,Double.MAX_VALUE};
				}
				crw.setMaxWeightWay(wayID, maxValue);
				double count = rs.getDouble("ANZAHL");
				double load = rs.getDouble("BELEGUNG");
				setMaximalWeightsInputWayImproved(getInputWay(crossroadID, wayID), count, load, from, timeRange);
				crw.addWeightWay(wayID, new double[]{count,load,(count/maxValue[0])*100});
				crw.setTimestamp(timestamp);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		crw.setMaxWeight(maxValuesCrossroad.get(crossroadID));
		crossroadModelNodes.put(crossroadID, crw);
		return crw;
	}
	
	/**
	 * berechnet das synthetische InitialGewicht im Simulationsmodus, beinhaltet eine DB-Abfrage zur Erstellung des CrossroadWeight-Objekts
	 * @param crossroadID, globale KreuzungsID
	 * @param from Startzeitpunkt für DB-Abfrage
	 * @param to Entzeitpunkt für DB-Abfrage
	 * @param timestamp, Zeitstempel für die Definition im CrossroadWeightObjekt
	 * @param trafficUpdate, beinhaltet die synthetischen Daten
	 * @return
	 */
	public CrossroadWeight getCrossroadWeightStaticInit(int crossroadID,DateTime from, DateTime to, int timestamp,TrafficUpdate trafficUpdate) {
		CrossroadWeight crw = getCrossroadWeight(crossroadID, from, to,timestamp);
		crw.resetInputWayWeight(trafficUpdate.getInitCount(), trafficUpdate.getInitLoad());
		return crw;
	}
	
	/**
	 * berechnet das synthetische Gewicht im Simulationsmodus für fortlaufende Batches
	 * @param crossroadID gloable KreuzungsID
	 * @param update beinhaltet die synthetischen Daten
	 * @return
	 */
	public CrossroadWeight getCrossroadWeightStaticBatch(int crossroadID,TrafficUpdate update) {
		CrossroadWeight crw = crossroadModelNodes.get(crossroadID);
		crw.setTimestamp(crw.getTimestamp()+1);
		if(update.isAffected(crossroadID)){
			crw.resetInputWayWeight((update.getSleepTillUpdate()*update.getInitCount()+update.getUpdateCount())/(update.getSleepTillUpdate()+1), (update.getSleepTillUpdate()*update.getInitLoad()+update.getUpdateLoad())/(update.getSleepTillUpdate()+1));
		}
		return crw;
	}
	
	/**
	 * liest das maximale Gewicht aus der Tabelle mw_MaxValues_Crossroad (auf 1xTimestep berechnet)
	 * @param crossroadID
	 * @return
	 */
	public double[] getMaximalWeightCrossroad(int crossroadID) {
		try {
			String selectStmt = "SELECT * FROM mw_MaxValues_Crossroad WHERE CROSSROAD_ID = "+crossroadID+" AND COUNT_OR_LOAD =0";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			if(rs.first()){
				return new double[]{rs.getDouble("COUNT"),0};
			}
			else
				return writeMaximalWeightsCrossroad(crossroadID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * liest Maximalwerte des Einfahrtsweges aus der HashMap (zwischengespeichert nach DB-Abfrage)
	 * @param wayID
	 * @param crossroadID
	 * @return
	 */
	private double[] getMaximalWeightInputWayFromMap(int wayID,int crossroadID){
		int inputWayID = getInputWay(crossroadID, wayID);
		double[] result = maxValuesInputWays.get(inputWayID);
		return result;
	}
	
	/**
	 * liest den Maximalwert für einen Einfahrtsweg direkt aus der Datenbank (aus mw_MaxValues_InputWays)
	 * @param osmWayID
	 * @param crossroadRoad
	 * @return
	 */
	private double[] getMaximalWeightInputWay(int inputWayID) {
		try {
			String selectStmt = "SELECT * FROM mw_MaxValues_InputWays WHERE INPUT_WAY_ID = "+inputWayID+" AND COUNT_OR_LOAD =0";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			if(rs.first()){
				return new double[]{rs.getDouble("COUNT"),0};
			}
			else
				return writeMaximalWeightsInputWays(inputWayID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * liest den Maximalwert für einen Einfahrtsweg direkt aus der Datenbank
	 * @param osmWayID
	 * @param crossroadRoad
	 * @return
	 */
	private double[] getMaximalWeightInputWay(int osmWayID, int crossroadRoad) {
		try {
			String selectStmt = null;
			if(backupWays)
				selectStmt = "SELECT IW.*,wayID  FROM mw_MaxValues_InputWays IW LEFT JOIN mw_InputWaysGlobal_bak2 IWG ON IW.INPUT_WAY_ID = IWG.ID WHERE wayID ='"+osmWayID+"' AND CROSSROAD_ID ="+crossroadRoad;
			else
				selectStmt = "SELECT IW.*,wayID  FROM mw_MaxValues_InputWays IW LEFT JOIN mw_InputWaysGlobal IWG ON IW.INPUT_WAY_ID = IWG.ID WHERE wayID ='"+osmWayID+"' AND CROSSROAD_ID ="+crossroadRoad;
			Statement stmt = con.createStatement();
			if(crossroadRoad == 8)
				System.out.println(selectStmt);
			ResultSet rs = stmt.executeQuery(selectStmt);
			if(rs.first()){
				return new double[]{rs.getDouble("COUNT"),0};
			}
			else
				return new double[]{100,100}; //TODO mit osmWay und Crossroad
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * liest die Maximalwerte der Einfahrtswege aus der, durch die Parameter festgelegten Tabelle
	 */
	public void getMaximalWeightInputWay() {
		try {
			String selectStmt = null;
			if(improvedMax){
				selectStmt = "SELECT * FROM mw_MaxValues_InputWaysImproved WHERE COUNT_OR_LOAD =0";
			}
			else if(!dummyMax)
				selectStmt = "SELECT * FROM mw_MaxValues_InputWays WHERE COUNT_OR_LOAD =0";
			else {
				if(backupWays)
					selectStmt = "SELECT * FROM mw_MaxValues_InputWay_Random JOIN mw_InputWaysGlobal_bak2 on INPUTWAY_ID = ID";
				else
					selectStmt = "SELECT * FROM mw_MaxValues_InputWay_Random JOIN mw_InputWaysGlobal on INPUTWAY_ID = ID";
			}
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			while(rs.next()) {
				if(improvedMax){
					double maxCount = rs.getDouble("COUNT_VALUE");
					if(maxCount <1)
						maxCount= 1;
					maxValuesInputWays.put(rs.getInt(1), new double[]{maxCount,rs.getDouble("LOAD_VALUE")});
				}
				else if(!dummyMax)
					maxValuesInputWays.put(rs.getInt(1), new double[]{rs.getDouble("COUNT"),rs.getDouble("LOAD")});
				else{
					maxValuesInputWays.put(rs.getInt("INPUTWAY_ID"), new double[]{rs.getDouble("MAX_COUNT"),0});
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * schreibt maximale Werte für die Einfahrtswege einer Kreuzung, die Werte orientieren sich dabei an den Maximalwerten der Kreuzung
	 */
	public void writeMaximalWeightInputWayRandom() {
		Random rand = new Random();
		try {
			String selectStmt = "SELECT ID, crossroadID,MAX_COUNT as MAX_COUNT_CROSSROAD FROM mw_InputWaysGlobal IWG LEFT JOIN mw_MaxValues_Crossroad_Random MAX_C_RANDOM ON IWG.crossroadID = MAX_C_RANDOM.CROSSROAD_ID";
			Statement stmt = con.createStatement();
			String insertString = "REPLACE INTO mw_MaxValues_InputWay_Random VALUES (?,?)";
			java.sql.PreparedStatement insertStmt = con.prepareStatement(insertString);
			ResultSet rs = stmt.executeQuery(selectStmt);
			while(rs.next()) {
				double randMax = rand.nextDouble()*(rs.getDouble("MAX_COUNT_CROSSROAD")/2)+rs.getDouble("MAX_COUNT_CROSSROAD")/4;
				insertStmt.setInt(1, rs.getInt("ID"));
				insertStmt.setDouble(2, randMax);
				insertStmt.addBatch();
			}
			insertStmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * schreibt maximale Werte für die realen Sensoren in die Datenbank, welche sich grob an den Werten für den passenden virtuellen Sensor orientieren
	 */
	public void writeMaximalWeightSensorRandom() {
		Random rand = new Random();
		try {
			String selectStmt = "SELECT NODE_ID as REALSENSOR_INDEX , R2.* FROM (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 0) R3 LEFT JOIN (SELECT R1.*,MAX_COUNT FROM (SELECT INPUTWAY_INDEX, ANZAHL_SENSORS,WAY_ID,CROSSROAD_ID,DIRECTION, ID as INPUTWAY_ID_GLOBAL FROM (SELECT NODE_ID as INPUTWAY_INDEX, COUNT(VS) as ANZAHL_SENSORS,WAY_ID,CROSSROAD_ID,DIRECTION FROM (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 1) WAYS LEFT JOIN (SELECT SG2.NODE_ID as VS, SG1.NODE_ID as RS FROM (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 0) SG1 JOIN (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 1) SG2 ON SG1.WAY_ID = SG2.WAY_ID AND SG1.CROSSROAD_ID = SG2.CROSSROAD_ID AND SG1.DIRECTION = SG2.DIRECTION) VS_TO_RS ON WAYS.NODE_ID = VS_TO_RS.VS GROUP BY VS) ANZAHL LEFT JOIN mw_InputWaysGlobal IWG ON ANZAHL.WAY_ID = IWG.wayID AND ANZAHL.CROSSROAD_ID = IWG.crossroadID) R1 LEFT JOIN mw_MaxValues_InputWay_Random MWIWR ON R1.INPUTWAY_ID_GLOBAL=MWIWR.INPUTWAY_ID) R2 ON R3.WAY_ID = R2.WAY_ID AND R3.CROSSROAD_ID = R2.CROSSROAD_ID AND R3.DIRECTIOn = R2.DIRECTION";
			Statement stmt = con.createStatement();
			String insertString = "REPLACE INTO mw_MaxValues_Sensor_Random VALUES (?,?)";
			java.sql.PreparedStatement insertStmt = con.prepareStatement(insertString);
			ResultSet rs = stmt.executeQuery(selectStmt);
			while(rs.next()) {
				double randMax = rand.nextDouble()*(rs.getDouble("MAX_COUNT")/rs.getDouble("ANZAHL_SENSORS"))+2;
				if(rs.getDouble("MAX_COUNT")==0)
					randMax = rand.nextDouble()*5+2;
				insertStmt.setInt(1, rs.getInt("REALSENSOR_INDEX"));
				insertStmt.setDouble(2, randMax);
				insertStmt.addBatch();
			}
			insertStmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	
	
	/**
	 * liest die maximalen Werte, welche für einen Kreuzung gemessen wurden - über dummyMax können einmalig zufällig generierte Werte geladen werden
	 */
	public void getMaximalWeightCrossroad() {
		try {
			String selectStmt;
			if(dummyMax)
				selectStmt = "SELECT * FROM mw_MaxValues_Crossroad_Random";
			else
				selectStmt = "SELECT * FROM mw_MaxValues_CrossroadImproved";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			while(rs.next()){
				if(dummyMax)
					maxValuesCrossroad.put(rs.getInt("CROSSROAD_ID"), new double[] {rs.getDouble("MAX_COUNT"),0});
				else{
					double maxCount = rs.getDouble("COUNT_VALUE");
					if(maxCount <1)
						maxCount = 1;
					maxValuesCrossroad.put(rs.getInt("CROSSROAD_ID"), new double[] {maxCount,rs.getDouble("LOAD_VALUE")});

				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * liest die maximalen Werte, welche für einen realen Sensor gemessen wurden - über dummyMax können einmalig zufällig generierte Werte geladen werden
	 */
	public void getMaximalWeightSensor() {
		Random rand = new Random();
		try {
			String selectStmt = "SELECT * FROM mw_MaxValues_Sensor_Random";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			while(rs.next()){
				maxValuesSensors.put(rs.getInt("SENSOR_ID"), new double[] {rs.getDouble("MAX_COUNT"),0});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	public int setFromID(int id) {
		return setID(id,"from");
	}
	public int setToID(int id) {
		return setID(id,"to");
	}
	public int setID(int id,String mode) {
		
		try {
			String selectStmtString = "SELECT * FROM mw_InputWaysGlobal WHERE ID = "+ id;
			Statement selectStmt = con.createStatement();
			ResultSet rs = selectStmt.executeQuery(selectStmtString);
			rs.first();
			String updateString = "UPDATE mw_InputWayConnection A SET "+mode+"ID = "+id+" WHERE A."+mode+"Way = "+rs.getInt(2) +" AND A."+mode+"Crossroad = " +rs.getInt(3);
			Statement updateStmt = con.createStatement();
			return updateStmt.executeUpdate(updateString);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  0;
	}
	
	
	public List<EdgeContainer> getCrossroadConnection() {
		List<EdgeContainer> connection = new ArrayList<>();
		try {
			String selectStmt = "SELECT FROM_CROSSROAD,TO_CROSSROAD FROM mw_CrossroadConnection";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			int c = 0;
			while(rs.next()) {
				connection.add(new EdgeContainer(rs.getInt(1),rs.getInt(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * alle InputWays einer Kreuzung
	 * @param crossroadID
	 * @return
	 */
	public HashMap<CardinalDirection, Integer> getInputWays(int crossroadID) {
		return getWays(crossroadID,0);
	}

	/**
	 * liefert eine Liste von Ausfahrtswegen, von denen der übergebene Einfahrtsweg direkt zu erreichen ist
	 * @param crossroadID
	 * @param toWay
	 * @return
	 */
	public List<int[]> getFromWays(int crossroadID, int toWay) {
		return inputWayConnections.get(new InputWay(toWay, crossroadID));
		
	}
	public void loadFromWays(){
		InputWay key;
		int[] value;
		try {
			String selectStmt = "SELECT FROM_CROSSROAD,FROM_WAY,TO_CROSSROAD,TO_WAY FROM mw_CrossroadConnection";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			while(rs.next()) {
				key = new InputWay(rs.getInt("TO_WAY"),rs.getInt("TO_CROSSROAD"));
				value = new int[]{rs.getInt("FROM_CROSSROAD"), rs.getInt("FROM_WAY")};
				if(!this.inputWayConnections.containsKey(key)){
					this.inputWayConnections.put(key,new ArrayList<int[]>());
				}
				this.inputWayConnections.get(key).add(value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * alle Output-Ways einer Kreuzung
	 * @param crossroadID
	 * @return
	 */
	public HashMap<CardinalDirection, Integer> getOutputWays(int crossroadID) {
		return getWays(crossroadID,1);
	}

	private HashMap<CardinalDirection, Integer> getWays(int crossroadID, int type) {
		HashMap<CardinalDirection, Integer> ways = new HashMap<>();
		try {
			Statement stmt = con.createStatement();
			String statementString = "SELECT DIRECTION , OSMWAY_ID FROM mw_CrossroadWays WHERE CROSSROAD_ID ="+crossroadID+" AND TYPE =" +type;
			ResultSet rs = stmt.executeQuery(statementString);
			while(rs.next()){
				ways.put(CardinalDirection.valueOf(rs.getString("DIRECTION")), rs.getInt("OSMWAY_ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ways;
	}
	
	public List<Sensor> getSensors(int crossroadID, int wayID) {
		List<Sensor> sensors = new ArrayList<>();
		try {
			Statement stmt = con.createStatement();
			String statementString = "SELECT * FROM mw_SensorWays WHERE crossroadID ="+crossroadID+" AND wayID =" +wayID;
			ResultSet rs = stmt.executeQuery(statementString);
			while(rs.next()){
				sensors.add(new Sensor(rs.getInt(1),rs.getString(2),rs.getInt(3),rs.getString(4),rs.getInt(5)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sensors;
	}
	public Sensor getSensor(int sensorID) {
		Set<CardinalDirection> directions= new HashSet<>();
		try {
			Statement stmt = con.createStatement();
			String statementString = "SELECT * FROM mw_SensorConnection WHERE SENSOR_ID = "+sensorID;
			ResultSet rs = stmt.executeQuery(statementString);
			if(rs.first()) {
				if(rs.getString("TO_LEFT")!=null) {
					directions.add(CardinalDirection.valueOf(rs.getString("TO_LEFT")));
				}
				if(rs.getString("TO_STRAIGHT")!=null) {
					directions.add(CardinalDirection.valueOf(rs.getString("TO_STRAIGHT")));
				}
				if(rs.getString("TO_RIGHT")!=null) {
					directions.add(CardinalDirection.valueOf(rs.getString("TO_RIGHT")));
				}
				return new Sensor(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getInt(5),directions);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * liefert alle Outputways zu einem Sensor
	 * @param sensorID
	 * @param crossroadID
	 * @return
	 */
	public HashMap<String, Integer> getOutputWays(int sensorID, int crossroadID) {
		HashMap<String, Integer> outputDirection = new HashMap<>();
		try {
			String[] direction = new String[] {"LEFT","STRAIGHT", "RIGHT"};
			Statement stmt = con.createStatement();
			int wayID;
			String statementString;
			for (int i = 0; i < direction.length; i++) {
				statementString = "SELECT SC.CROSSROAD_ID, SC.CROSSROAD_NAME, SC.SENSOR_ID, SC.SENSOR_NAME, SC.FROM_WAY, SC.FROM_DIRECTION, SC.TO_LEFT, CW.OSMWAY_ID as OUT_WAYID, CW.DIRECTION as OUTDIRECTION FROM mw_SensorConnection SC , mw_CrossroadWays CW WHERE SC.CROSSROAD_ID = CW.CROSSROAD_ID AND SC.TO_"+direction[i]+" = CW.DIRECTION AND SC.CROSSROAD_ID = "+crossroadID+" AND CW.TYPE=1 AND SC.SENSOR_ID = "+sensorID;
				ResultSet rs = stmt.executeQuery(statementString);
				while(rs.next() ) {
					wayID = rs.getInt("OUT_WAYID");
					if( wayID >0 )
					outputDirection.put(direction[i],wayID);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return outputDirection;
	}
	/**
	 * liefert alle Einfahrtswege, die mit dem übergebenen Ausfahrtsweg verbunden sind
	 * @param outputOSM, OSM-ID des Weges
	 * @param outputCrossroad, ID der Kreuzung, zu der der Weg gehört
	 * @return
	 */
	public List<InputWay> getConnectedInputWays(int outputOSM, int outputCrossroad) {
		List<InputWay> connections = new ArrayList<>();
		try {
			Statement stmt = con.createStatement();
			int wayID;
			String statementString;
			statementString = "SELECT TO_CROSSROAD,TO_WAY FROM mw_CrossroadConnection CC WHERE CC.FROM_WAY = "+outputOSM+" AND CC.FROM_CROSSROAD = " +outputCrossroad;
			ResultSet rs = stmt.executeQuery(statementString);
			while(rs.next() ) {
				wayID = rs.getInt("TO_WAY");
				if( wayID >0 ) {
					connections.add(new InputWay(rs.getInt(1), rs.getInt(2)));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connections;
	}
	
	public List<Integer> getSensorIDs(int crossroadID) {
		List<Integer> sensors = new ArrayList<>();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT SENSOR_ID FROM mw_SensorConnection WHERE CROSSROAD_ID = "+crossroadID);
			while(rs.next()) {
				sensors.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sensors;
	}
	
	public void getInputWays(){
		try {
			Statement stmt = con.createStatement();
			String statementString;
			if(backupWays)
				statementString = "SELECT * FROM mw_InputWaysGlobal_bak2"; 
			else
				statementString = "SELECT * FROM mw_InputWaysGlobal";
			ResultSet rs = stmt.executeQuery(statementString);
			while(rs.next() ) {
				int wayID = rs.getInt("wayID");
				int crossroadID = rs.getInt("crossroadID");
				inputWays.put(rs.getInt("ID"), new int[]{wayID,crossroadID});
				inputWaysToID.put(new InputWay(wayID,crossroadID), rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//TODO: Datenbankabfrage ändern
	public int getInputWay(int crossroadID,int wayID){
		InputWay key = new InputWay(wayID, crossroadID);
		if(inputWaysToID.containsKey(key)){
			return inputWaysToID.get(key);
		}
		else{
			return -1;
		}
	}
	/**
	 * 
	 * @return
	 */
	public List<INode> getInputWaysForDNA() {
		List<INode> nodes = new ArrayList<INode>();
		Node currentWeighted = null;
		try {
			Statement stmt = con.createStatement();
			String statementString;
			if(backupWays)
				statementString = "SELECT * FROM mw_InputWaysGlobal_bak2"; 
			else
				statementString = "SELECT * FROM mw_InputWaysGlobal"; 
			ResultSet rs = stmt.executeQuery(statementString);
			int i = 0;
			while(rs.next() ) {
				currentWeighted = gds.newNodeInstance(rs.getInt(1));
				nodes.add(currentWeighted);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nodes;
	}
	
	public List<EdgeContainer> getInputWaysConnectionForDNA() {
		List<EdgeContainer> edges = new ArrayList<>();
		try {
			Statement stmt = con.createStatement();
			String statementString;
			if(backupWays)
				statementString = "SELECT fromID , toID FROM mw_InputWayConnection_bak"; 
			else
				statementString = "SELECT fromID , toID FROM mw_InputWayConnection_bak3"; 
			ResultSet rs = stmt.executeQuery(statementString);
			int i = 0;
			while(rs.next() ) {
				edges.add(new EdgeContainer(rs.getInt("fromID"),rs.getInt("toID")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return edges;
	}
	
	
	/**
	 * liest alle Knoten für das Kreuzungsmodell aus der Datenbank
	 * @return Knotenliste
	 */
	public List<INode> getCrossroadsForDNA() {
		if(timeRange==0){
			System.out.println("TimeRange ist 0, setze auf 1");
			timeRange=1;
		}
		
		List<INode> nodes = new ArrayList<INode>();
		Node current = null;
		
		try {
			System.out.println("Lade Kreuzungen aus Datenbank...");
			Statement stmt = con.createStatement();
			String statementString;
			statementString = "SELECT * FROM ((SELECT DISTINCT FROM_CROSSROAD as CROSSROAD FROM mw_CrossroadConnection) UNION (SELECT DISTINCT TO_CROSSROAD as CROSSROAD FROM mw_CrossroadConnection)) V"; 
			ResultSet rs = stmt.executeQuery(statementString);
			
			while(rs.next() ) {
				int label = rs.getInt(1);	
				current= gds.newNodeInstance(label);
				nodes.add(current);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nodes;
	}
	
	
	public int writeConnections(Set<InputWayConnection> connections) {
		java.sql.PreparedStatement stmt;
		try {
			String inserTableSQL = "INSERT IGNORE INTO mw_InputWayConnection VALUES (?,?,?,?,?,?,DEFAULT)";
			stmt = con.prepareStatement(inserTableSQL);
			for (InputWayConnection connection : connections) {
				stmt.setInt(1, connection.fromWayID);
				stmt.setInt(2, connection.fromCrossroad);
				stmt.setString(3, getCrossroadName(connection.fromCrossroad));
				stmt.setInt(4, connection.toWayID);
				stmt.setInt(5, connection.toCrossroad);
				stmt.setString(6, getCrossroadName(connection.toCrossroad));
				stmt.addBatch();
			}
			stmt.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connections.size();
	}
	
	
	public Crossroad innerconnectionForCrossroad(int crossroadID){
		HashMap<CardinalDirection, Integer> inputWays = getInputWays(crossroadID);
		if(inputWays.size()==0){
			System.out.println("Keine Einfahrtswege");
			return null;
		}
		Crossroad crossroad = new Crossroad(crossroadID,this);
		crossroad.setInputWays(inputWays);
		HashMap<CardinalDirection, Integer> outPut = getOutputWays(crossroadID);
		crossroad.setOutputWays(outPut);
		HashMap<CardinalDirection, Integer> outputWays;
	
		for (Map.Entry<CardinalDirection,Integer> way : inputWays.entrySet()) {
			outputWays = getConnectedOutputWays(crossroadID, way.getValue(), way.getKey());
			if(outputWays==null) {
				continue;
			}
			for (Map.Entry<CardinalDirection, Integer> outputWay : outputWays.entrySet()) {
				crossroad.setOutputWay(way.getKey(), outputWay.getKey());
			}
		}
		return crossroad;
	}
	
	/**
	 * liefert für einen InputWay alle OuputWays die innerhalb der Kreuzung zu erreichen sind
	 * @param crossroadID
	 * @param wayID
	 * @param wayDirection
	 * @return
	 */
	public HashMap<CardinalDirection, Integer> getConnectedOutputWays(int crossroadID, int wayID, CardinalDirection wayDirection) {
		List<Sensor> sensors = getSensors(crossroadID, wayID);
		HashMap<CardinalDirection,Integer> outputWaysCrossroad = new HashMap<>();
		HashMap<String, Integer> outputWaysSensor;
		CardinalDirection cd;
		for (Sensor sensor : sensors) {
			outputWaysSensor = getOutputWays(sensor.sensorID, crossroadID);
			for (Map.Entry<String, Integer> integer : outputWaysSensor.entrySet()) {
				cd = transposeDirection(wayDirection, integer.getKey());
				if(!outputWaysCrossroad.containsKey(cd))
					outputWaysCrossroad.put(cd, integer.getValue());
			}
		}
		return (outputWaysCrossroad.size()>0) ? outputWaysCrossroad : null;
	}
	
	/**
	 * übersetzt InputWay-Direction und Richtung, in eine OutputWay-Direction
	 * @param inDir
	 * @param goDir
	 * @return
	 */
	public CardinalDirection transposeDirection (CardinalDirection inDir, String goDir) {
		String[] directions = new String[]{"LEFT","STRAIGHT"};
		switch (inDir) {
		case NORTH:
			if(goDir.equals(directions[0]))
				return CardinalDirection.EAST;	
			else if(goDir.equals(directions[1]))
				return CardinalDirection.SOUTH;
			else
				return  CardinalDirection.WEST;
		case EAST:
			if(goDir.equals(directions[0]))
				return CardinalDirection.SOUTH;	
			else if(goDir.equals(directions[1]))
				return CardinalDirection.WEST;
			else
				return CardinalDirection.NORTH;
		case SOUTH:
			if(goDir.equals(directions[0]))
				return CardinalDirection.WEST;	
			else if(goDir.equals(directions[1]))
				return CardinalDirection.NORTH;
			else
				return  CardinalDirection.EAST;
		default:
			if(goDir.equals(directions[0]))
				return CardinalDirection.NORTH;	
			else if(goDir.equals(directions[1]))
				return CardinalDirection.EAST;
			else
				return  CardinalDirection.SOUTH;
		}
	}
	
	/**
	 * liefert den Name den Kreuzung mit übergebener ID in der Datenbank
	 * @param crossroadID
	 * @return
	 */
	public String getCrossroadName(int crossroadID) {
		String selectStmt = "SELECT REALNAME FROM jee_crmodel_CrossroadDim WHERE ID = " + crossroadID;
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectStmt);
			while(rs.next()){
				return rs.getString("REALNAME");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Not Found";
	}
	
	/**
	 * wandelt ein DateTime in eine SQL-konforme Stringrepräsentation um.
	 * @param dateTime
	 * @return
	 */
	public static String toSql(DateTime dateTime) {
	    return new Timestamp( dateTime.getMillis() ).toString();
	}
	
	/**
	 * schließt die Verbindung zur Datenbank
	 */
	public void disconnect() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * liefert die Knoten für das SensorModell
	 * @return
	 */
	public List<INode> getSensorsForDNA() {
		System.out.println("Erstelle den Graph ... ");
		List<INode> nodes = new ArrayList<INode>();
		try {
			Statement stmt = con.createStatement();
			String statementString;
			statementString = "SELECT SG.*, ID as INPUT_WAY_ID FROM (SELECT * FROM mw_SensorGlobal_bak) SG LEFT JOIN (SELECT * FROM mw_InputWaysGlobal) IWG ON SG.WAY_ID = IWG.wayID AND SG.CROSSROAD_ID = IWG.crossroadID"; 
			ResultSet rs = stmt.executeQuery(statementString);
			int label;
			while (rs.next()) {
				label = rs.getInt("NODE_ID");
				nodes.add(gds.newNodeInstance(label));
				sensorModelNodes.put(label, new SensorModelNode(label, !rs.getBoolean("SENSOR_TYPE"),rs.getInt("INPUT_WAY_ID"),CardinalDirection.valueOf(rs.getString("DIRECTION"))));
			}
			/*double[] weight = null;
			DateTime from = null;
			DateTime to = null;
			if(modus==0){
				from = initDateTime;
				to = initDateTime.plusMinutes(stepSize);
			}
			else if (modus == 1){
				from = initDateTime.minusMinutes(timeRange);
				to = initDateTime.plusMinutes(timeRange);
			}
			getSensorWeights(from, to,0);
			while(rs.next() ) {
				int label = rs.getInt("NODE_ID");
				if(rs.getBoolean("SENSOR_TYPE")){
					int id = getInputWay(rs.getInt("CROSSROAD_ID"),rs.getInt("WAY_ID"));
					if(id>0){
						weight = getInputWayWeight(id,initDateTime, initDateTime.plusMinutes(stepSize));
					}
					else {
						weight = new double[]{-2,-2,-2};
					}
					sensorModelNodes.put(label, new SensorModelNode(label,false, weight, 0,id));
				}
				else {
					weight = getSensorNodeWeight(label,0,from,to);	
				}
				current = gds.newNodeInstance(label);
				currentWeighted = (current instanceof DirectedWeightedNode) ? (DirectedWeightedNode) current : null;
				if(currentWeighted!=null) {
					currentWeighted.setWeight(new Double3dWeight(weight[0],weight[1],weight[2]));
					nodes.add(currentWeighted);
				}
			}*/
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nodes;
	}
	
	public double[] getSensorModelWeight(int nodeID,DateTime from, DateTime to,int timestemp){
		if(sensorModelNodes.containsKey(nodeID)){
			SensorModelNode sensorModelNode = sensorModelNodes.get(nodeID);
			if(sensorModelNode.isReal()){
				if(sensorModelNode.getTimestep()==timestemp){
					return sensorModelNode.getWeight();
				}
				else{
					System.out.println("Not found");
					double[] weight = new double[]{0,0,0};
					sensorModelNode.setWeight(weight, timestemp);
					return weight;
				}
			}
			else {
				int inputWayID = sensorModelNode.getInputWayID();
				if(inputWayID>0){
					double[] weight = getInputWayWeight(inputWayID, from, to);
					sensorModelNode.setWeight(weight, timestemp);
					return weight;
				}
				return new double[]{0,0,0};
			}
		}
		else
			return new double[]{0,0,0};
	}
	
	/**
	 * liefert die Gewichte für den Einfahrtsweg
	 * @param inputWayID
	 * @param timestamp
	 * @return Index 0: count/maxCount, Index 1: load
	 */
	public double[] getInputWayWeight(int inputWayID, DateTime timestamp) {
		return getInputWayWeight(inputWayID, timestamp, timestamp.plusMinutes(5));
	}
	
	/**
	 * liefert die Gewichte für den Einfahrtsweg
	 * @param inputWayID
	 * @param timestamp
	 * @return Index 0: count/maxCount, Index 1: load
	 */
	public double[] getInputWayWeight(int inputWayID, DateTime from, DateTime to) {
		if(from.equals(to)){
			to = from.plusMinutes(1);
		}
		double count =0;
		double load = 0;
		if(!maxValuesInputWays.containsKey(inputWayID)){
			maxValuesInputWays.put(inputWayID, getMaximalWeightInputWay(inputWayID) );
		}
		String statementString = null;
		try {
			int[] inputData = inputWays.get(inputWayID);
			int crossroadID = inputData[1];
			Statement stmt = con.createStatement();
			if(backupWays)
				statementString = "SELECT SUM(ANZAHL)/COUNT(ANZAHL),SUM(BELEGUNG)/COUNT(BELEGUNG) FROM (SELECT SUM(cr_count) as ANZAHL, SUM(cr_load)/COUNT(cr_load) as BELEGUNG, DATETIME FROM (SELECT SENSORS.*,DATETIME,cr_count,cr_load FROM (SELECT S1.*, CSVOFFSET FROM (SELECT ID as INPUTWAY_ID, IWG.wayID as OSMWAY_ID, IWG.crossroadID as CROSSROAD_ID, crossroadName as CROSSROAD_NAME, sensorID as SENSOR_ID, sensorName as SENSOR_NAME,IWG.direction as DIRECTION FROM (SELECT * FROM mw_InputWaysGlobal IWG WHERE IWG.ID ="+inputWayID+") IWG LEFT JOIN mw_SensorWays SW ON IWG.wayID = SW.wayID AND IWG.crossroadID= SW.crossroadID AND IWG.direction = SW.direction) S1 JOIN (SELECT * FROM jee_crmodel_SensorDim SENSOR_DIM WHERE CROSSROAD_ID ="+crossroadID+") S2 ON S1.SENSOR_ID = S2.ID) SENSORS LEFT JOIN (SELECT * FROM jee_trafficlight_rawevents RE WHERE DATETIME  < '"+toSql(to)+"' AND DATETIME>='"+toSql(from)+"') EVENTS_DAY ON SENSORS.CROSSROAD_NAME = EVENTS_DAY.CROSSROAD AND SENSORS.CSVOFFSET=EVENTS_DAY.CSVOFFSET) FINAL GROUP BY DATETIME) GROUPED";
			else{
				statementString = "SELECT SUM(ANZAHL)/COUNT(ANZAHL),SUM(BELEGUNG)/COUNT(BELEGUNG) FROM (SELECT SUM(cr_count) as ANZAHL, SUM(cr_load)/COUNT(cr_load) as BELEGUNG, DATETIME FROM (SELECT SENSORS.*,DATETIME,cr_count,cr_load FROM (SELECT S1.*, CSVOFFSET FROM (SELECT ID as INPUTWAY_ID, IWG.wayID as OSMWAY_ID, IWG.crossroadID as CROSSROAD_ID, crossroadName as CROSSROAD_NAME, sensorID as SENSOR_ID, sensorName as SENSOR_NAME FROM (SELECT * FROM mw_InputWaysGlobal_bak2 IWG WHERE IWG.ID ="+inputWayID+") IWG LEFT JOIN mw_SensorWays_bak SW ON IWG.wayID = SW.wayID AND IWG.crossroadID= SW.crossroadID) S1 JOIN (SELECT * FROM jee_crmodel_SensorDim SENSOR_DIM WHERE CROSSROAD_ID ="+crossroadID+") S2 ON S1.SENSOR_ID = S2.ID) SENSORS LEFT JOIN (SELECT * FROM jee_trafficlight_rawevents RE WHERE DATETIME  < '"+toSql(to)+"' AND DATETIME>='"+toSql(from)+"') EVENTS_DAY ON SENSORS.CROSSROAD_NAME = EVENTS_DAY.CROSSROAD AND SENSORS.CSVOFFSET=EVENTS_DAY.CSVOFFSET) FINAL GROUP BY DATETIME) GROUPED";
				statementString = "SELECT SUM(ANZAHL)/COUNT(ANZAHL),SUM(BELEGUNG)/COUNT(BELEGUNG) FROM (SELECT SUM(cr_count) as ANZAHL, SUM(cr_load)/COUNT(cr_load) as BELEGUNG, DATETIME FROM (SELECT SENSORS.*,DATETIME,cr_count,cr_load FROM (SELECT FRONT_AND_BACK.* FROM (SELECT S1.*, CSVOFFSET FROM (SELECT ID AS INPUTWAY_ID, IWG.wayID AS OSMWAY_ID, IWG.crossroadID AS CROSSROAD_ID, crossroadName AS CROSSROAD_NAME, sensorID AS SENSOR_ID, sensorName AS SENSOR_NAME FROM (SELECT * FROM mw_InputWaysGlobal_bak2 IWG WHERE IWG.ID ='"+inputWayID+"') IWG LEFT JOIN mw_SensorWays_bak SW ON IWG.wayID = SW.wayID AND IWG.crossroadID= SW.crossroadID) S1 JOIN (SELECT * FROM jee_crmodel_SensorDim SENSOR_DIM WHERE CROSSROAD_ID ='"+crossroadID+"') S2 ON S1.SENSOR_ID = S2.ID) FRONT_AND_BACK JOIN (SELECT * FROM mw_SensorConnection_bak FR WHERE FR.FRONT_BACK = 0) ONLY_FRONT ON FRONT_AND_BACK.SENSOR_ID = ONLY_FRONT.SENSOR_ID) SENSORS LEFT JOIN (SELECT * FROM jee_trafficlight_rawevents RE WHERE DATETIME < '"+toSql(to)+"' AND DATETIME>='"+toSql(from)+"') EVENTS_DAY ON SENSORS.CROSSROAD_NAME = EVENTS_DAY.CROSSROAD AND SENSORS.CSVOFFSET=EVENTS_DAY.CSVOFFSET) FINAL GROUP BY DATETIME) GROUPED";
			}
			ResultSet rs = stmt.executeQuery(statementString);
			if(rs.first()){
				count=rs.getDouble(1);
				load=rs.getDouble(2);
			}
			else
				System.out.println("ERROR - " +statementString);
			if(count == 0 && load == 0){
				System.out.println("InputWayWeight ist 0/0/0");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		double savedMax =  maxValuesInputWays.get(inputWayID)[0];
		double countNorm =  savedMax> 0 ? count/savedMax : 0;
		return new double[]{count,load,countNorm*100};
	}

		
	
	/**
	 * liefert das Knotengewicht für einer der tatsächlichen Sensoren
	 * @param to 
	 * @param from 
	 * @param sensorID
	 * @param timeStamp
	 * @return
	 */
	
	// ALT
	public double[] getSensorNodeWeight(int nodeID, int timestamp, DateTime from, DateTime to){
		if(sensorModelNodes.containsKey(nodeID)){
			SensorModelNode node = sensorModelNodes.get(nodeID);
			if(node.getTimestep()==timestamp)
				return node.getWeight();
			else {	//Outdated Data	
				if(node.isReal()){
					getSensorWeights(from, to, timestamp);
					return getSensorNodeWeight(nodeID, timestamp, from, to);
				}
				else{
					double[] weight = (node.getInputWayID()>0)?getInputWayWeight(node.getInputWayID(), from, to):new double[]{-2,-2,-2};
					node.setWeight(weight, timestamp);
					return weight;
				}
			}
		}
		else{
			System.out.println("Nicht drin " +nodeID);
			return new double[]{0,0,0};
		}
	}
	/**
	 * Liefer TRUE, wenn es sich um einen realen Sensor handel, sonst FALSE
	 * @param nodeID
	 * @return
	 */
	public boolean getSensorType(int nodeID){
		return sensorModelNodes.get(nodeID).isReal();
	}
	/**
	 * liefert die Werte für alle tatsächlichen Sensoren
	 * @param from
	 * @param to
	 * @return
	 */
	public double[] getSensorWeights(DateTime from, DateTime to,int timestemp){
		try {
			Statement stmt = con.createStatement();
			String statementString;
			statementString = "SELECT NODE_ID,SUM(cr_count)/COUNT(cr_count) as ANZAHL, SUM(cr_load)/COUNT(cr_load) as BELEGUNG, SENSOR_ID,SENSOR_NAME,WAY_ID,CROSSROAD_ID,CROSSROAD_NAME,SENSOR_TYPE,CSVOFFSET FROM (SELECT NODE_ID,SENSOR_ID,SENSOR_NAME,WAY_ID,CROSSROAD_ID,CROSSROAD_NAME,SENSOR_TYPE,SENSORS.CSVOFFSET,cr_count,DATETIME,cr_load  FROM (SELECT NODES.*,CSVOFFSET FROM (SELECT * FROM mw_SensorGlobal SG WHERE SG.SENSOR_TYPE=0) NODES JOIN jee_crmodel_SensorDim SD ON NODES.SENSOR_ID = SD.ID) SENSORS LEFT JOIN (SELECT * FROM jee_trafficlight_rawevents RE WHERE DATETIME < '"+toSql(to)+"' AND DATETIME >= '"+toSql(from)+"') RE ON SENSORS.CSVOFFSET = RE.CSVOFFSET AND SENSORS.CROSSROAD_NAME = RE.CROSSROAD) RESULT GROUP BY NODE_ID ";
			ResultSet rs = stmt.executeQuery(statementString);
			while(rs.next()){
				if(!rs.getBoolean("SENSOR_TYPE")){
					double[] max = maxValuesSensors.get(rs.getInt("NODE_ID"));
					if(max == null)
						max = new double[]{10,0};
					sensorModelNodes.put(rs.getInt("NODE_ID"), new SensorModelNode(rs.getInt("NODE_ID"),true,new double[]{rs.getDouble("ANZAHL"), rs.getDouble("BELEGUNG"),(rs.getDouble("ANZAHL")/max[0])*100},timestemp));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new double[]{0,0,0};
	}


	public List<EdgeContainer> getSensorConnectionForDNA() {
		List<EdgeContainer> edges = new ArrayList<>();
		try {
			Statement vStmt = con.createStatement();
			String statementString;
			/*
			// Virtual Sensor -> Real Sensor
			// alle Sensoren statementString = "SELECT VS.NODE_ID AS V_NODE_ID, VS.WAY_ID as V_WAY_ID, VS.CROSSROAD_ID as V_CROSSROAD, RS.NODE_ID as R_NODE_ID, RS.WAY_ID as R_WAY_ID, RS.CROSSROAD_ID as R_CROSSROAD_ID FROM (SELECT * FROM mw_SensorGlobal SG1 WHERE SENSOR_TYPE = 1) VS LEFT JOIN (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 0) RS ON VS.WAY_ID = RS.WAY_ID AND VS.CROSSROAD_ID = RS.CROSSROAD_ID WHERE RS.NODE_ID IS NOT NULL"; 
			statementString = "SELECT VS.NODE_ID AS V_NODE_ID, VS.WAY_ID as V_WAY_ID, VS.CROSSROAD_ID as V_CROSSROAD, RS.NODE_ID as R_NODE_ID, RS.WAY_ID as R_WAY_ID, RS.CROSSROAD_ID as R_CROSSROAD_ID FROM (SELECT * FROM mw_SensorGlobal SG1 WHERE SENSOR_TYPE = 1) VS LEFT JOIN (SELECT REAL_SENSORS.* FROM (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 0) REAL_SENSORS JOIN (SELECT * FROM mw_SensorConnection WHERE FRONT_BACK = 0) FRONT_SENSORS ON REAL_SENSORS.SENSOR_ID = FRONT_SENSORS.SENSOR_ID) RS ON VS.WAY_ID = RS.WAY_ID AND VS.CROSSROAD_ID = RS.CROSSROAD_ID WHERE RS.NODE_ID IS NOT NULL";
			ResultSet v2r = vStmt.executeQuery(statementString);
			while(v2r.next() ) {
				edges.add(new EdgeContainer(v2r.getInt("V_NODE_ID"), v2r.getInt("R_NODE_ID")));
			}
			
			// Real Sensor -> Virtual Sensor
			Statement rStmt = con.createStatement();
			statementString = "SELECT DISTINCT R_NODE_ID,V_NODE_ID FROM (SELECT R_NODE.*,V_NODE.NODE_ID as V_NODE_ID FROM (SELECT FROM_NODE.*, TO_WAY,TO_CROSSROAD FROM (SELECT NODE_ID as R_NODE_ID,SENSOR_ID,SENSOR_NAME,WAY_ID as INNER_FROM_WAY,FROM_SENSOR.CROSSROAD_ID, FROM_SENSOR.CROSSROAD_NAME, FROM_SENSOR.DIRECTION as INNER_FROM_DIRECTION, TO_LEFT, OSMWAY_ID as INNER_TO_WAY,TO_WAY.DIRECTION as INNER_TO_DIRECTION FROM (SELECT NODE_ID,SC.SENSOR_ID,SC.SENSOR_NAME,SG.WAY_ID,SC.CROSSROAD_ID,SC.CROSSROAD_NAME,DIRECTION,TO_LEFT,TO_STRAIGHT,TO_RIGHT FROM (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 0) SG JOIN (SELECT * FROM mw_SensorConnection WHERE FRONT_BACK = 0) SC ON SG.SENSOR_ID = SC.SENSOR_ID) FROM_SENSOR JOIN (SELECT * FROM mw_CrossroadWays CRW WHERE TYPE = 1) TO_WAY ON FROM_SENSOR.TO_LEFT = TO_WAY.DIRECTION AND FROM_SENSOR.CROSSROAD_ID = TO_WAY.CROSSROAD_ID) FROM_NODE JOIN (SELECT * FROM mw_CrossroadConnection) CC ON FROM_NODE.CROSSROAD_ID = CC.FROM_CROSSROAD AND INNER_TO_WAY = CC.FROM_WAY) R_NODE JOIN (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 1) V_NODE ON R_NODE.TO_WAY = V_NODE.WAY_ID AND R_NODE.TO_CROSSROAD = V_NODE.CROSSROAD_ID) RESULT1 UNION SELECT DISTINCT R_NODE_ID, V_NODE_ID FROM (SELECT R_NODE.*, V_NODE.NODE_ID AS V_NODE_ID FROM (SELECT FROM_NODE.*, TO_WAY, TO_CROSSROAD FROM (SELECT NODE_ID AS R_NODE_ID, SENSOR_ID, SENSOR_NAME, WAY_ID AS INNER_FROM_WAY, FROM_SENSOR.CROSSROAD_ID, FROM_SENSOR.CROSSROAD_NAME, FROM_SENSOR.DIRECTION AS INNER_FROM_DIRECTION, TO_STRAIGHT, OSMWAY_ID AS INNER_TO_WAY, TO_WAY.DIRECTION AS INNER_TO_DIRECTION FROM (SELECT NODE_ID, SC.SENSOR_ID, SC.SENSOR_NAME, SG.WAY_ID, SC.CROSSROAD_ID, SC.CROSSROAD_NAME, DIRECTION, TO_LEFT, TO_STRAIGHT, TO_RIGHT FROM (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 0) SG JOIN (SELECT * FROM mw_SensorConnection WHERE FRONT_BACK = 0) SC ON SG.SENSOR_ID = SC.SENSOR_ID) FROM_SENSOR JOIN (SELECT * FROM mw_CrossroadWays CRW WHERE TYPE = 1) TO_WAY ON FROM_SENSOR.TO_STRAIGHT = TO_WAY.DIRECTION AND FROM_SENSOR.CROSSROAD_ID = TO_WAY.CROSSROAD_ID) FROM_NODE JOIN (SELECT * FROM mw_CrossroadConnection) CC ON FROM_NODE.CROSSROAD_ID = CC.FROM_CROSSROAD AND INNER_TO_WAY = CC.FROM_WAY) R_NODE JOIN (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 1) V_NODE ON R_NODE.TO_WAY = V_NODE.WAY_ID AND R_NODE.TO_CROSSROAD = V_NODE.CROSSROAD_ID) RESULT2 UNION SELECT DISTINCT R_NODE_ID, V_NODE_ID FROM (SELECT R_NODE.*, V_NODE.NODE_ID AS V_NODE_ID FROM (SELECT FROM_NODE.*, TO_WAY, TO_CROSSROAD FROM (SELECT NODE_ID AS R_NODE_ID, SENSOR_ID, SENSOR_NAME, WAY_ID AS INNER_FROM_WAY, FROM_SENSOR.CROSSROAD_ID, FROM_SENSOR.CROSSROAD_NAME, FROM_SENSOR.DIRECTION AS INNER_FROM_DIRECTION, TO_RIGHT, OSMWAY_ID AS INNER_TO_WAY, TO_WAY.DIRECTION AS INNER_TO_DIRECTION FROM (SELECT NODE_ID, SC.SENSOR_ID, SC.SENSOR_NAME, SG.WAY_ID, SC.CROSSROAD_ID, SC.CROSSROAD_NAME, DIRECTION, TO_LEFT, TO_STRAIGHT, TO_RIGHT FROM (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 0) SG JOIN (SELECT * FROM mw_SensorConnection WHERE FRONT_BACK = 0) SC ON SG.SENSOR_ID = SC.SENSOR_ID) FROM_SENSOR JOIN (SELECT * FROM mw_CrossroadWays CRW WHERE TYPE = 1) TO_WAY ON FROM_SENSOR.TO_RIGHT = TO_WAY.DIRECTION AND FROM_SENSOR.CROSSROAD_ID = TO_WAY.CROSSROAD_ID) FROM_NODE JOIN (SELECT * FROM mw_CrossroadConnection) CC ON FROM_NODE.CROSSROAD_ID = CC.FROM_CROSSROAD AND INNER_TO_WAY = CC.FROM_WAY) R_NODE JOIN (SELECT * FROM mw_SensorGlobal WHERE SENSOR_TYPE = 1) V_NODE ON R_NODE.TO_WAY = V_NODE.WAY_ID AND R_NODE.TO_CROSSROAD = V_NODE.CROSSROAD_ID) RESULT3";
			ResultSet r2v = rStmt.executeQuery(statementString);
			while(r2v.next() ) {
				edges.add(new EdgeContainer(r2v.getInt("R_NODE_ID"), r2v.getInt("V_NODE_ID")));
			}*/
			statementString = "SELECT * FROM mw_SensorConnection_Global";
			ResultSet sensor_con = vStmt.executeQuery(statementString);
			while(sensor_con.next() ) {
				edges.add(new EdgeContainer(sensor_con.getInt("FROM_NODE"), sensor_con.getInt("TO_NODE")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return edges;
	}

	public void writeSensorConnection(Sensor s) {
		try {
			Statement stmt = con.createStatement();
			String insertString;
			for (InputWay connectedInputWay : s.connections.values()) {
				insertString="INSERT IGNORE INTO mw_SensorGlobalConnection SELECT * FROM (SELECT NODE_ID as FROM_NODE_ID FROM mw_SensorGlobal WHERE mw_SensorGlobal.SENSOR_ID ='"+s.sensorID+"' AND CROSSROAD_ID = '"+s.crossroadID+"') A , (SELECT NODE_ID as TO_NODE_ID FROM mw_SensorGlobal SG WHERE SG.SENSOR_TYPE=1 AND SG.CROSSROAD_ID = '"+connectedInputWay.wayID+"' AND SG.WAY_ID = '"+connectedInputWay.crossroadID+"') B ";
				stmt.executeUpdate(insertString);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	
	
	public void setDisabledEdges(HashMap<EdgeContainer,Edge> disabledEdges){
		this.disabledEdges=disabledEdges;
	}
	public void setDisabledEdgesInputWay(HashMap<Integer, HashMap<EdgeContainer,Edge>> disabledEdges){
		this.disabledEdgesInputWay=disabledEdges;
	}
	public HashMap<EdgeContainer,Edge> getDisabledEdges(){
		return disabledEdges;
	}
	public HashMap<Integer, HashMap<EdgeContainer, Edge>> getDisabledEdgesInputWay(){
		return disabledEdgesInputWay;
	}


	public double[] getInputWayWeightStaticInit(int index,TrafficUpdate trafficUpdate) {
		double count = trafficUpdate.getInitCount();
		double load = trafficUpdate.getInitLoad();
		double savedMax = (maxValuesInputWays.containsKey(index)) ?(double) maxValuesInputWays.get(index)[0] : 0;
		double countNorm =  savedMax> 0 ? Double.valueOf(count)/savedMax : 0;
		return new double[]{count,load,countNorm*100};
	}
	public double[] getInputWayWeightStaticBatch(int index,TrafficUpdate trafficUpdate) {
		double count = (trafficUpdate.getSleepTillUpdate()*trafficUpdate.getInitCount()+trafficUpdate.getUpdateCount())/(trafficUpdate.getSleepTillUpdate()+1);
		double load = (trafficUpdate.getSleepTillUpdate()*trafficUpdate.getInitLoad()+trafficUpdate.getUpdateLoad())/(trafficUpdate.getSleepTillUpdate()+1);
		double savedMax = (maxValuesInputWays.containsKey(index)) ? maxValuesInputWays.get(index)[0] : 0;
		double countNorm =  savedMax> 0 ? count/savedMax : 0;
		return new double[]{count,load,countNorm*100};
	}
	public double[] getSensorModelWeightStaticInit(int index,TrafficUpdate trafficUpdate) {
		if(trafficUpdate.getModus()==0){
			if(sensorModelNodes.containsKey(index)){
				SensorModelNode sensorModelNode = sensorModelNodes.get(index);
				double count = trafficUpdate.getInitCount();
				double load = trafficUpdate.getInitLoad();
				double countNorm;
				if(getSensorType(index)){
					if(maxValuesSensors.containsKey(index))
						countNorm = (count/maxValuesSensors.get(index)[0]);
					else
						countNorm = (count/50);
				}
				else{
					double savedMax = (maxValuesInputWays.containsKey(sensorModelNode.getInputWayID())) ? maxValuesInputWays.get(sensorModelNode.getInputWayID())[0] : 0;
					countNorm =  savedMax> 0 ? count/savedMax : 0;
				}
				return new double[]{count,load,countNorm*100};
			}
			else
				return new double[]{0,0,0};
		}
		else{
			return new double[]{-1,-1,trafficUpdate.getInitUtilization()*100};
		}
	}
	public double[] getSensorModelWeightStaticBatch(int index,TrafficUpdate trafficUpdate) {
		if(trafficUpdate.getModus()==0){
			if(sensorModelNodes.containsKey(index)){
				SensorModelNode sensorModelNode = sensorModelNodes.get(index);
				if(sensorModelNode.isReal()){
					double count = (trafficUpdate.getSleepTillUpdate()*trafficUpdate.getInitCount()+trafficUpdate.getUpdateCount())/(trafficUpdate.getSleepTillUpdate()+1);
					double load = (trafficUpdate.getSleepTillUpdate()*trafficUpdate.getInitLoad()+trafficUpdate.getUpdateLoad())/(trafficUpdate.getSleepTillUpdate()+1);
					double savedMax = maxValuesSensors.get(sensorModelNode.getNodeID())[0];
					double countNorm =  savedMax> 0 ? count/savedMax : 0;
					return new double[]{count,load,countNorm*100};
				}
				else
					return getInputWayWeightStaticBatch(sensorModelNode.getInputWayID(), trafficUpdate);
			}
			else
				return new double[]{0,0,0};
		}
		else{
			return new double[]{-1,-1,trafficUpdate.getUpdateUtilization()*100};
		}
	}
	
}
