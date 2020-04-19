package kaptainwutax.playback.mixin.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.UserCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.Proxy;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer {
    public IntegratedServerMixin(File gameDir, Proxy proxy, DataFixer dataFixer, CommandManager commandManager, YggdrasilAuthenticationService authService, MinecraftSessionService sessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, String levelName) {
        super(gameDir, proxy, dataFixer, commandManager, authService, sessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory, levelName);
    }

    @Redirect(method = "loadWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;prepareStartRegion(Lnet/minecraft/server/WorldGenerationProgressListener;)V"))
    private void dontLoadStartRegion(IntegratedServer integratedServer, WorldGenerationProgressListener worldGenerationProgressListener) {
        if (!Playback.getManager().isInReplay()) prepareStartRegion(worldGenerationProgressListener);
    }

    @Inject(method = "shutdown", at = @At("RETURN"))
    private void onShutdown(CallbackInfo ci) {
        Playback.getManager().setReplaying(ReplayManager.PlaybackState.NO_REPLAY);
    }
}
