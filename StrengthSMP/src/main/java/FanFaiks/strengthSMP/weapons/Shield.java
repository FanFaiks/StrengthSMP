package FanFaiks.strengthSMP.weapons;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.managers.Items;
import FanFaiks.strengthSMP.utils.Display;
import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Shield implements Weapons {

    private final StrengthSMP plugin;

    public static final Map<UUID, Boolean> active = new HashMap<>();

    public Shield(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void activate(Player player) {
        UUID uuid = player.getUniqueId();
        active.put(uuid, true);

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1f);

        ItemDisplay display = Display.spawnItemDisplay(
                player.getLocation(),
                Material.NAUTILUS_SHELL,
                Items.CMD_SHIELD_DISPLAY,
                new Vector3f(0, -0.5f, 0),
                new Vector3f(1, 1, 1)
        );

        player.addPassenger(display);

        int durationTicks = plugin.getPluginConfig().getShieldDuration() * 20;
        plugin.getCooldownManager().setDuration(uuid, plugin.getPluginConfig().getShieldDuration());

        new BukkitRunnable() {
            @Override
            public void run() {
                display.remove();
                active.remove(uuid);
                plugin.getCooldownManager().setCooldown(uuid,
                        plugin.getPluginConfig().getShieldCooldown());
            }
        }.runTaskLater(plugin, durationTicks);
    }
}