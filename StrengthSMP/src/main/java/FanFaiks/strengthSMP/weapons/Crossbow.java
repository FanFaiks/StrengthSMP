package FanFaiks.strengthSMP.weapons;

import FanFaiks.strengthSMP.StrengthSMP;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Crossbow implements Weapons {

    private final StrengthSMP plugin;

    public static final Map<UUID, Boolean> active        = new HashMap<>();
    public static final Map<UUID, Integer> passiveShots  = new HashMap<>();
    public static final Map<UUID, Integer> ultimateShots = new HashMap<>();
    public static final Map<UUID, Boolean> ultimateReady = new HashMap<>();
    public static final Map<UUID, Boolean> passiveArrows = new HashMap<>();
    public static final Map<UUID, Boolean> abilityArrows = new HashMap<>();

    public Crossbow(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void activate(Player player) {
        UUID uuid = player.getUniqueId();
        if (player.getInventory().getItemInMainHand().getType() != Material.CROSSBOW) return;

        ultimateReady.remove(uuid);
        active.put(uuid, true);

        int durationSec = plugin.getPluginConfig().getCrossbowDuration();
        plugin.getCooldownManager().setDuration(uuid, durationSec);

        new BukkitRunnable() {
            @Override
            public void run() {
                active.remove(uuid);
                plugin.getCooldownManager().setCooldown(uuid,
                        plugin.getPluginConfig().getCrossbowCooldown());
            }
        }.runTaskLater(plugin, durationSec * 20L);
    }

    public void leashEffect(Player attacker, LivingEntity target) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (!attacker.isOnline() || tick >= 60) { cancel(); return; }
                Vector dir = attacker.getLocation()
                        .subtract(target.getLocation()).toVector().normalize().multiply(0.3);
                target.setVelocity(target.getVelocity().add(dir));
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}