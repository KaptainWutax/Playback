package kaptainwutax.playback.mixin.server;

import kaptainwutax.playback.Playback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.ServerTask;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements SnooperListener, CommandOutput, AutoCloseable, Runnable {

	@Shadow @Nullable public abstract ServerNetworkIo getNetworkIo();

	@Shadow protected abstract void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener);

	protected MinecraftServerMixin(String name) {
		super(name);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if(!Playback.getManager().isOrWasReplaying()) return;
		this.getNetworkIo().tick();
		this.runTasks();
		ci.cancel();
	}

	@Inject(method = "save", at = @At("HEAD"), cancellable = true)
	private void noSave(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
		if (Playback.getManager().isOrWasReplaying()) cir.cancel();
	}

	@Redirect(method = "loadWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;prepareStartRegion(Lnet/minecraft/server/WorldGenerationProgressListener;)V"))
	private void dontLoadStartRegion(MinecraftServer minecraftServer, WorldGenerationProgressListener worldGenerationProgressListener) {
		if (!Playback.getManager().isInReplay()) prepareStartRegion(worldGenerationProgressListener);
	}
}
