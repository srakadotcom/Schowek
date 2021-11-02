package pl.memexurer.mschowek.model.impl.bulk;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.memexurer.mschowek.model.UserManager;

public abstract class BulkUserManager extends UserManager<BulkUserDataModel> {
    private static final long saveInterval = 20 * 60 * 3;

    public BulkUserManager(Plugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::save, saveInterval, saveInterval);
    }

    @Override
    protected BulkUserDataModel createUser(Player player) {
        return new BulkUserDataModel();
    }

    public abstract void save();

    public abstract void shutdown();
}
