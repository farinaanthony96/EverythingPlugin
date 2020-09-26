package com.bluemarien.everythingplugin.eventlisteners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;


/**
 * This class contains event listeners that listen for the PrepareAnvilEvent.
 *
 * @author Anthony Farina
 * @version 2020.09.26
 */
public class PrepareAnvilListener implements Listener {

    /**
     * This listener checks if a player is trying to extract the enchantments off an item to put on
     * a book.
     *
     * @param event The PrepareAnvilEvent to listen for when a player is trying to extract
     *              enchantments.
     */
    @EventHandler
    public void checkForEnchantmentExtraction(PrepareAnvilEvent event) {
        // Get the anvil inventory object and the contents of the anvil inventory (the first two slots).
        AnvilInventory anvilInv = event.getInventory();
        ItemStack[] anvilInvContents = anvilInv.getContents();

        // Check if there are any items in the anvil inventory.
        if (anvilInvContents[0] == null || anvilInvContents[1] == null) {
            return;
        }

        // Check if an enchanted item is in the first slot and at least one book is in the second slot.
        if (anvilInvContents[0].getEnchantments().size() > 0 && anvilInvContents[1].getType() == Material.BOOK) {
            // Create a new enchanted book and prepare a variable to hold the storage meta.
            ItemStack enchantedBookItem = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta enchantedBookItemMeta =
                    (EnchantmentStorageMeta) enchantedBookItem.getItemMeta();

            // Store all the enchantments on the enchanted item into the enchanted book.
            for (Enchantment enchant : anvilInvContents[0].getEnchantments().keySet()) {
                enchantedBookItemMeta.addStoredEnchant(enchant,
                        anvilInvContents[0].getEnchantmentLevel(enchant), false);
            }

            // Save the storage meta to the enchanted book.
            enchantedBookItem.setItemMeta(enchantedBookItemMeta);

            // Set the result in the anvil inventory to the enchanted book.
            event.setResult(enchantedBookItem);
        }
    }
}
