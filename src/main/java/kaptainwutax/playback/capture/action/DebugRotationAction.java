package kaptainwutax.playback.capture.action;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.DebugHelper;
import net.minecraft.entity.player.PlayerEntity;

public class DebugRotationAction implements IAction {

	private float pitch,yaw;
	private DebugRotationAction previous;
	private long worldTick;

	private static int rotationTicks;
	private static DebugRotationAction current;

	public DebugRotationAction(float pitch, float yaw) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.worldTick = Playback.recording.currentTick;
		this.previous = current;
		current = this;

		rotationTicks=0;
	}

	public static void restart(){
		rotationTicks=0;
	}

	@Override
	public void play() {
		//System.out.print(",");

		//long diff = this.worldTick - Playback.recording.currentTick;
		//if (diff != 0){
		//	System.out.println("Played Debug Rotation too early by " + diff + " ticks!");
		//}

		++rotationTicks;
		PlayerEntity p = Playback.manager.replayPlayer.getPlayer();
		if (p.getPitch(1F) != this.pitch || p.getYaw(1F) != this.yaw) {
			//Put a breakpoint here if wanted
			System.out.println("Rotation wrong after " + rotationTicks + " ticks." +
					" Is: " + p.getPitch(1F) + ", " + p.getYaw(1F) + "." +
					" Should be: " + this.pitch + ", " + this.yaw + ".");

			//DebugHelper.printStackTraces(p);

			DebugHelper.clearStackTraces(p);
		} else {
			System.out.println("r fine");
			DebugHelper.clearStackTraces(p);
		}

		//if (diff != 0)
		//	System.out.println("-");
	}

}
