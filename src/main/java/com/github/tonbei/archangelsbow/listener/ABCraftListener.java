package com.github.tonbei.archangelsbow.listener;

import com.github.tonbei.archangelsbow.manager.ABRecipeManager;
import com.github.tonbei.archangelsbow.manager.TickTaskManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.Map;

public class ABCraftListener implements Listener {

    private final List<NamespacedKey> recipeKeys;

    public ABCraftListener(ABRecipeManager manager) {
        recipeKeys = manager.recipeKeys;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlaceItem(PrepareItemCraftEvent e) {
        CraftingInventory inv = e.getInventory();

        if (e.isRepair()) return;
        if (inv.getMatrix().length < 9) return;

        if (e.getRecipe() instanceof ShapedRecipe) {
            ShapedRecipe recipe = (ShapedRecipe) e.getRecipe();
            if (recipeKeys.stream().anyMatch(recipe.getKey()::equals)) {
                ItemStack[][] recipeMatrix = getRecipeMatrix(recipe);
                ItemStack[] matrix = inv.getMatrix();
                boolean cancelFlag = false;

                for (int index = 0; index < 9; index++) {
                    ItemStack item = matrix[index];
                    ItemStack[] itemList = recipeMatrix[index];

                    if (item == null || item.getType().isAir()) continue;

                    for (ItemStack recipeItem : itemList) {
                        if (recipeItem == null) continue;

                        if (item.getType() == recipeItem.getType()) {
                            if (item.getAmount() < recipeItem.getAmount()) {
                                cancelFlag = true;
                            }
                            break;
                        }
                    }

                    if (cancelFlag) break;
                }

                if (cancelFlag) inv.setResult(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickUpCraftItem(CraftItemEvent e) {
        CraftingInventory inv = e.getInventory();

        if (inv.getMatrix().length < 9) return;

        if (e.getRecipe() instanceof ShapedRecipe) {
            ShapedRecipe recipe = (ShapedRecipe) e.getRecipe();
            if (recipeKeys.stream().anyMatch(recipe.getKey()::equals)) {
                ItemStack[][] recipeMatrix = getRecipeMatrix(recipe);
                ItemStack[] matrix = inv.getMatrix();
                ItemStack[] afterMatrix = new ItemStack[9];
                boolean cancelFlag = false;

                for (int index = 0; index < 9; index++) {
                    ItemStack item = matrix[index];
                    ItemStack[] itemList = recipeMatrix[index];

                    if (item == null || item.getType().isAir()) continue;

                    for (ItemStack recipeItem : itemList) {
                        if (recipeItem == null) continue;

                        if (item.getType() == recipeItem.getType()) {
                            int amount = item.getAmount() - recipeItem.getAmount();
                            if (amount < 0) {
                                cancelFlag = true;
                            } else if (amount > 0) {
                                ItemStack afterItem = item.clone();
                                afterItem.setAmount(amount);
                                afterMatrix[index] = afterItem;
                            }
                            break;
                        }
                    }

                    if (cancelFlag) break;
                }

                if (cancelFlag) {
                    inv.setResult(null);
                    e.setCancelled(true);
                } else {
                    inv.setMatrix(new ItemStack[9]);
                    TickTaskManager.register(() -> inv.setMatrix(afterMatrix));
                    inv.setResult(recipe.getResult());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDiscoverABRecipe(PlayerRecipeDiscoverEvent e) {
        if (recipeKeys.stream().anyMatch(e.getRecipe()::equals))
            e.setCancelled(true);
    }

    private ItemStack[][] getRecipeMatrix(ShapedRecipe recipe) {
        ItemStack[][] matrix = new ItemStack[9][];
        Map<Character, RecipeChoice> recipeChoices = recipe.getChoiceMap();
        int index = 0;
        for (String row : recipe.getShape()) {
            for (Character c : row.toCharArray()) {
                RecipeChoice choice = recipeChoices.get(c);
                if (choice instanceof RecipeChoice.MaterialChoice)
                    matrix[index] = ((RecipeChoice.MaterialChoice) choice).getChoices().stream().map(ItemStack::new).toArray(ItemStack[]::new);
                else if (choice instanceof RecipeChoice.ExactChoice)
                    matrix[index] = ((RecipeChoice.ExactChoice) choice).getChoices().toArray(new ItemStack[0]);

                index++;
            }
            if (index % 3 != 0) index += 3 - (index % 3);
        }

        return matrix;
    }
}
