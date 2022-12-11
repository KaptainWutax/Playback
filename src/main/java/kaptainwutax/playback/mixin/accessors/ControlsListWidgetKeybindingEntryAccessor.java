package kaptainwutax.playback.mixin.accessors;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public interface ControlsListWidgetKeybindingEntryAccessor {
    @Accessor("binding")
    KeyBinding getKeybinding();

    @Accessor("resetButton")
    ButtonWidget getResetButton();

    @Accessor("editButton")
    ButtonWidget getEditButton();
}
