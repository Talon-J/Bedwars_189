package me.camm.productions.bedwars.Arena.Players.Scoreboards;

import org.bukkit.ChatColor;

public enum BoardEasterEgg
{
    MARCARONI(ChatColor.YELLOW+"Big Man Marc"),
    PASTA(ChatColor.YELLOW+"Macaroni"),
    CAMM(ChatColor.GOLD+"CAMM_H87"),
    TEAVEE(ChatColor.AQUA+"MrTeavee"),
    PRINTER(ChatColor.GREEN+"Printer screen"),
    FISHY(ChatColor.YELLOW+"TheBest_Fishy"),
    TELLYBOI(ChatColor.YELLOW+"Tellyboi"),
    MC_DONALDS(ChatColor.YELLOW+"McDeezNuts"),
    NOT_HYPIXEL(ChatColor.YELLOW+"not hypixel.net"),
    LACHI_MOLALA(ChatColor.YELLOW+"Lachi_Molala"),
    KIWI(ChatColor.GREEN+"KiwiGod888"),
    BUSTER(ChatColor.YELLOW+"Buster"),
    LERCERPE(ChatColor.YELLOW+"Lercerpe"),
    DARKNESS(ChatColor.YELLOW+"GR3aterG0ld"),
    WAKIEZ(ChatColor.YELLOW+"XxwakiezxX"),
    FORTNITE_KID(ChatColor.YELLOW+"fortnite kid"),
    ZENITSU(ChatColor.YELLOW+"dodo Zenitsu"),
    ZENITSU52(ChatColor.YELLOW+"zenitsu52"),
    BW_POG(ChatColor.YELLOW+"Bedwars pog"),
    POTATO_MAN(ChatColor.YELLOW+"potatoman"),
    QUACK(ChatColor.YELLOW+"Quack");

    private final String phrase;

    BoardEasterEgg(String phrase) {
        this.phrase = phrase;
    }

    public String getPhrase() {
        return phrase;
    }
}
