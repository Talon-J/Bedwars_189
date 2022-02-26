package me.camm.productions.bedwars.Files.FileKeywords;

public enum Instructions
{
    HEADER(new String[] {"This is the format the config should follow. \n" +
            "For teams, which are put in the teams file:"}),

    TEAM(new String[] {"TeamColor: color", "ForgeTime: integer", "ForgeSpawn: x/y/z", "TeamSpawn: x/y/z/yaw",
            "Bed: x1,x2/y1,y2/z1,z2", "Chest: x/y/z","QuickBuySpawn: x/y/z/yaw","TeamBuySpawn: x/y/z/yaw",
            "RestrictedPlaceArea: x1,x2/y1,y2/z1,z2", "HealPoolArea: x1,x2/y1,y2/z1,z2",
            "TrapTriggerArea: x1,x2/y1,y2/z1,z2"}),

    WORLD_GEN(new String[]{"For the arena, which is put in the world data file:"}),
    ARENA(new String[]{"World: abc", "Bounds: x1,x2/y1,y2/z1,z2", "SpectatorSpawn: x/y/z", "Void: y"}),

    GENERATOR(new String[] {"Generators go in the same file, after the world info."}),
    GENERATOR_INFO(new String[] {"Generator:","Type: Diamond or Emerald", "Spawn: x/y/z", "RegisteredArea: x1,x2/y1,y2/z1,z2"}),
    CASE_SENSITIVE(new String[]{"Please note that the keywords (E.g TeamColor, etc) are case sensitive."}),
    COMMENT(new String[] {"To put a single line comment in the config, use the # symbol followed by what you want to comment.",
            "Anything after a # on the same line is considered a comment."}),

    DATA(new String[]{"By the x1, y1, z1, etc, those are whole number or decimal values, positive or negative.",
            "The yaw is the rotation shown in-game in the f3 menu, and an \"integer\" is a whole number, no decimals.",
            "\"abc, or color\" means it's expecting a word or a sentence. For world, it is the world name, for teams, it's the color."}),

    DATA_TWO(new String[]{"Parameters that allow decimal values: Void, SpectatorSpawn, QuickBuySpawn, TeamBuySpawn, ForgeSpawn, TeamSpawn, GeneratorSpawn"}),

    COLORS(new String[] {"Possible team colors: red, blue, yellow, white, aqua, gray, pink, green"}),
    COMMANDS(new String[] {"Commands you'll need: /setup /register /start /unregister /shout"});

    private final String[] instructions;

    Instructions(String[] instructions)
    {
        this.instructions = instructions;
    }

    public String[] getInstructions() {
        return instructions;
    }
}
