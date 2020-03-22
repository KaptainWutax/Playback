package kaptainwutax.playback.capture.action;

import kaptainwutax.playback.Playback;
import net.minecraft.client.input.Input;

public class InputAction implements IAction {

	public float movementSideways;
	public float movementForward;
	public boolean pressingForward;
	public boolean pressingBack;
	public boolean pressingLeft;
	public boolean pressingRight;
	public boolean jumping;
	public boolean sneaking;

	public InputAction(Input input) {
		this.movementSideways = input.movementSideways;
		this.movementForward = input.movementForward;
		this.pressingForward = input.pressingForward;
		this.pressingBack = input.pressingBack;
		this.pressingLeft = input.pressingLeft;
		this.pressingRight = input.pressingRight;
		this.jumping = input.jumping;
		this.sneaking = input.sneaking;
	}

	@Override
	public void play() {
		this.play(Playback.manager.replayPlayer.getPlayer().input);
	}

	public void play(Input input) {
		input.movementSideways = this.movementSideways;
		input.movementForward = this.movementForward;
		input.pressingForward = this.pressingForward;
		input.pressingBack = this.pressingBack;
		input.pressingLeft = this.pressingLeft;
		input.pressingRight = this.pressingRight;
		input.jumping = this.jumping;
		input.sneaking = this.sneaking;
	}

}
