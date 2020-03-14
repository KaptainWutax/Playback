package kaptainwutax.playback.capture.action;

import kaptainwutax.playback.Playback;
import net.minecraft.entity.player.PlayerEntity;

public class DebugPositionAction implements IAction {

	private double x,y,z;
	private DebugPositionAction previous;

	private static int positionTicks;
	private static DebugPositionAction current;

	public DebugPositionAction(double x,double y,double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.previous = current;
		current = this;
	}

	@Override
	public void play() {
		++positionTicks;
		PlayerEntity p = Playback.manager.replayPlayer.getPlayer();
		if (p.getX() != this.x || p.getY() != this.y || p.getZ() != this.z) {
			//Put a breakpoint here if neccessary
			System.out.println("Position wrong after " + positionTicks + " ticks." +
					" Is: " + p.getX() + ", " + p.getY() + ", " + p.getZ() + "." +
					" Should be: " + this.x + ", " + this.y + ", " + this.z + ".");
			p.setPos(this.x,this.y,this.z);
		}
	}

}
