package dna.io.network.netflow;

import org.joda.time.DateTime;

import dna.io.network.NetworkEvent;
import dna.util.network.NetflowAnalysis.EdgeWeightValue;
import dna.util.network.NetflowAnalysis.NodeWeightValue;

/**
 * Reprenents one netflow event.
 * 
 * @author Rwilmes
 * 
 */
public class NetflowEvent extends NetworkEvent {

	public enum NetflowEventField {
		Date, Time, SrcAddress, DstAddress, Duration, Protocol, SrcPort, DstPort, Direction, None, Packets, PacketsToSrc, PacketsToDst, Bytes, BytesToSrc, BytesToDst, Label, Flags, ConnectionState, numberOfNetflows
	}

	public enum NetflowDirection {
		forward, backward, bidirectional
	}

	// CLASS
	protected long id;

	protected String srcAddress;
	protected String dstAddress;
	protected double duration;

	protected NetflowDirection direction;

	protected String flags;
	protected String connectionState;

	protected String protocol;
	protected String srcPort;
	protected String dstPort;

	protected int packets;
	protected int packetsToSrc;
	protected int packetsToDestination;

	protected int bytes;
	protected int bytesToSrc;
	protected int bytesToDestination;

	protected String label;

	public NetflowEvent(long id, DateTime time, String srcAddress,
			String dstAddress, double duration, NetflowDirection direction,
			String flags, String connectionState, String protocol,
			String srcPort, String dstPort, int packets, int packetsToSrc,
			int packetsToDestination, int bytes, int bytesToSrc,
			int bytesToDestination, String label) {
		super(time);
		this.srcAddress = srcAddress;
		this.dstAddress = dstAddress;
		this.duration = duration;
		this.direction = direction;
		this.flags = flags;
		this.connectionState = connectionState;

		this.protocol = protocol;
		this.srcPort = srcPort;
		this.dstPort = dstPort;

		this.packets = packets;
		this.packetsToSrc = packetsToSrc;
		this.packetsToDestination = packetsToDestination;

		this.bytes = bytes;
		this.bytesToSrc = bytesToSrc;
		this.bytesToDestination = bytesToDestination;

		this.label = label;
	}

	public long getId() {
		return id;
	}

	public String getSrcAddress() {
		return srcAddress;
	}

	public String getDstAddress() {
		return dstAddress;
	}

	public double getDuration() {
		return duration;
	}

	public NetflowDirection getDirection() {
		return direction;
	}

	public String getFlags() {
		return flags;
	}

	public String getConnectionState() {
		return connectionState;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getSrcPort() {
		return srcPort;
	}

	public String getDstPort() {
		return dstPort;
	}

	public int getPackets() {
		return packets;
	}

	public int getPacketsToSrc() {
		return packetsToSrc;
	}

	public int getPacketsToDestination() {
		return packetsToDestination;
	}

	public int getBytes() {
		return bytes;
	}

	public int getBytesToSrc() {
		return bytesToSrc;
	}

	public int getBytesToDestination() {
		return bytesToDestination;
	}

	public String getLabel() {
		return label;
	}

	public String get(NetflowEventField field) {
		switch (field) {
		case Bytes:
			return "" + this.bytes;
		case BytesToDst:
			return "" + this.bytesToDestination;
		case BytesToSrc:
			return "" + this.bytesToSrc;
		case ConnectionState:
			return this.connectionState;
		case Direction:
			return this.direction.toString();
		case DstAddress:
			return this.dstAddress;
		case DstPort:
			return this.dstPort;
		case Duration:
			return "" + this.duration;
		case Flags:
			return this.flags;
		case Label:
			return this.label;
		case numberOfNetflows:
			return "" + 1;
		case Packets:
			return "" + this.packets;
		case PacketsToDst:
			return "" + this.packetsToDestination;
		case PacketsToSrc:
			return "" + this.packetsToSrc;
		case Protocol:
			return this.protocol;
		case SrcAddress:
			return this.srcAddress;
		case SrcPort:
			return this.srcPort;
		case None:
			return "" + 0;
		}

		return null;
	}

	public double getEdgeWeight(EdgeWeightValue value,
			NetflowDirection direction) {
		switch (value) {
		case Bytes:
			if (direction.equals(NetflowDirection.forward))
				return this.bytesToDestination;
			else
				return this.bytesToSrc;
		case numberOfNetflows:
			if (this.direction.equals(NetflowDirection.bidirectional)
					|| this.direction.equals(direction))
				return 1;
		case Packets:
			if (direction.equals(NetflowDirection.forward))
				return this.packetsToDestination;
			else
				return this.packetsToSrc;
		default:
			return 0;
		}
	}

	public double getSrcNodeWeight2(NodeWeightValue value,
			NetflowDirection direction) {
		switch (value) {
		case numberOfNetflowsOut:
			if (this.direction.equals(NetflowDirection.bidirectional)
					|| this.direction.equals(direction)) {
				return 1;
			}
		case PacketsOut:
			if (direction.equals(NetflowDirection.forward))
				return this.packetsToDestination;
			else
				return this.packetsToSrc;
		case BytesOut:
			if (direction.equals(NetflowDirection.forward))
				return this.bytesToDestination;
			else
				return this.bytesToSrc;
		default:
			return 0;
		}
	}

	public double getDstNodeWeight2(NodeWeightValue value,
			NetflowDirection direction) {
		switch (value) {
		case numberOfNetflowsIn:
			if (this.direction.equals(NetflowDirection.bidirectional)
					|| this.direction.equals(direction)) {
				return 1;
			}
		case PacketsIn:
			if (direction.equals(NetflowDirection.forward))
				return this.packetsToDestination;
			else
				return this.packetsToSrc;
		case BytesIn:
			if (direction.equals(NetflowDirection.forward))
				return this.bytesToDestination;
			else
				return this.bytesToSrc;
		default:
			return 0;
		}
	}
}
