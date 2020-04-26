package kaptainwutax.playback.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.render.CameraPath;
import kaptainwutax.playback.replay.render.GameTimeStamp;
import kaptainwutax.playback.replay.render.KeyFrame;
import kaptainwutax.playback.replay.render.KeyFrameCameraPath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

public class Timeline extends DrawableHelper implements Drawable, Element {

	//some random texture until we have our own or a better one
	private static Identifier TEXTURE = new Identifier("textures/gui/widgets.png");
	private static int TEXTURE_Y_OFFSET = 46;
	public static int TEXTURE_HEIGHT = 20;

	private static Identifier KEYFRAME_TEXTURE = new Identifier("playback","textures/hud/keyframe.png");
	private static int KEYFRAME_TEXTURE_SIZE_X = 7;
	private static int KEYFRAME_TEXTURE_SIZE_Y = 7;


	protected int x;
	protected int y;
	protected int width;
	protected int height;

	protected GameTimeStamp startTime;
	protected GameTimeStamp endTime;
	protected double duration;

	public Timeline(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void init() {
		this.startTime = new GameTimeStamp(0,0f);
		this.endTime = new GameTimeStamp(Playback.getManager().recording.getEnd(), 0f);
		this.duration = this.endTime.tick - this.startTime.tick + this.endTime.tickDelta - this.startTime.tickDelta;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if (this.startTime == null)
			this.init();

		//Code mostly inspired from hotbar (InGameHud.renderHotbar)
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
		int i = this.width / 2;
		int prevBlitOffset = this.getBlitOffset();
		//BlitOffset is a z value, more positive renders on top
		this.setBlitOffset(-10);
		//args: on screen x1 (left), on screen y1 (top), in texture x1, in texture y1, width Dx, height Dy
		this.blit(this.x, this.y, 0, TEXTURE_Y_OFFSET, this.width, this.height);

		Collection<CameraPath> paths = Playback.getManager().renderManager.getCameraPaths();
		for (CameraPath path : paths) {
			if(this.endTime.isAfter(path.getStartTime()) && this.startTime.isBefore(path.getEndTime())) {
				this.renderCameraPathBox(path);
				if (path instanceof KeyFrameCameraPath)
					this.renderCameraPathKeyFrames((KeyFrameCameraPath) path);
			}
		}

		this.setBlitOffset(prevBlitOffset);
		RenderSystem.disableRescaleNormal();
		RenderSystem.disableBlend();
		//int xAdd = this.width / 2;
		//int yAdd = this.height / 2;
		//Parameters have the wrong name in fillGradient: order is LEFT,TOP,RIGHT,BOTTOM
		//this.fillGradient(this.x - xAdd, this.y - yAdd, this.x + xAdd, this.y + yAdd, -2130706433, -2130706433);
	}

	private void renderCameraPathKeyFrames(KeyFrameCameraPath path) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(KEYFRAME_TEXTURE);
		this.setBlitOffset(-8);

		List<KeyFrame> keyFrames = path.getKeyFrames();
		for (KeyFrame keyFrame : keyFrames) {
			int x1 = this.x + (int)(this.width * keyFrame.getTimeStampAsDouble() / this.duration);
			this.blit(x1 - KEYFRAME_TEXTURE_SIZE_X / 2, this.y + this.height / 2 - KEYFRAME_TEXTURE_SIZE_Y / 2, 0, 0, KEYFRAME_TEXTURE_SIZE_X, KEYFRAME_TEXTURE_SIZE_Y);
		}
	}

	private void renderCameraPathBox(CameraPath path) {
		double start = Math.max(this.startTime.asDouble(), path.getStartTime().asDouble());
		double end = Math.min(this.endTime.asDouble(), path.getEndTime().asDouble());
		int x1 = this.x + (int)(this.width * start / this.duration);
		int x2 = this.x + (int)(this.width * end / this.duration);

		//Parameters have the wrong name in fillGradient: order is LEFT,TOP,RIGHT,BOTTOM // x1,y1,x2,y2
		this.setBlitOffset(-9);
		this.fillGradient(x1, this.y, x2, this.y + this.height, 0xff808080, 0xff808080);
	}

	private void onLeftClick(double mouseX, double mouseY) {

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(button != 0) return false; //Can only left click. //Right click and middle click might be useful too

		if(this.isInBoundsOf(mouseX, mouseY)) {
			this.onLeftClick(mouseX, mouseY);
			return true;
		}

		return false;
	}

	private boolean isInBoundsOf(double mouseX, double mouseY) {
		return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
	}

	public GameTimeStamp getStartTime() {
		return startTime;
	}

	public GameTimeStamp getEndTime() {
		return endTime;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
