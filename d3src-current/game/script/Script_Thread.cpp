// Copyright (C) 2004 Id Software, Inc.
//

#include "../../idlib/precompiled.h"
#ifdef DAXMOO
	#include "../../daxmoo/CycAccess.h"
#endif

#pragma hdrstop

#include "../Game_local.h"

const idEventDef EV_Thread_Execute( "<execute>", NULL );
const idEventDef EV_Thread_SetCallback( "<script_setcallback>", NULL );

// script callable events
const idEventDef EV_Thread_TerminateThread( "terminate", "d" );
const idEventDef EV_Thread_Pause( "pause", NULL );
const idEventDef EV_Thread_Wait( "wait", "f" );
const idEventDef EV_Thread_WaitFrame( "waitFrame" );
const idEventDef EV_Thread_WaitFor( "waitFor", "e" );
const idEventDef EV_Thread_WaitForThread( "waitForThread", "d" );
const idEventDef EV_Thread_Print( "print", "s" );
const idEventDef EV_Thread_PrintLn( "println", "s" );
const idEventDef EV_Thread_Say( "say", "s" );
const idEventDef EV_Thread_Assert( "assert", "f" );
const idEventDef EV_Thread_Trigger( "trigger", "e" );
const idEventDef EV_Thread_SetCvar( "setcvar", "ss" );
const idEventDef EV_Thread_GetCvar( "getcvar", "s", 's' );
const idEventDef EV_Thread_Random( "random", "f", 'f' );
#if defined(_D3XP) || defined(D3V_D3XP)
#ifndef Q4SDK
const idEventDef EV_Thread_RandomInt( "randomInt", "d", 'd' );
#endif
#endif
const idEventDef EV_Thread_GetTime( "getTime", NULL, 'f' );
const idEventDef EV_Thread_KillThread( "killthread", "s" );
const idEventDef EV_Thread_SetThreadName( "threadname", "s" );
const idEventDef EV_Thread_GetEntity( "getEntity", "s", 'e' );
const idEventDef EV_Thread_Spawn( "spawn", "s", 'e' );
const idEventDef EV_Thread_CopySpawnArgs( "copySpawnArgs", "e" );
const idEventDef EV_Thread_SetSpawnArg( "setSpawnArg", "ss" );
const idEventDef EV_Thread_SpawnString( "SpawnString", "ss", 's' );
const idEventDef EV_Thread_SpawnFloat( "SpawnFloat", "sf", 'f' );
const idEventDef EV_Thread_SpawnVector( "SpawnVector", "sv", 'v' );
const idEventDef EV_Thread_ClearPersistantArgs( "clearPersistantArgs" );
const idEventDef EV_Thread_SetPersistantArg( "setPersistantArg", "ss" );
const idEventDef EV_Thread_GetPersistantString( "getPersistantString", "s", 's' );
const idEventDef EV_Thread_GetPersistantFloat( "getPersistantFloat", "s", 'f' );
const idEventDef EV_Thread_GetPersistantVector( "getPersistantVector", "s", 'v' );
const idEventDef EV_Thread_AngToForward( "angToForward", "v", 'v' );
const idEventDef EV_Thread_AngToRight( "angToRight", "v", 'v' );
const idEventDef EV_Thread_AngToUp( "angToUp", "v", 'v' );
const idEventDef EV_Thread_Sine( "sin", "f", 'f' );
const idEventDef EV_Thread_Cosine( "cos", "f", 'f' );
#if defined(_D3XP) || defined(D3V_D3XP)
const idEventDef EV_Thread_ArcSine( "asin", "f", 'f' );
const idEventDef EV_Thread_ArcCosine( "acos", "f", 'f' );
#endif
const idEventDef EV_Thread_SquareRoot( "sqrt", "f", 'f' );
const idEventDef EV_Thread_Normalize( "vecNormalize", "v", 'v' );
const idEventDef EV_Thread_VecLength( "vecLength", "v", 'f' );
const idEventDef EV_Thread_VecDotProduct( "DotProduct", "vv", 'f' );
const idEventDef EV_Thread_VecCrossProduct( "CrossProduct", "vv", 'v' );
const idEventDef EV_Thread_VecToAngles( "VecToAngles", "v", 'v' );
#if defined(_D3XP) || defined(D3V_D3XP)
const idEventDef EV_Thread_VecToOrthoBasisAngles( "VecToOrthoBasisAngles", "v", 'v' );
const idEventDef EV_Thread_RotateVector("rotateVector", "vv", 'v');
#endif
const idEventDef EV_Thread_OnSignal( "onSignal", "des" );
const idEventDef EV_Thread_ClearSignal( "clearSignalThread", "de" );
const idEventDef EV_Thread_SetCamera( "setCamera", "e" );
const idEventDef EV_Thread_FirstPerson( "firstPerson", NULL );
const idEventDef EV_Thread_Trace( "trace", "vvvvde", 'f' );
const idEventDef EV_Thread_TracePoint( "tracePoint", "vvde", 'f' );
const idEventDef EV_Thread_GetTraceFraction( "getTraceFraction", NULL, 'f' );
const idEventDef EV_Thread_GetTraceEndPos( "getTraceEndPos", NULL, 'v' );
const idEventDef EV_Thread_GetTraceNormal( "getTraceNormal", NULL, 'v' );
const idEventDef EV_Thread_GetTraceEntity( "getTraceEntity", NULL, 'e' );
const idEventDef EV_Thread_GetTraceJoint( "getTraceJoint", NULL, 's' );
const idEventDef EV_Thread_GetTraceBody( "getTraceBody", NULL, 's' );
const idEventDef EV_Thread_FadeIn( "fadeIn", "vf" );
const idEventDef EV_Thread_FadeOut( "fadeOut", "vf" );
const idEventDef EV_Thread_FadeTo( "fadeTo", "vff" );
const idEventDef EV_Thread_StartMusic( "music", "s" );
const idEventDef EV_Thread_Error( "error", "s" );
const idEventDef EV_Thread_Warning( "warning", "s" );
const idEventDef EV_Thread_StrLen( "strLength", "s", 'd' );
const idEventDef EV_Thread_StrLeft( "strLeft", "sd", 's' );
const idEventDef EV_Thread_StrRight( "strRight", "sd", 's' );
const idEventDef EV_Thread_StrSkip( "strSkip", "sd", 's' );
const idEventDef EV_Thread_StrMid( "strMid", "sdd", 's' );
const idEventDef EV_Thread_StrToFloat( "strToFloat", "s", 'f' );
const idEventDef EV_Thread_RadiusDamage( "radiusDamage", "vEEEsf" );
const idEventDef EV_Thread_IsClient( "isClient", NULL, 'f' );
const idEventDef EV_Thread_IsMultiplayer( "isMultiplayer", NULL, 'f' );
const idEventDef EV_Thread_GetFrameTime( "getFrameTime", NULL, 'f' );
const idEventDef EV_Thread_GetTicsPerSecond( "getTicsPerSecond", NULL, 'f' );
const idEventDef EV_Thread_DebugLine( "debugLine", "vvvf" );
const idEventDef EV_Thread_DebugArrow( "debugArrow", "vvvdf" );
const idEventDef EV_Thread_DebugCircle( "debugCircle", "vvvfdf" );
const idEventDef EV_Thread_DebugBounds( "debugBounds", "vvvf" );
const idEventDef EV_Thread_DrawText( "drawText", "svfvdf" );
const idEventDef EV_Thread_InfluenceActive( "influenceActive", NULL, 'd' );

#ifdef DAXMOO
const idEventDef EV_GetNextKey( "getNextKey", "ss", 's' );
const idEventDef EV_Thread_Dax_setSockServer( "setSockServer", "dsd", 'd' );
const idEventDef EV_Thread_Dax_setSockClient( "setSockClient", "dsd", 'd' );
const idEventDef EV_Thread_Dax_evalSockClient( "evalSockClient", "ds", 's' );
const idEventDef EV_Thread_Dax_udpPrint( "udpPrint", "sds", NULL );
const idEventDef EV_Thread_Dax_rconEval( "rconEval", "sd", 's' );
const idEventDef EV_Thread_Dax_scriptString( "scriptString", "Ess", 'd' );
const idEventDef EV_Thread_Dax_toVector( "toVector", "s", 'v' );
const idEventDef EV_Thread_Dax_toVector3( "toVector3", "fff", 'v' );
const idEventDef EV_Thread_Dax_findEntityByDef( "findEntityByDef", "sd", 'E' );
const idEventDef EV_Thread_Dax_toInstance( "toInstance", "ss", 'e' );
const idEventDef EV_Thread_Dax_toClassname( "toClassname", "E", 's' );

const idEventDef EV_Thread_Dax_clearSpawnArgs( "clearSpawnArgs", "s", 'f' );
const idEventDef EV_Thread_Dax_copyFromClass( "copyFromClass", "ds", 's' );
const idEventDef EV_Thread_Dax_copyMissingFromClass( "copyMissingFromClass", "ds", 's' );

//const idEventDef EV_Thread_Dax_copyMissingFromClass( "copyMissingFromClass", "ds", 's' );

const idEventDef EV_Thread_Dax_spawnClassGreater("spawnclassBetter", "ss", 's' );
const idEventDef EV_Thread_Dax_classBetter("classBetter", "ss", 's' );
const idEventDef EV_Thread_Dax_spawnclassBetter("spawnclassBetter", "ss", 's' );
const idEventDef EV_Thread_Dax_copyToClass( "copyToClass", "ds", 's' );
const idEventDef EV_Thread_Dax_createInstance( "createInstance", "ss", 'e' );
const idEventDef EV_Thread_Dax_createClass( "createClass", "ds", 's' );

const idEventDef EV_Thread_Dax_setClassSpawnArg("setClassSpawnArg","sss",NULL);
const idEventDef EV_Thread_Dax_getClassSpawnArg("getClassSpawnArg","sss",'s');
const idEventDef EV_Thread_Dax_clearClassSpawnArgs("clearClassSpawnArgs","ss",'f');

const idEventDef EV_Thread_Dax_getThreadnum( "getThreadnum", NULL, 'f' );
const idEventDef EV_Thread_Dax_getThreadname( "getThreadname", NULL, 's' );

const idEventDef EV_Dax_Str_Filter( "strFilter", "ssd", 'd' );

const idEventDef EV_Thread_Dax_toFunction( "toFunction", "Esd", 's' );
const idEventDef EV_Thread_Dax_invokeScript( "invokeScript", "Ess", NULL );
const idEventDef EV_Thread_Dax_retFloat( "retFloat", NULL, 'f' );
const idEventDef EV_Thread_Dax_retString( "retString", NULL, 's' );
const idEventDef EV_Thread_Dax_retEntity( "retEntity", NULL, 'e' );
const idEventDef EV_Thread_Dax_retVector( "retVector", NULL, 'v' );
const idEventDef EV_Thread_Dax_retInt( "retInt",NULL, 'd' );



const idEventDef EV_Thread_Dax_SayFrom( "sayFrom", "ss", NULL );

const idEventDef EV_guiCheck( "guiCheck", "s",'d' );
const idEventDef EV_guiFind( "guiFind", "sddd",'d' );

