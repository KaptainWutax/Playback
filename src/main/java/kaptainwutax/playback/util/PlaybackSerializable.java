package kaptainwutax.playback.util;

import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public interface PlaybackSerializable {
    void read(PacketByteBuf buf) throws IOException;
    void write(PacketByteBuf buf) throws IOException;
}
