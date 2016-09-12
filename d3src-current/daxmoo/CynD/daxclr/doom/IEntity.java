package daxclr.doom;

public interface IEntity extends IClass {

    public String getTypeDef();
    public int getEntnum();

//Entity
    void   setGui(float guiNum, String gui);
    void   precacheGui(String gui);
    String getGuiParm(float guiNum, String key);
    float  getGuiParmFloat(float guiNum, String key);
    void   guiNamedEvent(float guiNum, String event);

//Actor
    void   setDamageGroupScale(String groupName, float scale);
    void   setDamageGroupScaleAll(float scale);
    float  getDamageGroupScale(String goupName);
    void   setDamageCap(float cap);
    void   setWaitState(String waitState);
    String getWaitState();

//AI
    void   moveToPositionDirect( IVector pos );
    void   avoidObstacles( float avoid );


    //  Make sure the entity is sitting upright
    public void upright();

    //  Checks to see if this entity is close to another
    //public float isCloseTo(IEntity other);
    //  Teleport this entity to another location
    public void teleport(IVector loc);

    // rename
    public void rename(String name);

    /**
     * Removes this IEntity from the game.
     */
    public void remove();

    /**
     * Returns  the name of this IEntity.
     *

     */
    public String getName();

    /**
     * Sets the name of this IEntity.
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Activates this IEntity as if it was activated by a trigger.
     * Activator is the IEntity that caused the action (usually the player).
     *
     * @param activator
     */
    public void activate(IEntity activator);

    /**
     * Causes this IEntity to activate all it's targets. Similar to how a trigger activates entities.
     * Activator is the IEntity that caused the action (usually the player).
     *
     * @param activator
     */
    public void activateTargets(IEntity activator);

    /**
     * Returns  the number of entities this IEntity has targeted.
     *

     */
    public float numTargets();

    /**
     * Returns  the requested target IEntity.
     *
     * @param num
     *

     */
    public IEntity getTarget(float num);

    /**
     * Returns  a random targeted IEntity. Pass in an IEntity name to skip that IEntity.
     *
     * @param ignoreName
     *

     */
    public IEntity randomTarget(String ignoreName);

    /**
     * Fixes this IEntity's position (@see IVector) and orientation relative to another IEntity,
     * such that when the master IEntity moves, so does thisentity.
     *
     * @param master
     */
    public void bind(IEntity master);

    /**
     * Fixes this IEntity's position (@see IVector) (but not orientation) relative to another IEntity,
     * such that when the master IEntity moves, so does this IEntity.
     *
     * @param master
     */
    public void bindPosition(IEntity master);

    /**
     * Fixes this IEntity's position (@see IVector) and orientation relative to a bone on another IEntity,
     * such that when the master's bone moves, so does this IEntity.
     *
     * @param master
     * @param boneName
     * @param rotateWithMaster
     */
    public void bindToJoint(IEntity master, String boneName,
                            float rotateWithMaster);

    /**
     * Detaches this IEntity from its master.
     */
    public void unbind();

    /**
     * Removes all attached entities from the game.
     */
    public void removeBinds();

    /**
     * Sets the owner of this IEntity. Entity's will never collide with their owner.
     *
     * @param owner
     */
    public void setOwner(IEntity owner);

    /**
     * Sets the model this IEntity uses.
     *
     * @param modelName
     */
    public void setModel(String modelName);

    /**
     * Gets the model this IEntity uses.
     *
     */
    public String getModel();

    /**
     * Gets the skin this IEntity uses.
     *
     */
    public String getSkin();

    /**
     * Sets the skin this IEntity uses. Set to "" to turn off the skin.
     *
     * @param skinName
     */
    public void setSkin(String skinName);

    /**
     * Returns  the current worldspace position (@see IVector) of this IEntity (regardless of any bind parent).
     *

     */
    public IVector getWorldOrigin();

    /**
     * Sets the current position (@see IVector) of this IEntity (regardless of any bind parent).
     *
     * @param origin
     */
    public void setWorldOrigin(IVector origin);

    /**
     * Returns  the current position (@see IVector) of this IEntity (relative to bind parent if any).
     *

     */
    public IVector getOrigin();

    /**
     * Sets the current position (@see IVector) of this IEntity (relative to it's bind parent if any).
     *
     * @param origin
     */
    public void setOrigin(IVector origin);

