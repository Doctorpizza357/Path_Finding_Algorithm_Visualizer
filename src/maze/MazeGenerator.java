package maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Provides methods to generate different types of mazes.
 */
public class MazeGenerator {
    private final int gridSize;
    private final Random random = new Random();

    /**
     * Creates a new maze generator for a specific grid size.
     * 
     * @param gridSize The size of the grid (gridSize x gridSize)
     */
    public MazeGenerator(int gridSize) {
        this.gridSize = gridSize;
    }

    /**
     * Generates a random maze using density-based approach.
     * 
     * @param mazeDensity The density of barriers in the maze
     * @return A MazeData object containing start, end, and barrier positions
     */
    public MazeData generateRandomMaze(int mazeDensity) {
        MazeData mazeData = new MazeData();
        
        // Set random start point
        mazeData.setStart(new Point(random.nextInt(gridSize), random.nextInt(gridSize)));
        
        // Set random end point (different from start)
        do {
            mazeData.setEnd(new Point(random.nextInt(gridSize), random.nextInt(gridSize)));
        } while (mazeData.getStart().equals(mazeData.getEnd()));
        
        // Add random barriers
        int numBarriers = random.nextInt(mazeDensity);
        for (int i = 0; i < numBarriers; i++) {
            int barrierX;
            int barrierY;
            do {
                barrierX = random.nextInt(gridSize);
                barrierY = random.nextInt(gridSize);
            } while (mazeData.getStart().equals(new Point(barrierX, barrierY)) || 
                     mazeData.getEnd().equals(new Point(barrierX, barrierY)) ||
                     mazeData.getBarriers().contains(new Point(barrierX, barrierY)));
            
            mazeData.getBarriers().add(new Point(barrierX, barrierY));
        }
        
        return mazeData;
    }

    /**
     * Generates a maze using Prim's algorithm.
     * 
     * @return A MazeData object containing start, end, and barrier positions
     */
    public MazeData generatePrimsMaze() {
        MazeData mazeData = new MazeData();
        boolean[][] mazeGrid = new boolean[gridSize][gridSize];
        
        // Initialize grid with all walls
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                mazeGrid[i][j] = true;
            }
        }
        
        // Set random start and end points
        mazeData.setStart(new Point(random.nextInt(gridSize), random.nextInt(gridSize)));
        mazeData.setEnd(new Point(random.nextInt(gridSize), random.nextInt(gridSize)));
        
        // Apply Prim's algorithm
        mazeGrid[mazeData.getStart().x][mazeData.getStart().y] = false;
        
        List<Point> walls = new ArrayList<>();
        addNeighboringWalls(mazeData.getStart().x, mazeData.getStart().y, walls);
        
        while (!walls.isEmpty()) {
            int randomWallIndex = random.nextInt(walls.size());
            Point wall = walls.get(randomWallIndex);
            
            int x = wall.x;
            int y = wall.y;
            int[] dx = {0, 0, 1, -1};
            int[] dy = {1, -1, 0, 0};
            int openNeighborCount = 0;
            
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize && !mazeGrid[nx][ny]) {
                    openNeighborCount++;
                }
            }
            
            if (openNeighborCount == 1) {
                mazeGrid[x][y] = false;
                addNeighboringWalls(x, y, walls);
            }
            
            walls.remove(randomWallIndex);
        }
        
        // Convert grid to barriers
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Point point = new Point(i, j);
                if (mazeGrid[i][j] && !point.equals(mazeData.getStart()) && !point.equals(mazeData.getEnd())) {
                    mazeData.getBarriers().add(point);
                }
            }
        }
        
        return mazeData;
    }
    
    /**
     * Adds neighboring walls to the walls list.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param walls The list of walls to add to
     */
    private void addNeighboringWalls(int x, int y, List<Point> walls) {
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize) {
                walls.add(new Point(nx, ny));
            }
        }
    }
    
    /**
     * Data class to hold maze information.
     */
    public static class MazeData {
        private Point start;
        private Point end;
        private final List<Point> barriers = new ArrayList<>();
        
        public Point getStart() {
            return start;
        }
        
        public void setStart(Point start) {
            this.start = start;
        }
        
        public Point getEnd() {
            return end;
        }
        
        public void setEnd(Point end) {
            this.end = end;
        }
        
        public List<Point> getBarriers() {
            return barriers;
        }
    }
}
