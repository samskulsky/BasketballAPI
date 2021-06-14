import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Creates an interface for the NBA.
 *
 * @author Sam Skulsky
 * @version 06.14.2021
 */
public class League {

    public List<Game> getGamesOn(LocalDate localDate) throws IOException {
        List<Game> games = new ArrayList<>();
        URL apiUrl = new URL("https://www.balldontlie.io/api/v1/games?dates=" + localDate);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() != 200) {
            System.err.println("Error while updating game info: " + connection.getResponseCode());
        } else {
            StringBuilder jsonData = new StringBuilder();
            Scanner apiStream = new Scanner(apiUrl.openStream());
            while (apiStream.hasNextLine())
                jsonData.append(apiStream.nextLine());
            JSONObject jsonObject = new JSONObject(jsonData.toString());
            JSONArray data = jsonObject.getJSONArray("data");
            JSONObject meta = jsonObject.getJSONObject("meta");
            for (int j = 1; j <= meta.getInt("total_pages"); j++) {
                apiUrl = new URL("https://www.balldontlie.io/api/v1/games?per_page=1000&dates[]=" + localDate + "&page=" + j);
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
                        JSONObject currentTeam = data.getJSONObject(i).getJSONObject("home_team");
                        int teamID = currentTeam.getInt("id");
                        String abv = currentTeam.getString("abbreviation");
                        String city = currentTeam.getString("city");
                        String conference = currentTeam.getString("conference");
                        String division = currentTeam.getString("division");
                        String teamFullName = currentTeam.getString("full_name");
                        String teamName = currentTeam.getString("name");
                        Team homeTeam = new Team(teamID, teamName, teamFullName, abv, city, conference, division);
                        currentTeam = data.getJSONObject(i).getJSONObject("visitor_team");
                        teamID = currentTeam.getInt("id");
                        abv = currentTeam.getString("abbreviation");
                        city = currentTeam.getString("city");
                        conference = currentTeam.getString("conference");
                        division = currentTeam.getString("division");
                        teamFullName = currentTeam.getString("full_name");
                        teamName = currentTeam.getString("name");
                        Team awayTeam = new Team(teamID, teamName, teamFullName, abv, city, conference, division);
                        int gameID = data.getJSONObject(i).getInt("id");
                        int homeScore = data.getJSONObject(i).getInt("home_team_score");
                        int awayScore = data.getJSONObject(i).getInt("visitor_team_score");
                        int period = data.getJSONObject(i).getInt("period");
                        String time = data.getJSONObject(i).getString("time");
                        Game g = new Game(gameID, homeScore, awayScore, period, homeTeam, awayTeam, getStatus(data.getJSONObject(i).getString("status")), time);
                        games.add(g);
                    }
                }
            }
        }
        return games;
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
