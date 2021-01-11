package pl.memexurer.mschowek.model.impl.bulk;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.database.DatabaseCredentials;
import pl.memexurer.database.PluginDatabaseConnection;
import pl.memexurer.mschowek.util.BinaryUtils;

import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class SqlUserManager extends BulkUserManager {
    private final Connection connection;

    public SqlUserManager(JavaPlugin plugin, DatabaseCredentials credentials) {
        this(plugin, PluginDatabaseConnection.findDatabaseService(credentials, plugin).getConnection());

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `schowki` (PlayerUniqueId varchar(36), Schoweks tinyblob, PRIMARY KEY (PlayerUniqueId))");

            ResultSet set = statement.executeQuery("SELECT * FROM `schowki`");
            while (set.next())
                super.userMap.put(
                        UUID.fromString(set.getString("PlayerUniqueId")),
                        new BulkUserDataModel(BinaryUtils.readIntegerMap(set.getBytes("Schoweks"))))
                );
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public SqlUserManager(Plugin plugin, Connection connection) {
        super(plugin);
        this.connection = connection;
    }

    @Override
    public void save() {
        try (PreparedStatement statement = connection.prepareStatement("REPLACE INTO `schowki` VALUES (?, ?)")) {
            for (Map.Entry<UUID, BulkUserDataModel> dataModel : super.userMap.entrySet())
                if (dataModel.getValue().isMarkUpdated()) {
                    statement.setString(1, dataModel.getKey().toString());
                    statement.setBytes(2, dataModel.getValue().getSerializedValue());
                    statement.addBatch();
                }

            statement.executeBatch();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }
}
