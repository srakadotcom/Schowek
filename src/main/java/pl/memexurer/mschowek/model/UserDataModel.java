package pl.memexurer.mschowek.model;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.memexurer.mschowek.Configuration;
import pl.memexurer.mschowek.schowek.SchowekItem;

public interface UserDataModel {
    void setItemCount(int id, int i);

    int getItemCount(int id);

    default void addItemCount(int id, int i) {
        setItemCount(id, getItemCount(id) + i);
    }

    default void removeItemCount(int id, int i) { setItemCount(id, getItemCount(id) - i);}

    default void checkItems(Player player, SchowekItem item) {
        int itemCount = item.getItemCount(player);
        int itemLimit = getItemLimit(item.getItemId()) == 0 ? item.getLimit() : getItemLimit(item.getItemId());

        if (itemCount > itemLimit) {
            int takeItems = itemCount - itemLimit;
            player.sendMessage(Configuration.getInstance().getTooMany(item.getItemName(), takeItems));

            ItemStack removeItem = item.getItemStack();
            removeItem.setAmount(takeItems);
            player.getInventory().removeItem(removeItem);

            addItemCount(item.getItemId(), takeItems);
        }
    }
}
