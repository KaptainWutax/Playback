package kaptainwutax.playback.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;

public class Timeline extends DrawableHelper implements Drawable, Element {

	protected int x;
	protected int y;
	protected int width;
	protected int height;

	public Timeline(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		int xAdd = this.width / 2;
		int yAdd = this.height / 2;
		this.fillGradient(this.y - yAdd,this.x - xAdd, this.x + xAdd, this.y + yAdd, -2130706433, -2130706433);
		//this.fillGradient(0, 0, 100, 200, -2130706433, -2130706433);
	}

	private void onClick(double mouseX, double mouseY) {

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(button != 0)return false; //Can only left click.

		if(this.isInBoundsOf(mouseX, mouseY)) {
			this.onClick(mouseX, mouseY);
			return true;
		}

		return false;
	}

	private boolean isInBoundsOf(double mouseX, double mouseY) {
		return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}

}
