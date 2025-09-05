package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that displays complexity analysis for the pathfinding algorithm.
 */
public class AnalysisPanel extends JPanel {
    private final JProgressBar complexityBar;
    private final JProgressBar efficiencyBar;
    
    /**
     * Creates a new analysis panel.
     */
    public AnalysisPanel() {
        setLayout(new GridLayout(2, 1));
        setBorder(BorderFactory.createTitledBorder("Path Analysis"));
        
        complexityBar = new JProgressBar(0, 100);
        complexityBar.setStringPainted(true);
        complexityBar.setString("Optimality");
        
        efficiencyBar = new JProgressBar(0, 100);
        efficiencyBar.setStringPainted(true);
        efficiencyBar.setString("Efficiency");
        
        add(complexityBar);
        add(efficiencyBar);
    }
    
    /**
     * Updates the complexity indicators with new values.
     * 
     * @param optimality The optimality percentage (0-100)
     * @param efficiency The efficiency percentage (0-100)
     */
    public void updateIndicators(int optimality, int efficiency) {
        complexityBar.setValue(optimality);
        efficiencyBar.setValue(efficiency);
    }
}
