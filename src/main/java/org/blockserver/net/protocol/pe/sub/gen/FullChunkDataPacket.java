package org.blockserver.net.protocol.pe.sub.gen;

import org.apache.commons.lang3.ArrayUtils;
import org.blockserver.io.BinaryUtils;
import org.blockserver.io.BinaryWriter;
import org.blockserver.level.IChunk;
import org.blockserver.net.protocol.pe.PeProtocolConst;
import org.blockserver.net.protocol.pe.sub.PeDataPacket;
import org.blockserver.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FullChunkDataPacket implements PeProtocolConst{

    private IChunk chunk;
    private byte[] compressed;

    public FullChunkDataPacket(IChunk chunk){
        //super(new byte[] {MC_FULL_CHUNK_DATA});
        this.chunk = chunk;
    }

    public void encode() throws IOException {
        BinaryWriter writer = new BinaryWriter(new ByteArrayOutputStream(getLength()), BinaryUtils.LITTLE_ENDIAN);

        writer.writeInt((int) chunk.getPosition().getX());
        writer.writeInt((int) chunk.getPosition().getZ());
        writer.write(chunk.getBlockIds());
        writer.write(chunk.getBlockData());
        writer.write(chunk.getSkylight());
        writer.write(chunk.getBlocklight());

        writer.write(chunk.getBiomeIds());
        for(int i: chunk.getBiomeColors()){
            writer.writeByte((byte) (i & 0x0F));
            writer.writeByte((byte) ((i << 8) & 0x0F));
            writer.writeByte((byte) ((i << 16) & 0x0F));
            writer.writeByte((byte) ((i << 24) & 0x0F));
        }

        compressed = toCompressed(((ByteArrayOutputStream) writer.getOutputStream()).toByteArray());
    }

    public int getLength(){
        return 83208;
    }

    public byte[] getCompressed(){
        return compressed;
    }

    /**
     * Get the sendable form of this packet by compressing it.
     * @param uncompressed The Uncompressed data from encode()
     * @return The compressed byte array of this packet.
     */
    private byte[] toCompressed(byte[] uncompressed) throws IOException {
        //System.out.println("Uncompressed: "+uncompressed.length);
        ByteBuffer bb = ByteBuffer.allocate(1 + uncompressed.length);
        bb.put(MC_FULL_CHUNK_DATA);
        byte[] compressed = Utils.compressBytes(uncompressed);
        bb.put(compressed);
        //System.out.println("Compressed: "+compressed.length);
        return ArrayUtils.subarray(bb.array(), 0, compressed.length);
    }
}
