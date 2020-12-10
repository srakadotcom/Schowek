package pl.memexurer.mschowek.model;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class UserManager<T extends UserDataModel> {
    protected final Map<UUID, T> userMap = new HashMap<>();

    public T getUser(Player player) {
        T t = userMap.get(player.getUniqueId());
        if(t == null) {
            t = createUser(player);
            userMap.put(player.getUniqueId(), t);
        }

        return t;
    }

    protected abstract T createUser(Player player);
}
