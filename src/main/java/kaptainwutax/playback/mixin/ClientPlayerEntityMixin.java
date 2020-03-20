package kaptainwutax.playback.mixin;

import com.mojang.authlib.GameProfile;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.Recording;
import kaptainwutax.playback.capture.ReplayView;
import kaptainwutax.playback.entity.FakePlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	@Shadow public Input input;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "tickNewAi", at = @At("HEAD"))
	private void tickNewAi(CallbackInfo ci) {
		if(!Playback.isReplaying) {
			//TODO ??? why is this empty
		} else if((Object)this == Playback.manager.replayPlayer.getPlayer() && Playback.manager.getView() == ReplayView.THIRD_PERSON) {
			//TODO ??? why is this empty
		}
	}

	@Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
	private void isCamera(CallbackInfoReturnable<Boolean> ci) {
		//usually returns true, but setting the camera on the cameraplayer shouldn't modify behavior (movement code), so still return true
		if(Playback.manager.replayPlayer != null && (Object)this == Playback.manager.replayPlayer.getPlayer() && Playback.manager.getView() == ReplayView.THIRD_PERSON) {
			ci.setReturnValue(true);
		}
	}

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isFlyingLocked()Z"))
	private boolean isFlyingLocked(ClientPlayerInteractionManager clientPlayerInteractionManager) {

		//Attempt to make the cameraPlayer not fall into the void due to gravity
		AbstractClientPlayerEntity entity = this;

		if (entity instanceof FakePlayer) {
			return true;
		} else {
			return clientPlayerInteractionManager.isFlyingLocked();
		}
	}

}