EVDEFCONST(idThread,gui,Name,"d",'s')
EVDEFCONST(idThread,gui,Comment,"d",'s')
EVDEFCONST(idThread,gui,IsInteractive,"d",'d')
EVDEFCONST(idThread,gui,IsUniqued,"d",'d')
EVDEFCONST(idThread,gui,SetUniqued,"dd",NULL)
EVDEFCONST(idThread,gui,InitFromFile,"dsdd",'s')
EVDEFCONST(idThread,gui,HandleNamedEvent,"ds",NULL)
EVDEFCONST(idThread,gui,Redraw,"d",NULL)
EVDEFCONST(idThread,gui,DrawCursor,"d",NULL)
EVDEFCONST(idThread,gui,State,"d",'e')
EVDEFCONST(idThread,gui,DeleteStateVar,"ds",NULL)
EVDEFCONST(idThread,gui,SetStateString,"dss",NULL)
EVDEFCONST(idThread,gui,SetStateInt,"dsd",NULL)
EVDEFCONST(idThread,gui,SetStateFloat,"dsf",NULL)
EVDEFCONST(idThread,gui,SetStateBool,"dsd",NULL)
EVDEFCONST(idThread,gui,GetStateString,"ds",'s')
EVDEFCONST(idThread,gui,GetStateInt,"ds",'d')
EVDEFCONST(idThread,gui,GetStateFloat,"ds",'f')
EVDEFCONST(idThread,gui,GetStateBool,"ds",'d')
EVDEFCONST(idThread,gui,StateChanged,"dfd",NULL)
EVDEFCONST(idThread,gui,Activate,"ddd",'s')
EVDEFCONST(idThread,gui,Trigger,"dd",NULL)
EVDEFCONST(idThread,gui,SetCursor,"dff",NULL)
EVDEFCONST(idThread,gui,CursorX,"d",'f')
EVDEFCONST(idThread,gui,CursorY,"d",'f')

EVDEFCONST(idThread,dict,Create,NULL,'d')
EVDEFCONST(idThread,dict,Delete,"ds",NULL)
EVDEFCONST(idThread,dict,GetAngles,"ds",'d')
EVDEFCONST(idThread,dict,GetMatrix,"ds",'d')
EVDEFCONST(idThread,dict,GetBool,"ds",'d')
EVDEFCONST(idThread,dict,GetFloat,"ds",'f')
EVDEFCONST(idThread,dict,GetInt,"ds",'d')
EVDEFCONST(idThread,dict,GetNumKeyVals,"d",'d')
EVDEFCONST(idThread,dict,GetString,"ds",'s')
EVDEFCONST(idThread,dict,SetAngles,"dsd",NULL)
EVDEFCONST(idThread,dict,SetBool,"dsd",NULL)
EVDEFCONST(idThread,dict,SetDefaults,"dsd",NULL)
EVDEFCONST(idThread,dict,SetFloat,"dsf",NULL)
EVDEFCONST(idThread,dict,SetInt,"dsd",NULL)
EVDEFCONST(idThread,dict,SetMatrix,"dsd",NULL)
EVDEFCONST(idThread,dict,SetString,"dss",NULL)
EVDEFCONST(idThread,dict,SetVec2,"dsv",NULL)
EVDEFCONST(idThread,dict,SetVec4,"dsd",NULL)
EVDEFCONST(idThread,dict,SetVector,"dsv",NULL)

#endif //DAXMOO 


CLASS_DECLARATION( idClass, idThread )
EVENT( EV_Thread_Execute,               idThread::Event_Execute )
EVENT( EV_Thread_TerminateThread,       idThread::Event_TerminateThread )
EVENT( EV_Thread_Pause,                 idThread::Event_Pause )
EVENT( EV_Thread_Wait,                  idThread::Event_Wait )
EVENT( EV_Thread_WaitFrame,             idThread::Event_WaitFrame )
EVENT( EV_Thread_WaitFor,               idThread::Event_WaitFor )
EVENT( EV_Thread_WaitForThread,         idThread::Event_WaitForThread )
EVENT( EV_Thread_Print,                 idThread::Event_Print )
EVENT( EV_Thread_PrintLn,               idThread::Event_PrintLn )
EVENT( EV_Thread_Say,                   idThread::Event_Say )
EVENT( EV_Thread_Assert,                idThread::Event_Assert )
EVENT( EV_Thread_Trigger,               idThread::Event_Trigger )
EVENT( EV_Thread_SetCvar,               idThread::Event_SetCvar )
EVENT( EV_Thread_GetCvar,               idThread::Event_GetCvar )
EVENT( EV_Thread_Random,                idThread::Event_Random )
#if defined(_D3XP) || defined(D3V_D3XP)
EVENT( EV_Thread_RandomInt,             idThread::Event_RandomInt )
#endif
EVENT( EV_Thread_GetTime,               idThread::Event_GetTime )
EVENT( EV_Thread_KillThread,            idThread::Event_KillThread )
EVENT( EV_Thread_SetThreadName,         idThread::Event_SetThreadName )
EVENT( EV_Thread_GetEntity,             idThread::Event_GetEntity )
EVENT( EV_Thread_Spawn,                 idThread::Event_Spawn )
EVENT( EV_Thread_CopySpawnArgs,         idThread::Event_CopySpawnArgs )
EVENT( EV_Thread_SetSpawnArg,           idThread::Event_SetSpawnArg )
EVENT( EV_Thread_SpawnString,           idThread::Event_SpawnString )
EVENT( EV_Thread_SpawnFloat,            idThread::Event_SpawnFloat )
EVENT( EV_Thread_SpawnVector,           idThread::Event_SpawnVector )
EVENT( EV_Thread_ClearPersistantArgs,   idThread::Event_ClearPersistantArgs )
EVENT( EV_Thread_SetPersistantArg,      idThread::Event_SetPersistantArg )
EVENT( EV_Thread_GetPersistantString,   idThread::Event_GetPersistantString )
EVENT( EV_Thread_GetPersistantFloat,    idThread::Event_GetPersistantFloat )
EVENT( EV_Thread_GetPersistantVector,   idThread::Event_GetPersistantVector )
EVENT( EV_Thread_AngToForward,          idThread::Event_AngToForward )
EVENT( EV_Thread_AngToRight,            idThread::Event_AngToRight )
EVENT( EV_Thread_AngToUp,               idThread::Event_AngToUp )
EVENT( EV_Thread_Sine,                  idThread::Event_GetSine )
EVENT( EV_Thread_Cosine,                idThread::Event_GetCosine )
#if defined(_D3XP) || defined(D3V_D3XP)
EVENT( EV_Thread_ArcSine,               idThread::Event_GetArcSine )
EVENT( EV_Thread_ArcCosine,             idThread::Event_GetArcCosine )
#endif
EVENT( EV_Thread_SquareRoot,            idThread::Event_GetSquareRoot )
EVENT( EV_Thread_Normalize,             idThread::Event_VecNormalize )
EVENT( EV_Thread_VecLength,             idThread::Event_VecLength )
EVENT( EV_Thread_VecDotProduct,         idThread::Event_VecDotProduct )
EVENT( EV_Thread_VecCrossProduct,       idThread::Event_VecCrossProduct )
EVENT( EV_Thread_VecToAngles,           idThread::Event_VecToAngles )
#if defined(_D3XP) || defined(D3V_D3XP)
EVENT( EV_Thread_VecToOrthoBasisAngles, idThread::Event_VecToOrthoBasisAngles )
EVENT( EV_Thread_RotateVector,          idThread::Event_RotateVector )
#endif
EVENT( EV_Thread_OnSignal,              idThread::Event_OnSignal )
EVENT( EV_Thread_ClearSignal,           idThread::Event_ClearSignalThread )
EVENT( EV_Thread_SetCamera,             idThread::Event_SetCamera )
EVENT( EV_Thread_FirstPerson,           idThread::Event_FirstPerson )
EVENT( EV_Thread_Trace,                 idThread::Event_Trace )
EVENT( EV_Thread_TracePoint,            idThread::Event_TracePoint )
EVENT( EV_Thread_GetTraceFraction,      idThread::Event_GetTraceFraction )
EVENT( EV_Thread_GetTraceEndPos,        idThread::Event_GetTraceEndPos )
EVENT( EV_Thread_GetTraceNormal,        idThread::Event_GetTraceNormal )
EVENT( EV_Thread_GetTraceEntity,        idThread::Event_GetTraceEntity )
EVENT( EV_Thread_GetTraceJoint,         idThread::Event_GetTraceJoint )
EVENT( EV_Thread_GetTraceBody,          idThread::Event_GetTraceBody )
EVENT( EV_Thread_FadeIn,                idThread::Event_FadeIn )
EVENT( EV_Thread_FadeOut,               idThread::Event_FadeOut )
EVENT( EV_Thread_FadeTo,                idThread::Event_FadeTo )
EVENT( EV_SetShaderParm,                idThread::Event_SetShaderParm )
EVENT( EV_Thread_StartMusic,            idThread::Event_StartMusic )
EVENT( EV_Thread_Warning,               idThread::Event_Warning )
EVENT( EV_Thread_Error,                 idThread::Event_Error )
EVENT( EV_Thread_StrLen,                idThread::Event_StrLen )
EVENT( EV_Thread_StrLeft,               idThread::Event_StrLeft )
EVENT( EV_Thread_StrRight,              idThread::Event_StrRight )
EVENT( EV_Thread_StrSkip,               idThread::Event_StrSkip )
EVENT( EV_Thread_StrMid,                idThread::Event_StrMid )
EVENT( EV_Thread_StrToFloat,            idThread::Event_StrToFloat )
EVENT( EV_Thread_RadiusDamage,          idThread::Event_RadiusDamage )
EVENT( EV_Thread_IsClient,              idThread::Event_IsClient )
EVENT( EV_Thread_IsMultiplayer,         idThread::Event_IsMultiplayer )
EVENT( EV_Thread_GetFrameTime,          idThread::Event_GetFrameTime )
EVENT( EV_Thread_GetTicsPerSecond,      idThread::Event_GetTicsPerSecond )
EVENT( EV_CacheSoundShader,             idThread::Event_CacheSoundShader )
EVENT( EV_Thread_DebugLine,             idThread::Event_DebugLine )
EVENT( EV_Thread_DebugArrow,            idThread::Event_DebugArrow )
EVENT( EV_Thread_DebugCircle,           idThread::Event_DebugCircle )
EVENT( EV_Thread_DebugBounds,           idThread::Event_DebugBounds )
EVENT( EV_Thread_DrawText,              idThread::Event_DrawText )
EVENT( EV_Thread_InfluenceActive,       idThread::Event_InfluenceActive )

#ifdef DAXMOO
EVENT( EV_Thread_Dax_setSockServer,        idThread::Event_Dax_setSockServer )
EVENT( EV_Thread_Dax_setSockClient,        idThread::Event_Dax_setSockClient )
EVENT( EV_Thread_Dax_evalSockClient,        idThread::Event_Dax_evalSockClient )
EVENT( EV_Thread_Dax_udpPrint,        idThread::Event_Dax_udpPrint )
EVENT( EV_Thread_Dax_rconEval,       idThread::Event_Dax_rconEval )
EVENT( EV_Thread_Dax_toVector,       idThread::Event_Dax_toVector )
EVENT( EV_Thread_Dax_toVector3,       idThread::Event_Dax_toVector3 )
EVENT( EV_Thread_Dax_toInstance,       idThread::Event_Dax_toInstance )
EVENT( EV_Thread_Dax_toClassname,       idThread::Event_Dax_toClassname )
EVENT( EV_GetNextKey,           idThread::Event_GetNextKey )

