// Copyright (C) 2005 Daxtron/Logicmoo 
//
#define isValid(X) validPtr2((void*)X)

#ifdef DAXMOO
//#define DAXMOO_JNI
           
#ifdef DAXMOO_ASSERTLN
#ifdef assert
#undef assert
#endif 
#define assert(X) if (X){}else assertln("Assert Failed: %s\n%s(%d) ",#X,__FILE__,__LINE__ )
#endif

#include "../idlib/precompiled.h"

//#include "../game/script/Script_Interpreter.h"
//#include "idInterpreter.h"
//#include "DaxControl.h"

bool validPtr2(void* ptr);

extern void udpprint( const char*);
void gameLocalPrintf( const char *fmt, ... );
void debugln( const char *fmt, ... ) ;
void assertln( const char *fmt, ... ) ;

extern void Daxmoo_Init();
extern bool Daxmoo_Shutdown();
extern void Daxmoo_RunFrame();
void Daxmoo_ProcessChatMessage( int clientNum, bool team, const char *name, const char *text, const char *sound ) ;
void Daxmoo_Say(bool team,const char* name, const char* text);
void Daxmoo_CLR_Event_sayFrom( int clientNum, bool team, const char *name, const char *text, const char *sound );

#ifdef assert
#undef assert
#endif 

#define assert(X) if (X){}else assertln("Assert Failed: %s\n%s(%d) ",#X,__FILE__,__LINE__ )

#endif
