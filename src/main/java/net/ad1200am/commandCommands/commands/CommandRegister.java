package net.ad1200am.commandCommands.commands;

import net.ad1200am.commandCommands.Main;
import net.ad1200am.commandCommands.readers.Config;
import net.ad1200am.commandCommands.readers.Permissions;
import net.ad1200am.commandCommands.utils.CommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CommandRegister  {

    private static List<String> commands = new ArrayList<>();

    public static void reload() {
        for (String name : commands) {
            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                bukkitCommandMap.setAccessible(true);
                CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Main.getPlugin().getServer());
                Command cmd = commandMap.getCommand(name);
                if (cmd == null) continue;
                Map<String, Command> knownCommands = commandMap.getKnownCommands();
                knownCommands.remove(cmd.getName());
                for (String alias : cmd.getAliases()) {
                    if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(Main.getPlugin().getName())) {
                        knownCommands.remove(alias);
                    }
                }
                Main.getPlugin().getLogger().log(Level.INFO, "Removing custom command \"" + name + "\"");
            } catch (Exception e) {
                Main.getPlugin().getLogger().warning(e.toString());
            }
        }
        commands = new ArrayList<>();
        setUpCommands();
    }

    public static void setUpCommands() {
        ConfigurationSection section = Config.getFileConfiguration().getConfigurationSection("commands");
        if (section == null) return;
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Main.getPlugin().getServer());

            for (String name : section.getKeys(false)) {
                CommandNode node = new CommandNode(name);
                commandMap.register(Main.getPlugin().getName(), new CommandDefinition(name, node.getPermissionLevel()));
                commands.add(name);
                Main.getPlugin().getLogger().log(Level.INFO, "Adding custom command \"" + name + "\" for all with permission \"" + node.getPermissionLevel() + "\"");
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            Main.getPlugin().getLogger().warning(e.toString());
        }
    }

    private static class CommandDefinition extends BukkitCommand {

        private final CommandNode node0;

        public CommandDefinition(String name, Permissions.Level permission) {
            super(name);
            this.node0 = new CommandNode(name);
            setPermission(permission.getPermission());
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String cmd, @NotNull String @NotNull [] args) {
            CommandNode node = node0;
            for (int i = 0; i < args.length; i++) {
                String argument = args[i];
                if (node.isLeaf() || !node.getBranchNames().contains(argument)) {
                    if (sender instanceof Player player && !Permissions.hasPermission(player, node.getPermissionLevel())) {
                        player.sendMessage(Component.text("You do not have permission to use this command!", NamedTextColor.RED));
                        return true;
                    }
                    CommandUtils.incorrectArgument(sender, cmd, args, i);
                    return true;
                }
                node = node.getBranches().stream().filter(b -> b.getName().equals(argument)).toList().getFirst();
            }
            if (sender instanceof Player player && !Permissions.hasPermission(player, node.getPermissionLevel())) {
                player.sendMessage( Component.text("You do not have permission to use this command!", NamedTextColor.RED));
                return true;
            }
            List<String> commands = node.getCommand();
            if (commands == null) return true;
            if (sender instanceof Player player) {
                for (String command : commands) {
                    command = "execute as " + player.getUniqueId() + " at @s run " + command;
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
                return true;
            }
            for (String command : commands) {
                Bukkit.dispatchCommand(sender, command);
            }
            return true;
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
            CommandNode node = node0;
            if (args.length == 0) return List.of();
            for (int i = 0; i < args.length - 1; i++) {
                String argument = args[i];
                if (node.isLeaf() || !node.getBranchNames().contains(argument)) {
                    return List.of();
                }
                node = node.getBranches().stream().filter(b -> b.getName().equals(argument)).toList().getFirst();
            }
            List<String> results = new ArrayList<>();
            for (CommandNode finalNode : node.getBranches()) {
                if (finalNode.getName().toLowerCase().startsWith(args[args.length - 1]) && (!(sender instanceof Player player) || Permissions.hasPermission(player, finalNode.getPermissionLevel()))) results.add(finalNode.getName());
            }
            return results;
        }
    }
}
