package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.replay.action.GameOptionsAction;
import kaptainwutax.playback.replay.action.PacketAction;
import kaptainwutax.playback.replay.action.WindowFocusAction;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class StartState implements PlaybackSerializable {

	private PacketAction joinPacket = new PacketAction();
	private int perspective;
	private int isSinglePlayer = -1;
	private WindowFocusAction windowFocus = new WindowFocusAction(true);
	private GameOptionsAction gameOptionsAction = new GameOptionsAction();

	public StartState() {}

	public void addPerspective(int perspective) {
		this.perspective = perspective;
	}

	public void addJoinPacket(Packet<ClientPlayPacketListener> packet) {
		this.joinPacket = new PacketAction(packet);
	}

	public void addPhysicalSide(boolean isSinglePlayer) {
		this.isSinglePlayer = isSinglePlayer ? 1 : 0;
	}

	public void addWindowFocus(boolean windowFocus) {
		this.windowFocus = new WindowFocusAction(windowFocus);
	}

	public void addGameOptions(GameOptions options) {
		this.gameOptionsAction = new GameOptionsAction(options);
	}

	public void play() {
		MinecraftClient.getInstance().options.perspective = this.perspective;
		this.windowFocus.play();
		this.gameOptionsAction.play();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.perspective = buf.readVarInt();
		this.isSinglePlayer = buf.readBoolean() ? 1 : 0;
		this.joinPacket.read(buf);
		this.windowFocus = new WindowFocusAction(true);
		this.windowFocus.read(buf);
		this.gameOptionsAction.read(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.perspective);
		buf.writeBoolean(this.isSinglePlayer());
		this.joinPacket.write(buf);
		this.windowFocus.write(buf);
		this.gameOptionsAction.write(buf);
	}

	public int getPerspective() {
		return this.perspective;
	}

	public PacketAction getJoinPacketAction() {
		return this.joinPacket;
	}

	public boolean isSinglePlayer() {
		if (this.isSinglePlayer == -1) {
			System.err.println("Accessing non-initialized isSinglePlayer!");
		}
		return this.isSinglePlayer == 1;
	}

	public GameJoinS2CPacket getJoinPacket() {
		return joinPacket == null ? null : (GameJoinS2CPacket) joinPacket.getPacket();
	}

	public boolean getWindowFocus() {
	    return this.windowFocus.getFocus();
    }

}