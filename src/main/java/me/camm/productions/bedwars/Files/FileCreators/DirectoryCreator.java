package me.camm.productions.bedwars.Files.FileCreators;

import me.camm.productions.bedwars.Files.FileKeywords.ContributorList;
import me.camm.productions.bedwars.Files.FileKeywords.Instructions;
import me.camm.productions.bedwars.Files.FileStreams.GameFileWriter;
import me.camm.productions.bedwars.Util.Helpers.ChatSender;
import me.camm.productions.bedwars.Util.Helpers.StringHelper;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author CAMM
 * Class for creating folders in the server files for configuration
 */
public class DirectoryCreator extends StringHelper
{
    private Server server;
    private Plugin plugin;

    private File mainFile;
    private File teamFile;
    private File worldFile;
    private File playerFolder;
    private File credits;
    private File instructionFile;

    private final ChatSender sender;


    public DirectoryCreator(Plugin plugin)
    {
        super(plugin);
        sender = ChatSender.getInstance();

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
             sender.sendMessage("COULD NOT INITIALIZE MAIN FOLDER. (Does it already exist?)");


            if (!playerFolder.mkdir())
                sender.sendMessage("COULD NOT INITIALIZE PLAYER FOLDER. (Does it already exist?)");

        }
        catch (Exception e)
        {
            sender.sendConsoleMessage("COULD NOT INITIALIZE FOLDERS. [We can't set up the game like this!]",Level.SEVERE);
        }
    }


    //Creates the directory folders the plugin uses
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


            if (!mainFile.exists())
            {
                mainFile.createNewFile();
                sender.sendMessage("Creating new Main Directory.");
            }
            else
                sender.sendMessage("Main Directory Exists.");


            GameFileWriter creditWriter = new GameFileWriter(credits.getAbsolutePath(),plugin);
            GameFileWriter instructWriter = new GameFileWriter(instructionFile.getAbsolutePath(),plugin);

            if (mainFile.exists()&&
                    teamFile.exists()&&
                    worldFile.exists()&&
                    playerFolder.exists()&&
                    credits.exists()&&
                    instructionFile.exists())
            {
                sender.sendMessage("All files exist. Please make sure they are configured.");

                creditWriter.clear();
                creditWriter.writeSection(contributors);

                instructWriter.clear();
                instructWriter.writeSection(instructions);
                return true;
            }
            else
            {
                sender.sendPlayerMessage("AT-LEAST 1 FILE IN THE CONFIGURATION IS NOT CONFIGURED.", ChatSender.GameState.WARN);
                createFiles(mainFile);
                createFiles(teamFile);
                createFiles(worldFile);
                createFiles(playerFolder);
                createFiles(credits);
                createFiles(instructionFile);

                creditWriter.clear();
                creditWriter.writeSection(contributors);

                instructWriter.clear();
                instructWriter.writeSection(instructions);


                return false;
            }
        }
        catch (Exception e)
        {
            sender.sendPlayerMessage("COULD NOT VERIFY FILE INTEGRITY.", ChatSender.GameState.ERROR);

            return false;
        }

    }

    //creates a file.
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
                sender.sendPlayerMessage("Could not create file "+file.getName()+" (file not found)", ChatSender.GameState.ERROR);

                return false;
            }
            catch (IOException e)
            {
                sender.sendPlayerMessage("Could not create file "+file.getName()+" (IOException)", ChatSender.GameState.ERROR);
                return false;
            }
        }
        return true;  //file is init
    }

}
