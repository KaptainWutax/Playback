package kaptainwutax.playback.fixes;

import io.netty.buffer.ByteBuf;
import kaptainwutax.playback.replay.action.PacketAction;
import net.minecraft.util.PacketByteBuf;

public class PacketByteBuf_NotifyPacketActionOnDataloss extends PacketByteBuf {
    private final ByteBuf parent;

    public PacketByteBuf_NotifyPacketActionOnDataloss(ByteBuf byteBuf) {
        super(byteBuf);
        this.parent = byteBuf;
    }

    @Override
    public ByteBuf writeByte(int i) {
        if (i != (byte)i && (i != (i & 0x000000FF))) {
            PacketAction.dataLost = true;
            System.out.println("Saving int " + i + " as byte.");
        }
        return this.parent.writeByte(i);
    }
    @Override
    public ByteBuf writeShort(int i) {
        if (i != (int)(short)i) {
            PacketAction.dataLost = true;
            System.out.println("Saving int " + i + " as short.");
        }
        return this.parent.writeShort(i);
    }
    @Override
    public ByteBuf writeShortLE(int i) {
        if (i != (int)(short)i) {
            PacketAction.dataLost = true;
            System.out.println("Saving int " + i + " as shortLE.");
        }
        return this.parent.writeShortLE(i);
    }
    @Override
    public ByteBuf writeMedium(int i) {
        if (i != (i & 0xFFFFFF)) {
            PacketAction.dataLost = true;
            System.out.println("Saving int " + i + " as 24 bit medium.");
        }
        return this.parent.writeMedium(i);
    }
    @Override
    public ByteBuf writeMediumLE(int i) {
        if (i != (i & 0xFFFFFF)) {
            PacketAction.dataLost = true;
            System.out.println("Saving int " + i + " as 24 bit mediumLE.");
        }
        return this.parent.writeMediumLE(i);
    }
    @Override
    public ByteBuf writeChar(int i) {
        //2 Byte UTF-16
        if (i != (int)(short)i) {
            PacketAction.dataLost = true;
            System.out.println("Saving int " + i + " as 16 bit char.");
        }
        return this.parent.writeChar(i);
    }
}