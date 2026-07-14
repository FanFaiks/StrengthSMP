package FanFaiks.strengthSMP.listeners;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import FanFaiks.strengthSMP.weapons.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Projectiles implements Listener {

    private final StrengthSMP plugin;

    public Projectiles(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player shooter)) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;

        UUID uuid   = shooter.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);

        if (weapon.equals("bow") && event.getBow() != null &&
                event.getBow().getType() == Material.BOW &&
                plugin.getPlayerData().getStrength(uuid) >= plugin.getPluginConfig().getMinStrengthToHavePassive()) {

            int shots = Bow.passiveShots.getOrDefault(uuid, 0);
            if (shots >= plugin.getPluginConfig().getBowPassive()) {
                Bow.passiveShots.put(uuid, 0);
                homingArrow(arrow, shooter);
            }
        }

        if (weapon.equals("crossbow") && event.getBow() != null &&
                event.getBow().getType() == Material.CROSSBOW &&
                plugin.getPlayerData().getStrength(uuid) >= plugin.getPluginConfig().getMinStrengthToHavePassive()) {

            int shots = Crossbow.passiveShots.getOrDefault(uuid, 0);
            if (shots >= plugin.getPluginConfig().getCrossbowPassive()) {
                Crossbow.passiveShots.put(uuid, 0);
                Crossbow.passiveArrows.put(arrow.getUniqueId(), true);
                arrow.setGlowing(true);
            }
        }

        if (weapon.equals("crossbow") && event.getBow() != null &&
                event.getBow().getType() == Material.CROSSBOW &&
                Crossbow.active.containsKey(uuid)) {

            Crossbow.abilityArrows.put(arrow.getUniqueId(), true);
            arrow.setGlowing(true);
        }
    }

    private void homingArrow(Arrow arrow, Player shooter) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (!arrow.isValid() || tick >= 50) { cancel(); return; }

                LivingEntity nearest = null;
                double nearestDist   = Double.MAX_VALUE;

                for (Entity entity : arrow.getNearbyEntities(12, 12, 12)) {
                    if (!(entity instanceof LivingEntity le)) continue;
                    if (entity.equals(shooter)) continue;
                    if (entity instanceof Player tp &&
                            plugin.getPlayerData().isTrusted(shooter.getUniqueId(), tp)) continue;
                    double dist = entity.getLocation().distance(arrow.getLocation());
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearest     = le;
                    }
                }

                if (nearest != null) {
                    Vector dir = nearest.getLocation().add(0, 1, 0)
                            .subtract(arrow.getLocation()).toVector().normalize().multiply(2);
                    arrow.setVelocity(dir);
                    cancel();
                }
                tick += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player shooter)) return;
        UUID uuid   = shooter.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);

        if (weapon.equals("bow") &&
                plugin.getPlayerData().getStrength(uuid) >= plugin.getPluginConfig().getMinStrengthToHavePassive() &&
                event.getHitEntity() != null) {
            Bow.passiveShots.merge(uuid, 1, Integer::sum);
        }

        if (weapon.equals("bow") &&
                plugin.getPlayerData().getStrength(uuid) >= plugin.getPluginConfig().getMinStrengthToActivateAbility() &&
                plugin.getCooldownManager().isReady(uuid) &&
                !Bow.ultimateReady.containsKey(uuid) &&
                !Bow.active.containsKey(uuid) &&
                event.getHitEntity() != null) {

            Bow.ultimateShots.merge(uuid, 1, Integer::sum);
            if (Bow.ultimateShots.getOrDefault(uuid, 0) >= plugin.getPluginConfig().getBowUltimate()) {
                Bow.ultimateReady.put(uuid, true);
                Bow.ultimateShots.remove(uuid);
                shooter.sendMessage(Colors.c("&6&l🏹 &8&lГОТОВО!"));
                shooter.playSound(shooter.getLocation(),
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
            }
        }

        if (weapon.equals("crossbow") &&
                plugin.getPlayerData().getStrength(uuid) >= plugin.getPluginConfig().getMinStrengthToHavePassive() &&
                event.getHitEntity() != null) {
            Crossbow.passiveShots.merge(uuid, 1, Integer::sum);
        }

        if (weapon.equals("crossbow") &&
                plugin.getPlayerData().getStrength(uuid) >= plugin.getPluginConfig().getMinStrengthToActivateAbility() &&
                plugin.getCooldownManager().isReady(uuid) &&
                !Crossbow.ultimateReady.containsKey(uuid) &&
                !Crossbow.active.containsKey(uuid) &&
                event.getHitEntity() != null) {

            Crossbow.ultimateShots.merge(uuid, 1, Integer::sum);
            if (Crossbow.ultimateShots.getOrDefault(uuid, 0) >= plugin.getPluginConfig().getCrossbowUltimate()) {
                Crossbow.ultimateReady.put(uuid, true);
                Crossbow.ultimateShots.remove(uuid);
                shooter.sendMessage(Colors.c("&e&l➶ &e&lГОТОВО!"));
                shooter.playSound(shooter.getLocation(),
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
            }
        }
    }
}