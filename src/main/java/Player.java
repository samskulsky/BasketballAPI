import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Creates an interface for a basketball player.
 *
 * @author Sam Skulsky
 * @version 06.13.2021
 */
public class Player {

    public int id;
    public String fullName = "", firstName = "", lastName = "", position = "";
    public Team team;

    /**
     * Constructs an object of the Player class with a given ID.
     * @param id the ID of the Player
     */
    public Player(int id) throws IOException {
        this.id = id;
        updatePlayerInfo(new URL("https://www.balldontlie.io/api/v1/players/" + id));
    }

    /**
     * Manually constructs an object of the Player class.
     * @param id the ID of the Player
     * @param firstName the first name of the Player
     * @param lastName the last name of the Player
     * @param position the position of the Player
     * @param team the team of the Player
     */
    public Player(int id, String firstName, String lastName, String position, Team team) {
        this.id = id;
        this.fullName = firstName + " " + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.team = team;
    }

    /**
     * Updates the Player's info.
     */
    private void updatePlayerInfo(URL apiUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        if (connection.getResponseCode() != 200) {
            System.err.println("Error while updating player info: " + connection.getResponseCode());
        } else {
            StringBuilder jsonData = new StringBuilder();
            Scanner apiStream = new Scanner(apiUrl.openStream());
            while (apiStream.hasNextLine())
                jsonData.append(apiStream.nextLine());
            JSONObject playerObject = new JSONObject(jsonData.toString());
            firstName = playerObject.getString("first_name");
            lastName = playerObject.getString("last_name");
            fullName = firstName + " " + lastName;
            team = new Team(playerObject.getJSONObject("team").getInt("id"));
            position = playerObject.getString("position");
        }
    }

}
