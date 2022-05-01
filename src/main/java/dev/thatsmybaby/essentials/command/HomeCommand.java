package dev.thatsmybaby.essentials.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.essentials.EssentialsLoader;
import dev.thatsmybaby.essentials.Placeholders;
import dev.thatsmybaby.essentials.object.CrossServerLocation;
import dev.thatsmybaby.essentials.object.GamePlayer;

import java.util.HashMap;
import java.util.Map;

public final class HomeCommand extends Command {

    private final static Map<String, TaskHandler> taskHandlerMap = new HashMap<>();

    public HomeCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Run this command in-game");

            return false;
        }

        if (!this.testPermission(commandSender)) {
            return false;
        }

        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /home <name>");

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of((Player) commandSender);

        if (gamePlayer == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("UNEXPECTED_ERROR"));

            return false;
        }

        if (gamePlayer.isAlreadyTeleporting()) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_ALREADY_TELEPORTING"));

            return false;
        }

        CrossServerLocation crossServerLocation = gamePlayer.getCrossServerLocation(args[0]);

        if (crossServerLocation == null) {
            commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_NOT_FOUND", args[0]));

            return false;
        }

        commandSender.sendMessage(Placeholders.replacePlaceholders("HOME_TELEPORTING", args[0]));

        doTeleportQueue((Player) commandSender, gamePlayer, crossServerLocation.getLocationSerialized());

        return false;
    }

    public static void doTeleportQueue(Player player, GamePlayer gamePlayer, String locationSerialized) {
        gamePlayer.setAlreadyTeleporting(true);

        Location initialLocation = player.getLocation();

        final int[] time = {EssentialsLoader.getInstance().getConfig().getInt("general.teleport_time") + 1};

        taskHandlerMap.put(player.getLoginChainData().getXUID(), Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(EssentialsLoader.getInstance(), () -> {
            TaskHandler taskHandler = taskHandlerMap.get(player.getLoginChainData().getXUID());

            if (!gamePlayer.isAlreadyTeleporting()) {
                if (taskHandler != null) taskHandler.cancel();

                return;
            }

            if (hasMoved(player.getLocation(), initialLocation)) {
                player.sendMessage(Placeholders.replacePlaceholders("TELEPORTING_CANCELLED_MOVEMENT"));

                gamePlayer.setAlreadyTeleporting(false);

                return;
            }

            if (!player.isOnline()) {
                gamePlayer.setAlreadyTeleporting(false);

                return;
            }

            time[0]--;

            if (time[0] > 0) {
                player.sendMessage(Placeholders.replacePlaceholders("TELEPORTING_COUNTDOWN", Integer.toString(time[0])));

                return;
            }

            gamePlayer.setAlreadyTeleporting(false);

            player.teleport(Placeholders.locationFromString(locationSerialized));
        }, 5, 20));
    }

    // This returns if the player has moved during a timed teleport
    private static boolean hasMoved(Location location, Location initialLocation) {
        double xDiff = makePositive(initialLocation.getX() - location.getX());
        double yDiff = makePositive(initialLocation.getY() - location.getY());
        double zDiff = makePositive(initialLocation.getZ() - location.getZ());

        return (xDiff + yDiff + zDiff) > 0.1;
    }

    // This converts a negative to a positive double, used in checking if a player has moved
    private static double makePositive(double d) {
        if (d < 0) {
            d = d * -1D;
        }
        return d;
    }
}