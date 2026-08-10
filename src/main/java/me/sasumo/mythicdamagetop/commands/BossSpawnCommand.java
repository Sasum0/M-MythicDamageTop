package me.sasumo.mythicdamagetop.commands;

import me.sasumo.mythicdamagetop.MythicDamageTopPlugin;
import me.sasumo.mythicdamagetop.config.MobConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * /bossspawn <mobId> <x> <y> <z> <mundo> [cantidad]
 *
 * Ejemplo: /bossspawn DragonKing 100.5 10.1 -100 Towny
 */
public class BossSpawnCommand implements CommandExecutor, TabCompleter {

    private final MythicDamageTopPlugin plugin;

    public BossSpawnCommand(MythicDamageTopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(plugin.getConfigManager().getBossSpawnPermission())) {
            sender.sendMessage(ChatColor.RED + "No tenes permiso para usar este comando.");
            return true;
        }

        if (args.length < 5) {
            sender.sendMessage(ChatColor.RED + "Uso: /" + label + " <mobId> <x> <y> <z> <mundo> [cantidad]");
            return true;
        }

        String mobId = args[0];
        MobConfig mobConfig = plugin.getConfigManager().getMobConfig(mobId);
        if (mobConfig == null || !mobConfig.isEnabled()) {
            sender.sendMessage(ChatColor.RED + "El mob '" + mobId + "' no esta configurado o esta deshabilitado en mobs.yml.");
            return true;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            z = Double.parseDouble(args[3]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Coordenadas invalidas.");
            return true;
        }

        World world = Bukkit.getWorld(args[4]);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "El mundo '" + args[4] + "' no existe.");
            return true;
        }

        int amount = mobConfig.getDefaultAmount();
        if (args.length >= 6 && plugin.getConfigManager().isAllowAmountArgument()) {
            try {
                amount = Integer.parseInt(args[5]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Cantidad invalida.");
                return true;
            }
        }
        amount = Math.max(1, Math.min(amount, mobConfig.getMaxAmount()));

        Optional<MythicMob> mythicMobOpt = MythicBukkit.inst().getMobManager().getMythicMob(mobId);
        if (mythicMobOpt.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "MythicMobs no reconoce el mob '" + mobId + "'. Revisa el Internal Name.");
            return true;
        }

        Location location = new Location(world, x, y, z);
        MythicMob mythicMob = mythicMobOpt.get();

        int spawned = 0;
        for (int i = 0; i < amount; i++) {
            ActiveMob activeMob = mythicMob.spawn(BukkitAdapter.adapt(location), 1);
            if (activeMob != null) spawned++;
        }

        sender.sendMessage(ChatColor.GREEN + "Spawneado x" + spawned + " " + mobId
                + " en " + world.getName() + " (" + x + ", " + y + ", " + z + ").");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(plugin.getConfigManager().getMobConfigs().keySet());
        } else if (args.length == 5) {
            for (World world : Bukkit.getWorlds()) {
                completions.add(world.getName());
            }
        }

        return completions;
    }
}
