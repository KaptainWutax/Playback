package kaptainwutax.playback.replay.action.third;

import kaptainwutax.playback.replay.action.Action;
import net.minecraft.util.PacketByteBuf;

public class ScrollInHotbarAction extends Action {
	private double scrollAmount;

	public ScrollInHotbarAction() {}

	public ScrollInHotbarAction(double scrollAmount) {
		this.scrollAmount = scrollAmount;
	}

	@Override
	public void play() {
		client.player.inventory.scrollInHotbar(this.scrollAmount);
	}

	@Override
	public Type getType() {
		return Type.SCROLL_IN_HOTBAR;
	}

	@Override
	public void read(PacketByteBuf buf) {
		scrollAmount = buf.readBoolean() ? 1 : -1;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(scrollAmount > 0);
	}

}
