package kaptainwutax.playback.item;

import kaptainwutax.playback.Playback;
import kaptainwutax.playback.render.util.Color;
import kaptainwutax.playback.replay.edit.WorldText;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class FeatureStick extends Item {

	public FeatureStick(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		/*if(RenderQueue.get().getAt(context.getBlockPos()).isEmpty()) {
			RenderQueue.get().add(new Cube(context.getBlockPos()) {
				@Override
				public boolean shouldRender(Camera camera) {
					return super.shouldRender(camera)
							&& MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND).getItem() == PItems.FEATURE_STICK;
				}
			}, new Text3D("Test", context.getBlockPos(), Color.BLUE));

			return ActionResult.SUCCESS;
		}*/

		WorldText text = new WorldText("Hello World", null, Color.RED);
		text.setPos(context.getBlockPos());
		Playback.getManager().replayEdit.add(text);
		return super.useOnBlock(context);
	}

}