EVENT(  EV_Thread_Dax_clearSpawnArgs, idThread::Event_Dax_clearSpawnArgs)
EVENT(  EV_Thread_Dax_copyFromClass, idThread::Event_Dax_copyFromClass)
EVENT(  EV_Thread_Dax_copyMissingFromClass, idThread::Event_Dax_copyMissingFromClass)
EVENT(  EV_Thread_Dax_classBetter, idThread::Event_Dax_classBetter)
EVENT(  EV_Thread_Dax_spawnclassBetter, idThread::Event_Dax_spawnclassBetter)
EVENT(  EV_Thread_Dax_createInstance, idThread::Event_Dax_createInstance)
EVENT(  EV_Thread_Dax_copyToClass, idThread::Event_Dax_copyToClass)
EVENT(  EV_Thread_Dax_createClass, idThread::Event_Dax_createClass)
EVENT(  EV_Thread_Dax_getThreadnum, idThread::Event_Dax_getThreadnum)
EVENT(  EV_Thread_Dax_getThreadname, idThread::Event_Dax_getThreadname)

EVENT(  EV_Thread_Dax_setClassSpawnArg, idThread::Event_Dax_setClassSpawnArg)
EVENT(  EV_Thread_Dax_getClassSpawnArg, idThread::Event_Dax_getClassSpawnArg)
EVENT(  EV_Thread_Dax_clearClassSpawnArgs, idThread::Event_Dax_clearClassSpawnArgs)

EVENT( EV_Thread_Dax_findEntityByDef,       idThread::Event_Dax_findEntityByDef )
EVENT( EV_Thread_Dax_scriptString,       idThread::Event_Dax_scriptString )

EVENT( EV_Dax_Str_Filter,       idThread::Event_Dax_Str_Filter )


EVENT( EV_Thread_Dax_toFunction,       idThread::Event_Dax_toFunction )
EVENT( EV_Thread_Dax_invokeScript,       idThread::Event_Dax_invokeScript )
EVENT( EV_Thread_Dax_retFloat,       idThread::Event_Dax_retFloat )
EVENT( EV_Thread_Dax_retVector,       idThread::Event_Dax_retVector )
EVENT( EV_Thread_Dax_retInt,       idThread::Event_Dax_retInt )
EVENT( EV_Thread_Dax_retString,       idThread::Event_Dax_retString )
EVENT( EV_Thread_Dax_retEntity,       idThread::Event_Dax_retEntity )

EVENT( EV_Thread_Dax_SayFrom,       idThread::Event_Dax_SayFrom )

/*
#ifndef EVDEFEVENT
#define EVDEFEVENT(obj,prefixr,namer,parms,ret) EVENT( EV_##prefixr##namer, obj::Event_##prefixr##namer)
#endif

EVDEFEVENT(idThread,gui,Check,"s",'d')
EVDEFEVENT(idThread,gui,Find,"sddd",'d')
EVDEFEVENT(idThread,gui,Name,"d",'s')
EVDEFEVENT(idThread,gui,Comment,"d",'s')
EVDEFEVENT(idThread,gui,IsInteractive,"d",'d')
EVDEFEVENT(idThread,gui,IsUniqued,"d",'d')
EVDEFEVENT(idThread,gui,SetUniqued,"dd",NULL)
EVDEFEVENT(idThread,gui,InitFromFile,"dsdd",'s')
EVDEFEVENT(idThread,gui,HandleNamedEvent,"ds",NULL)
EVDEFEVENT(idThread,gui,Redraw,"d",NULL)
EVDEFEVENT(idThread,gui,DrawCursor,"d",NULL)
EVDEFEVENT(idThread,gui,State,"d",'e')
EVDEFEVENT(idThread,gui,DeleteStateVar,"ds",NULL)
EVDEFEVENT(idThread,gui,SetStateString,"dss",NULL)
EVDEFEVENT(idThread,gui,SetStateInt,"dsd",NULL)
EVDEFEVENT(idThread,gui,SetStateFloat,"dsf",NULL)
EVDEFEVENT(idThread,gui,SetStateBool,"dsd",NULL)
EVDEFEVENT(idThread,gui,GetStateString,"ds",'s')
EVDEFEVENT(idThread,gui,GetStateInt,"ds",'d')
EVDEFEVENT(idThread,gui,GetStateFloat,"ds",'f')
EVDEFEVENT(idThread,gui,GetStateBool,"ds",'d')
EVDEFEVENT(idThread,gui,StateChanged,"dfd",NULL)
EVDEFEVENT(idThread,gui,Activate,"ddd",'s')
EVDEFEVENT(idThread,gui,Trigger,"dd",NULL)
EVDEFEVENT(idThread,gui,SetCursor,"dff",NULL)
EVDEFEVENT(idThread,gui,CursorX,"d",'f')
EVDEFEVENT(idThread,gui,CursorY,"d",'f')


EVDEFEVENT(idThread,dict,Create,NULL,'d')
EVDEFEVENT(idThread,dict,Delete,"ds",NULL)
EVDEFEVENT(idThread,dict,GetAngles,"ds",'d')
EVDEFEVENT(idThread,dict,GetMatrix,"ds",'d')
EVDEFEVENT(idThread,dict,GetBool,"ds",'d')
EVDEFEVENT(idThread,dict,GetFloat,"ds",'f')
EVDEFEVENT(idThread,dict,GetInt,"ds",'d')
EVDEFEVENT(idThread,dict,GetNumKeyVals,"d",'d')
EVDEFEVENT(idThread,dict,GetString,"ds",'s')
EVDEFEVENT(idThread,dict,SetAngles,"dsd",NULL)
EVDEFEVENT(idThread,dict,SetBool,"dsd",NULL)
EVDEFEVENT(idThread,dict,SetDefaults,"dsd",NULL)
EVDEFEVENT(idThread,dict,SetFloat,"dsf",NULL)
EVDEFEVENT(idThread,dict,SetInt,"dsd",NULL)
EVDEFEVENT(idThread,dict,SetMatrix,"dsd",NULL)
EVDEFEVENT(idThread,dict,SetString,"dss",NULL)
EVDEFEVENT(idThread,dict,SetVec2,"dsv",NULL)
EVDEFEVENT(idThread,dict,SetVec4,"dsd",NULL)
EVDEFEVENT(idThread,dict,SetVector,"dsv",NULL)
//scriptString
*/
#endif  //DAXMOO
END_CLASS
#ifdef DAXMOO
/*
cyctask cyc_bot_1 goto light_1
cyctask cyc_bot_1 goto player1
cyctask cyc_bot_1 print hello
cyctask cyc_bot_1 print "hello"
cyctask cyc_bot_1 print "hello world"
cyctask cyc_bot_1 print hi
cyctask cyc_bot_1 eval 1
================
idThread::Event_Dax_scriptString     //evalString
================
*/
void idThread::Event_Dax_scriptString(idEntity *check,const char *funcname,const char *script) {
	// for (idEntity* check = gameLocal.spawnedEntities.Next(); check != NULL; check = check->spawnNode.Next() ) {
	//     gameLocal.program.SetEntity( check->name, check );
	// }
	try {
		gameLocal.Printf(script);
		if (gameLocal.program.CompileText( "console", script, true )) {
			const function_t* func = gameLocal.program.FindFunction( funcname );
			if (func) {
				idThread* thread = new idThread(check,func);
				thread->Start();        
				idThread::ReturnInt(thread->GetThreadNum());
			} else {
				idThread::ReturnInt(0);
			}
		} else {
		}
	} catch (idCompileError &err) {
		gameLocal.Printf( "%s", err.error );
		idThread::ReturnInt(-1);
	};
}

const char* idThread::GetStateName() {
	if (waitingForThread) {
		return va("waitingForThread:%d",waitingForThread->GetThreadNum());
	}
	if (waitingFor != ENTITYNUM_NONE) {
		return va("waitingFor:%d",waitingFor);
	}
	if (waitingUntil && ( waitingUntil > gameLocal.time )) {
		return va("waitingUntil:%d",gameLocal.time);
	}
	if (IsDoneProcessing()) return va("IsDoneProcessing");
	if (IsDying()) return va("IsDying");
	if (this==CurrentThread()) return va("RunningCurrent");
	return va("Running");
}

bool validPtr2(void* ptr) {
	if (ptr==(void*)0x00000000)	return false;
	if (ptr==(void*)0x11111111)	return false;
	if (ptr==(void*)0xfeeefefe)	return false;
	if (ptr==(void*)0xfeeefeee)	return false;
	if (ptr==(void*)0xeeeeeeee)	return false;
	if (ptr==(void*)0xdddddddd)	return false;
	if (ptr==(void*)0xffffffff)	return false;
	return true;
}



/*
================
idThread::Event_Dax_udpPrint
================
*/
void udpPrintTo(const char* host,int port,const char* msg) {
	#ifdef DAXMOO_UDPPRINT
	char szServer[128]; 
	int nPort; 
	// We are going to broadcast 
	strcpy(szServer,"127.0.0.1"); 
	nPort = 6999; 
	// 
	// Find the server 
	// 
	static LPHOSTENT lpHostEntry = 0;
	if (!lpHostEntry) {
		lpHostEntry = gethostbyname(szServer); 
	}
	if (!isValid(lpHostEntry)) {
		gameLocal.Printf( "ERROR gethostbyname()\n"); 
		return; 
	}

	// 
	// Create a UDP/IP datagram socket 
	// 
	SOCKET theSocket; 
	theSocket = socket(AF_INET,	// Address family 
					   SOCK_DGRAM, // Socket type 
					   IPPROTO_UDP); // Protocol 
	if (theSocket == INVALID_SOCKET) {
		gameLocal.Printf("ERROR socket()\n"); 
		return; 
	}

	// 
	// Fill in the address structure for the server 
	// 
	SOCKADDR_IN saServer; 
	saServer.sin_family = AF_INET; 
	if (!isValid(lpHostEntry->h_addr_list))	return;
	if (!isValid(lpHostEntry->h_name)) return;
	//return;
	saServer.sin_addr = *((LPIN_ADDR)*lpHostEntry->h_addr_list); 
	// ^ Server's address 
	saServer.sin_port = htons(nPort); // Port number from command line 
	saServer.sin_addr.s_addr = inet_addr(szServer); 
	// 
	// Send data to the server 
	// 
	char szBuf[MAX_STRING_CHARS]; 
	int nRet; 
	strcpy(szBuf, msg); 
	nRet = sendto(theSocket, // Socket 
				  szBuf, // Data buffer 
				  strlen(szBuf), // Length of data 
				  0, // Flags 
				  (LPSOCKADDR)&saServer, // Server address 
				  sizeof(struct sockaddr));	// Length of address 
	if (nRet == SOCKET_ERROR) {
		gameLocal.Printf( "ERROR sendto()\n"); 
		closesocket(theSocket); 
		return; 
	}
	closesocket(theSocket); 
	#endif
	return; 
}


void idThread::Event_Dax_setSockClient(int reuse,const char* host,int port) {
	if (reuse==0 || tcyc==NULL) {
		tcyc = new CycAccess();
	}
	tcyc->setConnection(host,port);
	tcyc->theConnection.persistent_connection = (reuse > 1)?1:0;
	ReturnInt(tcyc->theConnection.persistent_connection);
}

	#ifdef DAXMOO_THREADS
		#include <process.h>
	#endif
