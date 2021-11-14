package me.camm.productions.bedwars.Arena.Teams.TeamTraps;

public enum TrapType
{
    DEFAULT("It's a Trap!"),
    MINER_FATIGUE("Miner-Fatigue trap"),
    ALARM("Alarm trap"),
    COUNTER_CHARGE("Counter-Offensive trap");

    private final String name;

    TrapType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
