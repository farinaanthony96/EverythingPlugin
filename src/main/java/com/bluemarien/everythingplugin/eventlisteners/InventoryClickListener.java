package com.bluemarien.everythingplugin.eventlisteners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;


/**
 * This class contains event listeners that listen for the InventoryClickEvent.
 *
 * @author Anthony Farina
 * @version 2020.09.26
 */
public class InventoryClickListener implements Listener {

    /**
     * This listener checks if a player is trying to get an enchanted book from the result of an
     * enchantment extraction in the anvil.
     *
     * @param event The InventoryClickEvent to listen for when a player is trying to get their
     *              extracted enchantments.
     */
    @EventHandler
    public void givePlayerEnchantedBookFromExtraction(InventoryClickEvent event) {
        Inventory clickedInv = event.getClickedInventory();

        // Check if the inventory is an anvil inventory, the click is a left click, and the player's cursor is empty.
        if (!(clickedInv instanceof AnvilInventory) || !event.getClick().isLeftClick() || event.getWhoClicked().getItemOnCursor().getAmount() != 0) {
            return;
        }

        // Get the anvil inventory object, the inventory view, the raw slot clicked, and the anvil inventory contents.
        AnvilInventory anvilInv = (AnvilInventory) clickedInv;
        InventoryView view = event.getView();
        int rawSlot = event.getRawSlot();
        ItemStack[] anvilInvContents = anvilInv.getContents();

        // Check if the click was in the anvil inventory, the click was in the result slot, and if the anvil slots are empty.
        if (rawSlot != view.convertSlot(rawSlot) || rawSlot != 2 || anvilInvContents[0] == null || anvilInvContents[1] == null || event.getCurrentItem() == null) {
            return;
        }

        // Check that the first anvil slot has an enchanted item, the second slots has at least one book, and the result slot has an enchanted book.
        if (anvilInvContents[0].getEnchantments().size() > 0 && anvilInvContents[1].getType() == Material.BOOK && event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) {
            // Make an unenchanted copy of the enchanted item. We want to reset the anvil use, so we won't copy the enchanted item's meta.
            ItemStack unenchantedItem = new ItemStack(anvilInvContents[0].getType());
            ItemMeta unenchantedItemMeta = unenchantedItem.getItemMeta();

            // Copy the enchanted item's name, lore, and damage onto the unenchanted item.
            unenchantedItemMeta.setDisplayName(anvilInvContents[0].getItemMeta().getDisplayName());
            unenchantedItemMeta.setLore(anvilInvContents[0].getItemMeta().getLore());
            Damageable unenchantedItemDamage = (Damageable) unenchantedItemMeta;
            Damageable enchantedItemDamage = (Damageable) anvilInvContents[0].getItemMeta();
            unenchantedItemDamage.setDamage(enchantedItemDamage.getDamage());
            unenchantedItem.setItemMeta((ItemMeta) unenchantedItemDamage);

            // Replace the enchanted item with the unenchanted item and subtract a book from the second slot.
            anvilInvContents[0] = unenchantedItem;
            anvilInvContents[1].setAmount(anvilInvContents[1].getAmount() - 1);

            // Remove the enchanted book from the anvil's result slot and put it on the player's cursor.
            ItemStack enchantedBook = new ItemStack(event.getCurrentItem());
            event.getCurrentItem().setAmount(0);
            anvilInv.setContents(anvilInvContents);
            event.getWhoClicked().setItemOnCursor(enchantedBook);

            // We're done! Play the anvil use sound effect.
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                    Sound.BLOCK_ANVIL_USE, 1f, 1f);
        }
    }
}
