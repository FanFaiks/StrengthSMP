package FanFaiks.strengthSMP.data;

import FanFaiks.strengthSMP.StrengthSMP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerData {

    private final StrengthSMP plugin;
    private final File dataFile;
    private FileConfiguration data;

    private final Map<UUID, Integer>     strengths = new HashMap<>();
    private final Map<UUID, String>      weapons   = new HashMap<>();
    private final Map<UUID, Set<String>> trusted   = new HashMap<>();

    public PlayerData(StrengthSMP plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); }
            catch (IOException e) { e.printStackTrace(); }
        }
        this.data = YamlConfiguration.loadConfiguration(dataFile);
        loadAll();
    }

    private void loadAll() {
        if (data.getConfigurationSection("strength") != null) {
            for (String key : data.getConfigurationSection("strength").getKeys(false)) {
                strengths.put(UUID.fromString(key), data.getInt("strength." + key));
            }
        }
        if (data.getConfigurationSection("weapon") != null) {
            for (String key : data.getConfigurationSection("weapon").getKeys(false)) {
                weapons.put(UUID.fromString(key), data.getString("weapon." + key));
            }
        }
        if (data.getConfigurationSection("trusted") != null) {
            for (String key : data.getConfigurationSection("trusted").getKeys(false)) {
                trusted.put(UUID.fromString(key),
                        new HashSet<>(data.getStringList("trusted." + key)));
            }
        }
    }

    public void saveAll() {
        strengths.forEach((uuid, val) -> data.set("strength." + uuid, val));
        weapons.forEach((uuid, val)   -> data.set("weapon."   + uuid, val));
        trusted.forEach((uuid, set)   -> data.set("trusted."  + uuid, new ArrayList<>(set)));
        try { data.save(dataFile); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public int getStrength(UUID uuid) {
        return strengths.getOrDefault(uuid, plugin.getPluginConfig().getDefStrength());
    }

    public void setStrength(UUID uuid, int value)  { strengths.put(uuid, value); }
    public void addStrength(UUID uuid, int amount)  { setStrength(uuid, getStrength(uuid) + amount); }
    public void removeStrength(UUID uuid, int amount) { setStrength(uuid, getStrength(uuid) - amount); }
    public boolean hasStrengthSet(UUID uuid)        { return strengths.containsKey(uuid); }

    public String getWeapon(UUID uuid)              { return weapons.getOrDefault(uuid, "sword"); }
    public void setWeapon(UUID uuid, String weapon) { weapons.put(uuid, weapon); }
    public boolean hasWeaponSet(UUID uuid)          { return weapons.containsKey(uuid); }

    public Set<String> getTrusted(UUID uuid)        { return trusted.computeIfAbsent(uuid, k -> new HashSet<>()); }
    public void addTrusted(UUID uuid, String name)  { getTrusted(uuid).add(name); }
    public void removeTrusted(UUID uuid, String name) { getTrusted(uuid).remove(name); }

    public boolean isTrusted(UUID ownerUuid, Player target) {
        return getTrusted(ownerUuid).contains(target.getName());
    }
}