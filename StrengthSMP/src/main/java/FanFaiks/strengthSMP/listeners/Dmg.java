package FanFaiks.strengthSMP.listeners;

import FanFaiks.strengthSMP.StrengthSMP;
import FanFaiks.strengthSMP.utils.Colors;
import FanFaiks.strengthSMP.weapons.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

public class Dmg implements Listener {

    private final StrengthSMP plugin;
    private final Random random = new Random();

    public Dmg(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMaceDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        ItemStack tool = attacker.getInventory().getItemInMainHand();
        if (tool.getType() != Material.MACE) return;
        int maceCd = plugin.getPluginConfig().getMaceCooldown();
        if (maceCd <= 0) return;
        if (attacker.hasCooldown(Material.MACE)) {
            event.setCancelled(true);
            return;
        }
        attacker.setCooldown(Material.MACE, maceCd * 20);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onStrengthDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!isFullSwing(attacker)) return;

        UUID uuid    = attacker.getUniqueId();
        int strength = plugin.getPlayerData().getStrength(uuid);

        if (strength > 0) {
            event.setDamage(event.getDamage() + strength / 1.5);
        } else if (strength < 0 && plugin.getPluginConfig().isDealLessDamage()) {
            event.setDamage(Math.max(0, event.getDamage() + strength / 1.5));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSwordPassive(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!isFullSwing(attacker)) return;

        UUID uuid     = attacker.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("sword")) return;
        if (!attacker.getInventory().getItemInMainHand().getType().name().contains("SWORD")) return;
        if (plugin.getPlayerData().getStrength(uuid) < plugin.getPluginConfig().getMinStrengthToHavePassive()) return;

        Sword.passiveCombo.merge(uuid, 1, Integer::sum);
        int combo = Sword.passiveCombo.getOrDefault(uuid, 0);

        if (combo >= plugin.getPluginConfig().getSwordPassive()) {
            if (event.getEntity() instanceof LivingEntity victim) {
                Location loc = victim.getLocation().add(0, 1, 0);
                victim.getWorld().spawnParticle(Particle.CRIT, loc, 5);
                victim.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
                event.setDamage(event.getDamage() * 1.5);
            }
        }

