import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Creates an interface for a basketball team.
 *
 * @author Sam Skulsky
 * @version 06.13.2021
 */
public class Team {

    public int id;
    public String name = "", fullName = "", abbreviation = "", city = "", conference = "", division = "";

    /**
     * Constructs an object of the Team class with a given ID.
     * @param id the ID of the Team
     */
    public Team(int id) throws IOException {
        this.id = id;
        updateTeamInfo(new URL("https://www.balldontlie.io/api/v1/teams/" + id));
    }

    /**
     * Manually constructs an object of the Team class.
     * @param id the ID of the Team
     * @param name the name of the Team
     * @param fullName the full name of the Team
     * @param abbreviation the abbreviation of the Team
     * @param city the city of the Team
     * @param conference the conference of the Team
     * @param division the division of the Team
     */
    public Team(int id, String name, String fullName, String abbreviation, String city, String conference, String division) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.abbreviation = abbreviation;
        this.city = city;
        this.conference = conference;
        this.division = division;
    }

    /**
     * Updates the team's info.
     */
    private void updateTeamInfo(URL apiUrl) throws IOException {
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
            JSONObject teamObject = new JSONObject(jsonData.toString());
            name = teamObject.getString("name");
            fullName = teamObject.getString("full_name");
            abbreviation = teamObject.getString("abbreviation");
            city = teamObject.getString("city");
            conference = teamObject.getString("conference");
            division = teamObject.getString("division");
        }
    }

}
