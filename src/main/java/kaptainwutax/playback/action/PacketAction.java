package kaptainwutax.playback.action;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class PacketAction implements IAction {

	private int packetId;
	private byte[] packet;

	public PacketAction(Packet<?> packet) {
		Integer i = NetworkState.PLAY.getPacketId(NetworkSide.CLIENTBOUND, packet);

		if(i == null) {
			return;
		}

		this.packetId = i;
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());

		try {
			packet.write(packetByteBuf);
			this.packet = new byte[packetByteBuf.writerIndex()];
			packetByteBuf.readerIndex(0);
			packetByteBuf.readBytes(this.packet);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void play() {
		if(this.packet == null)return;

		ByteBuf byteBuf = Unpooled.buffer();
		byteBuf.writeBytes(this.packet);
		PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf);
		packetByteBuf.readerIndex(0);

		try {
			Packet<ClientPlayPacketListener> packet = (Packet<ClientPlayPacketListener>)NetworkState.PLAY.getPacketHandler(NetworkSide.CLIENTBOUND, this.packetId);
			packet.read(packetByteBuf);
			packet.apply(MinecraftClient.getInstance().getNetworkHandler());
		} catch(Exception e) {
		}
	}

}
