package pl.memexurer.mschowek;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.memexurer.mschowek.gui.SchowekInventory;
import pl.memexurer.mschowek.model.UserManager;
import pl.memexurer.mschowek.model.impl.bulk.BulkUserManager;
import pl.memexurer.mschowek.model.impl.bulk.SqlUserManager;
import pl.memexurer.mschowek.schowek.SchowekItem;

public final class SchowekPlugin extends JavaPlugin implements Listener {

    private UserManager<?> userManager;

    public static UserManager<?> getUserManager() {
        return getPlugin(SchowekPlugin.class).userManager;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.userManager = new SqlUserManager(this, getConfig().getConfigurationSection("database"));
        Configuration.getInstance().loadConfiguration(getConfig());

        getServer().getPluginManager().registerEvents(this, this);

        new SchowekPlaceholder(userManager).register();

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player: Bukkit.getOnlinePlayers())
                    for (SchowekItem item : Configuration.getInstance().getSchowekItemList())
                        userManager.getUser(player).checkItems(player, item);
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("schowek.reload")) {
            reloadConfig();
            Configuration.getInstance().loadConfiguration(getConfig());
            sender.sendMessage(ChatColor.GREEN + "Przeladowano!");
            return true;
        }
        Configuration.getInstance().getSchowekInventory().displayInventory((Player) sender);
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof SchowekInventory)
            ((SchowekInventory) e.getClickedInventory().getHolder()).handleClick(e);
    }

    @Override
    public void onDisable() {
        if (userManager instanceof BulkUserManager) {
            ((BulkUserManager) userManager).save();
            ((BulkUserManager) userManager).shutdown();
        }
    }
}
