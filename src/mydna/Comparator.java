package mydna;

import java.io.IOException;

import dna.plot.Plotting;
import dna.series.Series;
import dna.series.data.SeriesData;

public class Comparator {
	
	public static void main(String[] args){
		int knoten = 74;
		int vergleich1 = 6;
		int vergleich2 = 7;
	
		String data1 = "data/versuch"+vergleich1+"/";
		String name1 = "Versuch "+vergleich1;
		
		String data2 = "data/versuch"+vergleich2+"/";
		String name2 = "Versuch "+vergleich2;
		
		String vergleich = "data/vergleich_"+vergleich1+"_"+vergleich2+"/";
		SeriesData sd_1 = null;
		SeriesData sd_2 = null;
		try {
			sd_1 = Series.get(data1, name1);
			sd_2 = Series.get(data2, name2);
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Plotting.plot(new SeriesData[]{sd_1,sd_2}, vergleich);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	
	}
}
