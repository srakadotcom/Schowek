package pl.memexurer.mschowek.schowek;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class SchowekItem {
    private final ItemStack itemStack;
    private final int limit;
    private final int schowekLimit;
    private final String itemName;
    private final int itemId;

    public SchowekItem(ItemStack itemStack, int limit, int schowekLimit, String itemName, int itemId) {
        this.itemStack = itemStack;
        this.limit = limit;
        this.schowekLimit = schowekLimit;
        this.itemName = itemName;
        this.itemId = itemId;
    }

    public ItemStack getItemStack() {
        return new ItemStack(itemStack);
    }

    public int getLimit() {
        return limit;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public int countItems(Player player) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.isSimilar(itemStack))
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    public int getSchowekLimit() {
        return schowekLimit;
    }
}
