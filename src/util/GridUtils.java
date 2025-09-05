package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Utility methods for grid operations like screenshots and image handling.
 */
public class GridUtils {
    
    /**
     * Takes a screenshot of a component.
     * 
     * @param component The component to capture
     * @param outputFile The file to save the screenshot to
     * @return True if successful, false otherwise
     */
    public static boolean takeScreenshot(Component component, File outputFile) {
        try {
            Point location = component.getLocationOnScreen();
            Dimension size = component.getSize();
            Rectangle gridRectangle = new Rectangle(location.x, location.y, size.width, size.height);
            
            Robot robot = new Robot();
            BufferedImage gridImage = robot.createScreenCapture(gridRectangle);
            
            ImageIO.write(gridImage, "png", outputFile);
            System.out.println("Screenshot saved to: " + outputFile.getAbsolutePath());
            return true;
        } catch (AWTException | IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Determines if two colors are similar within a threshold.
     * 
     * @param c1 The first color
     * @param c2 The second color
     * @return True if the colors are similar
     */
    public static boolean isColorSimilar(Color c1, Color c2) {
        int threshold = 100;
        int diffRed = Math.abs(c1.getRed() - c2.getRed());
        int diffGreen = Math.abs(c1.getGreen() - c2.getGreen());
        int diffBlue = Math.abs(c1.getBlue() - c2.getBlue());
        return diffRed + diffGreen + diffBlue < threshold;
    }
    
    /**
     * Checks if a grid size would fit on the screen.
     * 
     * @param gridSize The grid size to check
     * @param buttonSize The size of each button in the grid
     * @return True if the grid would fit on screen
     */
    public static boolean isGridSizeViable(int gridSize, int buttonSize) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int totalWidth = gridSize * buttonSize;
        int totalHeight = gridSize * buttonSize;
        
        int margin = 100;
        return totalWidth <= screenSize.width - margin && totalHeight <= screenSize.height - margin;
    }
}
