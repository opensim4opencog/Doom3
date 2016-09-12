#include "../../idlib/precompiled.h"
#pragma hdrstop
#ifdef MOD_BOTS
#ifdef D3V_D3XP
#include "../game/Game_local.h"
#else 
#include "../d3xp/Game_local.h"
#endif
/*
===============================================================================

	botAi
	Base class to build bot on.

	TinMan: I was going to say something about this, but I've forgotten

===============================================================================
*/

const idEventDef BOT_SetNextState( "setNextState", "s" );
const idEventDef BOT_SetState( "setState", "s" );
const idEventDef BOT_GetState( "getState", NULL, 's' );
const idEventDef BOT_GetBody( "getBody", NULL, 'e' );

const idEventDef BOT_HasItem( "hasItem", "s", 'f' );
const idEventDef BOT_HasAmmo( "hasAmmo", "s", 'f' );

const idEventDef BOT_NextBestWeapon( "nextBestWeapon", NULL );

const idEventDef BOT_SetAimPosition( "setAimPosition", "v" );
const idEventDef BOT_GetAimPosition( "getAimPosition", NULL, 'v' );
const idEventDef BOT_GetMovePosition( "getMovePosition", NULL, 'v' );

const idEventDef BOT_CanSeeEntity( "canSeeEntity", "Ed", 'd' );

const idEventDef BOT_GetEyePosition( "getEyePosition", NULL, 'v' );
const idEventDef BOT_GetAIAimTargets( "getAIAimTargets", "ef", 'v' );

const idEventDef BOT_FindEnemies( "findEnemies", "d", 'f' );
const idEventDef BOT_FindInRadius( "findInRadius", "vfs", 'f' );
const idEventDef BOT_FindItems( "findItems", NULL, 'f' );
const idEventDef BOT_GetEntityList( "getEntityList", "f", 'e' );
const idEventDef BOT_HeardSound( "heardSound", "d", 'e' );
const idEventDef BOT_SetEnemy( "setEnemy", "E" );
const idEventDef BOT_ClearEnemy( "clearEnemy" );
const idEventDef BOT_GetEnemy( "getEnemy", NULL, 'e' );
const idEventDef BOT_LocateEnemy( "locateEnemy" );

const idEventDef BOT_EnemyRange( "enemyRange", NULL, 'f' );
const idEventDef BOT_EnemyRange2D( "enemyRange2D", NULL, 'f' );
const idEventDef BOT_GetEnemyPos( "getEnemyPos", NULL, 'v' );
const idEventDef BOT_GetEnemyEyePos( "getEnemyEyePos", NULL, 'v' );
const idEventDef BOT_PredictEnemyPos( "predictEnemyPos", "f", 'v' );
const idEventDef BOT_CanHitEnemy( "canHitEnemy", NULL, 'd' );
const idEventDef BOT_EnemyPositionValid( "enemyPositionValid", NULL, 'd' );

const idEventDef BOT_MoveStatus( "moveStatus", NULL, 'd' );
const idEventDef BOT_StopMove( "stopMove" );
const idEventDef BOT_SaveMove( "saveMove" );
const idEventDef BOT_RestoreMove( "restoreMove" );

const idEventDef BOT_SetMoveToCover( "setMoveToCover" );
const idEventDef BOT_SetMoveToEnemy( "setMoveToEnemy" );
const idEventDef BOT_SetMoveOutOfRange( "setMoveOutOfRange", "ef" );
//const idEventDef BOT_MoveToAttackPosition( "setMoveToAttackPosition", "es" );
const idEventDef BOT_SetMoveWander( "setMoveWander" );
const idEventDef BOT_SetMoveToEntity( "setMoveToEntity", "e" );
const idEventDef BOT_SetMoveToPosition( "setMoveToPosition", "v" );

const idEventDef BOT_CanReachPosition( "canReachPosition", "v", 'd' );
const idEventDef BOT_CanReachEntity( "canReachEntity", "E", 'd' );
const idEventDef BOT_CanReachEnemy( "canReachEnemy", NULL, 'd' );
const idEventDef BOT_GetReachableEntityPosition( "getReachableEntityPosition", "e", 'v' );

const idEventDef BOT_TravelDistanceToPoint( "travelDistanceToPoint", "v", 'f' );
const idEventDef BOT_TravelDistanceToEntity( "travelDistanceToEntity", "e", 'f' );
const idEventDef BOT_TravelDistanceBetweenPoints( "travelDistanceBetweenPoints", "vv", 'f' );
const idEventDef BOT_TravelDistanceBetweenEntities( "travelDistanceBetweenEntities", "ee", 'f' );

// TinMan: The old integration vs independant argument, the proper way would be to integrate the following script events into
const idEventDef BOT_GetHealth( "getActorHealth", "e", 'f' ); // TinMan: could go in actor
const idEventDef BOT_GetArmor( "getArmor", "e", 'f' ); // TinMan: could go in player

const idEventDef BOT_GetTeam( "getTeam", "e", 'f' ); // TinMan: could go in actor

// TinMan: Could be in entity
const idEventDef BOT_GetClassName( "getClassName", "e", 's' );
const idEventDef BOT_GetClassType( "getClassType", "e", 's' );

const idEventDef BOT_Acos( "acos", "f", 'f' ); // TinMan: could go in script sys

CLASS_DECLARATION( idEntity, botAi )
	EVENT( BOT_SetNextState,					botAi::Event_SetNextState )
	EVENT( BOT_SetState,						botAi::Event_SetState )
	EVENT( BOT_GetState,						botAi::Event_GetState )
	EVENT( BOT_GetBody,							botAi::Event_GetBody )

	EVENT( BOT_GetHealth,						botAi::Event_GetHealth )
	EVENT( BOT_GetArmor,						botAi::Event_GetArmor )

	EVENT( BOT_GetTeam,							botAi::Event_GetTeam )

	EVENT( BOT_HasItem,							botAi::Event_HasItem )
	EVENT( BOT_HasAmmo,							botAi::Event_HasAmmo )

	EVENT( BOT_NextBestWeapon,					botAi::Event_NextBestWeapon )

	EVENT( BOT_SetAimPosition,					botAi::Event_SetAimPosition )
	EVENT( BOT_GetAimPosition,					botAi::Event_GetAimPosition )
	EVENT( BOT_GetMovePosition,					botAi::Event_GetMovePosition )

	EVENT( BOT_CanSeeEntity,					botAi::Event_CanSeeEntity )

	EVENT( BOT_GetEyePosition,					botAi::Event_GetEyePosition )
	EVENT( BOT_GetAIAimTargets,					botAi::Event_GetAIAimTargets )

	EVENT( BOT_FindEnemies,						botAi::Event_FindEnemies )
	EVENT( BOT_FindInRadius,					botAi::Event_FindInRadius )
	EVENT( BOT_FindItems,						botAi::Event_FindItems )
	EVENT( BOT_GetEntityList,					botAi::Event_GetEntityList )
	EVENT( BOT_HeardSound,						botAi::Event_HeardSound )
	EVENT( BOT_SetEnemy,						botAi::Event_SetEnemy )
	EVENT( BOT_ClearEnemy,						botAi::Event_ClearEnemy )
	EVENT( BOT_GetEnemy,						botAi::Event_GetEnemy )
	EVENT( BOT_GetEnemy,						botAi::Event_GetEnemy )
	EVENT( BOT_LocateEnemy,						botAi::Event_LocateEnemy )

	EVENT( BOT_EnemyRange,						botAi::Event_EnemyRange )
	EVENT( BOT_EnemyRange2D,					botAi::Event_EnemyRange2D )
	EVENT( BOT_GetEnemyPos,						botAi::Event_GetEnemyPos )
	EVENT( BOT_GetEnemyEyePos,					botAi::Event_GetEnemyEyePos )
	EVENT( BOT_PredictEnemyPos,					botAi::Event_PredictEnemyPos )
	EVENT( BOT_CanHitEnemy,						botAi::Event_CanHitEnemy )
	EVENT( BOT_EnemyPositionValid,				botAi::Event_EnemyPositionValid )

	EVENT( BOT_MoveStatus,						botAi::Event_MoveStatus )
	EVENT( BOT_StopMove,						botAi::Event_StopMove )
	EVENT( BOT_SaveMove,						botAi::Event_SaveMove )
	EVENT( BOT_RestoreMove,						botAi::Event_RestoreMove )

	EVENT( BOT_SetMoveToCover,					botAi::Event_SetMoveToCover )
	EVENT( BOT_SetMoveToEnemy,					botAi::Event_SetMoveToEnemy )
	EVENT( BOT_SetMoveOutOfRange,				botAi::Event_SetMoveOutOfRange )
	//EVENT( BOT_SetMoveToAttackPosition,			botAi::Event_SetMoveToAttackPosition )
	EVENT( BOT_SetMoveWander,					botAi::Event_SetMoveWander )
	EVENT( BOT_SetMoveToEntity,					botAi::Event_SetMoveToEntity )
	EVENT( BOT_SetMoveToPosition,				botAi::Event_SetMoveToPosition )

	EVENT( BOT_CanReachPosition,				botAi::Event_CanReachPosition )
	EVENT( BOT_CanReachEntity,					botAi::Event_CanReachEntity )
	EVENT( BOT_CanReachEnemy,					botAi::Event_CanReachEnemy )
	EVENT( BOT_GetReachableEntityPosition,		botAi::Event_GetReachableEntityPosition )

	EVENT( BOT_TravelDistanceToPoint,			botAi::Event_TravelDistanceToPoint )
	EVENT( BOT_TravelDistanceToEntity,			botAi::Event_TravelDistanceToEntity )
	EVENT( BOT_TravelDistanceBetweenPoints,		botAi::Event_TravelDistanceBetweenPoints )
	EVENT( BOT_TravelDistanceBetweenEntities,	botAi::Event_TravelDistanceBetweenEntities )

	EVENT( BOT_GetClassName,					botAi::Event_GetClassName )
	EVENT( BOT_GetClassType,					botAi::Event_GetClassType )

	EVENT( BOT_Acos,							botAi::Event_Acos )
END_CLASS

botInfo_t botAi::bots[BOT_MAX_BOTS]; // TinMan: init bots array, must keep an eye on the blighters

/*
=====================
botAi::botAi
=====================
*/
botAi::botAi() {
	fl.networkSync		= false;

	botID				= 0;
	clientID			= 0;
	playerEnt			= NULL;
	physicsObject		= NULL;
	inventory			= NULL;

	viewAngles			= ang_zero;

	scriptThread		= NULL;		// id: initialized by ConstructScriptObject, which is called by idEntity::Spawn

	aas					= NULL;
	move.moveType		= MOVETYPE_ANIM;

	travelFlags			= TFL_WALK|TFL_AIR;

	kickForce			= 2048.0f;
	ignore_obstacles	= false;
	blockedRadius		= 0.0f;
	blockedMoveTime		= 750;
}

/*
=====================
botAi::~botAi
=====================
*/
botAi::~botAi() {
	ShutdownThreads();
}

/*
=====================
botAi::WriteUserCmdsToSnapshot
TinMan: A brian c and steven b production
=====================
*/
void botAi::WriteUserCmdsToSnapshot( idBitMsg &msg ) {
	int i; 
	int numBots; 

	numBots = 0; 
	// TODO: this loops through all the clients, really only need to loop starting at BOT_START_INDEX
	for ( i = 0; i < gameLocal.numClients; i++ ) { 
		if ( bots[i].inUse ) {
			numBots++; 
		}
	} 
	// send the number of bots over the wire
	msg.WriteBits( numBots, 5 ); 

	for ( i = 0; i < gameLocal.numClients; i++ ) { 
		// write the bot number over the wire
		if ( bots[i].inUse ) {
			// cusTom3 - the index in the usercmds array is i + BOT_START_INDEX
			msg.WriteBits( i + BOT_START_INDEX, 5 );
	        usercmd_t &cmd = gameLocal.usercmds[i + BOT_START_INDEX];
			// still have ?'s about exactly what to update.
			// gametime seemed to get set to 0 here
			// think duplicationcount also needs to be set
			//msg.WriteLong( cmd.gameTime ); 
			// msg.WriteShort( cmd.duplicateCount = 0 );
			msg.WriteByte( cmd.buttons ); 
			msg.WriteShort( cmd.mx ); 
			msg.WriteShort( cmd.my ); 
			msg.WriteChar( cmd.forwardmove ); 
			msg.WriteChar( cmd.rightmove ); 
			msg.WriteChar( cmd.upmove ); 
			msg.WriteShort( cmd.angles[0] ); 
			msg.WriteShort( cmd.angles[1] ); 
			msg.WriteShort( cmd.angles[2] ); 
		}
	}
}

/*
=====================
botAi::ReadUserCmdsFromSnapshot
TinMan: A brian c and steven b production
=====================
*/
void botAi::ReadUserCmdsFromSnapshot( const idBitMsg &msg ) {
	int i; 
	int numBots; 

	numBots = msg.ReadBits( 5 ); 
	for ( i = 0; i < numBots; i++ ) { 
		int iBot = msg.ReadBits( 5 ); 

		usercmd_t &cmd = gameLocal.usercmds[iBot]; 
		//cmd.gameTime    = msg.ReadLong();  // do i write gameTime
		//cmd.duplicateCount = 0;		// manally setting this in case it is used in processing
		cmd.buttons     = msg.ReadByte(); 
		cmd.mx          = msg.ReadShort(); 
		cmd.my          = msg.ReadShort(); 
		cmd.forwardmove = msg.ReadChar(); 
		cmd.rightmove   = msg.ReadChar(); 
		cmd.upmove      = msg.ReadChar(); 
		cmd.angles[0]   = msg.ReadShort(); 
		cmd.angles[1]   = msg.ReadShort(); 
		cmd.angles[2]   = msg.ReadShort(); 
	} 
} 