//unsigned int (__stdcall *)(void *)
unsigned __stdcall idThread::WaitForSocket(void* params) {
	bool oldManualControl = manualControl;
	manualControl = true;
	sockResult = tcyc->converseString((const char*)params);
	manualControl = oldManualControl;
	return true;
}
void idThread::Event_Dax_evalSockClient(int reuse, const char* send) {
	ClearWaitFor();
	if (tcyc==NULL) {
		Event_Dax_setSockClient(reuse,cvarSystem->GetCVarString("cyc_hostname"),cvarSystem->GetCVarInteger("cyc_port"));
	} else {
		Event_Dax_setSockClient(reuse,tcyc->theConnection.hostname,tcyc->theConnection.portnum);
	}
	sockResult = NULL;
	#ifdef DAXMOO_THREADS
	sockThread = (HANDLE)_beginthreadex(NULL,0,WaitForSocket,send, CREATE_SUSPENDED, &threadId);
	if (sockThread == NULL) {
		DWORD dwError = GetLastError();
		gameLocal.Printf("Error creating sockThread for ~s\nReason ~s\n",send,dwError);
	} else {
		ResumeThread(sockThread);
		WaitForSingleObject(sockThread,INFINITE);	  //THREAD_PRIORITY_NORMA
		CloseHandle(sockThread);
		sockThread = NULL;
	}   
	#else // DAXMOO_THREADS
	WaitForSocket((void*)send);
	if (reuse<2) {
		tcyc->theConnection.CloseConnection();
	}
	#endif // DAXMOO_THREADS
	if (sockResult) {
		ReturnString(sockResult.c_str());
	} else {
		ReturnString("<no sockResult>");
	}
}

void idThread::Event_Dax_setSockServer(int reuse,const char* cmd,int port) {
	ReturnInt(port);
}

void idThread::Event_Dax_udpPrint(const char* host,int port,const char* msg) {
	udpPrintTo(host,port,msg);
}

void idThread::Event_Dax_toVector3( float xx,float yy,float zz) {
	idThread::ReturnVector(idVec3(xx,yy,zz));
}
void idThread::Event_Dax_toVector( const char *string) {
	idVec3 vect3;
	sscanf(string,"%f %f %f",&vect3.x,&vect3.y,&vect3.z);  
	idThread::ReturnVector(vect3);
}
idInterpreter* idThread::Get_Dax_Interpreter(void) {
	return &interpreter;
}

void idThread::Event_Dax_Str_Filter( const char *filter, const char *name, int caseSense ) {
	return idThread::ReturnInt(idStr::Filter(filter,name,caseSense!=0));
}

void idThread::Event_Dax_findEntityByDef( const char *name, int num ) {
	for (idEntity* check = gameLocal.spawnedEntities.Next(); check != NULL; check = check->spawnNode.Next()) {
		if (check) {
			if (idStr::Filter(name,check->GetEntityDefName(),false)) {
				if (--num<0) {
					//  gameLocal.program.SetEntity( check->name, check );
					idThread::ReturnEntity(check);
					return;
				}
			}
		}
	}
	idThread::ReturnEntity( ( idEntity * )NULL );
}
/*
================
idThread::Event_Dax_rconEval
================
*/
void idThread::Event_Dax_rconEval( const char *string, int num ) {
	idThread::ReturnString( string );
	cmdSystem->BufferCommandText( CMD_EXEC_NOW, string);
}

void idThread::Event_Dax_toInstance(const char* spawnclass, const char *classname) {
	idEntity* ent = NULL;
	idClass *obj =  (idEntity*)idClass::CreateInstance(spawnclass);
	if (!obj) {
		obj = idEntity::CreateInstance();
	}
	if (obj->IsType( idEntity::Type )) {
		ent = static_cast<idEntity *>(obj);
	} else {
		ent = reinterpret_cast<idEntity *>(obj);
	}
	idDeclEntityDef* entdef = (idDeclEntityDef*)static_cast<const idDeclEntityDef*>(declManager->FindType(DECL_ENTITYDEF, classname, true ));
	//idDeclEntityDef& clazz = gameLocal.FindEntityDef(classname,true);
	ent->spawnArgs.args.list = entdef->dict.args.list;
	idStr named = classname;
	named.Append("_class");
	ent->SetName(named.c_str());
	idThread::ReturnEntity(ent);
}

void idThread::Event_Dax_toClassname( idEntity *check) {
	if (check)
		idThread::ReturnString( check->GetEntityDefName() );
	else
		idThread::ReturnString( "<no ent for toClassname>" );
}

void idThread::Event_Dax_clearSpawnArgs( const char *prefix) {
	int cleared = 0;
	for (int i = 0; i < spawnArgs.GetNumKeyVals(); i++) {
		const idKeyValue *kv = spawnArgs.GetKeyVal( i );  
		const char* key = kv->GetKey().c_str();
		if (idStr::Filter(prefix,key,false )) {
			//gameLocalPrintf( "(clearing \"%s\"  \"%s\" )\n", key, kv->GetValue().c_str() );  
			spawnArgs.Delete(key);
			idThread::ReturnFloat(1);
			Event_Dax_clearSpawnArgs(prefix);
			return;
		}
	}  
	idThread::ReturnFloat(0);
}

/*
================
idEntity::Event_GetNextKey
================
*/
void idThread::Event_GetNextKey( const char *prefix, const char *lastMatch ) {
	const idKeyValue *kv;
	const idKeyValue *previous;

	if (*lastMatch) {
		previous = spawnArgs.FindKey( lastMatch );
	} else {
		previous = NULL;
	}

	kv = spawnArgs.MatchPrefix( prefix, previous );
	if (!kv) {
		idThread::ReturnString( "" );
	} else {
		idThread::ReturnString( kv->GetKey() );
	}
}

void idThread::Event_Dax_getThreadname() {
	idThread::ReturnString( threadName );
}

void idThread::Event_Dax_getThreadnum() {
	idThread::ReturnFloat( threadNum );
}

void idThread::Event_Dax_copyToClass(int type,const char* classname) {
	declType_t declType = DECL_ENTITYDEF;
	if (type>0)	declType = (declType_t)type;
	idDeclEntityDef* check = (idDeclEntityDef*)gameLocal.FindEntityDef(classname,false);
	if (!check) {
		Event_Dax_createClass(type,classname);
		check = (idDeclEntityDef*)gameLocal.FindEntityDef(classname,false);
	} //SetDefaults
	if (check) {
		check->dict.Copy(spawnArgs);
		idThread::ReturnString( check->GetName());
	} else {
		idThread::ReturnString( "<no class>" );
	}
}

void idThread::Event_Dax_copyFromClass(int type,const char* classname) {
	declType_t declType = DECL_ENTITYDEF;
	if (type>0)	declType = (declType_t)type;
	const idDeclEntityDef *check = static_cast<const idDeclEntityDef* >(declManager->FindType(declType, classname, false ));
	if (!check) {
		Event_Dax_createClass(type,classname);
		check = static_cast<const idDeclEntityDef*> (declManager->FindType(declType, classname, false ));
	} //SetDefaults
	if (check) {
		spawnArgs.Copy(check->dict);
		//spawnArgs.(check->dict);
		idThread::ReturnString( check->GetName());
	} else {
		idThread::ReturnString( "<no class>" );
	}
}

void idThread::Event_Dax_classBetter(const char* type,const char* classname) {
	const idDeclEntityDef *cls1 = gameLocal.FindEntityDef( classname, false );  
	const idDeclEntityDef *cls2 = gameLocal.FindEntityDef( type, false );  
	if (!cls1 && !cls2) {
		idThread::ReturnString("");
	} else if (!cls1) {
		idThread::ReturnString(type);
	} else if (!cls2) {
		idThread::ReturnString(classname);
	} else if (0 && cls1) {
		idThread::ReturnString(classname);
	} else if (0 && cls2) {
		idThread::ReturnString(type);
	} else {
		int cls1s = cls1->dict.GetNumKeyVals();
		int cls2s = cls1->dict.GetNumKeyVals();
		if (cls1s>cls2s) {
			idThread::ReturnString(classname);
		} else {
			idThread::ReturnString(type);
		}
	}   
}


void idThread::Event_Dax_setClassSpawnArg(const char* classname,const char* key,const char* dvalue) {
	idDeclEntityDef* check = (idDeclEntityDef*)gameLocal.FindEntityDef(classname,true);
	if (check) {
		check->dict.Set(key,dvalue);
	}
}

void idThread::Event_Dax_getClassSpawnArg(const char* classname,const char* key,const char* dvalue) {
	idDeclEntityDef* check = (idDeclEntityDef*)gameLocal.FindEntityDef(classname,false);
	if (check) {
		const idKeyValue* kv = check->dict.FindKey(key);
		if (kv) {
			idThread::ReturnString(kv->GetValue().c_str());
		} else {
			idThread::ReturnString(dvalue);
		}
	} else {
		idThread::ReturnString( "<no class>" );
	}
}

void idThread::Event_Dax_clearClassSpawnArgs(const char* classname,const char* prefix) {
	idDeclEntityDef* check = (idDeclEntityDef*)gameLocal.FindEntityDef(classname,false);
	if (check) {
		int cleared = 0;
		for (int i = 0; i < check->dict.GetNumKeyVals(); i++) {
			const idKeyValue *kv = check->dict.GetKeyVal( i );  
			const char* key = kv->GetKey().c_str();
			if (idStr::Filter(prefix,key,false )) {
				//gameLocalPrintf( "(clearing \"%s\"  \"%s\" )\n", key, kv->GetValue().c_str() );  
				check->dict.Delete(key);
				Event_Dax_clearClassSpawnArgs(classname,prefix);
				idThread::ReturnFloat(1);
				return;
			}
		}  
		idThread::ReturnFloat(0);
	} else {
		idThread::ReturnFloat(-1);
	}

}


void idThread::Event_Dax_spawnclassBetter(const char* type,const char* classname) {
	const idTypeInfo *cls1 = idClass::GetClass( classname );
	const idTypeInfo *cls2 = idClass::GetClass( type );
	if (!cls1 && !cls2) {
		idThread::ReturnString("");
	} else if (!cls1) {
		idThread::ReturnString(type);
	} else if (!cls2) {
		idThread::ReturnString(classname);
	} else if (cls1->IsType(*cls2)) {
		idThread::ReturnString(classname);
	} else if (cls2->IsType(*cls1)) {
		idThread::ReturnString(type);
	} else {
		int cls1s = 0;
		int cls2s = 0;
		while (cls1 && cls1->superclass) {
			cls1 = idClass::GetClass(cls1->superclass);
			cls1s++;
		}
		while (cls2 && cls2->superclass) {
			cls2 = idClass::GetClass(cls2->superclass);
			cls2s++;
		}
		if (cls1s>cls2s) {
			idThread::ReturnString(classname);
		} else {
			idThread::ReturnString(type);
		}
	}
}

void idThread::Event_Dax_copyMissingFromClass(int type,const char* classname) {
	declType_t declType = DECL_ENTITYDEF;
	if (type>0)	declType = (declType_t)type;
	const idDeclEntityDef *check = static_cast<const idDeclEntityDef* >(declManager->FindType(declType, classname, false ));
	if (!check) {
		Event_Dax_createClass(type,classname);
		check = static_cast<const idDeclEntityDef*> (declManager->FindType(declType, classname, false ));
	} //SetDefaults
	if (check) {
		const idDict* d = &check->dict;
		// d->Print();
		spawnArgs.SetDefaults(d);
		idThread::ReturnString( check->GetName());
	} else {
		idThread::ReturnString( "<no class>" );
	}
}             

