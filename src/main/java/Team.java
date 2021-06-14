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

    public int id = 0;
    public String name = "", fullName = "", abbreviation = "", city = "", conference = "", division = "";

    /**
     * Constructs an object of the Team class with a given ID.
     * @param id the ID of the Team
     */
    public Team(int id) throws IOException {
        this.id = id;
        updateInfo(new URL("https://www.balldontlie.io/api/v1/teams/" + id));
    }

    /**
     * Updates the Team's info.
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
            JSONObject teamObject = new JSONObject(jsonData);
            name = teamObject.getString("name");
            fullName = teamObject.getString("full_name");
            abbreviation = teamObject.getString("abbreviation");
            city = teamObject.getString("city");
            conference = teamObject.getString("conference");
            division = teamObject.getString("division");
        }
    }

    /**
     * Gets the ID of the Team.
     * @return the ID of the Team
     */
    public int getID() {
        return id;
    }

    /**
     * Gets the short name of the Team.
     * @return the short name of the Team
     */
    public String getShortName() {
        return name;
    }

    /**
     * Gets the full name of the Team.
     * @return the full name of the Team
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets the abbreviation of the Team.
     * @return the abbreviation of the Team
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Gets the city of the Team.
     * @return the city of the Team
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the conference of the Team.
     * @return the conference of the Team
     */
    public String getConference() {
        return conference;
    }

    /**
     * Gets the division of the Team.
     * @return the division of the Team
     */
    public String getDivision() {
        return division;
    }

}
