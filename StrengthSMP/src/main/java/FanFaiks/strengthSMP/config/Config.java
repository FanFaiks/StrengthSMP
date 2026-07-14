package FanFaiks.strengthSMP.config;

import FanFaiks.strengthSMP.StrengthSMP;

public class Config {

    private final StrengthSMP plugin;

    public Config(StrengthSMP plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public int getMaxStrength()     { return plugin.getConfig().getInt("max_strength", 5); }
    public int getDefStrength()     { return plugin.getConfig().getInt("def_strength", 0); }
    public int getMinStrength()     { return plugin.getConfig().getInt("min_strength", -3); }

    public int getMinStrengthToActivateAbility() {
        return plugin.getConfig().getInt("minimum_strength_to_activate_ability", 5);
    }

    public int getMinStrengthToHavePassive() {
        return plugin.getConfig().getInt("minimum_strength_to_have_passive", 0);
    }

    public boolean isShowDurationInActionbar() {
        return plugin.getConfig().getBoolean("show_duration_in_actionbar", true);
    }

    public int getStunAxeDuration()       { return plugin.getConfig().getInt("stun_axe_duration", 1); }
    public double getSwordUltMultiplier() { return plugin.getConfig().getDouble("sword_ultimate_multiplier", 1.5); }
    public int getMaceCooldown()          { return plugin.getConfig().getInt("mace_cooldown", 30); }
    public boolean isNoStrength()         { return plugin.getConfig().getBoolean("no_strength", true); }
    public boolean isDealLessDamage()     { return plugin.getConfig().getBoolean("deal_less_damage", true); }
    public double getBowBeamDamage()      { return plugin.getConfig().getDouble("bow_beam_damage", 4) * 2; }
    public int getBowBeamRange()          { return plugin.getConfig().getInt("bow_beam_range", 20); }
    public double getBowBeamWidth()       { return plugin.getConfig().getDouble("bow_beam_width", 1.5); }

    public int getSwordCooldown()    { return plugin.getConfig().getInt("sword_cooldown", 60); }
    public int getAxeCooldown()      { return plugin.getConfig().getInt("axe_cooldown", 60); }
    public int getTridentCooldown()  { return plugin.getConfig().getInt("trident_cooldown", 60); }
    public int getShieldCooldown()   { return plugin.getConfig().getInt("shield_cooldown", 60); }
    public int getBowCooldown()      { return plugin.getConfig().getInt("bow_cooldown", 60); }
    public int getCrossbowCooldown() { return plugin.getConfig().getInt("crossbow_cooldown", 60); }

    public int getSwordDuration()    { return plugin.getConfig().getInt("sword_duration", 15); }
    public int getAxeDuration()      { return plugin.getConfig().getInt("axe_duration", 5); }
    public int getTridentDuration()  { return plugin.getConfig().getInt("trident_duration", 5); }
    public int getShieldDuration()   { return plugin.getConfig().getInt("shield_duration", 15); }
    public int getCrossbowDuration() { return plugin.getConfig().getInt("crossbow_duration", 15); }
    public int getBowBeams()         { return plugin.getConfig().getInt("bow_beams", 3); }

    public int getSwordUltimate()    { return plugin.getConfig().getInt("sword_ultimate", 5); }
    public int getAxeUltimate()      { return plugin.getConfig().getInt("axe_ultimate", 7); }
    public int getTridentUltimate()  { return plugin.getConfig().getInt("trident_ultimate", 3); }
    public int getCrossbowUltimate() { return plugin.getConfig().getInt("crossbow_ultimate", 5); }
    public int getBowUltimate()      { return plugin.getConfig().getInt("bow_ultimate", 10); }

    public int getSwordPassive()    { return plugin.getConfig().getInt("sword_passive", 3); }
    public int getAxePassive()      { return plugin.getConfig().getInt("axe_passive", 5); }
    public int getTridentPassive()  { return plugin.getConfig().getInt("trident_passive", 2); }
    public int getCrossbowPassive() { return plugin.getConfig().getInt("crossbow_passive", 3); }
    public int getBowPassive()      { return plugin.getConfig().getInt("bow_passive", 2); }
}