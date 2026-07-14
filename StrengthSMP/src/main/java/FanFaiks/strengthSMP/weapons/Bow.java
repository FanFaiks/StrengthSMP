package FanFaiks.strengthSMP.weapons;

import FanFaiks.strengthSMP.StrengthSMP;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Bow implements Weapons {

    private final StrengthSMP plugin;

    public static final Map<UUID, Boolean> active        = new HashMap<>();
    public static final Map<UUID, Integer> passiveShots  = new HashMap<>();
    public static final Map<UUID, Integer> ultimateShots = new HashMap<>();
    public static final Map<UUID, Boolean> ultimateReady = new HashMap<>();

    public Bow(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void activate(Player player) {
        UUID uuid = player.getUniqueId();
        if (player.getInventory().getItemInMainHand().getType() != Material.BOW) return;

        ultimateReady.remove(uuid);
        active.put(uuid, true);

        int beams    = plugin.getPluginConfig().getBowBeams();
        int range    = plugin.getPluginConfig().getBowBeamRange();
        double width = plugin.getPluginConfig().getBowBeamWidth();
        double dmg   = plugin.getPluginConfig().getBowBeamDamage();

        plugin.getCooldownManager().setDuration(uuid, beams * 2);

        new BukkitRunnable() {
            int beamIndex = 0;

            @Override
            public void run() {
                if (!player.isOnline() || beamIndex >= beams) {
                    active.remove(uuid);
                    plugin.getCooldownManager().setCooldown(uuid,
                            plugin.getPluginConfig().getBowCooldown());
                    cancel();
                    return;
                }
                fireBeam(player, range, width, dmg);
                beamIndex++;
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }

    private void fireBeam(Player player, int range, double width, double beamDmg) {
        for (Player p : player.getWorld().getPlayers()) {
            if (p.getLocation().distance(player.getLocation()) <= range) {
                p.playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, 1f, 1f);
            }
        }

        Location eyeLoc = player.getEyeLocation();
        Location start  = eyeLoc.clone().add(eyeLoc.getDirection());
        Vector dir      = eyeLoc.getDirection().normalize().multiply(0.5);

        new BukkitRunnable() {
            int t = 0;
            @Override
            public void run() {
                if (t >= 20) { cancel(); return; }
                drawBeamLine(start.clone(), dir, range, player.getWorld());
                t++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        Location linePoint = start.clone();
        int steps = (int) ((range - 1) / 0.5);
        for (int i = 0; i < steps; i++) {
            for (Entity entity : linePoint.getWorld().getNearbyEntities(
                    linePoint, width + 1, width + 1, width + 1)) {
                if (entity.equals(player)) continue;
                if (!(entity instanceof LivingEntity le)) continue;
                if (entity instanceof Player tp &&
                        plugin.getPlayerData().isTrusted(player.getUniqueId(), tp)) continue;

                if (le.getHealth() <= beamDmg) {
                    le.damage(70);
                } else {
                    le.damage(beamDmg, player);
                }
            }
            linePoint.add(dir);
        }

        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1f);

        Location redPoint = start.clone();
        for (int i = 0; i < steps; i++) {
            redPoint.getWorld().spawnParticle(Particle.DUST, redPoint, 1, 0, 0, 0,
                    new Particle.DustOptions(Color.RED, 3f));
            redPoint.add(dir);
        }
    }

    private void drawBeamLine(Location start, Vector dir, int range, World world) {
        Location p = start.clone();
        int steps = (int) ((range - 1) / 0.5);
        for (int i = 0; i < steps; i++) {
            world.spawnParticle(Particle.ELECTRIC_SPARK, p, 1, 0, 0, 0, 0);
            p.add(dir);
        }
    }
}