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
            JFrame frame = new JFrame("Path Finding Algorithm Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new PathFinderController());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
