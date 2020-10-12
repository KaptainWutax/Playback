package kaptainwutax.playback.mixin.accessors;

import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding;
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
