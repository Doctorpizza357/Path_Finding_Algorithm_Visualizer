package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Utility class for zoom functionality.
 */
public class ZoomHandler implements MouseWheelListener {
    private final JPanel gridPanel;
    private final JScrollPane scrollPane;
    
    private int zoomLevel = 100; // 100% is the default zoom
    private final int minZoom = 50; // 50% minimum zoom
    private final int maxZoom = 200; // 200% maximum zoom
    private final int zoomStep = 10; // Zoom in/out by 10% per wheel click
    
    /**
     * Creates a new zoom handler.
     * 
     * @param gridPanel The panel containing the grid
     * @param scrollPane The scroll pane containing the grid panel
     */
    public ZoomHandler(JPanel gridPanel, JScrollPane scrollPane) {
        this.gridPanel = gridPanel;
        this.scrollPane = scrollPane;
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            // Determine zoom direction
            int rotation = e.getWheelRotation();
            
            // Update zoom level
            if (rotation < 0) {
                // Zoom in
                zoomLevel = Math.min(zoomLevel + zoomStep, maxZoom);
            } else {
                // Zoom out
                zoomLevel = Math.max(zoomLevel - zoomStep, minZoom);
            }
            
            // Calculate the point where the mouse is pointing
            Point viewPoint = e.getPoint();
            
            // Convert to view coordinates
            Point viewPosition = scrollPane.getViewport().getViewPosition();
            
            // Calculate the position of the mouse relative to the view
            double relativeX = (viewPosition.x + viewPoint.x) / (double) gridPanel.getWidth();
            double relativeY = (viewPosition.y + viewPoint.y) / (double) gridPanel.getHeight();
            
            // Apply zoom
            applyZoom();
            
            // Calculate new position to keep the mouse over the same logical position
            int newX = (int) (relativeX * gridPanel.getWidth()) - viewPoint.x;
            int newY = (int) (relativeY * gridPanel.getHeight()) - viewPoint.y;
            
            // Ensure the new position is within bounds
            newX = Math.max(0, Math.min(newX, gridPanel.getWidth() - scrollPane.getViewport().getWidth()));
            newY = Math.max(0, Math.min(newY, gridPanel.getHeight() - scrollPane.getViewport().getHeight()));
            
            // Set the new view position
            scrollPane.getViewport().setViewPosition(new Point(newX, newY));
            
            // Consume the event to prevent scrolling
            e.consume();
        }
    }
    
    /**
     * Applies the current zoom level to the grid panel.
     */
    private void applyZoom() {
        // Calculate scale factor
        double scale = zoomLevel / 100.0;
        
        // Calculate new button size
        int baseButtonSize = 30; // Base size at 100% zoom
        int newButtonSize = (int) (baseButtonSize * scale);
        
        // Set new size for each button
        Component[] components = gridPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setPreferredSize(new Dimension(newButtonSize, newButtonSize));
            }
        }
        
        // Update the panel layout
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    /**
     * Gets the current zoom level.
     * 
     * @return The current zoom level as a percentage
     */
    public int getZoomLevel() {
        return zoomLevel;
    }
}
