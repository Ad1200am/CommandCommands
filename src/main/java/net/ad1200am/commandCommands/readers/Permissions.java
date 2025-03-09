package net.ad1200am.commandCommands.readers;

import com.google.gson.Gson;
import net.ad1200am.commandCommands.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Permissions implements Listener {

    public enum Level {
        ALL("server.all"),
        MODERATOR("server.moderator"),
        GAME_MASTER("server.gamemaster"),
        ADMIN("server.admin"),
        OWNER("server.owner");

        private final String level;

        Level(String level) {
            this.level = level;
        }

        public String getPermission() {
            return level;
        }
    }

    private static Map<UUID, Level> permissionLevels = getPermissions();
    private static boolean reloadOnUse = false;
    private static final List<String> reloadingCommands = List.of("/op", "/deop", "op", "deop");

    public static void reloadPermissions() {
        permissionLevels = getPermissions();
    }

    public static Map<UUID, Level> getPermissions() {
        Map<UUID, Level> output = new HashMap<>();
        try {
            List<?> entries = new Gson().fromJson(Files.readString(Paths.get("ops.json")), ArrayList.class);
            /*for (Level level : Level.values()) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addAttachment(Main.getPlugin()).unsetPermission(level.getPermission());
                }
            }*/
            for (Object entry : entries) {
                if (!(entry instanceof Map<?, ?> map)) continue;
                Map<String, Object> values = new HashMap<>();
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    if (!(e.getKey() instanceof String s)) continue;
                    values.put(s, e.getValue());
                }
                short s = (values.get("level") == null) ? 0 : (short) ((Double) values.get("level")).intValue();
                Level level = getLevel(s);
                UUID uuid = UUID.fromString((String) values.get("uuid"));
                output.put(uuid, level);
                //Player player = Bukkit.getPlayer(uuid);
                //if (player != null) player.addAttachment(Main.getPlugin()).setPermission(level.getPermission(), true);
            }
        } catch (IOException ignored) {
            Main.getPlugin().getLogger().warning("Could not read ops.json. Assuming everyone has permission level of 0!");
        }
        return output;
    }

    public static void setReloadOnUse(boolean value) {
        reloadOnUse = value;
    }

    public static Level getPermissionLevel(OfflinePlayer player) {
        if (reloadOnUse) reloadPermissions();
        if (!permissionLevels.containsKey(player.getUniqueId())) return Level.ALL;
        return permissionLevels.get(player.getUniqueId());
    }

    public static boolean hasPermission(Player player, Level level) {
        return switch (getPermissionLevel(player)) {
            case OWNER -> true;
            case ADMIN -> level != Level.OWNER;
            case GAME_MASTER -> level != Level.OWNER && level != Level.ADMIN;
            case MODERATOR -> level == Level.ALL || level == Level.MODERATOR;
            default -> level == Level.ALL;
        };
    }

    public static Level getLevel(short value) {
        return switch (value) {
            case 0 -> Level.ALL;
            case 1 -> Level.MODERATOR;
            case 2 -> Level.GAME_MASTER;
            case 3 -> Level.ADMIN;
            default -> Level.OWNER;
        };
    }

    @EventHandler(ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        String cmd = event.getCommand().split(" ")[0];
        if (!reloadingCommands.contains(cmd)) return;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), Permissions::reloadPermissions, 0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0];
        if (!reloadingCommands.contains(cmd)) return;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), Permissions::reloadPermissions, 0);
    }

    /*@EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Level level : Level.values()) {
            player.addAttachment(Main.getPlugin()).unsetPermission(level.getPermission());
        }
        player.addAttachment(Main.getPlugin()).setPermission(getPermissionLevel(player).getPermission(), true);
    }*/
}
