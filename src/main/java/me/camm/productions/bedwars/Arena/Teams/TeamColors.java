package me.camm.productions.bedwars.Arena.Teams;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public enum TeamColors
{
    RED("Red",14,Color.RED,ChatColor.RED,"R",DyeColor.RED),
    BLUE("Blue",11,Color.BLUE,ChatColor.BLUE,"B",DyeColor.BLUE),
    YELLOW("Yellow",4,Color.YELLOW,ChatColor.YELLOW,"Y",DyeColor.YELLOW),
    WHITE("White",0,Color.WHITE,ChatColor.WHITE,"W",DyeColor.WHITE),
    AQUA("Aqua",9,Color.AQUA,ChatColor.AQUA,"A",DyeColor.CYAN),
    GRAY("Gray",7,Color.GRAY,ChatColor.GRAY,"G",DyeColor.GRAY),
    PINK("Pink",6,Color.FUCHSIA,ChatColor.LIGHT_PURPLE,"P",DyeColor.PINK),
    GREEN("Green",5,Color.LIME,ChatColor.GREEN,"G",DyeColor.LIME);


    private final int value;
    private final String name;
    private final Color color;
    private final ChatColor chatColor;
    private final String symbol;
    private final DyeColor dye;

    TeamColors(String name, int value, Color color, ChatColor chatColor, String symbol, DyeColor dye)
    {
       this.value = value;
       this.name = name;
       this.color = color;
       this.chatColor = chatColor;
       this.symbol = symbol;
       this.dye = dye;
    }

    public int getValue()
    {
        return value;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public String getName()
    {
        return name;
    }

    public Color getColor()
    {
        return color;
    }

    public ChatColor getChatColor()
    {
        return chatColor;
    }

    public DyeColor getDye() {
        return dye;
    }
}
