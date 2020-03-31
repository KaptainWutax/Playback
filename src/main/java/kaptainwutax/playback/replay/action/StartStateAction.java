package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class StartStateAction implements PlaybackSerializable {

	private PacketAction joinPacket;
	private int perspective;
	private boolean isSinglePlayer;
	private WindowFocusAction windowFocus = new WindowFocusAction(true);

	public StartStateAction() {}

	public void addPerspective(int perspective) {
		this.perspective = perspective;
	}

	public void addJoinPacket(Packet<ClientPlayPacketListener> packet) {
		this.joinPacket = new PacketAction(packet);
	}

	public void addPhysicalSide(boolean isSinglePlayer) {
		this.isSinglePlayer = isSinglePlayer;
	}

	public void addWindowFocus(boolean windowFocus) {
		this.windowFocus = new WindowFocusAction(windowFocus);
	}

	public void play() {
		MinecraftClient.getInstance().options.perspective = this.perspective;
		this.joinPacket.play();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.perspective = buf.readVarInt();
		this.isSinglePlayer = buf.readBoolean();
		this.joinPacket = new PacketAction();
		this.joinPacket.read(buf);
		this.windowFocus = new WindowFocusAction(true);
		this.windowFocus.read(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.perspective);
		buf.writeBoolean(this.isSinglePlayer);
		this.joinPacket.write(buf);
		this.windowFocus.write(buf);
	}

	public int getPerspective() {
		return this.perspective;
	}

	public PacketAction getJoinPacketAction() {
		return this.joinPacket;
	}

	public boolean isSinglePlayer() {
		return this.isSinglePlayer;
	}

	public GameJoinS2CPacket getJoinPacket() {
		return joinPacket == null ? null : (GameJoinS2CPacket) joinPacket.getPacket();
	}
}