package dna.io.network.netflow;

import java.io.FileNotFoundException;

import dna.io.network.netflow.NetflowEvent.NetflowEventField;
import dna.util.Config;

public class DefaultNetflowReader extends NetflowEventReader {

	// FORMAT:
	// StartTime
	// Fraction
	// Flags
	// Type (Protocol)
	// SrcAddr
	// Dir
	// DstAddr
	// SrcPkt
	// DstPkt
	// SrcBytes
	// DstBytes
	// State
	// Dur
	// SrcBps
	// DstBps
	// Sport
	// Dport
	protected static final NetflowEventField[] fields = {
			NetflowEventField.Date, NetflowEventField.None,
			NetflowEventField.Flags, NetflowEventField.Protocol,
			NetflowEventField.SrcAddress, NetflowEventField.Direction,
			NetflowEventField.DstAddress, NetflowEventField.PacketsToDst,
			NetflowEventField.PacketsToSrc, NetflowEventField.BytesToDst,
			NetflowEventField.BytesToSrc, NetflowEventField.ConnectionState,
			NetflowEventField.Duration, NetflowEventField.None,
			NetflowEventField.None, NetflowEventField.SrcPort,
			NetflowEventField.DstPort };

	// constructor
	public DefaultNetflowReader(String dir, String filename)
			throws FileNotFoundException {
		this(dir, filename, 0);
	}

	public DefaultNetflowReader(String dir, String filename,
			int dataOffsetSeconds) throws FileNotFoundException {
		super(dir, filename, "\t", "MM-dd-yyyy HH:mm:ss", "HH:mm:ss", Config
				.getInt("GNUPLOT_TIMESTAMP_OFFSET") + dataOffsetSeconds, fields);
	}

}
