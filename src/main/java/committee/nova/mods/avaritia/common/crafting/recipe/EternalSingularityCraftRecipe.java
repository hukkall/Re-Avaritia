package committee.nova.mods.avaritia.common.crafting.recipe;

import com.google.gson.JsonObject;
import committee.nova.mods.avaritia.init.handler.SingularityRegistryHandler;
import committee.nova.mods.avaritia.init.registry.ModItems;
import committee.nova.mods.avaritia.init.registry.ModRecipeSerializers;
import committee.nova.mods.avaritia.util.SingularityUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Name: Avaritia-forge / InfinityCatalystRecipe
 * Author: cnlimiter
 * CreateTime: 2023/9/16 17:19
 * Description:
 */

public class EternalSingularityCraftRecipe extends ShapelessTableCraftingRecipe {
    private static final Object2BooleanOpenHashMap<EternalSingularityCraftRecipe> INGREDIENTS_LOADED = new Object2BooleanOpenHashMap<>();
    public NonNullList<Ingredient> inputs = NonNullList.create();

    public EternalSingularityCraftRecipe(ResourceLocation recipeId) {
        super(recipeId, NonNullList.create(), new ItemStack(ModItems.eternal_singularity.get()), 4);
    }

    public static void invalidate() {
        INGREDIENTS_LOADED.clear();
    }
    @Override
    public boolean matches(@NotNull Container input, @NotNull Level level) {
        var ingredients = this.getIngredients();
        return !ingredients.isEmpty() && super.matches(input, level);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        if (!INGREDIENTS_LOADED.getOrDefault(this, false)) {
            super.getIngredients().clear();

            SingularityRegistryHandler.getInstance().getSingularities()
                    .stream()
                    .filter(singularity -> singularity.getIngredient() != Ingredient.EMPTY)
                    .limit(81)
                    .map(SingularityUtils::getItemForSingularity)
                    .map(Ingredient::of)
                    .forEach(super.getIngredients()::add);

            INGREDIENTS_LOADED.put(this, true);
        }
        return super.getIngredients();
    }
    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ETERNAL_SINGULARITY_CRAFT_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<EternalSingularityCraftRecipe> {
        @Override
        public @NotNull EternalSingularityCraftRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            return new EternalSingularityCraftRecipe(recipeId);
        }

        @Override
        public EternalSingularityCraftRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            return new EternalSingularityCraftRecipe(recipeId);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull EternalSingularityCraftRecipe recipe) {
        }
    }
}
