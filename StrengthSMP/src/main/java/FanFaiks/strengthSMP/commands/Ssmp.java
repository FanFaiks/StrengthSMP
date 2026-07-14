package FanFaiks.strengthSMP.commands;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.managers.Weapons;
import FanFaiks.strengthSMP.utils.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class Ssmp implements CommandExecutor, TabCompleter {

    private final StrengthSMP plugin;

    public Ssmp(StrengthSMP plugin) {
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

        switch (args[0].toLowerCase()) {

            case "info" -> openInfoGUI(player);

            case "reload" -> {
                plugin.getPluginConfig().reload();
                player.sendMessage(Colors.c("&aКонфиг перезагружен!"));
            }

            case "reroll" -> {
                if (args.length < 2) { sendUsage(player); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(Colors.c("&cИгрок не найден!"));
                    return true;
                }
                plugin.getWeaponManager().randomWeapon(target);
                player.sendMessage(Colors.c(
                        "&aПереброшено оружие для &e" + target.getName()));
            }

            case "setstrength" -> {
                if (args.length < 3) { sendUsage(player); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(Colors.c("&cИгрок не найден!"));
                    return true;
                }
                int amount;
                try { amount = Integer.parseInt(args[2]); }
                catch (NumberFormatException e) {
                    player.sendMessage(Colors.c("&cНекорректное значение!"));
                    return true;
                }
                int min = plugin.getPluginConfig().getMinStrength();
                int max = plugin.getPluginConfig().getMaxStrength();
                if (amount < min || amount > max) {
                    player.sendMessage(Colors.c(
                            "&cЗначение должно быть от &e" + min + "&c до &e" + max + "&c!"));
                    return true;
                }
                plugin.getPlayerData().setStrength(target.getUniqueId(), amount);
                target.playSound(target.getLocation(),
                        Sound.BLOCK_END_PORTAL_FRAME_FILL, 1f, 1f);
                String sign = amount >= 0 ? "+" : "";
                target.sendMessage(Colors.c(
                        "&6&lВаша новая сила: &c&l" + sign + amount));
                player.sendMessage(Colors.c(
                        "&aСила игрока &e" + target.getName() +
                                "&a установлена на &e" + sign + amount));
            }

            case "setweapon" -> {
                if (args.length < 3) { sendUsage(player); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(Colors.c("&cИгрок не найден!"));
                    return true;
                }
                if (!Weapons.WEAPONS.contains(args[2].toLowerCase())) {
                    player.sendMessage(Colors.c("&cНеверное оружие! Доступно: " +
                            String.join(", ", Weapons.WEAPONS)));
                    return true;
                }
                String newWeapon = args[2].toLowerCase();
                plugin.getPlayerData().setWeapon(target.getUniqueId(), newWeapon);
                target.sendMessage(Colors.c("&6&lВаше новое оружие: " +
                        plugin.getWeaponManager().getWeaponDisplay(newWeapon)));
                target.playSound(target.getLocation(),
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                player.sendMessage(Colors.c(
                        "&aОружие игрока &e" + target.getName() +
                                "&a изменено на &e" + newWeapon));
            }

            default -> sendUsage(player);
        }
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(Colors.c("&cИспользование:"));
        player.sendMessage(Colors.c("&c/strengthsmp info"));
        player.sendMessage(Colors.c("&c/strengthsmp reload"));
        player.sendMessage(Colors.c("&c/strengthsmp reroll <игрок>"));
        player.sendMessage(Colors.c("&c/strengthsmp setstrength <игрок> <значение>"));
        player.sendMessage(Colors.c("&c/strengthsmp setweapon <игрок> <оружие>"));
    }

    private void openInfoGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("ᴡᴇᴀᴘᴏɴs"));
        gui.setItem(0,  plugin.getItemManager().getTridentItem());
        gui.setItem(4,  plugin.getItemManager().getRerollItem());
        gui.setItem(11, plugin.getItemManager().getSwordItem());
        gui.setItem(12, plugin.getItemManager().getAxeItem());
        gui.setItem(13, plugin.getItemManager().getShieldItem());
        gui.setItem(14, plugin.getItemManager().getBowItem());
        gui.setItem(15, plugin.getItemManager().getCrossbowItem());
        gui.setItem(22, plugin.getItemManager().getStrengthItem());
        player.openInventory(gui);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd,
                                      String alias, String[] args) {
        if (!sender.hasPermission("strengthsmp.admin")) return List.of();
        if (args.length == 1) {
            return Arrays.asList("info", "reload", "reroll", "setstrength", "setweapon");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("reroll") ||
                args[0].equalsIgnoreCase("setstrength") ||
                args[0].equalsIgnoreCase("setweapon"))) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setstrength")) {
                return Arrays.asList(
                        String.valueOf(plugin.getPluginConfig().getMinStrength()),
                        "0",
                        String.valueOf(plugin.getPluginConfig().getMaxStrength()));
            }
            if (args[0].equalsIgnoreCase("setweapon")) {
                return Weapons.WEAPONS;
            }
        }
        return List.of();
    }
}