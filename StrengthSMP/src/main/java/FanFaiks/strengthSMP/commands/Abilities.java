package FanFaiks.strengthSMP.commands;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import FanFaiks.strengthSMP.weapons.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Abilities implements CommandExecutor {

    private final StrengthSMP plugin;
    private final Sword swordAbility;
    private final Axe axeAbility;
    private final Trident tridentAbility;
    private final Shield shieldAbility;
    private final Bow bowAbility;
    private final Crossbow crossbowAbility;

    public Abilities(StrengthSMP plugin) {
        this.plugin          = plugin;
        this.swordAbility    = new Sword(plugin);
        this.axeAbility      = new Axe(plugin);
        this.tridentAbility  = new Trident(plugin);
        this.shieldAbility   = new Shield(plugin);
        this.bowAbility      = new Bow(plugin);
        this.crossbowAbility = new Crossbow(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        UUID uuid    = player.getUniqueId();
        String weapon  = plugin.getPlayerData().getWeapon(uuid);
        int strength   = plugin.getPlayerData().getStrength(uuid);
        int minStr     = plugin.getPluginConfig().getMinStrengthToActivateAbility();

        if (!plugin.getCooldownManager().isReady(uuid)) {
            int cd = plugin.getCooldownManager().getCooldown(uuid);
            player.sendMessage(Colors.c("&cВы ещё на кулдауне: &e" + cd + "с&c!"));
            return true;
        }

        if (plugin.getCooldownManager().hasActiveDuration(uuid)) {
            player.sendMessage(Colors.c("&cВаша способность уже активна!"));
            return true;
        }

        if (strength < minStr) {
            player.sendMessage(Colors.c(
                    "&cУ вас недостаточно силы для использования способности!"));
            return true;
        }

        if (weapon.equals("shield")) {
            shieldAbility.activate(player);
            return true;
        }

        boolean ready = switch (weapon) {
            case "sword"    -> Sword.ultimateReady.containsKey(uuid);
            case "axe"      -> Axe.ultimateReady.containsKey(uuid);
            case "trident"  -> Trident.ultimateReady.containsKey(uuid);
            case "bow"      -> Bow.ultimateReady.containsKey(uuid);
            case "crossbow" -> Crossbow.ultimateReady.containsKey(uuid);
            default -> false;
        };

        if (!ready) {
            player.sendMessage(Colors.c(
                    plugin.getWeaponManager().getNotReadyMessage(weapon)));
            return true;
        }

        switch (weapon) {
            case "sword"    -> swordAbility.activate(player);
            case "axe"      -> axeAbility.activate(player);
            case "trident"  -> tridentAbility.activate(player);
            case "bow"      -> bowAbility.activate(player);
            case "crossbow" -> crossbowAbility.activate(player);
        }

        return true;
    }
}