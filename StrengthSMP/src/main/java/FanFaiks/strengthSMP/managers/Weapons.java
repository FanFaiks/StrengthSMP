package FanFaiks.strengthSMP.managers;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Weapons {

    public static final List<String> WEAPONS =
            List.of("trident", "sword", "axe", "shield", "bow", "crossbow");

    private final StrengthSMP plugin;
    private final Random random = new Random();

    public Weapons(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    public void randomWeapon(Player player) {
        UUID uuid    = player.getUniqueId();
        String current = plugin.getPlayerData().getWeapon(uuid);
        String chosen;
        do {
            chosen = WEAPONS.get(random.nextInt(WEAPONS.size()));
        } while (chosen.equals(current));

        plugin.getPlayerData().setWeapon(uuid, chosen);
        player.sendMessage(Colors.c("&6&lВаше новое оружие: " + getWeaponDisplay(chosen)));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }

    public String getWeaponDisplay(String weapon) {
        return switch (weapon) {
            case "trident"  -> "&b&l🔱 Трезубец";
            case "sword"    -> "&c&l🗡 Меч";
            case "axe"      -> "&7&l🪓 Топор";
            case "shield"   -> "&a&l🛡 Щит";
            case "bow"      -> "&6&l🏹 Лук";
            case "crossbow" -> "&e&l➶ Арбалет";
            default         -> "&f" + weapon;
        };
    }

    public String getCooldownMessage(String weapon) {
        return switch (weapon) {
            case "sword"    -> "&c&l🗡 &4&lКулдаун снят!";
            case "trident"  -> "&b&l🔱 &3&lКулдаун снят!";
            case "axe"      -> "&7&l🪓 &8&lКулдаун снят!";
            case "shield"   -> "&a&l🛡 &2&lКулдаун снят!";
            case "bow"      -> "&6&l🏹 &8&lКулдаун снят!";
            case "crossbow" -> "&e&l➶ &e&lКулдаун снят!";
            default         -> "&aКулдаун снят!";
        };
    }

    public String getActionBarMessage(String weapon, int duration) {
        return switch (weapon) {
            case "sword"    -> "&c🗡 " + duration + "с";
            case "trident"  -> "&9🔱 " + duration + "с";
            case "axe"      -> "&8🪓 " + duration + "с";
            case "shield"   -> "&2🛡 " + duration + "с";
            case "bow"      -> "&6🏹 " + duration + "с";
            case "crossbow" -> "&e➶ " + duration + "с";
            default         -> duration + "с";
        };
    }

    public String getNotReadyMessage(String weapon) {
        return switch (weapon) {
            case "sword"    -> "&c&l🗡 &4&lНе готово!";
            case "trident"  -> "&b&l🔱 &3&lНе готово!";
            case "axe"      -> "&7&l🪓 &8&lНе готово!";
            case "shield"   -> "&a&l🛡 &2&lНе готово!";
            case "bow"      -> "&6&l🏹 &8&lНе готово!";
            case "crossbow" -> "&e&l➶ &e&lНе готово!";
            default         -> "&cНе готово!";
        };
    }

    public int getCooldownSeconds(String weapon) {
        var cfg = plugin.getPluginConfig();
        return switch (weapon) {
            case "sword"    -> cfg.getSwordCooldown();
            case "axe"      -> cfg.getAxeCooldown();
            case "trident"  -> cfg.getTridentCooldown();
            case "shield"   -> cfg.getShieldCooldown();
            case "bow"      -> cfg.getBowCooldown();
            case "crossbow" -> cfg.getCrossbowCooldown();
            default         -> 60;
        };
    }
}