package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.AbstractAction;

import java.util.ArrayList;
import java.util.List;

public abstract class TickCapture {

	private List<AbstractAction> actions = new ArrayList<>();

	public TickCapture() {

	}

	public void play() {
		this.actions.forEach(AbstractAction::play);
	}

	protected void addAction(AbstractAction action) {
		this.actions.add(action);
	}

	public boolean isEmpty() {
		return this.actions.isEmpty();
	}

}
