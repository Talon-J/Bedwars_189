package me.camm.productions.bedwars.Arena.GameRunning.Events;

public enum EventTime
{
    TIME_IN_MINUTE(60),

    TICKS(2),
    RUNNABLE_PERIOD(20),

    DIAMOND_TIER_ONE_TIME(30),
    DIAMOND_TIER_TWO_TIME(23),
    DIAMOND_TIER_THREE_TIME(12),

    EMERALD_TIER_ONE_TIME(64),
    EMERALD_TIER_TWO_TIME(50),
    EMERALD_TIER_THREE_TIME(35),

   //set to smaller values for testing

    DIAMOND_UPGRADE_TWO(20), //300
    EMERALD_UPGRADE_TWO(40), //600

    DIAMOND_UPGRADE_THREE(60), //900
    EMERALD_UPGRADE_THREE(80), //1200

    BED_WARNING_TIME(100), //1500
    BED_DESTROY_TIME(120), //1800

    DRAGON_WARNING_TIME(140), //2100
    DRAGON_SPAWN_TIME(160), //2400
    GAME_END_WARNING(180), //2700
    TOTAL_GAME_TIME(200); //3000




    private final int time;

    EventTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
