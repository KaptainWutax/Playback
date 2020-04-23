package kaptainwutax.playback.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class Timeline extends DrawableHelper implements Drawable, Element {

	//some random texture until we have our own or a better one
	private static Identifier TEXTURE = new Identifier("textures/environment/end_sky.png"); //= new Identifier("textures/gui/widgets.png");
	//private static final Identifier TEXTURE = new Identifier("textures/entity/painting/skeleton.png");
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	private Collection<Drawable> drawables;

	public Timeline(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		//Code mostly inspired from hotbar (InGameHud.renderHotbar)
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
		int i = this.width / 2;
		int prevBlitOffset = this.getBlitOffset();
		this.setBlitOffset(90);

		//       on screen x1 (left), on screen y1 (top), in texture x1, in texture x2, width height
		this.blit(this.x, this.y, 0, 0, this.width, this.height);

		this.setBlitOffset(prevBlitOffset);

		RenderSystem.disableRescaleNormal();
		RenderSystem.disableBlend();

		int xAdd = this.width / 2;
		int yAdd = this.height / 2;

		//render camera path on timeline
		//this.drawables.forEach(e -> e.render(mouseX, mouseY, delta));

		//Parameters have the wrong name in fillGradient: order is LEFT,TOP,RIGHT,BOTTOM
		//this.fillGradient(this.x - xAdd, this.y - yAdd, this.x + xAdd, this.y + yAdd, -2130706433, -2130706433);

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
