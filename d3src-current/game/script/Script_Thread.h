// Copyright (C) 2004 Id Software, Inc.
//
#ifdef DAXMOO
	#include "../../daxmoo/CycAccess.h"
#endif

#ifndef __SCRIPT_THREAD_H__
	#define __SCRIPT_THREAD_H__

extern const idEventDef EV_Thread_Execute;
extern const idEventDef EV_Thread_SetCallback;
extern const idEventDef EV_Thread_TerminateThread;
extern const idEventDef EV_Thread_Pause;
extern const idEventDef EV_Thread_Wait;
extern const idEventDef EV_Thread_WaitFrame;
extern const idEventDef EV_Thread_WaitFor;
extern const idEventDef EV_Thread_WaitForThread;
extern const idEventDef EV_Thread_Print;
extern const idEventDef EV_Thread_PrintLn;
extern const idEventDef EV_Thread_Say;
extern const idEventDef EV_Thread_Assert;
extern const idEventDef EV_Thread_Trigger;
extern const idEventDef EV_Thread_SetCvar;
extern const idEventDef EV_Thread_GetCvar;
extern const idEventDef EV_Thread_Random;
extern const idEventDef EV_Thread_GetTime;
extern const idEventDef EV_Thread_KillThread;
extern const idEventDef EV_Thread_SetThreadName;
extern const idEventDef EV_Thread_GetEntity;
extern const idEventDef EV_Thread_Spawn;
extern const idEventDef EV_Thread_SetSpawnArg;
extern const idEventDef EV_Thread_SpawnString;
extern const idEventDef EV_Thread_SpawnFloat;
extern const idEventDef EV_Thread_SpawnVector;
extern const idEventDef EV_Thread_AngToForward;
extern const idEventDef EV_Thread_AngToRight;
extern const idEventDef EV_Thread_AngToUp;
extern const idEventDef EV_Thread_Sine;
extern const idEventDef EV_Thread_Cosine;
extern const idEventDef EV_Thread_Normalize;
extern const idEventDef EV_Thread_VecLength;
extern const idEventDef EV_Thread_VecDotProduct;
extern const idEventDef EV_Thread_VecCrossProduct;
extern const idEventDef EV_Thread_OnSignal;
extern const idEventDef EV_Thread_ClearSignal;
extern const idEventDef EV_Thread_SetCamera;
extern const idEventDef EV_Thread_FirstPerson;
extern const idEventDef EV_Thread_TraceFraction;
extern const idEventDef EV_Thread_TracePos;
extern const idEventDef EV_Thread_FadeIn;
extern const idEventDef EV_Thread_FadeOut;
extern const idEventDef EV_Thread_FadeTo;
extern const idEventDef EV_Thread_Restart;

class idThread : public idClass {
private:
	#ifdef DAXMOO
	CycAccess*                  tcyc;
		#ifdef DAXMOO_THREADS
	HANDLE                      sockThread;
	unsigned                    threadId;
		#endif	
	unsigned __stdcall          WaitForSocket(void* params);
	idStr                       sockResult;
	void                        Event_Dax_setSockServer(int reuse,const char* cmd,int port);
	void                        Event_Dax_setSockClient(int reuse,const char* host,int port);
	void                        Event_Dax_evalSockClient(int reuse,const char* msg );

	void                        Event_Dax_udpPrint(const char* host,int port,const char* msg );
	void                        Event_Dax_rconEval( const char *string, int num );
	void                        Event_Dax_toInstance( const char *spawnclass,const char *classname);
	void                        Event_Dax_toVector( const char *string);
	void                        Event_Dax_toVector3( float,float,float);
	void                        Event_Dax_scriptString(idEntity *ent,const char *callstring,const char *scriptstring);	 //scriptString
	void                        Event_Dax_toClassname( idEntity *ent);
	void                        Event_Dax_getThreadnum();
	void                        Event_Dax_getThreadname();

