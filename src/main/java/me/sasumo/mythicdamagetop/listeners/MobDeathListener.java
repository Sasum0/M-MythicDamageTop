package me.sasumo.mythicdamagetop.listeners;

import me.sasumo.mythicdamagetop.MythicDamageTopPlugin;
import me.sasumo.mythicdamagetop.combat.CombatSession;
import me.sasumo.mythicdamagetop.config.MobConfig;
import me.sasumo.mythicdamagetop.config.RewardConfig;
import me.sasumo.mythicdamagetop.util.MessageUtil;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MobDeathListener implements Listener {

    private final MythicDamageTopPlugin plugin;

    public MobDeathListener(MythicDamageTopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        String mobType = event.getMob().getType().getInternalName();

        MobConfig mobConfig = plugin.getConfigManager().getMobConfig(mobType);
        if (mobConfig == null || !mobConfig.isEnabled()) return;

        UUID mobUniqueId = event.getMob().getUniqueId();
        CombatSession session = plugin.getCombatManager().getSession(mobUniqueId);

        if (session == null || session.getDamageMap().isEmpty()) {
            plugin.getCombatManager().removeSession(mobUniqueId);
            return;
        }

        double totalDamage = session.getTotalDamage();

        List<Map.Entry<UUID, Double>> sorted = session.getDamageMap().entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        // name, damage formateado, porcentaje formateado (para el broadcast)
        List<String[]> topEntries = new ArrayList<>();

        int limit = Math.min(sorted.size(), mobConfig.getTopSize());
        for (int i = 0; i < limit; i++) {
            Map.Entry<UUID, Double> entry = sorted.get(i);
            int position = i + 1;
            UUID playerId = entry.getKey();
            double damage = entry.getValue();
            double percent = totalDamage > 0 ? (damage / totalDamage) * 100.0 : 0.0;
            String playerName = session.getPlayerNames().getOrDefault(playerId, "???");

            topEntries.add(new String[]{
                    playerName,
                    MessageUtil.formatDamage(damage),
                    MessageUtil.formatPercent(percent)
            });

            RewardConfig reward = mobConfig.getRewards().get(position);
            if (reward != null) {
                giveReward(playerId, playerName, position, damage, percent, mobType, reward);
            }
        }

        broadcastAnnounce(mobConfig, mobType, topEntries);

        plugin.getCombatManager().removeSession(mobUniqueId);
    }

    private void giveReward(UUID playerId, String playerName, int position, double damage, double percent,
                             String mobType, RewardConfig reward) {

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%player_name%", playerName);
        placeholders.put("%mob_name%", mobType);
        placeholders.put("%position%", String.valueOf(position));
        placeholders.put("%damage%", MessageUtil.formatDamage(damage));
        placeholders.put("%damage_percent%", MessageUtil.formatPercent(percent));

        for (String cmd : reward.getCommands()) {
            String parsed = MessageUtil.replace(cmd, placeholders);
            Bukkit.getScheduler().runTask(plugin, () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed));
        }

        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) return;

        if (reward.getMessage() != null && !reward.getMessage().isEmpty()) {
            player.sendMessage(MessageUtil.color(MessageUtil.replace(reward.getMessage(), placeholders)));
        }

        boolean hasTitle = reward.getTitle() != null && !reward.getTitle().isEmpty();
        boolean hasSubtitle = reward.getSubtitle() != null && !reward.getSubtitle().isEmpty();
        if (hasTitle || hasSubtitle) {
            String title = MessageUtil.color(MessageUtil.replace(reward.getTitle(), placeholders));
            String subtitle = MessageUtil.color(MessageUtil.replace(reward.getSubtitle(), placeholders));
            player.sendTitle(title, subtitle, 10, 60, 20);
        }
    }

    private static final java.util.regex.Pattern TOP_POSITION_PATTERN =
            java.util.regex.Pattern.compile("%top_(\\d+)_");

    private void broadcastAnnounce(MobConfig mobConfig, String mobType, List<String[]> topEntries) {
        List<String> lines = mobConfig.getAnnounceLines();
        if (lines.isEmpty()) return;

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%mob_name%", mobType);

        for (int i = 0; i < topEntries.size(); i++) {
            int pos = i + 1;
            placeholders.put("%top_" + pos + "_name%", topEntries.get(i)[0]);
            placeholders.put("%top_" + pos + "_damage%", topEntries.get(i)[1]);
            placeholders.put("%top_" + pos + "_damage_percent%", topEntries.get(i)[2]);
        }

        int delay = plugin.getConfigManager().getBroadcastLineDelayTicks();
        int tick = 0;
        for (String line : lines) {
            // Si la linea referencia una posicion del top (%top_N_...%) que no
            // participo en el combate, se omite entera en vez de mostrar el
            // placeholder sin reemplazar.
            if (referencesMissingPosition(line, topEntries.size())) continue;

            String parsed = MessageUtil.color(MessageUtil.replace(line, placeholders));
            int scheduledTick = tick;
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.broadcastMessage(parsed), scheduledTick);
            tick += delay;
        }
    }

    private boolean referencesMissingPosition(String line, int availablePositions) {
        java.util.regex.Matcher matcher = TOP_POSITION_PATTERN.matcher(line);
        while (matcher.find()) {
            int position = Integer.parseInt(matcher.group(1));
            if (position > availablePositions) {
                return true;
            }
        }
        return false;
    }
}
