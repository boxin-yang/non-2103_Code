/**
 * The updater for GameState. Should only be constructed by GameState and used by servers.
 */
public interface GameStateUpdater {
    /**
     * Initializes the gamestate. Only used by the game state constructor.
     */
    void initializeGameState();

    /**
     * Add a new player into the game board.
     *
     * @param playerId The ID of the new player.
     * @return true if action succeeds, false otherwise.
     */
    Boolean addPlayer(String playerId);

    /**
     * Move a player to a new location.
     *
     * @param playerId The ID of the player.
     * @param dx The distance to move in the horizontal direction.
     * @param dy The distance to move in the vertical direction.
     * @return true of action succeeds, false otherwise.
     */
    Boolean movePlayer(String playerId, int dx, int dy);

    /**
     * Remove a player from the game.
     *
     * @param playerId The ID of the player to remove.
     * @return true if succeed, false otherwise.
     */
    Boolean removePlayer(String playerId);

    /**
     * Updates the servers in the game state.
     *
     * @param primaryServer The primary server.
     * @param secondaryServer The secondary server.
     */
    void updateServers(Player primaryServer, Player secondaryServer);
}
