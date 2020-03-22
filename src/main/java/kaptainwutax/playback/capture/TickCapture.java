package kaptainwutax.playback.capture;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.capture.action.IAction;

import java.util.ArrayList;
import java.util.List;

public abstract class TickCapture {

	private List<IAction> actions = new ArrayList<>();

	public TickCapture() {

	}

	public void play() {
		this.actions.forEach(IAction::play);
	}

	protected void addAction(IAction action) {
		this.actions.add(action);
	}

	public boolean isEmpty() {
		return this.actions.isEmpty();
	}

}
