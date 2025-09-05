package algorithm;

import model.Node;

import java.awt.Point;
import java.util.*;

/**
 * Implements various pathfinding algorithms for grid-based navigation.
 * Currently supports A* algorithm.
 */
public class PathFinder {
    private final int gridSize;
    private final List<Point> barriers;

    /**
     * Creates a new pathfinder with the specified grid size and barriers.
     * 
     * @param gridSize The size of the grid (gridSize x gridSize)
     * @param barriers List of barrier positions in the grid
     */
    public PathFinder(int gridSize, List<Point> barriers) {
        this.gridSize = gridSize;
        this.barriers = barriers;
    }

    /**
     * Executes the A* pathfinding algorithm to find a path between two points.
     * 
     * @param start The starting point
     * @param end The ending point
     * @return A list containing two lists: exploration path and shortest path
     */
    public List<List<Point>> findPath(Point start, Point end) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        boolean[][] visited = new boolean[gridSize][gridSize];
        List<Point> explorationPath = new ArrayList<>();
        List<Point> fastestPath = new ArrayList<>();

        Node startNode = new Node(start.x, start.y);
        startNode.setG(0);
        startNode.setH(calculateHeuristic(startNode, end));
        startNode.setF(startNode.getG() + startNode.getH());
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            explorationPath.add(new Point(current.getRow(), current.getCol()));

            visited[current.getRow()][current.getCol()] = true;

            if (current.getRow() == end.x && current.getCol() == end.y) {
                fastestPath = reconstructPath(current);
                return Arrays.asList(explorationPath, fastestPath);
            }

            int[] dRow = {0, 1, 0, -1};
            int[] dCol = {1, 0, -1, 0};
            for (int i = 0; i < 4; i++) {
                int newRow = current.getRow() + dRow[i];
                int newCol = current.getCol() + dCol[i];

                if (isValid(newRow, newCol) && !visited[newRow][newCol] && !barriers.contains(new Point(newRow, newCol))) {
                    double tentativeG = current.getG() + 1;
                    Node neighbor = new Node(newRow, newCol);
                    neighbor.setG(tentativeG);
                    neighbor.setH(calculateHeuristic(neighbor, end));
                    neighbor.setF(neighbor.getG() + neighbor.getH());
                    neighbor.setParent(current);

                    openSet.add(neighbor);
                    visited[newRow][newCol] = true;
                }
            }
        }

        return Arrays.asList(explorationPath, fastestPath);
    }

    /**
     * Calculates the Euclidean distance heuristic between a node and the end point.
     * 
     * @param node The node to calculate from
     * @param end The end point
     * @return The Euclidean distance
     */
    private double calculateHeuristic(Node node, Point end) {
        return Math.sqrt(Math.pow(node.getRow() - end.x, 2) + Math.pow(node.getCol() - end.y, 2));
    }

    /**
     * Checks if a position is valid on the grid.
     * 
     * @param row The row to check
     * @param col The column to check
     * @return True if the position is valid and not a barrier
     */
    private boolean isValid(int row, int col) {
        return row >= 0 && row < gridSize && col >= 0 && col < gridSize && !barriers.contains(new Point(row, col));
    }

    /**
     * Reconstructs the path from the end node back to the start node.
     * 
     * @param current The end node
     * @return A list of points representing the path
     */
    private List<Point> reconstructPath(Node current) {
        List<Point> path = new ArrayList<>();
        while (current != null) {
            path.add(new Point(current.getRow(), current.getCol()));
            current = current.getParent();
        }
        return path;
    }

    /**
     * Calculates the optimality of the path as a percentage.
     * 
     * @param start The start point
     * @param end The end point
     * @param fastestPath The calculated path
     * @return Optimality percentage (0-100)
     */
    public int calculateOptimality(Point start, Point end, List<Point> fastestPath) {
        if (fastestPath.isEmpty()) return 0;

        // Compare actual path length to theoretical minimum (Manhattan distance)
        int actualLength = fastestPath.size() - 1; // Subtract start node
        int theoreticalMin = Math.abs(start.x - end.x) + Math.abs(start.y - end.y);

        return theoreticalMin > 0
                ? (int) ((double) theoreticalMin / actualLength * 100)
                : 100;
    }

    /**
     * Calculates the efficiency of the pathfinding as a percentage.
     * 
     * @param exploredNodes Number of nodes explored
     * @param totalNodes Total number of nodes in the grid
     * @return Efficiency percentage (0-100)
     */
    public int calculateEfficiency(int exploredNodes, int totalNodes) {
        double explorationRatio = (double) exploredNodes / totalNodes;
        return 100 - (int) (explorationRatio * 100);
    }
}
