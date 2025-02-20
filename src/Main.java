import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class Main extends JPanel {
    private static int GRID_SIZE = 30;
    private JButton[][] gridButtons;
    private Point start;
    private Point end;
    private final List<Point> barriers;

    int animationDelay = 100;

    int mazeDensity = GRID_SIZE * GRID_SIZE / 4;

    boolean isAnimationToggled;

    boolean stopAnimation;

    private JLabel pathLengthLabel;
    private JLabel nodesExploredLabel;
    private JLabel timeTakenLabel;
    private JLabel algorithmLabel;
    private JLabel heuristicLabel;
    private JLabel gridStatsLabel;
    private JLabel statusMessageLabel;

    JProgressBar complexityBar = new JProgressBar(0, 100);
    JProgressBar efficiencyBar = new JProgressBar(0, 100);

    JPanel complexityPanel;
    JPanel legendPanel;
    JPanel statusPanel;

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
        controlFrame.setLayout(new BoxLayout(controlFrame.getContentPane(), BoxLayout.Y_AXIS));
        controlFrame.setResizable(false);

        JButton startButton = new JButton("Start Pathfinding");
        JButton clearButton = new JButton("Clear Grid");
        JButton GenMazePrimsButton = new JButton("Generate Maze (Prims)");
        JButton saveButton = new JButton("Save Image");
        JButton loadButton = new JButton("Load Image");
        JRadioButton animationToggle = new JRadioButton("Enable Animation");
        JSlider animationDelaySlider = new JSlider(0, 500, 100);
        JSlider mazeDensitySlider = new JSlider(100, GRID_SIZE * GRID_SIZE, mazeDensity);
        JButton changeGridSizeButton = new JButton("Change Grid Size");
        JButton GenMazeButton = new JButton("Generate Maze (Density)");

        JRadioButton statusToggle= new JRadioButton("Enable Status Dashboard");
        JRadioButton complexityToggle = new JRadioButton("Enable Complexity Indicators");
        JRadioButton legendToggle = new JRadioButton("Enable Legends");


        startButton.addActionListener(e -> {
            Action startAlgorithmAction = getActionMap().get("startAlgorithm");
            startAlgorithmAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "startAlgorithm"));
            stopAnimation = false;
        });

        clearButton.addActionListener(e -> reset());
        GenMazePrimsButton.addActionListener(e -> generateMazeUsingPrims());
        GenMazeButton.addActionListener(e -> generateRandomMaze());
        saveButton.addActionListener(e -> takeGridScreenshot());
        loadButton.addActionListener(e -> loadImage());
        animationToggle.addActionListener(e -> isAnimationToggled = animationToggle.isSelected());
        animationDelaySlider.addChangeListener(e -> animationDelay = animationDelaySlider.getValue());
        mazeDensitySlider.addChangeListener(e -> mazeDensity = mazeDensitySlider.getValue());
        changeGridSizeButton.addActionListener(e -> updateGridSizeWithPopup(mazeDensitySlider));

        statusToggle.addActionListener( e -> {
            if(statusToggle.isSelected()){
                statusPanel.setVisible(true);
                controlFrame.pack();
            }else{
                statusPanel.setVisible(false);
                controlFrame.pack();
            }
        });

        complexityToggle.addActionListener( e -> {
            if(complexityToggle.isSelected()){
                complexityPanel.setVisible(true);
                controlFrame.pack();
            }else{
                complexityPanel.setVisible(false);
                controlFrame.pack();
            }
        });

        legendToggle.addActionListener( e -> {
            if(legendToggle.isSelected()){
                legendPanel.setVisible(true);
                controlFrame.pack();
            }else{
                legendPanel.setVisible(false);
                controlFrame.pack();
            }
        });



        JPanel pathfindingPanel = createPanelWithComponents(startButton, clearButton);
        JPanel filePanel = createPanelWithComponents(saveButton, loadButton);

        JPanel mazePrimsPanel = createPanelWithComponents(GenMazePrimsButton);
        JPanel mazeDensityPanel = new JPanel(new BorderLayout());
        mazeDensityPanel.add(GenMazeButton, BorderLayout.WEST);
        mazeDensityPanel.add(mazeDensitySlider, BorderLayout.CENTER);

        JPanel animationPanel = new JPanel(new BorderLayout());
        animationPanel.add(animationToggle, BorderLayout.WEST);
        animationPanel.add(animationDelaySlider, BorderLayout.CENTER);

        JPanel gridSizePanel = createPanelWithComponents(changeGridSizeButton);

        controlFrame.add(createSectionPanel("Pathfinding Controls", pathfindingPanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("File Operations", filePanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("Maze Generation",
                mazePrimsPanel,
                mazeDensityPanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("Animation Settings", animationPanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("Grid Configuration", gridSizePanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("Features", statusToggle, complexityToggle, legendToggle));


        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createStatusPanel());
        controlFrame.add(createLegendPanel(), BorderLayout.CENTER);
        controlFrame.add(createComplexityPanel());
        controlFrame.pack();

        statusPanel.setVisible(false);
        complexityPanel.setVisible(false);
        legendPanel.setVisible(false);

        controlFrame.pack();
        controlFrame.setVisible(true);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "startAlgorithm");
        getActionMap().put("startAlgorithm", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (start != null && end != null) {
                    long startTime = System.currentTimeMillis();
                    List<List<Point>> paths = aStarPathfinding();
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    int totalNodes = GRID_SIZE * GRID_SIZE;

                    if (paths.get(1).isEmpty()) {
                        updateComplexityIndicators(0, 0);
                        updatePathMetrics(0, paths.get(0).size(), elapsedTime);
                        showStatusMessage("No path exists!", Color.RED);
                        return;
                    }

                    // Calculate metrics
                    int optimality = calculateOptimality(paths.get(1));
                    int efficiency = calculateEfficiency(paths.get(0).size(), totalNodes);

                    if (!isAnimationToggled) {
                        visualizePath(paths.get(1));
                        updatePathMetrics(
                                paths.get(1).size() - 1,
                                paths.get(0).size(),
                                elapsedTime
                        );
                        updateComplexityIndicators(optimality, efficiency);
                        showStatusMessage("Path found!", Color.GREEN);
                    } else {
                        visualizePathWithAnimation(paths.get(0), paths.get(1), () -> {
                            updatePathMetrics(
                                    paths.get(1).size() - 1,
                                    paths.get(0).size(),
                                    elapsedTime
                            );
                            updateComplexityIndicators(optimality, efficiency);
                            showStatusMessage("Path found!", Color.GREEN);
                        });
                    }
                    updateGridStats();
                } else {
                    showStatusMessage("Set both start and end points first!", Color.ORANGE);
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
                generateMazeUsingPrims();
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

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "newGridSize");
        getActionMap().put("newGridSize", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGridSizeWithPopup(mazeDensitySlider);
            }
        });

    }

    private JPanel createPanelWithComponents(JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        for (JComponent comp : components) {
            panel.add(comp);
        }
        return panel;
    }

    private JPanel createComplexityPanel() {
        complexityPanel = new JPanel(new GridLayout(2, 1));
        complexityPanel.setBorder(BorderFactory.createTitledBorder("Path Analysis"));

        complexityBar.setStringPainted(true);
        complexityBar.setString("Optimality");

        efficiencyBar.setStringPainted(true);
        efficiencyBar.setString("Efficiency");

        complexityPanel.add(complexityBar);
        complexityPanel.add(efficiencyBar);

        return complexityPanel;
    }

    private void updateComplexityIndicators(int optimality, int efficiency) {
        complexityBar.setValue(optimality);
        efficiencyBar.setValue(efficiency);
    }

    private JPanel createSectionPanel(String title, JComponent... components) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(BorderFactory.createTitledBorder(title));

        for (JComponent comp : components) {
            comp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sectionPanel.add(comp);
        }
        return sectionPanel;
    }

    private JPanel createStatusPanel() {


        statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status Dashboard"));

        // Initialize labels with monospaced font for alignment
        Font statusFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

        pathLengthLabel = createStatusLabel("Path Length: -", statusFont);
        nodesExploredLabel = createStatusLabel("Nodes Explored: -", statusFont);
        timeTakenLabel = createStatusLabel("Time Taken: -", statusFont);
        algorithmLabel = createStatusLabel("Algorithm: A*", statusFont);
        heuristicLabel = createStatusLabel("Heuristic: Euclidean", statusFont);
        gridStatsLabel = createStatusLabel(String.format("Grid: %dx%d (0 barriers)", GRID_SIZE, GRID_SIZE), statusFont);
        statusMessageLabel = createStatusLabel("Ready", new Font(Font.SANS_SERIF, Font.BOLD, 12));
        statusMessageLabel.setForeground(Color.BLUE);

        // Add components to panel
        statusPanel.add(createStatusRow(pathLengthLabel, nodesExploredLabel));
        statusPanel.add(createStatusRow(timeTakenLabel, algorithmLabel));
        statusPanel.add(createStatusRow(heuristicLabel, gridStatsLabel));
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(createCenteredLabel(statusMessageLabel));

        return statusPanel;
    }

    private JLabel createStatusLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createStatusRow(JComponent left, JComponent right) {
        JPanel row = new JPanel(new BorderLayout());
        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        return row;
    }

    private JPanel createCenteredLabel(JLabel label) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(label);
        return panel;
    }

    public void updatePathMetrics(int length, int explored, long timeMs) {
        pathLengthLabel.setText(String.format("Path Length: %d", length));
        nodesExploredLabel.setText(String.format("Nodes Explored: %d", explored));
        timeTakenLabel.setText(String.format("Time Taken: %d ms", timeMs));
    }

    public void updateGridStats() {
        gridStatsLabel.setText(String.format("Grid: %dx%d (%d barriers)",
                GRID_SIZE, GRID_SIZE, barriers.size()));
    }

    public void showStatusMessage(String message, Color color) {
        statusMessageLabel.setText(message);
        statusMessageLabel.setForeground(color);

        // Clear message after 3 seconds
        new Timer(3000, e -> {
            statusMessageLabel.setText("Ready");
            statusMessageLabel.setForeground(Color.BLUE);
        }).start();
    }

    private boolean isGridSizeViable(int newSize, int buttonSize) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int totalWidth = newSize * buttonSize;
        int totalHeight = newSize * buttonSize;

        int margin = 100;
        return totalWidth <= screenSize.width - margin && totalHeight <= screenSize.height - margin;
    }

    public void updateGridSizeWithPopup(JSlider mazeDensitySlider) {
        JTextField gridSizeField = new JTextField(5);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("New Grid Size:"));
        inputPanel.add(gridSizeField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Enter New Grid Size", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newSize = Integer.parseInt(gridSizeField.getText());
                if (newSize <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "Grid size must be positive.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int maxGridDimension = Math.min(screenSize.height, screenSize.width) - 100;
                int buttonSize = maxGridDimension / newSize;

                // Ensure minimum button size
                if (buttonSize < 10) {
                    buttonSize = 10;
                }

                if (!isGridSizeViable(newSize, buttonSize)) {
                    int confirmResult = JOptionPane.showConfirmDialog(null,
                            "The grid might not fit on your screen. Continue anyway?",
                            "Size Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (confirmResult != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                updateGridSize(newSize, buttonSize, mazeDensitySlider);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Please Invalid Input");
            }
        }
    }

    public void updateGridSize(int newSize, int buttonSize, JSlider mazeDensitySlider) {
        GRID_SIZE = newSize;
        mazeDensitySlider.setMaximum(GRID_SIZE * GRID_SIZE);

        removeAll();
        revalidate();
        repaint();

        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridButtons = new JButton[GRID_SIZE][GRID_SIZE];
        barriers.clear();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridButtons[row][col] = new JButton();
                gridButtons[row][col].setPreferredSize(new Dimension(buttonSize, buttonSize));
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

        reset();

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.pack();

        frame.setLocationRelativeTo(null);

        reset();
        showStatusMessage("Grid size updated to " + GRID_SIZE + "x" + GRID_SIZE, Color.BLUE);
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
            stopAnimation = false;
            start = new Point(row, col);
            gridButtons[row][col].setBackground(Color.blue);
        } else if (end == null) {
            end = new Point(row, col);
            gridButtons[row][col].setBackground(Color.RED);
            List<List<Point>> paths = aStarPathfinding();
            int optimality = calculateOptimality(paths.get(1));
            int efficiency = calculateEfficiency(paths.get(0).size(), GRID_SIZE*GRID_SIZE);
            updateComplexityIndicators(optimality, efficiency);
            if (!isAnimationToggled) {
                visualizePath(paths.get(1));
            }
        }else {
            addBarrier(row, col);
        }

    }

    private void addBarrier(int row, int col) {
        Point barrierToAdd = new Point(row, col);
        if (!barrierToAdd.equals(start) && !barrierToAdd.equals(end)) {
            barriers.add(barrierToAdd);
            gridButtons[row][col].setBackground(Color.BLACK);
            clearPath();
            if (!isAnimationToggled) {
                List<List<Point>> paths = aStarPathfinding();
                visualizePath(paths.get(1));
                int optimality = calculateOptimality(paths.get(1));
                int efficiency = calculateEfficiency(paths.get(0).size(), GRID_SIZE*GRID_SIZE);
                updateComplexityIndicators(optimality, efficiency);


            }
        }

        updateGridStats();
    }

    private void removeBarrier(int row, int col) {
        Point barrierToRemove = new Point(row, col);
        if (!barrierToRemove.equals(start) && !barrierToRemove.equals(end)) {
            barriers.removeIf(p -> p.equals(barrierToRemove));
            gridButtons[row][col].setBackground(Color.WHITE);
            clearPath();
            if (!isAnimationToggled) {
                List<List<Point>> paths = aStarPathfinding();
                visualizePath(paths.get(1));
                int optimality = calculateOptimality(paths.get(1));
                int efficiency = calculateEfficiency(paths.get(0).size(), GRID_SIZE*GRID_SIZE);
                updateComplexityIndicators(optimality, efficiency);
            }
        }

        updateGridStats();
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


    private List<List<Point>> aStarPathfinding() {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];
        List<Point> explorationPath = new ArrayList<>();
        List<Point> fastestPath = new ArrayList<>();

        Node startNode = new Node(start.x, start.y);
        startNode.g = 0;
        startNode.h = calculateHeuristic(startNode, end);
        startNode.f = startNode.g + startNode.h;
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            explorationPath.add(new Point(current.row, current.col));

            visited[current.row][current.col] = true;

            if (current.row == end.x && current.col == end.y) {
                fastestPath = reconstructPath(current);
                return Arrays.asList(explorationPath, fastestPath);
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

        return Arrays.asList(explorationPath, fastestPath);
    }

    private int calculateOptimality(List<Point> fastestPath) {
        if (fastestPath.isEmpty()) return 0;

        // Compare actual path length to theoretical minimum (Manhattan distance)
        int actualLength = fastestPath.size() - 1; // Subtract start node
        int theoreticalMin = Math.abs(start.x - end.x) + Math.abs(start.y - end.y);

        return theoreticalMin > 0
                ? (int) ((double) theoreticalMin / actualLength * 100)
                : 100;
    }

    private int calculateEfficiency(int exploredNodes, int totalNodes) {
        double explorationRatio = (double) exploredNodes / totalNodes;
        return 100 - (int) (explorationRatio * 100);
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
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE && !barriers.contains(new Point(row, col));
    }

    private void visualizePath(List<Point> path) {
        for (Point p : path) {
            if (!p.equals(start) && !p.equals(end)) {
                gridButtons[p.x][p.y].setBackground(Color.GREEN);
            }
        }
    }

    private void visualizePathWithAnimation(List<Point> explorationPath,
                                            List<Point> fastestPath,
                                            Runnable completionCallback) {
        stopAnimation = false;
        Timer timer = new Timer(animationDelay, null);
        timer.addActionListener(new ActionListener() {
            private int explorationIndex = 0;
            private int fastestIndex = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (stopAnimation) {
                    timer.stop();
                    if (completionCallback != null) completionCallback.run();
                    return;
                }

                if (explorationIndex < explorationPath.size()) {
                    Point p = explorationPath.get(explorationIndex);
                    if (!p.equals(start) && !p.equals(end)) {
                        gridButtons[p.x][p.y].setBackground(Color.YELLOW);
                    }
                    explorationIndex++;
                }
                else if (fastestIndex < fastestPath.size()) {
                    Point p = fastestPath.get(fastestIndex);
                    if (!p.equals(start) && !p.equals(end)) {
                        gridButtons[p.x][p.y].setBackground(Color.GREEN);
                    }
                    fastestIndex++;
                }
                else {
                    timer.stop();
                    if (completionCallback != null) completionCallback.run();
                }
            }
        });
        timer.start();
    }
    private void stopAnimation() {
        stopAnimation = true;
    }

    private JPanel createLegendPanel() {
        legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legend"));

        addLegendItem(legendPanel, Color.BLUE, "Start Point");
        addLegendItem(legendPanel, Color.RED, "End Point");
        addLegendItem(legendPanel, Color.BLACK, "Barrier");
        addLegendItem(legendPanel, Color.GREEN, "Final Path");
        addLegendItem(legendPanel, Color.YELLOW, "Explored Area");

        return legendPanel;
    }

    private void addLegendItem(JPanel panel, Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorLabel = new JLabel("   ");
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        item.add(colorLabel);
        item.add(new JLabel(text));
        panel.add(item);
    }



    private void showResetPopup() {
        int choice = JOptionPane.showConfirmDialog(this, "Reset?", "Reset", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            reset();
        }
    }

    private void reset() {
        stopAnimation();
        start = null;
        end = null;
        barriers.clear();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridButtons[row][col].setBackground(Color.WHITE);
            }
        }
        updatePathMetrics(0, 0, 0); // Reset metrics
        updateGridStats(); // Reset barrier count
        showStatusMessage("Grid cleared", Color.BLUE);
        updateComplexityIndicators(0, 0);
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
        stopAnimation = true;
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

        int numBarriers = random.nextInt(mazeDensity);
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

        updateGridStats();
        showStatusMessage("Maze generated (Density)", new Color(0, 100, 0));
    }

    private void generateMazeUsingPrims() {
        stopAnimation = true;
        start = null;
        end = null;
        barriers.clear();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridButtons[row][col].setBackground(Color.WHITE);
            }
        }
        boolean[][] mazeGrid = new boolean[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                mazeGrid[i][j] = true;
            }
        }

        Random random = new Random();
        start = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        end = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        gridButtons[end.x][end.y].setBackground(Color.RED);
        gridButtons[start.x][start.y].setBackground(Color.BLUE);

        mazeGrid[start.x][start.y] = false;

        List<Point> walls = new ArrayList<>();
        addNeighboringWalls(start.x, start.y, walls);

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
                if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE && !mazeGrid[nx][ny]) {
                    openNeighborCount++;
                }
            }

            if (openNeighborCount == 1) {
                mazeGrid[x][y] = false;
                addNeighboringWalls(x, y, walls);
            }
            walls.remove(randomWallIndex);
        }

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (mazeGrid[i][j] && !new Point(i,j).equals(start) && !new Point(i,j).equals(end) ) {
                    gridButtons[i][j].setBackground(Color.BLACK);
                    barriers.add(new Point(i,j));
                } else {
                    Point check = new Point(i, j);
                    if (!check.equals(start) && !check.equals(end)) {
                        gridButtons[i][j].setBackground(Color.WHITE);
                    }
                }
            }
        }
        updateGridStats();
        showStatusMessage("Prim's Maze generated", new Color(0, 100, 0));
    }

    private void addNeighboringWalls(int x, int y, List<Point> walls) {
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE) {
                walls.add(new Point(nx, ny));
            }
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
