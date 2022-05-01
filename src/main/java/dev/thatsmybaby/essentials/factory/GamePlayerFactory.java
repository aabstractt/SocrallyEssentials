package dev.thatsmybaby.essentials.factory;

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
        if (this.dataSource == null) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement;

            if (this.getTargetName(xuid) == null) {
                preparedStatement = connection.prepareStatement("INSERT INTO users (username, xuid, max_home_size) VALUES (?, ?, ?)");
            } else {
                preparedStatement = connection.prepareStatement("UPDATE users SET username = ? WHERE xuid = ? ");
            }

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, xuid);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement("SELECT max_home_size FROM users WHERE xuid = ?");

            preparedStatement.setString(1, xuid);
            ResultSet rs = preparedStatement.executeQuery();

            GamePlayer.add(xuid, new GamePlayer(
                    xuid,
                    name,
                    CrossServerTeleportFactory.getInstance().loadPlayerCrossServerLocations(xuid, true),
                    rs.getInt("max_home_size"),
                    false
            ));

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTargetXuid(String name) throws SQLException {
        String xuid = null;

        if (this.dataSource == null) {
            return null;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");

            preparedStatement.setString(1, name);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                xuid = rs.getString("xuid");
            }

            rs.close();
            preparedStatement.close();
        }

        return xuid;
    }

    public String getTargetName(String xuid) throws SQLException {
        String name = null;

        if (this.dataSource == null) {
            return null;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE xuid = ?");

            preparedStatement.setString(1, xuid);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                name = rs.getString("username");
            }

            rs.close();
            preparedStatement.close();
        }

        return name;
    }
}