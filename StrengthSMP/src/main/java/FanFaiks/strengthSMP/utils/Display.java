package FanFaiks.strengthSMP.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class Display {

    public static ItemDisplay spawnItemDisplay(Location loc, Material material,
                                               int customModelData, Vector3f translation,
                                               Vector3f scale) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta  = item.getItemMeta();
        if (customModelData > 0) meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);

        ItemStack finalItem = item;
        return loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
            display.setItemStack(finalItem);
            Transformation t = display.getTransformation();
            display.setTransformation(new Transformation(
                    translation != null ? translation : new Vector3f(0, 0, 0),
                    t.getLeftRotation(),
                    scale != null ? scale : new Vector3f(1, 1, 1),
                    t.getRightRotation()
            ));
        });
    }
}