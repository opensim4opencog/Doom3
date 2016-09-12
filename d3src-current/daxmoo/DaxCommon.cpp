// Copyright (C) 2005 Daxtron/Logicmoo 
//
#include "../idlib/precompiled.h"
//#include "../game/script/Script_Interpreter.h"
//#include "idInterpreter.h"
#include "DaxCommon.h"
#pragma hdrstop

#include "../Game/Game_local.h"
#include "../Game/gamesys/TypeInfo.h"
#include "../idlib/precompiled.h"

#ifdef DAXMOO


#ifdef DAXMOO_DOTNET
    #ifdef __IDLIB__
        #using <daxclr.dll>
        #using <mscorlib.dll>
    #endif
#endif



void udpPrintTo(const char* jost,int port, const char* msg);
void udpprint(const char *msg) {
   // udpPrintTo("127.0.0.1",6669,msg);
} 

/* 
 ============ 
gameLocal.debugln 
 ============ 
*/ 
void gameLocalPrintfBoth( const char *fmt, ... ) {
    va_list argptr; 
    char text[MAX_STRING_CHARS*3]; 
    va_start( argptr, fmt ); 
    idStr::vsnPrintf( text, sizeof( text ), fmt, argptr ); 
    va_end( argptr ); 
    udpprint( text ); 
    gameLocal.Printf(text);	
} 

/* 
 ============ 
debugln 
 ============ 
*/ 
void gameLocalPrintf( const char *fmt, ... ) {
    va_list argptr; 
    char text[MAX_STRING_CHARS*3]; 
    va_start( argptr, fmt ); 
    idStr::vsnPrintf( text, sizeof( text ), fmt, argptr ); 
    va_end( argptr ); 
    gameLocalPrintfBoth( text ); 
} 
/* 
 ============ 
debugln 
 ============ 
*/ 
void debugln( const char *fmt, ... ) {
    va_list argptr; 
    char text[MAX_STRING_CHARS*3]; 
    va_start( argptr, fmt ); 
    idStr::vsnPrintf( text, sizeof( text ), fmt, argptr ); 
    va_end( argptr ); 
    if (text[0]!= '(' && text[0]!= '\n') {
        gameLocalPrintfBoth("/* %s */\n",text); 
    } else {
        gameLocalPrintfBoth("/* %s */\n",text); 
    } 
} 

void Daxmoo_CLR_Init();
void Daxmoo_CYC_Init();
void Daxmoo_CLR_OnGameLoad();
void CheckForClientConnections();


static bool firstFrame = true;

void Daxmoo_Init() {
	firstFrame = true;
	debugln("Daxmoo_Init()"); 
	Daxmoo_CLR_Init();
	Daxmoo_CYC_Init();
}

bool Daxmoo_Shutdown() {
	firstFrame = true;
	debugln("Daxmoo_Shutdown()"); 
	return false;
}

void Daxmoo_FrameInit() {
	firstFrame = false;
	debugln("Daxmoo_FrameInit()"); 
	Daxmoo_CLR_OnGameLoad();
}

void Daxmoo_RunFrame() {
#ifdef DAXMOO_WWW
	CheckForClientConnections();
#endif 
	if (firstFrame) Daxmoo_FrameInit();
}

#endif//  DAXMOO_JNI

