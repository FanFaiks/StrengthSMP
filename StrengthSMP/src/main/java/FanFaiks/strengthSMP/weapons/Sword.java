package FanFaiks.strengthSMP.weapons;

import FanFaiks.strengthSMP.StrengthSMP;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Sword implements Weapons {

    private final StrengthSMP plugin;

    public static final Map<UUID, Boolean>    active        = new HashMap<>();
    public static final Map<UUID, ItemStack>  offhand       = new HashMap<>();
    public static final Map<UUID, Integer>    passiveCombo  = new HashMap<>();
    public static final Map<UUID, Integer>    ultimateCombo = new HashMap<>();
    public static final Map<UUID, Boolean>    ultimateReady = new HashMap<>();

    public Sword(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void activate(Player player) {
        UUID uuid = player.getUniqueId();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!tool.getType().name().contains("SWORD")) return;

        ultimateReady.remove(uuid);
        active.put(uuid, true);

        player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1f, 1f);

        offhand.put(uuid, player.getInventory().getItemInOffHand());
        player.getInventory().setItemInOffHand(tool.clone());

        int durationTicks = plugin.getPluginConfig().getSwordDuration() * 20;
        plugin.getCooldownManager().setDuration(uuid, plugin.getPluginConfig().getSwordDuration());

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || tick >= durationTicks) {
                    player.getInventory().setItemInOffHand(
                            offhand.getOrDefault(uuid, new ItemStack(Material.AIR)));
                    offhand.remove(uuid);
                    active.remove(uuid);
                    plugin.getCooldownManager().setCooldown(uuid,
                            plugin.getPluginConfig().getSwordCooldown());
                    cancel();
                    return;
                }

                double yaw   = Math.toRadians(player.getLocation().getYaw());
                double s1x   = Math.cos(yaw) * 0.35;
                double s1z   = Math.sin(yaw) * 0.35;
                double s2x   = Math.cos(yaw + Math.PI) * 0.35;
                double s2z   = Math.sin(yaw + Math.PI) * 0.35;
                Location base = player.getLocation().add(0, 0.7, 0);

                base.getWorld().spawnParticle(Particle.DUST,
                        base.clone().add(s1x, 0, s1z), 1, 0, 0, 0,
                        new Particle.DustOptions(Color.RED, 0.7f));
                base.getWorld().spawnParticle(Particle.DUST,
                        base.clone().add(s2x, 0, s2z), 1, 0, 0, 0,
                        new Particle.DustOptions(Color.RED, 0.7f));
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void cancel(Player player) {
        UUID uuid = player.getUniqueId();
        if (offhand.containsKey(uuid)) {
            player.getInventory().setItemInOffHand(offhand.remove(uuid));
        }
        active.remove(uuid);
    }
}