package pl.memexurer.mschowek;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.memexurer.mschowek.gui.InventoryItem;
import pl.memexurer.mschowek.gui.SchowekInventory;
import pl.memexurer.mschowek.schowek.SchowekItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Configuration {
    private static final Configuration INSTANCE = new Configuration();
    private final List<SchowekItem> schowekItemList = new ArrayList<>();

    private String tooMany;
    private String limit;
    private SchowekInventory inventory;

    public static Configuration getInstance() {
        return INSTANCE;
    }

    private static String fixColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public void loadConfiguration(ConfigurationSection section) {
        this.tooMany = fixColor(section.getString("too_many"));
        this.limit = fixColor(section.getString("limit"));

        ConfigurationSection schowekItemSection = section.getConfigurationSection("schowek");
        for (String str : schowekItemSection.getKeys(false)) {
            ConfigurationSection itemSection = schowekItemSection.getConfigurationSection(str);

            schowekItemList.add(new SchowekItem(
                    parseItem(itemSection.getConfigurationSection("item")),
                    itemSection.getInt("limit"),
                    itemSection.getInt("schowekLimit"),
                    fixColor(itemSection.getString("name")),
                    Integer.parseInt(str)
            ));
        }

        ConfigurationSection inventorySection = section.getConfigurationSection("inventory");

        ConfigurationSection slotsSection = inventorySection.getConfigurationSection("slots");
        Map<Integer, InventoryItem> itemMap = new HashMap<>();
        for (String str : slotsSection.getKeys(false)) {
            ConfigurationSection itemSection = slotsSection.getConfigurationSection(str);

            itemMap.put(Integer.parseInt(str), new InventoryItem(parseItem(itemSection.getConfigurationSection("item")), itemSection.getInt("id") - 1));
        }
        this.inventory = new SchowekInventory(inventorySection.getInt("size") * 9, fixColor(inventorySection.getString("title")), itemMap);
    }

    private ItemStack parseItem(ConfigurationSection section) {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(section.getString("material")), 1, (short) section.getInt("durability", 0));
        ItemMeta itemMeta = itemStack.getItemMeta();

        String name = section.getString("name");
        if (name != null)
            itemMeta.setDisplayName(fixColor(name));

        List<String> lore = section.getStringList("lore");
        if (lore != null)
            itemMeta.setLore(lore.stream().map(Configuration::fixColor).collect(Collectors.toList()));

        List<String> enchantments = section.getStringList("enchantments");
        if (enchantments != null)
            for (String strS : enchantments) {
                String[] splitted = strS.split(":");
                itemMeta.addEnchant(
                        Enchantment.getByName(splitted[0]),
                        Integer.parseInt(splitted[1]),
                        true
                );
            }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public SchowekItem getSchowekItem(int id) {
        for (SchowekItem item : schowekItemList)
            if (item.getItemId() == id)
                return item;
        return null;
    }

    public List<SchowekItem> getSchowekItemList() {
        return schowekItemList;
    }

    public String getTooMany(String item, int number) {
        return tooMany
                .replace("{ITEM}", item)
                .replace("{NUMBER}", Integer.toString(number));
    }

    public SchowekInventory getSchowekInventory() {
        return inventory;
    }

    public String getLimit() {
        return limit;
    }
}