void idThread::Event_Dax_createInstance( const char *name,const char *classname ) {
	const char  *spawn;
	idTypeInfo  *cls;
	idClass     *obj;
	idStr       error;
	idEntity *ent = NULL;
	const idDeclEntityDef *def = NULL;
	ReturnEntity( ent );

	if (name) {
		spawnArgs.Set( "name", name );
	}

	if (spawnArgs.GetString( "name", "", &name )) {
		sprintf( error, " on '%s'", name);
	}

	spawnArgs.Set( "classname", classname );

	def = gameLocal.FindEntityDef( classname, true );

	if (def->IsImplicit()) {
		Warning( "Unknown classname '%s'%s.", classname, error.c_str() );
		def = gameLocal.FindEntityDef( classname, true );
		Event_Dax_copyToClass(0,classname);
	}

	#ifdef _D3XP
	if (!spawnArgs.FindKey( "slowmo" )) {
		bool slowmo = true;

		for (int i = 0; fastEntityList[i]; i++) {
			if (!idStr::Cmp( classname, fastEntityList[i] )) {
				slowmo = false;
				break;
			}
		}

		if (!slowmo) {
			spawnArgs.SetBool( "slowmo", slowmo );
		}
	}
	#endif

	gameLocal.spawnArgs = spawnArgs;

	// check if we should spawn a class object
	spawnArgs.GetString( "spawnclass", NULL, &spawn );
	if (spawn) {
		cls = idClass::GetClass( spawn );
		if (!cls) {
			gameLocal.Warning( "Could not spawn '%s'.  idClass '%s' not found %s.", classname, spawn, error.c_str() );
			cls = &idMoveable::Type;
		}
		obj = cls->CreateInstance();
		if (!obj) {
			gameLocal.Warning( "Could not spawn '%s'. Instance could not be created %s.", classname, error.c_str() );
		} else {
			obj->CallSpawn();
			if (obj->IsType( idEntity::Type )) {
				ent = static_cast<idEntity *>(obj);
			}
			if (ent) {
				ent->SetName(name);
				ReturnEntity( ent );
				ent->spawnArgs.Print();
			}
		}
	}
	// check if we should call a script function to spawn
	spawnArgs.GetString( "spawnfunc", NULL, &spawn );
	if (spawn) {
		const function_t *func = gameLocal.program.FindFunction( spawn );
		if (!func) {
			gameLocal.Warning( "Could not spawn '%s'.  Script function '%s' not found%s.", classname, spawn, error.c_str() );
		} else {
			idThread *thread = new idThread( func );
			thread->DelayedStart( 0 );
			thread->spawnArgs.Print();
		}
	}
}



void idThread::Event_Dax_createClass(int type,const char* classname) {
	declType_t declType = DECL_ENTITYDEF;
	if (type>0)	declType = (declType_t)type;
	const idDeclEntityDef *check = static_cast<const idDeclEntityDef* >(declManager->FindType(declType, classname, false ));
	if (!check) {
		check = static_cast<const idDeclEntityDef*> (declManager->FindType(declType, classname, true ));
	} //SetDefaults
	if (check) {
/*
//	check->dict.Set( "classname", value );
		declManager->RegisterDeclType( "model",				DECL_ENTITYDEF,		idDeclAllocator<idDeclModelDef> );
		idDecl decl = declManager->CreateEntityDef(type,name,false);
		xrayEntityHandle = declManager->AddEntityDef( &xrayEntity );
		gameRenderWorld->UpdateEntityDef( xrayEntityHandle, &xrayEntity );
*/
		idThread::ReturnString( check->GetName());
	} else {
		idThread::ReturnString( "<no class>" );
	}
}

//-------------------------------------------------------------------------------------- 
// Description: Call a function with variable arguments. 
// Parameters: The script function to call, 
// The format string (same as script event format strings), 
// The variable argument list. 
// Returns: None. 
//-------------------------------------------------------------------------------------- 
// cycexec <entity1> <functname2> <a3> <b4>
// cycexec talk cyc_bot_1 hi

void idThread::Event_Dax_invokeScript(idEntity* ent,const char* fnname,const char* theargs) {
	const idCmdArgs *args =  new idCmdArgs(theargs,false);
	const function_t* func = NULL;
	if (ent) {
		func = ent->scriptObject.GetFunction(fnname);
	}
	if (!func) {
		func = gameLocal.program.FindFunction(fnname);
	}
	if (!func) {
		Warning("missing: cant find %s ",fnname); 
		return;
	}
	idEntity* savedEntity = interpreter.eventEntity;
	interpreter.eventEntity = ent;
	interpreter.InvokeScript(func,*args);
	interpreter.eventEntity = savedEntity;
}

void idThread::Event_Dax_retInt() {
	// just does it.. its magic!
	//ReturnInt(*gameLocal.program.returnDef->value.intPtr);
}
void idThread::Event_Dax_retFloat() {
	//ReturnFloat(*gameLocal.program.returnDef->value.floatPtr);
}
void idThread::Event_Dax_retVector() {
	//ReturnVector(*gameLocal.program.returnDef->value.vectorPtr);
}
void idThread::Event_Dax_retString() {
	//ReturnString(gameLocal.program.returnDef->value.stringPtr);
}
void idThread::Event_Dax_retEntity() {
	//ReturnEntity(gameLocal.program.returnDef->value.entityNumberPtr+1);
}

void idThread::Event_Dax_toFunction(  idEntity *check, const char *name, int giveNames) {
	idStr pbuffer = "";  
	const function_t* func = NULL;
	if ((check)) {
		func = check->scriptObject.GetFunction(name);
	}
	if (!func) {
		func = gameLocal.program.FindFunction(name);
	}
	if (!func) {
		idThread::ReturnString( "NIL" );
		return;
	}
	const idTypeDef *ftype = func->type;  
	// idVarDef* def = func->def;  
	idTypeDef* retType = ftype->ReturnType();  
	int arity = ftype->NumParameters();                  
	if (giveNames) {
		pbuffer =  "";
		pbuffer.Append("(");
	} else {
		pbuffer =  "";
	}
	if (retType) {
		pbuffer.Append(retType->Name());  
		pbuffer.Append(" ");  
	} else {
		pbuffer.Append("void ");
	}
	pbuffer.Append(func->Name());  
	pbuffer.Append(" ");  
	for (int p=0;p<arity;p++) {
		if (giveNames) {
			pbuffer.Append("(");  
			pbuffer.Append(ftype->GetParmType(p)->Name());  
			pbuffer.Append(" ");  
			pbuffer.Append(ftype->GetParmName(p));  
			pbuffer.Append(")");  
		} else {
			pbuffer.Append(ftype->GetParmType(p)->Name());  
			pbuffer.Append(" ");  
		}  
	}
	if (giveNames) {
		pbuffer.Append(") ");  
	}
	idThread::ReturnString( pbuffer.c_str() );
}
#endif

void Daxmoo_ProcessChatMessage( int clientNum, bool team, const char *name, const char *text, const char *sound ) {
#ifdef DAXMOO_SAY_EVENT
	//gameLocal.Printf("\n%s: \"%s\" sound = %s\n",name,text,sound);
	Daxmoo_CLR_Event_sayFrom(clientNum,team,name,text,sound);
#endif
}

void Daxmoo_Say(bool team,const char* name, const char* text) {
	if (gameLocal.isClient) {
		idBitMsg    outMsg;
		byte        msgBuf[ 256 ];
		outMsg.Init( msgBuf, sizeof( msgBuf ) );
		outMsg.WriteByte( team ? GAME_RELIABLE_MESSAGE_TCHAT : GAME_RELIABLE_MESSAGE_CHAT );
		outMsg.WriteString( name );
		outMsg.WriteString( text, -1, false );
		networkSystem->ClientSendReliableMessage( outMsg );
		Daxmoo_ProcessChatMessage( gameLocal.localClientNum, team, name, text, NULL );
	} else {
		gameLocal.mpGame.ProcessChatMessage( gameLocal.localClientNum, team, name, text, NULL );
	}
}


void idThread::Event_Dax_SayFrom(const char* from, const char* text) {
	Daxmoo_Say(false,from,text);
}

/* 
 ============ 
assertln 
 ============ 
*/ 
void assertln( const char *fmt, ... ) {
	va_list argptr; 
	char text[MAX_STRING_CHARS*4];
	va_start( argptr, fmt ); 
	idStr::vsnPrintf( text, sizeof(text),fmt, argptr ); 
	va_end( argptr ); 
	gameLocal.Printf("/*ASSERTLN %s */\n",text); 
//    if (text[0]!= '(' && text[0]!= '\n') {
	//  } else {
	//    gameLocalPrintfBoth("/* %s */\n",text); 
	// } 
} 

idThread            *idThread::currentThread = NULL;
int                 idThread::threadIndex = 0;
idList<idThread *>  idThread::threadList;
trace_t             idThread::trace;

/*
================
idThread::CurrentThread
================
*/
idThread *idThread::CurrentThread( void ) {
	return currentThread;
}

/*
================
idThread::CurrentThreadNum
================
*/
int idThread::CurrentThreadNum( void ) {
	if (currentThread) {
		return currentThread->GetThreadNum();
	} else {
		return 0;
	}
}

/*
================
idThread::BeginMultiFrameEvent
================
*/
bool idThread::BeginMultiFrameEvent( idEntity *ent, const idEventDef *event ) {
	if (!currentThread) {
		gameLocal.Error( "idThread::BeginMultiFrameEvent called without a current thread" );
	}
	return currentThread->interpreter.BeginMultiFrameEvent( ent, event );
}

/*
================
idThread::EndMultiFrameEvent
================
*/
void idThread::EndMultiFrameEvent( idEntity *ent, const idEventDef *event ) {
	if (!currentThread) {
		gameLocal.Error( "idThread::EndMultiFrameEvent called without a current thread" );
	}
	currentThread->interpreter.EndMultiFrameEvent( ent, event );
}

/*
================
idThread::idThread
================
*/
idThread::idThread() {
	Init();
	SetThreadName( va( "thread_%d", threadIndex ) );
	if (g_debugScript.GetBool()) {
		gameLocal.Printf( "%d: create thread (%d) '%s'\n", gameLocal.time, threadNum, threadName.c_str() );
	}
}

/*
================
idThread::idThread
================
*/
idThread::idThread( idEntity *self, const function_t *func ) {
	assert( self );

	Init();
	SetThreadName( self->name );
	interpreter.EnterObjectFunction( self, func, false );
	if (g_debugScript.GetBool()) {
		gameLocal.Printf( "%d: create thread (%d) '%s'\n", gameLocal.time, threadNum, threadName.c_str() );
	}
}

/*
================
idThread::idThread
================
*/
idThread::idThread( const function_t *func ) {
	assert( func );

	Init();
	SetThreadName( func->Name() );
	interpreter.EnterFunction( func, false );
	if (g_debugScript.GetBool()) {
		gameLocal.Printf( "%d: create thread (%d) '%s'\n", gameLocal.time, threadNum, threadName.c_str() );
	}
}

/*
================
idThread::idThread
================
*/
idThread::idThread( idInterpreter *source, const function_t *func, int args ) {
	Init();
	interpreter.ThreadCall( source, func, args );
	if (g_debugScript.GetBool()) {
		gameLocal.Printf( "%d: create thread (%d) '%s'\n", gameLocal.time, threadNum, threadName.c_str() );
	}
}

/*
================
idThread::idThread
================
*/
idThread::idThread( idInterpreter *source, idEntity *self, const function_t *func, int args ) {
	assert( self );

	Init();
	SetThreadName( self->name );
	interpreter.ThreadCall( source, func, args );
	if (g_debugScript.GetBool()) {
		gameLocal.Printf( "%d: create thread (%d) '%s'\n", gameLocal.time, threadNum, threadName.c_str() );
	}
}

/*
================
idThread::~idThread
================
*/
idThread::~idThread() {
	idThread    *thread;
	int         i;
	int         n;

	if (g_debugScript.GetBool()) {
		gameLocal.Printf( "%d: end thread (%d) '%s'\n", gameLocal.time, threadNum, threadName.c_str() );
	}
	threadList.Remove( this );
	n = threadList.Num();
	for (i = 0; i < n; i++) {
		thread = threadList[ i ];
		if (thread->WaitingOnThread() == this) {
			thread->ThreadCallback( this );
		}
	}

	if (currentThread == this) {
		currentThread = NULL;
	}
}

