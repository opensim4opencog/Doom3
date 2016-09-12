package daxclr.doom;

public interface IDoomConstants {
	// number of game frames per second. rendering framerate is independent of
	// game frames.
	int GAME_FPS = 60;
	// 16 milliseconds
	float GAME_FRAMETIME = 0.016f;
	int NULL = 0;
	int TRUE = 1;
	int FALSE = 0;
	int UP = -1;
	int DOWN = -2;
	int LEFT = -3;
	int RIGHT = -4;
	int FORWARD = -5;
	int BACK = -6;
	int REL_UP = -7;
	int REL_DOWN = -8;
	int REL_LEFT = -9;
	int REL_RIGHT = -10;
	int REL_FORWARD = -11;
	int REL_BACK = -12;
	int EAST = 0;
	int NORTH = 90;
	int WEST = 180;
	int SOUTH = 270;
	int X_AXIS = 0;
	int Y_AXIS = 1;
	int Z_AXIS = 2;
	int YAW = 0;
	int PITCH = 1;
	int ROLL = 2;
	float M_PI = 3.14159265358979323846f;
	// used with setSmokeVisibility
	int ALL_PARTICLES = -1;
	// move succeeded, or not moving
	int MOVE_STATUS_DONE = 0;
	// move in progress
	int MOVE_STATUS_MOVING = 1;
	int MOVE_STATUS_WAITING = 2;
	// destination doesn't exist
	int MOVE_STATUS_DEST_NOT_FOUND = 3;
	int MOVE_STATUS_DEST_UNREACHABLE = 4;
	int MOVE_STATUS_BLOCKED_BY_WALL = 5;
	int MOVE_STATUS_BLOCKED_BY_OBJECT = 6;
	int MOVE_STATUS_BLOCKED_BY_ENEMY = 7;
	int MOVE_STATUS_BLOCKED_BY_MONSTER = 8;
	//
	// signals
	//
	// object was touched
	int SIG_TOUCH = 0;
	// object was used
	int SIG_USE = 1;
	// object was activated (generally by a trigger)
	int SIG_TRIGGER = 2;
	// object was removed from the game
	int SIG_REMOVED = 3;
	// object was damaged
	int SIG_DAMAGE = 4;
	// object was blocked
	int SIG_BLOCKED = 5;
	// mover at position 1 (door closed)
	int SIG_MOVER_POS1 = 6;
	// mover at position 2 (door open)
	int SIG_MOVER_POS2 = 7;
	// mover changing from position 1 to 2 (door opening)
	int SIG_MOVER_1TO2 = 8;
	// mover changing from position 2 to 1 (door closing)
	int SIG_MOVER_2TO1 = 9;
	// convenience signals for doors
	int SIG_DOOR_CLOSED = 6;
	int SIG_DOOR_OPEN = 7;
	int SIG_DOOR_OPENING = 8;
	int SIG_DOOR_CLOSING = 9;
	//
	// buttons
	//
	// attack button pressed
	int BUTTON_ATTACK = 1;
	// when the player is talking or in the menus
	int BUTTON_TALK = 2;
	// player is walking
	int BUTTON_WALKING = 16;
	// any key whatsoever
	int BUTTON_ANY = 128;
	//
	// Joint modifiers
	//
	// no modification
	int JOINTMOD_NONE = 0;
	// modifies the joint's position or orientation in joint local space
	int JOINTMOD_LOCAL = 1;
	// sets the joint's position or orientation in joint local space
	int JOINTMOD_LOCAL_OVERRIDE = 2;
	// modifies joint's position or orientation in model space
	int JOINTMOD_WORLD = 3;
	// sets the joint's position or orientation in model space
	int JOINTMOD_WORLD_OVERRIDE = 4;
	//
	// shader parms
	//
	int SHADERPARM_RED = 0;
	int SHADERPARM_GREEN = 1;
	int SHADERPARM_BLUE = 2;
	int SHADERPARM_ALPHA = 3;
	int SHADERPARM_TIMESCALE = 3;
	int SHADERPARM_TIMEOFFSET = 4;
	// random between 0.0 and 1.0 for some effects (muzzle flashes, etc)
	int SHADERPARM_DIVERSITY = 5;
	// for selecting which shader passes to enable
	int SHADERPARM_MODE = 7;
	// for the monster skin-burn-away effect enable and time offset
	int SHADERPARM_TIME_OF_DEATH = 7;
	//
	// Contents flags NOTE: make sure these are up to date with
	// renderer/Material.h
	//
	// an eye is never valid in a solid
	int CONTENTS_SOLID = 1;
	// blocks visibility (for ai)
	int CONTENTS_OPAQUE = 2;
	// used for water
	int CONTENTS_WATER = 4;
	// solid to players
	int CONTENTS_PLAYERCLIP = 8;
	// solid to monsters
	int CONTENTS_MONSTERCLIP = 16;
	// solid to moveable entities
	int CONTENTS_MOVEABLECLIP = 32;
	// solid to IK
	int CONTENTS_IKCLIP = 64;
	// used to detect blood decals
	int CONTENTS_BLOOD = 128;
	// used for actors
	int CONTENTS_BODY = 256;
	// used for projectiles
	int CONTENTS_PROJECTILE = 512;
	// used for dead bodies
	int CONTENTS_CORPSE = 1024;
	// used for render models for collision detection
	int CONTENTS_RENDERMODEL = 2048;
	// used for triggers
	int CONTENTS_TRIGGER = 4096;
	// solid for AAS
	int CONTENTS_AAS_SOLID = 8192;
	// used to compile an obstacle into AAS that can be enabled/disabled
	int CONTENTS_AAS_OBSTACLE = 16384;
	// used for triggers that are activated by the flashlight
	int CONTENTS_FLASHLIGHT_TRIGGER = 32768;
	//
	// content masks
	//
	int MASK_ALL = (-1);
	int MASK_SOLID = (CONTENTS_SOLID);
	int MASK_MONSTERSOLID = (CONTENTS_SOLID | CONTENTS_MONSTERCLIP | CONTENTS_BODY);
	int MASK_PLAYERSOLID = (CONTENTS_SOLID | CONTENTS_PLAYERCLIP | CONTENTS_BODY);
	int MASK_DEADSOLID = (CONTENTS_SOLID | CONTENTS_PLAYERCLIP);
	int MASK_WATER = (CONTENTS_WATER);
	int MASK_OPAQUE = (CONTENTS_OPAQUE);
	int MASK_SHOT_RENDERMODEL = (CONTENTS_SOLID | CONTENTS_RENDERMODEL);
	int MASK_SHOT_BOUNDINGBOX = (CONTENTS_SOLID | CONTENTS_BODY);
	//
	// sound channels
	//
	int SND_CHANNEL_ANY = 0;
	int SND_CHANNEL_VOICE = 1;
	int SND_CHANNEL_VOICE2 = 2;
	int SND_CHANNEL_BODY = 3;
	int SND_CHANNEL_BODY2 = 4;
	int SND_CHANNEL_BODY3 = 5;
	int SND_CHANNEL_WEAPON = 6;
	int SND_CHANNEL_ITEM = 7;
	int SND_CHANNEL_HEART = 8;
	int SND_CHANNEL_PDA = 9;
	int SND_CHANNEL_DEMONIC = 10;
	//
	// animation channels
	//
	int ANIMCHANNEL_ALL = 0;
	int ANIMCHANNEL_TORSO = 1;
	int ANIMCHANNEL_LEGS = 2;
	int ANIMCHANNEL_HEAD = 3;
	int ANIMCHANNEL_EYELIDS = 4;
	//
	// projectile states
	//
	int PROJECTILE_SPAWNED = 0;
	int PROJECTILE_CREATED = 1;
	int PROJECTILE_LAUNCHED = 2;
	int PROJECTILE_FIZZLED = 3;
	int PROJECTILE_EXPLODED = 4;
}