	void                    Event_GetNextKey( const char *prefix, const char *lastMatch );
	void                        Event_Dax_clearSpawnArgs(const char* match);
	void                        Event_Dax_copyToClass(int type,const char* classname) ;
	void                        Event_Dax_copyFromClass(int type,const char* classname) ;
	void                        Event_Dax_copyMissingFromClass(int type,const char* classname);
	void                        Event_Dax_classBetter(const char* type,const char* classname);
	void                        Event_Dax_spawnclassBetter(const char* type,const char* classname);
	void                        Event_Dax_createInstance(const char *name,const char *classname) ;
	void                        Event_Dax_createClass(int type,const char* classname) ;


	void                        Event_Dax_setClassSpawnArg(const char* classname,const char* key,const char* dvalue);
	void                        Event_Dax_getClassSpawnArg(const char* classname,const char* key,const char* dvalue);
	void                        Event_Dax_clearClassSpawnArgs(const char* classname,const char* prefix);


	void         Event_Dax_findEntityByDef( const char *string, int num );
	void         Event_Dax_Str_Filter( const char *filter, const char *name, int caseSense );


	void         Event_Dax_toFunction( idEntity *ent , const char *funstring, int showNames );
	void         Event_Dax_invokeScript(idEntity* scope,const char* fnname,const char* theargs); 
	void         Event_Dax_retInt(); 
	void         Event_Dax_retFloat(); 
	void         Event_Dax_retVector(); 
	void         Event_Dax_retString(); 
	void         Event_Dax_retEntity(); 
	void         Event_Dax_SayFrom(const char *who,const char *what); 


		#ifndef EVDEFNAME
			#define EVDEFNAME(obj,prefixr,namer) void Event_##prefixr##namer
		#endif
		#define bool32 int
		#define idUI32 int

	EVDEFNAME(idThread,gui,Check)( const char* a );
	EVDEFNAME(idThread,gui,Find)( const char* a , bool32 b , bool32 c, bool32 d);
	EVDEFNAME(idThread,gui,Name)(idUI32 ui);
	EVDEFNAME(idThread,gui,Comment)(idUI32 ui);
	EVDEFNAME(idThread,gui,IsInteractive)(idUI32 ui);
	EVDEFNAME(idThread,gui,IsUniqued)(idUI32 ui);
	EVDEFNAME(idThread,gui,SetUniqued)(idUI32 ui,bool32 a);
	EVDEFNAME(idThread,gui,InitFromFile)(idUI32 ui,const char* a,bool32 b,bool32 c);
	EVDEFNAME(idThread,gui,HandleNamedEvent)(idUI32 ui,  const char* a);
	EVDEFNAME(idThread,gui,Redraw)(idUI32 ui, int time);
	EVDEFNAME(idThread,gui,DrawCursor)(idUI32 ui);
	EVDEFNAME(idThread,gui,State)(idUI32 ui );
	EVDEFNAME(idThread,gui,DeleteStateVar)(idUI32 ui,  const char* a );
	EVDEFNAME(idThread,gui,SetStateString)(idUI32 ui,  const char* a, const char* b );
	EVDEFNAME(idThread,gui,SetStateInt)(idUI32 ui,  const char* a,int b );
	EVDEFNAME(idThread,gui,SetStateFloat)(idUI32 ui, const char* a,float b );
	EVDEFNAME(idThread,gui,SetStateBool)(idUI32 ui,  const char* a,bool32 b );
	EVDEFNAME(idThread,gui,GetStateString)(idUI32 ui, const char* a );
	EVDEFNAME(idThread,gui,GetStateInt)(idUI32 ui, const char* a );
	EVDEFNAME(idThread,gui,GetStateFloat)(idUI32 ui, const char* a );
	EVDEFNAME(idThread,gui,GetStateBool)(idUI32 ui, const char* a );
	EVDEFNAME(idThread,gui,StateChanged)(idUI32 ui, int a, bool32 b );
	EVDEFNAME(idThread,gui,Activate)(idUI32 ui, bool32 a , int b );
	EVDEFNAME(idThread,gui,Trigger)(idUI32 ui, int a );
	EVDEFNAME(idThread,gui,SetCursor)(idUI32 ui, float a ,float b );
	EVDEFNAME(idThread,gui,CursorX)(idUI32 ui );
	EVDEFNAME(idThread,gui,CursorY)(idUI32 ui );

