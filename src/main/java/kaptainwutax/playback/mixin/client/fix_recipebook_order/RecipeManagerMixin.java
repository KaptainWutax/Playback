package kaptainwutax.playback.mixin.client.fix_recipebook_order;

import net.minecraft.recipe.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Redirect(method = "values", at = @At(value = "INVOKE", target = "Ljava/util/stream/Collectors;toSet()Ljava/util/stream/Collector;"))
    private <T> Collector<T, ?, Set<T>> toSet() {
        return Collectors.toCollection(LinkedHashSet::new);
    }
}
