package me.sasumo.mythicdamagetop.listeners;

import me.sasumo.mythicdamagetop.MythicDamageTopPlugin;
import me.sasumo.mythicdamagetop.config.MobConfig;
import me.sasumo.mythicdamagetop.util.MessageUtil;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobSpawnListener implements Listener {

    private final MythicDamageTopPlugin plugin;

    public MobSpawnListener(MythicDamageTopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMythicMobSpawn(MythicMobSpawnEvent event) {
        String mobType = event.getMob().getType().getInternalName();

        MobConfig mobConfig = plugin.getConfigManager().getMobConfig(mobType);
        if (mobConfig == null || !mobConfig.isEnabled()) return;

        List<String> lines = mobConfig.getSpawnMessageLines();
        if (lines.isEmpty()) return;

        Location loc = event.getMob().getEntity().getBukkitEntity().getLocation();

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%mob_name%", mobType);
        placeholders.put("%world%", loc.getWorld() != null ? loc.getWorld().getName() : "?");
        placeholders.put("%x%", String.valueOf(loc.getBlockX()));
        placeholders.put("%y%", String.valueOf(loc.getBlockY()));
        placeholders.put("%z%", String.valueOf(loc.getBlockZ()));

        int delay = plugin.getConfigManager().getBroadcastLineDelayTicks();
        int tick = 0;
        for (String line : lines) {
            String parsed = MessageUtil.color(MessageUtil.replace(line, placeholders));
            int scheduledTick = tick;
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.broadcastMessage(parsed), scheduledTick);
            tick += delay;
        }
    }
}
