package me.camm.productions.bedwars.Util.Locations;

public enum RegisterType
{
    AIR_ONLY(-1),
    NOT_AIR(0),
    EVERYTHING(1);

    private final int type;

    RegisterType(int type) {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }


}
