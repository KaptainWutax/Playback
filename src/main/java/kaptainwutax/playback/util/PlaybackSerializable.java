package kaptainwutax.playback.util;

import java.io.IOException;
import net.minecraft.network.PacketByteBuf;

public interface PlaybackSerializable {
    void read(PacketByteBuf buf) throws IOException;
    void write(PacketByteBuf buf) throws IOException;
}
