#ifndef __BOTAI_H__
#define __BOTAI_H__

#ifdef DAXMOO
#include "../game/game_local.h"
#endif
/*
===============================================================================

	botAi

===============================================================================
*/

#define	BOT_START_INDEX 16
#define BOT_MAX_BOTS	16

// TinMan: Info for bots array
typedef struct botInfo_s {
	bool					inUse;
	int						clientID;
	int						entityNum;
} botInfo_t;

class botAi : public idEntity {
public:
	static botInfo_t		bots[];

	static void				Addbot_f( const idCmdArgs &args );

// Variables
public:
	int						botID;
	int						clientID;

	idAngles				viewAngles;

	idScriptBool		AI_FORWARD;
	idScriptBool		AI_BACKWARD;
	idScriptBool		AI_STRAFE_LEFT;
	idScriptBool		AI_STRAFE_RIGHT;
	idScriptBool		AI_ATTACK_HELD;
	idScriptBool		AI_WEAPON_FIRED;
	idScriptBool		AI_JUMP;
	idScriptBool		AI_DEAD;
	idScriptBool		AI_CROUCH;
	idScriptBool		AI_ONGROUND;
	idScriptBool		AI_ONLADDER;
	idScriptBool		AI_RUN;
	idScriptBool		AI_HARDLANDING;
	idScriptBool		AI_SOFTLANDING;
	idScriptBool		AI_RELOAD;
	idScriptBool		AI_PAIN;
	idScriptBool		AI_TELEPORT;
	idScriptBool		AI_TURN_LEFT;
	idScriptBool		AI_TURN_RIGHT;
	
	idScriptBool		AI_ENEMY_VISIBLE;
	idScriptBool		AI_ENEMY_IN_FOV;
	idScriptBool		AI_ENEMY_DEAD;
	idScriptBool		AI_MOVE_DONE;
	idScriptBool		AI_DEST_UNREACHABLE;
	idScriptBool		AI_ENEMY_REACHABLE;
	idScriptBool		AI_BLOCKED;
	idScriptBool		AI_OBSTACLE_IN_PATH;
	idScriptBool		AI_PUSHED;


	idScriptBool		AI_WEAPON_FIRE;

	idVec3					aimPosition;

// Functions
public:
	CLASS_PROTOTYPE( botAi );

							botAi();
							~botAi();

	static void				WriteUserCmdsToSnapshot( idBitMsg &msg );
	static void				ReadUserCmdsFromSnapshot( const idBitMsg &msg );

	void					Init( void );
	void					Spawn( void );

	void					Save( idSaveGame *savefile ) const;
	void					Restore( idRestoreGame *savefile );

	void					PrepareForRestart( void );
	void					Restart( void );
	void					LinkScriptVariables( void );

	void					Think( void );

	void					GetBodyState( void );

	void					UpdateViewAngles( void );
	void					UpdateUserCmd( void );

							// script state management
	void					ShutdownThreads( void );
	virtual idThread *		ConstructScriptObject( void );
	const function_t		*GetScriptFunction( const char *funcname );
	void					SetState( const function_t *newState );
	void					SetState( const char *statename );
	void					UpdateScript( void );

protected:
	idPlayer			*	playerEnt;
	idPhysics_Player	*	physicsObject;
	idInventory			*	inventory;	

	// state variables
	const function_t	*	state;
	const function_t	*	idealState;

	// script variables
	idThread *				scriptThread;

	// navigation
	idAAS *					aas;
	int						travelFlags;

	float					aimRate;

	// enemy variables
	idEntityPtr<idActor>	enemy;
	idVec3					lastVisibleEnemyPos;
	idVec3					lastVisibleEnemyEyeOffset;
	idVec3					lastVisibleReachableEnemyPos;
	idVec3					lastReachableEnemyPos;

	bool					lastHitCheckResult;
	int						lastHitCheckTime;

	idMoveState				move;
	idMoveState				savedMove;

	float					kickForce;
	bool					ignore_obstacles;
	float					blockedRadius;
	int						blockedMoveTime;

	int						numSearchListEntities;
	idEntity			*	entitySearchList[ MAX_GENTITIES ];


protected:
	bool					ValidForBounds( const idAASSettings *settings, const idBounds &bounds );
	void					SetAAS( void );
	// navigation
	void					KickObstacles( const idVec3 &dir, float force, idEntity *alwaysKick );
	bool					ReachedPos( const idVec3 &pos, const moveCommand_t moveCommand ) const;
	float					TravelDistance( const idVec3 &start, const idVec3 &end ) const;
	int						PointReachableAreaNum( const idVec3 &pos, const float boundsScale = 2.0f ) const;
	bool					PathToGoal( aasPath_t &path, int areaNum, const idVec3 &origin, int goalAreaNum, const idVec3 &goalOrigin ) const;
	void					DrawRoute( void ) const;
	bool					GetMovePos( idVec3 &seekPos );
	bool					MoveDone( void ) const;
	bool					EntityCanSeePos( idActor *actor, const idVec3 &actorOrigin, const idVec3 &pos );
	void					BlockedFailSafe( void );

