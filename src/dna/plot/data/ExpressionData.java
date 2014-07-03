package dna.plot.data;

import dna.plot.PlotConfig;

/**
 * @author Rwilmes
 * 
 */
public class ExpressionData extends PlotData {

	private String expression;
	private String[] variables;
	private String[] domains;

	public ExpressionData(String exprName, String formular, PlotStyle style,
			String title, String generalDomain) {
		super(exprName, formular, style, title);

		// parse variables form formular
		String[] split = formular.split("\\$");
		this.variables = new String[split.length / 2];
		this.domains = new String[variables.length];
		for (int j = 0; j < this.variables.length; j++) {
			String var = split[j * 2 + 1];
			String[] varSplit = var.split(PlotConfig.customPlotDomainDelimiter);
			if (varSplit.length == 2) {
				// if it got domain, take it
				this.domains[j] = varSplit[0];
				this.variables[j] = varSplit[1];
			} else {
				// if not, take statistics as domain
				this.domains[j] = generalDomain;
				this.variables[j] = var;
			}
		}

		// build expression without domains
		this.expression = "";
		for (int i = 0; i < split.length; i++) {
			if ((i & 1) == 0)
				this.expression += split[i];
			else
				this.expression += variables[i / 2];
		}
	}

	@Override
	public boolean isStyleValid() {
		return !this.style.equals(PlotStyle.candlesticks)
				&& !this.style.equals(PlotStyle.yerrorbars);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY) {
		return this.getEntry(lt, lw, offsetX, offsetY, this.style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType) {
		return this.getEntry(lt, lw, offsetX, offsetY);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType, PlotStyle style) {
		return this.getEntry(lt, lw, offsetX, offsetY, style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			PlotStyle style) {
		// plot style
		PlotStyle styleTemp;
		if (style == null)
			styleTemp = this.style;
		else
			styleTemp = style;

		// data location
		String dataLoc = null;
		if (super.dataLocation.equals(PlotDataLocation.scriptFile))
			dataLoc = "'-'";
		if (super.dataLocation.equals(PlotDataLocation.dataFile))
			dataLoc = '"' + super.dataPath + '"';

		// build stringbuffer
		StringBuffer buff = new StringBuffer();
		buff.append(dataLoc + " using ($1 + " + offsetX + "):($2 + " + offsetY
				+ ") with " + styleTemp);
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		return buff.toString();
	}

	public String getExpression() {
		return super.domain;
	}

	public String getExpressionWithoutMarks() {
		return this.expression;
	}

	public String[] getVariables() {
		return variables;
	}

	public String[] getDomains() {
		return domains;
	}
}
