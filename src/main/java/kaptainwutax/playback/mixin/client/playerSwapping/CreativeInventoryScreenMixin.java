package kaptainwutax.playback.mixin.client.playerSwapping;

import kaptainwutax.playback.replay.PlayerFrame;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> implements PlayerFrame.ICreativeInventoryScreenCaller {
    @Mutable
    @Shadow
    @Final
    static SimpleInventory INVENTORY;

    @Shadow
    private static ItemGroup selectedTab;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Override
    public SimpleInventory getInventory() {
        return INVENTORY;
    }

    @Override
    public ItemGroup getSelectedTab() {
        return selectedTab;
    }

    @Override
    public void setInventory(SimpleInventory newVal) {
        INVENTORY = newVal;
    }

    @Override
    public void setSelectedTab(ItemGroup newVal) {
        selectedTab = newVal;
    }
}
