class Node implements Comparable<Node> {
    int row;
    int col;
    double f;
    double g;
    double h;
    Node parent;

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
        this.f = Double.POSITIVE_INFINITY;
        this.g = Double.POSITIVE_INFINITY;
        this.h = 0;
        this.parent = null;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.f, other.f);
    }
}