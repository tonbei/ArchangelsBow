package com.github.tonbei.archangelsbow.manager;

import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ABRecipeManager {

    private boolean isRecipeRegistered;
    private final List<NamespacedKey> recipeKeys = new ArrayList<>();

    public ABRecipeManager(ArchangelsBow plugin) {
        isRecipeRegistered = false;

        for (int level = 1; level <= ABUtil.BOW_MAX_LEVEL; level++)
            recipeKeys.add(new NamespacedKey(plugin, "bow_level_" + level));
    }

    public void addRecipe() {
        if (isRecipeRegistered) return;

        for (int level = 1; level <= ABUtil.BOW_MAX_LEVEL; level++) {
            if (!Bukkit.addRecipe(getArchangelsBowRecipe(level))) {
                Log.warning("Failed to register Archangel's Bow Recipe.");
                removeRecipe(true);
                return;
            }
        }

        Log.info("Archangel's Bow Recipes are registered.");
        isRecipeRegistered = true;
    }

    public void removeRecipe(boolean force) {
        if (!force && !isRecipeRegistered) return;

        for (NamespacedKey key : recipeKeys)
            Bukkit.removeRecipe(key);

        Log.info("Archangel's Bow Recipes are removed.");
        isRecipeRegistered = false;
    }

    @NotNull
    private ShapedRecipe getArchangelsBowRecipe(int level) {
        level = Math.max(1, Math.min(level, ABUtil.BOW_MAX_LEVEL));
        ShapedRecipe recipe = new ShapedRecipe(recipeKeys.get(level - 1), ABUtil.getArchangelsBow(level));
        switch (level) {
            case 1:
                recipe.shape("FTF", "MBI", "CSC")
                        .setIngredient('F', Material.FEATHER)
                        .setIngredient('T', Material.TRIDENT)
                        .setIngredient('M', new RecipeChoice.ExactChoice(ABUtil.getEnchantedBook(Enchantment.MENDING, 1, false)))
                        .setIngredient('B', Material.BOW)
                        .setIngredient('I', new RecipeChoice.ExactChoice(ABUtil.getEnchantedBook(Enchantment.ARROW_INFINITE, 1, false)))
                        .setIngredient('C', Material.END_CRYSTAL)
                        .setIngredient('S', Material.NETHER_STAR);
                break;
        }
        return recipe;
    }
}
