package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that displays a color legend for the grid.
 */
public class LegendPanel extends JPanel {
    
    /**
     * Creates a new legend panel.
     */
    public LegendPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Legend"));
        
        addLegendItem(Color.BLUE, "Start Point");
        addLegendItem(Color.RED, "End Point");
        addLegendItem(Color.BLACK, "Barrier");
        addLegendItem(Color.GREEN, "Final Path");
        addLegendItem(Color.YELLOW, "Explored Area");
    }
    
    /**
     * Adds a legend item with a color and description.
     * 
     * @param color The color for the legend item
     * @param text The description text
     */
    private void addLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorLabel = new JLabel("   ");
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        item.add(colorLabel);
        item.add(new JLabel(text));
        add(item);
    }
}
