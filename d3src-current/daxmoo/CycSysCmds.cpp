// Copyright (C) 2005 Daxtron/Logicmoo 
// 
#include "../../idlib/precompiled.h" 
#pragma hdrstop 

#include "../Daxmoo/DaxWebServer.h" 
#include "../Daxmoo/CycAccess.h"
//#include "../../DaxInterpreter.h" 

#include "../game/Game_local.h" 
#include "../game/GameSys/TypeInfo.h" 


//ALL is DAXMOO
#ifdef DAXMOO
//  void		udpprint(const char *msg) ; 
//  void idGameLocal::udpprint(const char *msg)  {gameLocal.udpprint(msg);} 

//-------------------------------------------------------------------------------------- 
// Description:   Call the specified script function. 
// Parameters:   The name of the script function to call. 
// Returns:      true if the script function was called successfully; false otherwise. 
//-------------------------------------------------------------------------------------- 
bool CallScriptFunction(idStr strScriptFunctionName, idStr strFormat, ...) {
	if (!strScriptFunctionName.IsEmpty()) {
		const function_t* k_pfnScript = gameLocal.program.FindFunction(strScriptFunctionName); 
		if (!k_pfnScript) {
			gameLocal.Warning("Function \"%s\" not found.\n", strScriptFunctionName.c_str()); 
			return false; 
		} else {
			// Create a script interpreter. 
			idInterpreter ScriptInterpreter; 
			va_list ArgumentList; 
			va_start(ArgumentList, strFormat); 
			ScriptInterpreter.CallFunction(k_pfnScript, strFormat, ArgumentList); 
			va_end(ArgumentList); 
			return true; 
		} 
	}

	return false; 
} 




/*  
============  
CycPrintf  
============  
*/  
void CycPrintf( const char *fmt, ... ) {
	va_list     argptr;  
	char        text[MAX_STRING_CHARS];  

	va_start( argptr, fmt );  
	idStr::vsnPrintf( text, sizeof( text ), fmt, argptr );  
	va_end( argptr );  

	gameLocalPrintf("(cyc %s)",text);  
}  

void sFormat(char *text, const char *fmt, ... ) {
	va_list     argptr;  

	va_start( argptr, fmt );  
	idStr::vsnPrintf( text, sizeof( text ), fmt, argptr );  
	va_end( argptr );  
}  

int gen_sessid(void) {
	static int sessidnum =0;  
	sessidnum++;  
	sessidnum = sessidnum % 12345;  
	return(sessidnum);  
}  

/*  
===================  
Cyc_QueryCanSee_f  
===================  
*/  
void Cyc_QueryCanSee_f( const idCmdArgs &args ) {
	int         e;  
	idEntity    *check;  
	int         count;  
	size_t      size;  
	idStr       match;  
	idPlayer    *player;  
	idVec3      origin;  
	idMat3      axis;  
	idVec3      ent_origin;  
	idVec3      diff;  

	int sessid;  
	sessid = gen_sessid();  

	player = gameLocal.GetLocalPlayer();  
	if (!player) {
		return;  
	}

	gameLocalPrintf( "(begin %d)\n",sessid);  

	const renderView_t *view = player->GetRenderView();  
	if (view) {
		gameLocalPrintf( "(locatedAtPoint-Spatial myBody (Point3Fn %s))\n (D3yaw myBody %.1f)\n", view->vieworg.ToString(), view->viewaxis[0].ToYaw() );  
	} else {
		player->GetViewPos( origin, axis );  
		gameLocalPrintf( "(locatedAtPoint-Spatial myBody (Point3Fn %s))\n (D3yaw myBody %.1f)\n", origin.ToString(), axis[0].ToYaw() );  
	}  

	if (args.Argc() > 1) {
		match = args.Args();  
		match.Replace("#$","");  
	} else {
		match = "";  
	}  

	count = 0;  
	size = 0;  

	//gameLocalPrintf( "%-4s  %-20s %-20s %s  %s\n", " Num", "EntityDef", "Class", "Name","Loc" );  
	//gameLocalPrintf( "--------------------------------------------------------------------\n" );  
	for (e = 0; e < MAX_GENTITIES; e++) {
		check = gameLocal.entities[ e ];  

		if (!check) {
			continue;  
		}
		if (!player->CanSee( check, true )) {
			continue;  
		}

		if (!check->name.Filter( match ,false )) {
			continue;  
		}

		ent_origin=check->GetPhysics()->GetOrigin(); //GetWorldCoordinates( ent_origin);  

		//gameLocalPrintf( "%4i: %-20s %-20s %s {%s}\n", e,  
		//	check->GetEntityDefName(), check->GetClassname(), check->name.c_str(),ent_origin.ToString()  );  
		gameLocalPrintf( "(sees myBody %s)\n", check->name.c_str());  
		gameLocalPrintf( "(isa %s %s)\n", check->name.c_str(),check->GetClassname());  
		gameLocalPrintf( "(defname %s %s)\n", check->name.c_str(),check->GetEntityDefName());  
		gameLocalPrintf( "(locatedAtPoint-Spatial %s (Point3Fn %s))\n", check->name.c_str(),ent_origin.ToString());  
		diff = ent_origin - player->GetPhysics()->GetOrigin();  
		gameLocalPrintf( "(selfRelative %s (Point3Fn %s))\n", check->name.c_str(),diff.ToString());  

		count++;  
		size += check->spawnArgs.Allocated();  
	}  

	//gameLocalPrintf( "...%d entities\n...%d bytes of spawnargs\n", count, size );  
	gameLocalPrintf( "(numVisibleEntities %d)\n (spawnArgs_size %d)\n", count, size );  
	gameLocalPrintf( "(fin %d)\n",sessid);  

}  

/*  
===================  
Cyc_QueryObject_f  
===================  
*/  
void Cyc_QueryObject_f( const idCmdArgs &args ) {
	idEntity    *check;  
	idStr       match;  

	int sessid;  
	sessid=gen_sessid();  

	gameLocalPrintf( "(begin %d)\n",sessid);  

	if (args.Argc() > 1) {
		match = args.Args();  
		match.Replace("#$","");  
	} else {
		match = "";  
	}  
	for (int e = 0; e < MAX_GENTITIES; e++) {
		check = gameLocal.entities[ e ];  

		if (!check)	continue;
		if (!check->name.Filter( match ,false )) continue;

		gameLocalPrintf( "(classname \"%s\" \"%s\")\n", check->name.c_str(),check->GetClassname());  
	}  
	gameLocalPrintf( "(fin %d)\n",sessid);  

}  

void Cyc_loadLang_f( const idCmdArgs &args ) {
	// idLangDict::Load("strings/english.lang");  

}  

