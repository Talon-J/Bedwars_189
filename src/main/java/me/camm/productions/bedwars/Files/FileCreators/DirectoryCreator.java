package me.camm.productions.bedwars.Files.FileCreators;

import me.camm.productions.bedwars.Files.FileKeywords.ContributorList;
import me.camm.productions.bedwars.Files.FileKeywords.Instructions;
import me.camm.productions.bedwars.Files.FileStreams.GameFileWriter;
import me.camm.productions.bedwars.Util.Helpers.StringToolBox;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class DirectoryCreator extends StringToolBox
{
    private Server server;
    private Plugin plugin;

    private File mainFile;
    private File teamFile;
    private File worldFile;
    private File playerFolder;
    private File credits;
    private File instructionFile;


    public DirectoryCreator(Plugin plugin)
    {
        super(plugin);

        try {
            this.plugin = plugin;
            this.server = plugin.getServer();


            mainFile = new File(getMainFolderPath());
            teamFile = new File(getTeamPath());
            worldFile = new File(getWorldPath());
            playerFolder = new File(getPlayerFolderPath());
            credits = new File(getCreditsPath());
            instructionFile = new File(getInstructionsPath());




            //this.getDataFolder().getParentFile().getAbsolutePath()
            if (!mainFile.mkdir())
             server.getConsoleSender().sendMessage(ChatColor.YELLOW+"BW [WARN]: COULD NOT INITIALIZE MAIN FOLDER. (Does it already exist?)");


            if (!playerFolder.mkdir())
                server.getConsoleSender().sendMessage(ChatColor.YELLOW+"BW [WARN]: COULD NOT INITIALIZE PLAYER FOLDER. (Does it already exist?)");

        }
        catch (Exception e)
        {
           server.getLogger().log(Level.SEVERE,ChatColor.RED+"BW [FATAL]: COULD NOT INITIALIZE FOLDERS. [We can't set up the game like this!]");
           e.printStackTrace();

        }
    }

    public boolean createFolders()
    {
        try {
            ArrayList<String[]> contributors = new ArrayList<>();
            ContributorList[] values = ContributorList.values();
            for (ContributorList contributorList: values)
                contributors.add(contributorList.getSection());


            ArrayList<String[]> instructions = new ArrayList<>();
            for (Instructions i: Instructions.values())
                instructions.add(i.getInstructions());




           // boolean doesMainExist = false;
            if (!mainFile.exists())
            {
                mainFile.createNewFile();
                server.getConsoleSender().sendMessage(ChatColor.YELLOW+"BW [Detected]:Creating new Main Directory.");
            }
            else
                server.getConsoleSender().sendMessage(ChatColor.YELLOW+"BW [Detected]:Main Directory Exists.");


            GameFileWriter creditWriter = new GameFileWriter(credits.getAbsolutePath(),plugin);
            GameFileWriter instructWriter = new GameFileWriter(instructionFile.getAbsolutePath(),plugin);

            if (mainFile.exists()&&
                    teamFile.exists()&&
                    worldFile.exists()&&
                    playerFolder.exists()&&
                    credits.exists()&&
                    instructionFile.exists())
            {
                server.getConsoleSender().sendMessage(ChatColor.GREEN+"BW [DETECTED]: All files exist. Please make sure they are configured.");

                creditWriter.clear();
                creditWriter.writeSection(contributors);

                instructWriter.clear();
                instructWriter.writeSection(instructions);
                return true;
            }
            else
            {
                server.getConsoleSender().sendMessage(ChatColor.YELLOW+"BW [WARN]: AT-LEAST 1 FILE IN THE CONFIGURATION IS NOT CONFIGURED.");
                createFiles(mainFile);
                createFiles(teamFile);
                createFiles(worldFile);
                createFiles(playerFolder);
                createFiles(credits);
                createFiles(instructionFile);

                //TODO - have something to refresh the file [clear the entire file and rewrite.]
                creditWriter.clear();
                creditWriter.writeSection(contributors);

                instructWriter.clear();
                instructWriter.writeSection(instructions);


                return false;
            }
        }
        catch (Exception e)
        {
            server.getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR]: COULD NOT VERIFY FILE INTEGRITY.");
            e.printStackTrace();
            return false;
        }

    }

    private boolean createFiles(File file)
    {
        if (!file.exists())
        {
            try
            {
               file.createNewFile();
               return false;  //the file is not init
            }
            catch (FileNotFoundException e)
            {
                sendFileErrorReport(file, e);
                e.printStackTrace();
                return false;
            }
            catch (IOException e)
            {
                sendIOExceptionReport(file, e);
                e.printStackTrace();
                return false;
            }
        }
        return true;  //file is init
    }



    private void sendFileErrorReport(File file, Exception e)
    {
        server.getConsoleSender().sendMessage(ChatColor.RED+"+++ERROR REPORT+++");
        server.getConsoleSender().sendMessage(ChatColor.RED+"BW - ERROR TYPE: File Creation Error");
        server.getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR] - Ran into "+e.toString());
        server.getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR] - File Involved: "+file.getName());
        server.getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR] - DEBUG - Path: "+file.getAbsolutePath());
        server.getConsoleSender().sendMessage(ChatColor.RED+"+++END OF REPORT+++");

    }

    private void sendIOExceptionReport(File file, Exception e)
    {
        server.getConsoleSender().sendMessage(ChatColor.RED+"---ERROR REPORT---");
        server.getConsoleSender().sendMessage(ChatColor.RED+"BW - ERROR TYPE: IOException");
        server.getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR] - Ran into "+e.toString());
        server.getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR] - File Involved: "+file.getName());
        server.getConsoleSender().sendMessage(ChatColor.RED+"BW [ERROR] - DEBUG - Path: "+file.getAbsolutePath());
        server.getConsoleSender().sendMessage(ChatColor.RED+"---END OF REPORT---");

    }

}