    /**
     * Returns  the current orientation of this IEntity (relative to bind parent if any).
     *

     */
    public IVector getAngles();

    /**
     * Sets the current orientation of this IEntity (relative to bind parent if any).
     *
     * @param angles
     */
    public void setAngles(IVector angles);

    /**
     * Returns the current linear velocity of this IEntity. The linear velocity of a physics
     * object is a IVector that defines the translation of the center of mass in units per second.
     *

     */
    public IVector getLinearVelocity();

    /**
     * Sets the current linear velocity of this IEntity in units per second. The linear velocity of
     * a physics object is a IVector that defines the translation of the center of mass in units per second.
     *
     * @param velocity
     */
    public void setLinearVelocity(IVector velocity);

    /**
     * Returns the current angular velocity of this IEntity. The angular velocity of
     * a physics object is a IVector that passes through the center of mass. The
     * direction of this IVector defines the axis of rotation and the magnitude
     * defines the rate of rotation about the axis in radians per second.
     *

     */
    public IVector getAngularVelocity();

    /**
     * Sets the current angular velocity of this IEntity. The angular velocity of
     * a physics object is a IVector that passes through the center of mass. The
     * direction of this IVector defines the axis of rotation and the magnitude
     * defines the rate of rotation about the axis in radians per second.
     *
     * @param velocity
     */
    public void setAngularVelocity(IVector velocity);

    /**
     * Returns the size of this IEntity's bounding box.
     *
     */
    public IVector getSize();

    /**
     * Sets the size of this IEntity's bounding box.
     *
     * @param min
     * @param max
     */
    public void setSize(IVector min, IVector max);

    /**
     * Returns the minimum corner of this IEntity's bounding box.
     *

     */
    public IVector getMins();

    /**
     * Returns the maximum corner of this IEntity's bounding box.
     *

     */
    public IVector getMaxs();

    /**
     * Returns true if the IEntity's model is invisible.
     *

     */
    public float isHidden();

    /**
     * Makes this IEntity invisible.
     */
    public void hide();

    /**
     * Makes this IEntity visible if it has a model.
     */
    public void show();

    /**
     * Returns  true if this IEntity touches the other IEntity.
     *
     * @param other
     *

     */
    public float touches(IEntity other);

    /**
     * Disables the callback function on the specified signal.
     *
     * @param signalNum
     */
    public void clearSignal(float signalNum);

    /**
     * Returns the value of the specified shader parm.
     *
     * @param parm
     *

     */
    public float getShaderParm(float parm);

    /**
     * Sets the value of the specified shader parm.
     *
     * @param parm (red=0, green, blue, and alpha=3 respectively).
     * @param value
     */
    public void setShaderParm(float parm, float value);

    /**
     * Sets shader parms Red, Green, Blue, and Alpha (red, green, blue, and alpha respectively).
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void setShaderParms(float red, float green, float blue,
                               float alpha);

    /**
     * Sets the RGB color of this IEntity (shader parms Red, Green, Blue).
     *
     * @param red
     * @param green
     * @param blue
     */
    public void setColor(float red, float green, float blue);

    /**
     * Returns the color of this IEntity (shader parms Red, Green, Blue).
     *

     */
    public IVector getColor();

    /**
     * ensure the specified sound shader is loaded by the system. prevents cache hits when playing sound shaders.
     *
     * @param shaderName
     */
    public void cacheSoundShader(String shaderName);

    /**
     * Plays a specific sound shader on the channel and returns the length of the sound in
     * seconds. This is not the prefered method of playing a sound since you must ensure
     * that the sound is loaded.
     *
     * @param shaderName
     * @param channel
     *

     */
    public float startSoundShader(String shaderName, float channel);

    /**
     * Stops a specific sound shader on the channel.
     *
     * @param channel
     * @param netSync
     */
    public void stopSound(float channel, float netSync);

    /**
     * Plays the sound specified by the snd_* key/value pair on the channel and returns
     * the length of the sound. This is the preferred method for playing sounds on an
     * IEntity since it ensures that the sound is precached.
     *
     * @param sound
     * @param channel
     * @param netSync
     *

     */
    public float startSound(String sound, float channel, float netSync);

    /**
     * Fades the sound on this IEntity to a new level over a period of time. Use SND_CHANNEL_ANY for all currently playing sounds.
     *
     * @param channel
     * @param newLevel
     * @param fadeTime
     */
    public void fadeSound(float channel, float newLevel, float fadeTime);

