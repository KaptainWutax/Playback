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
    //@Mutable
    //@Shadow @Final private static SimpleInventory inventory;

    @Shadow private static ItemGroup selectedTab;

    @Shadow public abstract boolean isInventoryTabSelected();

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Override
    public SimpleInventory getInventory() {
       // return inventory; //TODO: fix
        return null;
    }

    @Override
    public ItemGroup getSelectedTab() {
        return selectedTab;
    }

    @Override
    public void setInventory(SimpleInventory newVal) {
      //  inventory = newVal; //TODO: fix
    }

    @Override
    public void setSelectedTab(ItemGroup newVal) {
        selectedTab = newVal;
    }
}
