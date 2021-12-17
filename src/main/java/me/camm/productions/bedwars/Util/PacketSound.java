package me.camm.productions.bedwars.Util;

public enum PacketSound
{
    DRAGON("mob.enderdragon.growl",1F),
    PLING("note.pling",1.4F),
    RECORD_STAL("records.stal",1F),
    ALARM("note.pling",1.5F),
    ALARM_TWO("note.pling",1.6F),
    WITHER("mob.wither.death",1F),
    ENDERMAN("mob.endermen.portal",1F);

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
