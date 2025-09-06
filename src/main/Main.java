package main;

import controller.PathFinderController;

import javax.swing.*;

/**
 * Main application class for the Path Finding Algorithm Visualizer.
 * Creates and initializes the main application window.
 */
public class Main {
    /**
     * Application entry point.
     * Creates and displays the main application window.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for better appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fall back to default look and feel
                e.printStackTrace();
            }
            
            // Create main frame
            JFrame frame = new JFrame("Path Finding Algorithm Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Create controller
            PathFinderController controller = new PathFinderController();
            
            // Add controller to frame
            frame.getContentPane().add(controller);
            
            // Show frame
            frame.pack();
            frame.setLocationRelativeTo(null);
            //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}