/*
================
idThread::ManualDelete
================
*/
void idThread::ManualDelete( void ) {
	interpreter.terminateOnExit = false;
}

/*
================
idThread::Save
================
*/
void idThread::Save( idSaveGame *savefile ) const {

	// We will check on restore that threadNum is still the same,
	//  threads should have been restored in the same order.
	savefile->WriteInt( threadNum );

	savefile->WriteObject( waitingForThread );
	savefile->WriteInt( waitingFor );
	savefile->WriteInt( waitingUntil );

	interpreter.Save( savefile );

	savefile->WriteDict( &spawnArgs );
	savefile->WriteString( threadName );

	savefile->WriteInt( lastExecuteTime );
	savefile->WriteInt( creationTime );

	savefile->WriteBool( manualControl );
}

/*
================
idThread::Restore
================
*/
void idThread::Restore( idRestoreGame *savefile ) {
	savefile->ReadInt( threadNum );

	savefile->ReadObject( reinterpret_cast<idClass *&>( waitingForThread ) );
	savefile->ReadInt( waitingFor );
	savefile->ReadInt( waitingUntil );

	interpreter.Restore( savefile );

	savefile->ReadDict( &spawnArgs );
	savefile->ReadString( threadName );

	savefile->ReadInt( lastExecuteTime );
	savefile->ReadInt( creationTime );

	savefile->ReadBool( manualControl );
}

/*
================
idThread::Init
================
*/
void idThread::Init( void ) {
	// create a unique threadNum
	do {
		threadIndex++;
		if (threadIndex == 0) {
			threadIndex = 1;
		}
	} while (GetThread( threadIndex ));

	threadNum = threadIndex;
	threadList.Append( this );

	creationTime = gameLocal.time;
	lastExecuteTime = 0;
	manualControl = false;

	ClearWaitFor();
#ifdef DAXMOO
	tcyc = NULL;
	#ifdef DAXMOO_THREADS
	sockThread = NULL;
	#endif
	sockResult = NULL;
#endif

	interpreter.SetThread( this );
}

/*
================
idThread::GetThread
================
*/
idThread *idThread::GetThread( int num ) {
	int         i;
	int         n;
	idThread    *thread;

	n = threadList.Num();
	for (i = 0; i < n; i++) {
		thread = threadList[ i ];
		if (thread->GetThreadNum() == num) {
			return thread;
		}
	}

	return NULL;
}

/*
================
idThread::DisplayInfo
================
*/
void idThread::DisplayInfo( void ) {
	gameLocal.Printf(
					"%12i: '%s'\n"
					"        File: %s(%d)\n"
					"     Created: %d (%d ms ago)\n"
					"      Status: ", 
					threadNum, threadName.c_str(), 
					interpreter.CurrentFile(), interpreter.CurrentLine(), 
					creationTime, gameLocal.time - creationTime );

	if (interpreter.threadDying) {
		gameLocal.Printf( "Dying\n" );
	} else if (interpreter.doneProcessing) {
		gameLocal.Printf(
						"Paused since %d (%d ms)\n"
						"      Reason: ",  lastExecuteTime, gameLocal.time - lastExecuteTime );
		if (waitingForThread) {
			gameLocal.Printf( "Waiting for thread #%3i '%s'\n", waitingForThread->GetThreadNum(), waitingForThread->GetThreadName() );
		} else if (( waitingFor != ENTITYNUM_NONE ) && ( gameLocal.entities[ waitingFor ] )) {
			gameLocal.Printf( "Waiting for entity #%3i '%s'\n", waitingFor, gameLocal.entities[ waitingFor ]->name.c_str() );
		} else if (waitingUntil) {
			gameLocal.Printf( "Waiting until %d (%d ms total wait time)\n", waitingUntil, waitingUntil - lastExecuteTime );
		} else {
			gameLocal.Printf( "None\n" );
		}
	} else {
		gameLocal.Printf( "Processing\n" );
	}

	interpreter.DisplayInfo();

	gameLocal.Printf( "\n" );
}

/*
================
idThread::ListThreads_f
================
*/
void idThread::ListThreads_f( const idCmdArgs &args ) {
	int i;
	int n;

	n = threadList.Num();
	for (i = 0; i < n; i++) {
		//threadList[ i ]->DisplayInfo();
		gameLocal.Printf( "%3i: %-20s : %s(%d)\n", threadList[ i ]->threadNum, threadList[ i ]->threadName.c_str(), threadList[ i ]->interpreter.CurrentFile(), threadList[ i ]->interpreter.CurrentLine() );
	}
	gameLocal.Printf( "%d active threads\n\n", n );
}

/*
================
idThread::Restart
================
*/
void idThread::Restart( void ) {
	int i;
	int n;

	// reset the threadIndex
	threadIndex = 0;

	currentThread = NULL;
	n = threadList.Num();
	for (i = n - 1; i >= 0; i--) {
		delete threadList[ i ];
	}
	threadList.Clear();

	memset( &trace, 0, sizeof( trace ) );
	trace.c.entityNum = ENTITYNUM_NONE;
}

/*
================
idThread::DelayedStart
================
*/
void idThread::DelayedStart( int delay ) {
	CancelEvents( &EV_Thread_Execute );
	if (gameLocal.time <= 0) {
		delay++;
	}
	PostEventMS( &EV_Thread_Execute, delay );
}

/*
================
idThread::Start
================
*/
bool idThread::Start( void ) {
	bool result;

	CancelEvents( &EV_Thread_Execute );
	result = Execute();

	return result;
}

/*
================
idThread::SetThreadName
================
*/
void idThread::SetThreadName( const char *name ) {
	threadName = name;
}

/*
================
idThread::ObjectMoveDone
================
*/
void idThread::ObjectMoveDone( int threadnum, idEntity *obj ) {
	idThread *thread;

	if (!threadnum) {
		return;
	}

	thread = GetThread( threadnum );
	if (thread) {
		thread->ObjectMoveDone( obj );
	}
}

/*
================
idThread::End
================
*/
void idThread::End( void ) {
	// Tell thread to die.  It will exit on its own.
	Pause();
	interpreter.threadDying = true;
}

/*
================
idThread::KillThread
================
*/
void idThread::KillThread( const char *name ) {
	int         i;
	int         num;
	int         len;
	const char  *ptr;
	idThread    *thread;

	// see if the name uses a wild card
	ptr = strchr( name, '*' );
	if (ptr) {
		len = ptr - name;
	} else {
		len = strlen( name );
	}

	// kill only those threads whose name matches name
	num = threadList.Num();
	for (i = 0; i < num; i++) {
		thread = threadList[ i ];
		if (!idStr::Cmpn( thread->GetThreadName(), name, len )) {
			thread->End();
		}
	}
}

/*
================
idThread::KillThread
================
*/
void idThread::KillThread( int num ) {
	idThread *thread;

	thread = GetThread( num );
	if (thread) {
		// Tell thread to die.  It will delete itself on it's own.
		thread->End();
	}
}

/*
================
idThread::Execute
================
*/
bool idThread::Execute( void ) {
	idThread    *oldThread;
	bool        done;

	if (manualControl && ( waitingUntil > gameLocal.time )) {
		return false;
	}

	oldThread = currentThread;
	currentThread = this;

	lastExecuteTime = gameLocal.time;
	ClearWaitFor();
	done = interpreter.Execute();
	if (done) {
		End();
		if (interpreter.terminateOnExit) {
			PostEventMS( &EV_Remove, 0 );
		}
	} else if (!manualControl) {
		if (waitingUntil > lastExecuteTime) {
			PostEventMS( &EV_Thread_Execute, waitingUntil - lastExecuteTime );
		} else if (interpreter.MultiFrameEventInProgress()) {
			PostEventMS( &EV_Thread_Execute, gameLocal.msec );
		}
	}

	currentThread = oldThread;

	return done;
}

/*
================
idThread::IsWaiting

Checks if thread is still waiting for some event to occur.
================
*/
bool idThread::IsWaiting( void ) {
	if (waitingForThread || ( waitingFor != ENTITYNUM_NONE )) {
		return true;
	}

	if (waitingUntil && ( waitingUntil > gameLocal.time )) {
		return true;
	}

	return false;
}

/*
================
idThread::CallFunction

NOTE: If this is called from within a event called by this thread, the function arguments will be invalid after calling this function.
================
*/
void idThread::CallFunction( const function_t *func, bool clearStack ) {
	ClearWaitFor();
	interpreter.EnterFunction( func, clearStack );
}

/*
================
idThread::CallFunction

NOTE: If this is called from within a event called by this thread, the function arguments will be invalid after calling this function.
================
*/
void idThread::CallFunction( idEntity *self, const function_t *func, bool clearStack ) {
	assert( self );
	ClearWaitFor();
	interpreter.EnterObjectFunction( self, func, clearStack );
}

/*
================
idThread::ClearWaitFor
================
*/
void idThread::ClearWaitFor( void ) {
	waitingFor          = ENTITYNUM_NONE;
	waitingForThread    = NULL;
	waitingUntil        = 0;
}

/*
================
idThread::IsWaitingFor
================
*/
bool idThread::IsWaitingFor( idEntity *obj ) {
	assert( obj );
	return waitingFor == obj->entityNumber;
}

/*
================
idThread::ObjectMoveDone
================
*/
void idThread::ObjectMoveDone( idEntity *obj ) {
	assert( obj );

	if (IsWaitingFor( obj )) {
		ClearWaitFor();
		DelayedStart( 0 );
	}
}

/*
================
idThread::ThreadCallback
================
*/
void idThread::ThreadCallback( idThread *thread ) {
	if (interpreter.threadDying) {
		return;
	}

	if (thread == waitingForThread) {
		ClearWaitFor();
		DelayedStart( 0 );
	}
}

/*
================
idThread::Event_SetThreadName
================
*/
void idThread::Event_SetThreadName( const char *name ) {
	SetThreadName( name );
}

/*
================
idThread::Error
================
*/
void idThread::Error( const char *fmt, ... ) const {
	va_list argptr;
	char    text[ 1024 ];

	va_start( argptr, fmt );
	vsprintf( text, fmt, argptr );
	va_end( argptr );

	interpreter.Error( text );
}

/*
================
idThread::Warning
================
*/
void idThread::Warning( const char *fmt, ... ) const {
	va_list argptr;
	char    text[ 1024 ];

	va_start( argptr, fmt );
	vsprintf( text, fmt, argptr );
	va_end( argptr );

	interpreter.Warning( text );
}

/*
================
idThread::ReturnString
================
*/
void idThread::ReturnString( const char *text ) {
	gameLocal.program.ReturnString( text );
}

/*
================
idThread::ReturnFloat
================
*/
void idThread::ReturnFloat( float value ) {
	gameLocal.program.ReturnFloat( value );
}

/*
================
idThread::ReturnInt
================
*/
void idThread::ReturnInt( int value ) {
	// true integers aren't supported in the compiler,
	// so int values are stored as floats
	gameLocal.program.ReturnFloat( value );
}

/*
================
idThread::ReturnVector
================
*/
void idThread::ReturnVector( idVec3 const &vec ) {
	gameLocal.program.ReturnVector( vec );
}

/*
================
idThread::ReturnEntity
================
*/
void idThread::ReturnEntity( idEntity *ent ) {
	gameLocal.program.ReturnEntity( ent );
}

/*
================
idThread::Event_Execute
================
*/
void idThread::Event_Execute( void ) {
	Execute();
}

