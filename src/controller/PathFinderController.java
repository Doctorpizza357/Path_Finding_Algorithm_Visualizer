package controller;

import algorithm.PathFinder;
import maze.MazeGenerator;
import ui.AnalysisPanel;
import ui.LegendPanel;
import ui.StatusPanel;
import util.GridUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main controller class for the Path Finding Visualizer application.
 * Manages the grid, UI components, and pathfinding operations.
 */
public class PathFinderController extends JPanel {
    // Grid properties
    private int gridSize = 30;
    private JButton[][] gridButtons;
    private Point start;
    private Point end;
    private final List<Point> barriers = new ArrayList<>();
    
    // Animation properties
    private int animationDelay = 100;
    private boolean isAnimationToggled = false;
    private boolean stopAnimation = false;
    
    // Maze properties
    private int mazeDensity;
    
    // UI components
    private StatusPanel statusPanel;
    private AnalysisPanel analysisPanel;
    private LegendPanel legendPanel;
    
    // Algorithm components
    private PathFinder pathFinder;
    private MazeGenerator mazeGenerator;
    
    /**
     * Creates a new PathFinderController with the default grid size.
     */
    public PathFinderController() {
        mazeDensity = gridSize * gridSize / 4;
        
        // Initialize the grid
        initializeGrid();
        
        // Initialize algorithm components
        pathFinder = new PathFinder(gridSize, barriers);
        mazeGenerator = new MazeGenerator(gridSize);
        
        // Create and display the control panel
        createControlPanel();
        
        // Set up keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    /**
     * Initializes the grid with buttons.
     */
    private void initializeGrid() {
        setLayout(new GridLayout(gridSize, gridSize));
        gridButtons = new JButton[gridSize][gridSize];
        
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
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
    }
    
    /**
     * Creates and displays the control panel.
     */
    private void createControlPanel() {
        JFrame controlFrame = new JFrame("Controls");
        controlFrame.setAlwaysOnTop(true);
        controlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        controlFrame.setLayout(new BoxLayout(controlFrame.getContentPane(), BoxLayout.Y_AXIS));
        controlFrame.setResizable(false);
        
        // Create buttons and controls
        JButton startButton = new JButton("Start Pathfinding");
        JButton clearButton = new JButton("Clear Grid");
        JButton genMazePrimsButton = new JButton("Generate Maze (Prims)");
        JButton saveButton = new JButton("Save Image");
        JButton loadButton = new JButton("Load Image");
        JRadioButton animationToggle = new JRadioButton("Enable Animation");
        JSlider animationDelaySlider = new JSlider(0, 500, animationDelay);
        JSlider mazeDensitySlider = new JSlider(100, gridSize * gridSize, mazeDensity);
        JButton changeGridSizeButton = new JButton("Change Grid Size");
        JButton genMazeButton = new JButton("Generate Maze (Density)");
        
        JRadioButton statusToggle = new JRadioButton("Enable Status Dashboard");
        JRadioButton complexityToggle = new JRadioButton("Enable Complexity Indicators");
        JRadioButton legendToggle = new JRadioButton("Enable Legends");
        
        // Create UI panels
        statusPanel = new StatusPanel(gridSize);
        analysisPanel = new AnalysisPanel();
        legendPanel = new LegendPanel();
        
        // Add action listeners
        startButton.addActionListener(e -> {
            Action startAlgorithmAction = getActionMap().get("startAlgorithm");
            startAlgorithmAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "startAlgorithm"));
            stopAnimation = false;
        });
        
        clearButton.addActionListener(e -> reset());
        genMazePrimsButton.addActionListener(e -> generateMazeUsingPrims());
        genMazeButton.addActionListener(e -> generateRandomMaze());
        saveButton.addActionListener(e -> takeGridScreenshot());
        loadButton.addActionListener(e -> loadImage());
        animationToggle.addActionListener(e -> isAnimationToggled = animationToggle.isSelected());
        animationDelaySlider.addChangeListener(e -> animationDelay = animationDelaySlider.getValue());
        mazeDensitySlider.addChangeListener(e -> mazeDensity = mazeDensitySlider.getValue());
        changeGridSizeButton.addActionListener(e -> updateGridSizeWithPopup(mazeDensitySlider));
        
        statusToggle.addActionListener(e -> statusPanel.setVisible(statusToggle.isSelected()));
        complexityToggle.addActionListener(e -> analysisPanel.setVisible(complexityToggle.isSelected()));
        legendToggle.addActionListener(e -> legendPanel.setVisible(legendToggle.isSelected()));
        