/*
===================
botAi::Addbot_f
TinMan: Console command. Bring in teh b0tz!
*todo* set default def to something sensible
*todo* random name if default bot
*todo* get passed in args working for no added classname
===================
*/
void botAi::Addbot_f( const idCmdArgs &args ) {
   	const char *key, *value;
	int			i;
	idVec3		org;
	idDict		dict;
	const char* name;
	idDict		userInfo;

	int botID, newClientID;

	if ( !gameLocal.isMultiplayer ) {
		gameLocal.Printf( "You may only add a bot to a multiplayer game\n" );
		return;
	}

	if ( !gameLocal.isServer ) {
		gameLocal.Printf( "Bots may only be added on server\n" );
		return;
	}

	idEntity * ent;
	// Try to find an ID in the bots list
	for ( i = 0; i < BOT_MAX_BOTS; i++ ) {
		if ( bots[i].inUse ) {			
			// TinMan: *cheaphack* make sure it isn't an orphaned spot, *todo* should be done in client disconnect
			if ( gameLocal.entities[ bots[i].clientID ] ) {
				continue;
			} else {
				break;
			}
		} else {
			break;
		}
	}

	if ( i == BOT_MAX_BOTS ) {
		botID = -1;
	} else {
		botID = i;
	}

	// If we dont have a bot ID, return and show an error
	if (botID == -1) {
		gameLocal.Printf("^1ERROR: The maximum number of bots are already in the game.\n");
		return;
	}

	value = args.Argv( 1 );

	// TinMan: Check to see if valid def
	const idDeclEntityDef *botDef = gameLocal.FindEntityDef( value );
	const char *spawnclass = botDef->dict.GetString( "spawnclass" );
	idTypeInfo *cls = idClass::GetClass( spawnclass );
	if ( !cls || !cls->IsType( botAi::Type ) ) {
		gameLocal.Printf( "def not of type botAi or no def name given\n" );
		return;
	}

	dict.Set( "classname", value );

	// TinMan: Add rest of key/values passed in
	for( i = 2; i < args.Argc() - 1; i += 2 ) {
		key = args.Argv( i );
		value = args.Argv( i + 1 );

		dict.Set( key, value );
	}

	dict.SetInt( "botID", botID ); // TinMan: Bot needs to know this before it's spawned so it can sync up with the client
	newClientID = BOT_START_INDEX + botID; // TinMan: Set up as client id, bots use >16

	//gameLocal.Printf("Spawning bot as client %d\n", newClientID);

	// Start up client
	gameLocal.ServerClientConnect(newClientID);

	gameLocal.ServerClientBegin(newClientID); // TinMan: Does the spawning of player ents on clients.

	idPlayer * botClient = static_cast< idPlayer * >( gameLocal.entities[ newClientID ] );
	botClient->spawnArgs.SetBool( "isBot", true ); // TinMan: Put our grubby fingerprints on client.
	botClient->spawnArgs.SetInt( "botID", botID );

	// TinMan: Add client bot list
	bots[botID].inUse	= true;
	bots[botID].clientID	= newClientID;

	// TinMan: Spawn bot
	//idEntity *ent;
	gameLocal.SpawnEntityDef( dict, &ent, false );

	botAi * newBot = static_cast< botAi * >( ent );

	// TinMan: Add bot to bot list
	bots[botID].entityNum = newBot->entityNumber;

	name = newBot->spawnArgs.GetString( "npc_name" );
	userInfo.Set( "ui_name", name );
	int skinNum = newBot->spawnArgs.GetInt( "mp_skin" );
	userInfo.Set( "ui_skin", ui_skinArgs[ skinNum ] );

	if ( gameLocal.gameType == GAME_TDM ) {
		userInfo.Set( "ui_team", newBot->spawnArgs.GetInt( "team" ) ? "Blue" : "Red" );
	}

	// TinMan: Finish up connecting - Called in SetUserInfo
	//gameLocal.mpGame.EnterGame(newClientID); 
	//gameLocal.Printf("Bot has been connected, and client has begun.\n");

	userInfo.Set( "ui_ready", "Ready" );

	gameLocal.SetUserInfo( newClientID, userInfo, false, true ); // TinMan: Was changed slightly in 1.3

	botClient->Spectate( false );
}

/*
================
botAi::Init
================
*/
void botAi::Init( void ) {
	state				= NULL;
	idealState			= NULL;

	enemy				= NULL;
	lastVisibleEnemyPos.Zero();
	lastVisibleEnemyEyeOffset.Zero();
	lastVisibleReachableEnemyPos.Zero();
	lastReachableEnemyPos.Zero();

	lastHitCheckResult	= false;
	lastHitCheckTime	= 0;

	AI_FORWARD			= false;
	AI_BACKWARD			= false;
	AI_STRAFE_LEFT		= false;
	AI_STRAFE_RIGHT		= false;
	AI_ATTACK_HELD		= false;
	AI_WEAPON_FIRED		= false;
	AI_JUMP				= false;
	AI_DEAD				= false;
	AI_CROUCH			= false;
	AI_ONGROUND			= true;
	AI_ONLADDER			= false;
	AI_RUN				= false;
	AI_HARDLANDING		= false;
	AI_SOFTLANDING		= false;
	AI_RELOAD			= false;
	AI_PAIN				= false;
	AI_TELEPORT			= false;
	AI_TURN_LEFT		= false;
	AI_TURN_RIGHT		= false;
	
	AI_ENEMY_VISIBLE	= false;
	AI_ENEMY_IN_FOV		= false;
	AI_ENEMY_DEAD		= false;
	AI_MOVE_DONE		= false;
	AI_DEST_UNREACHABLE = false;
	AI_BLOCKED			= false;
	AI_OBSTACLE_IN_PATH = false;
	AI_PUSHED			= false;

	AI_WEAPON_FIRE		= false;

	// id: init the move variables
	StopMove( MOVE_STATUS_DONE );

	numSearchListEntities = 0;
	memset( entitySearchList, 0, sizeof( entitySearchList ) );

	// id: reset the script object
	ConstructScriptObject();

	// id: execute the script so the script object's constructor takes effect immediately
	scriptThread->Execute();

	viewAngles		= playerEnt->viewAngles;
	aimPosition		= physicsObject->GetOrigin();
	aimRate			= spawnArgs.GetFloat( "aim_rate", "0.1" );
}

/*
================
botAi::Spawn
================
*/
void botAi::Spawn( void ) {
	// TinMan: Sync up with our client
	botID = spawnArgs.GetInt( "botID" );
	clientID = bots[botID].clientID;
	//assert( clientID );
	if (clientID) {

	playerEnt = static_cast< idPlayer * >( gameLocal.entities[ clientID ] ); // TinMan: Let the brain know what it's body is
	physicsObject = static_cast< idPhysics_Player * >( playerEnt->GetPhysics() );
	inventory = &playerEnt->inventory;

	/*
	// TinMan: *debug*
	if ( playerEnt->spawnArgs.GetBool( "isBot" ) ) {
		gameLocal.Printf( "[botAi][client tagged as bot]\n" );
	}
	*/

	state		= NULL;
	idealState	= NULL;

	move.blockTime = 0;

	SetAAS();

	LinkScriptVariables();

	Init();
	}
}

/*
================
botAi::Save
================
*/
void botAi::Save( idSaveGame *savefile ) const {
	int i;

	savefile->WriteInt( botID );
	savefile->WriteInt( clientID );
	savefile->WriteObject( playerEnt );

	savefile->WriteAngles( viewAngles );

	savefile->WriteObject( scriptThread );

	idToken token;
	if ( idealState ) {
		idLexer src( idealState->Name(), idStr::Length( idealState->Name() ), "botAi::Save" );

		src.ReadTokenOnLine( &token );
		src.ExpectTokenString( "::" );
		src.ReadTokenOnLine( &token );

		savefile->WriteString( token );
	} else {
		savefile->WriteString( "" );
	}

	savefile->WriteVec3( aimPosition );
	savefile->WriteFloat( aimRate );

	savefile->WriteInt( numSearchListEntities );
	for ( i = 0; i == numSearchListEntities; i++ ) {
		savefile->WriteObject( entitySearchList[ i ] );
	}

	savefile->WriteInt( travelFlags );
	move.Save( savefile );
	savedMove.Save( savefile );
	savefile->WriteFloat( kickForce );
	savefile->WriteBool( ignore_obstacles );
	savefile->WriteFloat( blockedRadius );
	savefile->WriteInt( blockedMoveTime );

	enemy.Save( savefile );
	savefile->WriteVec3( lastVisibleEnemyPos );
	savefile->WriteVec3( lastVisibleEnemyEyeOffset );
	savefile->WriteVec3( lastVisibleReachableEnemyPos );
	savefile->WriteVec3( lastReachableEnemyPos );

	savefile->WriteBool( lastHitCheckResult );
	savefile->WriteInt( lastHitCheckTime );
}

/*
================
botAi::Restore
================
*/
void botAi::Restore( idRestoreGame *savefile ) {
	int i;

	savefile->ReadInt( botID );
	savefile->ReadInt( clientID );
	savefile->ReadObject( reinterpret_cast<idClass *&>( playerEnt ) );
	
	physicsObject = static_cast< idPhysics_Player * >( playerEnt->GetPhysics() );
	inventory = &playerEnt->inventory; // TinMan: *todo* this may not be nice

	savefile->ReadAngles( viewAngles );
	//playerEnt->SetViewAngles( viewAngles );

	savefile->ReadObject( reinterpret_cast<idClass *&>( scriptThread ) );

	idStr statename;
	savefile->ReadString( statename );
	if ( statename.Length() > 0 ) {
		state = GetScriptFunction( statename );
	}

	savefile->ReadVec3( aimPosition );
	savefile->ReadFloat( aimRate );

	savefile->ReadInt( numSearchListEntities );
	for ( i = 0; i == numSearchListEntities; i++ ) {
		savefile->ReadObject( reinterpret_cast<idClass *&>( entitySearchList[ i ] ) );
	}

	savefile->ReadInt( travelFlags );
	move.Restore( savefile );
	savedMove.Restore( savefile );
	savefile->ReadFloat( kickForce );
	savefile->ReadBool( ignore_obstacles );
	savefile->ReadFloat( blockedRadius );
	savefile->ReadInt( blockedMoveTime );

	enemy.Restore( savefile );
	savefile->ReadVec3( lastVisibleEnemyPos );
	savefile->ReadVec3( lastVisibleEnemyEyeOffset );
	savefile->ReadVec3( lastVisibleReachableEnemyPos );
	savefile->ReadVec3( lastReachableEnemyPos );

	savefile->ReadBool( lastHitCheckResult );
	savefile->ReadInt( lastHitCheckTime );

	SetAAS();

	LinkScriptVariables();
}

/*
===============
botAi::PrepareForRestart
TinMan: Get ready for map restart
================
*/
void botAi::PrepareForRestart( void ) {
	//gameLocal.Printf( "[PrepareForRestart]\n" );
	// we will be restarting program, clear the client entities from program-related things first
	ShutdownThreads();
}

/*
===============
botAi::Restart
TinMan: Re init after map restart
================
*/
void botAi::Restart( void ) {
	//gameLocal.Printf( "[Restart]\n" );

	Init();

	LinkScriptVariables();

	BecomeActive( TH_THINK );
}

/*
==============
botAi::LinkScriptVariables
==============
*/
void botAi::LinkScriptVariables( void ) {
	AI_FORWARD.LinkTo(			scriptObject, "AI_FORWARD" );
	AI_BACKWARD.LinkTo(			scriptObject, "AI_BACKWARD" );
	AI_STRAFE_LEFT.LinkTo(		scriptObject, "AI_STRAFE_LEFT" );
	AI_STRAFE_RIGHT.LinkTo(		scriptObject, "AI_STRAFE_RIGHT" );
	AI_ATTACK_HELD.LinkTo(		scriptObject, "AI_ATTACK_HELD" );
	AI_WEAPON_FIRED.LinkTo(		scriptObject, "AI_WEAPON_FIRED" );
	AI_JUMP.LinkTo(				scriptObject, "AI_JUMP" );
	AI_DEAD.LinkTo(				scriptObject, "AI_DEAD" );
	AI_CROUCH.LinkTo(			scriptObject, "AI_CROUCH" );
	AI_ONGROUND.LinkTo(			scriptObject, "AI_ONGROUND" );
	AI_ONLADDER.LinkTo(			scriptObject, "AI_ONLADDER" );
	AI_HARDLANDING.LinkTo(		scriptObject, "AI_HARDLANDING" );
	AI_SOFTLANDING.LinkTo(		scriptObject, "AI_SOFTLANDING" );
	AI_RUN.LinkTo(				scriptObject, "AI_RUN" );
	AI_PAIN.LinkTo(				scriptObject, "AI_PAIN" );
	AI_RELOAD.LinkTo(			scriptObject, "AI_RELOAD" );
	AI_TELEPORT.LinkTo(			scriptObject, "AI_TELEPORT" );
	AI_TURN_LEFT.LinkTo(		scriptObject, "AI_TURN_LEFT" );
	AI_TURN_RIGHT.LinkTo(		scriptObject, "AI_TURN_RIGHT" );


	AI_ENEMY_VISIBLE.LinkTo(	scriptObject, "AI_ENEMY_VISIBLE" );
	AI_ENEMY_IN_FOV.LinkTo(		scriptObject, "AI_ENEMY_IN_FOV" );
	AI_ENEMY_DEAD.LinkTo(		scriptObject, "AI_ENEMY_DEAD" );
	AI_MOVE_DONE.LinkTo(		scriptObject, "AI_MOVE_DONE" );
	AI_BLOCKED.LinkTo(			scriptObject, "AI_BLOCKED" );
	AI_DEST_UNREACHABLE.LinkTo( scriptObject, "AI_DEST_UNREACHABLE" );
	AI_OBSTACLE_IN_PATH.LinkTo(	scriptObject, "AI_OBSTACLE_IN_PATH" );
	AI_PUSHED.LinkTo(			scriptObject, "AI_PUSHED" );

	AI_WEAPON_FIRE.LinkTo(		scriptObject, "AI_WEAPON_FIRE" );
}

/*
=====================
botAi::Think
=====================
*/
void botAi::Think( void ) {
	//gameLocal.Printf( "--------- Botthink ----------\n" ); // TinMan: *debug*
}

