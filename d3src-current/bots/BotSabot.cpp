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

	SABot - Stupid Angry Bot - release alpha 6 - "The bride of Frankenstein"

	botSabot
	SABot specific mojo and stuff not for the botAi base.

===============================================================================
*/

CLASS_DECLARATION( botAi, botSabot )
END_CLASS

/*
=====================
botSabot::botSabot
=====================
*/
botSabot::botSabot() {
}

/*
=====================
botSabot::~botSabot
=====================
*/
botSabot::~botSabot() {
}

/*
=====================
botSabot::Think
TinMan: I think therefore I am stupid
=====================
*/
void botSabot::Think( void ) {
	//gameLocal.Printf( "--------- Botthink ----------pilgrim`, do you enjoy being an ass tard?\n" ); // TinMan: *debug*

	//gameLocal.Printf( "[Bot ClientId: %i]\n", clientID ); // TinMan: *debug*

	GetBodyState();		

	if ( !playerEnt->spectating ) { // TinMan: *todo* handle this diferently?
		if ( AI_DEAD ) {
			if ( idStr::Icmp( state->Name(), "state_Killed" ) != 0 ) { // TinMan: *todo* still a bit broken, keeps setting state
				state = GetScriptFunction( "state_Killed" );
				SetState( state );
			}

			UpdateScript();

			UpdateUserCmd();
		} else {
			// id: clear out the enemy when he dies or is hidden
			idActor *enemyEnt = enemy.GetEntity();
			if ( enemyEnt ) {
				if ( enemyEnt->health <= 0 ) {
					EnemyDead();
				}
			}

			UpdateEnemyPosition();

			UpdateScript();

			UpdateViewAngles();

			UpdateUserCmd();
		}
	}
}
#endif //MOD BOTS
