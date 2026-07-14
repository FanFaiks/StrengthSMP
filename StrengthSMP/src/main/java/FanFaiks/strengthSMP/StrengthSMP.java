package FanFaiks.strengthSMP;

import FanFaiks.strengthSMP.commands.*;
import FanFaiks.strengthSMP.config.Config;
import FanFaiks.strengthSMP.data.PlayerData;
import FanFaiks.strengthSMP.listeners.*;
import FanFaiks.strengthSMP.managers.Items;
import FanFaiks.strengthSMP.managers.Weapons;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class StrengthSMP extends JavaPlugin {

    private static StrengthSMP instance;
    private Config pluginConfig;
    private PlayerData playerData;
    private Weapons weaponManager;
    private FanFaiks.strengthSMP.managers.CD cooldownManager;
    private Items itemManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.pluginConfig    = new Config(this);
        this.playerData      = new PlayerData(this);
        this.itemManager     = new Items(this);
        this.weaponManager   = new Weapons(this);
        this.cooldownManager = new FanFaiks.strengthSMP.managers.CD(this);

        getCommand("strength").setExecutor(new Strength(this));
        getCommand("withdraw").setExecutor(new Withdraw(this));
        getCommand("withdraw").setTabCompleter(new Withdraw(this));
        getCommand("strengthsmp").setExecutor(new Ssmp(this));
        getCommand("strengthsmp").setTabCompleter(new Ssmp(this));
        getCommand("ability").setExecutor(new Abilities(this));
        getCommand("cooldown").setExecutor(new CD(this));
        getCommand("trust").setExecutor(new Trust(this));
        getCommand("trust").setTabCompleter(new Trust(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new Dmg(this), this);
        getServer().getPluginManager().registerEvents(new Projectiles(this), this);
        getServer().getPluginManager().registerEvents(new Inv(this), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                cooldownManager.tick();
            }
        }.runTaskTimer(this, 20L, 20L);

        getLogger().info("StrengthSMP");
    }

    @Override
    public void onDisable() {
        playerData.saveAll();
    }

    public static StrengthSMP getInstance()        { return instance; }
    public Config getPluginConfig()                 { return pluginConfig; }
    public PlayerData getPlayerData()               { return playerData; }
    public Weapons getWeaponManager()         { return weaponManager; }
    public FanFaiks.strengthSMP.managers.CD getCooldownManager()     { return cooldownManager; }
    public Items getItemManager()             { return itemManager; }
}