/*
================
botAi::GetBodyState
TinMan: Grab updated information from fakeclient, this will be info from last frame since bot thinks before fakeclient
================
*/
void botAi::GetBodyState( void ) {
	// TinMan: *todo* Encounted some weirdness if set scriptbool stright from playerEnts bool, it would be set in gamecode but not set for script.
	if ( playerEnt->AI_DEAD ) {
		AI_DEAD	= true;
	} else {
		AI_DEAD = false;
	}

	if ( playerEnt->AI_PAIN ) {
		AI_PAIN	= true;
	} else {
		AI_PAIN = false;
	}

	if ( playerEnt->AI_ONGROUND) {
		AI_ONGROUND	= true;
	} else {
		AI_ONGROUND = false;
	}

	if ( playerEnt->AI_TELEPORT ) {
		AI_TELEPORT	= true;
	} else {
		AI_TELEPORT = false;
	}
}

/*
================
botAi::UpdateViewAngles
TinMan: Aim at aimPosition
aimPosition: Set by script - where we want to aim
aimRate: Rate at which view changes to match aim - like the mouserate/speed a player can turn view
================
*/
void botAi::UpdateViewAngles( void ) {
	idVec3		eyePos;
	idVec3		org;
	idVec3		newAimDir;
	idMat3		axis;
	idAngles 	newAimAng;
	idAngles	diff;

	axis	= playerEnt->viewAxis;
	org		= physicsObject->GetOrigin(); 
	//eyePos	=  playerEnt->GetEyePosition();
	eyePos	= org + playerEnt->EyeOffset(); // TinMan: More stable than eyeposition, don't think it's updated for crouching

	// TinMan: Something within your bounds is too close, so look ahead
	idBounds selfBounds = physicsObject->GetAbsBounds();

	if ( selfBounds.ContainsPoint( aimPosition ) ) {
		//gameLocal.Printf("botAi::UpdateViewAngles][AimPosition inside bounds: Resetting Ahead]\n"); // TinMan: *debug*
		aimPosition = eyePos + axis[ 0 ] * 512.0f;
	}

	viewAngles = playerEnt->viewAngles; // TinMan: Make sure we're cooking with the right ingredients

	newAimDir = aimPosition - eyePos;
	newAimAng.pitch = -idMath::AngleNormalize180( newAimDir.ToPitch() );
	newAimAng.yaw = idMath::AngleNormalize180( newAimDir.ToYaw() );
	newAimAng.roll = 0;

	diff = newAimAng - viewAngles;
	
	// TinMan: Fix popping angles
	if ( ( diff.pitch > 180.0f ) || ( diff.pitch <= -180.0f ) ) {
		diff.pitch = 360.0f - diff.pitch;
	}
	if ( diff.yaw > 180.0f ) {
		diff.yaw -= 360.0f;
	} else if ( diff.yaw <= -180.0f ) {
		diff.yaw += 360.0f;
	}

	viewAngles = viewAngles + diff * aimRate;

	//playerEnt->SetViewAngles( newAimAng ); // TinMan: *test* no blend
	//playerEnt->SetViewAngles( viewAngles );
}

/*
================
botAi::UpdateUserCmd
TinMan: Convert bot input to usrcmds for client
================
*/
void botAi::UpdateUserCmd( void ) {	
	usercmd_t usercmd;

	if ( AI_WEAPON_FIRE ) { // TinMan: Bang bang you're dead. No, I shot you first.
		usercmd.buttons |= BUTTON_ATTACK;
	}

	if ( AI_RUN ) {
		usercmd.buttons |= BUTTON_RUN;
	}

	if ( AI_FORWARD ) {
		usercmd.forwardmove = 127.0f;
	} else if ( AI_BACKWARD ) {
		usercmd.forwardmove = -127.0f;
	} else {
		usercmd.forwardmove = 0;
	}

	if ( AI_STRAFE_RIGHT ) {
		usercmd.rightmove = 127.0f;
	} else if ( AI_STRAFE_LEFT ) {
		usercmd.rightmove = -127.0f;
	} else {
		usercmd.rightmove = 0;
	}

	if ( AI_JUMP ) {
		usercmd.upmove = 127.0f;
	} else if ( AI_CROUCH ) {
		usercmd.upmove = -127.0f;
	} else {
		usercmd.upmove = 0;
	}

	int i;
	idAngles deltaViewAngles = playerEnt->GetDeltaViewAngles();
	for ( i = 0; i < 3; i++ ) {
		usercmd.angles[i] = ANGLE2SHORT( viewAngles[i] - deltaViewAngles[i] );
	}

	gameLocal.usercmds[ clientID ] = usercmd;
}

/***********************************************************************

	script state management

***********************************************************************/

/*
================
botAi::ShutdownThreads
================
*/
void botAi::ShutdownThreads( void ) {
	if ( scriptThread ) {
		scriptThread->EndThread();
		scriptThread->PostEventMS( &EV_Remove, 0 );
		delete scriptThread;
		scriptThread = NULL;
	}
}

/*
================
botAi::ConstructScriptObject

Called during idEntity::Spawn.  Calls the constructor on the script object.
Can be overridden by subclasses when a thread doesn't need to be allocated.
================
*/
idThread *botAi::ConstructScriptObject( void ) {
	const function_t *constructor;

	// make sure we have a scriptObject
	if ( !scriptObject.HasObject() ) {
		gameLocal.Error( "No scriptobject set on '%s'.  Check the '%s' entityDef.", name.c_str(), GetEntityDefName() );
	}

	if ( !scriptThread ) {
		// create script thread
		scriptThread = new idThread();
		scriptThread->ManualDelete();
		scriptThread->ManualControl();
		scriptThread->SetThreadName( name.c_str() );
	} else {
		scriptThread->EndThread();
	}
	
	// call script object's constructor
	constructor = scriptObject.GetConstructor();
	if ( !constructor ) {
		gameLocal.Error( "Missing constructor on '%s' for entity '%s'", scriptObject.GetTypeName(), name.c_str() );
	}

	// init the script object's data
	scriptObject.ClearObject();

	// just set the current function on the script.  we'll execute in the subclasses.
	scriptThread->CallFunction( this, constructor, true );

	return scriptThread;
}

/*
=====================
botAi::GetScriptFunction
=====================
*/
const function_t *botAi::GetScriptFunction( const char *funcname ) {
	const function_t *func;

	func = scriptObject.GetFunction( funcname );
	if ( !func ) {
		scriptThread->Error( "Unknown function '%s' in '%s'", funcname, scriptObject.GetTypeName() );
	}

	return func;
}

/*
=====================
botAi::SetState
=====================
*/
void botAi::SetState( const function_t *newState ) {
	if ( !newState ) {
		gameLocal.Error( "botAi::SetState: Null state" );
	}

	if ( ai_debugScript.GetInteger() == entityNumber ) {
		gameLocal.Printf( "%d: %s: State: %s\n", gameLocal.time, name.c_str(), newState->Name() );
	}

	state = newState;
	idealState = state;
	scriptThread->CallFunction( this, state, true );
}

/*
=====================
botAi::SetState
=====================
*/
void botAi::SetState( const char *statename ) {
	const function_t *newState;

	newState = GetScriptFunction( statename );
	SetState( newState );
}

/*
=====================
botAi::UpdateScript
=====================
*/
void botAi::UpdateScript( void ) {
	int	i;

	if ( ai_debugScript.GetInteger() == entityNumber ) {
		scriptThread->EnableDebugInfo();
	} else {
		scriptThread->DisableDebugInfo();
	}

	// a series of state changes can happen in a single frame.
	// this loop limits them in case we've entered an infinite loop.
	for( i = 0; i < 20; i++ ) {
		if ( idealState != state ) {
			SetState( idealState );
		}

		// don't call script until it's done waiting
		if ( scriptThread->IsWaiting() ) {
			break;
		}
        
		scriptThread->Execute();
		if ( idealState == state ) {
			break;
		}
	}

	if ( i == 20 ) {
		scriptThread->Warning( "botAi::UpdateScript: exited loop to prevent lockup" );
	}
}

/***********************************************************************

	navigation

***********************************************************************/

/*
============
botAi::KickObstacles
============
*/
void botAi::KickObstacles( const idVec3 &dir, float force, idEntity *alwaysKick ) {
	int i, numListedClipModels;
	idBounds clipBounds;
	idEntity *obEnt;
	idClipModel *clipModel;
	idClipModel *clipModelList[ MAX_GENTITIES ];
	int clipmask;
	idVec3 org;
	idVec3 forceVec;
	idVec3 delta;
	idVec2 perpendicular;

	org = physicsObject->GetOrigin();

	// find all possible obstacles
	clipBounds = physicsObject->GetAbsBounds();
	clipBounds.TranslateSelf( dir * 32.0f );
	clipBounds.ExpandSelf( 8.0f );
	clipBounds.AddPoint( org );
	clipmask = physicsObject->GetClipMask();
	numListedClipModels = gameLocal.clip.ClipModelsTouchingBounds( clipBounds, clipmask, clipModelList, MAX_GENTITIES );
	for ( i = 0; i < numListedClipModels; i++ ) {
		clipModel = clipModelList[i];
		obEnt = clipModel->GetEntity();
		if ( obEnt == alwaysKick ) {
			// we'll kick this one outside the loop
			continue;
		}

		if ( !clipModel->IsTraceModel() ) {
			continue;
		}

		if ( obEnt->IsType( idMoveable::Type ) && obEnt->GetPhysics()->IsPushable() ) {
			delta = obEnt->GetPhysics()->GetOrigin() - org;
			delta.NormalizeFast();
			perpendicular.x = -delta.y;
			perpendicular.y = delta.x;
			delta.z += 0.5f;
			delta.ToVec2() += perpendicular * gameLocal.random.CRandomFloat() * 0.5f;
			forceVec = delta * force * obEnt->GetPhysics()->GetMass();
			obEnt->ApplyImpulse( playerEnt, 0, obEnt->GetPhysics()->GetOrigin(), forceVec );
		}
	}

	if ( alwaysKick ) {
		delta = alwaysKick->GetPhysics()->GetOrigin() - org;
		delta.NormalizeFast();
		perpendicular.x = -delta.y;
		perpendicular.y = delta.x;
		delta.z += 0.5f;
		delta.ToVec2() += perpendicular * gameLocal.random.CRandomFloat() * 0.5f;
		forceVec = delta * force * alwaysKick->GetPhysics()->GetMass();
		alwaysKick->ApplyImpulse( playerEnt, 0, alwaysKick->GetPhysics()->GetOrigin(), forceVec );
	}
}

/*
============
ValidForBounds
TinMan: Odd that this should be orphaned like it was ( see idAI )
============
*/
bool botAi::ValidForBounds( const idAASSettings *settings, const idBounds &bounds ) {
	int i;

	for ( i = 0; i < 3; i++ ) {
		if ( bounds[0][i] < settings->boundingBoxes[0][0][i] ) {
			return false;
		}
		if ( bounds[1][i] > settings->boundingBoxes[0][1][i] ) {
			return false;
		}
	}
	return true;
}

/*
=====================
botAi::SetAAS
TinMan: *todo* why isn't physics bounds returning something nice?
=====================
*/
void botAi::SetAAS( void ) {
	idStr use_aas;

	//spawnArgs.GetString( "use_aas", NULL, use_aas );
	//aas = gameLocal.GetAAS( use_aas );
	aas = gameLocal.GetAAS( "aas48" );
	if ( aas ) {
		const idAASSettings *settings = aas->GetSettings();
		if ( settings ) {
			// TinMan: *todo* why isn't physics bounds returning something nice?
			/*if ( !ValidForBounds( settings, physicsObject->GetBounds() ) ) {
				gameLocal.Error( "%s cannot use use_aas %s\n", name.c_str(), use_aas.c_str() );
			}*/
			float height = settings->maxStepHeight;
			physicsObject->SetMaxStepHeight( height );
			return;
		} else {
			aas = NULL;
		}
	}
	gameLocal.Printf( "WARNING: %s has no AAS file\n", name.c_str() );
}

/*
=====================
botAi::DrawRoute
=====================
*/
void botAi::DrawRoute( void ) const {
	if ( aas && move.toAreaNum && move.moveCommand != MOVE_NONE && move.moveCommand != MOVE_WANDER && move.moveCommand != MOVE_FACE_ENEMY && move.moveCommand != MOVE_FACE_ENTITY && move.moveCommand != MOVE_TO_POSITION_DIRECT ) {
		if ( move.moveType == MOVETYPE_FLY ) {
			aas->ShowFlyPath( physicsObject->GetOrigin(), move.toAreaNum, move.moveDest );
		} else {
			aas->ShowWalkPath( physicsObject->GetOrigin(), move.toAreaNum, move.moveDest );
		}
	}
}

/*
=====================
botAi::ReachedPos
=====================
*/
bool botAi::ReachedPos( const idVec3 &pos, const moveCommand_t moveCommand ) const {
	if ( move.moveType == MOVETYPE_SLIDE ) {
		idBounds bnds( idVec3( -4, -4.0f, -8.0f ), idVec3( 4.0f, 4.0f, 64.0f ) );
		bnds.TranslateSelf( physicsObject->GetOrigin() );	
		if ( bnds.ContainsPoint( pos ) ) {
			return true;
		}
	} else {
		if ( ( moveCommand == MOVE_TO_ENEMY ) || ( moveCommand == MOVE_TO_ENTITY ) ) {
			if ( physicsObject->GetAbsBounds().IntersectsBounds( idBounds( pos ).Expand( 8.0f ) ) ) {
				return true;
			}
		} else {
			idBounds bnds( idVec3( -16.0, -16.0f, -8.0f ), idVec3( 16.0, 16.0f, 64.0f ) );
			bnds.TranslateSelf( physicsObject->GetOrigin() );	
			if ( bnds.ContainsPoint( pos ) ) {
				return true;
			}
		}
	}
	return false;
}

