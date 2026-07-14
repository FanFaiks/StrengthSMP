package FanFaiks.strengthSMP.managers;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CD {

    private final Map<UUID, Integer> cooldowns = new HashMap<>();
    private final Map<UUID, Integer> durations = new HashMap<>();
    private final StrengthSMP plugin;

    public CD(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isReady(UUID uuid)           { return cooldowns.getOrDefault(uuid, -1) <= 0; }
    public int getCooldown(UUID uuid)           { return cooldowns.getOrDefault(uuid, 0); }
    public void setCooldown(UUID uuid, int sec) { cooldowns.put(uuid, sec); }
    public void resetCooldown(UUID uuid)        { cooldowns.put(uuid, -1); }

    public boolean hasActiveDuration(UUID uuid) { return durations.getOrDefault(uuid, 0) > 0; }
    public int getDuration(UUID uuid)           { return durations.getOrDefault(uuid, 0); }
    public void setDuration(UUID uuid, int sec) { durations.put(uuid, sec); }
    public void clearDuration(UUID uuid)        { durations.remove(uuid); }

    public void tick() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            tickCooldown(player, uuid);
            tickDuration(player, uuid);
        }
    }

    private void tickCooldown(Player player, UUID uuid) {
        int cd = cooldowns.getOrDefault(uuid, -1);
        if (cd <= 0) return;
        cd--;
        cooldowns.put(uuid, cd);
        if (cd <= 0) {
            cooldowns.put(uuid, -1);
            String weapon = plugin.getPlayerData().getWeapon(uuid);
            player.sendMessage(Colors.c(
                    plugin.getWeaponManager().getCooldownMessage(weapon)));
            player.playSound(player.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        }
    }

    private void tickDuration(Player player, UUID uuid) {
        int dur = durations.getOrDefault(uuid, 0);
        if (dur <= 0) return;
        dur--;
        if (dur <= 0) {
            durations.remove(uuid);
        } else {
            durations.put(uuid, dur);
        }
        if (plugin.getPluginConfig().isShowDurationInActionbar() && dur > 0) {
            String weapon = plugin.getPlayerData().getWeapon(uuid);
            player.sendActionBar(Colors.c(
                    plugin.getWeaponManager().getActionBarMessage(weapon, dur)));
        }
    }
}