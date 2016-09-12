package daxclr.doom;

public interface IAI extends IActor {

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
     * Gets a new the projectile to be fired
     * @return  new projectile for firing
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

    public IEntity randomPath();

    public float canBecomeSolid();

    public void becomeSolid();

    public void becomeNonSolid();

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


}
