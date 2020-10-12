package kaptainwutax.playback.mixin.accessors;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ButtonListWidget.ButtonEntry.class)
public interface ButtonListWidgetButtonEntryAccessor {
    @Accessor("buttons")
    List<AbstractButtonWidget> getButtons();
}
