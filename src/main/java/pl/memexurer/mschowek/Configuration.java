package pl.memexurer.mschowek;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.memexurer.mschowek.gui.InventoryItem;
import pl.memexurer.mschowek.gui.SchowekInventory;
import pl.memexurer.mschowek.schowek.SchowekItem;

import java.util.*;
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
                    parse(itemSection.getString("item")),
                    itemSection.getInt("limit"),
                    itemSection.getInt("schowekLimit"),
                    itemSection.getString("name") == null ? null : fixColor(itemSection.getString("name")),
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

    public static ItemStack parse(String stack) {
        String[] stackArgs = stack.split(";");
        ItemStack itemStack;
        switch (stackArgs[0]) {
            case "WATER_BOTTLE":
                itemStack = new ItemStack(Material.POTION);
                break;
            case "ENCHANTED_GOLDEN_APPLE":
                itemStack = new ItemStack(Material.GOLDEN_APPLE);
                itemStack.setDurability((short) 1);
                break;
            case "KURWA_KRIPER":
                itemStack = new ItemStack(Material.MONSTER_EGG);
                itemStack.setDurability((short) 50);
                break;
            default:
                if(stackArgs[0].startsWith("POTKA-"))
                {
                    itemStack = new ItemStack(Material.POTION, 1, Short.parseShort(stackArgs[0].substring(6)));
                } else
                    itemStack = new ItemStack(Material.valueOf(stackArgs[0]));
                break;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        for (int i = 0; i < stackArgs.length - 1; i++) {
            switch (i) {
                case 0:
                    itemStack.setAmount(Integer.parseInt(stackArgs[1]));
                    break;
                case 1:
                    String stackName = stackArgs[2];
                    boolean isLore = false;
                    if(stackName.startsWith("DogeLore")) {
                        stackName = stackName.substring(8);
                        isLore = true;
                    }

                    boolean hideEnchant = false;
                    if(stackName.startsWith("X")) {
                        stackName = stackName.substring(1);
                        hideEnchant = true;
                    }

                    if(hideEnchant)
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    if(!isLore)
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', stackName));
                    else
                    {
                        String[] splitted = stackName.split(":");
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', splitted[0]));
                        itemMeta.setLore(Arrays.stream(splitted[1].split("'"))
                                .map(text -> ChatColor.translateAlternateColorCodes('&', text))
                                .collect(Collectors.toList()));
                    }
                    break;
                case 2:
                    String[] enchantments = StringUtils.split(stackArgs[3], ":");
                    for (String enchantment : enchantments) {
                        itemMeta.addEnchant(Enchantment.getByName(enchantment.substring(1)), Integer.parseInt("" + enchantment.charAt(0)), true);
                    }
                    break;
            }
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
