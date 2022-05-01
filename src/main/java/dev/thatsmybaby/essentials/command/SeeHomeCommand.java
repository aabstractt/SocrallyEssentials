package dev.thatsmybaby.essentials.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.TaskUtils;
import dev.thatsmybaby.essentials.factory.HomeFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public final class SeeHomeCommand extends Command {

    public SeeHomeCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!this.testPermission(commandSender)) {
            return false;
        }

        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /" + s + " <player>");

            return false;
        }

        TaskUtils.runAsync(() -> {
            try {
                String xuid = HomeFactory.getInstance().getTargetXuid(args[0]);

                if (xuid == null) {
                    commandSender.sendMessage(TextFormat.RED + args[0] + " not found");

                    return;
                }

                List<Map<String, String>> list = HomeFactory.getInstance().getPlayerHomeList(xuid);

                if (list.isEmpty()) {
                    commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOMES_EMPTY", args[0]));

                    return;
                }

                commandSender.sendMessage(Placeholders.replacePlaceholders("PLAYER_HOMES_LIST", args[0], String.valueOf(list.size()), String.valueOf(HomeFactory.getInstance().getPlayerMaxHome(xuid))));

                for (Map<String, String> map : list) {
                    Position position = Placeholders.stringToPosition(map.get("positionString"));

                    commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_PLACEHOLDER", map.get("homeName"), String.valueOf(position.getFloorX()), String.valueOf(position.getFloorY()), String.valueOf(position.getFloorZ()), position.getLevelName()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return false;
    }
}