package daxclr.doom;

/***********************************************************************
 * system events (called via 'sys.')
 *
 ***********************************************************************/
public interface ISys extends IClass {
    
    //Sys
    float	asin( float a );
    float	acos( float a );
    float	randomInt( float range );
    IVector	VecToOrthoBasisAngles( IVector vec );
    IVector	rotateVector( IVector vec, IVector ang );
    
    
    
// Terminates a thread.
    public void terminate(float threadNumber);
    
// Pauses the current thread.
    public void pause();
    
// Suspends execution of the current thread for the given number of seconds.
    public void wait(float time);
    
// Suspends execution for one game frame.
    public void waitFrame();
    
// Waits for the given IEntity to complete it's move.
    public void waitFor(IEntity mover);
    
// Waits for the given thread to terminate.
    public void waitForThread(float threadNumber);
    
// Prints the given String to the console.
    public void print(String text);
    
// Prints the given line to the console.
    public void println(String text);
    
// Multiplayer - Print this line on the network
    public void say(String text);
    
// Breaks if the condition is zero. (Only works in debug builds.)
    public void assertGame(float condition);
    
// Triggers the given IEntity.
    public void trigger(IEntity entityToTrigger);
    
// Sets a cvar.
    public void setcvar(String name, String value);
    
// Returns the String for a cvar.
    public String getcvar(String name);
    
// Returns a random value X where 0 <= X < range.
    public float random(float range);
    
// Returns the current game time in seconds.
    public float getTime();
    
// Kills all threads with the specified name
    public void killthread(String threadName);
    
// Sets the name of the current thread.
    public void threadname(String name);
    
// Returns a reference to the IEntity with the specified name.
    public IEntity getEntity(String name);
    
// Creates an IEntity of the specified classname and returns a reference to the IEntity.
    public IEntity spawn(String classname);
    
// Respawn
    public void respawn();
    
// copies the spawn args from an IEntity
    public void copySpawnArgs(IEntity ent);
    
// Sets a key/value pair to be used when a new IEntity is spawned.
    public void setSpawnArg(String key, String value);
    
// Returns the String for the given spawn argument.
    public String SpawnString(String key, String pDefault);
    
// Returns the floating point value for the given spawn argument.
    public float SpawnFloat(String key, float pDefault);
    
// Returns the IdVector for the given spawn argument.
    public IVector SpawnVector(String key, IVector pDefault);
    
// clears data that persists between maps
    public void clearPersistantArgs();
    
// Sets a key/value pair that persists between maps
    public void setPersistantArg(String key, String value);
    
// Returns the String for the given persistant arg
    public String getPersistantString(String key);
    
// Returns the floating point value for the given persistant arg
    public float getPersistantFloat(String key);
    
// Returns the IdVector for the given persistant arg
    public IVector getPersistantVector(String key);
    
// Returns a forward IdVector for the given Euler angles.
    public IVector angToForward(IVector angles);
    
// Returns a right IdVector for the given Euler angles.
    public IVector angToRight(IVector angles);
    
// Returns an up IdVector for the given Euler angles.
    public IVector angToUp(IVector angles);
    
// Returns the sine of the given angle in degrees.
    public float sin(float degrees);
    
// Returns the cosine of the given angle in degrees.
    public float cos(float degrees);
    
// Returns the square root of the given number.
    public float sqrt(float square);
    
// Returns the normalized version of the given IdVector.
    public IVector vecNormalize(IVector vec);
    
// Returns the length of the given IdVector.
    public float vecLength(IVector vec);
    
// Returns the dot product of the two vectors.
    public float DotProduct(IVector vec1, IVector vec2);
    
// Returns the cross product of the two vectors.
    public IVector CrossProduct(IVector vec1, IVector vec2);
    
// Returns Euler angles for the given direction.
    public IVector VecToAngles(IVector vec);
    
// Sets a script callback function for when the given signal is raised on the given IEntity.
    public void onSignal(float signalNum, IEntity ent, String functionName);
    
// Clears the script callback function set for when the given signal is raised on the given IEntity.
    public void clearSignalThread(float signalNum, IEntity ent);
    
// Turns over view control to the given camera IEntity.
    public void setCamera(IEntity cameraEnt);
    
// Returns view control to the player IEntity.
    public void firstPerson();
    
// Returns the fraction of movement completed before the box from 'mins' to 'maxs' hits solid geometry
// when moving from 'start' to 'end'. The 'passEntity' is considered non-solid during the move.
    public float trace(IVector start, IVector end, IVector mins,
            IVector maxs, float contents_mask, IEntity passEntity);
    
// Returns the fraction of movement completed before the trace hits solid geometry
// when moving from 'start' to 'end'. The 'passEntity' is considered non-solid during the move.
    public float tracePoint(IVector start, IVector end, float contents_mask,
            IEntity passEntity);
    
// Returns the fraction of movement completed during the last call to trace or tracePoint.
    public float getTraceFraction();
    
// Returns the position the trace stopped due to a collision with solid geometry during the last call to trace or tracePoint
    public IVector getTraceEndPos();
    
// Returns the normal of the hit plane during the last call to trace or tracePoint
    public IVector getTraceNormal();
    
// Returns a reference to the IEntity which was hit during the last call to trace or tracePoint
    public IEntity getTraceEntity();
    
// Returns the number of the skeletal joint closest to the location on the IEntity which was hit
// during the last call to trace or tracePoint
    public String getTraceJoint();
    
// Returns the number of the body part of the IEntity which was hit during the last call to trace or tracePoint
    public String getTraceBody();
    
// Fades towards the given color over the given time in seconds.
    public void fadeIn(IVector color, float time);
    
// Fades from the given color over the given time in seconds.
    public void fadeOut(IVector color, float time);
    
// Fades to the given color up to the given alpha over the given time in seconds.
    public void fadeTo(IVector color, float alpha, float time);
    
// Starts playing background music.
    public void music(String shaderName);
    
// Issues an error.
    public void error(String text);
    
// Issues a warning.
    public void warning(String text);
    
// Returns the number of characters in the String
    public float strLength(String text);
    
// Returns a String composed of the first num characters
    public String strLeft(String text, float num);
    
// Returns a String composed of the last num characters
    public String strRight(String text, float num);
    
// Returns the String following the first num characters
    public String strSkip(String text, float num);
    
// Returns a String composed of the characters from start to start + num
    public String strMid(String text, float start, float num);
    
// Returns the numeric value of a String
    public float strToFloat(String text);
    
// damages entities within a radius defined by the damageDef. inflictor is the IEntity causing the damage and can be the same as the attacker (in the case
// of projectiles, the projectile is the inflictor, while the attacker is the character that fired the projectile). ignore is an IEntity to not cause damage to.
// dmgPower scales the damage (for cases where damage is dependent on time).
    public void radiusDamage(IVector origin, IEntity inflictor,
            IEntity attacker, IEntity ignore,
            String damageDefName, float dmgPower);
    
// networking - checks for client
    public float isClient();
    
// checks if it's a multiplayer game
    public float isMultiplayer();
    
// returns the length of time between game frames. this is not related to renderer frame rate.
    public float getFrameTime();
    
// returns the number of game frames per second. this is not related to renderer frame rate.
    public float getTicsPerSecond();
    
// ensure the specified sound shader is loaded by the system. prevents cache hits when playing sound shaders.
    public void cacheSoundShader(String shaderName);
    
// line drawing for debug visualization. lifetime of 0 == 1 frame.
    public void debugLine(IVector color, IVector start, IVector end,
            float lifetime);
    
    public void debugArrow(IVector color, IVector start, IVector end,
            float size, float lifetime);
    
    public void debugCircle(IVector color, IVector origin, IVector dir,
            float radius, float numSteps, float lifetime);
    
    public void debugBounds(IVector color, IVector mins, IVector maxs,
            float lifetime);
    
// text drawing for debugging. align can be 0-left, 1-center, 2-right. lifetime of 0 == 1 frame.
    public void drawText(String text, IVector origin, float scale,
            IVector color, float align, float lifetime);
    
// checks if an influence is active
    public float influenceActive();
}
