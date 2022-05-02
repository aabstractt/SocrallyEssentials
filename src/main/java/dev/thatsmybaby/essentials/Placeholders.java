package dev.thatsmybaby.essentials;

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Placeholders {

    public static Map<String, Object> messages = new HashMap<>();

    public static String replacePlaceholders(String text, String... args) {
        if (messages.isEmpty()) {
            messages = (new Config(new File(AbstractEssentials.getInstance().getDataFolder(), "messages.yml"))).getAll();
        }

        if (messages.containsKey(text)) {
            text = messages.get(text).toString();
        }

        for (int i = 0; i < args.length; i++) {
            text = text.replaceAll("\\{%" + i + "}", args[i]);
        }

        return TextFormat.colorize(text);
    }

    public static String stringFromPosition(Position position) {
        return String.format("%s;%s;%s;%s", position.getFloorX(), position.getFloorY(), position.getFloorZ(), position.getLevel().getFolderName());
    }

    public static Position positionFromString(String string) {
        String[] split = string.split(";");

        if (split.length < 4) {
            throw new PluginException("Invalid string length");
        }

        if (!Server.getInstance().loadLevel(split[3])) {
            throw new PluginException("World " + split[3] + " not found");
        }

        return new Position(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Server.getInstance().getLevelByName(split[3]));
    }

    public static String stringFromLocation(Location location) {
        return stringFromPosition(location) + String.format(";%s;%s", location.yaw, location.pitch);
    }

    public static Location locationFromString(String string) {
        String[] split = string.split(";");

        Position position = positionFromString(string);
        return Location.fromObject(position, position.getValidLevel(), Double.parseDouble(split[4]), Double.parseDouble(split[5]));
    }

    public static boolean isNumber(String parsed) {
        try {
            Integer.parseInt(parsed);

            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}