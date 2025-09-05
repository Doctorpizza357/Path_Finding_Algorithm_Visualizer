package model;

/**
 * Represents a node in the pathfinding grid with A* algorithm values.
 * Used for pathfinding calculations.
 */
public class Node implements Comparable<Node> {
    private int row;
    private int col;
    private double f; // Total cost (g + h)
    private double g; // Cost from start
    private double h; // Heuristic (estimated cost to end)
    private Node parent;

    /**
     * Creates a new node with the specified position.
     * 
     * @param row The row position in the grid
     * @param col The column position in the grid
     */
    public Node(int row, int col) {
        this.row = row;
        this.col = col;
        this.f = Double.POSITIVE_INFINITY;
        this.g = Double.POSITIVE_INFINITY;
        this.h = 0;
        this.parent = null;
    }

    /**
     * Compares this node to another based on f value.
     * Used for priority queue ordering.
     * 
     * @param other The node to compare to
     * @return Negative if this node has lower f, positive if higher
     */
    @Override
    public int compareTo(Node other) {
        return Double.compare(this.f, other.f);
    }

    // Getters and setters
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
