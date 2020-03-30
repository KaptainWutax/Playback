package kaptainwutax.playback.replay.action;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.fixes.PacketByteBuf_NotifyPacketActionOnDataloss;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;

public class PacketAction extends Action {

	public static boolean dataLost = false;
	public static final boolean debug = true;

	private Packet<ClientPlayPacketListener> packet;

	public PacketAction() {}

	public PacketAction(Packet<ClientPlayPacketListener> packet) {
		this.packet = packet;
	}

	@Override
	public void play() {
		if(this.packet == null) return;
		try {
			ClientPlayPacketListener listener = client.getNetworkHandler();
			if(listener != null) {
				packet.apply(listener);
			} else {
				//This mess just safely gets ClientPlayPacketListener since we don't have a player instance to go about.
				packet.apply((ClientPlayPacketListener)((IConnectionGetter) client).getConnection().getPacketListener());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Type getType() {
		return Type.PACKET;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(PacketByteBuf buf) throws IOException {
		int id = buf.readVarInt();
		this.packet = (Packet<ClientPlayPacketListener>) NetworkState.PLAY.getPacketHandler(NetworkSide.CLIENTBOUND, id);
		if (this.packet == null) throw new IOException("Invalid packet id " + id);
		this.packet.read(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		Integer id = NetworkState.PLAY.getPacketId(NetworkSide.CLIENTBOUND, packet);
		if (id == null) throw new IOException("Invalid packet " + packet.getClass());
		buf.writeVarInt(id);
		packet.write(debug && Playback.getManager().recording.isSinglePlayerRecording() ? new PacketByteBuf_NotifyPacketActionOnDataloss(buf) : buf);
		if (dataLost) {
			System.err.println("Packet with type " + packet.getClass().toString() + " was serialized with dataloss!");
			dataLost = false;
		}
	}

	public Packet<ClientPlayPacketListener> getPacket() {
		return packet;
	}

	public interface IConnectionGetter {

		ClientConnection getConnection();

	}

}
