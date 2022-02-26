package me.camm.productions.bedwars.Arena.Teams;

public enum BedMessage
{
    COOKIES("traded in for milk and cookies"),
    OBLITERATED("obliterated"),
    NORMAL("destroyed"),
    ICED("iced"),
    DELETED("deleted"),
    UNDONE("undone"),
    STOMPED("stomped"),
    YOINKED("yoinked"),
    EATEN("eaten"),
    BOMBED("bombed"),
    CRUSHED("crushed"),
    SWIPED("swiped"),
    BANNED("banned"),
    TAMPERED("tampered with"),
    WONDERFUL("a very wonderful bed but was sadly destroyed"),
    STOLEN("stolen"),
    CHOPPED("chopped to a million bits"),
    ATOMIZED("atomized"),
    HACKED_OUT("hacked out of existence");


    private final String message;
    BedMessage(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
