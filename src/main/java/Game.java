import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Creates an interface for a single game.
 *
 * @author Sam Skulsky
 * @version 06.13.2021
 */
public class Game {

    public int id = 0, homeScore = 0, awayScore = 0, period = 0;
    public Team home = null, away = null;
    public GameStatus status;
    public String timeInPeriod = "";

    /**
     * Constructs an object of the Game class with a given ID.
     * @param id the ID of the Game
     */
    public Game(int id) throws IOException {
        this.id = id;
        updateInfo(new URL("https://www.balldontlie.io/api/v1/games/" + id));
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
            String jsonData = "";
            Scanner apiStream = new Scanner(apiUrl.openStream());
            while (apiStream.hasNextLine())
                jsonData += apiStream.nextLine();
            JSONObject gameObject = new JSONObject(jsonData);
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

    /**
     * Gets the ID of the Game.
     * @return the ID of the Game
     */
    public int getID() {
        return id;
    }

    /**
     * Gets the Home Score of the Game.
     * @return the Home Score of the Game
     */
    public int getHomeScore() {
        return homeScore;
    }

    /**
     * Gets the Away Score of the Game.
     * @return the Away Score of the Game
     */
    public int getAwayScore() {
        return awayScore;
    }

    /**
     * Gets the Period of the Game.
     * @return the Period of the Game, or 0 if not in play
     */
    public int getPeriod() {
        return period;
    }

    /**
     * Gets the Home Team.
     * @return the Home Team of the Game
     */
    public Team getHome() {
        return home;
    }

    /**
     * Gets the Away Team.
     * @return the Away Team of the Game
     */
    public Team getAway() {
        return away;
    }

    /**
     * Gets the Game Status.
     * @return the Status of the Game
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Gets the Time in Period.
     * @return the Time in Period as ("0:00"), "" if not in play
     */
    public String getTimeInPeriod() {
        return timeInPeriod;
    }

}
