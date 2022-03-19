package dev.thatsmybaby.essentials.factory;

import cn.nukkit.level.Position;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.factory.provider.MysqlProvider;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class HomeFactory extends MysqlProvider {

    @Getter private final static HomeFactory instance = new HomeFactory();

    @Override
    public void init(File file) {
        super.init(file);

        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS essentials_home (rowId INT PRIMARY KEY AUTO_INCREMENT, xuid VARCHAR(60), homeName TEXT, positionString TEXT)");

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createOrUpdateHome(String xuid, String homeName, Position position) {
        try (Connection connection = this.getConnection()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Position getHomePosition(String xuid, String homeName) {
        String string = null;

        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM essentials_home WHERE xuid = ? AND homeName = ?");

            preparedStatement.setString(1, xuid);
            preparedStatement.setString(2, homeName);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                string = rs.getString("positionString");
            }

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (string == null) {
            return null;
        }

        return Placeholders.stringToPosition(string);
    }

    public void removePlayerHome(String xuid, String homeName) {
        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM essentials_home WHERE xuid = ? AND homeName = ?");

            preparedStatement.setString(1, xuid);
            preparedStatement.setString(2, homeName);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}