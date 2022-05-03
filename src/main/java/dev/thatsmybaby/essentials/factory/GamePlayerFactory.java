package dev.thatsmybaby.essentials.factory;

import dev.thatsmybaby.essentials.AbstractEssentials;
import dev.thatsmybaby.essentials.factory.provider.MysqlProvider;
import dev.thatsmybaby.essentials.object.GamePlayer;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class GamePlayerFactory extends MysqlProvider {

    @Getter private final static GamePlayerFactory instance = new GamePlayerFactory();

    @Override
    public void init(File file) {
        super.init(file);

        if (this.dataSource == null) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users(rowId INT PRIMARY KEY AUTO_INCREMENT, xuid TEXT, username VARCHAR(16), max_home_size INT)");

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadGamePlayer(String xuid, String name) {
        if (this.dataSource == null || this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            if (this.reconnect()) {
                this.loadGamePlayer(xuid, name);
            }

            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement;

            if (this.getTargetName(xuid) == null) {
                preparedStatement = connection.prepareStatement("INSERT INTO users (username, xuid, max_home_size) VALUES (?, ?, ?)");

                preparedStatement.setInt(3, AbstractEssentials.getInstance().getConfig().getInt("general.default-home-size"));
            } else {
                preparedStatement = connection.prepareStatement("UPDATE users SET username = ? WHERE xuid = ? ");
            }

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, xuid);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE xuid = ?");

            preparedStatement.setString(1, xuid);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                GamePlayer.add(xuid, new GamePlayer(
                        xuid,
                        name,
                        CrossServerTeleportFactory.getInstance().loadPlayerCrossServerLocations(xuid, true),
                        rs.getInt("max_home_size"),
                        true,
                        false,
                        null
                ));
            }

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTargetXuid(String name) {
        if (this.dataSource == null || this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            return this.reconnect() ? this.getTargetXuid(name) : null;
        }

        String xuid = null;

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");

            preparedStatement.setString(1, name);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                xuid = rs.getString("xuid");
            }

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return xuid;
    }

    public String getTargetName(String xuid) {
        if (this.dataSource == null || this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            return this.reconnect() ? this.getTargetName(xuid) : null;
        }

        String name = null;

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE xuid = ?");

            preparedStatement.setString(1, xuid);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                name = rs.getString("username");
            }

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return name;
    }

    public int getTargetMaxHomeSize(String xuid) {
        if (this.dataSource == null || this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            AbstractEssentials.getInstance().getLogger().warning("MySQL Provider was disconnected... Reconnecting.");

            return this.reconnect() ? this.getTargetMaxHomeSize(xuid) : 0;
        }

        int amount = 0;

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE xuid = ?");

            preparedStatement.setString(1, xuid);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                amount = rs.getInt("max_home_size");
            }

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amount;
    }

    public void updateMaxHomeSize(String xuid, int maxHomeSize) {
        if (this.dataSource == null) {
            return;
        }

        if (this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            AbstractEssentials.getInstance().getLogger().warning("MySQL Provider was disconnected... Reconnecting.");

            if (this.reconnect()) this.updateMaxHomeSize(xuid, maxHomeSize);

            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET max_home_size = ? WHERE xuid = ?");

            preparedStatement.setInt(1, maxHomeSize);
            preparedStatement.setString(2, xuid);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}