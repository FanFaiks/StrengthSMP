package FanFaiks.strengthSMP.weapons;

import FanFaiks.strengthSMP.StrengthSMP;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Trident implements Weapons {

    private final StrengthSMP plugin;

    public static final Map<UUID, Boolean> active        = new HashMap<>();
    public static final Map<UUID, Integer> hitCount      = new HashMap<>();
    public static final Map<UUID, Integer> ultimateCombo = new HashMap<>();
    public static final Map<UUID, Boolean> ultimateReady = new HashMap<>();

    private static boolean blockFlowGlobal = false;

    public Trident(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void activate(Player player) {
        UUID uuid = player.getUniqueId();
        if (player.getInventory().getItemInMainHand().getType() != Material.TRIDENT) return;

        ultimateReady.remove(uuid);
        active.put(uuid, true);
        blockFlowGlobal = true;

        player.getWorld().playSound(player.getLocation(),
                Sound.ITEM_TRIDENT_RIPTIDE_3, 1f, 1f);

        int durationTicks = plugin.getPluginConfig().getTridentDuration() * 20;
        plugin.getCooldownManager().setDuration(uuid, plugin.getPluginConfig().getTridentDuration());

        List<Location> waterBlocks = new ArrayList<>();

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || tick >= durationTicks) {
                    for (Location wl : waterBlocks) {
                        if (wl.getBlock().getType() == Material.WATER) {
                            wl.getBlock().setType(Material.AIR);
                        }
                    }
                    waterBlocks.clear();
                    blockFlowGlobal = false;
                    active.remove(uuid);
                    plugin.getCooldownManager().setCooldown(uuid,
                            plugin.getPluginConfig().getTridentCooldown());
                    cancel();
                    return;
                }

                Vector forward = player.getLocation().getDirection().multiply(0.1);
                player.setVelocity(player.getVelocity().add(forward));

                Location center = player.getLocation().add(0, 1, 0);
                double radius   = 1.8;

                waterBlocks.removeIf(wl -> {
                    if (wl.distance(center) > radius) {
                        if (wl.getBlock().getType() == Material.WATER) {
                            wl.getBlock().setType(Material.AIR);
                        }
                        return true;
                    }
                    return false;
                });

                int ri = (int) Math.ceil(radius);
                for (int x = -ri; x <= ri; x++) {
                    for (int y = -ri; y <= ri; y++) {
                        for (int z = -ri; z <= ri; z++) {
                            Location bl = center.clone().add(x, y, z);
                            if (bl.distance(center) <= radius) {
                                Block block = bl.getBlock();
                                if (block.getType() == Material.AIR) {
                                    block.setType(Material.WATER);
                                    waterBlocks.add(bl);
                                }
                            }
                        }
                    }
                }

                for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && !entity.equals(player)) {
                        Vector dir = entity.getLocation()
                                .subtract(player.getLocation())
                                .toVector().normalize().multiply(2);
                        entity.setVelocity(entity.getVelocity().add(dir));
                    }
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static boolean isBlockFlowActive() {
        return blockFlowGlobal;
    }
}