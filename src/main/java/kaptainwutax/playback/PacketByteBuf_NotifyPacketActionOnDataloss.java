package kaptainwutax.playback;

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
        PacketAction.dataLost |= i != (i & 0xFF);
        return this.parent.writeByte(i);
    }
    @Override
    public ByteBuf writeShort(int i) {
        PacketAction.dataLost |= i != (i & 0xFFFF);
        return this.parent.writeShort(i);
    }
    @Override
    public ByteBuf writeShortLE(int i) {
        PacketAction.dataLost |= i != (i & 0xFFFF);
        return this.parent.writeShortLE(i);
    }
    @Override
    public ByteBuf writeMedium(int i) {
        PacketAction.dataLost |= i != (i & 0xFFFFFF);
        return this.parent.writeMedium(i);
    }
    @Override
    public ByteBuf writeMediumLE(int i) {
        PacketAction.dataLost |= i != (i & 0xFFFFFF);
        return this.parent.writeMediumLE(i);
    }
    @Override
    public ByteBuf writeChar(int i) {
        //2 Byte UTF-16
        PacketAction.dataLost |= i != (i & 0xFFFF);
        return this.parent.writeChar(i);
    }
}