package me.camm.productions.bedwars.Files.FileStreams;

import me.camm.productions.bedwars.Arena.GameRunning.Arena;
import me.camm.productions.bedwars.Arena.Teams.BattleTeam;
import me.camm.productions.bedwars.Arena.Teams.TeamColor;
import me.camm.productions.bedwars.Files.FileKeywords.DataSeparatorKeys;
import me.camm.productions.bedwars.Files.FileKeywords.TeamFileKeywords;
import me.camm.productions.bedwars.Generators.Forge;
import me.camm.productions.bedwars.Util.Helpers.StringHelper;
import me.camm.productions.bedwars.Util.Locations.Boundaries.GameBoundary;
import me.camm.productions.bedwars.Util.Locations.Coordinate;
import me.camm.productions.bedwars.Validation.BedWarsException;
import me.camm.productions.bedwars.Validation.ParameterException;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static me.camm.productions.bedwars.Files.FileKeywords.FilePaths.TEAMS;

public class TeamFileReader extends StringHelper {
    private Forge currentForge;
    private long currentTime;
    private Coordinate currentTeamSpawn;
    private GameBoundary currentTeamBed;
    private Coordinate currentTeamChest;
    private GameBoundary currentTeamBox;
    private Coordinate currentTeamQuick;
    private Coordinate currentTeamGroup;
    private GameBoundary aura;

    private final Plugin plugin;
    //  private String path;
    private TeamColor color;
    private final Arena arena;


    public TeamFileReader(Plugin plugin, Arena arena) {
        super(plugin);
        this.plugin = plugin;
        this.arena = arena;
        this.color = null;
    }

    public ArrayList<BattleTeam> read() throws BedWarsException {
        ArrayList<BattleTeam> teams = new ArrayList<>();
        try {

            BufferedReader reader = new BufferedReader(new FileReader(getTeamPath()));
            ArrayList<String> values = new ArrayList<>();
            String currentLine = reader.readLine();
            while (currentLine != null) {
                currentLine = checkForComments(currentLine);
                if (currentLine != null)
                    values.add(currentLine);
                currentLine = reader.readLine();
            }
            reader.close();

            TeamColor[] colors = TeamColor.values();


            int index = 0;

            int line = 0;

            for (String string : values) {
                line++;
                TeamFileKeywords key = getKeyword(string);
                if (key == null)
                    continue;

                if (index != key.getIndex())
                    continue;

                switch (key) {
                    case TEAM_GENERATE:
                        String color = getInfoSection(string);
                        if (color == null)
                            throw new ParameterException(TEAMS.getValue(), line, " A valid color (see instructions.txt)", string);


                        for (TeamColor currentColor : colors) {
                            if (currentColor.getName().equalsIgnoreCase(color)) {
                                this.color = currentColor;
                                index = key.getIndex() + 1;
                                break;
                            }
                        }

                        break;

                    case FORGE_TIME: {
                        Integer[] time = doubleToIntArray(getNumbers(string));
                        if (time.length != 1)
                            throw new ParameterException(TEAMS.getValue(), line, "One time parameter, not many or zero", string);


                        if (time[0] <= 0)
                            throw new ParameterException(TEAMS.getValue(), line, "Time greater or equal to 1", "" + time[0]);

                        currentTime = time[0];
                        index = key.getIndex() + 1;

                    }
                    break;

                    case FORGE_SPAWN: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for forge spawn", "" + string);

                        if (numbers.length != 3) {
                            throw new ParameterException(TEAMS.getValue(), line, "3 parameters for forge spawn", "" + string);
                        }

                        Coordinate currentForgeCoordinate = new Coordinate(numbers);

                        if (currentTime > 0 && this.color != null) {
                            currentForge = new Forge(currentForgeCoordinate.getX(), currentForgeCoordinate.getY(), currentForgeCoordinate.getZ(), arena.getWorld(), this.color, currentTime, plugin);
                            index = key.getIndex() + 1;
                            continue;
                        }
                        index = 0;

                    }
                    break;

                    case SPAWN: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for team spawn", string);

                        if (numbers.length != 3 && numbers.length != 4)
                            throw new ParameterException(TEAMS.getValue(), line, "team spawn area to have 3 or 4 parameters", "" + string);
                        currentTeamSpawn = new Coordinate(numbers);
                        index = key.getIndex() + 1;
                    }
                    break;

                    case BED: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for bed", string);

                        if (numbers.length != 6)
                            throw new ParameterException(TEAMS.getValue(), line, "bed area to have 6 parameters", "" + string);
                        currentTeamBed = new GameBoundary(doubleToIntArray(numbers));
                        index = key.getIndex() + 1;
                    }
                    break;

                    case CHEST: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for team chest", string);

                        if (numbers.length != 3)
                            throw new ParameterException(TEAMS.getValue(), line, "chest area to have 3 parameters", "" + string);
                        currentTeamChest = new Coordinate(numbers);
                        index = key.getIndex() + 1;
                    }
                    break;

