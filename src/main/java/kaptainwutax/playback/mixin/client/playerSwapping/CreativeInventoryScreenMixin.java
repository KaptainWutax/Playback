package kaptainwutax.playback.mixin.client.playerSwapping;

import kaptainwutax.playback.replay.PlayerFrame;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.inventory.SimpleInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin implements PlayerFrame.ICreativeInventoryScreenCaller {
    @Mutable
    @Shadow @Final private static SimpleInventory inventory;

    @Shadow private static int selectedTab;

    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }

    @Override
    public int getSelectedTab() {
        return selectedTab;
    }

    @Override
    public void setInventory(SimpleInventory newVal) {
        inventory = newVal;
    }

    @Override
    public void setSelectedTab(int newVal) {
        selectedTab = newVal;
    }
}
