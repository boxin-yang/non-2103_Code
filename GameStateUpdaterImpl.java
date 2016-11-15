import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of GameStateUpdater.
 */
public class GameStateUpdaterImpl implements GameStateUpdater {
    // n is the grid size.
    // k is the numebr of treasures.
    private Integer n, k;

    // 0 based grid used to mark players and trackers
    // 0 is empty, 1 is player, 2 is treasure
    private Integer[][] grid;

    private GameStateImpl gameState;

    // a random number generater to find a random unoccupied position on the map
    private Random randomNumGenerator;

    // points increment when a player moves to a point with treasure
    private final static Integer POINTS_PER_TREASURE = 1;

    public GameStateUpdaterImpl(GameStateImpl gameState) {
        this.n = gameState.getGridDimension();
        this.k = gameState.getNumberOfTreasures();
        assert (k < n * n) : "Cannot initialize a map with k = " + k + " and n = " + n;
        this.gameState = gameState;
        this.grid = new Integer[n][n];
        this.randomNumGenerator = new Random();
        initializeGrid();
    }

    /**
     * Add a new player to an random initial position.
     *
     * @return false is the current map is full of players and treasures
     */
    @Override
    public synchronized Boolean addPlayer(String id) {
        if (gameState.getPlayers().size() + k >= n * n){
            return false;
        }

        Point newCoordinates = findFreeCoordinate();
        PlayerStateImpl newPlayer = new PlayerStateImpl(id, newCoordinates);

        gameState.getPlayers().put(id, newPlayer);
        grid[newCoordinates.x][newCoordinates.y] = 1;
        return true;
    }

    @Override
    public synchronized Boolean removePlayer(String id) {
        PlayerState player = gameState.getPlayers().get(id);
        if (player == null) {
            System.out.println("removePlayer failed for id " + id);
            return false;
        }

        Point position = player.getCoordinates();
        grid[position.x][position.y] = 0;
        gameState.getPlayers().remove(id);
        return true;
    }

    /**
     * @param dx change in x coordinate in grid
     * @param dy change in y coordinate in grid
     *
     * @return false if the move is not permitted;
     */
    @Override
    public synchronized Boolean movePlayer(String id, int dx, int dy) {
        PlayerState player = gameState.getPlayers().get(id);
        if (player == null) {
            System.out.println("movePlayer request from " + id + "is refused, cannot find user");
            return false;
        }

        int newX = player.getCoordinates().x + dx;
        int newY = player.getCoordinates().y + dy;

        // wants to move out of the border
        if((newX < 0) || (newX >= n) || (newY < 0) || (newY >= n)){
            System.out.println("movePlayer request from " + id + " with newX: " + newX + " newY: " + newY + " is refused, out of boundary move");
            return false;
        }

        // wants to move to a point occupied by another player
        if(grid[newX][newY] == 1) {
            System.out.println("movePlayer request from " + id + " with dx: " + dx + " dy: " + dy + " is refused");
            return false;
        }

        // wants to move to a point occupied by a treasure
        if(grid[newX][newY] == 2) {
            System.out.println("movePlayer request from " + id + " with newX: " + newX + " newY: " + newY);

            // position is occupied by the player
            grid[newX][newY] = 1;

            // the treasure is consumed
            gameState.getTreasures().remove(new Point(newX, newY));

            // Free up old position before generating the new treasure.
            grid[player.getCoordinates().x][player.getCoordinates().y] = 0;

            // player state is updated
            player.setScore(player.getScore() + POINTS_PER_TREASURE);
            player.setCoordinates(new Point(newX, newY));

            // generate new treasure
            Point newTreasurePostion = findFreeCoordinate();
            assert (newTreasurePostion != null)
            : "findFreeCoordinate() returns null in MovePlayer with grid " + Arrays.toString(grid);
            grid[newTreasurePostion.x][newTreasurePostion.y] = 2;
            gameState.getTreasures().add(newTreasurePostion);
        }

        // wants to move to a point unoccupied
        if(grid[newX][newY] == 0) {
            // old position is empty
            grid[player.getCoordinates().x][player.getCoordinates().y] = 0;

            // position is occupied by the player
            grid[newX][newY] = 1;
            player.setCoordinates(new Point(newX, newY));
        }

        System.out.printf(toString());
        return true;
    }

    @Override
    public synchronized void updateServers(Player primaryServer, Player secondaryServer) {
        gameState.updateServers(primaryServer, secondaryServer);
    }

    @Override
    public void initializeGameState() {
        for (int i = 0; i < k; i++) {
            Point newTreasureLocation = findFreeCoordinate();
            assert newTreasureLocation != null;
            gameState.getTreasures().add(newTreasureLocation);
            grid[newTreasureLocation.x][newTreasureLocation.y] = 2;
        }
    }

    // fill the initial grid with k treasures
    private void initializeGrid() {
        // 0 for empty position
        for (int i = 0 ; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = 0;
            }
        }

        // 1 for players
        HashMap<String, PlayerStateImpl> players = gameState.getPlayers();
        for (Map.Entry<String, PlayerStateImpl> entry : players.entrySet()) {
            Point coordinates = entry.getValue().getCoordinates();
            grid[coordinates.x][coordinates.y] = 1;
        }

        // 2 for treasures
        for (Point coordinates : gameState.getTreasures()){
            grid[coordinates.x][coordinates.y] = 2;
        }

        assert gameState.getTreasures().size() == k;
    }

    /**
     * find a point that is not occupied by player or treasure.
     *
     * @return null if all the positions are occupied
     */
    private Point findFreeCoordinate() {
        int ranX = randomNumGenerator.nextInt(n);
        int ranY = randomNumGenerator.nextInt(n);
        int iterationLimit = n * n;

        while(iterationLimit-- > 0) {
            if(grid[ranX][ranY] == 0) {
                return new Point(ranX, ranY);
            }

            if(ranX < n - 1) {
                ranX++;
            } else {
                ranX = 0;
                ranY = (ranY + 1) % n;
            }
        }

        return null;
    }

    public String toString() {
        String result = "updated treasureList:\n";

        for(Point point : gameState.getTreasures()){
            result += "[" + point.x + "," + point.y + "] ";
        }

        result += "\nupdated playerList:\n";

        for (Map.Entry<String, PlayerStateImpl> entry : gameState.getPlayers().entrySet()) {
            Point coordinates = entry.getValue().getCoordinates();
            result += "player: " + entry.getKey() + " is at [" + coordinates.x + "," + coordinates.y + "] and his point is : " + entry.getValue().getScore() + "\n";
        }
        return result;
    }
}
