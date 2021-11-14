package me.camm.productions.bedwars.Util.GamePhase;

import org.bukkit.ChatColor;

public enum GameEventText
{
    BED_DESTROY_SCHEDULED_WARNING(ChatColor.RED+"[BEDWARS] All beds will be destroyed in 5 minutes!"),
    SPAWN_DRAGONS(ChatColor.RED+"Sudden Death"),
    DESTROY_BEDS(ChatColor.RED+"[BEDWARS] All beds have been destroyed!"),
    DRAGON_SPAWN_SCHEDULED_WARNING(ChatColor.RED+"[BEDWARS] Sudden Death in 5 minutes!"),
    GAME_END_SCHEDULED_WARNING(ChatColor.RED+"[BEDWARS] The game ends in 5 minutes!"),
    GAME_END_TEXT(ChatColor.YELLOW+"[BEDWARS] The game has ended!"),
    UPGRADE_DIAMONDS(ChatColor.AQUA+"Diamond"+ChatColor.YELLOW+" generators have been upgraded to tier "+ChatColor.RED),
    UPGRADE_EMERALDS(ChatColor.GREEN+"Emerald"+ ChatColor.YELLOW+" generators have been upgraded to tier "+ChatColor.RED);

    private final String text;

    GameEventText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
