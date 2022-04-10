package me.camm.productions.bedwars.Files.FileKeywords;

/**
 * @author CAMM
 * Keywords for reading from the team config files
 * Index is the order that they should come in
 */
public enum TeamFileKeywords
{
    TEAM_GENERATE("TeamColor",0),
    FORGE_TIME("ForgeTime",1),
    FORGE_SPAWN("ForgeSpawn",2),
    SPAWN("TeamSpawn",3),
    BED("Bed",4),
    CHEST("Chest",5),
    QUICK_BUY("QuickBuySpawn",6),
    TEAM_BUY("TeamBuySpawn",7),
    REGISTERED_BOUNDS("RestrictedPlaceArea",8),
    AURA("HealPoolArea",9),
    TRIGGER_AREA("TrapTriggerArea",10);

    private final String key;
    private final int index;

    TeamFileKeywords(String key, int index)
    {
        this.key = key;
        this.index = index;
    }

    public String getKey()
    {
        return key;
    }

    public int getIndex() { return index; }

    public TeamFileKeywords[] getOrder()
    {
        return new TeamFileKeywords[] {TEAM_GENERATE,FORGE_TIME,FORGE_SPAWN,SPAWN,BED,CHEST,
                QUICK_BUY,TEAM_BUY,REGISTERED_BOUNDS,AURA,TRIGGER_AREA};
    }
}