/*
=====================
botAi::PointReachableAreaNum
=====================
*/
int botAi::PointReachableAreaNum( const idVec3 &pos, const float boundsScale ) const {
	int areaNum;
	idVec3 size;
	idBounds bounds;

	if ( !aas ) {
		return 0;
	}

	size = aas->GetSettings()->boundingBoxes[0][1] * boundsScale;
	bounds[0] = -size;
	size.z = 32.0f;
	bounds[1] = size;

	if ( move.moveType == MOVETYPE_FLY ) {
		areaNum = aas->PointReachableAreaNum( pos, bounds, AREA_REACHABLE_WALK | AREA_REACHABLE_FLY );
	} else {
		areaNum = aas->PointReachableAreaNum( pos, bounds, AREA_REACHABLE_WALK );
	}

	return areaNum;
}

/*
=====================
botAi::PathToGoal
=====================
*/
bool botAi::PathToGoal( aasPath_t &path, int areaNum, const idVec3 &origin, int goalAreaNum, const idVec3 &goalOrigin ) const {
	idVec3 org;
	idVec3 goal;

	if ( !aas ) {
		return false;
	}

	org = origin;
	aas->PushPointIntoAreaNum( areaNum, org );
	if ( !areaNum ) {
		return false;
	}

	goal = goalOrigin;
	aas->PushPointIntoAreaNum( goalAreaNum, goal );
	if ( !goalAreaNum ) {
		return false;
	}

	if ( move.moveType == MOVETYPE_FLY ) {
		return aas->FlyPathToGoal( path, areaNum, org, goalAreaNum, goal, travelFlags );
	} else {
		return aas->WalkPathToGoal( path, areaNum, org, goalAreaNum, goal, travelFlags );
	}
}

/*
=====================
botAi::TravelDistance

Returns the approximate travel distance from one position to the goal, or if no AAS, the straight line distance.

This is feakin' slow, so it's not good to do it too many times per frame.  It also is slower the further you
are from the goal, so try to break the goals up into shorter distances.
=====================
*/
float botAi::TravelDistance( const idVec3 &start, const idVec3 &end ) const {
	int			fromArea;
	int			toArea;
	float		dist;
	idVec2		delta;
	aasPath_t	path;

	if ( !aas ) {
		// no aas, so just take the straight line distance
		delta = end.ToVec2() - start.ToVec2();
		dist = delta.LengthFast();

		if ( ai_debugMove.GetBool() ) {
			gameRenderWorld->DebugLine( colorBlue, start, end, gameLocal.msec, false );
			gameRenderWorld->DrawText( va( "%d", ( int )dist ), ( start + end ) * 0.5f, 0.1f, colorWhite, gameLocal.GetLocalPlayer()->viewAngles.ToMat3() );
		}

		return dist;
	}

	fromArea = PointReachableAreaNum( start );
	toArea = PointReachableAreaNum( end );

	if ( !fromArea || !toArea ) {
		// can't seem to get there
		return -1;
	}

	if ( fromArea == toArea ) {
		// same area, so just take the straight line distance
		delta = end.ToVec2() - start.ToVec2();
		dist = delta.LengthFast();

		if ( ai_debugMove.GetBool() ) {
			gameRenderWorld->DebugLine( colorBlue, start, end, gameLocal.msec, false );
			gameRenderWorld->DrawText( va( "%d", ( int )dist ), ( start + end ) * 0.5f, 0.1f, colorWhite, gameLocal.GetLocalPlayer()->viewAngles.ToMat3() );
		}

		return dist;
	}

	idReachability *reach;
	int travelTime;
	if ( !aas->RouteToGoalArea( fromArea, start, toArea, travelFlags, travelTime, &reach ) ) {
		return -1;
	}

	if ( ai_debugMove.GetBool() ) {
		if ( move.moveType == MOVETYPE_FLY ) {
			aas->ShowFlyPath( start, toArea, end );
		} else {
			aas->ShowWalkPath( start, toArea, end );
		}
	}

	return travelTime;
}

/*
=====================
botAi::StopMove
=====================
*/
void botAi::StopMove( moveStatus_t status ) {
	AI_MOVE_DONE		= true;
	//AI_FORWARD			= false;
	move.moveCommand	= MOVE_NONE;
	move.moveStatus		= status;
	move.toAreaNum		= 0;
	move.goalEntity		= NULL;
	move.moveDest		= physicsObject->GetOrigin();
	AI_DEST_UNREACHABLE	= false;
	AI_OBSTACLE_IN_PATH = false;
	AI_BLOCKED			= false;
	move.startTime		= gameLocal.time;
	move.duration		= 0;
	move.range			= 0.0f;
	move.speed			= 0.0f;
	move.anim			= 0;
	move.moveDir.Zero();
	move.lastMoveOrigin.Zero();
	move.lastMoveTime	= gameLocal.time;
}

/*
=====================
botAi::SetMoveToEnemy
=====================
*/
bool botAi::SetMoveToEnemy( void ) {
	int			areaNum;
	aasPath_t	path;
	idActor		*enemyEnt = enemy.GetEntity();

	if ( !enemyEnt ) {
		StopMove( MOVE_STATUS_DEST_NOT_FOUND );
		return false;
	}

	if ( ReachedPos( lastVisibleReachableEnemyPos, MOVE_TO_ENEMY ) ) {
		if ( !ReachedPos( lastVisibleEnemyPos, MOVE_TO_ENEMY ) || !AI_ENEMY_VISIBLE ) {
			StopMove( MOVE_STATUS_DEST_UNREACHABLE );
			AI_DEST_UNREACHABLE = true;
			return false;
		}
		StopMove( MOVE_STATUS_DONE );
		return true;
	}

	idVec3 pos = lastVisibleReachableEnemyPos;

	move.toAreaNum = 0;
	if ( aas ) {
		move.toAreaNum = PointReachableAreaNum( pos );
		aas->PushPointIntoAreaNum( move.toAreaNum, pos );

		areaNum	= PointReachableAreaNum( physicsObject->GetOrigin() );
		if ( !PathToGoal( path, areaNum, physicsObject->GetOrigin(), move.toAreaNum, pos ) ) {
			AI_DEST_UNREACHABLE = true;
			return false;
		}
	}

	if ( !move.toAreaNum ) {
		// if only trying to update the enemy position
		if ( move.moveCommand == MOVE_TO_ENEMY ) {
			if ( !aas ) {
				// keep the move destination up to date for wandering
				move.moveDest = pos;
			}
			return false;
		}

		if ( !NewWanderDir( pos ) ) {
			StopMove( MOVE_STATUS_DEST_UNREACHABLE );
			AI_DEST_UNREACHABLE = true;
			return false;
		}
	}

	if ( move.moveCommand != MOVE_TO_ENEMY ) {
		move.moveCommand	= MOVE_TO_ENEMY;
		move.startTime		= gameLocal.time;
	}

	move.moveDest		= pos;
	move.goalEntity		= enemyEnt;
//	move.speed			= fly_speed;
	move.moveStatus		= MOVE_STATUS_MOVING;
	AI_MOVE_DONE		= false;
	AI_DEST_UNREACHABLE = false;
	//AI_FORWARD			= true;

	return true;
}

/*
=====================
botAi::SetMoveToEntity
=====================
*/
bool botAi::SetMoveToEntity( idEntity *ent ) {
	int			areaNum;
	aasPath_t	path;
	idVec3		pos;

	if ( !ent ) {
		StopMove( MOVE_STATUS_DEST_NOT_FOUND );
		return false;
	}

	pos = ent->GetPhysics()->GetOrigin();
	if ( ( move.moveType != MOVETYPE_FLY ) && ( ( move.moveCommand != MOVE_TO_ENTITY ) || ( move.goalEntityOrigin != pos ) ) ) {
		ent->GetFloorPos( 64.0f, pos );
	}

	if ( ReachedPos( pos, MOVE_TO_ENTITY ) ) {
		StopMove( MOVE_STATUS_DONE );
		return true;
	}

	move.toAreaNum = 0;
	if ( aas ) {
		move.toAreaNum = PointReachableAreaNum( pos );
		aas->PushPointIntoAreaNum( move.toAreaNum, pos );

		areaNum	= PointReachableAreaNum( physicsObject->GetOrigin() );
		if ( !PathToGoal( path, areaNum, physicsObject->GetOrigin(), move.toAreaNum, pos ) ) {
			AI_DEST_UNREACHABLE = true;
			return false;
		}
	}

	if ( !move.toAreaNum ) {
		// if only trying to update the entity position
		if ( move.moveCommand == MOVE_TO_ENTITY ) {
			if ( !aas ) {
				// keep the move destination up to date for wandering
				move.moveDest = pos;
			}
			return false;
		}

		if ( !NewWanderDir( pos ) ) {
			StopMove( MOVE_STATUS_DEST_UNREACHABLE );
			AI_DEST_UNREACHABLE = true;
			return false;
		}
	}

	if ( ( move.moveCommand != MOVE_TO_ENTITY ) || ( move.goalEntity.GetEntity() != ent ) ) {
		move.startTime		= gameLocal.time;
		move.goalEntity		= ent;
		move.moveCommand	= MOVE_TO_ENTITY;
	}

	move.moveDest			= pos;

	move.goalEntityOrigin	= ent->GetPhysics()->GetOrigin();
	move.moveStatus			= MOVE_STATUS_MOVING;
	//move.speed				= fly_speed;
	AI_MOVE_DONE			= false;
	AI_DEST_UNREACHABLE		= false;
	//AI_FORWARD				= true;

	return true;
}

/*
=====================
botAi::SetMoveOutOfRange
=====================
*/
bool botAi::SetMoveOutOfRange( idEntity *ent, float range ) {
	int				areaNum;
	aasObstacle_t	obstacle;
	aasGoal_t		goal;
	idBounds		bounds;
	idVec3			pos;

	if ( !aas || !ent ) {
		StopMove( MOVE_STATUS_DEST_UNREACHABLE );
		AI_DEST_UNREACHABLE = true;
		return false;
	}

	const idVec3 &org = physicsObject->GetOrigin();
	areaNum	= PointReachableAreaNum( org );

	// consider the entity the monster is getting close to as an obstacle
	obstacle.absBounds = ent->GetPhysics()->GetAbsBounds();

	if ( ent == enemy.GetEntity() ) {
		pos = lastVisibleEnemyPos;
	} else {
		pos = ent->GetPhysics()->GetOrigin();
	}

	idAASFindAreaOutOfRange findGoal( pos, range );
	if ( !aas->FindNearestGoal( goal, areaNum, org, pos, travelFlags, &obstacle, 1, findGoal ) ) {
		StopMove( MOVE_STATUS_DEST_UNREACHABLE );
		AI_DEST_UNREACHABLE = true;
		return false;
	}

	if ( ReachedPos( goal.origin, move.moveCommand ) ) {
		StopMove( MOVE_STATUS_DONE );
		return true;
	}

	move.moveDest		= goal.origin;
	move.toAreaNum		= goal.areaNum;
	move.goalEntity		= ent;
	move.moveCommand	= MOVE_OUT_OF_RANGE;
	move.moveStatus		= MOVE_STATUS_MOVING;
	move.range			= range;
//	move.speed			= fly_speed;
	move.startTime		= gameLocal.time;
	AI_MOVE_DONE		= false;
	AI_DEST_UNREACHABLE = false;
	//AI_FORWARD			= true;

	return true;
}

/*
=====================
botAi::SetMoveToAttackPosition
=====================
*/
bool botAi::SetMoveToAttackPosition( idEntity *ent, int attack_anim ) {
	int				areaNum;
	aasObstacle_t	obstacle;
	aasGoal_t		goal;
	idBounds		bounds;
	idVec3			pos;

	if ( !aas || !ent ) {
		StopMove( MOVE_STATUS_DEST_UNREACHABLE );
		AI_DEST_UNREACHABLE = true;
		return false;
	}

	const idVec3 &org = physicsObject->GetOrigin();
	areaNum	= PointReachableAreaNum( org );

	// consider the entity the monster is getting close to as an obstacle
	obstacle.absBounds = ent->GetPhysics()->GetAbsBounds();

	if ( ent == enemy.GetEntity() ) {
		pos = lastVisibleEnemyPos;
	} else {
		pos = ent->GetPhysics()->GetOrigin();
	}

	idAI * pAI = reinterpret_cast<idAI *&>( playerEnt ); // TinMan: *todo* is this nice?
	idAASFindAttackPosition findGoal( pAI, physicsObject->GetGravityAxis(), ent, pos, playerEnt->EyeOffset() );
	if ( !aas->FindNearestGoal( goal, areaNum, org, pos, travelFlags, &obstacle, 1, findGoal ) ) {
		StopMove( MOVE_STATUS_DEST_UNREACHABLE );
		AI_DEST_UNREACHABLE = true;
		return false;
	}

	move.moveDest		= goal.origin;
	move.toAreaNum		= goal.areaNum;
	move.goalEntity		= ent;
	move.moveCommand	= MOVE_TO_ATTACK_POSITION;
	move.moveStatus		= MOVE_STATUS_MOVING;
	//move.speed			= fly_speed;
	move.startTime		= gameLocal.time;
	move.anim			= attack_anim;
	AI_MOVE_DONE		= false;
	AI_DEST_UNREACHABLE = false;
	//AI_FORWARD			= true;

	return true;
}

/*
=====================
botAi::SetMoveToPosition
=====================
*/
bool botAi::SetMoveToPosition( const idVec3 &pos ) {
	idVec3		org;
	int			areaNum;
	aasPath_t	path;

	if ( ReachedPos( pos, move.moveCommand ) ) {
		StopMove( MOVE_STATUS_DONE );
		return true;
	}

	org = pos;
	move.toAreaNum = 0;
	if ( aas ) {
		move.toAreaNum = PointReachableAreaNum( org );
		aas->PushPointIntoAreaNum( move.toAreaNum, org );

		areaNum	= PointReachableAreaNum( physicsObject->GetOrigin() );
		if ( !PathToGoal( path, areaNum, physicsObject->GetOrigin(), move.toAreaNum, org ) ) {
			StopMove( MOVE_STATUS_DEST_UNREACHABLE );
			AI_DEST_UNREACHABLE = true;
			return false;
		}
	}

	if ( !move.toAreaNum && !NewWanderDir( org ) ) {
		StopMove( MOVE_STATUS_DEST_UNREACHABLE );
		AI_DEST_UNREACHABLE = true;
		return false;
	}

	move.moveDest		= org;
	move.goalEntity		= NULL;
	move.moveCommand	= MOVE_TO_POSITION;
	move.moveStatus		= MOVE_STATUS_MOVING;
	move.startTime		= gameLocal.time;
//	move.speed			= fly_speed;
	AI_MOVE_DONE		= false;
	AI_DEST_UNREACHABLE = false;
	//AI_FORWARD			= true;

	return true;
}