    /**
     * Sets a parameter on this IEntity's GUI.
     *
     * @param key
     * @param value
     */
    public void setGuiParm(String key, String value);

    /**
     * Sets a parameter on this IEntity's GUI.
     *
     * @param key
     * @param value
     */
    public void setGuiFloat(String key, float value);

    /**
     * searches for the name of a spawn arg that matches the prefix. for example,
     * passing in "attack_target" matches "attack_target1", "attack_targetx", "attack_target_enemy",
     * etc. The returned String is the name of the key which can then be passed into
     * functions like getKey() to lookup the value of that spawn arg. This
     * is usefull for when you have multiple values to look up, like when you
     * target multiple objects. To find the next matching key, pass in the previous
     * result and the next key returned will be the first one that matches after
     * the previous result. pass in "" to get the first match. returns "" when no
     * more keys match. Note to coders: this is the same as MatchPrefix in the game code.
     *
     * @param prefix
     * @param lastMatch
     *

     */
    public String getNextKey(String prefix, String lastMatch);

    /**
     * Sets a key on this IEntity's spawn args. Note that most spawn args are evaluated when
     * this IEntity spawns in, so this will not change the IEntity's behavior in most cases.
     * This is chiefly for saving data the script needs in an IEntity for later retrieval.
     *
     * @param key
     * @param value
     */
    public void setKey(String key, String value);

    /**
     * Returns  the value of a specific spawn arg.
     *
     * @param key
     *

     */
    public String getKey(String key);

    /**
     * Returns  the integer value of a specific spawn arg.
     *
     * @param key
     *

     */
    public float getIntKey(String key);

    /**
     * Returns  the floating point value of a specific spawn arg.
     *
     * @param key
     *

     */
    public float getFloatKey(String key);

    /**
     * Returns  the IVector value of a specific spawn arg.
     *
     * @param key
     *

     */
    public IVector getVectorKey(String key);

    /**
     * Returns  the IEntity specified by the spawn arg.
     *
     * @param key
     *

     */
    public IEntity getEntityKey(String key);

    /**
     * Returns  this IEntity to the position (@see IVector) stored in the "origin" spawn arg.
     * This is the position (@see IVector) the IEntity was spawned in unless the "origin" key is changed.
     * Note that there is no guarantee that the IEntity won't be stuck in another IEntity
     * when moved, so care should be taken to make sure that isn't possible.
     */
    public void restorePosition();

    /**
     * Returns  the distance of this IEntity to another IEntity.
     *
     * @param other
     *

     */
    public float distanceTo(IEntity other);

    /**
     * Returns  the distance of this IEntity to a point.
     *
     * @param point
     *

     */
    public float distanceToPoint(IVector point);

    /**
     * Starts an FX on this IEntity.
     *
     * @param fx
     */
    public void startFx(String fx);

    /**
     * Suspends execution of current thread for one game frame.
     */
    public void waitFrame();

    /**
     * Suspends execution of the current thread for the given number of seconds.
     *
     * @param time
     */
    public void wait(float time);

    /**
     * Returns true if an IEntity's script object has a specific function
     *
     * @param functionName
     *

     */
    public float hasFunction(String functionName);

    /**
     * calls a function on an IEntity's script object
     *
     * @param functionName
     */
    public void callFunction(String functionName);

    /**
     * enables or prevents an IEntity from going dormant
     *
     * @param enable
     */
    public void setNeverDormant(float enable);


    /**
     * func_forcefield
     *
     *
     *
     * Turns the forcefield on and off.
     */
    public void Toggle();


    /**
     * func_animate
     *
     *
     *
     * Launches a projectile.
     *
     * @param projectilename
     * @param sound
     * @param launchbone
     * @param targetbone
     * @param numshots
     * @param framedelay
     */
    public void launchMissiles(String projectilename, String sound,
                               String launchbone, String targetbone,
                               float numshots, float framedelay);

    /**
     * Switches to a ragdoll taking over the animation.
     */
    public void startRagdoll();

    /**
     * Changes to left foot and plays footstep sound.
     */
    public void leftFoot();

    /**
     * Changes to right foot and plays footstep sound.
     */
    public void rightFoot();

    /***********************************************************************
     four fingered claw

     ***********************************************************************/

