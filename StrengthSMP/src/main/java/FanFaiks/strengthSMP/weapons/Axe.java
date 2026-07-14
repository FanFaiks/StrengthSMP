package FanFaiks.strengthSMP.weapons;

import FanFaiks.strengthSMP.StrengthSMP;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Axe implements Weapons {

    private final StrengthSMP plugin;
    private final Random random = new Random();

    public static final Map<UUID, Boolean>       active        = new HashMap<>();
    public static final Map<UUID, Double>        storedDamage  = new HashMap<>();
    public static final Map<UUID, LivingEntity>  victim        = new HashMap<>();
    public static final Map<UUID, Integer>       passiveCombo  = new HashMap<>();
    public static final Map<UUID, Integer>       ultimateCombo = new HashMap<>();
    public static final Map<UUID, Boolean>       ultimateReady = new HashMap<>();
    public static final Map<UUID, Boolean>       stunned       = new HashMap<>();

    public Axe(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void activate(Player player) {
        UUID uuid = player.getUniqueId();
        if (!player.getInventory().getItemInMainHand().getType().name().contains("AXE")) return;

        ultimateReady.remove(uuid);
        active.put(uuid, true);
        storedDamage.put(uuid, 0.0);
        victim.remove(uuid);

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 0.7f);

        int durationTicks = plugin.getPluginConfig().getAxeDuration() * 20;
        plugin.getCooldownManager().setDuration(uuid, plugin.getPluginConfig().getAxeDuration());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }

                LivingEntity target = victim.get(uuid);
                double damage = storedDamage.getOrDefault(uuid, 0.0);

                if (target != null && damage > 0) {
                    target.damage(damage, player);
                    spawnReleaseParticles(target.getLocation());
                    target.getWorld().playSound(target.getLocation(),
                            Sound.ENTITY_BREEZE_DEATH, 1f, 0.6f);
                }

                active.remove(uuid);
                storedDamage.remove(uuid);
                victim.remove(uuid);
                plugin.getCooldownManager().setCooldown(uuid,
                        plugin.getPluginConfig().getAxeCooldown());
                cancel();
            }
        }.runTaskLater(plugin, durationTicks);
    }

    private void spawnReleaseParticles(Location loc) {
        for (int i = 0; i < 30; i++) {
            double dx = (random.nextDouble() - 0.5) * 1.5;
            double dy = random.nextDouble() * 2;
            double dz = (random.nextDouble() - 0.5) * 1.5;
            loc.getWorld().spawnParticle(Particle.DUST,
                    loc.clone().add(dx, dy, dz), 1, 0, 0, 0,
                    new Particle.DustOptions(Color.fromRGB(85, 85, 85), 1f));
        }
    }

    public void accumulateDamage(UUID uuid, double damage, LivingEntity target) {
        storedDamage.merge(uuid, damage, Double::sum);
        victim.put(uuid, target);
    }

    @Override
    public void cancel(Player player) {
        UUID uuid = player.getUniqueId();
        active.remove(uuid);
        storedDamage.remove(uuid);
        victim.remove(uuid);
    }
}