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
        if(item.getLimit() == 0) return;
        int itemCount = item.countItems(player);

        if (itemCount > item.getLimit()) {
            int takeItems = itemCount - item.getLimit();
            player.sendMessage(Configuration.getInstance().getTooMany(item.getItemName(), takeItems));

            ItemStack removeItem = item.getItemStack();
            removeItem.setAmount(takeItems);
            player.getInventory().removeItem(removeItem);

            addItemCount(item.getItemId(), takeItems);
        }
    }
}