	EVDEFNAME(idThread,dict,Create)() ;
	EVDEFNAME(idThread,dict,Delete)(idDict* ui,  const char* a ) ;
	EVDEFNAME(idThread,dict,GetAngles)(idDict* ui , const char* a) ;
	EVDEFNAME(idThread,dict,GetMatrix)(idDict* ui , const char* a) ;
	EVDEFNAME(idThread,dict,GetBool)(idDict* ui, const char* a );
	EVDEFNAME(idThread,dict,GetFloat)(idDict* ui, const char* a );
	EVDEFNAME(idThread,dict,GetInt)(idDict* ui, const char* a );
	EVDEFNAME(idThread,dict,GetNumKeyVals)(idDict* ui) ;
	EVDEFNAME(idThread,dict,GetString)(idDict* ui, const char* a) ;
	EVDEFNAME(idThread,dict,SetAngles)(idDict* ui,  const char* a,const idAngles& b ) ;
	EVDEFNAME(idThread,dict,SetBool)(idDict* ui,  const char* a,bool32 b ) ;
	EVDEFNAME(idThread,dict,SetDefaults)(idDict* ui, idDict* a ) ;
	EVDEFNAME(idThread,dict,SetFloat)(idDict* ui, const char* a,float b ) ;
	EVDEFNAME(idThread,dict,SetInt)(idDict* ui,  const char* a,int b ) ;
	EVDEFNAME(idThread,dict,SetMatrix)(idDict* ui,  const char* a,const idMat3& b ) ;
	EVDEFNAME(idThread,dict,SetString)(idDict* ui,  const char* a, const char* b ) ;
	EVDEFNAME(idThread,dict,SetVec2)(idDict* ui,  const char* a,const idVec2& b ) ;
	EVDEFNAME(idThread,dict,SetVec4)(idDict* ui,  const char* a,const idVec4& b ) ;
	EVDEFNAME(idThread,dict,SetVector)(idDict* ui,  const char* a,const idVec3 &b ) ;
public:
	const char*					GetStateName();
	idInterpreter             *Get_Dax_Interpreter(void);     

	#endif //DAXMOO_JNI
	static idThread             *currentThread;

	idThread                    *waitingForThread;
	int                         waitingFor;
	int                         waitingUntil;
	idInterpreter               interpreter;

	idDict                      spawnArgs;

	int                         threadNum;
	idStr                       threadName;

	int                         lastExecuteTime;
	int                         creationTime;

	bool                        manualControl;

	static int                  threadIndex;
	static idList<idThread *>   threadList;

	static trace_t              trace;

	void                        Init( void );
	void                        Pause( void );

	void                        Event_Execute( void );
	void                        Event_SetThreadName( const char *name );

