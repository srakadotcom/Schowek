package pl.memexurer.mschowek.gui;

import com.sun.tools.javac.jvm.Items;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pl.memexurer.mschowek.Configuration;
import pl.memexurer.mschowek.SchowekPlugin;
import pl.memexurer.mschowek.model.UserDataModel;
import pl.memexurer.mschowek.schowek.SchowekItem;

import java.util.Map;

public class SchowekInventory implements InventoryHolder {
    private final int inventorySize;
    private final String title;
    private final Map<Integer, InventoryItem> itemMap;

    public SchowekInventory(int inventorySize, String title, Map<Integer, InventoryItem> itemMap) {
        this.inventorySize = inventorySize;
        this.title = title;
        this.itemMap = itemMap;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public void displayInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(this, inventorySize, title);
        for (Map.Entry<Integer, InventoryItem> itemEntry : itemMap.entrySet())
            inventory.setItem(itemEntry.getKey(), itemEntry.getValue().getItemStack(player));

        player.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent e) {
        InventoryItem inventoryItem = itemMap.get(e.getRawSlot());
        if (inventoryItem == null) return;


        e.setCancelled(true);
        if(inventoryItem.getItemId() == -1) return;

        Player player = (Player) e.getWhoClicked();

        UserDataModel schowekUser = SchowekPlugin.getUserManager().getUser(player);
        if(inventoryItem.getItemId() == 9) {
            for(SchowekItem item: Configuration.getInstance().getSchowekItemList())
            {
                int count = schowekUser.getItemCount(item.getItemId());
                if (count == 0)
                    continue;

                if(count > item.getLimit())
                    count = item.getLimit();

                schowekUser.removeItemCount(item.getItemId(), count);
                ItemStack itemStack = item.getItemStack();
                itemStack.setAmount(count);
                player.getInventory().addItem(itemStack);
            }
            return;
        }

        SchowekItem item = Configuration.getInstance().getSchowekItem(inventoryItem.getItemId());

        if(e.isLeftClick()) { //wyplacanie
            int count = schowekUser.getItemCount(inventoryItem.getItemId());
            if (count == 0)
                return;

            if(count > item.getLimit())
                count = item.getLimit();

            schowekUser.removeItemCount(inventoryItem.getItemId(), count);
            ItemStack itemStack = item.getItemStack();
            itemStack.setAmount(count);
            player.getInventory().addItem(itemStack);
        } else if(e.isRightClick()) { //wplacnaie
            int count = item.getItemCount(player);
            if (count == 0)
                return;

            ItemStack itemStack = item.getItemStack();
            itemStack.setAmount(count);

            player.getInventory().removeItem(itemStack);
            schowekUser.addItemCount(item.getItemId(), count);
        }

        displayInventory(player);
    }
}
