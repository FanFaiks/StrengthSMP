package FanFaiks.strengthSMP.commands;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CD implements CommandExecutor {

    private final StrengthSMP plugin;

    public CD(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String label, String[] args) {
        if (!(sender instanceof Player admin)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : admin;

        if (target == null) {
            admin.sendMessage(Colors.c("&cИгрок не найден!"));
            return true;
        }

        plugin.getCooldownManager().resetCooldown(target.getUniqueId());
        admin.sendMessage(Colors.c(
                "&a&lКулдаун сброшен для &e" + target.getName() + "&a."));
        admin.playSound(admin.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

        return true;
    }
}