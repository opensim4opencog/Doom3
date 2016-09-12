package daxclr.doom;


/**
 * doors
 */
public interface IDoor extends IMover {

    /**
     * Enables the door.
     */
    public void enable();

    /**
     * Disables the door.
     */
    public void disable();

    /**
     * Opens the door.
     */
    public void open();

    /**
     * Closes the door.
     */
    public void close();

    /**
     * Locks or unlocks the door.
     *
     * @param locked
     */
    public void lock(float locked);

    /**
     * Returns true if the door is open.
     *
     * @return
     */
    public boolean isOpen();

    /**
     * Returns true if the door is locked.
     *
     * @return
     */
    public float isLocked();
}
