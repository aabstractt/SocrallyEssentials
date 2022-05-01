package dev.thatsmybaby.essentials.factory;

import cn.nukkit.level.Location;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.factory.provider.MysqlProvider;
import dev.thatsmybaby.essentials.object.CrossServerLocation;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class CrossServerTeleportFactory extends MysqlProvider {

    @Getter private final static CrossServerTeleportFactory instance = new CrossServerTeleportFactory();

    @Override
    public void init(File file) {
        super.init(file);

        if (this.dataSource == null) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS essentials_home (rowId INT PRIMARY KEY AUTO_INCREMENT, xuid VARCHAR(60), name TEXT, location TEXT)");

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CrossServerLocation createPlayerCrossServerLocation(String xuid, String homeName, Location location) {
        if (this.dataSource == null) {
            return null;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement;

            if (this.loadPlayerCrossServerLocation(xuid, homeName, true) != null) {
                preparedStatement = connection.prepareStatement("UPDATE essentials_home SET location = ? WHERE name = ? AND xuid = ?");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO essentials_home (location, name, xuid) VALUES (?, ?, ?)");
            }

            preparedStatement.setString(1, Placeholders.stringFromLocation(location));
            preparedStatement.setString(2, homeName.toLowerCase());
            preparedStatement.setString(3, xuid);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            return new CrossServerLocation(homeName, Placeholders.stringFromLocation(location), location);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, CrossServerLocation> loadPlayerCrossServerLocations(String xuid, boolean isXuid) {
        if (this.dataSource == null) {
            return null;
        }

        Map<String, CrossServerLocation> map = new HashMap<>();

        try (Connection connection = this.dataSource.getConnection()) {
            if (!isXuid && (xuid = GamePlayerFactory.getInstance().getTargetXuid(xuid)) == null) return null;

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM essentials_home WHERE xuid = ?");

            preparedStatement.setString(1, xuid);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("name"), new CrossServerLocation(
                        rs.getString("name"),
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

    public CrossServerLocation loadPlayerCrossServerLocation(String xuid, String homeName, boolean isXuid) {
        if (this.dataSource == null) {
            return null;
        }

        CrossServerLocation crossServerLocation = null;

        try (Connection connection = this.dataSource.getConnection()) {
            if (!isXuid && (xuid = GamePlayerFactory.getInstance().getTargetXuid(xuid)) == null) return null;

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM essentials_home WHERE xuid = ? AND name = ?");

            preparedStatement.setString(1, xuid);
            preparedStatement.setString(2, homeName);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                crossServerLocation = new CrossServerLocation(
                        rs.getString("name"),
                        rs.getString("location"),
                        Placeholders.locationFromString(rs.getString("location"))
                );
            }

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return crossServerLocation;
    }

    public int removePlayerCrossServerLocation(String xuid, String homeName, boolean isXuid) {
        if (this.dataSource == null) return -1;

        try (Connection connection = this.dataSource.getConnection()) {
            if (!isXuid && (xuid = GamePlayerFactory.getInstance().getTargetXuid(xuid)) == null) return -1;

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM essentials_home WHERE name = ? AND xuid = ?");

            preparedStatement.setString(1, homeName);
            preparedStatement.setString(2, xuid);

            int rowCount = preparedStatement.executeUpdate();
            preparedStatement.close();

            return rowCount;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}