    public void setFingerAngle(float angle);

    public void stopFingers();


    /**
     * func_moveable
     *
     *
     *
     * Makes the moveable non-solid for other entities.
     */
    public void becomeNonSolid();

    /**
     * returns true if object is not moving
     *

     */
    public float isAtRest();

    /**
     * enable/disable damage
     *
     * @param enable
     */
    public void enableDamage(float enable);


    /**
     * skeletal animation (weapons, players, ai, func_animated)
     *
     *
     *
     * Looks up the number of the specified joint. Returns INVALID_JOINT if the joint is not found.
     *
     * @param jointname
     *

     */
    public float getJointHandle(String jointname);

    /**
     * Removes any custom transforms on all joints.
     */
    public void clearAllJoints();

    /**
     * Removes any custom transforms on the specified joint.
     *
     * @param jointnum
     */
    public void clearJoint(float jointnum);

    /**
     * Modifies the position (@see IVector) of the joint based on the transform type.
     *
     * @param jointnum
     * @param transform_type
     * @param pos
     */
    public void setJointPos(float jointnum, float transform_type,
                            IVector pos);

    /**
     * Modifies the orientation of the joint based on the transform type.
     *
     * @param jointnum
     * @param transform_type
     * @param angles
     */
    public void setJointAngle(float jointnum, float transform_type,
                              IVector angles);

    /**
     * returns the position (@see IVector) of the joint in world space
     *
     * @param jointnum
     *

     */
    public IVector getJointPos(float jointnum);

    /**
     * returns the angular orientation of the joint in world space
     *
     * @param jointnum
     *

     */
    public IVector getJointAngle(float jointnum);

    /***********************************************************************
     weapons

     ***********************************************************************/

    public IEntity getOwner();

    public void nextWeapon();

    public void weaponState(String stateFunction, float blendFrames);

    /**
     * eats the specified amount of ammo
     *
     * @param amount
     */
    public void useAmmo(float amount);

    /**
     * Adds the specified amount of ammo
     *
     * @param amount
     */
    public void addToClip(float amount);

    /**
     * number of shots left in clip
     *
     */
    public float ammoInClip();

    /**
     * number of shots left in inventory
     *
     */
    public float ammoAvailable();

    /**
     * amount of ammo in inventory. since each shot may use more than 1 ammo, this is different than ammoAvailable()
     *
     */
    public float totalAmmoCount();

    /**
     * amount of ammo the clip holds. since each shot may use more than 1 ammo
     *
     */
    public float clipSize();

    /**
     * Returns true if this IEntity is invisible
     *
     */
    public float isInvisible();

    /**
     * Plays the given animation on the given channel. Returns false if anim doesn't exist.
     * NOTE: weapons only use ANIMCHANNEL_ALL
     *
     * @param channel
     * @param animName
     *

     */
    public float playAnim(float channel, String animName);

    /**
     * Continuously repeats the given animation on the given channel. Returns false if anim doesn't exist.
     * NOTE: weapons only use ANIMCHANNEL_ALL
     *
     * @param channel
     * @param animName
     *

     */
    public float playCycle(float channel, String animName);

    /**
     * Returns  true if the animation playing on the given channel
     * is completed considering a number of blend frames.
     * NOTE: weapons only use ANIMCHANNEL_ALL
     *
     * @param channel
     * @param blendOutFrames
     *

     */
    public float animDone(float channel, float blendOutFrames);

    /**
     * Sets the number of frames to blend between animations on the given channel.
     * NOTE: weapons only use ANIMCHANNEL_ALL
     *
     * @param channel
     * @param blendFrame
     */
    public void setBlendFrames(float channel, float blendFrame);

    /**
     * Returns  the number of frames to blend between animations on the given channel.
     * NOTE: weapons only use ANIMCHANNEL_ALL
     *
     * @param channel
     *

     */
    public float getBlendFrames(float channel);

    public void weaponReady();

    public void weaponOutOfAmmo();

    public void weaponReloading();

    public void weaponHolstered();

    public void weaponRising();

    public void weaponLowering();

    public void flashlight(float enable);

    public void launchProjectiles(float num_projectiles, float spread,
                                  float fuseOffset, float launchPower,
                                  float dmgPower);

    public IEntity createProjectile();

    public float melee();

    /**
     * disables/enables owner dropping weapon when killed
     *
     * @param allow
     */
    public void allowDrop(float allow);

