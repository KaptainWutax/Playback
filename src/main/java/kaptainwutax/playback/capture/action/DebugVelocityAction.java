package kaptainwutax.playback.capture.action;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.DebugHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class DebugVelocityAction implements IAction {

	private double x,y,z;
	private DebugVelocityAction previous;
	private long worldTick;

	private static int positionTicks;
	private static DebugVelocityAction current;

	public DebugVelocityAction(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldTick = Playback.recording.currentTick;
		this.previous = current;
		current = this;

		positionTicks=0;
	}

	public static void restart(){
		positionTicks=0;
	}

	@Override
	public void play() {
		//System.out.print(",");

		//outdated stuff since the injection point of the debugAction changed
		//long diff = this.worldTick - Playback.recording.currentTick;
		//if (diff != 0){
		//	System.out.println("Played Debug Position too early by " + diff + " ticks!");
		//}

		++positionTicks;
		PlayerEntity p = Playback.manager.replayPlayer.getPlayer();
		if (p.getVelocity().x != this.x || p.getVelocity().y != this.y || p.getVelocity().z != this.z) {
			//Put a breakpoint here if wanted
			System.out.println("Velocity wrong after " + positionTicks + " ticks." +
					" Is: " + p.getVelocity().x + ", " + p.getVelocity().y + ", " + p.getVelocity().z + "." +
					" Should be: " + this.x + ", " + this.y + ", " + this.z + ".");
			//p.setPos(this.x,this.y,this.z);

			//DebugHelper.printStackTraces(p);

			DebugHelper.clearStackTraces(p);
		} else {
			System.out.println("p fine");
			DebugHelper.clearStackTraces(p);
		}

		//if (diff != 0)
		//	System.out.println("-");
	}

}
