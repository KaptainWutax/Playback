package kaptainwutax.playback.mixin.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.PlayerFrame;
import kaptainwutax.playback.replay.ReplayManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ApiServices;
import net.minecraft.util.UserCache;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer {

    public IntegratedServerMixin(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(serverThread, session, dataPackManager, saveLoader, proxy, dataFixer, apiServices, worldGenerationProgressListenerFactory);
    }

    @Inject(method = "shutdown", at = @At("RETURN"))
    private void onShutdown(CallbackInfo ci) {
        Playback.getManager().setReplaying(ReplayManager.PlaybackState.NO_REPLAY);
    }

    /**
     * Fix "38. the camera player having a different view distance than the replay player causes console being spammed with Changing view distance ...
     *      - caused by Server Thread accessing this.client.options without any synchronization with the client"
     * by always accessing the camera player's options.
     * WARNING: this method is not reliable as the java memory model does NOT guarantee a lot without synchronization.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
    private int max(int min, int viewDistance) {
        if (Playback.getManager().isInReplay()) {
            //might also just return min instead, the integrated server doesn't load chunks anyways
            PlayerFrame p = Playback.getManager().cameraPlayer;
            if (p != null && p.options != null) {
                viewDistance = -1 + p.options.getOptions().getViewDistance().getValue();
            }
        }
        return Math.max(min, viewDistance);
    }

}