    /**
     * ui_autoReload check
     *
     * Returns
     */
    public float autoReload();

    /**
     * network client
     */
    public void netReload();

    /**
     * network client - force end of a reload
     */
    public void netEndReload();

    /**
     * Sets a shader parameter on the muzzleflash/light
     *      public void setLightParm( float parmNum, float value );
     *
     * Sets the red/green/blue/alpha shader parms on the muzzleflash/light
     *      public void setLightParms( float red, float green, float blue, float alpha );
     *
     * Returns  the IEntity that controls the world model
     *

     */
    public IEntity getWorldModel();

    /**
     * projectiles
     *
     *
     * gets the current state of the projectile. states are defined in doom_defs.script
     *

     */
    public float getProjectileState();

    /**
     * combat nodes
     *
     * Disables the combat node if "use_once" is set on the IEntity.
     */
    public void markUsed();

    /**
     * path nodes
     *
     *
     *
     * chooses a random path from the IEntity's targets, ignoring non-path entities.
     *

     */
    public IEntity randomPath();


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


    /**
     * lights
     *
     *
     *
     * Sets the shader to be used for the light.
     *
     * @param shader
     */
    public void setShader(String shader);

    /**
     * Gets a shader parameter.
     *
     * @param parmNum
     *
     * @return
     */
    public float getLightParm(float parmNum);

    /**
     * Sets a shader parameter.
     *
     * @param parmNum
     * @param value
     */
    public void setLightParm(float parmNum, float value);

    /**
     * Sets the red/green/blue/alpha shader parms on the light and the model.
     *
     * @param parm0
     * @param parm1
     * @param parm2
     * @param parm3
     */
    public void setLightParms(float parm0, float parm1, float parm2,
                              float parm3);

    /**
     * Sets the width/length/height of the light bounding box.
     *
     * @param x
     * @param y
     * @param z
     */
    public void setRadiusXYZ(float x, float y, float z);

    /**
     * Sets the size of the bounding box.
     *
     * @param radius
     */
    public void setRadius(float radius);

    /**
     * Turns the light on.
     */
    public void On();

    /**
     * Turns the light off.
     */
    public void Off();

    /**
     * Turns the light out over the given time in seconds.
     *
     * @param time
     */
    public void fadeOutLight(float time);

    /**
     * Turns the light on over the given time in seconds.
     *
     * @param time
     */
    public void fadeInLight(float time);


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
     * Initiates a translation back and forth along the given IVector with the given speed and sphase.
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
     * Stops the animation currently playing on the given channel over the given number of frames.
     *
     * @param channel
     * @param frames
     */
    public void stopAnim(float channel, float frames);


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
    public void setSyncedAnimWeight(float channel, float animindex,
                                    float weight);


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
     * @return movement relative to the player's view angles from the current user command.
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
     * @return the IEntity for the player's weapon
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







    /**
     * AI characters and monsters
     *
     *
     *
     * finds enemy player in PVS
     *
     * @param onlyInFov
     *

     */
    public IEntity findEnemy(float onlyInFov);

    /**
     * finds enemy monster in PVS
     *
     * @param onlyInFov
     *

     */
    public IEntity findEnemyAI(float onlyInFov);

    /**
     * finds enemy player in attack cones
     *

     */
    public IEntity findEnemyInCombatNodes();

    /**
     * finds another character's closest reachable enemy
     *
     * @param team_mate
     *

     */
    public IEntity closestReachableEnemyOfEntity(IEntity team_mate);

    public IEntity heardSound(float ignore_team);

    public void setEnemy(IEntity enemy);

    public void clearEnemy();

    public void muzzleFlash(String jointname);

    /**
     * @return  projectile created
     *
     * @param jointname
     *

     */
    public IEntity createMissile(String jointname);

    /**
     * @return  projectile fired
     *
     * @param jointname
     *

     */
    public IEntity attackMissile(String jointname);

    /**
     * launches a missile at IEntity specified by 'attack_target'. returns projectile fired
     *
     * @param jointname
     * @param targetname
     *

     */
    public IEntity fireMissileAtTarget(String jointname, String targetname);

    /**
     * Resturns The projectile
     * @return  the projectile IEntity
     *
     * @param origin
     * @param angles
     *

     */
    public IEntity launchMissile(IVector origin, IVector angles);

