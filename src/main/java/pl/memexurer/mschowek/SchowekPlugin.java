package pl.memexurer.mschowek;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.manager.CombatManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.memexurer.database.DatabaseCredentials;
import pl.memexurer.mschowek.gui.SchowekInventory;
import pl.memexurer.mschowek.model.UserManager;
import pl.memexurer.mschowek.model.impl.bulk.BulkUserManager;
import pl.memexurer.mschowek.model.impl.bulk.SqlUserManager;
import pl.memexurer.mschowek.schowek.SchowekItem;

public final class SchowekPlugin extends JavaPlugin implements Listener {

    private static SchowekPlugin instance;

    private UserManager<?> userManager;
    private ICombatManager combatManager;

    public static UserManager<?> getUserManager() {
        return instance.userManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        combatManager = ((ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX")).getCombatManager();

        this.userManager = new SqlUserManager(this, new DatabaseCredentials(getConfig().getConfigurationSection("database")));
        Configuration.getInstance().loadConfiguration(getConfig());

        getServer().getPluginManager().registerEvents(this, this);

        new SchowekPlaceholder(userManager).register();
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
    public void onCombat(PlayerTagEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!combatManager.isInCombat(e.getPlayer()))
                {
                    this.cancel();
                    return;
                }

                for (SchowekItem item : Configuration.getInstance().getSchowekItemList())
                    userManager.getUser(e.getPlayer()).checkItems(e.getPlayer(), item);
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof SchowekInventory)
            ((SchowekInventory) e.getClickedInventory().getHolder()).handleClick(e);
    }

    @Override
    public void onDisable() {
        if (userManager instanceof BulkUserManager)
            ((BulkUserManager) userManager).save();
    }
}