                    case QUICK_BUY: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for quick buy", string);

                        if (numbers.length != 3 && numbers.length != 4)
                            throw new ParameterException(TEAMS.getValue(), line, "quick buy spawn to have 3 or 4 parameters", "" + string);
                        currentTeamQuick = new Coordinate(numbers);
                        index = key.getIndex() + 1;
                    }
                    break;

                    case TEAM_BUY: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for team buy", "" + string);

                        if (numbers.length != 3 && numbers.length != 4)
                            throw new ParameterException(TEAMS.getValue(), line, "team buy spawn to have 3 or 4 parameters", "" + string);
                        currentTeamGroup = new Coordinate(numbers);
                        index = key.getIndex() + 1;
                    }
                    break;

                    case REGISTERED_BOUNDS: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for registered team area", "" + string);

                        if (numbers.length != 6)
                            throw new ParameterException(TEAMS.getValue(), line, "team registered area to have 6 parameters", "" + string);
                        currentTeamBox = new GameBoundary(doubleToIntArray(numbers));
                        index = key.getIndex() + 1;
                    }
                    break;

                    case AURA: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for heal pool area", "" + string);
                        if (numbers.length != 6)
                            throw new ParameterException(TEAMS.getValue(), line, "heal pool area to have 6 parameters", "" + string);


                        aura = new GameBoundary(doubleToIntArray(numbers));
                        index = key.getIndex() + 1;
                    }
                    break;

                    case TRIGGER_AREA: {
                        double[] numbers = getNumbers(string);
                        if (numbers == null)
                            throw new ParameterException(TEAMS.getValue(), line, "defined values for trap area", string);

                        if (numbers.length != 6)
                            throw new ParameterException(TEAMS.getValue(), line, "trap area to have 6 parameters", "" + string);

                        index = 0;
                        GameBoundary trap = new GameBoundary(doubleToIntArray(numbers));
                        if (currentForge != null && this.color != null)
                            teams.add(new BattleTeam(arena, this.color, currentForge, currentTeamSpawn, currentTeamBed, currentTeamChest, currentTeamQuick, currentTeamGroup, currentTeamBox, aura, trap));
                    }
                    break;

                }
            }
        } catch (IOException ignored) {

        }
        return teams;
    }

    private TeamFileKeywords getKeyword(String original) {
        int index = original.indexOf(DataSeparatorKeys.DECLARATION.getKey());
        return index == -1 ? null : toTeamKey((original.substring(0, index)).trim());
    }


    private TeamFileKeywords toTeamKey(String dissected) {
        TeamFileKeywords[] words = TeamFileKeywords.values();
        for (TeamFileKeywords word : words) {
            if (word.getKey().equalsIgnoreCase(dissected))
                return word;
        }
        return null;
    }
}