    /**
     * @return  true if the attack hit
     *
     * @param damageDef
     *

     */
    public float attackMelee(String damageDef);

    public void directDamage(IEntity damageTarget, String damageDef);

    public void radiusDamageFromJoint(String jointname, String damageDef);

    public void attackBegin(String damageDef);

    public void attackEnd();

    public float meleeAttackToJoint(String joint, String damageDef);

    public float canBecomeSolid();

    public void becomeSolid();


    /**
     * enables the ragdoll if the IEntity has one
     *

     */
    public float becomeRagdoll();

    /**
     * turns off the ragdoll
     */
    public void stopRagdoll();

    public void setHealth(float health);

    public float getHealth();

    public void allowDamage();

    public void ignoreDamage();

    public float getCurrentYaw();

    public void turnTo(float yaw);

    public void turnToPos(IVector pos);

    public void turnToEntity(IEntity ent);

    public float moveStatus();

    public void stopMove();

    public void moveToCover();

    public void moveToEnemy();

    public void moveToEnemyHeight();

    public void moveOutOfRange(IEntity ent, float range);

    public void moveToAttackPosition(IEntity ent, String attack_anim);

    public void wander();

    public void moveToEntity(IEntity destination);

    public void moveToPosition(IVector position);

    public void slideTo(IVector position, float time);

    public float facingIdeal();

    public void faceEnemy();

    public void faceEntity(IEntity ent);

    public IEntity getCombatNode();

    public float enemyInCombatCone(IEntity combatNode,
                                   float use_current_enemy_location);

    public void waitMove();

    public IVector getJumpVelocity(IVector pos, float speed,
                                    float max_jump_height);

    public float entityInAttackCone(IEntity ent);

    public float canSee(IEntity ent);

    public float enemyRange();

    public float enemyRange2D();

    /**
     * sets the IEntity (player) trying to talk to the character
     *
     * @param target
     */
    public void setTalkTarget(IEntity target);

    /**
     * @return  the IEntity (player) trying to talk to the character
     *

     */
    public IEntity getTalkTarget();

    public IEntity getEnemy();

    public IVector getEnemyPos();

    public IVector getEnemyEyePos();

    /**
     * Tries to predict the player's movement based on the AAS and his direction of movement.
     *
     * @param time
     *

     */
    public IVector predictEnemyPos(float time);

    public float canHitEnemy();

    public float canHitEnemyFromAnim(String anim);

    public float canHitEnemyFromJoint(String jointname);

    public float enemyPositionValid();

    public void chargeAttack(String damageDef);

    public float testChargeAttack();

    public float testAnimMoveTowardEnemy(String animname);

    public float testAnimMove(String animname);

    public float testMoveToPosition(IVector position);

    public float testMeleeAttack();

    public float testAnimAttack(String animname);

    public void shrivel(float time);

    public void preBurn();

    public void burn();

    public void clearBurn();

    /**
     * enables/disables smoke particles on bones. pass in the particle #, or ALL_PARTICLES for turning on/off all particle systems.
     * particles are spawned in the order they appear in the entityDef
     *
     * @param particle_num
     * @param on
     */
    public void setSmokeVisibility(float particle_num, float on);

    /**
     * @return  the # of emitters defined by 'smokeParticleSystem' in the entitydef
     *

     */
    public float numSmokeEmitters();

    public void waitAction(String name);

    public void stopThinking();

    public float getTurnDelta();

    /**
     * @return  the current movetype
     *

     */
    public float getMoveType();

    /**
     * set the current movetype. movetypes are defined in ai_base.script
     *
     * @param movetype
     */
    public void setMoveType(float movetype);

    public void saveMove();

    public void restoreMove();

    public void allowMovement(float allow);

    public void enableClip();

    public void disableClip();

    public void enableGravity();

    public void disableGravity();

    public void enableAFPush();

    public void disableAFPush();

    /**
     * set the speed flying creatures move at. also sets speed for moveTypeSlide.
     *
     * @param speed
     */
    public void setFlySpeed(float speed);

    /**
     * sets the prefered height relative to the player's view height to fly at
     *
     * @param offset
     */
    public void setFlyOffset(float offset);

    /**
     * sets the prefered height relative to the player's view height to fly at to the value set in the def file
     */
    public void clearFlyOffset();

