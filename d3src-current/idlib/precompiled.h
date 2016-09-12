// Copyright (C) 2004 Id Software, Inc.
//

#ifndef __PRECOMPILED_H__
#define __PRECOMPILED_H__

#ifdef __cplusplus

#ifdef DAXMOO
#ifndef D3V_D3XP
#define D3V_D3XP
#endif

#ifndef ID_DEMO_BUILD
#define ID_DEMO_BUILD
#endif
//   #define DAXMOO_WWW
//	#define DAXMOO_JNI
//	#define DAXMOO_SAY_EVENT
//	#define DAXMOO_CYC
//    #define DAXMOO_D3XP
//    #define DAXMOO_LEAK
//    #define DAXMOO_CLR
//    #define DAXMOO_CLR_INIT

#define EVDEFNAME(obj,prefixr,namer) void Event_##prefixr##namer
#define EVDEFNAMEOBJ(obj,prefixr,namer) void obj::Event_##prefixr##namer

#define EVDEFCONST(obj,prefix,name,parms,ret) const idEventDef EV_##prefix##name(#prefix#name,parms,ret);
#define EVDEFEVENT(obj,prefixr,namer,parms,ret) EVENT( EV_##prefixr##namer, obj::Event_##prefixr##namer)
//#define EVDEFHH(obj,prefixr,namer,parms,reet,argys,doret) void Event_##prefixr##namer (( argys ));

#ifdef DAXMOO_ASSERTLN
extern void assertln( const char *fmt, ... );
#undef assert
 #define assert( X ) if ( X ) { } else assertln("%s %d \n %s ", __FILE__, __LINE__, #X )
#endif
#endif

//-----------------------------------------------------

#ifdef _WIN32

#define _ATL_CSTRING_EXPLICIT_CONSTRUCTORS	// prevent auto literal to string conversion

#ifndef _D3SDK
#ifndef GAME_DLL

#define WINVER				0x501

#ifdef	ID_DEDICATED
// dedicated sets windows version here
#define	_WIN32_WINNT WINVER
#define	WIN32_LEAN_AND_MEAN
#else
// non-dedicated includes MFC and sets windows verion here
//dmiles  #include "../tools/comafx/StdAfx.h"			// this will go away when MFC goes away
#endif

#include <winsock2.h>
#include <mmsystem.h>
#include <mmreg.h>

#define DIRECTINPUT_VERSION  0x0700
#define DIRECTSOUND_VERSION  0x0800

/*demiles
#include "../mssdk/include/dsound.h"
#include "../mssdk/include/dinput.h"
#include "../mssdk/include/dxerr8.h"
dmiles*/

#endif /* !GAME_DLL */
#endif /* !_D3SDK */

#pragma warning(disable : 4100)				// unreferenced formal parameter
#pragma warning(disable : 4244)				// conversion to smaller type, possible loss of data
#pragma warning(disable : 4714)				// function marked as __forceinline not inlined

#include <malloc.h>							// no malloc.h on mac or unix
#include <windows.h>						// for qgl.h
#undef FindText								// stupid namespace poluting Microsoft monkeys

#endif /* _WIN32 */

//-----------------------------------------------------

#if !defined( _DEBUG ) && !defined( NDEBUG )
	// don't generate asserts
	#define NDEBUG
#endif

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <assert.h>
#include <time.h>
#include <ctype.h>
#include <typeinfo>
#include <errno.h>
#include <math.h>

//-----------------------------------------------------

// non-portable system services
#include "../sys/sys_public.h"

// DAXMOO
#ifdef DAXMOO
#include "../daxmoo/DaxCommon.h"
#endif
// id lib
#include "../idlib/Lib.h"

#if !defined( _D3SDK ) && defined( __WITH_PB__ )
	#include "../punkbuster/pbcommon.h"
#endif

// framework
#include "../framework/BuildVersion.h"
#include "../framework/BuildDefines.h"
#include "../framework/Licensee.h"
#include "../framework/CmdSystem.h"
#include "../framework/CVarSystem.h"
#include "../framework/Common.h"
#include "../framework/File.h"
#include "../framework/FileSystem.h"
#include "../framework/UsercmdGen.h"

// decls
#include "../framework/DeclManager.h"
#include "../framework/DeclTable.h"
#include "../framework/DeclSkin.h"
#include "../framework/DeclEntityDef.h"
#include "../framework/DeclFX.h"
#include "../framework/DeclParticle.h"
#include "../framework/DeclAF.h"
#include "../framework/DeclPDA.h"

// We have expression parsing and evaluation code in multiple places:
// materials, sound shaders, and guis. We should unify them.
const int MAX_EXPRESSION_OPS = 4096;
const int MAX_EXPRESSION_REGISTERS = 4096;

// renderer
#include "../renderer/qgl.h"
#include "../renderer/Cinematic.h"
#include "../renderer/Material.h"
#include "../renderer/Model.h"
#include "../renderer/ModelManager.h"
#include "../renderer/RenderSystem.h"
#include "../renderer/RenderWorld.h"

// sound engine
#include "../sound/sound.h"

// asynchronous networking
#include "../framework/async/NetworkSystem.h"

// user interfaces
#include "../ui/ListGUI.h"
#include "../ui/UserInterface.h"

// collision detection system
#include "../cm/CollisionModel.h"

// AAS files and manager
#include "../tools/compilers/aas/AASFile.h"
#include "../tools/compilers/aas/AASFileManager.h"

// game
#if defined(_D3XP) && defined(GAME_DLL) 
#include "../game/Game.h"
//#include "../d3xp/Game.h"
#else

#include "../game/Game.h"
#endif

//-----------------------------------------------------

#ifndef _D3SDK

#ifdef GAME_DLL

#if defined(_D3XP)
#include "../game/Game_local.h"
///#include "../d3xp/Game_local.h"
#else
#include "../game/Game_local.h"
#endif

#else

/*dmiles 
 
#include "../framework/DemoChecksum.h"

// framework
#include "../framework/Compressor.h"
#include "../framework/EventLoop.h"
#include "../framework/KeyInput.h"
#include "../framework/EditField.h"
#include "../framework/Console.h"
#include "../framework/DemoFile.h"
#include "../framework/Session.h"

// asynchronous networking
#include "../framework/async/AsyncNetwork.h"

// The editor entry points are always declared, but may just be
// stubbed out on non-windows platforms.
#include "../tools/edit_public.h"

// Compilers for map, model, video etc. processing.
#include "../tools/compilers/compiler_public.h"

dmiles*/
#endif /* !GAME_DLL */

#endif /* !_D3SDK */
// DAXMOO
#ifdef DAXMOO
// #include "../game/physics/Force.h"
#ifdef DAXMOO
#include "../daxmoo/CycConnection.h"

#endif
#endif

//-----------------------------------------------------

#endif	/* __cplusplus */

#endif /* !__PRECOMPILED_H__ */