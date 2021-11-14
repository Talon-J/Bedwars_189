package me.camm.productions.bedwars.Util;

public enum PacketSound
{
    DRAGON("mob.enderdragon.growl",1),
    PLING("note.pling",1.3F),
    RECORD_STAL("records.stal",1),
    ALARM("note.pling",1.5F),
    ALARM_TWO("note.pling",1.6F),
    WITHER("mob.wither.death",1),
    ENDERMAN("mob.endermen.portal",1);

    private final String soundName;
    private final float pitch;

    PacketSound(String soundName, float pitch) {
        this.soundName = soundName;
        this.pitch = pitch;
    }

    public String getSoundName() {
        return soundName;
    }

    public float getPitch() {
        return pitch;
    }
}
