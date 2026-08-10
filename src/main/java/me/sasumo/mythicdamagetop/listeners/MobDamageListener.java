package me.sasumo.mythicdamagetop.listeners;

import me.sasumo.mythicdamagetop.MythicDamageTopPlugin;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Optional;

/**
 * Escucha el daño directo (Bukkit) que reciben los mobs de MythicMobs.
 * Solo cuenta daño al mob principal (no a summons/adds), tal como fue pedido.
 */
public class MobDamageListener implements Listener {

    private final MythicDamageTopPlugin plugin;

    public MobDamageListener(MythicDamageTopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();

        if (!MythicBukkit.inst().getMobManager().isActiveMob(victim.getUniqueId())) {
            return;
        }

        Optional<ActiveMob> activeMobOpt = MythicBukkit.inst().getMobManager().getActiveMob(victim.getUniqueId());
        if (activeMobOpt.isEmpty()) return;

        String mobType = activeMobOpt.get().getType().getInternalName();
        if (plugin.getConfigManager().getMobConfig(mobType) == null) return;

        Player damager = resolvePlayer(event.getDamager());
        if (damager == null) return;

        double damage = event.getFinalDamage();
        if (damage <= 0) return;

        plugin.getCombatManager().addDamage(victim.getUniqueId(), mobType, damager, damage);
    }

    private Player resolvePlayer(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        }
        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }
}
