import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * The state of the game. Will be transferred over the network, so extra logics will be put in the updater.
 */
public interface GameState extends Serializable {

    /**
     * Gets the grid dimension of the game (N).
     *
     * @return The grid dimension of the game.
     */
    int getGridDimension();

    /**
     * Gets the number of treasures that the game should have (K).
     *
     * @return The number of treasures.
     */
    int getNumberOfTreasures();

    /**
     * Get all the players and their states in the game.
     *
     * @return A hash map that maps player IDs to their states.
     */
    HashMap<String, PlayerStateImpl> getPlayers();

    /**
     * Gets the remote reference of the primary server.
     *
     * @return The remote reference of the primary server.
     */
    Player getPrimaryServer();

    /**
     * Gets the remote reference of the secondary server.
     *
     * @return The remote reference of the secondary server.
     */
    Player getSecondaryServer();

    /**
     * Gets the locations of all the treasures.
     *
     * @return A list of coordinates.
     */
    List<Point> getTreasures();

    /**
     * Gets the updater of the current game state.
     *
     * @return The updater.
     */
    GameStateUpdater getUpdater();

    /**
     * Updates the servers in the game state.
     *
     * @param primaryServer The primary server.
     * @param secondaryServer The secondary server.
     */
    void updateServers(Player primaryServer, Player secondaryServer);
}