/*
=====================
botAi::SetMoveToCover
=====================
*/
bool botAi::SetMoveToCover( idEntity *entity, const idVec3 &hideFromPos ) {
	int				areaNum;
	aasObstacle_t	obstacle;
	aasGoal_t		hideGoal;
	idBounds		bounds;

	if ( !aas || !entity ) {
		StopMove( MOVE_STATUS_DEST_UNREACHABLE );
		AI_DEST_UNREACHABLE = true;
		return false;
	}

	const idVec3 &org = physicsObject->GetOrigin();
	areaNum	= PointReachableAreaNum( org );

	// consider the entity the monster tries to hide from as an obstacle
	obstacle.absBounds = entity->GetPhysics()->GetAbsBounds();

	idAASFindCover findCover( hideFromPos );
	if ( !aas->FindNearestGoal( hideGoal, areaNum, org, hideFromPos, travelFlags, &obstacle, 1, findCover ) ) {
		StopMove( MOVE_STATUS_DEST_UNREACHABLE );
		AI_DEST_UNREACHABLE = true;
		return false;
	}

	if ( ReachedPos( hideGoal.origin, move.moveCommand ) ) {
		StopMove( MOVE_STATUS_DONE );
		return true;
	}

	move.moveDest		= hideGoal.origin;
	move.toAreaNum		= hideGoal.areaNum;
	move.goalEntity		= entity;
	move.moveCommand	= MOVE_TO_COVER;
	move.moveStatus		= MOVE_STATUS_MOVING;
	move.startTime		= gameLocal.time;
//	move.speed			= fly_speed;
	AI_MOVE_DONE		= false;
	AI_DEST_UNREACHABLE = false;
	//AI_FORWARD			= true;

	return true;
}

/*
=====================
botAi::WanderAround
=====================
*/
bool botAi::WanderAround( void ) {
	StopMove( MOVE_STATUS_DONE );
	
	move.moveDest = physicsObject->GetOrigin() + playerEnt->viewAxis[ 0 ] * physicsObject->GetGravityAxis() * 256.0f;
	if ( !NewWanderDir( move.moveDest ) ) {
		StopMove( MOVE_STATUS_DEST_UNREACHABLE );
		AI_DEST_UNREACHABLE = true;
		return false;
	}

	move.moveCommand	= MOVE_WANDER;
	move.moveStatus		= MOVE_STATUS_MOVING;
	move.startTime		= gameLocal.time;
//	move.speed			= fly_speed;
	AI_MOVE_DONE		= false;
	//AI_FORWARD			= true;

	return true;
}

/*
=====================
botAi::MoveDone
=====================
*/
bool botAi::MoveDone( void ) const {
	return ( move.moveCommand == MOVE_NONE );
}

/*
================
botAi::StepDirection
================
*/
bool botAi::StepDirection( float dir ) {
	predictedPath_t path;
	idVec3 org;

	move.wanderYaw = dir;
	move.moveDir = idAngles( 0, move.wanderYaw, 0 ).ToForward();

	org = physicsObject->GetOrigin();

	idAI::PredictPath( playerEnt, aas, org, move.moveDir * 48.0f, 1000, 1000, ( move.moveType == MOVETYPE_FLY ) ? SE_BLOCKED : ( SE_ENTER_OBSTACLE | SE_BLOCKED | SE_ENTER_LEDGE_AREA ), path );

	if ( path.blockingEntity && ( ( move.moveCommand == MOVE_TO_ENEMY ) || ( move.moveCommand == MOVE_TO_ENTITY ) ) && ( path.blockingEntity == move.goalEntity.GetEntity() ) ) {
		// don't report being blocked if we ran into our goal entity
		return true;
	}

	if ( ( move.moveType == MOVETYPE_FLY ) && ( path.endEvent == SE_BLOCKED ) ) {
		float z;

		move.moveDir = path.endVelocity * 1.0f / 48.0f;

		// trace down to the floor and see if we can go forward
		idAI::PredictPath( playerEnt, aas, org, idVec3( 0.0f, 0.0f, -1024.0f ), 1000, 1000, SE_BLOCKED, path );

		idVec3 floorPos = path.endPos;
		idAI::PredictPath( playerEnt, aas, floorPos, move.moveDir * 48.0f, 1000, 1000, SE_BLOCKED, path );
		if ( !path.endEvent ) {
			move.moveDir.z = -1.0f;
			return true;
		}

		// trace up to see if we can go over something and go forward
		idAI::PredictPath( playerEnt, aas, org, idVec3( 0.0f, 0.0f, 256.0f ), 1000, 1000, SE_BLOCKED, path );

		idVec3 ceilingPos = path.endPos;

		for( z = org.z; z <= ceilingPos.z + 64.0f; z += 64.0f ) {
			idVec3 start;
			if ( z <= ceilingPos.z ) {
				start.x = org.x;
				start.y = org.y;
                start.z = z;
			} else {
				start = ceilingPos;
			}
			idAI::PredictPath( playerEnt, aas, start, move.moveDir * 48.0f, 1000, 1000, SE_BLOCKED, path );
			if ( !path.endEvent ) {
				move.moveDir.z = 1.0f;
				return true;
			}
		}
		return false;
	}

	return ( path.endEvent == 0 );
}

/*
================
botAi::NewWanderDir
================
*/
bool botAi::NewWanderDir( const idVec3 &dest ) {
	float	deltax, deltay;
	float	d[ 3 ];
	float	tdir, olddir, turnaround;

	move.nextWanderTime = gameLocal.time + ( gameLocal.random.RandomFloat() * 500 + 500 );

	olddir = idMath::AngleNormalize360( ( int )( playerEnt->viewAngles.yaw / 45 ) * 45 );
	turnaround = idMath::AngleNormalize360( olddir - 180 );

	idVec3 org = physicsObject->GetOrigin();
	deltax = dest.x - org.x;
	deltay = dest.y - org.y;
	if ( deltax > 10 ) {
		d[ 1 ]= 0;
	} else if ( deltax < -10 ) {
		d[ 1 ] = 180;
	} else {
		d[ 1 ] = DI_NODIR;
	}

	if ( deltay < -10 ) {
		d[ 2 ] = 270;
	} else if ( deltay > 10 ) {
		d[ 2 ] = 90;
	} else {
		d[ 2 ] = DI_NODIR;
	}

	// try direct route
	if ( d[ 1 ] != DI_NODIR && d[ 2 ] != DI_NODIR ) {
		if ( d[ 1 ] == 0 ) {
			tdir = d[ 2 ] == 90 ? 45 : 315;
		} else {
			tdir = d[ 2 ] == 90 ? 135 : 215;
		}

		if ( tdir != turnaround && StepDirection( tdir ) ) {
			return true;
		}
	}

	// try other directions
	if ( ( gameLocal.random.RandomInt() & 1 ) || abs( deltay ) > abs( deltax ) ) {
		tdir = d[ 1 ];
		d[ 1 ] = d[ 2 ];
		d[ 2 ] = tdir;
	}

	if ( d[ 1 ] != DI_NODIR && d[ 1 ] != turnaround && StepDirection( d[1] ) ) {
		return true;
	}

	if ( d[ 2 ] != DI_NODIR && d[ 2 ] != turnaround	&& StepDirection( d[ 2 ] ) ) {
		return true;
	}

	// there is no direct path to the player, so pick another direction
	if ( olddir != DI_NODIR && StepDirection( olddir ) ) {
		return true;
	}

	 // randomly determine direction of search
	if ( gameLocal.random.RandomInt() & 1 ) {
		for( tdir = 0; tdir <= 315; tdir += 45 ) {
			if ( tdir != turnaround && StepDirection( tdir ) ) {
                return true;
			}
		}
	} else {
		for ( tdir = 315; tdir >= 0; tdir -= 45 ) {
			if ( tdir != turnaround && StepDirection( tdir ) ) {
				return true;
			}
		}
	}

	if ( turnaround != DI_NODIR && StepDirection( turnaround ) ) {
		return true;
	}

	// can't move
	StopMove( MOVE_STATUS_DEST_UNREACHABLE );
	return false;
}

/*
=====================
botAi::GetMovePos
=====================
*/
bool botAi::GetMovePos( idVec3 &seekPos ) {
	int			areaNum;
	aasPath_t	path;
	bool		result;
	idVec3		org;

	org = physicsObject->GetOrigin();
	seekPos = org;

	switch( move.moveCommand ) {
	case MOVE_NONE :
		seekPos = move.moveDest;
		return false;
		break;

	case MOVE_FACE_ENEMY :
	case MOVE_FACE_ENTITY :
		seekPos = move.moveDest;
		return false;
		break;

	case MOVE_TO_POSITION_DIRECT :
		seekPos = move.moveDest;
		if ( ReachedPos( move.moveDest, move.moveCommand ) ) {
			StopMove( MOVE_STATUS_DONE );
		}
		return false;
		break;
	
	case MOVE_SLIDE_TO_POSITION :
		seekPos = org;
		return false;
		break;
	}

	if ( move.moveCommand == MOVE_TO_ENTITY ) {
		SetMoveToEntity( move.goalEntity.GetEntity() );
	}

	move.moveStatus = MOVE_STATUS_MOVING;
	result = false;
	if ( gameLocal.time > move.blockTime ) {
		if ( move.moveCommand == MOVE_WANDER ) {
			move.moveDest = org + playerEnt->viewAxis[ 0 ] * physicsObject->GetGravityAxis() * 256.0f;
		} else {
			if ( ReachedPos( move.moveDest, move.moveCommand ) ) {
				StopMove( MOVE_STATUS_DONE );
				seekPos	= org;
				return false;
			}
		}

		if ( aas && move.toAreaNum ) {
			areaNum	= PointReachableAreaNum( org );
			if ( PathToGoal( path, areaNum, org, move.toAreaNum, move.moveDest ) ) {
				seekPos = path.moveGoal;
				result = true;
				move.nextWanderTime = 0;
			} else {
				AI_DEST_UNREACHABLE = true;
			}
		}
	}

	if ( !result ) {
		// wander around
		if ( ( gameLocal.time > move.nextWanderTime ) || !StepDirection( move.wanderYaw ) ) {
			result = NewWanderDir( move.moveDest );
			if ( !result ) {
				StopMove( MOVE_STATUS_DEST_UNREACHABLE );
				AI_DEST_UNREACHABLE = true;
				seekPos	= org;
				return false;
			}
		} else {
			result = true;
		}

		seekPos = org + move.moveDir * 2048.0f;
		if ( ai_debugMove.GetBool() ) {
			gameRenderWorld->DebugLine( colorYellow, org, seekPos, gameLocal.msec, true );
		}
	} else {
		AI_DEST_UNREACHABLE = false;
	}

	if ( result && ( ai_debugMove.GetBool() ) ) {
		gameRenderWorld->DebugLine( colorCyan, physicsObject->GetOrigin(), seekPos );
	}

	return result;
}

/*
=====================
botAi::EntityCanSeePos
=====================
*/
bool botAi::EntityCanSeePos( idActor *actor, const idVec3 &actorOrigin, const idVec3 &pos ) {
	idVec3 eye, point;
	trace_t results;
	pvsHandle_t handle;

	handle = gameLocal.pvs.SetupCurrentPVS( actor->GetPVSAreas(), actor->GetNumPVSAreas() );

	if ( !gameLocal.pvs.InCurrentPVS( handle, GetPVSAreas(), GetNumPVSAreas() ) ) {
		gameLocal.pvs.FreeCurrentPVS( handle );
		return false;
	}

	gameLocal.pvs.FreeCurrentPVS( handle );

	eye = actorOrigin + actor->EyeOffset();

	point = pos;
	point[2] += 1.0f;

	physicsObject->DisableClip();

	gameLocal.clip.TracePoint( results, eye, point, MASK_SOLID, actor );
	if ( results.fraction >= 1.0f || ( gameLocal.GetTraceEntity( results ) == playerEnt ) ) {
		physicsObject->EnableClip();
		return true;
	}

	const idBounds &bounds = physicsObject->GetBounds();
	point[2] += bounds[1][2] - bounds[0][2];

	gameLocal.clip.TracePoint( results, eye, point, MASK_SOLID, actor );
	physicsObject->EnableClip();
	if ( results.fraction >= 1.0f || ( gameLocal.GetTraceEntity( results ) == playerEnt ) ) {
		return true;
	}
	return false;
}

/*
=====================
botAi::BlockedFailSafe
=====================
*/
void botAi::BlockedFailSafe( void ) {
	if ( !ai_blockedFailSafe.GetBool() || blockedRadius < 0.0f ) {
		return;
	}
	if ( !physicsObject->HasGroundContacts() || enemy.GetEntity() == NULL ||
			( physicsObject->GetOrigin() - move.lastMoveOrigin ).LengthSqr() > Square( blockedRadius ) ) {
		move.lastMoveOrigin = physicsObject->GetOrigin();
		move.lastMoveTime = gameLocal.time;
	}
	if ( move.lastMoveTime < gameLocal.time - blockedMoveTime ) {
		//if ( lastAttackTime < gameLocal.time - blockedAttackTime ) {
			AI_BLOCKED = true;
			move.lastMoveTime = gameLocal.time;
		//}
	}
}