        int savedCombo = combo;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Sword.passiveCombo.getOrDefault(uuid, 0).equals(savedCombo)) {
                    Sword.passiveCombo.remove(uuid);
                }
            }
        }.runTaskLater(plugin, 30L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwordUltimateCombo(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!isFullSwing(attacker)) return;

        UUID uuid     = attacker.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("sword")) return;
        if (!attacker.getInventory().getItemInMainHand().getType().name().contains("SWORD")) return;
        if (plugin.getPlayerData().getStrength(uuid) < plugin.getPluginConfig().getMinStrengthToActivateAbility()) return;
        if (!plugin.getCooldownManager().isReady(uuid)) return;
        if (Sword.active.containsKey(uuid)) return;

        Sword.ultimateCombo.merge(uuid, 1, Integer::sum);
        int combo = Sword.ultimateCombo.getOrDefault(uuid, 0);

        if (combo >= plugin.getPluginConfig().getSwordUltimate()) {
            Sword.ultimateReady.put(uuid, true);
            attacker.sendMessage(Colors.c("&c&l🗡 &4&lГОТОВО!"));
            attacker.playSound(attacker.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
        }

        int savedCombo = combo;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Sword.ultimateCombo.getOrDefault(uuid, 0).equals(savedCombo)) {
                    Sword.ultimateCombo.remove(uuid);
                    if (Sword.ultimateReady.containsKey(uuid)) {
                        attacker.sendMessage(Colors.c("&c&l🗡 &4&lПотеряно"));
                        Sword.ultimateReady.remove(uuid);
                    }
                }
            }
        }.runTaskLater(plugin, 100L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSwordAbilityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;

        UUID uuid     = attacker.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("sword")) return;
        if (!attacker.getInventory().getItemInMainHand().getType().name().contains("SWORD")) return;
        if (!Sword.active.containsKey(uuid)) return;

        event.setDamage(event.getDamage() * plugin.getPluginConfig().getSwordUltMultiplier());

        if (event.getEntity() instanceof LivingEntity victim) {
            victim.getWorld().playSound(victim.getLocation(),
                    Sound.BLOCK_ANVIL_LAND, 1f, 1.5f);
            spawnSwordParticles(victim.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTridentPassive(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!isFullSwing(attacker)) return;

        UUID uuid     = attacker.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("trident")) return;
        if (attacker.getInventory().getItemInMainHand().getType() != Material.TRIDENT) return;
        if (plugin.getPlayerData().getStrength(uuid) < plugin.getPluginConfig().getMinStrengthToHavePassive()) return;

        Trident.hitCount.merge(uuid, 1, Integer::sum);
        if (Trident.hitCount.getOrDefault(uuid, 0) >= plugin.getPluginConfig().getTridentPassive()) {
            if (event.getEntity() instanceof LivingEntity victim) {
                if (!(victim instanceof Player vp) ||
                        !plugin.getPlayerData().isTrusted(uuid, vp)) {
                    victim.getWorld().strikeLightning(victim.getLocation());
                }
            }
            Trident.hitCount.remove(uuid);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTridentUltCombo(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!isFullSwing(attacker)) return;

        UUID uuid     = attacker.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("trident")) return;
        if (attacker.getInventory().getItemInMainHand().getType() != Material.TRIDENT) return;
        if (plugin.getPlayerData().getStrength(uuid) < plugin.getPluginConfig().getMinStrengthToActivateAbility()) return;
        if (!plugin.getCooldownManager().isReady(uuid)) return;
        if (Trident.active.containsKey(uuid)) return;
        if (Trident.ultimateReady.containsKey(uuid)) return;

        Trident.ultimateCombo.merge(uuid, 1, Integer::sum);
        if (Trident.ultimateCombo.getOrDefault(uuid, 0) >= plugin.getPluginConfig().getTridentUltimate()) {
            Trident.ultimateReady.put(uuid, true);
            Trident.ultimateCombo.remove(uuid);
            attacker.sendMessage(Colors.c("&b&l🔱 &3&lГОТОВО!"));
            attacker.playSound(attacker.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAxePassive(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!isFullSwing(attacker)) return;
        if (!isCrit(attacker)) return;

        UUID uuid     = attacker.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("axe")) return;
        if (!attacker.getInventory().getItemInMainHand().getType().name().contains("AXE")) return;
        if (plugin.getPlayerData().getStrength(uuid) < plugin.getPluginConfig().getMinStrengthToHavePassive()) return;
        if (!(event.getEntity() instanceof LivingEntity victimEntity)) return;
        if (Axe.stunned.containsKey(victimEntity.getUniqueId())) return;
        if (victimEntity instanceof Player vp && plugin.getPlayerData().isTrusted(uuid, vp)) return;

        Axe.passiveCombo.merge(uuid, 1, Integer::sum);
        if (Axe.passiveCombo.getOrDefault(uuid, 0) >= plugin.getPluginConfig().getAxePassive()) {
            Axe.passiveCombo.remove(uuid);
            stunEntity(victimEntity);
        }
    }

    private void stunEntity(LivingEntity entity) {
        UUID victimUuid = entity.getUniqueId();
        Axe.stunned.put(victimUuid, true);
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_BREEZE_DEATH, 1f, 1f);
        spawnStunParticles(entity.getLocation());

        new BukkitRunnable() {
            @Override
            public void run() {
                Axe.stunned.remove(victimUuid);
            }
        }.runTaskLater(plugin, plugin.getPluginConfig().getStunAxeDuration() * 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAxeUltCombo(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!isFullSwing(attacker)) return;
        if (!isCrit(attacker)) return;

        UUID uuid     = attacker.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("axe")) return;
        if (!attacker.getInventory().getItemInMainHand().getType().name().contains("AXE")) return;
        if (plugin.getPlayerData().getStrength(uuid) < plugin.getPluginConfig().getMinStrengthToActivateAbility()) return;
        if (!plugin.getCooldownManager().isReady(uuid)) return;
        if (Axe.active.containsKey(uuid)) return;
        if (Axe.ultimateReady.containsKey(uuid)) return;

        Axe.ultimateCombo.merge(uuid, 1, Integer::sum);
        if (Axe.ultimateCombo.getOrDefault(uuid, 0) >= plugin.getPluginConfig().getAxeUltimate()) {
            Axe.ultimateReady.put(uuid, true);
            Axe.ultimateCombo.remove(uuid);
            attacker.sendMessage(Colors.c("&7&l🪓 &8&lГОТОВО!"));
            attacker.playSound(attacker.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAxeAbilityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
        if (!isFullSwing(attacker)) return;

        UUID uuid     = attacker.getUniqueId();
        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("axe")) return;
        if (!attacker.getInventory().getItemInMainHand().getType().name().contains("AXE")) return;
        if (!Axe.active.containsKey(uuid)) return;
        if (!(event.getEntity() instanceof LivingEntity victimEntity)) return;
        if (victimEntity instanceof Player vp &&
                plugin.getPlayerData().isTrusted(uuid, vp)) return;

        event.setCancelled(true);
        victimEntity.getWorld().playSound(victimEntity.getLocation(),
                Sound.ENTITY_BREEZE_CHARGE, 1f, 1f);

        Axe.storedDamage.merge(uuid, event.getDamage(), Double::sum);
        Axe.victim.put(uuid, victimEntity);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShieldPassive(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        UUID uuid = victim.getUniqueId();

        String weapon = plugin.getPlayerData().getWeapon(uuid);
        if (!weapon.equals("shield")) return;
        if (plugin.getPlayerData().getStrength(uuid) < plugin.getPluginConfig().getMinStrengthToHavePassive()) return;

        if (victim.hasCooldown(Material.SHIELD)) {
            event.setDamage(event.getDamage() * 0.8);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShieldAbility(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        UUID uuid = victim.getUniqueId();

        if (!Shield.active.containsKey(uuid)) return;
        event.setCancelled(true);

        if (!(event.getDamager() instanceof Player attacker)) return;

        Vector knockback = victim.getLocation()
                .subtract(attacker.getLocation())
                .toVector()
                .normalize()
                .multiply(-1.0);

        attacker.setVelocity(attacker.getVelocity()
                .add(new Vector(0, 0.3, 0))
                .add(knockback));

        victim.getWorld().playSound(victim.getLocation(),
                Sound.ENTITY_SHULKER_SHOOT, 1f, 1f);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCrossbowPassiveDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile proj)) return;
        if (!(proj instanceof Arrow)) return;
        UUID arrowUuid = proj.getUniqueId();

        if (!Crossbow.passiveArrows.containsKey(arrowUuid)) return;
        Crossbow.passiveArrows.remove(arrowUuid);

        event.setDamage(event.getDamage() * 2);
        if (event.getEntity() instanceof LivingEntity le) {
            le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 140, 0));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCrossbowAbilityArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile proj)) return;
        if (!(proj instanceof Arrow)) return;
        UUID arrowUuid = proj.getUniqueId();

        if (!Crossbow.abilityArrows.containsKey(arrowUuid)) return;
        Crossbow.abilityArrows.remove(arrowUuid);

        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (!(proj.getShooter() instanceof Player attacker)) return;
        if (target instanceof Player tp &&
                plugin.getPlayerData().isTrusted(attacker.getUniqueId(), tp)) return;

        target.getWorld().playSound(target.getLocation(),
                Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1f);
        attacker.getWorld().playSound(attacker.getLocation(),
                Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1f);

        new Crossbow(plugin).leashEffect(attacker, target);
    }

    private boolean isFullSwing(Player player) {
        return player.getAttackCooldown() >= 1.0f;
    }

    private boolean isCrit(Player player) {
        return player.getFallDistance() > 0
                && !player.isOnGround()
                && !player.isInWater();
    }

    private void spawnSwordParticles(Location loc) {
        double yaw = Math.toRadians(loc.getYaw());
        double s1x = Math.cos(yaw) * 0.35;
        double s1z = Math.sin(yaw) * 0.35;
        double s2x = Math.cos(yaw + Math.PI) * 0.35;
        double s2z = Math.sin(yaw + Math.PI) * 0.35;
        Location base = loc.clone().add(0, 0.7, 0);
        loc.getWorld().spawnParticle(Particle.DUST,
                base.clone().add(s1x, 0, s1z), 1, 0, 0, 0,
                new Particle.DustOptions(Color.RED, 2f));
        loc.getWorld().spawnParticle(Particle.DUST,
                base.clone().add(s2x, 0, s2z), 1, 0, 0, 0,
                new Particle.DustOptions(Color.RED, 2f));
    }

    private void spawnStunParticles(Location loc) {
        for (int i = 0; i < 30; i++) {
            double dx = (random.nextDouble() - 0.5) * 1.5;
            double dy = random.nextDouble() * 2;
            double dz = (random.nextDouble() - 0.5) * 1.5;
            loc.getWorld().spawnParticle(Particle.DUST,
                    loc.clone().add(dx, dy, dz), 1, 0, 0, 0,
                    new Particle.DustOptions(Color.fromRGB(85, 85, 85), 1f));
        }
    }
}