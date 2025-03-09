package net.ad1200am.commandCommands.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

public class CommandUtils {

    public static void incorrectArgument(CommandSender sender, String command, String[] args, int split) {
        StringBuilder message1 = new StringBuilder(command);
        for (int i = 0; i < split; i++) {
            message1.append(" ").append(args[i]);
        }
        if (message1.length() > 9) {
            message1 = new StringBuilder("..." + message1.substring(message1.length() - 9));
        }
        StringBuilder message2 = new StringBuilder(args[split]);
        for (int i = split + 1; i < args.length; i++) {
            message2.append(" ").append(args[i]);
        }

        sender.sendMessage(Component.text("Incorrect argument for command", NamedTextColor.RED));
        sender.sendMessage(Component.text(message1 + " ", NamedTextColor.GRAY)
                .append(Component.text(message2.toString(), NamedTextColor.RED, TextDecoration.UNDERLINED)
                        .append(Component.text("<--[HERE]", NamedTextColor.RED).decoration(TextDecoration.UNDERLINED, false))));
    }
}
