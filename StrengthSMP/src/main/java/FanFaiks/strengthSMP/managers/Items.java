package FanFaiks.strengthSMP.managers;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Items {

    public static final int CMD_STRENGTH      = 12345;
    public static final int CMD_REROLL        = 12346;
    public static final int CMD_SHIELD_DISPLAY = 12347;
    public static final int CMD_BOW_BEAM      = 12348;
    public static final int CMD_BOW_SPIRAL    = 12349;

    private final StrengthSMP plugin;

    private ItemStack strengthItem;
    private ItemStack rerollItem;
    private ItemStack swordItem;
    private ItemStack axeItem;
    private ItemStack tridentItem;
    private ItemStack shieldItem;
    private ItemStack bowItem;
    private ItemStack crossbowItem;

    public Items(StrengthSMP plugin) {
        this.plugin = plugin;
        buildItems();
    }

    private void buildItems() {
        strengthItem = build(Material.NAUTILUS_SHELL, "&4&lСила", CMD_STRENGTH,
                List.of(Colors.c("&fПКМ чтобы получить &4+1&f силы")));

        rerollItem = build(Material.NAUTILUS_SHELL, "&6&lСменить оружие", CMD_REROLL,
                List.of(Colors.c("&fПКМ чтобы сменить оружие")));

        int sp = plugin.getPluginConfig().getSwordPassive();
        int su = plugin.getPluginConfig().getSwordUltimate();
        double sm = plugin.getPluginConfig().getSwordUltMultiplier();
        swordItem = build(Material.NETHERITE_SWORD, "&c&l🗡 Меч", -1, Arrays.asList(
                Colors.c("&c&lПассивка:"),
                Colors.c("&c🠺 &fНаберите &c" + sp + "&f-хитовое комбо для"),
                Colors.c("&c🠺 &fавто-крита на следующих атаках"),
                Component.empty(),
                Colors.c("&c&lАльтимейт:"),
                Colors.c("&c🠺 &fНаберите &c" + su + "&f-хитовое комбо для зарядки"),
                Colors.c("&c🠺 &fДвуручный режим — урон x&c" + sm)
        ));

        int ap = plugin.getPluginConfig().getAxePassive();
        int au = plugin.getPluginConfig().getAxeUltimate();
        axeItem = build(Material.NETHERITE_AXE, "&7&l🪓 Топор", -1, Arrays.asList(
                Colors.c("&7&lПассивка:"),
                Colors.c("&7🠺 &f" + ap + " крита подряд — оглушение врага на 1с"),
                Component.empty(),
                Colors.c("&7&lАльтимейт:"),
                Colors.c("&7🠺 &f" + au + " критов для зарядки"),
                Colors.c("&7🠺 &fНакапливает урон и высвобождает его")
        ));

        int tp = plugin.getPluginConfig().getTridentPassive();
        int tu = plugin.getPluginConfig().getTridentUltimate();
        tridentItem = build(Material.TRIDENT, "&b&l🔱 Трезубец", -1, Arrays.asList(
                Colors.c("&b&lПассивка:"),
                Colors.c("&b🠺 &fКаждые &b" + tp + "&f удара — молния в врага"),
                Component.empty(),
                Colors.c("&b&lАльтимейт:"),
                Colors.c("&b🠺 &f" + tu + " ударов для зарядки"),
                Colors.c("&b🠺 &fВолна воды вперёд")
        ));

        shieldItem = build(Material.SHIELD, "&a&l🛡 Щит", -1, Arrays.asList(
                Colors.c("&a&lПассивка:"),
                Colors.c("&a🠺 &fПолучаете на 20% меньше урона"),
                Colors.c("&a🠺 &fкогда щит в откате"),
                Component.empty(),
                Colors.c("&a&lАльтимейт:"),
                Colors.c("&a🠺 &fНеуязвимость на 15 секунд")
        ));

        int bp = plugin.getPluginConfig().getBowPassive();
        int bu = plugin.getPluginConfig().getBowUltimate();
        bowItem = build(Material.BOW, "&6&l🏹 Лук", -1, Arrays.asList(
                Colors.c("&6&lПассивка:"),
                Colors.c("&6🠺 &fКаждый &6" + bp + "&f выстрел наводится на цель"),
                Component.empty(),
                Colors.c("&6&lАльтимейт:"),
                Colors.c("&6🠺 &f" + bu + " попаданий для зарядки"),
                Colors.c("&6🠺 &fВыстрел тремя мощными лучами")
        ));

        int cp = plugin.getPluginConfig().getCrossbowPassive();
        int cu = plugin.getPluginConfig().getCrossbowUltimate();
        crossbowItem = build(Material.CROSSBOW, "&e&l➶ Арбалет", -1, Arrays.asList(
                Colors.c("&e&lПассивка:"),
                Colors.c("&e🠺 &fКаждые &e" + cp + "&f выстрела — двойной урон"),
                Component.empty(),
                Colors.c("&e&lАльтимейт:"),
                Colors.c("&e🠺 &f" + cu + " попаданий для зарядки"),
                Colors.c("&e🠺 &fПривязать врага и притянуть к себе")
        ));
    }

    private ItemStack build(Material mat, String name, int cmd, List<Object> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(Colors.c(name));
        if (cmd > 0) meta.setCustomModelData(cmd);
        meta.lore(lore.stream().map(o -> {
            if (o instanceof Component c) return c;
            return Colors.c(o.toString());
        }).toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getStrengthItem()  { return strengthItem.clone(); }
    public ItemStack getRerollItem()    { return rerollItem.clone(); }
    public ItemStack getSwordItem()     { return swordItem.clone(); }
    public ItemStack getAxeItem()       { return axeItem.clone(); }
    public ItemStack getTridentItem()   { return tridentItem.clone(); }
    public ItemStack getShieldItem()    { return shieldItem.clone(); }
    public ItemStack getBowItem()       { return bowItem.clone(); }
    public ItemStack getCrossbowItem()  { return crossbowItem.clone(); }

    public ItemStack getItemByWeapon(String weapon) {
        return switch (weapon) {
            case "sword"    -> getSwordItem();
            case "axe"      -> getAxeItem();
            case "trident"  -> getTridentItem();
            case "shield"   -> getShieldItem();
            case "bow"      -> getBowItem();
            case "crossbow" -> getCrossbowItem();
            default         -> getSwordItem();
        };
    }
}