    /**
     * Finds the closest targeted IEntity of the specified type.
     *
     * @param entity_type
     *

     */
    public IEntity getClosestHiddenTarget(String entity_type);

    /**
     * Finds a random targeted IEntity of the specified type.
     *
     * @param entity_type
     *

     */
    public IEntity getRandomTarget(String entity_type);

    /**
     * Approximate travel distance to point.
     *
     * @param destination
     *

     */
    public float travelDistanceToPoint(IVector destination);

    /**
     * Approximate travel distance to IEntity.
     *
     * @param destination
     *

     */
    public float travelDistanceToEntity(IEntity destination);

    /**
     * Approximate travel distance between two entities.
     *
     * @param source
     * @param dest
     *

     */
    public float travelDistanceBetweenEntities(IEntity source, IEntity dest);

    /**
     * Approximate travel distance between two points.
     *
     * @param source
     * @param dest
     *

     */
    public float travelDistanceBetweenPoints(IVector source, IVector dest);

    /**
     * Aims the character's eyes and head toward an IEntity for a period of time.
     *
     * @param focusEntity
     * @param duration
     */
    public void lookAt(IEntity focusEntity, float duration);

    /**
     * Aims the character's eyes and head toward the current enemy for a period of time.
     *
     * @param duration
     */
    public void lookAtEnemy(float duration);

    /**
     * Enables or disables head looking (may be obsolete).
     *
     * @param allowBoneMod
     */
    public void setBoneMod(float allowBoneMod);

    /**
     * Kills the monster.
     */
    public void kill();

    /**
     * Tells the monster to activate when flashlight shines on them.
     *
     * @param enable
     */
    public void wakeOnFlashlight(float enable);

    /**
     * Sets whether the player can talk to this character or not.
     *
     * @param state
     */
    public void setTalkState(float state);

    /**
     * Updates the last known position of the enemy independent
     * from whether or not the enemy is visible.
     */
    public void locateEnemy();

    /**
     * kicks any obstacle in the character's path. pass in $null_entity if you don't have a specific IEntity to kick.
     *
     * @param kickEnt
     * @param force
     */
    public void kickObstacles(IEntity kickEnt, float force);

    /**
     * gets the obstacle in the character's path
     *

     */
    public IEntity getObstacle();

    /**
     * tries to push the point into a valid AAS area
     *
     * @param pos
     *

     */
    public IVector pushPointIntoAAS(IVector pos);

    /**
     * gets the rate the character turns
     *

     */
    public float getTurnRate();

    /**
     * set the rate the character turns at
     *
     * @param rate
     */
    public void setTurnRate(float rate);

    /**
     * enable/disable animation controlled turning. pass in the maximum # of degrees the animation turns. use an amount of 0 to disable.
     *
     * @param angle
     */
    public void animTurn(float angle);

    /**
     * normally, when hidden, monsters do not run physics. this enables physics when hidden.
     *
     * @param enable
     */
    public void allowHiddenMovement(float enable);

    /**
     * @return  an IEntity within the bounds specified
     *
     * @param mins
     * @param maxs
     *

     */
    public IEntity findActorsInBounds(IVector mins, IVector maxs);

    /**
     * @return  true if character can walk to specified position. for walking monsters, position should be near the floor.
     *
     * @param pos
     *

     */
    public float canReachPosition(IVector pos);

    /**
     * @return  true if character can walk to IEntity's position. for walking monsters, IEntity should be near the floor.
     *
     * @param ent
     *

     */
    public float canReachEntity(IEntity ent);

    /**
     * @return  true if character can walk to enemy's position. for walking monsters, enemy should be near the floor.
     *

     */
    public float canReachEnemy();

    /**
     * @return  the position of the IEntity within the aas if possible, otherwise just the IEntity position.
     *
     * @param ent
     *

     */
    public IVector getReachableEntityPosition(IEntity ent);

    /***********************************************************************
     AI - Vagary

     ***********************************************************************/

// finds a moveable object to throw at the enemy
    public IEntity vagary_ChooseObjectToThrow(IVector mins, IVector maxs,
                                              float speed, float minDist,
                                              float offset);

// throws object at enemy
    public void vagary_ThrowObjectAtEnemy(IEntity ent, float speed);


    /**
     * Starts a spline or anim camera moving.
     */
    public void start();

    /**
     * Stops a spline or anim camera moving.
     */
    public void stop();

}
