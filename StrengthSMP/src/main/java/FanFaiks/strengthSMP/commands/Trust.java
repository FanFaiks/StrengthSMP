package FanFaiks.strengthSMP.commands;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Trust implements CommandExecutor, TabCompleter {

    private final StrengthSMP plugin;

    public Trust(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        if (args.length == 0) { sendUsage(player); return true; }

        UUID uuid = player.getUniqueId();

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 2) { sendUsage(player); return true; }
                String name = args[1];
                if (name.equalsIgnoreCase(player.getName())) {
                    player.sendMessage(Colors.c("&cВы и так доверяете себе..."));
                    return true;
                }
                plugin.getPlayerData().addTrusted(uuid, name);
                player.sendMessage(Colors.c("&aВы теперь доверяете &e" + name + "&a!"));
            }
            case "remove" -> {
                if (args.length < 2) { sendUsage(player); return true; }
                String name = args[1];
                plugin.getPlayerData().removeTrusted(uuid, name);
                player.sendMessage(Colors.c("&cВы больше не доверяете &e" + name + "&c!"));
            }
            case "list" -> {
                Set<String> trusted = plugin.getPlayerData().getTrusted(uuid);
                if (trusted.isEmpty()) {
                    player.sendMessage(Colors.c("&cВы никому не доверяете!"));
                } else {
                    player.sendMessage(Colors.c("&aДоверенные игроки:"));
                    trusted.forEach(name ->
                            player.sendMessage(Colors.c("&a- &e" + name)));
                }
            }
            default -> sendUsage(player);
        }
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(Colors.c("&cИспользование:"));
        player.sendMessage(Colors.c("&c/trust add <игрок>"));
        player.sendMessage(Colors.c("&c/trust remove <игрок>"));
        player.sendMessage(Colors.c("&c/trust list"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd,
                                      String alias, String[] args) {
        if (!(sender instanceof Player player)) return List.of();
        if (args.length == 1) return Arrays.asList("add", "remove", "list");
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
            if (args[0].equalsIgnoreCase("remove")) {
                return List.copyOf(plugin.getPlayerData().getTrusted(player.getUniqueId()));
            }
        }
        return List.of();
    }
}