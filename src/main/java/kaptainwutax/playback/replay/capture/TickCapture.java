package kaptainwutax.playback.replay.capture;

import kaptainwutax.playback.replay.action.Action;

import java.util.ArrayList;
import java.util.List;

public abstract class TickCapture {

	private List<Action> actions = new ArrayList<>();

	public TickCapture() {

	}

	public void play() {
		this.actions.forEach(Action::play);
	}

	protected void addAction(Action action) {
		this.actions.add(action);
	}

	public boolean isEmpty() {
		return this.actions.isEmpty();
	}

}
