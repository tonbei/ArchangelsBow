package com.github.tonbei.archangelsbow.listener;

import com.github.tonbei.archangelsbow.ArchangelsBow;
import com.github.tonbei.archangelsbow.manager.ABRecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ABCraftListener implements Listener {

    private final Map<ItemStack, List<List<ItemStack>>> stackRecipeList;

    public ABCraftListener(ABRecipeManager manager) {
        stackRecipeList = manager.stackRecipeList;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlaceItem(PrepareItemCraftEvent e) {
        CraftingInventory inv = e.getInventory();
        ItemStack[] matrix = inv.getMatrix();

        if (e.isRepair()) return;
        if (matrix.length < 9) return;

        for (Map.Entry<ItemStack, List<List<ItemStack>>> itemRecipe : stackRecipeList.entrySet()) {
            boolean cancelFlag = true;
            List<List<ItemStack>> recipeMatrix = itemRecipe.getValue();

            for (int index = 0; index < 9; index++) {
                ItemStack item = matrix[index];
                List<ItemStack> itemList = recipeMatrix.get(index);
                cancelFlag = true;

                for (ItemStack recipeItem : itemList) {
                    if (item == null && recipeItem == null) {
                        cancelFlag = false;
                        break;
                    }
                    if (item == null || recipeItem == null) {
                        continue;
                    }

                    if (item.isSimilar(recipeItem)) {
                        cancelFlag = item.getAmount() < recipeItem.getAmount();
                        break;
                    }
                }

                if (cancelFlag) break;
            }

            if (!cancelFlag) {
                inv.setResult(itemRecipe.getKey());
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickUpCraftItem(CraftItemEvent e) {
        CraftingInventory inv = e.getInventory();
        ItemStack[] matrix = inv.getMatrix();

        if (matrix.length < 9) return;

        for (Map.Entry<ItemStack, List<List<ItemStack>>> itemRecipe : stackRecipeList.entrySet()) {
            boolean cancelFlag = true;
            List<List<ItemStack>> recipeMatrix = itemRecipe.getValue();
            ItemStack[] afterMatrix = new ItemStack[9];

            for (int index = 0; index < 9; index++) {
                ItemStack item = matrix[index];
                List<ItemStack> itemList = recipeMatrix.get(index);
                cancelFlag = true;

                for (ItemStack recipeItem : itemList) {
                    if (item == null && recipeItem == null) {
                        afterMatrix[index] = null;
                        cancelFlag = false;
                        break;
                    }
                    if (item == null || recipeItem == null) {
                        continue;
                    }

                    if (item.isSimilar(recipeItem)) {
                        int amount = item.getAmount() - recipeItem.getAmount();
                        if (amount == 0) {
                            afterMatrix[index] = null;
                        } else if (amount > 0) {
                            ItemStack afterItem = item.clone();
                            afterItem.setAmount(amount);
                            afterMatrix[index] = afterItem;
                        }
                        if (amount >= 0) cancelFlag = false;
                        break;
                    }
                }

                if (cancelFlag) break;
            }

            if (!cancelFlag) {
                inv.setMatrix(afterMatrix);
                inv.setResult(itemRecipe.getKey());
                break;
            }
        }
    }
}
