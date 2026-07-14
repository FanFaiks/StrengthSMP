package FanFaiks.strengthSMP.commands;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Strength implements CommandExecutor {

    private final StrengthSMP plugin;

    public Strength(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        UUID uuid   = player.getUniqueId();
        int str     = plugin.getPlayerData().getStrength(uuid);
        String sign = str > 0 ? "+" : "";
        String weapon = plugin.getPlayerData().getWeapon(uuid);

        player.sendMessage(Colors.c("&6&lСила&8&l: &c&l" + sign + str));
        player.sendMessage(Colors.c("&6&lОружие&8&l: " +
                plugin.getWeaponManager().getWeaponDisplay(weapon)));

        return true;
    }
}