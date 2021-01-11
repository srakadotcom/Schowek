package pl.memexurer.mschowek;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class GlobalConfiguration {
    private static final GlobalConfiguration INSTANCE = new GlobalConfiguration();

    private final Map<Integer, Integer> limits = new HashMap<>();
    private boolean takeCombatItems;

    public void loadConfiguration(ConfigurationSection section) {
        this.takeCombatItems = section.getBoolean("takeCombatItems");
        ConfigurationSection limitSection = section.getConfigurationSection("limitSection");

        for(String str : limitSection.getKeys(false))
            limits.put(Integer.parseInt(str), limitSection.getInt(str));
    }

    public int getItemLimit(int id) {
        return limits.getOrDefault(id, 0);
    }

    public void setItemLimit(int id, int val) {
        limits.put(id, val);
    }

    public boolean getCombatItems() {
        return takeCombatItems;
    }

    public boolean toggleCombatItems() {
        return this.takeCombatItems = !this.takeCombatItems;
    }
}
