package pl.memexurer.mschowek.model.impl.bulk;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.mschowek.util.BinaryUtils;

import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class SqlUserManager extends BulkUserManager {
    private final Connection connection;
    private final HikariDataSource dataSource;

    public SqlUserManager(JavaPlugin plugin, ConfigurationSection section) {
        this(plugin, getDataSource(section));

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `schowki` (PlayerUniqueId varchar(36), Schoweks tinyblob, PRIMARY KEY (PlayerUniqueId))");

            ResultSet set = statement.executeQuery("SELECT * FROM `schowki`");
            while (set.next())
                super.userMap.put(
                        UUID.fromString(set.getString("PlayerUniqueId")),
                        new BulkUserDataModel(BinaryUtils.readIntegerMap(set.getBytes("Schoweks")))
                );
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public SqlUserManager(Plugin plugin, HikariDataSource dataSource) {
        super(plugin);
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.dataSource = dataSource;
    }

    private static HikariDataSource getDataSource(ConfigurationSection section) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + section.getString("ip") + ":" + section.getInt("port") + "/" + section.getString("name"));
        config.setUsername(section.getString("user"));
        config.setPassword(section.getString("password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        return new HikariDataSource(config);
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

    @Override
    public void shutdown() {
        dataSource.close();
    }
}
