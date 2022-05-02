package dev.thatsmybaby.essentials.factory.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.thatsmybaby.essentials.AbstractEssentials;

import java.io.File;

public abstract class MysqlProvider {

    protected HikariDataSource dataSource = null;

    protected HikariConfig hikariConfig = null;

    public void init(File file) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }

        HikariConfig config = new HikariConfig(file.toString());

        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        this.dataSource = new HikariDataSource(this.hikariConfig = config);
    }

    protected boolean reconnect() {
        this.close();

        if (this.hikariConfig == null) {
            AbstractEssentials.getInstance().getLogger().critical("Can't reconnect because Hikari config is null...");

            return false;
        }

        try {
            this.dataSource = new HikariDataSource(this.hikariConfig);
        } catch (Exception e) {
            AbstractEssentials.getInstance().getLogger().critical("Can't reconnect because, reason: " + e.getMessage());

            return false;
        }

        return true;
    }

    public void close() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }
}