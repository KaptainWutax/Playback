package kaptainwutax.playback.capture.action;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.DebugHelper;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;

public class DebugSneakingAction implements IAction {

	private boolean state;
	private EntityPose pose;

	private DebugSneakingAction previous;
	private long worldTick;

	private static int positionTicks;
	private static DebugSneakingAction current;

	public DebugSneakingAction(boolean state, EntityPose pose) {
		this.state = state;
		this.pose = pose;
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
		if (p.isSneaking() != this.state || p.getPose() != this.pose) {
			//Put a breakpoint here if wanted
			System.out.println("Sneaking wrong after " + positionTicks + " ticks." +
					" Is: " + p.isSneaking() + ", " + p.getPose() + "." +
					" Should be: " + this.state + ", " + this.pose + ".");
			//p.setPos(this.x,this.y,this.z);

			//DebugHelper.printStackTraces(p);

			DebugHelper.clearStackTraces(p);
		} else {
			System.out.println("s fine");
			DebugHelper.clearStackTraces(p);
		}

		//if (diff != 0)
		//	System.out.println("-");
	}

}
