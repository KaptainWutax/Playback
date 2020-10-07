package kaptainwutax.playback.mixin.accessors;

import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditGameRulesScreen.IntRuleWidget.class)
public interface EditGameRulesScreen$IntRuleWidgetAccessor {
    @Accessor("valueWidget")
    TextFieldWidget getValueWidget();
}
