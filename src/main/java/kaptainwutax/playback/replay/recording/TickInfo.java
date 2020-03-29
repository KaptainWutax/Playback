package kaptainwutax.playback.replay.recording;

import io.netty.buffer.Unpooled;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.replay.action.DebugAction;
import kaptainwutax.playback.replay.action.PacketAction;
import kaptainwutax.playback.replay.capture.FirstPersonTickCapture;
import kaptainwutax.playback.replay.capture.ThirdPersonTickCapture;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;

import javax.annotation.Nullable;
import java.io.IOException;

public class TickInfo implements PlaybackSerializable {

	public static final TickInfo EMPTY = new TickInfo();

	public FirstPersonTickCapture first = new FirstPersonTickCapture();
	public ThirdPersonTickCapture third = new ThirdPersonTickCapture();

	@Nullable
	private PacketAction joinPacket;

	public void play(ReplayView view) {
		Playback.isProcessingReplay = true;
		Playback.allowInput = true;
		if(view == ReplayView.FIRST_PERSON) this.first.play();
		else if(view == ReplayView.THIRD_PERSON) this.third.play();
		Playback.isProcessingReplay = false;
		Playback.allowInput = Playback.allowInputDefault;
	}

	public void recordPacket(Packet<ClientPlayPacketListener> packet) {
		if(packet instanceof GameJoinS2CPacket) {
			this.joinPacket = new PacketAction(packet);
			Playback.recording.joinPacket = joinPacket;
			return;
		}

		this.first.addPacketAction(packet);
		this.third.addPacketAction(packet);
	}

	public void recordKey(int action, int key, int scanCode, int i, int j) {
		this.first.addKeyAction(action, key, scanCode, i, j);
	}

	public void recordMouse(int action, double d1, double d2, int i1, boolean isCursorLocked) {
		this.first.addMouseAction(action, d1, d2, i1, isCursorLocked);
	}

	public void recordKeyState(long handle, int i) {
		this.first.addKeyState(handle, i);
	}

	public boolean getKeyState(long handle, int i) {
		return this.first.getKeyState(handle, i);
	}

	public void recordChangeLook(double cursorDeltaX, double cursorDeltaY) {
		this.third.addChangeLookAction(cursorDeltaX, cursorDeltaY);
	}

	public void recordScrollInHotbar(double scrollAmount) {
		this.third.addScrollInHotbarAction(scrollAmount);
	}

	public void recordSetFlySpeed(float flySpeed) {
		this.third.addSetFlySpeedAction(flySpeed);
	}

	public void recordFirstTickFixes() {
		this.first.addF5ModeFixAction(MinecraftClient.getInstance().options.perspective);
	}

	public void recordDebug() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		this.first.addDebugAction(new DebugAction(player));
		this.third.addDebugAction(new DebugAction(player));
	}

	public boolean isEmpty() {
		return this.first.isEmpty() && this.third.isEmpty();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int firstSize = buf.readVarInt();
		first.read(new PacketByteBuf(buf.slice(buf.readerIndex(), firstSize)));
		buf.readerIndex(buf.readerIndex() + firstSize);
		int thirdSize = buf.readVarInt();
		third.read(new PacketByteBuf(buf.slice(buf.readerIndex(), thirdSize)));
		buf.readerIndex(buf.readerIndex() + thirdSize);
		boolean join = buf.readBoolean();
		if (join) {
			joinPacket = new PacketAction();
			joinPacket.read(buf);
			Recording current = Recording.currentlyReading.get();
			if (current != null) current.joinPacket = joinPacket;
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		PacketByteBuf tmp = new PacketByteBuf(Unpooled.buffer());
		first.write(tmp);
		buf.writeVarInt(tmp.writerIndex());
		buf.writeBytes(tmp);
		tmp.readerIndex(0);
		tmp.writerIndex(0);
		third.write(tmp);
		buf.writeVarInt(tmp.writerIndex());
		buf.writeBytes(tmp);
		tmp.release();
		if (joinPacket != null) {
			buf.writeBoolean(true);
			joinPacket.write(buf);
		} else {
			buf.writeBoolean(false);
		}
	}
}
