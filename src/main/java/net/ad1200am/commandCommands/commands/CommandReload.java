package net.ad1200am.commandCommands.commands;

import net.ad1200am.commandCommands.readers.Config;
import net.ad1200am.commandCommands.readers.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandReload implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (sender instanceof Player player && !Permissions.hasPermission(player, Permissions.Level.GAME_MASTER)) {
            player.sendMessage(Component.text("You do not have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        Config.reload();
        CommandRegister.reload();
        sender.sendMessage("Reloaded custom commands");
        sender.sendMessage(Component.text("[Info]: ", NamedTextColor.YELLOW).append(Component.text("Added, or removed commands will display correctly only after rejoining!", NamedTextColor.WHITE)));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
