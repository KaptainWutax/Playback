package kaptainwutax.playback.mixin.client.fix_recipebook_order;

import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {
    @Redirect(method = "toGroupedMap", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;"))
    private static HashMap<RecipeBookGroup, List<List<Recipe<?>>>> newLinkedHashMap() {
        //prevent ordering being dependent on identity hashcode
        return new LinkedHashMap<>();
    }
}
