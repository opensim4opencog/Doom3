package daxclr.doom;

/**
 * func_movers
 *
 *
 */
public interface IMover extends IEntity {


    /**
     * Stops any translational movement.
     */
    public void stopMoving();

    /**
     * Stops any rotational movement.
     */
    public void stopRotating();


    /**
     * Sets the movement speed. Set this speed before initiating a new move.
     * @param speed
     */
    public void speed(float speed);

    /**
     * Sets the movement time. Set this time before initiating a new move.
     * @param time
     */
    public void time(float time);

    /**
     * Sets the deceleration time. Set this deceleration time before initiating a new move.
     *
     * @param time
     */
    public void decelTime(float time);

    /**
     * Sets the acceleration time. Set this acceleration time before initiating a new move.
     *
     * @param time
     */
    public void accelTime(float time);

    /**
     * Initiates a translation to the position of an IEntity.
     * Uses the current speed/time and acceleration and deceleration settings.
     *
     * @param targetEntity
     */
    public void moveTo(IEntity targetEntity);

    /**
     * Initiates a translation to an absolute position.
     * Uses the current speed/time and acceleration and deceleration settings.
     *
     * @param pos
     */
    public void moveToPos(IVector pos);

    /**
     * Initiates a translation with the given distance in the given yaw direction.
     * Uses the current speed/time and acceleration and deceleration settings.
     *
     * @param angle
     * @param distance
     */
    public void move(float angle, float distance);

    /**
     * Initiates an acceleration to the given speed over the given time in seconds.
     *
     * @param speed
     * @param time
     */
    public void accelTo(float speed, float time);

    /**
     * Initiates a deceleration to the given speed over the given time in seconds.
     *
     * @param speed
     * @param time
     */
    public void decelTo(float speed, float time);

    /**
     * Initiates a rotation about the given axis by decreasing the current angle towards the given angle.
     * Uses the current speed/time and acceleration and deceleration settings.
     *
     * @param axis
     * @param angle
     */
    public void rotateDownTo(float axis, float angle);

    /**
     * Initiates a rotation about the given axis by increasing the current angle towards the given angle.
     * Uses the current speed/time and acceleration and deceleration settings.
     *
     * @param axis
     * @param angle
     */
    public void rotateUpTo(float axis, float angle);

    /**
     * Initiates a rotation towards the given Euler angles.
     * Uses the current speed/time and acceleration and deceleration settings.
     *
     * @param angles
     */
    public void rotateTo(IVector angles);

    /**
     * Initiates a rotation with the given angular speed.
     * Uses the current speed/time and acceleration and deceleration settings.
     *
     * @param angleSpeed
     */
    public void rotate(IVector angleSpeed);

    /**
     * Initiates a rotation towards the current angles plus the given Euler angles.
     * Uses the current speed/time and acceleration and deceleration settings.
     *
     * @param angles
     */
    public void rotateOnce(IVector angles);

    /**
     * Initiates a translation back and forth along the given IdVector with the given speed and sphase.
     *
     * @param speed
     * @param phase
     * @param distance
     */
    public void bob(float speed, float phase, IVector distance);

    /**
     * Initiates a rotation back and forth along the given angles with the given speed and phase.
     *
     * @param speed
     * @param phase
     * @param angles
     */
    public void sway(float speed, float phase, IVector angles);

    /**
     * Opens the renderer portal associated with this mover.
     */
    public void openPortal();

    /**
     * Closes the renderer portal associated with this mover.
     */
    public void closePortal();

    /**
     * Sets the sound to be played when the mover accelerates.
     *
     * @param sound
     */
    public void accelSound(String sound);

    /**
     * Sets the sound to be played when the mover decelerates.
     *
     * @param sound
     */
    public void decelSound(String sound);

    /**
     * Sets the sound to be played when the moving.
     *
     * @param sound
     */
    public void moveSound(String sound);

    /**
     * Enables aligning the mover with the spline direction.
     */
    public void enableSplineAngles();

    /**
     * Disables aligning the mover with the spline direction.
     */
    public void disableSplineAngles();

    /**
     * Subtracts the initial spline angles to maintain the initial orientation of the mover.
     */
    public void removeInitialSplineAngles();

    /**
     * Starts moving along a spline stored on the given IEntity.
     *
     * @param spline
     */
    public void startSpline(IEntity spline);

    /**
     * Stops moving along a spline.
     */
    public void stopSpline();

    /**
     * Returns true if a mover is moving
     *
     * @return
     */
    public float isMoving();

    /**
     * Returns true if a mover is rotating
     *
     * @return
     */
    public float isRotating();


    public String getMoveStatusString();
}
