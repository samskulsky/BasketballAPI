import org.json.JSONObject;

/**
 * Creates an interface for a list of player stats in a single game.
 *
 * @author Sam Skulsky
 * @version 06.13.2021
 */
public class PlayerStats {

    public int assists, blocks, defensiveRebounds, attempts3PT, makes3PT, attemptsFG, makesFG, attemptsFT, makesFT, offensiveRebounds, fouls, points, totalRebounds, steals, turnovers;
    public double percent3PT, percentFG, percentFT;
    public String minutes;

    public PlayerStats(Player player, JSONObject currentPlayerStats) {
        assists = currentPlayerStats.getInt("ast");
        blocks = currentPlayerStats.getInt("blk");
        defensiveRebounds = currentPlayerStats.getInt("dreb");
        percent3PT = currentPlayerStats.getDouble("fg3_pct");
        attempts3PT = currentPlayerStats.getInt("fg3a");
        makes3PT = currentPlayerStats.getInt("fg3m");
        percentFG = currentPlayerStats.getDouble("fg_pct");
        attemptsFG = currentPlayerStats.getInt("fga");
        makesFG = currentPlayerStats.getInt("fgm");
        percentFT = currentPlayerStats.getDouble("ft_pct");
        attemptsFT = currentPlayerStats.getInt("fta");
        makesFT = currentPlayerStats.getInt("ftm");
        minutes = currentPlayerStats.getString("min");
        offensiveRebounds = currentPlayerStats.getInt("oreb");
        fouls = currentPlayerStats.getInt("pf");
        points = currentPlayerStats.getInt("pts");
        totalRebounds = currentPlayerStats.getInt("reb");
        steals = currentPlayerStats.getInt("stl");
        turnovers = currentPlayerStats.getInt("turnover");
    }

}
