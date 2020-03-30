package kaptainwutax.playback.mixin.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import kaptainwutax.playback.Playback;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to save the exact yaw and pitch, to be able to reconstruct it later.
 * This is necessary only when the values are coming from the internal server without being serialized like from dedicated servers.
 */
@Mixin(EntitySpawnS2CPacket.class)
public abstract class EntitySpawnS2CPacketMixin {
    @Shadow private int pitch; //the problem is that the datatype is int not byte
    @Shadow private int yaw; //the problem is that the datatype is int not byte

    @Redirect(method = "write", require = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PacketByteBuf;writeByte(I)Lio/netty/buffer/ByteBuf;"))
    private ByteBuf saveIntInstead(PacketByteBuf packetByteBuf, int i) {
        ByteBuf retVal;
        if (Playback.recording != null && Playback.recording.isSingleplayerRecording())
            retVal = packetByteBuf.writeInt(i);
        else
            retVal = packetByteBuf.writeByte(i);
        return retVal;
    }

    @Redirect(method = "read", require = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PacketByteBuf;readByte()B"))
    private byte readNothing(PacketByteBuf packetByteBuf) {
        if (Playback.recording != null && Playback.recording.isSingleplayerRecording()) {
            return 0;
        } else {
            return packetByteBuf.readByte();
        }
    }

    @Inject(method = "read", require = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PacketByteBuf;readInt()I", ordinal = 0, shift = At.Shift.BEFORE))
    private void readIntsInstead(PacketByteBuf buf, CallbackInfo ci) {
        if (Playback.recording != null && Playback.recording.isSingleplayerRecording()) {
            this.pitch = buf.readInt();
            this.yaw = buf.readInt();
        }
    }
}
