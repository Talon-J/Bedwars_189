package me.camm.productions.bedwars.Arena.Players.Scoreboards;

import org.bukkit.ChatColor;

public enum ScoreBoardHeader
{
    DIAMOND_TWO_HEADER("Diamond II in "+ ChatColor.GREEN),
    DIAMOND_THREE_HEADER("Diamond III in "+ChatColor.GREEN),
    EMERALD_TWO_HEADER("Emerald II in "+ChatColor.GREEN),
    EMERALD_THREE_HEADER("Emerald III in "+ChatColor.GREEN),
    BED_GONE_HEADER("Bed Gone in "+ChatColor.GREEN),
    SUDDEN_DEATH_HEADER("S.Death in "+ChatColor.GREEN),
    GAME_END_HEADER("Game end in "+ChatColor.RED),

    B_ONE(ChatColor.RED.toString()),
    B_TWO(ChatColor.YELLOW.toString()),
    B_THREE(ChatColor.GREEN.toString()),

    TITLE(ChatColor.YELLOW+""+ChatColor.BOLD+"BedWars"),
    TEAM_ALIVE(ChatColor.BOLD+"\u2714"),
    TEAM_DEAD(ChatColor.RED+""+ChatColor.BOLD+"\u2718"),
    KILLS("Kills: "+ChatColor.GREEN),
    FINALS("Finals: "+ChatColor.GREEN),
    BEDS("Beds Broken: "+ChatColor.GREEN),
    CURRENT_TEAM(ChatColor.GRAY+"You"),
    HEART(" "+ChatColor.RED+"\u2764"+ChatColor.RESET),

    //change to a randomizer
    SPACE_CREDIT(ChatColor.YELLOW+"not hypixel.net"),

    DUMMY("dummy"),
    HEALTH_CATEGORY("Health"),
    HEALTH_CATEGORY_TWO("Health 2"),
    HEALTH_CRITERIA("health"),
    OBJECTIVE_ONE("Primary"),
    OBJECTIVE_TWO("Buffer"),
    INITIALIZER_ONE("INTIALIZER_ONE"),
    INITIALIZER_TWO("INITIALIZER_TWO"),

    //QUICK_BUY("QuickBuy"),  //Use the inventory database titles instead
    //TEAM_BUY("TeamBuy"),
    TIME("Time");
    //TEAM_JOIN(ChatColor.DARK_AQUA+""+ChatColor.BOLD+"Join a Team");




    private final String phrase;

    ScoreBoardHeader(String phrase)
    {
        this.phrase = phrase;
    }

    public String getPhrase()
    {
        return phrase;
    }

    @Override
    public String toString() {
        return getPhrase();
    }

}
