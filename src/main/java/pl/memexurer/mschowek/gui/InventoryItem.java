package pl.memexurer.mschowek.gui;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.memexurer.mschowek.Configuration;
import pl.memexurer.mschowek.schowek.SchowekItem;

public class InventoryItem {
    private final ItemStack itemStack;
    private final int itemId;

    public InventoryItem(ItemStack itemStack, int itemId) {
        this.itemStack = itemStack;
        this.itemId = itemId;
    }

    public ItemStack getItemStack(Player player) {
        ItemStack itemStack = new ItemStack(this.itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta.hasDisplayName())
            itemMeta.setDisplayName(PlaceholderAPI.setPlaceholders(player, itemMeta.getDisplayName()));

        if (itemMeta.hasLore())
            itemMeta.setLore(PlaceholderAPI.setPlaceholders(player, itemMeta.getLore()));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public int getItemId() {
        return itemId;
    }
}
