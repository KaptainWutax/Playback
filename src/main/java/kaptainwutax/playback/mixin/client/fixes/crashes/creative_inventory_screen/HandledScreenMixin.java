package kaptainwutax.playback.mixin.client.fixes.crashes.creative_inventory_screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * PlayerFrame needs a dummy CreativeInventoryScreen. This mixin allows creating a dummy instance without sideffects by
 * passing null as argument.
 */
@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Redirect(
            method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getDisplayName()Lnet/minecraft/text/Text;")
    )
    private Text nullCheck(PlayerInventory instance) {
        return instance == null ? null : instance.getDisplayName();
    }

}
