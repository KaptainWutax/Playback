package kaptainwutax.playback.mixin.client.gui.screen;

import kaptainwutax.playback.gui.PlaybackBrowserScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
        throw new AbstractMethodError();
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;initWidgetsNormal(II)V", shift = At.Shift.AFTER))
    private void adjustButtons(CallbackInfo ci) {
        boolean modMenu = FabricLoader.getInstance().isModLoaded("modmenu");
        int line4 = this.height / 4 + 48 + 24 * 3;
        int x = modMenu ? this.width / 2 + 2 : this.width / 2 - 100;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Playback"),
            button -> this.client.setScreen(new PlaybackBrowserScreen(this)))
            .dimensions(x, line4, 200, 20).build());

        for(Element element : this.children()) {
            if(element instanceof ClickableWidget button) {
                if(!modMenu && button.y <= line4) button.y -= 12;
                if(!modMenu && button.y > line4) button.y += 12;
                if(modMenu && button.y == line4 - 12) button.setWidth(98);
            }
        }
    }

}