        // Create panels and add components
        JPanel pathfindingPanel = createPanelWithComponents(startButton, clearButton);
        JPanel filePanel = createPanelWithComponents(saveButton, loadButton);
        JPanel mazePrimsPanel = createPanelWithComponents(genMazePrimsButton);
        
        JPanel mazeDensityPanel = new JPanel(new BorderLayout());
        mazeDensityPanel.add(genMazeButton, BorderLayout.WEST);
        mazeDensityPanel.add(mazeDensitySlider, BorderLayout.CENTER);
        
        JPanel animationPanel = new JPanel(new BorderLayout());
        animationPanel.add(animationToggle, BorderLayout.WEST);
        animationPanel.add(animationDelaySlider, BorderLayout.CENTER);
        
        JPanel gridSizePanel = createPanelWithComponents(changeGridSizeButton);
        
        // Add sections to control frame
        controlFrame.add(createSectionPanel("Pathfinding Controls", pathfindingPanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("File Operations", filePanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("Maze Generation", mazePrimsPanel, mazeDensityPanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("Animation Settings", animationPanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("Grid Configuration", gridSizePanel));
        controlFrame.add(Box.createVerticalStrut(10));
        controlFrame.add(createSectionPanel("Features", statusToggle, complexityToggle, legendToggle));
        controlFrame.add(Box.createVerticalStrut(10));
        
        // Add UI components
        controlFrame.add(statusPanel);
        controlFrame.add(legendPanel);
        controlFrame.add(analysisPanel);
        
        // Hide panels by default
        statusPanel.setVisible(false);
        analysisPanel.setVisible(false);
        legendPanel.setVisible(false);
        
        // Display the control frame
        controlFrame.pack();
        controlFrame.setVisible(true);
    }
    
    /**
     * Sets up keyboard shortcuts.
     */
    private void setupKeyboardShortcuts() {
        // Start algorithm shortcut (S key)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "startAlgorithm");
        getActionMap().put("startAlgorithm", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startPathfinding();
            }
        });
        
        // Reset shortcut (R key)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reset");
        getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showResetPopup();
            }
        });
        
        // Generate maze shortcut (G key)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "generateMaze");
        getActionMap().put("generateMaze", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMazeUsingPrims();
            }
        });
        
        // Load screenshot shortcut (L key)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), "loadScreenshot");
        getActionMap().put("loadScreenshot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });
        
        // Take screenshot shortcut (E key)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "takeScreenshot");
        getActionMap().put("takeScreenshot", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                takeGridScreenshot();
            }
        });
        
        // New grid size shortcut (F key)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "newGridSize");
        getActionMap().put("newGridSize", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGridSizeWithPopup(null);
            }
        });
    }
    
    /**
     * Handles grid button clicks for setting start/end points and barriers.
     */
    private void handleButtonClick(int row, int col) {
        if (start == null) {
            stopAnimation = false;
            start = new Point(row, col);
            gridButtons[row][col].setBackground(Color.BLUE);
        } else if (end == null) {
            end = new Point(row, col);
            gridButtons[row][col].setBackground(Color.RED);
            startPathfinding();
        } else {
            addBarrier(row, col);
        }
    }
    
    /**
     * Adds a barrier at the specified position.
     */
    private void addBarrier(int row, int col) {
        Point barrierToAdd = new Point(row, col);
        if (!barrierToAdd.equals(start) && !barrierToAdd.equals(end)) {
            barriers.add(barrierToAdd);
            gridButtons[row][col].setBackground(Color.BLACK);
            clearPath();
            
            if (!isAnimationToggled && start != null && end != null) {
                runPathfinding();
            }
            
            statusPanel.updateGridStats(gridSize, barriers.size());
        }
    }
    
    /**
     * Removes a barrier at the specified position.
     */
    private void removeBarrier(int row, int col) {
        Point barrierToRemove = new Point(row, col);
        if (!barrierToRemove.equals(start) && !barrierToRemove.equals(end)) {
            barriers.removeIf(p -> p.equals(barrierToRemove));
            gridButtons[row][col].setBackground(Color.WHITE);
            clearPath();
            
            if (!isAnimationToggled && start != null && end != null) {
                runPathfinding();
            }
            
            statusPanel.updateGridStats(gridSize, barriers.size());
        }
    }
    
    /**
     * Starts the pathfinding process.
     */
    private void startPathfinding() {
        if (start != null && end != null) {
            long startTime = System.currentTimeMillis();
            List<List<Point>> paths = pathFinder.findPath(start, end);
            long elapsedTime = System.currentTimeMillis() - startTime;
            int totalNodes = gridSize * gridSize;
            
            if (paths.get(1).isEmpty()) {
                analysisPanel.updateIndicators(0, 0);
                statusPanel.updatePathMetrics(0, paths.get(0).size(), elapsedTime);
                statusPanel.showStatusMessage("No path exists!", Color.RED);
                return;
            }
            
            // Calculate metrics
            int optimality = pathFinder.calculateOptimality(start, end, paths.get(1));
            int efficiency = pathFinder.calculateEfficiency(paths.get(0).size(), totalNodes);
            
            if (!isAnimationToggled) {
                visualizePath(paths.get(1));
                statusPanel.updatePathMetrics(
                        paths.get(1).size() - 1,
                        paths.get(0).size(),
                        elapsedTime
                );
                analysisPanel.updateIndicators(optimality, efficiency);
                statusPanel.showStatusMessage("Path found!", Color.GREEN);
            } else {
                visualizePathWithAnimation(paths.get(0), paths.get(1), () -> {
                    statusPanel.updatePathMetrics(
                            paths.get(1).size() - 1,
                            paths.get(0).size(),
                            elapsedTime
                    );
                    analysisPanel.updateIndicators(optimality, efficiency);
                    statusPanel.showStatusMessage("Path found!", Color.GREEN);
                });
            }
            
            statusPanel.updateGridStats(gridSize, barriers.size());
        } else {
            statusPanel.showStatusMessage("Set both start and end points first!", Color.ORANGE);
        }
    }
    
    /**
     * Runs the pathfinding algorithm and updates visualizations.
     */
    private void runPathfinding() {
        pathFinder = new PathFinder(gridSize, barriers);
        List<List<Point>> paths = pathFinder.findPath(start, end);
        visualizePath(paths.get(1));
        
        int optimality = pathFinder.calculateOptimality(start, end, paths.get(1));
        int efficiency = pathFinder.calculateEfficiency(paths.get(0).size(), gridSize * gridSize);
        analysisPanel.updateIndicators(optimality, efficiency);
    }
    
    /**
     * Creates a panel with multiple components.
     */
    private JPanel createPanelWithComponents(JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        for (JComponent comp : components) {
            panel.add(comp);
        }
        return panel;
    }
    
    /**
     * Creates a section panel with a title and components.
     */
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
    
    /**
     * Updates grid size based on user input.
     */
    private void updateGridSizeWithPopup(JSlider mazeDensitySlider) {
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
                
                if (!GridUtils.isGridSizeViable(newSize, buttonSize)) {
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
                JOptionPane.showMessageDialog(null, "Invalid Input");
            }
        }
    }
    
    /**
     * Updates the grid size and reinitializes the grid.
     */
    private void updateGridSize(int newSize, int buttonSize, JSlider mazeDensitySlider) {
        gridSize = newSize;
        if (mazeDensitySlider != null) {
            mazeDensitySlider.setMaximum(gridSize * gridSize);
        }
        
        removeAll();
        revalidate();
        repaint();
        
        setLayout(new GridLayout(gridSize, gridSize));
        gridButtons = new JButton[gridSize][gridSize];
        barriers.clear();
        
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
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
        
        pathFinder = new PathFinder(gridSize, barriers);
        mazeGenerator = new MazeGenerator(gridSize);
        
        statusPanel.showStatusMessage("Grid size updated to " + gridSize + "x" + gridSize, Color.BLUE);
    }
    
    /**
     * Visualizes the path on the grid.
     */
    private void visualizePath(List<Point> path) {
        for (Point p : path) {
            if (!p.equals(start) && !p.equals(end)) {
                gridButtons[p.x][p.y].setBackground(Color.GREEN);
            }
        }
    }
    
    /**
     * Visualizes the exploration and path with animation.
     */
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
                } else if (fastestIndex < fastestPath.size()) {
                    Point p = fastestPath.get(fastestIndex);
                    if (!p.equals(start) && !p.equals(end)) {
                        gridButtons[p.x][p.y].setBackground(Color.GREEN);
                    }
                    fastestIndex++;
                } else {
                    timer.stop();
                    if (completionCallback != null) completionCallback.run();
                }
            }
        });
        timer.start();
    }
    
    /**
     * Stops any ongoing animation.
     */
    private void stopAnimation() {
        stopAnimation = true;
    }
    
    /**
     * Resets the grid to its initial state.
     */
    private void reset() {
        stopAnimation();
        start = null;
        end = null;
        barriers.clear();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                gridButtons[row][col].setBackground(Color.WHITE);
            }
        }
        statusPanel.updatePathMetrics(0, 0, 0);
        statusPanel.updateGridStats(gridSize, 0);
        statusPanel.showStatusMessage("Grid cleared", Color.BLUE);
        analysisPanel.updateIndicators(0, 0);
    }
    
    /**
     * Shows a reset confirmation popup.
     */
    private void showResetPopup() {
        int choice = JOptionPane.showConfirmDialog(this, "Reset?", "Reset", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            reset();
        }
    }
    
    /**
     * Clears the path visualization from the grid.
     */
    private void clearPath() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Point currentPoint = new Point(row, col);
                if (!currentPoint.equals(start) && !currentPoint.equals(end) && !barriers.contains(currentPoint)) {
                    gridButtons[row][col].setBackground(Color.WHITE);
                }
            }
        }
    }
    
    /**
     * Generates a random maze using density approach.
     */
    private void generateRandomMaze() {
        stopAnimation = true;
        
        // Clear grid
        reset();
        
        // Generate maze
        MazeGenerator.MazeData mazeData = mazeGenerator.generateRandomMaze(mazeDensity);
        
        // Apply maze to grid
        start = mazeData.getStart();
        end = mazeData.getEnd();
        barriers.addAll(mazeData.getBarriers());
        
        // Update UI
        gridButtons[start.x][start.y].setBackground(Color.BLUE);
        gridButtons[end.x][end.y].setBackground(Color.RED);
        
        for (Point barrier : barriers) {
            gridButtons[barrier.x][barrier.y].setBackground(Color.BLACK);
        }
        
        statusPanel.updateGridStats(gridSize, barriers.size());
        statusPanel.showStatusMessage("Maze generated (Density)", new Color(0, 100, 0));
    }
    
    /**
     * Generates a maze using Prim's algorithm.
     */
    private void generateMazeUsingPrims() {
        stopAnimation = true;
        
        // Clear grid
        reset();
        
        // Generate maze
        MazeGenerator.MazeData mazeData = mazeGenerator.generatePrimsMaze();
        
        // Apply maze to grid
        start = mazeData.getStart();
        end = mazeData.getEnd();
        barriers.addAll(mazeData.getBarriers());
        
        // Update UI
        gridButtons[start.x][start.y].setBackground(Color.BLUE);
        gridButtons[end.x][end.y].setBackground(Color.RED);
        
        for (Point barrier : barriers) {
            gridButtons[barrier.x][barrier.y].setBackground(Color.BLACK);
        }
        
        statusPanel.updateGridStats(gridSize, barriers.size());
        statusPanel.showStatusMessage("Prim's Maze generated", new Color(0, 100, 0));
    }
    
    /**
     * Takes a screenshot of the grid.
     */
    private void takeGridScreenshot() {
        File outputFile = new File("grid_screenshot.png");
        if (GridUtils.takeScreenshot(this, outputFile)) {
            statusPanel.showStatusMessage("Screenshot saved", Color.GREEN);
        } else {
            statusPanel.showStatusMessage("Screenshot failed", Color.RED);
        }
    }
    
    /**
     * Loads a maze from an image file.
     */
    private void loadImage() {
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
    
    /**
     * Loads a maze from a screenshot file.
     */
    private void loadMazeFromScreenshot(String filePath) {
        try {
            BufferedImage screenshot = ImageIO.read(new File(filePath));
            int width = screenshot.getWidth();
            int height = screenshot.getHeight();
            
            double cellWidth = (double) width / gridSize;
            double cellHeight = (double) height / gridSize;
            
            start = null;
            end = null;
            barriers.clear();
            
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    int x = (int) (col * cellWidth + cellWidth / 2);
                    int y = (int) (row * cellHeight + cellHeight / 2);
                    
                    Color pixelColor = new Color(screenshot.getRGB(x, y));
                    
                    if (GridUtils.isColorSimilar(pixelColor, Color.BLUE)) {
                        start = new Point(row, col);
                        gridButtons[row][col].setBackground(Color.BLUE);
                    } else if (GridUtils.isColorSimilar(pixelColor, Color.RED)) {
                        end = new Point(row, col);
                        gridButtons[row][col].setBackground(Color.RED);
                    } else if (GridUtils.isColorSimilar(pixelColor, Color.BLACK)) {
                        barriers.add(new Point(row, col));
                        gridButtons[row][col].setBackground(Color.BLACK);
                    } else {
                        gridButtons[row][col].setBackground(Color.WHITE);
                    }
                }
            }
            
            statusPanel.updateGridStats(gridSize, barriers.size());
            statusPanel.showStatusMessage("Maze loaded from image", Color.GREEN);
        } catch (Exception ex) {
            ex.printStackTrace();
            statusPanel.showStatusMessage("Failed to load maze", Color.RED);
        }
    }
}
