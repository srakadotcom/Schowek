package pl.memexurer.mschowek;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.memexurer.mschowek.model.UserManager;
import pl.memexurer.mschowek.schowek.SchowekItem;

import java.util.Arrays;

public class SchowekPlaceholder extends PlaceholderExpansion {
    private final UserManager<?> userManager;

    public SchowekPlaceholder(UserManager<?> userManager) {
        this.userManager = userManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "schowek";
    }

    @Override
    public @NotNull String getAuthor() {
        return "memexurer";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] paramas = params.split("_");
        if (paramas.length != 2)
            return null;

        int id;
        try {
            id = Integer.parseInt(paramas[1]);
        } catch (NumberFormatException exception) {
            return null;
        }

        SchowekItem item = Configuration.getInstance().getSchowekItem(id);
        if (item == null)
            return null;

        if (paramas[0].equalsIgnoreCase("count")) {
            return Integer.toString(userManager.getUser(player).getItemCount(item.getItemId()));
        } else if (paramas[0].equalsIgnoreCase("limit")) {
            return Integer.toString(item.getLimit());
        }
        return null;
    }
}
