package kaptainwutax.playback.mixin.client.fixes.crashes.creative_inventory_screen;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * PlayerFrame needs a dummy CreativeInventoryScreen. This mixin allows creating a dummy instance without sideffects by
 * passing null as argument.
 */
@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {

    @Redirect(
            method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;")
    )
    private static PlayerInventory nullCheck1(PlayerEntity instance) {
        return instance == null ? null : instance.getInventory();
    }

    @Redirect(
            method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;")
    )
    private void nullCheck(PlayerEntity instance, ScreenHandler value) {
        if (instance == null) {
            return;
        }
        instance.currentScreenHandler = value;
    }
}
