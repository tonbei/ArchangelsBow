package com.github.tonbei.archangelsbow.manager;

import com.github.tonbei.archangelsbow.util.ABUtil;
import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ABRecipeManager {

    private boolean isRecipeRegistered;
    public final List<NamespacedKey> recipeKeys;

    public ABRecipeManager(ArchangelsBow plugin) {
        isRecipeRegistered = false;

        List<NamespacedKey> tempKeys = new ArrayList<>();
        for (int level = 1; level <= ABUtil.BOW_MAX_LEVEL; level++)
            tempKeys.add(new NamespacedKey(plugin, "bow_level_" + level));

        recipeKeys = Collections.unmodifiableList(tempKeys);
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
                recipe.shape("XTX", "MBI", "CSC")
                        .setIngredient('X', new RecipeChoice.ExactChoice(new ItemStack(Material.EXPERIENCE_BOTTLE, 32)))
                        .setIngredient('T', Material.TRIDENT)
                        .setIngredient('M', new RecipeChoice.ExactChoice(ABUtil.getEnchantedBook(Enchantment.MENDING, 1, false)))
                        .setIngredient('B', Material.BOW)
                        .setIngredient('I', new RecipeChoice.ExactChoice(ABUtil.getEnchantedBook(Enchantment.LOYALTY, 3, false)))
                        .setIngredient('C', new RecipeChoice.ExactChoice(new ItemStack(Material.END_CRYSTAL, 16)))
                        .setIngredient('S', Material.NETHER_STAR);
                break;
        }
        return recipe;
    }
}
