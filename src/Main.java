import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Main extends JPanel {
    private static final int GRID_SIZE = 30;
    private final JButton[][] gridButtons;
    private Point start;
    private Point end;
    private final List<Point> barriers;

    public Main() {
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridButtons = new JButton[GRID_SIZE][GRID_SIZE];
        barriers = new ArrayList<>();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridButtons[row][col] = new JButton();
                gridButtons[row][col].setPreferredSize(new Dimension(30, 30));
                gridButtons[row][col].setBackground(Color.WHITE);
                add(gridButtons[row][col]);

                final int r = row;
                final int c = col;
                gridButtons[row][col].addActionListener(e -> handleButtonClick(r, c));
                gridButtons[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            removeBarrier(r, c);
                        }
                    }
                });
            }
        }

        JFrame controlFrame = new JFrame("Controls");
        controlFrame.setAlwaysOnTop(true);
        controlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        controlFrame.setLayout(new FlowLayout());


        JButton startButton = new JButton("Start Pathfinding");
        JButton clearButton = new JButton("Clear");
        JButton mazeButton = new JButton("Generate Maze");
        JButton saveButton = new JButton("Save Image");
        JButton loadButton = new JButton("Load Image");

        startButton.addActionListener(e -> {
            Action startAlgorithmAction = getActionMap().get("startAlgorithm");
            if (startAlgorithmAction != null) {
                startAlgorithmAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "startAlgorithm"));
            }
        });
        clearButton.addActionListener(e -> reset());
        mazeButton.addActionListener(e -> generateRandomMaze());
        saveButton.addActionListener(e -> takeGridScreenshot());
        loadButton.addActionListener(e -> loadImage());

        controlFrame.add(startButton);
        controlFrame.add(clearButton);
        controlFrame.add(mazeButton);
        controlFrame.add(saveButton);
        controlFrame.add(loadButton);

        controlFrame.pack();
        controlFrame.setVisible(true);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "startAlgorithm");
        getActionMap().put("startAlgorithm", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (start != null && end != null) {
                    List<Point> path = aStarPathfinding();
                    visualizePath(path);
                }
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reset");
        getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showResetPopup();
            }
        });


        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "generateMaze");
        getActionMap().put("generateMaze", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRandomMaze();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), "loadScreenshot");
        getActionMap().put("loadScreenshot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "takeScreenshot");
        getActionMap().put("takeScreenshot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                takeGridScreenshot();
            }
        });

    }

    public void loadImage(){
        JFileChooser fileChooser = new JFileChooser();


        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            loadMazeFromScreenshot(filePath);
        } else {
            System.out.println("No file selected.");
        }
    }

    private void handleButtonClick(int row, int col) {
        if (start == null) {
            start = new Point(row, col);
            gridButtons[row][col].setBackground(Color.blue);
        } else if (end == null) {
            end = new Point(row, col);
            gridButtons[row][col].setBackground(Color.RED);
            List<Point> path = aStarPathfinding();
            visualizePath(path);
        } else {
            addBarrier(row, col);
        }
    }

    private void addBarrier(int row, int col) {
        Point barrierToAdd = new Point(row, col);
        if (!barrierToAdd.equals(start) && !barrierToAdd.equals(end)) {
            barriers.add(barrierToAdd);
            gridButtons[row][col].setBackground(Color.BLACK);
            clearPath();
            List<Point> path = aStarPathfinding();
            visualizePath(path);
        }
    }

    private void removeBarrier(int row, int col) {
        Point barrierToRemove = new Point(row, col);
        if (!barrierToRemove.equals(start) && !barrierToRemove.equals(end)) {
            barriers.remove(barrierToRemove);
            gridButtons[row][col].setBackground(Color.WHITE);
            clearPath();
            List<Point> path = aStarPathfinding();
            visualizePath(path);
        }
    }

    public void loadMazeFromScreenshot(String filePath) {
        try {
            BufferedImage screenshot = ImageIO.read(new File(filePath));
            int width = screenshot.getWidth();
            int height = screenshot.getHeight();

            double cellWidth = (double) width / GRID_SIZE;
            double cellHeight = (double) height / GRID_SIZE;

            start = null;
            end = null;
            barriers.clear();

            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    int x = (int) (col * cellWidth + cellWidth / 2);
                    int y = (int) (row * cellHeight + cellHeight / 2);

                    Color pixelColor = new Color(screenshot.getRGB(x, y));

                    if (isColorSimilar(pixelColor, Color.BLUE)) {
                        start = new Point(row, col);
                        gridButtons[row][col].setBackground(Color.BLUE);
                    } else if (isColorSimilar(pixelColor, Color.RED)) {
                        end = new Point(row, col);
                        gridButtons[row][col].setBackground(Color.RED);
                    } else if (isColorSimilar(pixelColor, Color.BLACK)) {
                        barriers.add(new Point(row, col));
                        gridButtons[row][col].setBackground(Color.BLACK);
                    } else {
                        gridButtons[row][col].setBackground(Color.WHITE);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isColorSimilar(Color c1, Color c2) {
        int threshold = 100;
        int diffRed = Math.abs(c1.getRed() - c2.getRed());
        int diffGreen = Math.abs(c1.getGreen() - c2.getGreen());
        int diffBlue = Math.abs(c1.getBlue() - c2.getBlue());
        return diffRed + diffGreen + diffBlue < threshold;
    }

    private void takeGridScreenshot() {
        Point location = this.getLocationOnScreen();
        Dimension size = this.getSize();

        Rectangle gridRectangle = new Rectangle(location.x, location.y, size.width, size.height);

        try {
            Robot robot = new Robot();
            BufferedImage gridImage = robot.createScreenCapture(gridRectangle);

            File outputFile = new File("grid_screenshot.png");
            ImageIO.write(gridImage, "png", outputFile);

            System.out.println("Screenshot saved to: " + outputFile.getAbsolutePath());
        } catch (AWTException | IOException ex) {
            ex.printStackTrace();
        }
    }


    private List<Point> aStarPathfinding() {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];

        Node startNode = new Node(start.x, start.y);
        startNode.g = 0;
        startNode.h = calculateHeuristic(startNode, end);
        startNode.f = startNode.g + startNode.h;
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            visited[current.row][current.col] = true;

            if (current.row == end.x && current.col == end.y) {
                return reconstructPath(current);
            }

            int[] dRow = {0, 1, 0, -1};
            int[] dCol = {1, 0, -1, 0};
            for (int i = 0; i < 4; i++) {
                int newRow = current.row + dRow[i];
                int newCol = current.col + dCol[i];

                if (isValid(newRow, newCol) && !visited[newRow][newCol] && !barriers.contains(new Point(newRow, newCol))) {
                    double tentativeG = current.g + 1;
                    Node neighbor = new Node(newRow, newCol);
                    neighbor.g = tentativeG;
                    neighbor.h = calculateHeuristic(neighbor, end);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;

                    openSet.add(neighbor);
                    visited[newRow][newCol] = true;
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Point> reconstructPath(Node current) {
        List<Point> path = new ArrayList<>();
        while (current != null) {
            path.add(new Point(current.row, current.col));
            current = current.parent;
        }
        return path;
    }

    private double calculateHeuristic(Node node, Point end) {
        return Math.sqrt((node.row - end.x) * (node.row - end.x) + (node.col - end.y) * (node.col - end.y));
    }

    private boolean isValid(int row, int col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }

    private void visualizePath(List<Point> path) {
        for (Point p : path) {
            if (!p.equals(start) && !p.equals(end)) {
                gridButtons[p.x][p.y].setBackground(Color.GREEN);
            }
        }
    }

    private void showResetPopup() {
        int choice = JOptionPane.showConfirmDialog(this, "Reset?", "Reset", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            reset();
        }
    }

    private void reset() {
        start = null;
        end = null;
        barriers.clear();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridButtons[row][col].setBackground(Color.WHITE);
            }
        }
    }

    private void clearPath() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Point currentPoint = new Point(row, col);
                if (!currentPoint.equals(start) && !currentPoint.equals(end) && !barriers.contains(currentPoint)) {
                    gridButtons[row][col].setBackground(Color.WHITE);
                }
            }
        }
    }


    private void generateRandomMaze() {
        start = null;
        end = null;
        barriers.clear();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridButtons[row][col].setBackground(Color.WHITE);
            }
        }

        Random random = new Random();
        start = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        gridButtons[start.x][start.y].setBackground(Color.blue);

        do {
            end = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        } while (start.equals(end));
        gridButtons[end.x][end.y].setBackground(Color.RED);

        int numBarriers = random.nextInt(GRID_SIZE * GRID_SIZE / 4);
        for (int i = 0; i < numBarriers; i++) {
            int barrierX;
            int barrierY;
            do {
                barrierX = random.nextInt(GRID_SIZE);
                barrierY = random.nextInt(GRID_SIZE);
            } while (start.equals(new Point(barrierX, barrierY)) || end.equals(new Point(barrierX, barrierY)));
            barriers.add(new Point(barrierX, barrierY));
            gridButtons[barrierX][barrierY].setBackground(Color.BLACK);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Grid Pathfinding");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new Main());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
