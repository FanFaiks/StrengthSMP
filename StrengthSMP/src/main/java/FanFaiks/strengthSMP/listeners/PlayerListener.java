package FanFaiks.strengthSMP.listeners;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.managers.Items;
import FanFaiks.strengthSMP.utils.Colors;
import FanFaiks.strengthSMP.weapons.Sword;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final StrengthSMP plugin;

    public PlayerListener(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!plugin.getPlayerData().hasStrengthSet(uuid)) {
            plugin.getPlayerData().setStrength(uuid,
                    plugin.getPluginConfig().getDefStrength());
            plugin.getWeaponManager().randomWeapon(player);
        }

        plugin.getCooldownManager().resetCooldown(uuid);

        if (!Sword.active.containsKey(uuid) && Sword.offhand.containsKey(uuid)) {
            player.getInventory().setItemInOffHand(Sword.offhand.remove(uuid));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (Sword.active.containsKey(uuid)) {
            player.getInventory().setItemInOffHand(
                    Sword.offhand.getOrDefault(uuid, new ItemStack(Material.AIR)));
        }

        plugin.getPlayerData().saveAll();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        UUID uuid = victim.getUniqueId();

        if (plugin.getPlayerData().getStrength(uuid) > plugin.getPluginConfig().getMinStrength()) {
            victim.getWorld().dropItemNaturally(
                    victim.getLocation(), plugin.getItemManager().getStrengthItem());
            plugin.getPlayerData().removeStrength(uuid, 1);
            sendStrengthMessage(victim);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.NAUTILUS_SHELL) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasCustomModelData()) return;

        int cmd = item.getItemMeta().getCustomModelData();

        if (cmd == Items.CMD_STRENGTH) {
            event.setCancelled(true);
            UUID uuid = player.getUniqueId();
            int current = plugin.getPlayerData().getStrength(uuid);
            int maxStr  = plugin.getPluginConfig().getMaxStrength();

            if (current < maxStr) {
                plugin.getPlayerData().addStrength(uuid, 1);
                item.setAmount(item.getAmount() - 1);
                sendStrengthMessage(player);
                player.playSound(player.getLocation(),
                        Sound.BLOCK_END_PORTAL_FRAME_FILL, 1f, 1f);
            } else {
                player.sendMessage(Colors.c(
                        "&cВы уже на максимальной силе (&4+" + current + "&c)!"));
            }

        } else if (cmd == Items.CMD_REROLL) {
            event.setCancelled(true);
            item.setAmount(item.getAmount() - 1);
            plugin.getWeaponManager().randomWeapon(player);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!plugin.getPluginConfig().isNoStrength()) return;

        if (event.getNewEffect() == null) return;
        if (!event.getNewEffect().getType().equals(PotionEffectType.STRENGTH)) return;

        event.setCancelled(true);
        if (event.getEntity() instanceof Player p) {
            p.sendMessage(Colors.c("&cВы не можете использовать зелье силы!"));
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (Sword.active.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    private void sendStrengthMessage(Player player) {
        UUID uuid = player.getUniqueId();
        int str   = plugin.getPlayerData().getStrength(uuid);
        String sign = str >= 0 ? "+" : "";
        player.sendMessage(Colors.c("&6&lВаша новая сила: &c&l" + sign + str));
    }
}