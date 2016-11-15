import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementation of GameState.
 */
public class GameStateImpl implements GameState {
    // n is the grid dimension.
    // k is the numebr of treasures.
    private Integer n, k;

    // find the player by id
    private HashMap<String, PlayerStateImpl> playersLookup;

    // Coordinates of all current treasures;
    private ArrayList<Point> treasuresState;

    // Stub of primary and secondary servers
    private Player primaryServer = null;
    private Player secondaryServer = null;

    public GameStateImpl(int gridDimension, int numberOfTreasures) {
        this.n = gridDimension;
        this.k = numberOfTreasures;
        this.playersLookup = new HashMap<>();
        this.treasuresState = new ArrayList<>();
        new GameStateUpdaterImpl(this).initializeGameState();
    }

    @Override
    public int getGridDimension() {
        return n;
    }

    @Override
    public int getNumberOfTreasures() {
        return k;
    }

    @Override
    public HashMap<String, PlayerStateImpl> getPlayers() {
        return playersLookup;
    }

    @Override
    public Player getPrimaryServer() {
        return primaryServer;
    }

    @Override
    public Player getSecondaryServer() {
        return secondaryServer;
    }

    @Override
    public java.util.List<Point> getTreasures() {
        return treasuresState;
    }

    @Override
    public GameStateUpdater getUpdater() {
        return new GameStateUpdaterImpl(this);
    }

    @Override
    public void updateServers(Player primaryServer, Player secondaryServer) {
        this.primaryServer = primaryServer;
        this.secondaryServer = secondaryServer;
    }
}
