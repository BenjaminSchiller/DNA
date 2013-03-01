package dna.plot.data;

import dna.plot.Gnuplot.PlotStyle;


/**
 * @author benni
 * 
 */
public class ConfidenceData2 extends PlotData {

	public ConfidenceData2(String data, PlotStyle style, String title) {
		super(data, style, title);
	}

	@Override
	public boolean isStyleValid() {
		return this.style.equals(PlotStyle.candlesticks);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY) {
		StringBuffer buff = new StringBuffer();
		// 2 avg
		// 3 med
		// 4 min
		// 5 max
		// 6 var
		// 7 varLow
		// 8 varUp
		// 9 confLow
		// 10 confUp
		// X Min 1stQuartile Median 3rdQuartile Max
		buff.append("'" + this.data + "' using ($1 + " + offsetX + "):($9 + "
				+ offsetY + "):($4 + " + offsetY + "):($5 + " + offsetY
				+ "):($10 + " + offsetY + ") with " + this.style);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		buff.append(",\\\n");
		buff.append("'' using ($1 + " + offsetX + "):($3 + " + offsetY
				+ "):($3 + " + offsetY + "):($3 + " + offsetY + "):($3 + "
				+ offsetY + ") with " + this.style + " lt -1 lw " + lw
				+ " notitle");
		buff.append(",\\\n");
		buff.append("'' using ($1 + " + offsetX + "):($2 + " + offsetY
				+ ") with " + PlotStyle.lines + " lt " + lt + " lw " + lw
				+ " notitle");
		return buff.toString();
	}

}
