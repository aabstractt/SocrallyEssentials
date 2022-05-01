package dev.thatsmybaby.essentials.factory;

import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.factory.provider.MysqlProvider;
import dev.thatsmybaby.essentials.object.CrossServerLocation;
import lombok.Getter;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HomeFactory extends MysqlProvider {

    @Getter private final static HomeFactory instance = new HomeFactory();

    @Override
    public void init(File file) {
        super.init(file);

        if (this.dataSource == null) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS essentials_home (rowId INT PRIMARY KEY AUTO_INCREMENT, xuid VARCHAR(60), homeName TEXT, positionString TEXT)");

            preparedStatement.executeUpdate();
            preparedStatement.close();

            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users(rowId INT PRIMARY KEY AUTO_INCREMENT, xuid TEXT, username VARCHAR(16))");

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createOrUpdateUser(String xuid, String name) throws SQLException {
        if (this.dataSource == null) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement;

            if (this.getTargetXuid(xuid) == null) {
                preparedStatement = connection.prepareStatement("INSERT INTO users (username, xuid) VALUES (?, ?)");
            } else {
                preparedStatement = connection.prepareStatement("UPDATE users SET username = ? WHERE xuid = ?");
            }

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, xuid);

            preparedStatement.executeUpdate();
            preparedStatement.close();
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

    public void createOrUpdateHome(String xuid, String homeName, Position position) throws SQLException {
        if (this.dataSource == null) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement;

            if (this.getHomePosition(xuid, homeName) != null) {
                preparedStatement = connection.prepareStatement("UPDATE essentials_home SET positionString = ? WHERE homeName = ? AND xuid = ?");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO essentials_home (positionString, homeName, xuid) VALUES (?, ?, ?)");
            }

            preparedStatement.setString(1, Placeholders.positionToString(position));
            preparedStatement.setString(2, homeName.toLowerCase());
            preparedStatement.setString(3, xuid);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }

    public CrossServerLocation createPlayerCrossServerLocation(String xuid, String homeName, Location location) {
        if (this.dataSource == null) {
            return null;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement;

            if (this.getHomePosition(xuid, homeName) != null) {
                preparedStatement = connection.prepareStatement("UPDATE essentials_home SET positionString = ? WHERE homeName = ? AND xuid = ?");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO essentials_home (positionString, homeName, xuid) VALUES (?, ?, ?)");
            }

            preparedStatement.setString(1, Placeholders.positionToString(location));
            preparedStatement.setString(2, homeName.toLowerCase());
            preparedStatement.setString(3, xuid);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            return new CrossServerLocation(homeName, Placeholders.positionToString(location), location);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, CrossServerLocation> loadPlayerCrossServerLocations(String xuid) {
        if (this.dataSource == null) {
            return new HashMap<>();
        }

        Map<String, CrossServerLocation> map = new HashMap<>();

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM essentials_home WHERE xuid = ?");

            preparedStatement.setString(1, xuid);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("homeName"), new CrossServerLocation(
                        rs.getString("homeName"),
                        rs.getString("location"),
                        Placeholders.locationFromString(rs.getString("location"))
                ));
            }

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public Position getHomePosition(String xuid, String homeName) throws SQLException {
        String string = null;

        if (this.dataSource == null) {
            return null;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM essentials_home WHERE xuid = ? AND homeName = ?");

            preparedStatement.setString(1, xuid);
            preparedStatement.setString(2, homeName);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                string = rs.getString("positionString");
            }

            rs.close();
            preparedStatement.close();
        }

        if (string == null) {
            return null;
        }

        return Placeholders.stringToPosition(string);
    }

    public void removePlayerHome(String xuid, String homeName) throws SQLException {
        if (this.dataSource == null) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM essentials_home WHERE xuid = ? AND homeName = ?");

            preparedStatement.setString(1, xuid);
            preparedStatement.setString(2, homeName);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }

    public List<Map<String, String>> getPlayerHomeList(String xuid) throws SQLException {
        List<Map<String, String>> list = new ArrayList<>();

        if (this.dataSource == null) {
            return list;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM essentials_home WHERE xuid = ?");

            preparedStatement.setString(1, xuid);

            ResultSet rs = preparedStatement.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();

                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    map.put(metaData.getColumnName(i), rs.getString(i));
                }

                list.add(map);
            }

            rs.close();
            preparedStatement.close();
        }

        return list;
    }

    public int getPlayerMaxHome(String xuid) throws SQLException {
        if (this.dataSource == null) {
            return 0;
        }

        int maxHome = 0;

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE xuid = ?");

            preparedStatement.setString(1, xuid);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                maxHome = rs.getInt("max_home");
            }

            rs.close();
            preparedStatement.close();
        }

        return maxHome;
    }
}