/*
================
idThread::Pause
================
*/
void idThread::Pause( void ) {
	ClearWaitFor();
	interpreter.doneProcessing = true;
}

/*
================
idThread::WaitMS
================
*/
void idThread::WaitMS( int time ) {
	Pause();
	waitingUntil = gameLocal.time + time;
}

/*
================
idThread::WaitSec
================
*/
void idThread::WaitSec( float time ) {
	WaitMS( SEC2MS( time ) );
}

/*
================
idThread::WaitFrame
================
*/
void idThread::WaitFrame( void ) {
	Pause();

	// manual control threads don't set waitingUntil so that they can be run again
	// that frame if necessary.
	if (!manualControl) {
		waitingUntil = gameLocal.time + gameLocal.msec;
	}
}

/***********************************************************************

  Script callable events  
	
***********************************************************************/

/*
================
idThread::Event_TerminateThread
================
*/
void idThread::Event_TerminateThread( int num ) {
	idThread *thread;

	thread = GetThread( num );
	KillThread( num );
}

/*
================
idThread::Event_Pause
================
*/
void idThread::Event_Pause( void ) {
	Pause();
}

/*
================
idThread::Event_Wait
================
*/
void idThread::Event_Wait( float time ) {
	WaitSec( time );
}

/*
================
idThread::Event_WaitFrame
================
*/
void idThread::Event_WaitFrame( void ) {
	WaitFrame();
}

/*
================
idThread::Event_WaitFor
================
*/
void idThread::Event_WaitFor( idEntity *ent ) {
	if (ent && ent->RespondsTo( EV_Thread_SetCallback )) {
		ent->ProcessEvent( &EV_Thread_SetCallback );
		if (gameLocal.program.GetReturnedInteger()) {
			Pause();
			waitingFor = ent->entityNumber;
		}
	}
}

/*
================
idThread::Event_WaitForThread
================
*/
void idThread::Event_WaitForThread( int num ) {
	idThread *thread;

	thread = GetThread( num );
	if (!thread) {
		if (g_debugScript.GetBool()) {
			// just print a warning and continue executing
			Warning( "Thread %d not running", num );
		}
	} else {
		Pause();
		waitingForThread = thread;
	}
}

/*
================
idThread::Event_Print
================
*/
void idThread::Event_Print( const char *text ) {
	gameLocal.Printf( "%s", text );
}

/*
================
idThread::Event_PrintLn
================
*/
void idThread::Event_PrintLn( const char *text ) {
	gameLocal.Printf( "%s\n", text );
}

/*
================
idThread::Event_Say
================
*/
void idThread::Event_Say( const char *text ) {
	cmdSystem->BufferCommandText( CMD_EXEC_NOW, va( "say \"%s\"", text ) );
}

/*
================
idThread::Event_Assert
================
*/
void idThread::Event_Assert( float value ) {
	assert( value );
}

/*
================
idThread::Event_Trigger
================
*/
void idThread::Event_Trigger( idEntity *ent ) {
	if (ent) {
		ent->Signal( SIG_TRIGGER );
		ent->ProcessEvent( &EV_Activate, gameLocal.GetLocalPlayer() );
		ent->TriggerGuis();
	}
}

/*
================
idThread::Event_SetCvar
================
*/
void idThread::Event_SetCvar( const char *name, const char *value ) const {
	cvarSystem->SetCVarString( name, value );
}

/*
================
idThread::Event_GetCvar
================
*/
void idThread::Event_GetCvar( const char *name ) const {
	ReturnString( cvarSystem->GetCVarString( name ) );
}

/*
================
idThread::Event_Random
================
*/
void idThread::Event_Random( float range ) const {
	float result;

	result = gameLocal.random.RandomFloat();
	ReturnFloat( range * result );
}

#if defined(_D3XP) || defined(D3V_D3XP)

void idThread::Event_RandomInt( int range ) const {
	int result;
	result = gameLocal.random.RandomInt(range);
	ReturnFloat(result);
}

#endif

/*
================
idThread::Event_GetTime
================
*/
void idThread::Event_GetTime( void ) {
	ReturnFloat( MS2SEC( gameLocal.realClientTime ) );
}

/*
================
idThread::Event_KillThread
================
*/
void idThread::Event_KillThread( const char *name ) {
	KillThread( name );
}

/*
================
idThread::Event_GetEntity
================
*/
void idThread::Event_GetEntity( const char *name ) {
	int         entnum;
	idEntity    *ent;

	assert( name );

	if (name[ 0 ] == '*') {
		entnum = atoi( &name[ 1 ] );
		if (( entnum < 0 ) || ( entnum >= MAX_GENTITIES )) {
			Error( "Entity number in string out of range." );
		}
		ReturnEntity( gameLocal.entities[ entnum ] );
	} else {
		ent = gameLocal.FindEntity( name );
		ReturnEntity( ent );
	}
}

/*
================
idThread::Event_Spawn
================
*/
void idThread::Event_Spawn( const char *classname ) {
	idEntity *ent;

	spawnArgs.Set( "classname", classname );
	gameLocal.SpawnEntityDef( spawnArgs, &ent );
	ReturnEntity( ent );
	spawnArgs.Clear();
}

/*
================
idThread::Event_CopySpawnArgs
================
*/
void idThread::Event_CopySpawnArgs( idEntity *ent ) {
	spawnArgs.Copy( ent->spawnArgs );
}

/*
================
idThread::Event_SetSpawnArg
================
*/
void idThread::Event_SetSpawnArg( const char *key, const char *value ) {
	spawnArgs.Set( key, value );
}

/*
================
idThread::Event_SpawnString
================
*/
void idThread::Event_SpawnString( const char *key, const char *defaultvalue ) {
	const char *result;

	spawnArgs.GetString( key, defaultvalue, &result );
	ReturnString( result );
}

/*
================
idThread::Event_SpawnFloat
================
*/
void idThread::Event_SpawnFloat( const char *key, float defaultvalue ) {
	float result;

	spawnArgs.GetFloat( key, va( "%f", defaultvalue ), result );
	ReturnFloat( result );
}

/*
================
idThread::Event_SpawnVector
================
*/
void idThread::Event_SpawnVector( const char *key, idVec3 &defaultvalue ) {
	idVec3 result;

	spawnArgs.GetVector( key, va( "%f %f %f", defaultvalue.x, defaultvalue.y, defaultvalue.z ), result );
	ReturnVector( result );
}

/*
================
idThread::Event_ClearPersistantArgs
================
*/
void idThread::Event_ClearPersistantArgs( void ) {
	gameLocal.persistentLevelInfo.Clear();
}


/*
================
idThread::Event_SetPersistantArg
================
*/
void idThread::Event_SetPersistantArg( const char *key, const char *value ) {
	gameLocal.persistentLevelInfo.Set( key, value );
}

/*
================
idThread::Event_GetPersistantString
================
*/
void idThread::Event_GetPersistantString( const char *key ) {
	const char *result;

	gameLocal.persistentLevelInfo.GetString( key, "", &result );
	ReturnString( result );
}

/*
================
idThread::Event_GetPersistantFloat
================
*/
void idThread::Event_GetPersistantFloat( const char *key ) {
	float result;

	gameLocal.persistentLevelInfo.GetFloat( key, "0", result );
	ReturnFloat( result );
}

/*
================
idThread::Event_GetPersistantVector
================
*/
void idThread::Event_GetPersistantVector( const char *key ) {
	idVec3 result;

	gameLocal.persistentLevelInfo.GetVector( key, "0 0 0", result );
	ReturnVector( result );
}

/*
================
idThread::Event_AngToForward
================
*/
void idThread::Event_AngToForward( idAngles &ang ) {
	ReturnVector( ang.ToForward() );
}

/*
================
idThread::Event_AngToRight
================
*/
void idThread::Event_AngToRight( idAngles &ang ) {
	idVec3 vec;

	ang.ToVectors( NULL, &vec );
	ReturnVector( vec );
}

/*
================
idThread::Event_AngToUp
================
*/
void idThread::Event_AngToUp( idAngles &ang ) {
	idVec3 vec;

	ang.ToVectors( NULL, NULL, &vec );
	ReturnVector( vec );
}

/*
================
idThread::Event_GetSine
================
*/
void idThread::Event_GetSine( float angle ) {
	ReturnFloat( idMath::Sin( DEG2RAD( angle ) ) );
}

/*
================
idThread::Event_GetCosine
================
*/
void idThread::Event_GetCosine( float angle ) {
	ReturnFloat( idMath::Cos( DEG2RAD( angle ) ) );
}

#if defined(_D3XP) || defined(D3V_D3XP)
/*
================
idThread::Event_GetArcSine
================
*/
void idThread::Event_GetArcSine( float a ) {
	ReturnFloat(RAD2DEG(idMath::ASin(a)));
}

/*
================
idThread::Event_GetArcCosine
================
*/
void idThread::Event_GetArcCosine( float a ) {
	ReturnFloat(RAD2DEG(idMath::ACos(a)));
}
#endif

/*
================
idThread::Event_GetSquareRoot
================
*/
void idThread::Event_GetSquareRoot( float theSquare ) {
	ReturnFloat( idMath::Sqrt( theSquare ) );
}

/*
================
idThread::Event_VecNormalize
================
*/
void idThread::Event_VecNormalize( idVec3 &vec ) {
	idVec3 n;

	n = vec;
	n.Normalize();
	ReturnVector( n );
}

/*
================
idThread::Event_VecLength
================
*/
void idThread::Event_VecLength( idVec3 &vec ) {
	ReturnFloat( vec.Length() );
}

/*
================
idThread::Event_VecDotProduct
================
*/
void idThread::Event_VecDotProduct( idVec3 &vec1, idVec3 &vec2 ) {
	ReturnFloat( vec1 * vec2 );
}

/*
================
idThread::Event_VecCrossProduct
================
*/
void idThread::Event_VecCrossProduct( idVec3 &vec1, idVec3 &vec2 ) {
	ReturnVector( vec1.Cross( vec2 ) );
}

/*
================
idThread::Event_VecToAngles
================
*/
void idThread::Event_VecToAngles( idVec3 &vec ) {
	idAngles ang = vec.ToAngles();
	ReturnVector( idVec3( ang[0], ang[1], ang[2] ) );
}

#if defined(_D3XP) || defined(D3V_D3XP)
/*
================
idThread::Event_VecToOrthoBasisAngles
================
*/
void idThread::Event_VecToOrthoBasisAngles( idVec3 &vec ) {
	idVec3 left, up;
	idAngles ang;

	vec.OrthogonalBasis( left, up );
	idMat3 axis( left, up, vec );

	ang = axis.ToAngles();

	ReturnVector( idVec3( ang[0], ang[1], ang[2] ) );
}

void idThread::Event_RotateVector( idVec3 &vec, idVec3 &ang ) {

	idAngles tempAng(ang);
	idMat3 axis = tempAng.ToMat3();
	idVec3 ret = vec * axis;
	ReturnVector(ret);

}
#endif

/*
================
idThread::Event_OnSignal
================
*/
void idThread::Event_OnSignal( int signal, idEntity *ent, const char *func ) {
	const function_t *function;

	assert( func );

	if (!ent) {
		Error( "Entity not found" );
	}

	if (( signal < 0 ) || ( signal >= NUM_SIGNALS )) {
		Error( "Signal out of range" );
	}

	function = gameLocal.program.FindFunction( func );
	if (!function) {
		Error( "Function '%s' not found", func );
	}

	ent->SetSignal( ( signalNum_t )signal, this, function );
}

