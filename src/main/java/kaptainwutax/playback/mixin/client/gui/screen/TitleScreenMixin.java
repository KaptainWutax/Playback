package kaptainwutax.playback.mixin.client.gui.screen;

import kaptainwutax.playback.gui.PlaybackBrowserScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
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
        this.addButton(new ButtonWidget(x, line4, 200, 20, new LiteralText("Playback"), button -> this.client.openScreen(new PlaybackBrowserScreen(this))));

        for(AbstractButtonWidget button : this.buttons) {
            if (!modMenu && button.y <= line4) button.y -= 12;
            if (!modMenu && button.y > line4) button.y += 12;
            if (modMenu && button.y == line4 - 12) button.setWidth(98);
        }
    }

}
