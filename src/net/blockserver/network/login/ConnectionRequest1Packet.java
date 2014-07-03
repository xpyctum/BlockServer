package net.blockserver.network.login;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import net.blockserver.Server;
import net.blockserver.utility.Utils;
import net.blockserver.network.BaseLoginPacket;

public class ConnectionRequest1Packet extends BaseLoginPacket {
	private DatagramPacket packet;
	private Server server;
	
	protected ByteBuffer buffer;
	public byte PID;
	public byte[] MAGIC;
	public byte protocolVersion;
	public byte[] nullPayload;
	
	
	public ConnectionRequest1Packet(DatagramPacket packet, Server server){
		this.packet = packet;
		this.server = server;
		
		buffer = ByteBuffer.wrap(packet.getData());
		PID = buffer.get();
		MAGIC = buffer.get(new byte[16]).array();
		protocolVersion = buffer.get();
		nullPayload = buffer.get(new byte[packet.getData().length - 18]).array();
	}
	
	public ByteBuffer getResponse(){
		ByteBuffer response = ByteBuffer.allocate(28);
		byte packetID = (byte) 0x06;
		byte[] magic = Utils.hexStringToByteArray("00ffff00fefefefefdfdfdfd12345678");
		long serverID = server.serverID;
		byte security = 0;
		short mtu = (short) nullPayload.length;
		
		response.put(packetID);
		response.put(magic);
		response.putLong(serverID);
		response.put(security);
		response.putShort(mtu);
		
		return response;
	}
	
	public DatagramPacket getPacket(){
		return packet;
	}
	
	public ByteBuffer getBuffer(){
		return ByteBuffer.wrap(packet.getData());
	}
	
	public byte getPID(){
		return PID;
	}

}
