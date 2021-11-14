package me.camm.productions.bedwars.Files.FileKeywords;

public enum Instructions
{
    PLACEHOLDER(new String[] {"This is a placeholder message. Instructions will be written here in the future!"});

    private final String[] instructions;

    Instructions(String[] instructions)
    {
        this.instructions = instructions;
    }

    public String[] getInstructions() {
        return instructions;
    }
}
