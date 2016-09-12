#ifdef MOD_BOTS
#ifndef __BOTCLIENT_H__
#define __BOTCLIENT_H__

/*
===============================================================================

	botClient
	
	TinMan: class for tweak to bot's player ent, very cheap and cheasy, just exploring at the moment

===============================================================================
*/

class botClient : public idPlayer {
// Variables
//public:

// Functions
public:
	CLASS_PROTOTYPE( botClient );

							botClient();
							~botClient();

	void					Think( void );
	
	void					EvaluateControls( void );
};

#endif /* !__BOTCLIENT_H__ */
#endif //MOD_BOTS