import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Creates an interface for a single game.
 *
 * @author Sam Skulsky
 * @version 06.13.2021
 */
public class Game {

    public int id, homeScore = 0, awayScore = 0, period = 0;
    public Team home = null, away = null;
    public GameStatus status;
    public String timeInPeriod = "";
    public List<Player> players = new ArrayList<>();
    public HashMap<Player, PlayerStats> playerStats = new LinkedHashMap<>();

    /**
     * Constructs an object of the Game class with a given ID.
     * @param id the ID of the Game
     */
    public Game(int id) throws IOException {
        this.id = id;
        updateInfo(new URL("https://www.balldontlie.io/api/v1/games/" + id));
        updatePlayerInfo(new URL("https://www.balldontlie.io/api/v1/stats?game_ids=" + id));
    }

    /**
     * Updates the Game's info.
     */
    private void updateInfo(URL apiUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() != 200) {
            System.err.println("Error while updating team info: " + connection.getResponseCode());
        } else {
            StringBuilder jsonData = new StringBuilder();
            Scanner apiStream = new Scanner(apiUrl.openStream());
            while (apiStream.hasNextLine())
                jsonData.append(apiStream.nextLine());
            JSONObject gameObject = new JSONObject(jsonData.toString());
            int homeTeamID = gameObject.getJSONObject("home_team").getInt("id");
            int awayTeamID = gameObject.getJSONObject("visitor_team").getInt("id");
            home = new Team(homeTeamID);
            away = new Team(awayTeamID);
            period = gameObject.getInt("period");
            homeScore = gameObject.getInt("home_team_score");
            awayScore = gameObject.getInt("visitor_team_score");
            String status = gameObject.getString("status");
            this.status = getStatus(status);
            timeInPeriod = gameObject.getString("time");
        }
    }

    /**
     * Updates the Players info.
     */
    private void updatePlayerInfo(URL apiUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() != 200) {
            System.err.println("Error while updating team info: " + connection.getResponseCode());
        } else {
            StringBuilder jsonData = new StringBuilder();
            Scanner apiStream = new Scanner(apiUrl.openStream());
            while (apiStream.hasNextLine())
                jsonData.append(apiStream.nextLine());
            JSONObject jsonObject = new JSONObject(jsonData.toString());
            JSONArray data = jsonObject.getJSONArray("data");
            JSONObject meta = jsonObject.getJSONObject("meta");
            for (int j = 1; j <= meta.getInt("total_pages"); j++) {
                apiUrl = new URL("https://www.balldontlie.io/api/v1/stats?per_page=1000&game_ids[]=" + id + "&page=" + j);
                connection = (HttpURLConnection)apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() != 200) {
                    System.err.println("Error while updating team info: " + connection.getResponseCode());
                } else {
                    jsonData = new StringBuilder();
                    apiStream = new Scanner(apiUrl.openStream());
                    while (apiStream.hasNextLine())
                        jsonData.append(apiStream.nextLine());
                    jsonObject = new JSONObject(jsonData.toString());
                    data = jsonObject.getJSONArray("data");
                    meta = jsonObject.getJSONObject("meta");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject currentPlayer = data.getJSONObject(i).getJSONObject("player");
                        int playerID = currentPlayer.getInt("id");
                        String playerFirstName = currentPlayer.getString("first_name");
                        String playerLastName = currentPlayer.getString("last_name");
                        String playerPosition = currentPlayer.getString("position");
                        JSONObject currentPlayerTeam = data.getJSONObject(i).getJSONObject("team");
                        int teamID = currentPlayerTeam.getInt("id");
                        String abv = currentPlayerTeam.getString("abbreviation");
                        String city = currentPlayerTeam.getString("city");
                        String conference = currentPlayerTeam.getString("conference");
                        String division = currentPlayerTeam.getString("division");
                        String teamFullName = currentPlayerTeam.getString("full_name");
                        String teamName = currentPlayerTeam.getString("name");
                        Team playerTeam = new Team(teamID, teamName, teamFullName, abv, city, conference, division);
                        Player p = new Player(playerID, playerFirstName, playerLastName, playerPosition, playerTeam);
                        players.add(p);
                        playerStats.put(p, new PlayerStats(p, data.getJSONObject(i)));
                    }
                }
            }
        }
    }

    /**
     * Gets the current status as a GameStatus.
     * @param status the Status as a String
     * @return the Status as a GameStatus
     */
    private GameStatus getStatus(String status) {
        if (status.contains("M ET"))
            return GameStatus.NOT_STARTED;
        return switch (status) {
            case "1st Qtr" -> GameStatus.QUARTER1;
            case "2nd Qtr" -> GameStatus.QUARTER2;
            case "3rd Qtr" -> GameStatus.QUARTER3;
            case "4th Qtr" -> GameStatus.QUARTER4;
            case "Halftime" -> GameStatus.HALFTIME;
            case "Final" -> GameStatus.FINAL;
            default -> GameStatus.OTHER;
        };
    }

}
