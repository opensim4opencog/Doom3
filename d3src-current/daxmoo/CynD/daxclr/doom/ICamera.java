package daxclr.doom;

/**
 * cameras
 */
public interface ICamera extends IEntity {
    /**
     * Starts a spline or anim camera moving.
     */
    public void start();

    
    /**
     * Stops a spline or anim camera moving.
     */
    public void stop();

}
