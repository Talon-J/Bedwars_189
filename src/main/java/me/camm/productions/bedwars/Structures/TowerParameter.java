package me.camm.productions.bedwars.Structures;

public enum TowerParameter
{
    BASE_LENGTH(3),
    BASE_WIDTH(2),


  //priming measurements for building
    PRIME_ONE(1),
    PRIME_TWO(2),

    PERIOD(2),
    DELAY(5),

    POSITIVE(1),
    NEGATIVE(-1),


    HEIGHT(5),
    DOCKING_HEIGHT(3),
    BATTLEMENT_BASE_HEIGHT(6),

    PLATFORM_LENGTH(5),
    PLATFORM_WIDTH(4),
    HATCH_ROW(1),

    SIDE_BATTLEMENTS(2),
    MAIN_BATTLEMENTS(3),

    BATTLEMENT_SIZE(1),
    SIDE_BATTLEMENT_GAP(3),
    LENGTH_WITH_BATTLEMENTS(6);

    private final int measurement;

    TowerParameter(int measurement) {
        this.measurement = measurement;
    }

    public int getMeasurement() {
        return measurement;
    }
}