	idVec3					GetMovePosition( void );
	void					StopMove( moveStatus_t status );
	bool					SetMoveOutOfRange( idEntity *entity, float range );
	bool					SetMoveToAttackPosition( idEntity *ent, int attack_anim );
	bool					SetMoveToEnemy( void );
	bool					SetMoveToEntity( idEntity *ent );
	bool					SetMoveToPosition( const idVec3 &pos );
	bool					SetMoveToCover( idEntity *entity, const idVec3 &pos );
	bool					WanderAround( void );
	bool					StepDirection( float dir );
	bool					NewWanderDir( const idVec3 &dest );

	void					CheckObstacleAvoidance( const idVec3 &goalPos, idVec3 &newPos );

	void					EnemyDead( void );
	idActor				*	GetEnemy( void ) const;
	void					ClearEnemy( void );
	bool					EnemyPositionValid( void ) const;
	void					SetEnemyPosition( void );
	void					UpdateEnemyPosition( void );
	void					SetEnemy( idActor *newEnemy );

private:
	void					Event_SetNextState( const char *name );
	void					Event_SetState( const char *name );
	void					Event_GetState( void );
	void					Event_GetBody( void );

	void					Event_GetHealth( idEntity *ent );
	void					Event_GetArmor( idEntity *ent );

	void					Event_GetTeam( idEntity *ent );

	void					Event_HasAmmo( const char *name );
	void					Event_HasItem( const char *name );
	void					Event_NextBestWeapon( void );


	void					Event_SetAimPosition( const idVec3 &aimPosition );
	void					Event_GetAimPosition( void );
	void					Event_GetMovePosition( void );

	void					Event_CanSeeEntity( idEntity *ent, bool useFov );

	void					Event_GetEyePosition( void );
	void					Event_GetAIAimTargets( idEntity *aimAtEnt, float location );

	void					Event_FindEnemies( int useFOV );
	void					Event_FindInRadius( const idVec3 &origin, float radius, const char *classname );
	void					Event_FindItems( void );
	void					Event_GetEntityList( float index );

	void					Event_HeardSound( int ignore_team );
	void					Event_SetEnemy( idEntity *ent );
	void					Event_ClearEnemy( void );
	void					Event_GetEnemy( void );
	void					Event_LocateEnemy( void );
	void					Event_EnemyRange( void );
	void					Event_EnemyRange2D( void );
	void					Event_GetEnemyPos( void );
	void					Event_GetEnemyEyePos( void );
	void					Event_PredictEnemyPos( float time );
	void					Event_CanHitEnemy( void );
	void					Event_EnemyPositionValid( void );

	void					Event_MoveStatus( void );
	void					Event_StopMove( void );
	void					Event_SaveMove( void );
	void					Event_RestoreMove( void );

	void					Event_SetMoveToCover( void );
	void					Event_SetMoveToEnemy( void );
	void					Event_SetMoveOutOfRange( idEntity *entity, float range );
	void					Event_SetMoveToAttackPosition( idEntity *entity, const char *attack_anim );
	void					Event_SetMoveToEntity( idEntity *ent );
	void					Event_SetMoveToPosition( const idVec3 &pos );
	void					Event_SetMoveWander( void );

	void 					Event_CanReachPosition( const idVec3 &pos );
	void 					Event_CanReachEntity( idEntity *ent );
	void					Event_CanReachEnemy( void );
	void					Event_GetReachableEntityPosition( idEntity *ent );

	void					Event_TravelDistanceToPoint( const idVec3 &pos );
	void					Event_TravelDistanceToEntity( idEntity *ent );
	void					Event_TravelDistanceBetweenPoints( const idVec3 &source, const idVec3 &dest );
	void					Event_TravelDistanceBetweenEntities( idEntity *source, idEntity *dest );

	void					Event_Acos( float a );

	void					Event_GetClassName( idEntity *ent );	
	void					Event_GetClassType( idEntity *ent );
};

/*
===============================================================================

	bots

	TinMan: This will do for now

===============================================================================
*/
#include "BotSabot.h"

#endif /* !__BOTAI_H__ */