	//
	// script callable Events
	//
	void                        Event_TerminateThread( int num );
	void                        Event_Pause( void );
	void                        Event_Wait( float time );
	void                        Event_WaitFrame( void );
	void                        Event_WaitFor( idEntity *ent );
	void                        Event_WaitForThread( int num );
	void                        Event_Print( const char *text );
	void                        Event_PrintLn( const char *text );
	void                        Event_Say( const char *text );
	void                        Event_Assert( float value );
	void                        Event_Trigger( idEntity *ent );
	void                        Event_SetCvar( const char *name, const char *value ) const;
	void                        Event_GetCvar( const char *name ) const;
	void                        Event_Random( float range ) const;
	#if defined(_D3XP) || defined(D3V_D3XP)
	void                        Event_RandomInt( int range ) const;
	#endif
	void                        Event_GetTime( void );
	void                        Event_KillThread( const char *name );
	void                        Event_GetEntity( const char *name );
	void                        Event_Spawn( const char *classname );
	void                        Event_CopySpawnArgs( idEntity *ent );
	void                        Event_SetSpawnArg( const char *key, const char *value );
	void                        Event_SpawnString( const char *key, const char *defaultvalue );
	void                        Event_SpawnFloat( const char *key, float defaultvalue );
	void                        Event_SpawnVector( const char *key, idVec3 &defaultvalue );
	void                        Event_ClearPersistantArgs( void );
	void                        Event_SetPersistantArg( const char *key, const char *value );
	void                        Event_GetPersistantString( const char *key );
	void                        Event_GetPersistantFloat( const char *key );
	void                        Event_GetPersistantVector( const char *key );
	void                        Event_AngToForward( idAngles &ang );
	void                        Event_AngToRight( idAngles &ang );
	void                        Event_AngToUp( idAngles &ang );
	void                        Event_GetSine( float angle );
	void                        Event_GetCosine( float angle );
	#if defined(_D3XP) || defined(D3V_D3XP)
	void                        Event_GetArcSine( float a );
	void                        Event_GetArcCosine( float a );
	#endif
	void                        Event_GetSquareRoot( float theSquare );
	void                        Event_VecNormalize( idVec3 &vec );
	void                        Event_VecLength( idVec3 &vec );
	void                        Event_VecDotProduct( idVec3 &vec1, idVec3 &vec2 );
	void                        Event_VecCrossProduct( idVec3 &vec1, idVec3 &vec2 );
	void                        Event_VecToAngles( idVec3 &vec );
	#if defined(_D3XP) || defined(D3V_D3XP)
	void                        Event_VecToOrthoBasisAngles( idVec3 &vec );
	void                        Event_RotateVector( idVec3 &vec, idVec3 &ang );
	#endif
	void                        Event_OnSignal( int signal, idEntity *ent, const char *func );
	void                        Event_ClearSignalThread( int signal, idEntity *ent );
	void                        Event_SetCamera( idEntity *ent );
	void                        Event_FirstPerson( void );
	void                        Event_Trace( const idVec3 &start, const idVec3 &end, const idVec3 &mins, const idVec3 &maxs, int contents_mask, idEntity *passEntity );
	void                        Event_TracePoint( const idVec3 &start, const idVec3 &end, int contents_mask, idEntity *passEntity );
	void                        Event_GetTraceFraction( void );
	void                        Event_GetTraceEndPos( void );
	void                        Event_GetTraceNormal( void );
	void                        Event_GetTraceEntity( void );
	void                        Event_GetTraceJoint( void );
	void                        Event_GetTraceBody( void );
	void                        Event_FadeIn( idVec3 &color, float time );
	void                        Event_FadeOut( idVec3 &color, float time );
	void                        Event_FadeTo( idVec3 &color, float alpha, float time );
	void                        Event_SetShaderParm( int parmnum, float value );
	void                        Event_StartMusic( const char *name );
	void                        Event_Warning( const char *text );
	void                        Event_Error( const char *text );
	void                        Event_StrLen( const char *string );
	void                        Event_StrLeft( const char *string, int num );
	void                        Event_StrRight( const char *string, int num );
	void                        Event_StrSkip( const char *string, int num );
	void                        Event_StrMid( const char *string, int start, int num );
	void                        Event_StrToFloat( const char *string );
	void                        Event_RadiusDamage( const idVec3 &origin, idEntity *inflictor, idEntity *attacker, idEntity *ignore, const char *damageDefName, float dmgPower );
	void                        Event_IsClient( void );
	void                        Event_IsMultiplayer( void );
	void                        Event_GetFrameTime( void );
	void                        Event_GetTicsPerSecond( void );
	void                        Event_CacheSoundShader( const char *soundName );
	void                        Event_DebugLine( const idVec3 &color, const idVec3 &start, const idVec3 &end, const float lifetime );
	void                        Event_DebugArrow( const idVec3 &color, const idVec3 &start, const idVec3 &end, const int size, const float lifetime );
	void                        Event_DebugCircle( const idVec3 &color, const idVec3 &origin, const idVec3 &dir, const float radius, const int numSteps, const float lifetime );
	void                        Event_DebugBounds( const idVec3 &color, const idVec3 &mins, const idVec3 &maxs, const float lifetime );
	void                        Event_DrawText( const char *text, const idVec3 &origin, float scale, const idVec3 &color, const int align, const float lifetime );
	void                        Event_InfluenceActive( void );

public:                         
	CLASS_PROTOTYPE( idThread );

	idThread();
	idThread( idEntity *self, const function_t *func );
	idThread( const function_t *func );
	idThread( idInterpreter *source, const function_t *func, int args );
	idThread( idInterpreter *source, idEntity *self, const function_t *func, int args );

	virtual                     ~idThread();