/*
=====================
botAi::CheckObstacleAvoidance
=====================
*/
void botAi::CheckObstacleAvoidance( const idVec3 &goalPos, idVec3 &newPos ) {
	idEntity		*obstacle;
	obstaclePath_t	path;
	idVec3			dir;
	float			dist;
	bool			foundPath;

	if ( ignore_obstacles ) {
		newPos = goalPos;
		move.obstacle = NULL;
		return;
	}

	const idVec3 &origin = physicsObject->GetOrigin();

	obstacle = NULL;
	AI_OBSTACLE_IN_PATH = false;
	foundPath = idAI::FindPathAroundObstacles( playerEnt->GetPhysics(), aas, enemy.GetEntity(), origin, goalPos, path );
	if ( ai_showObstacleAvoidance.GetBool() ) {
		gameRenderWorld->DebugLine( colorBlue, goalPos + idVec3( 1.0f, 1.0f, 0.0f ), goalPos + idVec3( 1.0f, 1.0f, 64.0f ), gameLocal.msec );
		gameRenderWorld->DebugLine( foundPath ? colorYellow : colorRed, path.seekPos, path.seekPos + idVec3( 0.0f, 0.0f, 64.0f ), gameLocal.msec );
	}

	if ( !foundPath ) {
		// couldn't get around obstacles
		if ( path.firstObstacle ) {
			AI_OBSTACLE_IN_PATH = true;
			if ( physicsObject->GetAbsBounds().Expand( 2.0f ).IntersectsBounds( path.firstObstacle->GetPhysics()->GetAbsBounds() ) ) {
				obstacle = path.firstObstacle;
			}
		} else if ( path.startPosObstacle ) {
			AI_OBSTACLE_IN_PATH = true;
			if ( physicsObject->GetAbsBounds().Expand( 2.0f ).IntersectsBounds( path.startPosObstacle->GetPhysics()->GetAbsBounds() ) ) {
				obstacle = path.startPosObstacle;
			}
		} else {
			// Blocked by wall
			move.moveStatus = MOVE_STATUS_BLOCKED_BY_WALL;
		}
#if 0
	} else if ( path.startPosObstacle ) {
		// check if we're past where the our origin was pushed out of the obstacle
		dir = goalPos - origin;
		dir.Normalize();
		dist = ( path.seekPos - origin ) * dir;
		if ( dist < 1.0f ) {
			AI_OBSTACLE_IN_PATH = true;
			obstacle = path.startPosObstacle;
		}
#endif
	} else if ( path.seekPosObstacle ) {
		// if the AI is very close to the path.seekPos already and path.seekPosObstacle != NULL
		// then we want to push the path.seekPosObstacle entity out of the way
		AI_OBSTACLE_IN_PATH = true;

		// check if we're past where the goalPos was pushed out of the obstacle
		dir = goalPos - origin;
		dir.Normalize();
		dist = ( path.seekPos - origin ) * dir;
		if ( dist < 1.0f ) {
			obstacle = path.seekPosObstacle;
		}
	}

	// if we had an obstacle, set our move status based on the type, and kick it out of the way if it's a moveable
	if ( obstacle ) {
		if ( obstacle->IsType( idActor::Type ) ) {
			// monsters aren't kickable
			if ( obstacle == enemy.GetEntity() ) {
				move.moveStatus = MOVE_STATUS_BLOCKED_BY_ENEMY;
			} else {
				move.moveStatus = MOVE_STATUS_BLOCKED_BY_MONSTER;
			}
		} else {
			// try kicking the object out of the way
			move.moveStatus = MOVE_STATUS_BLOCKED_BY_OBJECT;
		}
		newPos = obstacle->GetPhysics()->GetOrigin();
		//newPos = path.seekPos;
		move.obstacle = obstacle;
	} else {
		newPos = path.seekPos;
		move.obstacle = NULL;
	}
}

/*
=====================
botAi::GetMovePosition
=====================
*/
idVec3 botAi::GetMovePosition( void ) {
	idVec3				goalPos;
	idVec3				newDest;

	idVec3 oldorigin = physicsObject->GetOrigin();

	AI_BLOCKED = false;

	if ( move.moveCommand < NUM_NONMOVING_COMMANDS ){ 
		move.lastMoveOrigin.Zero();
		move.lastMoveTime = gameLocal.time;
	}

	move.obstacle = NULL;
	if ( GetMovePos( goalPos ) ) {
		if ( move.moveCommand != MOVE_WANDER ) {
			CheckObstacleAvoidance( goalPos, newDest );
			goalPos = newDest;
		}
	}

	if ( ai_debugMove.GetBool() ) {
		gameRenderWorld->DebugLine( colorCyan, oldorigin, physicsObject->GetOrigin(), 5000 );
	}

	BlockedFailSafe();

	if ( ai_debugMove.GetBool() ) {
		gameRenderWorld->DebugBounds( colorMagenta, physicsObject->GetBounds(), physicsObject->GetOrigin(), gameLocal.msec );
		gameRenderWorld->DebugBounds( colorMagenta, physicsObject->GetBounds(), move.moveDest, gameLocal.msec );
		gameRenderWorld->DebugLine( colorYellow, physicsObject->GetOrigin() + playerEnt->EyeOffset(), physicsObject->GetOrigin() + playerEnt->EyeOffset() + playerEnt->viewAxis[ 0 ] * physicsObject->GetGravityAxis() * 16.0f, gameLocal.msec, true );
		DrawRoute();
	}

	return goalPos;
}

/*
=====================
botAi::EnemyDead
=====================
*/
void botAi::EnemyDead( void ) {
	ClearEnemy();
	AI_ENEMY_DEAD = true;
}

/*
=====================
botAi::GetEnemy
=====================
*/
idActor	*botAi::GetEnemy( void ) const {
	return enemy.GetEntity();
}

/*
=====================
botAi::ClearEnemy
=====================
*/
void botAi::ClearEnemy( void ) {
	if ( move.moveCommand == MOVE_TO_ENEMY ) {
		StopMove( MOVE_STATUS_DEST_NOT_FOUND );
	}

	playerEnt->enemyNode.Remove();
	enemy				= NULL;
	AI_ENEMY_IN_FOV		= false;
	AI_ENEMY_VISIBLE	= false;
	AI_ENEMY_DEAD		= true;

	//SetChatSound();
}

/*
=====================
botAi::EnemyPositionValid
=====================
*/
bool botAi::EnemyPositionValid( void ) const {
	trace_t	tr;
	idVec3	muzzle;
	idMat3	axis;

	if ( !enemy.GetEntity() ) {
		return false;
	}

	if ( AI_ENEMY_VISIBLE ) {
		return true;
	}

	gameLocal.clip.TracePoint( tr, playerEnt->GetEyePosition(), lastVisibleEnemyPos + lastVisibleEnemyEyeOffset, MASK_OPAQUE, playerEnt );
	if ( tr.fraction < 1.0f ) {
		// can't see the area yet, so don't know if he's there or not
		return true;
	}

	return false;
}

/*
=====================
botAi::SetEnemyPosition
=====================
*/
void botAi::SetEnemyPosition( void ) {
	idActor		*enemyEnt = enemy.GetEntity();
	int			enemyAreaNum;
	int			areaNum;
	int			lastVisibleReachableEnemyAreaNum;
	aasPath_t	path;
	idVec3		pos;
	bool		onGround;

	if ( !enemyEnt ) {
		return;
	}

	lastVisibleReachableEnemyPos = lastReachableEnemyPos;
	lastVisibleEnemyEyeOffset = enemyEnt->EyeOffset();
	lastVisibleEnemyPos = enemyEnt->GetPhysics()->GetOrigin();
	if ( move.moveType == MOVETYPE_FLY ) {
		pos = lastVisibleEnemyPos;
		onGround = true;
	} else {
		onGround = enemyEnt->GetFloorPos( 64.0f, pos );
		if ( enemyEnt->OnLadder() ) {
			onGround = false;
		}
	}

	if ( !onGround ) {
		if ( move.moveCommand == MOVE_TO_ENEMY ) {
			AI_DEST_UNREACHABLE = true;
		}
		return;
	}

	// when we don't have an AAS, we can't tell if an enemy is reachable or not,
	// so just assume that he is.
	if ( !aas ) {
		lastVisibleReachableEnemyPos = lastVisibleEnemyPos;
		if ( move.moveCommand == MOVE_TO_ENEMY ) {
			AI_DEST_UNREACHABLE = false;
		}
		enemyAreaNum = 0;
		areaNum = 0;
	} else {
		lastVisibleReachableEnemyAreaNum = move.toAreaNum;
		enemyAreaNum = PointReachableAreaNum( lastVisibleEnemyPos, 1.0f );
		if ( !enemyAreaNum ) {
			enemyAreaNum = PointReachableAreaNum( lastReachableEnemyPos, 1.0f );
			pos = lastReachableEnemyPos;
		}
		if ( !enemyAreaNum ) {
			if ( move.moveCommand == MOVE_TO_ENEMY ) {
				AI_DEST_UNREACHABLE = true;
			}
			areaNum = 0;
		} else {
			const idVec3 &org = physicsObject->GetOrigin();
			areaNum = PointReachableAreaNum( org );
			if ( PathToGoal( path, areaNum, org, enemyAreaNum, pos ) ) {
				lastVisibleReachableEnemyPos = pos;
				lastVisibleReachableEnemyAreaNum = enemyAreaNum;
				if ( move.moveCommand == MOVE_TO_ENEMY ) {
					AI_DEST_UNREACHABLE = false;
				}
			} else if ( move.moveCommand == MOVE_TO_ENEMY ) {
				AI_DEST_UNREACHABLE = true;
			}
		}
	}

	if ( move.moveCommand == MOVE_TO_ENEMY ) {
		if ( !aas ) {
			// keep the move destination up to date for wandering
			move.moveDest = lastVisibleReachableEnemyPos;
		} else if ( enemyAreaNum ) {
			move.toAreaNum = lastVisibleReachableEnemyAreaNum;
			move.moveDest = lastVisibleReachableEnemyPos;
		}
	}
}

/*
=====================
botAi::UpdateEnemyPosition
=====================
*/
void botAi::UpdateEnemyPosition( void ) {
	idActor *enemyEnt = enemy.GetEntity();
	int				enemyAreaNum;
	int				areaNum;
	aasPath_t		path;
	predictedPath_t predictedPath;
	idVec3			enemyPos;
	bool			onGround;

	if ( !enemyEnt ) {
		return;
	}

	const idVec3 &org = physicsObject->GetOrigin();

	if ( move.moveType == MOVETYPE_FLY ) {
		enemyPos = enemyEnt->GetPhysics()->GetOrigin();
		onGround = true;
	} else {
		onGround = enemyEnt->GetFloorPos( 64.0f, enemyPos );
		if ( enemyEnt->OnLadder() ) {
			onGround = false;
		}
	}

	if ( onGround ) {
		// when we don't have an AAS, we can't tell if an enemy is reachable or not,
		// so just assume that he is.
		if ( !aas ) {
			enemyAreaNum = 0;
			lastReachableEnemyPos = enemyPos;
		} else {
			enemyAreaNum = PointReachableAreaNum( enemyPos, 1.0f );
			if ( enemyAreaNum ) {
				areaNum = PointReachableAreaNum( org );
				if ( PathToGoal( path, areaNum, org, enemyAreaNum, enemyPos ) ) {
					lastReachableEnemyPos = enemyPos;
				}
			}
		}
	}

	AI_ENEMY_IN_FOV		= false;
	AI_ENEMY_VISIBLE	= false;

	if ( playerEnt->CanSee( enemyEnt, false ) ) {
		AI_ENEMY_VISIBLE = true;
		if ( playerEnt->CheckFOV( enemyEnt->GetPhysics()->GetOrigin() ) ) {
			AI_ENEMY_IN_FOV = true;
		}

		SetEnemyPosition();
	} else {
		// check if we heard any sounds in the last frame
		if ( enemyEnt == gameLocal.GetAlertEntity() ) {
			float dist = ( enemyEnt->GetPhysics()->GetOrigin() - org ).LengthSqr();
			if ( dist < Square( AI_HEARING_RANGE ) ) {
				SetEnemyPosition();
			}
		}
	}

	if ( ai_debugMove.GetBool() ) {
		gameRenderWorld->DebugBounds( colorLtGrey, enemyEnt->GetPhysics()->GetBounds(), lastReachableEnemyPos, gameLocal.msec );
		gameRenderWorld->DebugBounds( colorWhite, enemyEnt->GetPhysics()->GetBounds(), lastVisibleReachableEnemyPos, gameLocal.msec );
	}
}

/*
=====================
botAi::SetEnemy
=====================
*/
void botAi::SetEnemy( idActor *newEnemy ) {
	int enemyAreaNum;

	if ( AI_DEAD ) {
		ClearEnemy();
		return;
	}

	AI_ENEMY_DEAD = false;
	if ( !newEnemy ) {
		ClearEnemy();
	} else if ( enemy.GetEntity() != newEnemy ) {
		enemy = newEnemy;
		playerEnt->enemyNode.AddToEnd( newEnemy->enemyList );
		if ( newEnemy->health <= 0 ) {
			EnemyDead();
			return;
		}
		// let the monster know where the enemy is
		newEnemy->GetAASLocation( aas, lastReachableEnemyPos, enemyAreaNum );
		SetEnemyPosition();
		//SetChatSound();

		lastReachableEnemyPos = lastVisibleEnemyPos;
		lastVisibleReachableEnemyPos = lastReachableEnemyPos;
		enemyAreaNum = PointReachableAreaNum( lastReachableEnemyPos, 1.0f );
		if ( aas && enemyAreaNum ) {
			aas->PushPointIntoAreaNum( enemyAreaNum, lastReachableEnemyPos );
			lastVisibleReachableEnemyPos = lastReachableEnemyPos;
		}
	}
}

/***********************************************************************

	Events

***********************************************************************/

/*
=====================
botAi::Event_SetNextState
=====================
*/
void botAi::Event_SetNextState( const char *name ) {
	idealState = GetScriptFunction( name );
	if ( idealState == state ) {
		state = NULL;
	}
}

