package kaptainwutax.playback.mixin.client.gui.widget;

import kaptainwutax.playback.mixin.accessors.ButtonListWidgetButtonEntryAccessor;
import kaptainwutax.playback.mixin.accessors.ControlsListWidgetKeybindingEntryAccessor;
import kaptainwutax.playback.mixin.accessors.EditGameRulesScreenBooleanRuleWidgetAccessor;
import kaptainwutax.playback.mixin.accessors.EditGameRulesScreenIntRuleWidgetAccessor;
import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin<E extends EntryListWidget.Entry<E>> {

    @Shadow protected abstract void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta);

    @Shadow protected abstract int getRowLeft();

    @Shadow protected int top;

    @Shadow protected abstract int getRowTop(int index);

    @Shadow protected abstract int getRowBottom(int index);

    @Shadow protected abstract int getItemCount();

    @Shadow protected int bottom;

    @Shadow protected abstract E getEntry(int index);

    @Shadow public abstract int getRowWidth();

    @Inject(method = "setScrollAmount", at = @At("RETURN"))
    private void update(double amount, CallbackInfo ci) {
        this.updateLikeRendering();
    }

    @Inject(method = "updateSize", at = @At("RETURN"))
    private void update2(int width, int height, int top, int bottom, CallbackInfo ci) {
        this.updateLikeRendering();
    }

    /**
     * Manipulates the GUI the same way rendering would.
     */
    private void updateLikeRendering() {
        int count = this.getItemCount();

        for(int rowIndex = 0; rowIndex < count; ++rowIndex) {
            int newY = this.getRowTop(rowIndex);
            int newYEnd = this.getRowBottom(rowIndex);
            if (newYEnd >= this.top && newY <= this.bottom) {
                E entry = this.getEntry(rowIndex);
                if (entry instanceof ButtonListWidget.ButtonEntry) {
                    ((ButtonListWidgetButtonEntryAccessor) entry).getButtons().forEach((button) -> button.y = newY);
                }
                if (entry instanceof ControlsListWidget.KeyBindingEntry) {
                    int newX = this.getRowLeft();
                    ((ControlsListWidgetKeybindingEntryAccessor) entry).getResetButton().x = newX + 190;
                    ((ControlsListWidgetKeybindingEntryAccessor) entry).getResetButton().y = newY;
                    ((ControlsListWidgetKeybindingEntryAccessor) entry).getResetButton().active = !((ControlsListWidgetKeybindingEntryAccessor) entry).getKeybinding().isDefault();

                    ((ControlsListWidgetKeybindingEntryAccessor) entry).getEditButton().x = newX + 105;
                    ((ControlsListWidgetKeybindingEntryAccessor) entry).getEditButton().y = newY;
                }
                if (entry instanceof EditGameRulesScreen.IntRuleWidget) {
                    int entryWidth = this.getRowWidth();
                    int x = this.getRowLeft();

                    ((EditGameRulesScreenIntRuleWidgetAccessor) entry).getValueWidget().x = x + entryWidth - 44;
                    ((EditGameRulesScreenIntRuleWidgetAccessor) entry).getValueWidget().y = newY;

                }
                if (entry instanceof EditGameRulesScreen.BooleanRuleWidget) {
                    int entryWidth = this.getRowWidth();
                    int x = this.getRowLeft();

                    ((EditGameRulesScreenBooleanRuleWidgetAccessor) entry).getToggleButton().x = x + entryWidth - 45;
                    ((EditGameRulesScreenBooleanRuleWidgetAccessor) entry).getToggleButton().y = newY;
                }
            }
        }
    }
}
