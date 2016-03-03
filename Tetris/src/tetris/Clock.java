package tetris;

/**
 * Class Clock
 * @authors Brendan Jones, arrangements by: luisfelipesv y melytc
 *
 * Luis Felipe Salazar A00817158 Melissa Janet Treviño A00816715
 *
 * 1/MAR/16
 * @version 2.0
 *
 * The {@code Clock} class is responsible for tracking the number of cycles that
 * have elapsed over time.
 */
public class Clock {

    // The number of milliseconds that make up one cycle.
    private float fMillisPerCycle;

    // The last time that the clock was updated 
    // (used for calculating the delta time).
    private long lLastUpdate;

    // The number of cycles that have elapsed and have not yet been polled.
    private int iElapsedCycles;

    // The amount of excess time towards the next elapsed cycle.
    private float fExcessCycles;

    // Whether or not the clock is paused.
    private boolean bIsPaused;

    /**
     * Constructor that creates a new clock and sets it's cycles-per-second.
     *
     * @param fCyclesPerSecond is the number of cycles that elapse per second.
     */
    public Clock(float fCyclesPerSecond) {
        setCyclesPerSecond(fCyclesPerSecond);
        reset();
    }

    /**
     * Sets the number of cycles that elapse per second.
     *
     * @param fCyclesPerSecond is the number of cycles per second.
     */
    public void setCyclesPerSecond(float fCyclesPerSecond) {
        this.fMillisPerCycle = (1.0f / fCyclesPerSecond) * 1000;
    }

    /**
     * Method that resets the clock stats. 
     * Elapsed cycles and cycle excess will be reset
     * to 0, the last update time will be reset to the current time, and the
     * paused flag will be set to false.
     */
    public void reset() {
        // Elapsed cycles and cycle excess will be reset to 0.
        this.iElapsedCycles = 0;
        this.fExcessCycles = 0.0f;
        
        // The last update time will be reset to the current time.
        this.lLastUpdate = getCurrentTime();
        
        // The paused flag will be set to false.
        this.bIsPaused = false;
    }

    /**
     * Method that updates the clock stats. 
     * The number of elapsed cycles, as well as the
     * cycle excess will be calculated only if the clock is not paused. This
     * method should be called every frame even when paused to prevent any nasty
     * surprises with the delta time.
     */
    public void update() {
        // Get the current time and calculate the delta time.
        long currUpdate = getCurrentTime();
        float delta = (float) (currUpdate - lLastUpdate) + fExcessCycles;

        // Update the number of elapsed and excess ticks if we're not paused.
        if (!bIsPaused) {
            this.iElapsedCycles += (int) Math.floor(delta / fMillisPerCycle);
            this.fExcessCycles = delta % fMillisPerCycle;
        }

        // Set the last update time for the next update cycle.
        this.lLastUpdate = currUpdate;
    }

    /**
     * Modifier method that pauses or unpauses the clock. 
     *
     * @param paused is a <code>boolean</code> that defines 
     * if the clock is paused.
     */
    public void setPaused(boolean paused) {
        this.bIsPaused = paused;
    }

    /**
     * Modifier method that checks to see if the clock is currently paused.
     *
     * @return A <code>boolean</code> value to know if the clock is paused.
     */
    public boolean bIsPaused() {
        return bIsPaused;
    }

    /**
     * Method that checks to see if a cycle has elapsed for this clock yet. 
     * If so, the number of elapsed cycles will be decremented by one.
     *
     * @return A <code>boolean</code> value to know whether or not
     * a cycle has elapsed.
     * @see peekElapsedCycle
     */
    public boolean hasElapsedCycle() {
        if (iElapsedCycles > 0) {
            this.iElapsedCycles--;
            return true;
        }
        return false;
    }

    /**
     * Method that checks to see if a cycle has elapsed for this clock yet. 
     * Unlike {@code hasElapsedCycle}, 
     * the number of cycles will not be decremented if
     * the number of elapsed cycles is greater than 0.
     *
     * @return A <code>boolean</code> value to know whether or not
     * a cycle has elapsed.
     * @see hasElapsedCycle
     */
    public boolean peekElapsedCycle() {
        return (iElapsedCycles > 0);
    }

    /**
     * Method that calculates the current time in milliseconds 
     * using the computer's high resolution clock. 
     * This is much more reliable than
     * {@code System.getCurrentTimeMillis()}, and quicker than
     * {@code System.nanoTime()}.
     *
     * @return A <code>long</code> value with the current time in milliseconds.
     */
    private static final long getCurrentTime() {
        return (System.nanoTime() / 1000000L);
    }
}
