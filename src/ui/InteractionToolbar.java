package ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Toolbar containing buttons for different interaction modes.
 */
public class InteractionToolbar extends JPanel {
    private final ButtonGroup toolGroup = new ButtonGroup();
    private final JToggleButton startButton;
    private final JToggleButton endButton;
    private final JToggleButton barrierButton;
    private final JToggleButton eraseButton;
    private final JToggleButton moveButton;
    private final JLabel currentModeLabel;
    
    /**
     * Creates a new interaction toolbar.
     * 
     * @param modeConsumer Consumer that will be called when the mode changes
     */
    public InteractionToolbar(Consumer<InteractionMode> modeConsumer) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createTitledBorder("Tools"));
        
        startButton = createToggleButton("Start Point", "Place starting point", InteractionMode.PLACE_START, modeConsumer);
        endButton = createToggleButton("End Point", "Place ending point", InteractionMode.PLACE_END, modeConsumer);
        barrierButton = createToggleButton("Add Barriers", "Add barriers", InteractionMode.ADD_BARRIERS, modeConsumer);
        eraseButton = createToggleButton("Erase", "Remove barriers", InteractionMode.REMOVE_BARRIERS, modeConsumer);
        moveButton = createToggleButton("Move", "Move start/end points", InteractionMode.MOVE, modeConsumer);
        
        // Pre-select the start button
        startButton.setSelected(true);
        
        // Add current mode label
        currentModeLabel = new JLabel("Current mode: Place Start Point");
        currentModeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        // Add components to panel
        add(new JLabel("Select mode: "));
        add(startButton);
        add(endButton);
        add(barrierButton);
        add(eraseButton);
        add(moveButton);
        add(Box.createHorizontalStrut(20));
        add(currentModeLabel);
    }
    
    /**
     * Creates a toggle button for a specific interaction mode.
     * 
     * @param text Button text
     * @param tooltip Button tooltip
     * @param mode The interaction mode
     * @param modeConsumer Consumer that will be called when the mode changes
     * @return The created toggle button
     */
    private JToggleButton createToggleButton(String text, String tooltip, InteractionMode mode, Consumer<InteractionMode> modeConsumer) {
        JToggleButton button = new JToggleButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(e -> {
            modeConsumer.accept(mode);
            currentModeLabel.setText("Current mode: " + text);
        });
        toolGroup.add(button);
        return button;
    }
    
    /**
     * Programmatically sets the current interaction mode.
     * 
     * @param mode The interaction mode to set
     */
    public void setMode(InteractionMode mode) {
        switch (mode) {
            case PLACE_START:
                startButton.doClick();
                break;
            case PLACE_END:
                endButton.doClick();
                break;
            case ADD_BARRIERS:
                barrierButton.doClick();
                break;
            case REMOVE_BARRIERS:
                eraseButton.doClick();
                break;
            case MOVE:
                moveButton.doClick();
                break;
        }
    }
}