/*
================
idThread::Event_ClearSignalThread
================
*/
void idThread::Event_ClearSignalThread( int signal, idEntity *ent ) {
	if (!ent) {
		Error( "Entity not found" );
	}

	if (( signal < 0 ) || ( signal >= NUM_SIGNALS )) {
		Error( "Signal out of range" );
	}

	ent->ClearSignalThread( ( signalNum_t )signal, this );
}

/*
================
idThread::Event_SetCamera
================
*/
void idThread::Event_SetCamera( idEntity *ent ) {
	if (!ent) {
		Error( "Entity not found" );
		return;
	}

	if (!ent->IsType( idCamera::Type )) {
		Error( "Entity is not a camera" );
		return;
	}

	gameLocal.SetCamera( ( idCamera * )ent );
}

/*
================
idThread::Event_FirstPerson
================
*/
void idThread::Event_FirstPerson( void ) {
	gameLocal.SetCamera( NULL );
}

/*
================
idThread::Event_Trace
================
*/
void idThread::Event_Trace( const idVec3 &start, const idVec3 &end, const idVec3 &mins, const idVec3 &maxs, int contents_mask, idEntity *passEntity ) {
	if (mins == vec3_origin && maxs == vec3_origin) {
		gameLocal.clip.TracePoint( trace, start, end, contents_mask, passEntity );
	} else {
		gameLocal.clip.TraceBounds( trace, start, end, idBounds( mins, maxs ), contents_mask, passEntity );
	}
	ReturnFloat( trace.fraction );
}

/*
================
idThread::Event_TracePoint
================
*/
void idThread::Event_TracePoint( const idVec3 &start, const idVec3 &end, int contents_mask, idEntity *passEntity ) {
	gameLocal.clip.TracePoint( trace, start, end, contents_mask, passEntity );
	ReturnFloat( trace.fraction );
}

/*
================
idThread::Event_GetTraceFraction
================
*/
void idThread::Event_GetTraceFraction( void ) {
	ReturnFloat( trace.fraction );
}

/*
================
idThread::Event_GetTraceEndPos
================
*/
void idThread::Event_GetTraceEndPos( void ) {
	ReturnVector( trace.endpos );
}

/*
================
idThread::Event_GetTraceNormal
================
*/
void idThread::Event_GetTraceNormal( void ) {
	if (trace.fraction < 1.0f) {
		ReturnVector( trace.c.normal );
	} else {
		ReturnVector( vec3_origin );
	}
}

/*
================
idThread::Event_GetTraceEntity
================
*/
void idThread::Event_GetTraceEntity( void ) {
	if (trace.fraction < 1.0f) {
		ReturnEntity( gameLocal.entities[ trace.c.entityNum ] );
	} else {
		ReturnEntity( ( idEntity * )NULL );
	}
}

/*
================
idThread::Event_GetTraceJoint
================
*/
void idThread::Event_GetTraceJoint( void ) {
	if (trace.fraction < 1.0f && trace.c.id < 0) {
		idAFEntity_Base *af = static_cast<idAFEntity_Base *>( gameLocal.entities[ trace.c.entityNum ] );
		if (af && af->IsType( idAFEntity_Base::Type ) && af->IsActiveAF()) {
			ReturnString( af->GetAnimator()->GetJointName( CLIPMODEL_ID_TO_JOINT_HANDLE( trace.c.id ) ) );
			return;
		}
	}
	ReturnString( "" );
}

/*
================
idThread::Event_GetTraceBody
================
*/
void idThread::Event_GetTraceBody( void ) {
	if (trace.fraction < 1.0f && trace.c.id < 0) {
		idAFEntity_Base *af = static_cast<idAFEntity_Base *>( gameLocal.entities[ trace.c.entityNum ] );
		if (af && af->IsType( idAFEntity_Base::Type ) && af->IsActiveAF()) {
			int bodyId = af->BodyForClipModelId( trace.c.id );
			idAFBody *body = af->GetAFPhysics()->GetBody( bodyId );
			if (body) {
				ReturnString( body->GetName() );
				return;
			}
		}
	}
	ReturnString( "" );
}

/*
================
idThread::Event_FadeIn
================
*/
void idThread::Event_FadeIn( idVec3 &color, float time ) {
	idVec4      fadeColor;
	idPlayer    *player;

	player = gameLocal.GetLocalPlayer();
	if (player) {
		fadeColor.Set( color[ 0 ], color[ 1 ], color[ 2 ], 0.0f );
		player->playerView.Fade(fadeColor, SEC2MS( time ) );
	}
}

/*
================
idThread::Event_FadeOut
================
*/
void idThread::Event_FadeOut( idVec3 &color, float time ) {
	idVec4      fadeColor;
	idPlayer    *player;

	player = gameLocal.GetLocalPlayer();
	if (player) {
		fadeColor.Set( color[ 0 ], color[ 1 ], color[ 2 ], 1.0f );
		player->playerView.Fade(fadeColor, SEC2MS( time ) );
	}
}

/*
================
idThread::Event_FadeTo
================
*/
void idThread::Event_FadeTo( idVec3 &color, float alpha, float time ) {
	idVec4      fadeColor;
	idPlayer    *player;

	player = gameLocal.GetLocalPlayer();
	if (player) {
		fadeColor.Set( color[ 0 ], color[ 1 ], color[ 2 ], alpha );
		player->playerView.Fade(fadeColor, SEC2MS( time ) );
	}
}

/*
================
idThread::Event_SetShaderParm
================
*/
void idThread::Event_SetShaderParm( int parmnum, float value ) {
	if (( parmnum < 0 ) || ( parmnum >= MAX_GLOBAL_SHADER_PARMS )) {
		Error( "shader parm index (%d) out of range", parmnum );
	}

	gameLocal.globalShaderParms[ parmnum ] = value;
}

/*
================
idThread::Event_StartMusic
================
*/
void idThread::Event_StartMusic( const char *text ) {
	gameSoundWorld->PlayShaderDirectly( text );
}

/*
================
idThread::Event_Warning
================
*/
void idThread::Event_Warning( const char *text ) {
	Warning( "%s", text );
}

/*
================
idThread::Event_Error
================
*/
void idThread::Event_Error( const char *text ) {
	Error( "%s", text );
}

/*
================
idThread::Event_StrLen
================
*/
void idThread::Event_StrLen( const char *string ) {
	int len;

	len = strlen( string );
	idThread::ReturnInt( len );
}

/*
================
idThread::Event_StrLeft
================
*/
void idThread::Event_StrLeft( const char *string, int num ) {
	int len;

	if (num < 0) {
		idThread::ReturnString( "" );
		return;
	}

	len = strlen( string );
	if (len < num) {
		idThread::ReturnString( string );
		return;
	}

	idStr result( string, 0, num );
	idThread::ReturnString( result );
}

/*
================
idThread::Event_StrRight 
================
*/
void idThread::Event_StrRight( const char *string, int num ) {
	int len;

	if (num < 0) {
		idThread::ReturnString( "" );
		return;
	}

	len = strlen( string );
	if (len < num) {
		idThread::ReturnString( string );
		return;
	}

	idThread::ReturnString( string + len - num );
}

/*
================
idThread::Event_StrSkip
================
*/
void idThread::Event_StrSkip( const char *string, int num ) {
	int len;

	if (num < 0) {
		idThread::ReturnString( string );
		return;
	}

	len = strlen( string );
	if (len < num) {
		idThread::ReturnString( "" );
		return;
	}

	idThread::ReturnString( string + num );
}

/*
================
idThread::Event_StrMid
================
*/
void idThread::Event_StrMid( const char *string, int start, int num ) {
	int len;

	if (num < 0) {
		idThread::ReturnString( "" );
		return;
	}

	if (start < 0) {
		start = 0;
	}
	len = strlen( string );
	if (start > len) {
		start = len;
	}

	if (start + num > len) {
		num = len - start;
	}

	idStr result( string, start, start + num );
	idThread::ReturnString( result );
}

/*
================
idThread::Event_StrToFloat( const char *string )
================
*/
void idThread::Event_StrToFloat( const char *string ) {
	float result;

	result = atof( string );
	idThread::ReturnFloat( result );
}

/*
================
idThread::Event_RadiusDamage
================
*/
void idThread::Event_RadiusDamage( const idVec3 &origin, idEntity *inflictor, idEntity *attacker, idEntity *ignore, const char *damageDefName, float dmgPower ) {
	gameLocal.RadiusDamage( origin, inflictor, attacker, ignore, ignore, damageDefName, dmgPower );
}

/*
================
idThread::Event_IsClient
================
*/
void idThread::Event_IsClient( void ) {
	idThread::ReturnFloat( gameLocal.isClient );
}

/*
================
idThread::Event_IsMultiplayer
================
*/
void idThread::Event_IsMultiplayer( void ) {
	idThread::ReturnFloat( gameLocal.isMultiplayer );
}

/*
================
idThread::Event_GetFrameTime
================
*/
void idThread::Event_GetFrameTime( void ) {
	idThread::ReturnFloat( MS2SEC( gameLocal.msec ) );
}

/*
================
idThread::Event_GetTicsPerSecond
================
*/
void idThread::Event_GetTicsPerSecond( void ) {
	idThread::ReturnFloat( USERCMD_HZ );
}

/*
================
idThread::Event_CacheSoundShader
================
*/
void idThread::Event_CacheSoundShader( const char *soundName ) {
	declManager->FindSound( soundName );
}

/*
================
idThread::Event_DebugLine
================
*/
void idThread::Event_DebugLine( const idVec3 &color, const idVec3 &start, const idVec3 &end, const float lifetime ) {
	gameRenderWorld->DebugLine( idVec4( color.x, color.y, color.z, 0.0f ), start, end, SEC2MS( lifetime ) );
}

/*
================
idThread::Event_DebugArrow
================
*/
void idThread::Event_DebugArrow( const idVec3 &color, const idVec3 &start, const idVec3 &end, const int size, const float lifetime ) {
	gameRenderWorld->DebugArrow( idVec4( color.x, color.y, color.z, 0.0f ), start, end, size, SEC2MS( lifetime ) );
}

/*
================
idThread::Event_DebugCircle
================
*/
void idThread::Event_DebugCircle( const idVec3 &color, const idVec3 &origin, const idVec3 &dir, const float radius, const int numSteps, const float lifetime ) {
	gameRenderWorld->DebugCircle( idVec4( color.x, color.y, color.z, 0.0f ), origin, dir, radius, numSteps, SEC2MS( lifetime ) );
}

/*
================
idThread::Event_DebugBounds
================
*/
void idThread::Event_DebugBounds( const idVec3 &color, const idVec3 &mins, const idVec3 &maxs, const float lifetime ) {
	gameRenderWorld->DebugBounds( idVec4( color.x, color.y, color.z, 0.0f ), idBounds( mins, maxs ), vec3_origin, SEC2MS( lifetime ) );
}

/*
================
idThread::Event_DrawText
================
*/
void idThread::Event_DrawText( const char *text, const idVec3 &origin, float scale, const idVec3 &color, const int align, const float lifetime ) {
	gameRenderWorld->DrawText( text, origin, scale, idVec4( color.x, color.y, color.z, 0.0f ), gameLocal.GetLocalPlayer()->viewAngles.ToMat3(), align, SEC2MS( lifetime ) );
}

/*
================
idThread::Event_InfluenceActive
================
*/
void idThread::Event_InfluenceActive( void ) {
	idPlayer *player;

	player = gameLocal.GetLocalPlayer();
	if (player && player->GetInfluenceLevel()) {
		idThread::ReturnInt( true );
	} else {
		idThread::ReturnInt( false );
	}
}