void Cyc_QueryObjectType_f( const idCmdArgs &args ) {
	idEntity    *check;  
	idStr       match;  
	idStr       objclass;  
	int sessid=gen_sessid();  

	gameLocalPrintf( "(begin %d)\n",sessid);  

	if (args.Argc() > 1) {
		match = args.Args();  
		match.Replace("#$","");  
	} else {
		match = "";  
	}  

	for (int e = 0; e < MAX_GENTITIES; e++) {
		check = gameLocal.entities[ e ];  

		if (!check)	continue;

		objclass=check->GetClassname();  
		if (!objclass.Filter( match ,false )) continue;

		const char* theDefName = check->GetEntityDefName();  

		if (theDefName[0]!= '*') {
			gameLocalPrintf( "(defname \"%s\" \"%s\")\n", check->name.c_str(),theDefName);  
		} else {
			gameLocalPrintf( "(defname \"%s\" \"classnameNULL\")\n", check->name.c_str());  
		}  
	}  
	gameLocalPrintf( "(fin %d)\n",sessid);  

}  


void Cyc_QuerySpawnArgs_f( const idCmdArgs &args ) {
	int i;  
	idEntity *ent;  
	idStr match;  
	idStr varid;  
	int ecount=0;  
	int sessid;  
	sessid=gen_sessid();  

	//ent = gameLocal.FindEntity( args.Argv( 1 ) );  
	if (args.Argc() > 1) {
		match = args.Argv(2);  
		match.Replace("#$","");  
	} else {
		match = "";  
	}  
	gameLocalPrintf( "(begin %d)\n",sessid);  


	for (int e = 0; e < MAX_GENTITIES; e++) {
		ent = gameLocal.entities[ e ];  

		if (!ent) {
			continue;  
		}
		if (!ent->name.Filter( match ,false )) {
			continue;  
		}

		if (ent->spawnArgs.GetNumKeyVals() <=0) {
			continue;  
		}

		//spreader=DiffMod / ent->spawnArgs.GetNumKeyVals();  
		for (i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++) {
			const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
			gameLocalPrintf( "(spawnArgs \"%s\" \"%s\"  \"%s\" )\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
		}  
	}  
	gameLocalPrintf( "(numchanges %i)\n",ecount );  
	gameLocalPrintf( "(fin %d)\n",sessid);  

}  

/*  
===================  
Cyc_QueryObjectArgs_f  
===================  
*/  
void Cyc_QueryObjectArgs_f( const idCmdArgs &args ) {
	int i;  
	idEntity *ent;  
	idStr match;  
	int sessid;  
	sessid=gen_sessid();  

	ent = gameLocal.FindEntity( args.Argv( 1 ) );  
	if (!ent) {
		gameLocalPrintf( "entity not found\n" );  
		return;  
	}
	gameLocalPrintf( "(begin %d)\n",sessid);  
	if (args.Argc() > 1) {
		match = args.Argv(2);  
		match.Replace("#$","");  
	} else {
		match = "";  
	}  

	for (i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++) {
		const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
		if (kv->GetKey().Filter( match ,false )) {
			gameLocalPrintf( "(spawnArgs \"%s\" \"%s\"  \"%s\" )\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
		}
	}  

	gameLocalPrintf( "(fin %d)\n",sessid);  
}  
/*  
===================  
Cyc_QueryChangedObjectArgs_f  
===================  
*/  
//#define DiffSpace 65535  
//#define DiffMod 65521  
	#define DiffSpace 323584  
	#define DiffMod 323581  
static int DiffMap[DiffSpace];  
static int DiffHits[DiffSpace];  

void Cyc_QueryChangedObjectArgs_f( const idCmdArgs &args ) {
	int i;  
	int e;  
	idEntity *ent;  
	idStr match;  
	idStr varid;  
	long hashindex;  
	int hashval;  
	int ecount=0;  
	int spreader;  
	int sessid;  
	sessid=gen_sessid();  

	//ent = gameLocal.FindEntity( args.Argv( 1 ) );  
	if (args.Argc() > 1) {
		match = args.Argv(2);  
		match.Replace("#$","");  
	} else {
		match = "";  
	}  

	gameLocalPrintf( "(begin %d)\n",sessid);  

	spreader= DiffMod / MAX_GENTITIES;  

	for (e = 0; e < MAX_GENTITIES; e++) {
		ent = gameLocal.entities[ e ];  


		if (!ent) {
			//gameLocalPrintf( "entity not found\n" );  
			//return;  
			continue;  
		}
		if (!ent->name.Filter( match ,false )) {
			continue;  
		}

		if (ent->spawnArgs.GetNumKeyVals() <=0) {
			//gameLocalPrintf( "entity has no args\n" );  
			//return;  
			continue;  
		}

		//spreader=DiffMod / ent->spawnArgs.GetNumKeyVals();  
		for (i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++) {
			const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
			if (kv->GetKey().Filter( match ,false )) {
				varid = kv->GetKey()+ent->name;  
				hashindex = (e * spreader) + i;  
				hashindex= hashindex % DiffMod;  
				hashval = match.Hash(kv->GetValue().c_str());  

				if (DiffMap[hashindex]!=hashval) {
					DiffHits[hashindex]++;  
					//gameLocalPrintf( "[%ld , %d , %d] ",hashindex,hashval,DiffHits[hashindex] );  
					gameLocalPrintf( "(spawnArgs \"%s\" \"%s\"  \"%s\" )\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
					DiffMap[hashindex]=hashval;  
					ecount++;  
				}
			}
		}  
	}  
	gameLocalPrintf( "(numchanges %i)\n",ecount );  
	gameLocalPrintf( "(fin %d)\n",sessid);  

}  
void Cyc_QueryChangedWorldArgs_f( const idCmdArgs &args ) {
	int i;  
	idEntity *ent;  
	idStr match;  
	idStr varid;  
	long hashindex;  
	int hashval;  
	int e;  
	int ecount=0;  
	int spreader;  
	int sessid;  
	sessid=gen_sessid();  

	if (args.Argc() > 0) {
		match = args.Argv(1);  
		match.Replace("#$","");  
	} else {
		match = "";  
	}  
	gameLocalPrintf( "(begin %d)\n",sessid);  


	spreader= DiffMod / MAX_GENTITIES;  
	for (e = 0; e < MAX_GENTITIES; e++) {
		ent = gameLocal.entities[ e ];  

		if (!ent) {
			continue;  
		}

		if (ent->spawnArgs.GetNumKeyVals() <=0) {
			continue;  
		}
		//mapEnt->epairs.Set( "origin", m->GetPhysics()->GetOrigin().ToString( 8 ) );  
		ent->spawnArgs.SetVector( "origin", ent->GetPhysics()->GetOrigin() );  

		//spreader=DiffMod / ent->spawnArgs.GetNumKeyVals();  
		for (i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++) {
			const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
			if (kv->GetKey().Filter( match ,false )) {
				varid = kv->GetKey()+ent->name;  
				hashindex = (e * spreader) + i;  
				//hashindex = e + (i*spreader);  
				hashindex= hashindex % DiffMod;  
				hashval = match.Hash(kv->GetValue().c_str());  

				if (DiffMap[hashindex]!=hashval) {
					DiffHits[hashindex]++;  
					//gameLocalPrintf( "[%ld ,  %d , %d] ",hashindex,hashval,DiffHits[hashindex] );  
					gameLocalPrintf( "(spawnArgs \"%s\" \"%s\"  \"%s\" )\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
					DiffMap[hashindex]=hashval;  
					ecount++;  
				}
			}
		}  
	}  
	gameLocalPrintf( "(numchanges %d)\n",ecount );  
	gameLocalPrintf( "(spreader %d)\n",spreader );  
	gameLocalPrintf( "(maxents %d)\n",MAX_GENTITIES );  


	gameLocalPrintf( "(fin %d)\n",sessid);  

}  

void Cyc_InitChangedWorldArgs_f( const idCmdArgs &args ) {
	int i;  
	idEntity *ent;  
	idStr match;  
	idStr varid;  
	long hashindex;  
	int hashval;  
	int e;  
	int ecount=0;  
	int spreader;  
	int sessid;  
	sessid=gen_sessid();  

	if (args.Argc() > 0) {
		match = args.Argv(1);  
		match.Replace("#$","");  
	} else {
		match = "";  
	}  

	gameLocalPrintf( "(begin %d)\n",sessid);  
	spreader= DiffMod / MAX_GENTITIES;  
	for (e = 0; e < MAX_GENTITIES; e++) {
		ent = gameLocal.entities[ e ];  

		if (!ent) {
			continue;  
		}

		if (ent->spawnArgs.GetNumKeyVals() <=0) {
			continue;  
		}
		//spreader=DiffMod / ent->spawnArgs.GetNumKeyVals();  
		for (i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++) {
			const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
			if (kv->GetKey().Filter( match ,false )) {
				varid = kv->GetKey()+ent->name;  
				hashindex = (e * spreader) + i;  
				//hashindex = e + (i*spreader);  
				hashindex= hashindex % DiffMod;  
				hashval = match.Hash(kv->GetValue().c_str());  

				if (DiffMap[hashindex]!=hashval) {
					DiffHits[hashindex]++;  
					//gameLocalPrintf( "[%ld ,  %d , %d] ",hashindex,hashval,DiffHits[hashindex] );  
					//gameLocalPrintf( "(spawnArgs \"%s\" \"%s\"  \"%s\" )\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str() );  
					DiffMap[hashindex]=hashval;  
					ecount++;  
				}
			}
		}  
	}  
	gameLocalPrintf( "(numchanges %d)\n",ecount );  
	gameLocalPrintf( "(spreader %d)\n",spreader );  
	gameLocalPrintf( "(maxents %d)\n",MAX_GENTITIES );  

	gameLocalPrintf( "(fin %d)\n",sessid);  

}  


void util_ObjectArgUpdate_f(idEntity *ent, idStr key ) {
	const char          *temp;  
	idVec3              origin;  
	idMat3              axis;  
	const char          *classname;  
	const char          *scriptObjectName;  

	idDict spawnArgs = ent->spawnArgs;  

	if (key.Cmp("scriptobject")) {
		// setup script object  
		if (ent->ShouldConstructScriptObjectAtSpawn() && spawnArgs.GetString( "scriptobject", NULL, &scriptObjectName )) {
			if (!ent->scriptObject.SetType( scriptObjectName )) {
				gameLocal.Error( "Script object '%s' not found on entity '%s'.", scriptObjectName, ent->GetName() );  
			}

			ent->ConstructScriptObject();  
		}
		return;
	}


	if (key.Cmp("origin")) {
		ent->UpdateVisuals();  
		return;
	}
	if (key.Cmp("task")) {
		return;
	}
	if (key.Cmp("currentDest")) {
		return;
	}
	if (key.Cmp("currentItem")) {
		return;
	}


//	newCStr = newValue.c_str();  
	if (key.Cmp("classname")==0) {
		//ent->UpdateChangeableSpawnArgs(spawnArgs);  
		//dmiles  gameLocal.RegisterEntity( this );  
		spawnArgs.GetString( "classname", NULL, &classname );  
		const idDeclEntityDef *def = gameLocal.FindEntityDef( classname, false );  
		if (def) {
			ent->entityDefNumber = def->Index();  
		}
		//dmiles gameLocalPrintf("Hello %s!\n", classname);  
		gameLocalPrintf("(defname \"%s\" \"%s\")\n", spawnArgs.GetString( "name" ), classname);  

		//dmiles ent->FixupLocalizedStrings();  

		//  class is updated so we should update the model	  
		key = "model";  
	}

	// every object will have a unique name  
	if (!ent->GetName()) {
		temp = spawnArgs.GetString( "name", va( "%s_%s_%d", ent->GetClassname(), spawnArgs.GetString( "classname" ), ent->entityNumber ) );  
		ent->SetName( temp );  
	}


	if (key.Cmp("model")==0) {
		renderEntity_t renderEntity = *ent->GetRenderEntity();  
		// parse static models the same way the editor display does  
		gameEdit->ParseSpawnArgsToRenderEntity( &spawnArgs, &renderEntity );  

		renderEntity.entityNum = ent->entityNumber;  

		// go dormant within 5 frames so that when the map starts most monsters are dormant  
		ent->dormantStart = gameLocal.time - DELAY_DORMANT_TIME + gameLocal.msec * 5;  

		origin = renderEntity.origin;  
		axis = renderEntity.axis;  

		// do the audio parsing the same way dmap and the editor do  
		refSound_t refSound;  
		gameEdit->ParseSpawnArgsToRefSound( &spawnArgs, &refSound );  

		// only play SCHANNEL_PRIVATE when sndworld->PlaceListener() is called with this listenerId  
		// don't spatialize sounds from the same entity  
		refSound.listenerId = ent->entityNumber + 1;  


		// ent->cameraTarget = NULL;  
		temp = spawnArgs.GetString( "cameraTarget" );  
		if (temp && temp[0]) {
			// update the camera taget  
			//ent->PostEventMS( &EV_UpdateCameraTarget, 0 );  
		}
		//	int					i;  
		//	for ( i = 0; i < MAX_RENDERENTITY_GUI; i++ ) {  
		//		ent->UpdateGuiParms( renderEntity.gui[ i ], &spawnArgs );  
		//	}  
		//	entityFlags_s fl = ent->fl;  
		ent->fl.solidForTeam = spawnArgs.GetBool( "solidForTeam", "0" );  
		ent->fl.neverDormant = spawnArgs.GetBool( "neverDormant", "0" );  
		ent->fl.hidden = spawnArgs.GetBool( "hide", "0" );  
		if (ent->fl.hidden) {
			// make sure we're hidden, since a spawn function might not set it up right  
			ent->PostEventMS( &EV_Hide, 0 );  
		}

		ent->cinematic = spawnArgs.GetBool( "cinematic", "0" );  
	}


	if (key.Cmp("networkSync")==0) {
		const idKeyValue    *networkSync;  
		networkSync = spawnArgs.FindKey( "networkSync" );  
		if (networkSync) {
			ent->fl.networkSync = ( atoi( networkSync->GetValue() ) != 0 );  
		}
	}

	// if we have targets, wait until all entities are spawned to get them  
	if (spawnArgs.MatchPrefix( "target" ) || spawnArgs.MatchPrefix( "guiTarget" )) {
		if (gameLocal.GameState() == GAMESTATE_STARTUP) {
			ent->PostEventMS( &EV_FindTargets, 0 );  
		} else {
			// not during spawn, so it's ok to get the targets  
			ent->FindTargets();  
		}  
	}

	if (key.Cmp("health")==0) {
		ent->health = spawnArgs.GetInt( "health" );  
	}


//	ent->InitDefaultPhysics( origin, axis );  
//dmiles noclip origin axis  
	{  
		const char *temp;  
		idClipModel *clipModel = NULL;  

		// check if a clipmodel key/value pair is set  
		if (spawnArgs.GetString( "clipmodel", "", &temp )) {
			if (idClipModel::CheckModel( temp )) {
				clipModel = new idClipModel( temp );  
			}
		}

		if (!spawnArgs.GetBool( "noclipmodel", "0" )) {

			// check if mins/maxs or size key/value pairs are set  
			if (!clipModel) {
				idVec3 size;  
				idBounds bounds;  
				bool setClipModel = false;  

				if (spawnArgs.GetVector( "mins", NULL, bounds[0] ) &&  
					spawnArgs.GetVector( "maxs", NULL, bounds[1] )) {
					setClipModel = true;  
					if (bounds[0][0] > bounds[1][0] || bounds[0][1] > bounds[1][1] || bounds[0][2] > bounds[1][2]) {
						gameLocal.Error( "Invalid bounds '%s'-'%s' on entity '%s'", bounds[0].ToString(), bounds[1].ToString(), ent->GetName() );  
					}
				} else if (spawnArgs.GetVector( "size", NULL, size )) {
					if (( size.x < 0.0f ) || ( size.y < 0.0f ) || ( size.z < 0.0f )) {
						gameLocal.Error( "Invalid size '%s' on entity '%s'", size.ToString(), ent->GetName() );  
					}
					bounds[0].Set( size.x * -0.5f, size.y * -0.5f, 0.0f );  
					bounds[1].Set( size.x * 0.5f, size.y * 0.5f, size.z );  
					setClipModel = true;  
				}

				if (setClipModel) {
					int numSides;  
					idTraceModel trm;  

					if (spawnArgs.GetInt( "cylinder", "0", numSides ) && numSides > 0) {
						trm.SetupCylinder( bounds, numSides < 3 ? 3 : numSides );  
					} else if (spawnArgs.GetInt( "cone", "0", numSides ) && numSides > 0) {
						trm.SetupCone( bounds, numSides < 3 ? 3 : numSides );  
					} else {
						trm.SetupBox( bounds );  
					}  
					clipModel = new idClipModel( trm );  
				}
			}

			// check if the visual model can be used as collision model  
			if (!clipModel) {
				temp = spawnArgs.GetString( "model" );  
				if (( temp != NULL ) && ( *temp != 0 )) {
					if (idClipModel::CheckModel( temp )) {
						clipModel = new idClipModel( temp );  
					}
				}
			}
		}

		idPhysics* defaultPhysicsObj;  
		defaultPhysicsObj = ent->GetPhysics();  

		defaultPhysicsObj->SetSelf( ent );  
		defaultPhysicsObj->SetClipModel( clipModel, 1.0f );  
		defaultPhysicsObj->SetOrigin( origin );  
		defaultPhysicsObj->SetAxis( axis );  

		ent->SetPhysics(defaultPhysicsObj);  
	}  
	ent->SetOrigin( origin );  
	ent->SetAxis( axis );  

//dmiles model  
	temp = spawnArgs.GetString( "model" );  
	if (temp && *temp) {
		ent->SetModel( temp );  
	}

	if (spawnArgs.GetString( "bind", "", &temp )) {
//		ent->PostEventMS( &EV_SpawnBind, 0 );  
	}

	ent->UpdateVisuals();  
}  


void Cyc_clrScript_f(const idCmdArgs &args ) {
	idEntity* ent;  

	ent = gameLocal.FindEntity(args.Argv(1));  
	// clear out the object's memory  
	ent->scriptObject.ClearObject();  
}  


void Cyc_QueryScript_f(const idCmdArgs &args ) {
//    idEntity* ent;   
	idStr name;  
	const idTypeDef *type;  
//	const idVarDef	*tdef;  
//	const idVarDef	*def;  

	int sessid =gen_sessid();  
	gameLocalPrintf( "(begin %d)\n",sessid);  
	//  ent = gameLocal.FindEntity(args.Argv(1));  
	//if (ent) {
	// type = ent->scriptObject.GetTypeDef();  
	//} else {
	type=NULL;
	int showname =1;
	// type = gameLocal.FindEntityDef(args.Argv(1));  
	//}  

	// idStaticList<function_t,MAX_FUNCS>			functions;

	const function_t* funct;  
	int max = MAX_FUNCS;  
	//max = 2167;  
	for (int i=0 ; i < max ;i++) {
		//gameLocalPrintf("%d>%d",MAX_FUNCS,i);  
		funct = gameLocal.program.GetFunction(i);  
		// gameLocalPrintf("b");  
		if (funct) {
			idStr pbuffer = " ";  
			const idTypeDef *ftype = funct->type;  
			if (ftype) {
				if (idStr::Cmp(funct->Name(),"doom_main")==0) {
					gameLocal.Printf( "(fin %d)\n",sessid);  
					return;
				}
				name = funct->Name();
				int arity = ftype->NumParameters();                  
				pbuffer.Append("(");  
				for (int p=0;p<arity;p++) {
					const idTypeDef *ptype = ftype->GetParmType(p);
					if (showname) pbuffer.Append("(");
					if (ptype) {
						pbuffer.Append(ptype->Name());  
					} else {
						pbuffer.Append("NULLPARAM");  
					}
					pbuffer.Append(" ");  
					if (showname) pbuffer.Append(ftype->GetParmName(p));
					if (showname) pbuffer.Append(")");
				}  
				pbuffer.Append(") ");  
//                const idVarDef *fdef =  funct->def;
				//  gameLocalPrintf("c");  
				//def->DepthOfScope()  
				idTypeDef* retType = ftype->ReturnType();  
				if (retType) {
					idStr rname = retType->Name();
					//    gameLocalPrintf("d1");  
					gameLocal.Printf("(function %s/%d %s %s)\n",name.c_str(),arity,pbuffer.c_str(),rname.c_str());  
				} else {
					//     gameLocalPrintf("d2");  
					gameLocal.Printf("(function %s/%d %s NULL)\n",name.c_str(),arity,pbuffer.c_str());  
				}  
			}
			//  gameLocalPrintf("e");  
		}
	}  
	gameLocal.Printf( "(fin %d)\n",sessid);  

}  




/*  
===================  
Cyc_addScript_f  
===================  
*/  
void Cyc_addScript_f(const idCmdArgs &args ) {
	idStr scriptObjectName;  
	idEntity* ent;  

	ent = gameLocal.FindEntity(args.Argv(1));  
	scriptObjectName = args.Argv(2);  


	if (!ent->scriptObject.data) {
		// init the script object's data  
		ent->scriptObject.ClearObject();  
	}

	if (!ent->scriptObject.SetType(scriptObjectName)) {
		gameLocalPrintf( "(error \"Script object '%s' not found on entity '%s'.\")", scriptObjectName, ent->GetName() );  
		gameLocal.Error( "Script object '%s' not found on entity '%s'.", scriptObjectName, ent->GetName() );  
	}


	ent->ConstructScriptObject();  
}  

/*  
===================  
util_update_value  
===================  
*/  
bool util_update_value(idEntity *ent,const idKeyValue *kv, const idCmdArgs &args ) {
	idStr match,newValue;  

	if (!ent) {
		gameLocalPrintf( "entity not found (NULL)\n" );  
		return false;  
	}
	if (!kv) {
		gameLocalPrintf( "kv not found (NULL)\n" );  
		return false;  
	}

	if (args.Argc() > 2) {
		newValue = args.Argv(3);  
		newValue.Replace( "#$", "" );  
	} else {
		newValue = "";  
	}  

	match = kv->GetKey();  

	if (newValue.Cmp(kv->GetValue())==0) return false;

	ent->spawnArgs.Set(match,newValue);  
	gameLocalPrintf( "(spawnArgs \"%s\" \"%s\"  \"%s\" )\n",ent->name.c_str(), kv->GetKey().c_str(), kv->GetValue().c_str());  

	if (match.Filter("origin",false)) {
		idVec3 vect3 = idVec3();  
		if (args.Argc()==5) {
			vect3.Set(atof(args.Argv(3)),atof(args.Argv(4)),atof(args.Argv(5)));  
			ent->spawnArgs.SetVector("origin",vect3);  
		} else {
			sscanf(newValue.c_str(),"%f %f %f",&vect3.x,&vect3.y,&vect3.z);  
		}  
		ent->SetOrigin(vect3);  
		//      ent->UpdateVisuals();
		return true;
	} else if (kv->GetKey().Filter("name",false)) {
		ent->SetName(newValue.c_str());  
	} else if (kv->GetKey().Filter("axis",false)) {
	}
//    util_ObjectArgUpdate_f(ent,match);  
	return true;  
}  

/*  
===================  
util_ObjectUpdate_f  
===================  
*/  
void util_ObjectUpdate_f(idEntity *ent,const idCmdArgs &args ) {
	int i,count;  
	idStr match;  


	if (!ent) {
		gameLocalPrintf( "entity not found (NULL)\n" );  
		return;  
	}

	if (args.Argc() > 1) {
		match = args.Argv(2);  
		match.Replace( "#$", "" );  
	} else {
		match = "";  
	}  
	count = 0;  
	for (i = 0; i < ent->spawnArgs.GetNumKeyVals(); i++) {
		const idKeyValue *kv = ent->spawnArgs.GetKeyVal( i );  
		if (kv->GetKey().Filter(match,false)) {
			util_update_value(ent,kv,args);  
			count++;  
		}
	}  
	if (count==0) {
		ent->spawnArgs.Set(match,"");  
		util_update_value(ent,ent->spawnArgs.FindKey(match),args);  
	}
}  




/*  
===================  
Cyc_SetObjectArgs_f  
===================  
*/  
void Cyc_SetObjectArgs_f( const idCmdArgs &args ) {
	int         e,count;  
	idEntity    *check;  
	idStr       match,objclass,objname,objtype;  
	int sessid =gen_sessid();  
	gameLocalPrintf( "(begin %d)\n",sessid);  

	if (args.Argc() > 0) {
		match = args.Argv(1);  
		match.Replace( "#$", "" );  
	} else {
		match = "";  
	}  

	count = 0;  

	for (e = 0; e < MAX_GENTITIES; e++) {
		check = gameLocal.entities[ e ];  

		if (!check) {
			continue;  
		}

		objclass=check->GetClassname();  
		objname=check->GetName();  
		objtype=check->GetEntityDefName();  
		if (!objclass.Filter(match,false) && !objname.Filter( match,false)&& !objtype.Filter( match,false)) {
			continue;  
		}

		util_ObjectUpdate_f(check,args);  
		count++;  

	}  

	gameLocalPrintf( "(numVisibleEntities %d)\n", count);  
	gameLocalPrintf( "(fin %d)\n",sessid);  
}  

void Cyc_UDPBegin( const idCmdArgs &args ) {

	if (args.Argc()) {
		gameLocalPrintf( "(begin %s)\n",args.Argv(1));  
	} else {
		int sessid=gen_sessid();  

		gameLocalPrintf( "(begin %d)\n",sessid);  
	}  
}  

void Cyc_UDPEnd( const idCmdArgs &args ) {
	gameLocalPrintf( "(fin %s)\n",args.Argv(1));  
}  



/*  
===================  
Cyc_loadScript_f  
===================  
*/  
void Cyc_loadScript_f( const idCmdArgs &args ) {
	//dmiles  
	idStr filename = SCRIPT_DEFAULT;  

	if (args.Argc()>0) filename = args.Argv(1);


	// shutdown the map because entities may point to script objects  
	//gameLocal.MapShutdown();  

	// compile the scripts  
	gameLocal.program.CompileFile(filename);  
	//gameLocal.program.CompileText();  

	// error out so that the user can rerun the scripts  
	//gameLocal.Error( "Exiting map to reload scripts" );  
}  


void Cmd_StartScript_f( const idCmdArgs &args ) {
	//dmiles  
	idStr filename = SCRIPT_DEFAULT;  

	if (args.Argc()>0) filename = args.Argv(1);


	// shutdown the map because entities may point to script objects  
//	gameLocal.MapShutdown();  

	// recompile the scripts  
	gameLocal.program.Startup( filename );  

	// error out so that the user can rerun the scripts  
//	gameLocal.Error( "Exiting map to reload scripts" );  
}  



/*  
==================  
Cyc_Say  
==================  
*/  
extern void udpprint(const char *msg);

void Cmd_Say_All( bool team, const idCmdArgs &args );
void Cyc_Say( const idCmdArgs &args ) {
	const char* name = args.Argv(1);
	const char* text = args.Args(2);
	bool team = false;
	Daxmoo_Say(team,name,text);
}


/*  
===================  
Cmd_Spawn_f  
===================  
*/  
void Cmd2_Spawn_f( const idCmdArgs &args ) {
	const char *key, *value;  
	int         i;  
	float       yaw;  
	idVec3      org;  
	idPlayer    *player;  
	idDict      dict;  

	player = gameLocal.GetLocalPlayer();  
	if (!player || !gameLocal.CheatsOk( false )) {
		return;  
	}

	if (args.Argc() & 1) {	   // must always have an even number of arguments  
		gameLocalPrintf( "usage: spawn classname [key/value pairs]\n" );  
		return;  
	}

	yaw = player->viewAngles.yaw;  

	value = args.Argv( 1 );  
	dict.Set( "classname", value );  
	dict.Set( "angle", va( "%f", yaw + 180 ) );  

	org = player->GetPhysics()->GetOrigin() + idAngles( 0, yaw, 0 ).ToForward() * 80 + idVec3( 0, 0, 1 );  
	dict.Set( "origin", org.ToString() );  

	for (i = 2; i < args.Argc() - 1; i += 2) {

		key = args.Argv( i );  
		value = args.Argv( i + 1 );  

		dict.Set( key, value );  
	}  

	gameLocal.SpawnEntityDef( dict );  

}  

bool Cyc_SpawnEntityDef( const idDict &args, idEntity **ent, bool setDefaults ) {
	const char  *classname;  
	const char  *spawn;  
	idTypeInfo  *cls;  
	idClass     *obj;  
	idStr       error;  
	const char  *name;  

	if (ent) {
		*ent = NULL;  
	}

	idDict                  spawnArgs;				// spawn args used during entity spawning  FIXME: shouldn't be necessary anymore  

	spawnArgs = args;  

	if (spawnArgs.GetString( "name", "", &name )) {
		sprintf( error, " on '%s'", name);  
	}

	spawnArgs.GetString( "classname", NULL, &classname );  

	const idDeclEntityDef *def = gameLocal.FindEntityDef( classname, false );  

	if (!def) {
		gameLocal.Warning( "Unknown classname '%s'%s.", classname, error.c_str() );  
		return false;  
	}

	spawnArgs.SetDefaults( &def->dict );  

	// check if we should spawn a  class object  
	spawnArgs.GetString( "spawnclass", NULL, &spawn );  
	if (spawn) {

		cls = idClass::GetClass( spawn );  
		if (!cls) {
			gameLocal.Warning( "Could not spawn '%s'.  Class '%s' not found%s.", classname, spawn, error.c_str() );  
			return false;  
		}

		obj = cls->CreateInstance();  
		if (!obj) {
			gameLocal.Warning( "Could not spawn '%s'. Instance could not be created%s.", classname, error.c_str() );  
			return false;  
		}

		obj->CallSpawn();  

		if (ent && obj->IsType( idEntity::Type )) {
			*ent = static_cast<idEntity *>(obj);  
		}

		return true;  
	}

	// check if we should call a script function to spawn  
	spawnArgs.GetString( "spawnfunc", NULL, &spawn );  
	if (spawn) {
		const function_t *func = gameLocal.program.FindFunction( spawn );  
		if (!func) {
			gameLocal.Warning( "Could not spawn '%s'.  Script function '%s' not found%s.", classname, spawn, error.c_str() );  
			return false;  
		}
		idThread *thread = new idThread( func );  
		thread->DelayedStart( 0 );  
		return true;  
	}

	gameLocal.Warning( "%s doesn't include a spawnfunc or spawnclass%s.", classname, error.c_str() );  
	return false;  
}  

/*  
===================  
Cyc_Dax_Spawn_f  
  
   spawns and entity for cyc  
===================  
*/  

void Cyc_Dax_Spawn_f( const idCmdArgs &args ) {
	const char *key, *value;  
	int         i;  
//    float       yaw;  
	idVec3      org;  
	idPlayer    *player;  
	idDict      dict;  
	int sessid;  
	sessid=gen_sessid();  


	gameLocalPrintf( "(begin %d)\n",sessid);  


	player = gameLocal.GetLocalPlayer();  

	if (args.Argc() & 1) {	   // must always have an even number of arguments  
		gameLocalPrintf( "usage: spawn classname [key/value pairs]\n" );  
		return;  
	}

	// yaw = player->viewAngles.yaw;  

	value = args.Argv( 1 );  
	dict.Set( "classname", value );  

	//  dict.Set( "angle", va( "%f", yaw + 180 ) );  

	org = player->GetPhysics()->GetOrigin() + idAngles( 0, 0, 0 ).ToForward() * 80 + idVec3( 0, 0, 1 );  
	dict.Set( "origin", org.ToString() );  

	for (i = 2; i < args.Argc() - 1; i += 2) {

		key = args.Argv( i );  
		value = args.Argv( i + 1 );  

		dict.Set( key, value );  
	}  

	gameLocal.SpawnEntityDef( dict );  

	gameLocalPrintf( "(fin %d)\n",sessid);  
}  


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  

void Cyc_QueryEntityDefs_f( const idCmdArgs &args ) {
	int sessid = gen_sessid();  
	gameLocalPrintf( "(begin %d)\n",sessid);  

	idStr dictItem = args.Argv(1);  
	idStr dictDefault = args.Argv(2);  
	int total=0;  

	if (declManager) {
		int num = declManager->GetNumDecls(DECL_ENTITYDEF);  
		for (int i = 0 ;i<num;i++) {
			int count = 0;  
			const idDecl* decl = declManager->DeclByIndex(DECL_ENTITYDEF,i);  
			const char* dname = decl->GetName();  
			const idDeclEntityDef* declDef = gameLocal.FindEntityDef(dname);  
			const idDict dict = declDef->dict;  
			for (int j=0;j<dict.GetNumKeyVals();j++) {
				const idKeyValue * kv= dict.GetKeyVal(j);  
				if (kv->GetKey().Filter(dictItem.c_str(),false)) {
					gameLocalPrintf("(entityDef \"%s\" \"%s\" \"%s\")",dname,kv->GetKey().c_str(),kv->GetValue().c_str());  
					total++;  
					count++;  
				}
			}  
			if (!count && !dictDefault.IsEmpty()) {
				total++;  
				gameLocalPrintf("(entityDef \"%s\" \"%s\" \"%s\")",dname,dictItem.c_str(),dictDefault.c_str());  
			}
		}  
		gameLocalPrintf( "\n(numVisibleEntities %d)", total );  
	}
	gameLocalPrintf( "(fin %d)\n",sessid);  
}  

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  


void Cyc_Query_Classses_f( const idCmdArgs &args ) {
	int sessid = gen_sessid();  
	gameLocalPrintf( "(begin %d)\n",sessid);  
	for (int i = 0; i < idClass::GetNumTypes(); i++) {
		idTypeInfo *type;  
		type = idClass::GetType(i);  
		gameLocalPrintf( "(superClass \"%s\" \"%s\")",type->classname,type->superclass );  
//			type->lastChild - type->typeNum  
	}  

	gameLocalPrintf( "(numVisibleEntities %d)", idClass::GetNumTypes() );  
	gameLocalPrintf( "(fin %d)\n",sessid);  
}  

void Cyc_Dax_Echo( const idCmdArgs &args ) {
	gameLocalPrintf(args.Args(1));
}



idThread* GetListenerThread(idThread* temp) {
	static idThread* savedLister;
	if (temp) {
		savedLister=temp;
	}
	return savedLister;
}

void STARTLISTENER() {
	if (gameLocal.program.CompileText( "console", "void sysSTARTLISTENER() {sys.STARTLISTENER();}\n", true )) {
		GetListenerThread(new idThread( gameLocal.program.FindFunction( "sysSTARTLISTENER" ) ))->Start();
	}
}

// cycfunct <entity> <function_name> <arglist....>  
// cycfunct cyc_bot_1 testCWA 1 2 3
void Cyc_CallWithArgs_f( const idCmdArgs &args ) {
//	string fools;
	int sessid = gen_sessid();  
	// gameLocalPrintf( "(begin %d)\n",sessid);  
	idEntity* check = gameLocal.FindEntity(args.Argv(1));  
	if (check) {
		idStr funcname =  args.Argv(2);
		const function_t *func;  
		// arg2 = function_name , arg3....argn = funct_arg1 .... func_ArgN-2  
		func = check->scriptObject.GetFunction(funcname.c_str());
		if (func) {
			check->spawnArgs.Set("argv",args.Args(2));  
			check->spawnArgs.SetInt("argc",args.Argc()-3 );  
			check->spawnArgs.SetInt("sessionid",sessid);  
			for (int argi=3;argi<args.Argc();argi++) {
				sprintf(funcname,"arg%d",argi-2);  
				check->spawnArgs.Set(funcname.c_str(),args.Argv(argi));  
			}  
			// function args will be invalid after this call  
			idThread* thread = new idThread(func);
			thread->DelayedStart(0);
			while (!thread->IsDying()) {
				thread->Execute();
			}
		} else {
			gameLocalPrintf( "(begin %d)\n",sessid);  
			gameLocalPrintf( "(error %d \"bad_function %s\")\n",sessid,args.Argv(2));  
		}
	} else {
		gameLocalPrintf( "(begin %d)\n",sessid);  
		gameLocalPrintf( "(error %d \"bad_entity %s\")\n",sessid,args.Argv(1));  
	}
}

void Cyc_Dax_Task( const idCmdArgs &args ) {
	idEntity* check = gameLocal.FindEntity(args.Argv(1));
	if (!check) {
		gameLocalPrintf("cyctask: entity not found %s",args.Argv(1));
		return;
	}
	idStr test = check->spawnArgs.GetString("task");
	while (!test.IsEmpty()) {
		//gameLocalPrintf("Cyc_Dax_Task sleeping 100ms becasue of \"%s!\"",test.c_str());
		gameLocal.frameCommandThread->WaitFrame();
		test = check->spawnArgs.GetString("task");
	}
	check->spawnArgs.Set("task",args.Args(2));
}

/*
===================
Cmd_Script_f
===================
*/
static CycAccess* cyca = NULL;
static CycConnection* cyccon = NULL;

void Cyc_Dax_System_f( const idCmdArgs &args ) {
	idStr script = args.Args(); 
	gameLocal.Printf("\ncyc-system: %s\n",script.c_str());
	if (cyca==NULL)	cyca = new CycAccess();
	if (cyccon==NULL) cyccon = cyca->getCycConnection();
// cyc->
	//cyc->open();
	gameLocal.Printf("cyc-info: %s\n",cyccon->connectionInfo().c_str());
	idStr result = cyca->converseString(script);
	gameLocal.Printf("cyc-system result:%s\n",result.c_str());

	//gameLocal.Printf("\n\cyc
}
/*
===================
Cmd_Script_f
===================
void idGameEdit::MapSave( const char *path ) const {
	idMapFile *mapFile = gameLocal.GetLevelMap();
	if (mapFile) {
		mapFile->Write( (path) ? path : mapFile->GetName(), ".map");
	}
}
*/

static void Cyc_Dax_SaveMap_f( const idCmdArgs &args ) {
	idMapFile *mapFile = gameLocal.GetLevelMap();
	idStr mapName = "";
	//if ( !gameLocal.CheatsOk() ) return;

	if (args.Argc() > 1) {
		mapName = args.Argv( 1 );
		mapName = "maps/" + mapName;
	} else {
		mapName = mapFile->GetName();
		mapName += "_save";
	}

	for (int e = 0; e < MAX_GENTITIES; e++) {
		idEntity *  ent = static_cast<idEntity*>(gameLocal.entities[ e ]);
		if (!ent) continue;
		if (ent->IsType(idPlayer::Type)) {
			//TODO Player
			continue;
		}
		if (ent->IsType(idItem::Type)) {
			//TODO Items
			continue;
		}
		if (ent->IsType(idWorldspawn::Type)) {
			//TODO Better brushdefs
			continue;
		}
		if (ent->IsBound()) {
			//TODO Binders
			continue;
		}		

		// find map file entity
		idMapEntity *mapEnt = mapFile->FindEntity( ent->name );
		// create new map file entity if there isn't one for this ent
		if (!mapEnt) {
			mapEnt = new idMapEntity();
			mapFile->AddEntity( mapEnt );
		}

		mapEnt->epairs.Copy(ent->spawnArgs);

		mapEnt->epairs.Set( "classname", ent->GetEntityDefName() );
		mapEnt->epairs.Set( "name", ent->name );

		// save the moveable state
		mapEnt->epairs.Set( "origin", ent->GetPhysics()->GetOrigin().ToString( 8 ) );
		mapEnt->epairs.Set( "rotation", ent->GetPhysics()->GetAxis().ToString( 8 ) );

		if (ent->IsType( idAFEntity_Generic::Type ) || ent->IsType( idAFEntity_WithAttachedHead::Type )) {
			// save the articulated figure state
			idDict dict;
			dict.Clear();
			static_cast<idAFEntity_Base *>(ent)->SaveState( dict );
			mapEnt->epairs.Copy( dict );
		}

		if (ent->IsType( idLight::Type )) {
			// save the ent state
			idDict dict;
			dict.Clear();
			static_cast<idLight *>(ent)->SaveState( &dict );
			mapEnt->epairs.Copy( dict );
		}
		const idKeyValue* kv;

		kv = mapEnt->epairs.MatchPrefix("editor_var");
		while (kv) {
			mapEnt->epairs.Delete(kv->GetKey());
			kv = mapEnt->epairs.MatchPrefix("editor_var");
		}
		kv = mapEnt->epairs.MatchPrefix("editor_usage");
		while (kv) {
			mapEnt->epairs.Delete(kv->GetKey());
			kv = mapEnt->epairs.MatchPrefix("editor_usage");
		}
		kv = mapEnt->epairs.MatchPrefix("editor_bool");
		while (kv) {
			mapEnt->epairs.Delete(kv->GetKey());
			kv = mapEnt->epairs.MatchPrefix("editor_bool");
		}
		mapEnt->epairs.Set("mapfile","1");
// write out the map file
	}
	mapFile->Write( mapName, ".map" );
	gameLocal.Printf("done writing\n");
}


/*
===================
Cmd_Script_f
===================
*/

void Cyc_Dax_Script( const idCmdArgs &args ) {
	const char *    script;
	idStr           text;
	idStr           funcname;
	static int      funccount = 899990;
	const function_t *func;

	sprintf( funcname, "ConsoleFunction_%d", funccount++ );



	script = args.Args();    
	sprintf( text, "void %s() {%s;}\n", funcname.c_str(), script );
	text.Replace("'","\"");
	text.Replace("%22","\"");
	text.Replace("%20"," ");
	text.Replace("%0A","\n");
	text.Replace("%0a","\n");

	if (gameLocal.program.CompileText( "console", text, true )) {
		func = gameLocal.program.FindFunction( funcname );
		if (func) {
			idThread* thread = new idThread( func );
			thread->Start();
		}
	}
}


/*  
=================  
idGameLocal::InitConsoleCommands  
  
Let the system know about all of our commands  
so it can perform tab completion  
=================  
*/  
void Daxmoo_CYC_Init( void ) {
	cmdSystem->AddCommand("saveMap", Cyc_Dax_SaveMap_f,                CMD_FL_GAME,                "saveMap <filename>" );  

	cmdSystem->AddCommand( "cycsystem",              Cyc_Dax_System_f,             CMD_FL_GAME,    "spawns a game entity", idCmdSystem::ArgCompletion_Decl<DECL_ENTITYDEF> );  
	cmdSystem->AddCommand( "cycspawn",              Cyc_Dax_Spawn_f,             CMD_FL_GAME,    "spawns a game entity", idCmdSystem::ArgCompletion_Decl<DECL_ENTITYDEF> );  
	cmdSystem->AddCommand( "cycecho",                    Cyc_Dax_Echo,               CMD_FL_GAME,                "cycecho <msg>" );  
	cmdSystem->AddCommand( "cycfunct",                    Cyc_CallWithArgs_f,               CMD_FL_GAME,                "cycfunct <entity> <funct> <arg1...argN>" );  
//    cmdSystem->AddCommand( "cycexec",                    Cyc_Dax_Exec,               CMD_FL_GAME,                "cycexec <funct> <arg1...argN>" );  
	cmdSystem->AddCommand( "cyctask",                    Cyc_Dax_Task,               CMD_FL_GAME,                "cyctask <entity> <task>" );  
	cmdSystem->AddCommand( "cycscript",             Cyc_Dax_Script,               CMD_FL_GAME|CMD_FL_CHEAT,   "executes a line of script" );
	cmdSystem->AddCommand( "cycsay",                    Cyc_Say,               CMD_FL_GAME,                "cycexec <funct> <arg1...argN>" );  

	cmdSystem->AddCommand( "loadLang",              Cyc_loadLang_f,         CMD_FL_GAME,    "load a string file" );  
	cmdSystem->AddCommand( "loadScript",            Cyc_loadScript_f,           CMD_FL_GAME,    "loads scripts" );  
	cmdSystem->AddCommand( "addScript",             Cyc_addScript_f,                CMD_FL_GAME,    "adds an entity script", idCmdSystem::ArgCompletion_Decl<DECL_ENTITYDEF> );  
	cmdSystem->AddCommand( "clrScript",             Cyc_clrScript_f,                CMD_FL_GAME,    "clears an entity script", idCmdSystem::ArgCompletion_Decl<DECL_ENTITYDEF> );  
	cmdSystem->AddCommand( "callWithArgs",          Cyc_CallWithArgs_f,         CMD_FL_GAME,    "queries entities with names matching LIKE expression" );  
	cmdSystem->AddCommand( "queryClasses",          Cyc_Query_Classses_f,       CMD_FL_GAME,                "queries game classes" );  
	cmdSystem->AddCommand( "queryEntityDefs",       Cyc_QueryEntityDefs_f,      CMD_FL_GAME,                "queries entityDef(s)" );  
	cmdSystem->AddCommand( "queryScript",           Cyc_QueryScript_f,              CMD_FL_GAME,    "list an entity script", idCmdSystem::ArgCompletion_Decl<DECL_ENTITYDEF> );  
	cmdSystem->AddCommand( "queryCanSee",           Cyc_QueryCanSee_f,          CMD_FL_GAME,    "queries entities in FOV" );  
	cmdSystem->AddCommand( "queryObject",           Cyc_QueryObject_f,          CMD_FL_GAME,    "queries entities with names matching LIKE expression" );  
	cmdSystem->AddCommand( "queryObjectType",       Cyc_QueryObjectType_f,      CMD_FL_GAME,    "queries entities with  class names matching LIKE expression" );  
	cmdSystem->AddCommand( "queryObjectArgs",       Cyc_QueryObjectArgs_f,      CMD_FL_GAME,    "queries spawn args of object" );  
	cmdSystem->AddCommand( "querySpawnArgs",        Cyc_QuerySpawnArgs_f,       CMD_FL_GAME,    "queries spawn args of object" );  
	cmdSystem->AddCommand( "queryChangedObjectArgs",Cyc_QueryChangedObjectArgs_f,   CMD_FL_GAME,    "queries spawn args of object that changed" );  
	cmdSystem->AddCommand( "queryChangedWorldArgs", Cyc_QueryChangedWorldArgs_f,    CMD_FL_GAME,    "queries spawn args of ANY object that changed" );  
	cmdSystem->AddCommand( "initChangedWorldArgs",  Cyc_InitChangedWorldArgs_f,     CMD_FL_GAME,    "initialize the spawn args change noticer mechanism" );  
	cmdSystem->AddCommand( "setObjectArgs",         Cyc_SetObjectArgs_f,        CMD_FL_GAME,    "changes spawn args of object" );  
	cmdSystem->AddCommand( "udpBegin",              Cyc_UDPBegin,             CMD_FL_GAME,                "begin udp print session" );  
	cmdSystem->AddCommand( "udpEnd",                    Cyc_UDPEnd,               CMD_FL_GAME,                "end udp print session" );  

//	cmdSystem->AddCommand( "queryEntities",         Cmd_EntityList_f,           CMD_FL_GAME,    "queries game entities" );  
	//  cmdSystem->AddCommand( "queryActiveEntities",   Cmd_ActiveEntityList_f,     CMD_FL_GAME,    "queries active game entities" );  
	cmdSystem->AddCommand( "queryThreads",          idThread::ListThreads_f,    CMD_FL_GAME,    "queries script threads" );  
	cmdSystem->AddCommand( "queryMonsters",         idAI::List_f,               CMD_FL_GAME,    "queries monsters" );

//    cmdSystem->AddCommand( "queryTypeInfo",         ListTypeInfo_f,             CMD_FL_GAME,                "list type info" );  

}  

#endif



