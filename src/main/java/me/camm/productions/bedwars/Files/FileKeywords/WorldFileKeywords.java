package me.camm.productions.bedwars.Files.FileKeywords;


//Enum for world file reading
public enum WorldFileKeywords
{
    WORLD("World",0),
    ARENA_BOUNDS("Bounds",1),
    SPEC_SPAWN("SpectatorSpawn",2),
    VOID("Void",3),

    GENERATOR("Generator",0),
    GEN_TYPE("Type",1),
    GEN_SPAWN("Spawn",2),
    GEN_BOX("RegisteredArea",3),
    DIAMOND("Diamond",-1),
    EMERALD("Emerald",-1);

    private final int index;
    private final String key;

    WorldFileKeywords(String key, int index)
    {
        this.index = index;
        this.key = key;
    }

    public int getIndex()
    {
        return this.index;
    }

    public String getKey()
    {
        return this.key;
    }

    public WorldFileKeywords[] getGeneratorOrder()
    {
        return new WorldFileKeywords[] {GENERATOR,GEN_TYPE,GEN_SPAWN,GEN_BOX};
    }

    public WorldFileKeywords[] getWorldOrder()
    {
        return new WorldFileKeywords[] {WORLD,ARENA_BOUNDS,SPEC_SPAWN,VOID};
    }

}
