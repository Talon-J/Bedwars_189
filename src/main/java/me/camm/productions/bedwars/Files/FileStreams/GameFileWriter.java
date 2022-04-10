package me.camm.productions.bedwars.Files.FileStreams;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;


public class GameFileWriter {
    private BufferedWriter writer;
    private final File file;
    private final Plugin plugin;

    public GameFileWriter(String path, Plugin plugin)
    {
        file = new File(path);
        this.plugin = plugin;
    }

    public void clear()
    {
        try
        {
            writer = new BufferedWriter(new FileWriter(file,false));
            writer.write("");
            writer.close();

        }
        catch (IOException ignored)
        {

        }
    }

    public void write(String[] lines, boolean delete)
    {
        try
        {
            writer = new BufferedWriter(new FileWriter(file,!delete));
            for (String s : lines) {
                writer.write(s + "\n");
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void write(ArrayList<String> lines, boolean delete)
    {
        try
        {
            writer = new BufferedWriter(new FileWriter(file,!delete));
            for (String s : lines) {
                writer.write(s + "\n");
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void writeSection(ArrayList<String[]> values)  //arraylist of string arrays.
    {
        try
        {

            writer = new BufferedWriter(new FileWriter(file,true));
            // System.out.println(values.size());

            for (String[] value : values) {

                for (String s : value) {
                    writer.write(s + "\n");
                }
                writer.newLine();

            }
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR] - COULD NOT WRITE DATA TO A FILE.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR] - RAN INTO EXCEPTION.");
        }
    }
}