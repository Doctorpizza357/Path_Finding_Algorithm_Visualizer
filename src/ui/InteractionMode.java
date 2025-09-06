package ui;

/**
 * Enum representing different interaction modes for the grid.
 */
public enum InteractionMode {
    /**
     * Mode for placing the start point.
     */
    PLACE_START,
    
    /**
     * Mode for placing the end point.
     */
    PLACE_END,
    
    /**
     * Mode for adding barriers.
     */
    ADD_BARRIERS,
    
    /**
     * Mode for removing barriers.
     */
    REMOVE_BARRIERS,
    
    /**
     * Mode for moving the start or end point.
     */
    MOVE
}
