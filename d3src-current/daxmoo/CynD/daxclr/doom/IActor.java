package daxclr.doom;

/**
 * actors (players and AI)
 */
public interface IActor extends IMover {

    /**
     * Moves the constraint with the given name that binds this IEntity to another IEntity.
     *
     * @param constraintName
     * @param position
     */
    public void SetConstraintPosition(String constraintName, IVector position);

    /**
     * Enables eye focus.
     */
    public void enableEyeFocus();

    /**
     * Disables eye focus.
     */
    public void disableEyeFocus();

    /**
     * Changes to left foot and plays footstep sound.
     */
    public void leftFoot();

    /**
     * Changes to right foot and plays footstep sound.
     */
    public void rightFoot();

    /**
     * Stops the animation currently playing on the given channel over the given number of frames.
     *
     * @param channel
     * @param frames
     */
    public void stopAnim(float channel, float frames);

    /**
     * Plays the given animation on the given channel. Returns false if anim doesn't exist.
     *
     * @param channel
     * @param animName
     *

     */
    public float playAnim(float channel, String animName);

    /**
     * Continuously repeats the given animation on the given channel. Returns false if anim doesn't exist.
     *
     * @param channel
     * @param animName
     *

     */
    public float playCycle(float channel, String animName);

    /**
     * Plays the given idle animation on the given channel. Returns false if anim doesn't exist.
     *
     * @param channel
     * @param animName
     *

     */
    public float idleAnim(float channel, String animName);

    /**
     * sets the blend amount on multi-point anims.
     *
     * @param channel
     * @param animindex
     * @param weight
     */
    public void setSyncedAnimWeight(float channel, float animindex,float weight);

    /**
     * Sets the number of frames to blend between animations on the given channel.
     *
     * @param channel
     * @param blendFrame
     */
    public void setBlendFrames(float channel, float blendFrame);

    /**
     * Returns  the number of frames to blend between animations on the given channel.
     *
     * @param channel
     *

     */
    public float getBlendFrames(float channel);

    /**
     * Sets a new animation state script function for the given channel.
     *
     * @param channel
     * @param stateFunction
     * @param blendFrame
     */
    public void animState(float channel, String stateFunction,
                          float blendFrame);

    /**
     * Returns  the name of the current animation state script function used for the given channel.
     *
     * @param channel
     *

     */
    public String getAnimState(float channel);

    /**
     *
     * @param channel
     * @param stateFunc
     *
     * Returns true if the given animation state script function is currently used for the given channel.
     */
    public float inAnimState(float channel, String stateFunc);

    /**
     * Finishes the given wait action.
     *
     * @param action
     */
    public void finishAction(String action);

    /**
     * Returns  true if the animation playing on the given channel
     * is completed considering a number of blend frames.
     *
     * @param channel
     * @param blendOutFrames
     *

     */
    public float animDone(float channel, float blendOutFrames);

    /**
     * Disables the animation currently playing on the given channel and syncs
     * the animation with the animation of the nearest animating channel.
     *
     * @param channel
     */
    public void overrideAnim(float channel);

    /**
     * Prevents any pain animation from being played for the given time in seconds.
     *
     * @param duration
     */
    public void preventPain(float duration);

    /**
     * Enables animation on the given channel.
     *
     * @param channel
     * @param blendFrames
     */
    public void enableAnim(float channel, float blendFrames);

    /**
     * Disables pain animations.
     */
    public void disablePain();

    /**
     * Enables pain animations.
     */
    public void enablePain();

    /**
     * Returns  the name of the pain animation.
     *

     */
    public String getPainAnim();

    /**
     * Sets a String which is placed in front of any animation names.
     *
     * @param prefix
     */
    public void setAnimPrefix(String prefix);

    /**
     * Returns  true when an IEntity has a specific animation.
     *
     * @param channel
     * @param animName
     *

     */
    public float hasAnim(float channel, String animName);

    /**
     * Ensures that the animation exists and causes an error if it doesn't.
     *
     * @param channel
     * @param animName
     */
    public void checkAnim(float channel, String animName);

    /**
     * Chooses a random anim and returns the name. Useful for doing move tests on anims.
     *
     * @param channel
     * @param animName
     *

     */
    public String chooseAnim(float channel, String animName);

    /**
     * Returns  the length of the anim in seconds. If the IEntity has multiple anims with animName,
     * length may not match the anim that is played. Use chooseAnim to get a non-random anim
     * and pass that String into animLength.
     *
     * @param channel
     * @param animName
     *
     */
    public float animLength(float channel, String animName);

    /**
     * Returns  the distance that the anim travels. If the IEntity has multiple anims with animName,
     * the distance may not match the anim that is played. Use chooseAnim to get a non-random anim
     * and pass that String into animDistance.
     *
     * @param channel
     * @param animName
     *

     */
    public float animDistance(float channel, String animName);

    /**
     * Returns  true if the actor has one or more enemies.
     *

     */
    public float hasEnemies();

    /**
     * Returns  the next enemy the actor has acquired.
     *
     * @param lastEnemy
     *

     */
    public IEntity nextEnemy(IEntity lastEnemy);

    /**
     * Returns  the enemy closest to the given location.
     *
     * @param point
     *

     */
    public IEntity closestEnemyToPoint(IVector point);

    /**
     * sets the next state and waits until thread exits, or a frame delay before calling it. handy for setting the state in the constructor.
     *
     * @param stateFunc
     */
    public void setNextState(String stateFunc);

    /**
     * sets the next state and goes to it immediately
     *
     * @param stateFunc
     */
    public void setState(String stateFunc);

    public String getState();

    /**
     * returns the IEntity used for the character's head, if it has one.
     *

     */
    public IEntity getHead();
}
