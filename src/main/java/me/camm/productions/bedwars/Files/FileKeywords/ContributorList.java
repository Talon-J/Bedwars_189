package me.camm.productions.bedwars.Files.FileKeywords;

public enum ContributorList
{
    HEADER(new String[] {"Plugin Team:"}),
    CODER(new String[] {"Programming, Optimization, Debugging:","CAMM_H87"}),
    TESTING(new String[] {"Testing:","CAMM_H87","Lachi_molala","TheBest_Fishy","Zenitsu52","Marcaroni_","KiwiGod888","MrTeavee"}),
    MAJORS(new String[] {"Major Contributors: ","Buster_Buckaroo","CAMM_H87","KiwiGod888","Lercerpe","Marcaroni_","MrTeavee","Lachi_molala"}),
    RESEARCH(new String[] {"Game Research:","adithemaddy","Buster_Buckaroo", "CAMM_H87","GR3aterG0ld", "KiwiGod888", "Lercerpe", "Marcaroni_", "MrTeavee","XxWakiezxX"}),
    MORALE(new String[] {"Motivation:","Marcaroni_","MrTeavee","Lachi_molala"}),
    SPECIAL(new String[] {"Special thanks:","Buster_Buckaroo - For a private Hypixel BedWars match used for experimentation.","Lachi_molala - For lending a Minecraft account to help with dedicated testing."}),

    ORIGIN(new String[] {"Message from CAMM_H87 (Developer) - \"No matter how hard something is, keep up with the persistence; you can do it. I coded the plugin on my little spare time while juggling school and social life over the course of several years. It became a hobby of mine.\"" ,
            " Bedwars is originally made by the Hypixel Team. This plugin is for private educational and developmental purposes only, not for commercial use.","Idea for the challenge was initially from MrTeavee and KiwiGod888."}),


    DATE(new String[]{"List up to date as of Dec 19 2021. Plugin development started in November 2019. Project finished in ----"});

    private final String[] players;

    ContributorList(String[] players)
    {
        this.players = players;
    }

    public String[] getSection()
    {
        return players;
    }
}
