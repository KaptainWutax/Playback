package kaptainwutax.playback.mixin;

import com.mojang.authlib.GameProfile;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
	private void isCamera(CallbackInfoReturnable<Boolean> ci) {
		//usually returns true, but setting the camera on the cameraplayer shouldn't modify behavior (movement code), so still return true
		if(Playback.manager.replayPlayer != null && (Object) this == Playback.manager.replayPlayer.getPlayer() && Playback.manager.getView() == ReplayView.THIRD_PERSON) {
			ci.setReturnValue(true);
		}
	}
}
