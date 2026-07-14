package FanFaiks.strengthSMP.listeners;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import FanFaiks.strengthSMP.weapons.Sword;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class Inv implements Listener {

    private final StrengthSMP plugin;

    public Inv(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();

        if (Sword.active.containsKey(uuid)) {
            if (event.getClick() == ClickType.SWAP_OFFHAND || event.getSlot() == 40) {
                event.setCancelled(true);
                return;
            }
        }

        String title = Colors
                .toPlain(event.getView().title());
        if (!title.contains("ᴡᴇᴀᴘᴏɴs")) return;

        event.setCancelled(true);
        if (!player.hasPermission("strengthsmp.admin")) return;

        int slot = event.getSlot();
        if (slot == 4) {
            player.getInventory().addItem(plugin.getItemManager().getRerollItem());
        } else if (slot == 22) {
            player.getInventory().addItem(plugin.getItemManager().getStrengthItem());
        }
    }
}