	// tells the thread manager not to delete this thread when it ends
	void                        ManualDelete( void );

	// save games
	void                        Save( idSaveGame *savefile ) const;				// archives object for save game file
	void                        Restore( idRestoreGame *savefile );				// unarchives object from save game file

	void                        EnableDebugInfo( void ) {
		interpreter.debug = true;
	};
	void                        DisableDebugInfo( void ) {
		interpreter.debug = false;
	};

	void                        WaitMS( int time );
	void                        WaitSec( float time );
	void                        WaitFrame( void );

	// NOTE: If this is called from within a event called by this thread, the function arguments will be invalid after calling this function.
	void                        CallFunction( const function_t  *func, bool clearStack );

	// NOTE: If this is called from within a event called by this thread, the function arguments will be invalid after calling this function.
	void                        CallFunction( idEntity *obj, const function_t *func, bool clearStack );

	void                        DisplayInfo();
	static idThread             *GetThread( int num );
	static void                 ListThreads_f( const idCmdArgs &args );
	static void                 Restart( void );
	static void                 ObjectMoveDone( int threadnum, idEntity *obj );

	static idList<idThread*>&   GetThreads ( void );

	bool                        IsDoneProcessing ( void );
	bool                        IsDying          ( void );  

	void                        End( void );
	static void                 KillThread( const char *name );
	static void                 KillThread( int num );
	bool                        Execute( void );
	void                        ManualControl( void ) {
		manualControl = true; CancelEvents( &EV_Thread_Execute );
	};
	void                        DoneProcessing( void ) {
		interpreter.doneProcessing = true;
	};
	void                        ContinueProcessing( void ) {
		interpreter.doneProcessing = false;
	};
	bool                        ThreadDying( void ) {
		return interpreter.threadDying;
	};
	void                        EndThread( void ) {
		interpreter.threadDying = true;
	};
	bool                        IsWaiting( void );
	void                        ClearWaitFor( void );
	bool                        IsWaitingFor( idEntity *obj );
	void                        ObjectMoveDone( idEntity *obj );
	void                        ThreadCallback( idThread *thread );
	void                        DelayedStart( int delay );
	bool                        Start( void );
	idThread                    *WaitingOnThread( void );
	void                        SetThreadNum( int num );
	int                         GetThreadNum( void );
	void                        SetThreadName( const char *name );
	const char                  *GetThreadName( void );

	void                        Error( const char *fmt, ... ) const id_attribute((format(printf,2,3)));
	void                        Warning( const char *fmt, ... ) const id_attribute((format(printf,2,3)));

	static idThread             *CurrentThread( void );
	static int                  CurrentThreadNum( void );
	static bool                 BeginMultiFrameEvent( idEntity *ent, const idEventDef *event );
	static void                 EndMultiFrameEvent( idEntity *ent, const idEventDef *event );

	static void                 ReturnString( const char *text );
	static void                 ReturnFloat( float value );
	static void                 ReturnInt( int value );
	static void                 ReturnVector( idVec3 const &vec );
	static void                 ReturnEntity( idEntity *ent );
};

/*
================
idThread::WaitingOnThread
================
*/
ID_INLINE idThread *idThread::WaitingOnThread( void ) {
	return waitingForThread;
}

/*
================
idThread::SetThreadNum
================
*/
ID_INLINE void idThread::SetThreadNum( int num ) {
	threadNum = num;
}

/*
================
idThread::GetThreadNum
================
*/
ID_INLINE int idThread::GetThreadNum( void ) {
	return threadNum;
}

/*
================
idThread::GetThreadName
================
*/
ID_INLINE const char *idThread::GetThreadName( void ) {
	return threadName.c_str();
}

/*
================
idThread::GetThreads
================
*/
ID_INLINE idList<idThread*>& idThread::GetThreads ( void ) {
	return threadList;
}   

/*
================
idThread::IsDoneProcessing
================
*/
ID_INLINE bool idThread::IsDoneProcessing ( void ) {
	return interpreter.doneProcessing;
}

/*
================
idThread::IsDying
================
*/
ID_INLINE bool idThread::IsDying ( void ) {
	return interpreter.threadDying;
}

#endif /* !__SCRIPT_THREAD_H__ */