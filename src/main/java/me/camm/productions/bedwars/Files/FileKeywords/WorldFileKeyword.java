package me.camm.productions.bedwars.Files.FileKeywords;


//Enum for world file reading
public enum WorldFileKeyword
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

    WorldFileKeyword(String key, int index)
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

    public WorldFileKeyword[] getGeneratorOrder()
    {
        return new WorldFileKeyword[] {GENERATOR,GEN_TYPE,GEN_SPAWN,GEN_BOX};
    }

    public WorldFileKeyword[] getWorldOrder()
    {
        return new WorldFileKeyword[] {WORLD,ARENA_BOUNDS,SPEC_SPAWN,VOID};
    }

}
