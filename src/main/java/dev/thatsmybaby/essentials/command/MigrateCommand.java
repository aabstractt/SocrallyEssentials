package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.AbstractEssentials;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.CrossServerTeleportFactory;
import dev.thatsmybaby.essentials.object.CrossServerLocation;
import dev.thatsmybaby.essentials.object.GamePlayer;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class MigrateCommand extends Command {

    public MigrateCommand(String name, String description) {
        super(name, description);

        this.setPermission("essentials.command.migrate");
    }

    @Override @SuppressWarnings("unchecked")
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Run this command in-game");

            return false;
        }

        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /" + s + " help");

            return false;
        }

        if (!this.testPermission(commandSender)) return false;

        if (args[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("MIGRATE_EXPLAIN"));

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of((Player) commandSender);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        if (gamePlayer.getCrossServerLocation(args[0]) == null && gamePlayer.getCrossServerLocationMap().size() >= gamePlayer.getMaxHomeSize()) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("MAX_HOMES_REACHED", String.valueOf(gamePlayer.getCrossServerLocationMap().size())));

            return false;
        }

        Config config = new Config(new File(AbstractEssentials.getInstance().getDataFolder(), "homes.yml"));

        if (!config.exists(((Player) commandSender).getUniqueId().toString())) {
            commandSender.sendMessage(TextFormat.RED + "You don't have any home registered.");

            return false;
        }

        Map<String, List<Object>> map = (Map<String, List<Object>>) config.get(((Player) commandSender).getUniqueId().toString());

        if (map.isEmpty()) {
            commandSender.sendMessage(TextFormat.RED + "You don't have any home registered.");

            return false;
        }

        List<Object> list = map.get(args[0].toLowerCase());
        if (list == null || list.size() != 6) {
            commandSender.sendMessage(TextFormat.RED + "You don't have any home registered.");

            return false;
        }

        Location location = new Location((double) list.get(1), (double) list.get(2), (double) list.get(3), (double) list.get(4), (double) list.get(5), Server.getInstance().getLevelByName((String) list.get(0)));
        TaskUtils.runAsync(() -> {
            CrossServerLocation crossServerLocation = CrossServerTeleportFactory.getInstance().createPlayerCrossServerLocation(((Player) commandSender).getLoginChainData().getXUID(), args[0], location);

            if (crossServerLocation == null) {
                commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

                return;
            }

            commandSender.sendMessage(Placeholders.replacePlaceholders("SET_HOME_SUCCESSFULLY_" + (gamePlayer.getCrossServerLocation(args[0]) == null ? "CREATED" : "UPDATED"), args[0]));

            gamePlayer.setCrossServerLocation(args[0], crossServerLocation);
        });

        map.remove(args[0].toLowerCase());

        config.set(((Player) commandSender).getUniqueId().toString(), map);
        config.save();

        // Clear cache?
        config.getAll().clear();
        list.clear();
        map.clear();

        return false;
    }
}