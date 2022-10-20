package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "VillagersGeneral")
public class VillagersGeneralConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int findPOIRange = 128;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int pathfindingMaxRange = 256;

    @ConfigEntry.Gui.Tooltip
    public boolean healOnWake = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidCactus = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidWater = true;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontClimb = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int villagerSafeFallDistance = 2;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersDontBreed = false;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontSummonGolems = false;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontLookForWorkstationsAtNight = true;

    @ConfigEntry.Gui.Tooltip
    public boolean noNitwitVillagers = false;

    @ConfigEntry.Gui.Tooltip
    public boolean allNitwitVillagers = false;

    @ConfigEntry.Gui.Tooltip
    public boolean allBabyVillagers = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int villagerStatRange = 256;

    @ConfigEntry.Gui.Tooltip
    public boolean enableVillagerBrainDebug = false;

    @ConfigEntry.Gui.Tooltip
    public boolean enableVillagerFindPOIDebug = false;
}