/*
=====================
botAi::Event_SetState
=====================
*/
void botAi::Event_SetState( const char *name ) {
	idealState = GetScriptFunction( name );
	if ( idealState == state ) {
		state = NULL;
	}
	scriptThread->DoneProcessing();
}

/*
=====================
botAi::Event_GetState
=====================
*/
void botAi::Event_GetState( void ) {
	if ( state ) {
		idThread::ReturnString( state->Name() );
	} else {
		idThread::ReturnString( "" );
	}
}

/*
=====================
botAi::Event_GetBody
=====================
*/
void botAi::Event_GetBody( void ) {
	idThread::ReturnEntity( playerEnt );
}

/*
=====================
botAi::Event_GetHealth
=====================
*/
void botAi::Event_GetHealth( idEntity *ent ) {
	if ( !ent->IsType( idActor::Type ) ) {
		gameLocal.Warning( "'%s' is not an idActor (player or ai controlled character)", ent->name.c_str() );
		idThread::ReturnFloat( -1 );
		return;
	}
	idActor * actor = static_cast<idActor *>( ent );
	idThread::ReturnFloat( actor->health );
}

/*
=====================
botAi::Event_GetArmor
=====================
*/
void botAi::Event_GetArmor( idEntity *ent ) {
	if ( !ent->IsType( idPlayer::Type ) ) {
		gameLocal.Warning( "'%s' is not an idPlayer", ent->name.c_str() );
		idThread::ReturnFloat( -1 );
		return;
	}
	idPlayer * player = static_cast<idPlayer *>( ent );
	idThread::ReturnFloat( player->inventory.armor );
}

/*
=====================
botAi::Event_GetTeam
TinMan: *todo* could be moved to idActor
=====================
*/
void botAi::Event_GetTeam( idEntity *ent ) {
	if ( !ent->IsType( idActor::Type ) ) {
		gameLocal.Warning( "'%s' is not an idActor (player or ai controlled character)", ent->name.c_str() );
		idThread::ReturnFloat( -1 );
		return;
	}

	idActor * actor = static_cast<idActor *>( ent );
	idThread::ReturnFloat( actor->team );
}

/*
===============
botAi::Event_HasItem
===============
*/
void botAi::Event_HasItem( const char *name ) {
	idDict *item;
	item = playerEnt->FindInventoryItem( name );
	if ( item ) {
		idThread::ReturnFloat( true );
	}
	idThread::ReturnFloat( false );
}

/*
===============
botAi::Event_HasAmmo
===============
*/
void botAi::Event_HasAmmo( const char *name ) {
	int ammo = inventory->HasAmmo( name );
	idThread::ReturnFloat( ammo );
}

/*
==================
botAi::Event_NextBestWeapon
==================
*/
void botAi::Event_NextBestWeapon( void ) {
	playerEnt->NextBestWeapon();
}

/*
================
botAi::Event_SetAimPosition
TinMan: Sets where bot should adjust view angles to
================
*/
void botAi::Event_SetAimPosition( const idVec3 &aimPos ) {
	aimPosition = aimPos;
}

/*
================
botAi::Event_GetAimPosition
TinMan: Get where bot has been told to aim
================
*/
void botAi::Event_GetAimPosition( void ) {
	idThread::ReturnVector( aimPosition );
}

/*
================
botAi::Event_GetMovePosition
TinMan: Current point to move to on set path
================
*/
void botAi::Event_GetMovePosition( void ) {
	idVec3 movePos = GetMovePosition();
	idThread::ReturnVector( movePos );
}

/*
=====================
botAi::Event_CanSeeEntity
=====================
*/
void botAi::Event_CanSeeEntity( idEntity *ent, bool useFov ) {
	if ( !ent ) {
		idThread::ReturnInt( false );
		return;
	}

	bool cansee = playerEnt->CanSee( ent, useFov );
	idThread::ReturnInt( cansee );
}

/*
================
botAi::Event_GetEyePosition
TinMan: *todo* this might be handy enough for lower down the chain, idActor perhaps?
================
*/
void botAi::Event_GetEyePosition( void ) {
	idThread::ReturnVector( playerEnt->GetEyePosition() );
}

/*
================
botAi::Event_GetAIAimTargets
================
*/
void botAi::Event_GetAIAimTargets( idEntity *aimAtEnt, float location ) {
	idVec3	headPosition;
	idVec3	chestPosition;

	static_cast<idActor *>( aimAtEnt )->GetAIAimTargets( lastVisibleEnemyPos, headPosition, chestPosition );
	if ( location == 1 ) {
		idThread::ReturnVector( headPosition );
	} else {
		idThread::ReturnVector( chestPosition );
	}
}

/*
=====================
botAi::Event_FindEnemies
TinMan: Finds all visable enemies (AI and Player) and adds them to entitySearchList[].
=====================
*/
void botAi::Event_FindEnemies( int useFOV ) {
	int			i;
	idEntity	*ent;
	idActor		*actor;
	pvsHandle_t pvs;

	// TinMan: Clear existing list
	memset( entitySearchList, 0, sizeof( entitySearchList ) );
	/* TinMan: *todo* remove this
	for ( e = 0; e == numSearchListEntities; e++ ) {
		entitySearchList[ e ] = NULL;
	}*/
	numSearchListEntities = 0; // TinMan: Reset

	pvs = gameLocal.pvs.SetupCurrentPVS( playerEnt->GetPVSAreas(), playerEnt->GetNumPVSAreas() );

	// TinMan: Find players
	for ( i = 0; i < gameLocal.numClients ; i++ ) {
		ent = gameLocal.entities[ i ];

		if ( !ent || !ent->IsType( idActor::Type ) ) {
			continue;
		}

		// TinMan: Don't want to return self
		if ( ent == playerEnt ) {
			continue;
		}

		actor = static_cast<idActor *>( ent );
		if ( ( actor->health <= 0 ) /* || !( ReactionTo( actor ) & ATTACK_ON_SIGHT ) */ ) {
			continue;
		}

		// TinMan: Friendly fire check
		if ( gameLocal.gameType == ( GAME_SP ) || gameLocal.gameType == ( GAME_TDM ) ) {
			if ( actor->team == playerEnt->team ) {
				continue;
			}
		}

		if ( !gameLocal.pvs.InCurrentPVS( pvs, actor->GetPVSAreas(), actor->GetNumPVSAreas() ) ) {
			continue;
		}

		if ( playerEnt->CanSee( actor, useFOV != 0 ) ) {
			//gameLocal.Printf("[idAI::Event_FindEnemies][Found a Player]\n" ); // TinMan: *debug*
			// TinMan: Add to new list
			entitySearchList[numSearchListEntities] = ent;
			numSearchListEntities++;
		}
	}

	// TinMan: Find AI
	for ( ent = gameLocal.activeEntities.Next(); ent != NULL; ent = ent->activeNode.Next() ) {
		if ( ent->fl.hidden || ent->fl.isDormant || !ent->IsType( idActor::Type ) ) {
			continue;
		}

		// TinMan: Players caught above^
		if ( ent->IsType( idPlayer::Type ) ) {
			continue;
		}

		// TinMan: Don't want to return self
		if ( ent == playerEnt ) {
			continue;
		}

		actor = static_cast<idActor *>( ent );
		if ( ( actor->health <= 0 ) /*|| !( ReactionTo( actor ) & ATTACK_ON_SIGHT ) */ ) {
			continue;
		}

		// TinMan: Friendly fire check
		if ( gameLocal.gameType == ( GAME_SP ) || gameLocal.gameType == ( GAME_TDM ) ) {
			if ( actor->team == playerEnt->team ) {
				continue;
			}
		}

		if ( !gameLocal.pvs.InCurrentPVS( pvs, actor->GetPVSAreas(), actor->GetNumPVSAreas() ) ) {
			continue;
		}

		if ( playerEnt->CanSee( actor, useFOV != 0 ) ) {
			//gameLocal.Printf("[idAI::Event_FindEnemies][Found an AI]\n" ); // TinMan: *debug*
			// TinMan: Add to new list
			entitySearchList[numSearchListEntities] = ent;
			numSearchListEntities++;
		}
	}

	gameLocal.pvs.FreeCurrentPVS( pvs );

	idThread::ReturnFloat( numSearchListEntities );
}

/*
============
botAi::Event_FindInRadius
TinMan: Finds all visable entities of specified type in radius and adds them to entitySearchList[].
TinMan: Interestingly gamelocal has a radius function but it seems incomplete, just doing the bounds cull.
============
*/
void botAi::Event_FindInRadius( const idVec3 &origin, float radius, const char *classname ) {
	float		dist;
	idEntity *	ent;
	idEntity *	entityList[ MAX_GENTITIES ];
	int			numListedEntities;
	idBounds	bounds;
	idVec3 		v, dir;
	int			i, e;

	// TinMan: Clear existing list
	memset( entitySearchList, 0, sizeof( entitySearchList ) );
	numSearchListEntities = 0; // TinMan: Reset

	if ( radius < 1 ) {
		radius = 1;
	}

	bounds = idBounds( origin ).Expand( radius );

	// id: get all entities touching the bounds
	numListedEntities = gameLocal.clip.EntitiesTouchingBounds( bounds, -1, entityList, MAX_GENTITIES );

	for ( e = 0; e < numListedEntities; e++ ) {
		ent = entityList[ e ];
		assert( ent );

		// TinMan: If it's not of specidied classname skip it
		if ( idStr::Icmp( ent->GetClassname(), classname ) != 0 ) { 
			continue;
		}

		// id: find the distance from the edge of the bounding box
		for ( i = 0; i < 3; i++ ) {
			if ( origin[ i ] < ent->GetPhysics()->GetAbsBounds()[0][ i ] ) {
				v[ i ] = ent->GetPhysics()->GetAbsBounds()[0][ i ] - origin[ i ];
			} else if ( origin[ i ] > ent->GetPhysics()->GetAbsBounds()[1][ i ] ) {
				v[ i ] = origin[ i ] - ent->GetPhysics()->GetAbsBounds()[1][ i ];
			} else {
				v[ i ] = 0;
			}
		}

		// TinMan: If it's not in radius skip it
		dist = v.Length();
		if ( dist >= radius ) {
			continue;
		}

		// TinMan: Add to new list
		entitySearchList[numSearchListEntities] = ent;
		numSearchListEntities++;

	}

	// DebugCircle( const idVec4 &color, const idVec3 &origin, const idVec3 &dir, const float radius, const int numSteps, const int lifetime = 0, const bool depthTest = false ) = 0;
	//gameRenderWorld->DebugCircle( colorBlue, origin, vec3_origin, radius, 10, 100000 ); // TinMan: *debug*

	//void			DebugBounds( const idVec4 &color, const idBounds &bounds, const idVec3 &org = vec3_origin, const int lifetime = 0 ) = 0;
	//gameRenderWorld->DebugBounds( colorBlue, bounds, vec3_origin, 1000 );

	idThread::ReturnFloat( numSearchListEntities );
}

/*
============
botAi::Event_FindItems
TinMan: Finds all items in level and adds them to entitySearchList[].
============
*/
void botAi::Event_FindItems( void ) {
	idEntity *	ent;
	int			i;

	// TinMan: Clear existing list
	memset( entitySearchList, 0, sizeof( entitySearchList ) );
	numSearchListEntities = 0; // TinMan: Reset

	for ( i = 0; i < MAX_GENTITIES; i++ ) {
		ent = gameLocal.entities[ i ];
		if ( ent ) {
			if ( ent->IsType( idItem::Type ) || ent->IsType( idItemPowerup::Type ) ) {
				entitySearchList[numSearchListEntities] = ent;
				numSearchListEntities++;
			}
		}
	}

	idThread::ReturnFloat( numSearchListEntities );
}

/*
============
botAi::Event_GetEntityList
TinMan: Return entity from array built during one of the find functions
TinMan: *todo* Oh dear, nasty, check to see if not empty before barging in
============
*/
void botAi::Event_GetEntityList( float index ) {
	int i;
	i = index;
	idEntity *	ent;
	if ( i > numSearchListEntities ) {
		gameLocal.Error( "[botAi::Event_GetEntityList][Invalid index]\n" );
	}
	ent = entitySearchList[ i ];

	if ( ent ) {
		idThread::ReturnEntity( ent );
	}
}

/*
=====================
botAi::Event_HeardSound
TinMan: *todo* hmm do we want pvs check
=====================
*/
void botAi::Event_HeardSound( int ignore_team ) {
	// check if we heard any sounds in the last frame
	idActor	*actor = gameLocal.GetAlertEntity();
	if ( actor ) { //( !ignore_team || ( ReactionTo( actor ) & ATTACK_ON_SIGHT ) ) && gameLocal.InPlayerPVS( playerEnt ) ) { // TinMan: Don't use inplayerpvs check obviously
		// TinMan: Friendly fire check
		if ( gameLocal.gameType == ( GAME_SP ) || gameLocal.gameType == ( GAME_TDM ) ) {
			if ( actor->team == playerEnt->team && !ignore_team ) {
				idThread::ReturnEntity( NULL );
				return;
			}
		}

		idVec3 pos = actor->GetPhysics()->GetOrigin();
		idVec3 org = physicsObject->GetOrigin();
		float dist = ( pos - org ).LengthSqr();
		if ( dist < Square( AI_HEARING_RANGE ) ) {
			idThread::ReturnEntity( actor );
			return;
		}
	}

	idThread::ReturnEntity( NULL );
}

/*
=====================
botAi::Event_SetEnemy
=====================
*/
void botAi::Event_SetEnemy( idEntity *ent ) {
	if ( !ent ) {
		ClearEnemy();
	} else if ( !ent->IsType( idActor::Type ) ) {
		gameLocal.Error( "'%s' is not an idActor (player or ai controlled character)", ent->name.c_str() );
	} else {
		SetEnemy( static_cast<idActor *>( ent ) );
	}
}

/*
=====================
botAi::Event_ClearEnemy
=====================
*/
void botAi::Event_ClearEnemy( void ) {
	ClearEnemy();
}

/*
=====================
botAi::Event_GetEnemy
=====================
*/
void botAi::Event_GetEnemy( void ) {
	idThread::ReturnEntity( enemy.GetEntity() );
}

