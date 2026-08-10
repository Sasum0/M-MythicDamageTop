package me.sasumo.mythicdamagetop.commands;

import me.sasumo.mythicdamagetop.MythicDamageTopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminCommand implements CommandExecutor {

    private final MythicDamageTopPlugin plugin;

    public AdminCommand(MythicDamageTopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mythicdamagetop.admin")) {
            sender.sendMessage(ChatColor.RED + "No tenes permiso para usar este comando.");
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.RED + "Uso: /mdt reload");
            return true;
        }

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "MythicDamageTop recargado. " +
                plugin.getConfigManager().getMobConfigs().size() + " mob(s) cargado(s).");
        return true;
    }
}
