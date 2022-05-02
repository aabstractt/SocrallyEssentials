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

    private final Map<String, CrossServerLocation> crossServerLocationMap = new HashMap<>();

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

            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS essentials_warps (rowId INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(30), location TEXT)");

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.loadWarpCrossServerLocations();
    }

    public CrossServerLocation createPlayerCrossServerLocation(String xuid, String homeName, Location location) {
        if (this.dataSource == null) {
            return null;
        }

        if (this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            return this.reconnect() ? this.createPlayerCrossServerLocation(xuid, homeName, location) : null;
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

        if (this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            return this.reconnect() ? this.loadPlayerCrossServerLocations(xuid, isXuid) : null;
        }

        Map<String, CrossServerLocation> map = new HashMap<>();

        try (Connection connection = this.dataSource.getConnection()) {
            if (!isXuid && (xuid = GamePlayerFactory.getInstance().getTargetXuid(xuid)) == null) return null;

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM essentials_home WHERE xuid = ?");

            preparedStatement.setString(1, xuid);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("name").toLowerCase(), new CrossServerLocation(
                        rs.getString("name"),
                        rs.getString("location"),
                        Placeholders.locationFromString(rs.getString("location"))
                ));
            }

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public CrossServerLocation loadPlayerCrossServerLocation(String xuid, String homeName, boolean isXuid) {
        if (this.dataSource == null) {
            return null;
        }

        if (this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            return this.reconnect() ? this.loadPlayerCrossServerLocation(xuid, homeName, isXuid) : null;
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
            e.printStackTrace();
        }

        return crossServerLocation;
    }

    public int removePlayerCrossServerLocation(String name, String homeName) {
        if (this.dataSource == null) return -1;

        if (this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            return this.reconnect() ? this.removePlayerCrossServerLocation(name, homeName) : -1;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            String xuid = GamePlayerFactory.getInstance().getTargetXuid(name);

            if (xuid == null) return -1;

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM essentials_home WHERE name = ? AND xuid = ?");

            preparedStatement.setString(1, homeName);
            preparedStatement.setString(2, xuid);

            int rowCount = preparedStatement.executeUpdate();
            preparedStatement.close();

            System.out.println("Row count affected is " + rowCount);

            return rowCount;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void loadWarpCrossServerLocations() {
        if (this.dataSource == null) {
            return;
        }

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM essentials_warps");

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                CrossServerLocation crossServerLocation = new CrossServerLocation(
                        rs.getString("name"),
                        rs.getString("location"),
                        Placeholders.locationFromString(rs.getString("location"))
                );

                this.crossServerLocationMap.put(crossServerLocation.getName().toLowerCase(), crossServerLocation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CrossServerLocation getWarpCrossServerLocation(String name) {
        return this.crossServerLocationMap.get(name.toLowerCase());
    }

    public void createWarpCrossServerLocation(String name, Location location) {
        if (this.dataSource == null) {
            return;
        }

        if (this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            if (this.reconnect()) this.createWarpCrossServerLocation(name, location);

            return;
        }

        this.crossServerLocationMap.put(name.toLowerCase(), new CrossServerLocation(name, Placeholders.stringFromLocation(location), location));

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement;

            if (this.getWarpCrossServerLocation(name) == null) {
                preparedStatement = connection.prepareStatement("INSERT INTO essentials_warps (location, name) VALUES (?, ?)");
            } else {
                preparedStatement = connection.prepareStatement("UPDATE essentials_warps SET location = ? WHERE name = ?");
            }

            preparedStatement.setString(1, Placeholders.stringFromLocation(location));
            preparedStatement.setString(2, name);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeWarpCrossServerLocation(String name) {
        if (this.dataSource == null) {
            return;
        }

        if (this.dataSource.isClosed() || !this.dataSource.isRunning()) {
            if (this.reconnect()) this.removeWarpCrossServerLocation(name);

            return;
        }

        this.crossServerLocationMap.remove(name.toLowerCase());

        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM essentials_warps WHERE name = ?");

            preparedStatement.setString(1, name);
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}