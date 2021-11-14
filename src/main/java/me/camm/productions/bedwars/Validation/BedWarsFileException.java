package me.camm.productions.bedwars.Validation;

import org.bukkit.ChatColor;

public class BedWarsFileException extends Exception
{

    private final String fileOrigin;
    private final FileProblem problemType;

    public BedWarsFileException(String fileOrigin, FileProblem problemType)
    {
        this.fileOrigin = fileOrigin;
        this.problemType = problemType;
    }

    @Override
    public String toString()
    {
        return ChatColor.RED+"BedWarsFileException: We tried to operate on the file in the path: "+fileOrigin+
                ChatColor.RED+" but we encountered a Problem: "+problemType.getProblem();
    }
}
