package kaptainwutax.playback.mixin.server;

import kaptainwutax.playback.Playback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements SnooperListener, CommandOutput, AutoCloseable, Runnable {

	@Shadow @Nullable public abstract ServerNetworkIo getNetworkIo();

	protected MinecraftServerMixin(String name) {
		super(name);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if(Playback.getManager().isRecording())return;
		this.getNetworkIo().tick();
		this.runTasks();
		ci.cancel();
	}

}
