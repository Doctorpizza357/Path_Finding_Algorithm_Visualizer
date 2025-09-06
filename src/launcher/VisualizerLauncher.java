package launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Launcher application that allows the user to choose between the old and new versions
 * of the Path Finding Algorithm Visualizer.
 */
public class VisualizerLauncher {
    
    /**
     * Main method that creates and displays the launcher GUI.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
    
    /**
     * Creates and displays the launcher GUI.
     */
    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Path Finding Visualizer Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen
        frame.setLocationRelativeTo(null);
        
        // Create the main panel with a BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the title label
        JLabel titleLabel = new JLabel("Path Finding Algorithm Visualizer", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Create the description label
        JLabel descLabel = new JLabel("<html><center>Choose which version of the application to launch:</center></html>", JLabel.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Create a panel for the title and description
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.CENTER);
        
        // Create the button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // Create the button for the original version
        JButton originalButton = new JButton("Original Version");
        originalButton.setFont(new Font("Arial", Font.BOLD, 16));
        originalButton.setToolTipText("Launch the original version of the visualizer");
        originalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        Class.forName("Main").getMethod("main", String[].class).invoke(null, (Object) new String[]{});
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error launching original version: " + ex.getMessage(), 
                            "Launch Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                });
            }
        });
        
        // Create the button for the enhanced version
        JButton enhancedButton = new JButton("Enhanced Version (with UX Improvements)");
        enhancedButton.setFont(new Font("Arial", Font.BOLD, 16));
        enhancedButton.setToolTipText("Launch the enhanced version with UX improvements");
        enhancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        Class.forName("main.Main").getMethod("main", String[].class).invoke(null, (Object) new String[]{});
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error launching enhanced version: " + ex.getMessage(), 
                            "Launch Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                });
            }
        });
        
        // Add the buttons to the button panel
        buttonPanel.add(originalButton);
        buttonPanel.add(enhancedButton);
        
        // Create a panel for feature comparison
        JPanel comparisonPanel = new JPanel(new BorderLayout());
        comparisonPanel.setBorder(BorderFactory.createTitledBorder("Features Comparison"));
        
        // Create comparison text
        JTextArea comparisonText = new JTextArea(
            "Original Version:\n" +
            "- Basic grid interaction with mouse clicks\n" +
            "- Simple controls\n" +
            "- Familiar interface\n\n" +
            "Enhanced Version:\n" +
            "- Interactive tools toolbar\n" +
            "- Drag-and-drop functionality\n" +
            "- Visual feedback and cursor changes\n" +
            "- Keyboard shortcuts (1-5 to switch tools)\n" +
            "- Zoom with Ctrl+Mouse wheel\n" +
            "- Scrollable grid for larger mazes"
        );
        comparisonText.setEditable(false);
        comparisonText.setFont(new Font("Arial", Font.PLAIN, 12));
        comparisonText.setBackground(new Color(240, 240, 240));
        comparisonPanel.add(new JScrollPane(comparisonText), BorderLayout.CENTER);
        
        // Add everything to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(comparisonPanel, BorderLayout.SOUTH);
        
        // Add the main panel to the frame
        frame.add(mainPanel);
        
        // Show the frame
        frame.setVisible(true);
    }
}