/*
================
botAi::Event_LocateEnemy
================
*/
void botAi::Event_LocateEnemy( void ) {
	idActor *enemyEnt;
	int areaNum;
	
	enemyEnt = enemy.GetEntity();
	if ( !enemyEnt ) {
		return;
	}

	enemyEnt->GetAASLocation( aas, lastReachableEnemyPos, areaNum );
	SetEnemyPosition();
	UpdateEnemyPosition();
}

/*
=====================
botAi::Event_EnemyRange
=====================
*/
void botAi::Event_EnemyRange( void ) {
	float dist;
	idActor *enemyEnt = enemy.GetEntity();

	if ( enemyEnt ) {
		dist = ( enemyEnt->GetPhysics()->GetOrigin() - physicsObject->GetOrigin() ).Length();
	} else {
		// Just some really high number
		dist = idMath::INFINITY;
	}

	idThread::ReturnFloat( dist );
}

/*
=====================
botAi::Event_EnemyRange2D
=====================
*/
void botAi::Event_EnemyRange2D( void ) {
	float dist;
	idActor *enemyEnt = enemy.GetEntity();

	if ( enemyEnt ) {
		dist = ( enemyEnt->GetPhysics()->GetOrigin().ToVec2() - physicsObject->GetOrigin().ToVec2() ).Length();
	} else {
		// Just some really high number
		dist = idMath::INFINITY;
	}

	idThread::ReturnFloat( dist );
}

/*
=====================
botAi::Event_GetEnemyPos
=====================
*/
void botAi::Event_GetEnemyPos( void ) {
	idThread::ReturnVector( lastVisibleEnemyPos );
}

/*
=====================
botAi::Event_GetEnemyEyePos
=====================
*/
void botAi::Event_GetEnemyEyePos( void ) {
	idThread::ReturnVector( lastVisibleEnemyPos + lastVisibleEnemyEyeOffset );
}

/*
=====================
botAi::Event_PredictEnemyPos
=====================
*/
void botAi::Event_PredictEnemyPos( float time ) {
	predictedPath_t path;
	idActor *enemyEnt = enemy.GetEntity();

	// if no enemy set
	if ( !enemyEnt ) {
		idThread::ReturnVector( physicsObject->GetOrigin() );
		return;
	}

	// predict the enemy movement
	idAI::PredictPath( enemyEnt, aas, lastVisibleEnemyPos, enemyEnt->GetPhysics()->GetLinearVelocity(), SEC2MS( time ), SEC2MS( time ), ( move.moveType == MOVETYPE_FLY ) ? SE_BLOCKED : ( SE_BLOCKED | SE_ENTER_LEDGE_AREA ), path );

	idThread::ReturnVector( path.endPos );
}

/*
=====================
botAi::Event_CanHitEnemy
=====================
*/
void botAi::Event_CanHitEnemy( void ) {
	trace_t	tr;
	idEntity *hit;

	idActor *enemyEnt = enemy.GetEntity();
	if ( !AI_ENEMY_VISIBLE || !enemyEnt ) {
		idThread::ReturnInt( false );
		return;
	}

	// don't check twice per frame
	if ( gameLocal.time == lastHitCheckTime ) {
		idThread::ReturnInt( lastHitCheckResult );
		return;
	}

	lastHitCheckTime = gameLocal.time;

	idVec3 toPos = enemyEnt->GetEyePosition();
	idVec3 eye = playerEnt->GetEyePosition();
	idVec3 dir;

	// expand the ray out as far as possible so we can detect anything behind the enemy
	dir = toPos - eye;
	dir.Normalize();
	toPos = eye + dir * MAX_WORLD_SIZE;
	gameLocal.clip.TracePoint( tr, eye, toPos, MASK_SHOT_BOUNDINGBOX, playerEnt );
	hit = gameLocal.GetTraceEntity( tr );
	if ( tr.fraction >= 1.0f || ( hit == enemyEnt ) ) {
		lastHitCheckResult = true;
	} /*else if ( ( tr.fraction < 1.0f ) && ( hit->IsType( idAI::Type ) ) && ( static_cast<idAI *>( hit )->team != team ) ) {
		lastHitCheckResult = true;
	} */else {
		lastHitCheckResult = false;
	}

	idThread::ReturnInt( lastHitCheckResult );
}

/*
=====================
botAi::Event_EnemyPositionValid
=====================
*/
void botAi::Event_EnemyPositionValid( void ) {
	bool result;

	result = EnemyPositionValid();
	idThread::ReturnInt( result );
}

/*
=====================
botAi::Event_MoveStatus
=====================
*/
void botAi::Event_MoveStatus( void ) {
	idThread::ReturnInt( move.moveStatus );
}

/*
=====================
botAi::Event_StopMove
=====================
*/
void botAi::Event_StopMove( void ) {
	StopMove( MOVE_STATUS_DONE );
}

/*
=====================
botAi::Event_SaveMove
=====================
*/
void botAi::Event_SaveMove( void ) {
	savedMove = move;
}

/*
=====================
botAi::Event_RestoreMove
=====================
*/
void botAi::Event_RestoreMove( void ) {
	idVec3 goalPos;
	idVec3 dest;

	switch( savedMove.moveCommand ) {
	case MOVE_NONE :
		StopMove( savedMove.moveStatus );
		break;

	case MOVE_TO_ENEMY :
		SetMoveToEnemy();
		break;

	case MOVE_TO_ENTITY :
		SetMoveToEntity( savedMove.goalEntity.GetEntity() );
		break;

	case MOVE_OUT_OF_RANGE :
		SetMoveOutOfRange( savedMove.goalEntity.GetEntity(), savedMove.range );
		break;

		/*
	case MOVE_TO_ATTACK_POSITION :
		SetMoveToAttackPosition( savedMove.goalEntity.GetEntity(), savedMove.anim );
		break;
		*/

	case MOVE_TO_COVER :
		SetMoveToCover( savedMove.goalEntity.GetEntity(), lastVisibleEnemyPos );
		break;

	case MOVE_TO_POSITION :
		SetMoveToPosition( savedMove.moveDest );
		break;

	case MOVE_WANDER :
		WanderAround();
		break;
	}

	if ( GetMovePos( goalPos ) ) {
		CheckObstacleAvoidance( goalPos, dest );
	}
}

/*
=====================
botAi::Event_SetMoveToCover
=====================
*/
void botAi::Event_SetMoveToCover( void ) {
	idActor *enemyEnt = enemy.GetEntity();

	StopMove( MOVE_STATUS_DEST_NOT_FOUND );
	if ( !enemyEnt || !SetMoveToCover( enemyEnt, lastVisibleEnemyPos ) ) {
		return;
	}
}

/*
=====================
botAi::Event_SetMoveToEnemy
=====================
*/
void botAi::Event_SetMoveToEnemy( void ) {
	StopMove( MOVE_STATUS_DEST_NOT_FOUND );
	if ( !enemy.GetEntity() || !SetMoveToEnemy() ) {
		return;
	}
}

/*
=====================
botAi::Event_SetMoveOutOfRange
=====================
*/
void botAi::Event_SetMoveOutOfRange( idEntity *entity, float range ) {
	StopMove( MOVE_STATUS_DEST_NOT_FOUND );
	SetMoveOutOfRange( entity, range );
}

/*
=====================
botAi::Event_SetMoveToAttackPosition
=====================
*/
/*
void botAi::Event_MoveToAttackPosition( idEntity *entity, const char *attack_anim ) {
	int anim;

	StopMove( MOVE_STATUS_DEST_NOT_FOUND );

	anim = GetAnim( ANIMCHANNEL_LEGS, attack_anim );
	if ( !anim ) {
		gameLocal.Error( "Unknown anim '%s'", attack_anim );
	}

	SetMoveToAttackPosition( entity, anim );
}
*/

/*
=====================
botAi::Event_SetMoveToEntity
=====================
*/
void botAi::Event_SetMoveToEntity( idEntity *ent ) {
	StopMove( MOVE_STATUS_DEST_NOT_FOUND );
	if ( ent ) {
		SetMoveToEntity( ent );
	}
}

/*
=====================
botAi::Event_SetMoveToPosition
=====================
*/
void botAi::Event_SetMoveToPosition( const idVec3 &pos ) {
	StopMove( MOVE_STATUS_DONE );
	SetMoveToPosition( pos );
}

/*
=====================
botAi::Event_SetMoveWander
=====================
*/
void botAi::Event_SetMoveWander( void ) {
	WanderAround();
}

/*
================
botAi::Event_CanReachPosition
================
*/
void botAi::Event_CanReachPosition( const idVec3 &pos ) {
	aasPath_t	path;
	int			toAreaNum;
	int			areaNum;

	toAreaNum = PointReachableAreaNum( pos );
	areaNum	= PointReachableAreaNum( physicsObject->GetOrigin() );
	if ( !toAreaNum || !PathToGoal( path, areaNum, physicsObject->GetOrigin(), toAreaNum, pos ) ) {
		idThread::ReturnInt( false );
	} else {
		idThread::ReturnInt( true );
	}
}

/*
================
botAi::Event_CanReachEntity
================
*/
void botAi::Event_CanReachEntity( idEntity *ent ) {
	aasPath_t	path;
	int			toAreaNum;
	int			areaNum;
	idVec3		pos;

	if ( !ent ) {
		idThread::ReturnInt( false );
		return;
	}

	if ( move.moveType != MOVETYPE_FLY ) {
		if ( !ent->GetFloorPos( 64.0f, pos ) ) {
			idThread::ReturnInt( false );
			return;
		}
		if ( ent->IsType( idActor::Type ) && static_cast<idActor *>( ent )->OnLadder() ) {
			idThread::ReturnInt( false );
			return;
		}
	} else {
		pos = ent->GetPhysics()->GetOrigin();
	}

	toAreaNum = PointReachableAreaNum( pos );
	if ( !toAreaNum ) {
		idThread::ReturnInt( false );
		return;
	}

	const idVec3 &org = physicsObject->GetOrigin();
	areaNum	= PointReachableAreaNum( org );
	if ( !toAreaNum || !PathToGoal( path, areaNum, org, toAreaNum, pos ) ) {
		idThread::ReturnInt( false );
	} else {
		idThread::ReturnInt( true );
	}
}

/*
================
botAi::Event_CanReachEnemy
================
*/
void botAi::Event_CanReachEnemy( void ) {
	aasPath_t	path;
	int			toAreaNum;
	int			areaNum;
	idVec3		pos;
	idActor		*enemyEnt;

	enemyEnt = enemy.GetEntity();
	if ( !enemyEnt ) {
		idThread::ReturnInt( false );
		return;
	}

	if ( move.moveType != MOVETYPE_FLY ) {
		if ( enemyEnt->OnLadder() ) {
			idThread::ReturnInt( false );
			return;
		}
		enemyEnt->GetAASLocation( aas, pos, toAreaNum );
	}  else {
		pos = enemyEnt->GetPhysics()->GetOrigin();
		toAreaNum = PointReachableAreaNum( pos );
	}

	if ( !toAreaNum ) {
		idThread::ReturnInt( false );
		return;
	}

	const idVec3 &org = physicsObject->GetOrigin();
	areaNum	= PointReachableAreaNum( org );
	if ( !PathToGoal( path, areaNum, org, toAreaNum, pos ) ) {
		idThread::ReturnInt( false );
	} else {
		idThread::ReturnInt( true );
	}
}

/*
================
botAi::Event_GetReachableEntityPosition
================
*/
void botAi::Event_GetReachableEntityPosition( idEntity *ent ) {
	int		toAreaNum;
	idVec3	pos;

	if ( !ent->GetFloorPos( 64.0f, pos ) ) {
		idThread::ReturnInt( false );
		return;
	}
	if ( ent->IsType( idActor::Type ) && static_cast<idActor *>( ent )->OnLadder() ) {
		idThread::ReturnInt( false );
		return;
	}

	if ( aas ) {
		toAreaNum = PointReachableAreaNum( pos );
		aas->PushPointIntoAreaNum( toAreaNum, pos );
	}

	idThread::ReturnVector( pos );
}

/*
================
botAi::Event_TravelDistanceToPoint
================
*/
void botAi::Event_TravelDistanceToPoint( const idVec3 &pos ) {
	float time;

	time = TravelDistance( physicsObject->GetOrigin(), pos );
	idThread::ReturnFloat( time );
}

/*
================
botAi::Event_TravelDistanceToEntity
================
*/
void botAi::Event_TravelDistanceToEntity( idEntity *ent ) {
	float time;

	time = TravelDistance( physicsObject->GetOrigin(), ent->GetPhysics()->GetOrigin() );
	idThread::ReturnFloat( time );
}

/*
================
botAi::Event_TravelDistanceBetweenPoints
================
*/
void botAi::Event_TravelDistanceBetweenPoints( const idVec3 &source, const idVec3 &dest ) {
	float time;

	time = TravelDistance( source, dest );
	idThread::ReturnFloat( time );
}

/*
================
botAi::Event_TravelDistanceBetweenEntities
================
*/
void botAi::Event_TravelDistanceBetweenEntities( idEntity *source, idEntity *dest ) {
	float time;

	assert( source );
	assert( dest );
	time = TravelDistance( source->GetPhysics()->GetOrigin(), dest->GetPhysics()->GetOrigin() );
	idThread::ReturnFloat( time );
}

/*
================
botAi::Event_Acos
TinMan: *todo* this belongs in sys scripting
================
*/
void botAi::Event_Acos( float a ) {
	idThread::ReturnFloat( idMath::ACos16( a ) );
}

/*
================
botAi::Event_GetClassName
TinMan: Added, simple as pimples *todo* rename?
================
*/
void botAi::Event_GetClassName( idEntity *ent ) {
	idThread::ReturnString( ent->spawnArgs.GetString( "classname" ) );
}

/*
================
botAi::Event_GetClassType
TinMan: Added, simple as pimples *todo* rename?
================
*/
void botAi::Event_GetClassType( idEntity *ent ) {
	idThread::ReturnString( ent->GetClassname() );
}
#endif //MOD_BOTS