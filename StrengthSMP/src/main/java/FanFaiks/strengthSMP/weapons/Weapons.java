package FanFaiks.strengthSMP.weapons;

import org.bukkit.entity.Player;

public interface Weapons {
    void activate(Player player);
    default void cancel(Player player) {}
}