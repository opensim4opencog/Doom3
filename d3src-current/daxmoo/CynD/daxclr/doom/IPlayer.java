package daxclr.doom;

/**
 * players
 */
public interface IPlayer extends IActor {

    /**
     * Returns the button state from the current user command.
     *
     * @return
     */
    public float getButtons();

    /**
     * Returns the movement relative to the player's view angles from the current user command.
     * vector_x = forward, vector_y = right, vector_z = up
     *
     * @return the movement relative to the player's view angles from the current user command.
     */
    public IVector getMove();

    /**
     * Returns the player view angles.
     *
     * @return the player view angles.
     */
    public IVector getViewAngles();

    /**
     * Enables the player weapon.
     */
    public void enableWeapon();

    /**
     * Lowers and disables the player weapon.
     */
    public void disableWeapon();

    /**
     * Returns "weaponX" where X is the number of the weapon the player is currently holding.
     *
     * @return
     */
    public String getCurrentWeapon();

    /**
     * Returns "weaponX" where X is the number of the weapon the player was previously holding.
     *
     * @return
     */
    public String getPreviousWeapon();

    /**
     * Selects the weapon the player is holding.
     *
     * @param weapon
     */
    public void selectWeapon(String weapon);

    /**
     * Returns the IEntity for the player's weapon
     *
     * @return
     */
    public IEntity getWeaponEntity();

    /**
     * Opens the player's PDA.
     */
    public void openPDA();

    /**
     * Returns true if the player has the PDA open.
     *
     * @return
     */
    public float inPDA();

// Returns the level of a specific weapon.
//public void setSpecWepLevel(float weptype, float specwep);

// Increments the level of a specific weapon by 1.
//public float getSpecWepLevel(float weptype, float specwep);

//public void setGenWepLevel(float weptype);

//public float getGenWepLevel(float weptype);

//public void setSubskillPoints(float weptype, float amount);

//public float getSubskillPoints(float weptype);


}
