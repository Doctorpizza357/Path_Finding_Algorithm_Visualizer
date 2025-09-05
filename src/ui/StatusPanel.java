package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that displays status information about the pathfinding.
 */
public class StatusPanel extends JPanel {
    private final JLabel pathLengthLabel;
    private final JLabel nodesExploredLabel;
    private final JLabel timeTakenLabel;
    private final JLabel algorithmLabel;
    private final JLabel heuristicLabel;
    private final JLabel gridStatsLabel;
    private final JLabel statusMessageLabel;
    
    /**
     * Creates a new status panel with the default grid size.
     * 
     * @param gridSize The initial grid size
     */
    public StatusPanel(int gridSize) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Status Dashboard"));
        
        // Initialize labels with monospaced font for alignment
        Font statusFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        
        pathLengthLabel = createStatusLabel("Path Length: -", statusFont);
        nodesExploredLabel = createStatusLabel("Nodes Explored: -", statusFont);
        timeTakenLabel = createStatusLabel("Time Taken: -", statusFont);
        algorithmLabel = createStatusLabel("Algorithm: A*", statusFont);
        heuristicLabel = createStatusLabel("Heuristic: Euclidean", statusFont);
        gridStatsLabel = createStatusLabel(String.format("Grid: %dx%d (0 barriers)", gridSize, gridSize), statusFont);
        statusMessageLabel = createStatusLabel("Ready", new Font(Font.SANS_SERIF, Font.BOLD, 12));
        statusMessageLabel.setForeground(Color.BLUE);
        
        // Add components to panel
        add(createStatusRow(pathLengthLabel, nodesExploredLabel));
        add(createStatusRow(timeTakenLabel, algorithmLabel));
        add(createStatusRow(heuristicLabel, gridStatsLabel));
        add(Box.createVerticalStrut(5));
        add(createCenteredLabel(statusMessageLabel));
    }
    
    /**
     * Creates a status label with the specified text and font.
     * 
     * @param text The label text
     * @param font The font to use
     * @return The created label
     */
    private JLabel createStatusLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    /**
     * Creates a row with two components.
     * 
     * @param left The left component
     * @param right The right component
     * @return A panel containing the components
     */
    private JPanel createStatusRow(JComponent left, JComponent right) {
        JPanel row = new JPanel(new BorderLayout());
        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        return row;
    }
    
    /**
     * Creates a centered label panel.
     * 
     * @param label The label to center
     * @return A panel with the centered label
     */
    private JPanel createCenteredLabel(JLabel label) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(label);
        return panel;
    }
    
    /**
     * Updates path metrics in the status panel.
     * 
     * @param length Path length
     * @param explored Number of nodes explored
     * @param timeMs Time taken in milliseconds
     */
    public void updatePathMetrics(int length, int explored, long timeMs) {
        pathLengthLabel.setText(String.format("Path Length: %d", length));
        nodesExploredLabel.setText(String.format("Nodes Explored: %d", explored));
        timeTakenLabel.setText(String.format("Time Taken: %d ms", timeMs));
    }
    
    /**
     * Updates grid statistics.
     * 
     * @param gridSize The grid size
     * @param barrierCount The number of barriers
     */
    public void updateGridStats(int gridSize, int barrierCount) {
        gridStatsLabel.setText(String.format("Grid: %dx%d (%d barriers)",
                gridSize, gridSize, barrierCount));
    }
    
    /**
     * Shows a status message with the specified color.
     * Automatically clears after a delay.
     * 
     * @param message The message to show
     * @param color The color of the message
     */
    public void showStatusMessage(String message, Color color) {
        statusMessageLabel.setText(message);
        statusMessageLabel.setForeground(color);
        
        // Clear message after 3 seconds
        new Timer(3000, e -> {
            statusMessageLabel.setText("Ready");
            statusMessageLabel.setForeground(Color.BLUE);
        }).start();
    }
}
