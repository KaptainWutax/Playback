package kaptainwutax.playback.item;

import kaptainwutax.playback.init.PItems;
import kaptainwutax.playback.render.Cube;
import kaptainwutax.playback.render.RenderQueue;
import kaptainwutax.playback.render.Text3D;
import kaptainwutax.playback.render.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class FeatureStick extends Item {

	public FeatureStick(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if(RenderQueue.get().getAt(context.getBlockPos()).isEmpty()) {
			RenderQueue.get().add(new Cube(context.getBlockPos()) {
				@Override
				public boolean shouldRender(Camera camera) {
					return super.shouldRender(camera)
							&& MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND).getItem() == PItems.FEATURE_STICK;
				}
			}, new Text3D("Test", context.getBlockPos(), Color.BLUE));

			return ActionResult.SUCCESS;
		}

		return super.useOnBlock(context);
	}

}
