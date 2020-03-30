package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class StartStateAction implements PlaybackSerializable {

	private PacketAction joinPacket;
	private int perspective;
	private boolean isSinglePlayer;

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

	public void play() {
		MinecraftClient.getInstance().options.perspective = this.perspective;
		this.joinPacket.play();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.perspective = buf.readVarInt();
		this.joinPacket = new PacketAction();
		this.joinPacket.read(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.perspective);
		this.joinPacket.write(buf);
	}

	public int getPerspective() {
		return this.perspective;
	}

	public PacketAction getJoinPacket() {
		return this.joinPacket;
	}

	public boolean isSinglePlayer() {
		return this.isSinglePlayer;
	}

}