package FanFaiks.strengthSMP.commands;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Withdraw implements CommandExecutor, TabCompleter {

    private final StrengthSMP plugin;

    public Withdraw(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Colors.c("&cИспользование: /withdraw <количество>"));
            return true;
        }

        UUID uuid = player.getUniqueId();

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Colors.c("&cВам нужен хотя бы 1 свободный слот!"));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(Colors.c("&cВведите корректное число!"));
            return true;
        }

        if (amount < 1) {
            player.sendMessage(Colors.c("&cНужно выводить минимум 1 силу!"));
            return true;
        }

        int currentStr = plugin.getPlayerData().getStrength(uuid);
        if (amount > currentStr) {
            player.sendMessage(Colors.c("&cУ вас недостаточно силы!"));
            return true;
        }

        plugin.getPlayerData().removeStrength(uuid, amount);
        var item = plugin.getItemManager().getStrengthItem();
        item.setAmount(amount);
        player.getInventory().addItem(item);

        int newStr  = plugin.getPlayerData().getStrength(uuid);
        String sign = newStr >= 0 ? "+" : "";
        player.sendMessage(Colors.c("&6&lВаша новая сила: &c&l" + sign + newStr));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd,
                                      String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("1", "2", "3",
                    String.valueOf(plugin.getPluginConfig().getMaxStrength()));
        }
        return List.of();
    }
}