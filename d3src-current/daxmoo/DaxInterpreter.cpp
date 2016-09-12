#include <vector>
// Copyright (C) 2005 Daxtron/Logicmoo 
//
#include "../idlib/precompiled.h"
//#include "../game/script/Script_Interpreter.h"
//#include "idInterpreter.h"
//#include "DaxControl.h"
#pragma hdrstop
#ifdef DAXMOO_JNI

	#include <jni.h>
	#include "../game/game_local.h"

//static JNIEnv *env = NULL;
static JavaVM *theJVM = NULL;

static JavaVMInitArgs vm_args;
static JavaVMOption options[4];

static jclass class_Number = NULL;
static jclass class_Integer = NULL;
static jclass class_Object = NULL;
static jclass class_Class = NULL;
static jclass class_String = NULL;
static jclass class_Method = NULL;
static jclass class_Field = NULL;
static jclass class_NoSuchFieldException = NULL;
static jclass class_NoSuchMethodException = NULL;
static jclass class_Exception = NULL;
static jclass class_Set = NULL;
static jclass class_Map = NULL;

static jobject object_IGameLocal = NULL;	/* Uninitialized */
//static jobject object_IDoomServer = NULL;
static jobject object_DoomNativeConsole = NULL;
static jobject object_NativeDoomServer = NULL;

static jclass class_IGameLocal = NULL;	/* Uninitialized */
//static jclass class_IDoomServer = NULL;
static jclass class_DoomNativeConsole = NULL;
static jclass class_NativeDoomServer = NULL;

static jclass class_IClass = NULL;
static jclass class_IEntity = NULL;
static jclass class_IVector = NULL;
static jclass class_ISys = NULL;
static jclass class_IScriptObject = NULL;

//static jclass class_IdDict = NULL;
//static jclass class_NativeManager = NULL;
//static jclass class_DoomConsoleChannel = NULL;

//static jclass class_ScriptContext = NULL;
/*
static jmethodID method_NativeDoomServer_toVector = NULL;
static jmethodID method_NativeDoomServer_toString = NULL;
static jmethodID method_NativeDoomServer_toNull = NULL;
static jmethodID method_NativeDoomServer_toInt = NULL;
static jmethodID method_NativeDoomServer_toFloat = NULL;
static jmethodID method_NativeDoomServer_IGameLocal_toEntity = NULL;
static jmethodID method_NativeDoomServer_IGameLocal_isImplemented = NULL;
static jmethodID method_NativeDoomServer_IGameLocal_start = NULL;
static jmethodID method_NativeDoomServer_floatValue = NULL;
static jmethodID method_NativeDoomServer_intValue = NULL;
static jmethodID method_NativeDoomServer_floatArrayValue = NULL;
static jmethodID method_NativeDoomServer_invokeJavaFn = NULL;
static jmethodID method_NativeDoomServer_toObject = NULL;
static jmethodID method_NativeDoomServer_evalJavaCommand = NULL;
static jmethodID method_NativeDoomServer_loadPlugin = NULL;
static jmethodID method_NativeDoomServer_startPlugin = NULL;
static jmethodID method_NativeDoomServer_getThreadGroup = NULL;
static jmethodID method_NativeDoomServer_getGameLocal = NULL;
*/
typedef jint (JNICALL GetCreatedJavaVMs_t)( JavaVM**, jsize, jsize* );
typedef jint (JNICALL CreateJavaVM_t)( JavaVM**, void**, void* );
GetCreatedJavaVMs_t* jvmGetJVMs = NULL;
CreateJavaVM_t* jvmCreateJVM = NULL;
HINSTANCE theJavaDll = NULL;


	#define STRINGIFY(THIS) #THIS

void clearExceptions(JNIEnv* env) {
	if (isValid(env)) {
		if (env->ExceptionCheck()) {
          	debugln("Exceptions found");
			env->ExceptionClear();
		}
	}
}

int scriptArity(const function_t* func);
const function_t *findFunction(const char* scope,const char* fnname);


bool isScriptValid(const function_t* func) {
	if ((func)) {
		if (func->type) {
			return true;
		}
		if (func->eventdef) {
			return true;
		}
		if (func->def) {
			return true;
		}
		return false;
	} else {
		return false;
	}
}

const char* ctypeToString(char t ) {
	switch (t) {
	case D_EVENT_VOID:
		return "void";
	case D_EVENT_INTEGER:
		return "integer";
	case D_EVENT_FLOAT :
		return "float";
	case D_EVENT_VECTOR :
		return "vector";
	case D_EVENT_STRING :
		return "string";
	case D_EVENT_ENTITY :
		return "entity";
	case D_EVENT_ENTITY_NULL :
		return "entity";
	case D_EVENT_TRACE :
		return "trace";
	default:
		return va("type_%d",t);
	}
}

const char* scriptName(const function_t* func) {
	if (isScriptValid(func)) return func->Name();
	return "<nofunct>";
}
idVarDef* toErrorDef(const char* errorString) {
	idVarDef* returnDef = new idVarDef(&type_scriptevent);
	returnDef->value.intPtr = new int(0);
	returnDef->value.floatPtr = new float(0);
	returnDef->value.stringPtr = const_cast<char*>(errorString);
	return returnDef;
}

const char* getTypeName(const idTypeDef *type) {
	const char* ret = 0;
	if (type) {
		ret = type->Name();
		if (isValid(ret)) return ret;
		idVarDef* fdef = type->def;
		if (isValid(fdef)) {
			// NULL POINTER
			ret = fdef->GlobalName();
			if (ret) return ret;
		}
	}
	return "<TypeDef>";
}


const char* scriptReturnType(const function_t* func) {
	if (isScriptValid(func)) {
		const idEventDef* evdef = func->eventdef; 
		const idTypeDef *ftype = func->type;
		if (ftype) {
			const idTypeDef* retType = ftype->ReturnType();
			if (retType) {
				return getTypeName(retType);
			}
		}
		if (evdef) {
			return ctypeToString(evdef->GetReturnType());
		}
		return "*"; 
	} else {
		return "<nofunct>";
	}
}
char typeToChar(const char* t) {
	if (idStr::Cmp(t,"vector")==0) {
		return 'v';
	} else if (idStr::Cmp(t,"string")==0) {
		return 's';
	} else if (idStr::Cmp(t,"float")==0) {
		return 'f';
	} else if (idStr::Cmp(t,"void")==0) {
		return 0;
	} else if (idStr::Cmp(t,"entity")==0) {
		return 'e';
	} else if (idStr::Cmp(t,"int")==0) {
		return 'd';
	} else if (idStr::Cmp(t,"boolean")==0) {
		return 'f';
	} else if (idStr::Cmp(t,"bool")==0) {
		return 'f';
	} else if (idStr::Cmp(t,"thread")==0) {
		return 'f';
	} else if (idStr::Cmp(t,"integer")==0) {
		return 'd';
	} else
		return 'e';
}

idStr scriptFullname(const function_t* func) {
	if (isScriptValid(func)) {
		const idEventDef* evdef = func->eventdef; 
		const idTypeDef *ftype = func->type;
		idVarDef *fdef = func->def;
		if (fdef) {
			return fdef->GlobalName();
		}
		if (ftype) {
			return ftype->Name();
		}
		if (evdef) {
			return evdef->GetName();
		}
		return "<functionname>";
	}
	return "<nofunct>";
}
bool isEvent(const function_t *func) {
	if (isScriptValid(func)) {
		if (func->eventdef) {
			return true;
		}
	}return false;
}

const char* scriptClass(const function_t* func) {
	if (isScriptValid(func)) {
		// gameLocal.Printf("scriptClass start");
		char* s = (char*)malloc(120);
		const char* fullname = scriptFullname(func);
		int len = strlen(fullname);
		strcpy(s,fullname);
		if (len>2) {
			// gameLocal.Printf("scriptClass >2");
			for (int lst=0;lst<len;lst++) {
				char c = s[lst];
				if (c==':') {
					s[lst]=0;
					return s;
				}
			}
		}

		delete s;
		// gameLocal.Printf("scriptClass lst == -1");
		if (isEvent(func)) {
			return "event";
		} else {
			return "global";
		}

	}
	return "<nofunct>";
}
const char* scriptFormat(const function_t* func) {
	if (isScriptValid(func)) {
		const idTypeDef* ftype = func->type;
		if (ftype) {
			int len = ftype->NumParameters();
			char* ret = (char*)malloc(len+1);            
			for (int i=0;i<len;i++) {
				idTypeDef* ptdef = ftype->GetParmType(i);
				if (ptdef) {
					const char* ptname = ptdef->Name();
					if (ptname) {
						ret[i]=typeToChar(ptname);
					} else {
						ret[i]='V';
					}
				} else {
					ret[i]='*';
				}
			}
			ret[len]=0;
			return ret;
		}
		const idEventDef* evdef = func->eventdef;
		if (evdef) return evdef->GetArgFormat();

	}
	return "";
}
const char* scriptParameterType(const function_t* func, int num) {
	if (isScriptValid(func)) {
		if (num<0) {
			return scriptReturnType(func);
		}
		if (num<1) {
			return scriptClass(func);
		}
		//       const idEventDef* evdef = func->eventdef; 
		const idTypeDef *ftype = func->type;
		if (ftype) {
			if (num>(ftype->NumParameters())) {
				return "<noarg>";
			}
			return getTypeName(ftype->GetParmType(num-1));
		} else {
			return "<paramtype>";
		}
	}
	return "<nofunct>";
}

idStr scriptSignature(const function_t* func ) {
	if (isScriptValid(func)) {
		idStr form = "";
		form.Append(scriptReturnType(func));
		form.Append("(");
		//gameLocal.Printf("getting scriptClass =%s ",form.c_str());
		//gameLocal.Printf("returned ret =%s",ret);
		form.Append(scriptClass(func));
		//gameLocal.Printf("getting done appending =%s ",form.c_str());
		form.Append(",'");
		form.Append(scriptFullname(func));
		form.Append("'");
		int arity = scriptArity(func);
		if (arity!=0) {
			form.Append("(");
			for (int ii=1;ii<=arity;ii++) {
				form.Append(scriptParameterType(func,ii));
				if (ii<arity)form.Append(",");
			}
			form.Append(")");
		}
		form.Append(",'");
		// gameLocal.Printf("getting scriptFormat");
		form.Append(scriptFormat(func));
		form.Append("')");
		return form;
	}
	return "<nofunct>";
}


const char* scriptParameterName(const function_t* func, int num) {
	if (isScriptValid(func)) {
		if (num<0) {
			return scriptName(func);
		}
		if (num<1) {
			return scriptClass(func);
		}
		//       const idEventDef* evdef = func->eventdef; 
		const idTypeDef *ftype = func->type;
		if (ftype) {
			if (num>(ftype->NumParameters())) {
				return "<noarg>";
			}
			return(ftype->GetParmName(num-1));
		} else {
			return "<paramname>";
		}
	}
	return "<nofunct>";
}

idVec3* chars_to_vector(const char* vstring) {
	idVec3 tempvar;
	if (sscanf(vstring,"%f %f %f",&tempvar.x,&tempvar.y,&tempvar.z)<3) gameLocal.Printf( "(throw format_vector \"%s\")\n", vstring );
	idEntity* ent = gameLocal.FindEntity(vstring);
	if (isValid(ent)) {
		return new idVec3(ent->GetPhysics()->GetOrigin());
	}
	return new idVec3(tempvar.x,tempvar.y,tempvar.z);
}

idEntity* int2Entity(int entnum) {
	if (entnum<1 || entnum>MAX_GENTITIES) return NULL;
	return gameLocal.entities[entnum-1];
}


idTypeDef* charToTypeDef(char type) {
	switch (type) {
	case D_EVENT_VOID :
		return &type_void;
	case D_EVENT_INTEGER :
		return &type_float;
	case D_EVENT_FLOAT :
		return &type_float;
	case D_EVENT_VECTOR :
		return &type_vector;
	case D_EVENT_STRING :
		return &type_string;
	case D_EVENT_ENTITY :
	case D_EVENT_ENTITY_NULL :
		return &type_entity;
	case D_EVENT_TRACE :
	default:
		// unsupported data type
		return &type_entity;
	}
}

idEntity* SearchEntity(const char* cstr) {
	if (!cstr) return 0;
	if (*cstr==0) return 0;
	idStr str = cstr;
	idEntity* check = gameLocal.FindEntity(str.c_str());
	if (isValid(check))	return check;
	if (str.CmpPrefix("$")) {
		str.Insert("$",0);
		idEntity* check = gameLocal.FindEntity(str.c_str());
		if (isValid(check))	return check;
	} else {
		idEntity* check = gameLocal.FindEntity((str.Right(str.Length()-1)).c_str());
		if (isValid(check))	return check;
	}
	int tempvar;
	if (sscanf(str.c_str(),"%d",&tempvar)==1) {
		return int2Entity(tempvar);
	}
	if (!str) return 0;
	return 0;
}
const function_t *scriptSearch(const char* target, idEntity* check, const char* fnname) {
	const function_t* func = NULL;
	if (isValid(check)) {
		func = findFunction(check->GetName(),fnname);
		if (isScriptValid(func)) return func;
		func = findFunction(check->GetClassname(),fnname);
		if (isScriptValid(func)) return func;
	}
	return findFunction(target,fnname);
}
#define FindCLRMethod(CCLASS,NAME,SIG) FindCLRMethod0(env,class_##CCLASS,#NAME,SIG)
jmethodID FindCLRMethod0(JNIEnv* env,jclass cls,char* name,char* proto) {
	clearExceptions(env);
	jmethodID methid = env->GetMethodID(cls,name,proto);
	if (!methid) {
		debugln("cant find interface on %s %s...",name,proto);
		clearExceptions(env);
	} else {
		debugln("Found interface %s %s...",name,proto);
	}
	return methid;
}
	#define EnsureJavaClass(CCLASS,STRING) (isValid(class_##CCLASS)?class_##CCLASS:(class_##CCLASS=javart_FindClass(env,STRING)))

#define CallCLRMethod(CCLASS,NAME,SIG,ARGS) (env->CallObjectMethod(object_##CCLASS,FindCLRMethod(CCLASS,NAME,SIG),ARGS))

jclass javart_FindClass(JNIEnv* env, const char* classname);
jobject getThreadGroup(JNIEnv*env) {
	if (!isValid(env)) return NULL;
	return CallCLRMethod(NativeDoomServer,getThreadGroup,"()Ljava/lang/ThreadGroup;",NULL);
}


JNIEnv* attachEnv(JNIEnv*env) {
	if (env) return env;
	jobject threadGroup = getThreadGroup(env);
	JavaVMAttachArgs args;
	args.version = env->GetVersion(); 
	args.name = "IGameLocal C++";
	args.group = threadGroup; 

	jint res = theJVM->AttachCurrentThread((void**)&env,(void*)&args);
	if (!(res==JNI_OK)) {
		debugln("bad result from attachEnv %d",res);
	}
	return env;
}

JNIEnv *JNU_GetEnv() {
	JNIEnv *env = NULL;
	theJVM->GetEnv((void **)&env,JNI_VERSION_1_4);
	if (!isValid(env)) return NULL;
	return attachEnv(env);
}

jclass javart_FindClass(JNIEnv* env, const char* classname) {
	jclass clazz = env->FindClass(classname);
	if (!clazz) {
		debugln("missing %s...",classname);
	} else {
		debugln("found %s...",classname);
	}
	return clazz;
}


const function_t *findFunction(const char* scope,const char* fnname) {
	const function_t* func = 0;
	idEntity* check = 0;
	if (!scope) {
		scope="entity";
	} else if (idStr::Cmp(scope,"")==0) {
		scope="entity";
	} else {
		if (!func) {
			idStr fnfind = scope;
			fnfind.Append("_");
			fnfind.Append(fnname);
			func = gameLocal.program.FindFunction(fnfind.c_str());
		}
		if (!func) {
			idStr fnfind = scope;
			fnfind.Append("::");
			fnfind.Append(fnname);
			func = gameLocal.program.FindFunction(fnfind.c_str());
		}
	}
	if (!func) {
		idStr fnfind = "";
		fnfind.Append("entity_");
		fnfind.Append(fnname);
		func = gameLocal.program.FindFunction(fnfind.c_str());
	}
	if (!func) {
		idStr fnfind = "";
		fnfind.Append("sys_");
		fnfind.Append(fnname);
		func = gameLocal.program.FindFunction(fnfind.c_str());
	}
	if (scope) {
		check = SearchEntity(scope);
		if (!func) {
			if (isValid(check))	func = check->scriptObject.GetFunction(fnname);
		}
	}
	if (!func) func = gameLocal.program.FindFunction(fnname);
	return func;
}

static char* JNI_NULL_STRING = "";


const char* jstring_to_chars(JNIEnv * env,jstring source) {
	// 
	if (isValid(source)) {
		int len = env->GetStringUTFLength(source);
		char* ret = (char * )env->GetStringUTFChars((jstring )source,0);
		// if (len<129) {
		return ret;
		// } else {
		// ret[127]=0;
		// return ret;
		// }
	} else {
		// wont real ver get here 
		return JNI_NULL_STRING;
	}
}

const char* jobject_to_chars(JNIEnv*env, jobject val) {
	return jstring_to_chars(env,(jstring)CallCLRMethod(NativeDoomServer,toString,"(Ljava/lang/Object;)Ljava/lang/String;",val));
}

const idCmdArgs* jarrayToCmdArgs(JNIEnv*env,jobjectArray argArray) {

	idCmdArgs* cargs = new idCmdArgs("",false);
	if (!argArray) return cargs;
	int len = env->GetArrayLength(argArray);
	for (int i = 0;i<len;i++) {

		jobject obj = env->GetObjectArrayElement(argArray,i);
		cargs->AppendArg(jobject_to_chars(env,obj));
	}//Number
	return cargs;
}
/*
cycexec uprint "la la la"
cycexec execForFloat "doesSee cyc_bot_1 light_1"
jcall cyc_bot_1 faceEntity player1 
jcall global doesSee cyc_bot_1 light_1
jcall global talk cyc_bot_1 "hi"
jcall cyc_bot_1 getName cyc_bot_1
jcall cyc_bot_1 lookAt player1 30
jcall cyc_bot_1 faceEntity player1 10
jcall cyc_bot_1 distanceTo player1
jcall global nameOf player1
jcall global distanceToEntity player1 cyc_bot_1
jcall cyc_bot_1 getOrigin
jcall global talk cyc_bot_1 "hi"
cycexe <entity1> <functname2> <a3> <b4>
cycexec talk cyc_bot_1 hi
*/
#define STRING const char*
STRING newIdStr(int size) {
// STRING eval = new STRING("\0\0\0\0");
	// eval->Clear();
	// eval->ReAllocate(size,false);
	// eval->Clear();
	return(STRING) malloc(size);
}
const char* etypeToString(etype_t t ) {
	switch (t) {
	case ev_void:
		return "ev_void";
	case ev_virtualfunction:
		return "*ev_virtualfunction";
	case ev_vector:
		return "v";	//"ev_vector";
	case ev_string:
		return "s";	//"ev_string";
	case ev_scriptevent:
		return "*ev_scriptevent";
	case ev_pointer:
		return "*ev_pointer";
	case ev_object:
		return "*ev_object";
	case ev_namespace:
		return "*ev_namespace";
	case ev_jumpoffset:
		return "*ev_jumpoffset";
	case ev_function:
		return "*ev_function";
	case ev_float:
		return "f";	//ev_float";
	case ev_field:
		return "*ev_field";
	case ev_error:
		return "*ev_error";
	case ev_entity:
		return "e";	//"ev_entity";
	case ev_boolean:
		return "b";	//"ev_boolean";
	case ev_argsize:
		return "*ev_argsize";
	default:
		return va("*ev%d",t);

	}
}
bool isScriptValid(const function_t* func);

const function_t *eventToFunction(const idEventDef* eevdef) {
	const function_t* func = NULL;
	for (int i=0;i<MAX_FUNCS;i++) {
		func=gameLocal.program.GetFunction(i);
		if (isScriptValid(func)) {
			const idEventDef* evdef = func->eventdef;
			if (evdef) {
				if (evdef==eevdef) {
					return func;
				}
			}
		}
	}
	return func;
}

const function_t *int2Function(jint i) {
	if (i<1) return NULL;
	if (i>MAX_FUNCS) return NULL;
	return gameLocal.program.GetFunction(i-1);
}

int isNonVoid(const function_t* func) {
	if (isScriptValid(func)) {
		const idTypeDef *ftype = func->type;
		const idEventDef *evdef = func->eventdef;
		// idVarDef *fdef = func->def;
		if (evdef) {
			char c = evdef->GetReturnType();
			if (c>0) return c;
		}
		if (ftype) {
			const idTypeDef *rtype = ftype->ReturnType();
			if (rtype) {
				const char* tname = rtype->Name();
				if (tname) {
					if (idStr::Cmp(tname,"void") == 0) return 0;
				}
			}
		}
		return 0;
	}
	return -1;
}


HINSTANCE java_find_vm(const char* home, const char* dir, const char* name) {
	if (!theJavaDll) {
		char* pathToJvm = (char*)malloc( 8192 );

		if (!theJavaDll) {
			sprintf(pathToJvm, "%s\\%s\\client\\%s", home, dir, name);
			theJavaDll = LoadLibrary( pathToJvm );
		}

		if (!theJavaDll) {
			sprintf(pathToJvm, "%s\\%s\\%s", home, dir, name);
			theJavaDll = LoadLibrary( pathToJvm );
		}
		if (!theJavaDll) {
			sprintf(pathToJvm, "%s\\%s\\server\\%s", home, dir, name);
			theJavaDll = LoadLibrary( pathToJvm );
		}
		if (theJavaDll)	debugln("Used JVM: %s",pathToJvm);
	}
	return theJavaDll;
}

JNIEnv* java_create_vm() {
	JNIEnv *env = NULL;

	if (!theJavaDll)theJavaDll = LoadLibrary("java.dll" );
	if (!theJavaDll)theJavaDll = LoadLibrary("jvm.dll" );

	if (!theJavaDll) {
		const char* JREHOME = getenv("JREHOME" );
		if (!JREHOME) JREHOME = "C:\\Program Files\\JRE";
		java_find_vm(JREHOME,"BIN","JVM.DLL");
		java_find_vm(JREHOME,"BIN","JAVA.DLL");
		java_find_vm(JREHOME,"LIB","JVM.DLL");
		java_find_vm(JREHOME,"LIB","JAVA.DLL");
	}
	if (!theJavaDll) {
		const char* JAVA_HOME = getenv("JAVA_HOME" );
		if (!JAVA_HOME)	JAVA_HOME = "C:\\Program Files\\Java";
		java_find_vm(JAVA_HOME,"JRE\\BIN","JVM.DLL");
		java_find_vm(JAVA_HOME,"JRE\\BIN","JAVA.DLL");
		java_find_vm(JAVA_HOME,"JRE\\LIB","JVM.DLL");
		java_find_vm(JAVA_HOME,"JRE\\LIB","JAVA.DLL");
		java_find_vm(JAVA_HOME,"BIN","JAVA.DLL");
		java_find_vm(JAVA_HOME,"BIN","JVM.DLL");
		java_find_vm(JAVA_HOME,"LIB","JAVA.DLL");
		java_find_vm(JAVA_HOME,"LIB","JVM.DLL");
	}

	if (!theJavaDll) {
		debugln("Cannot load the JVM DLL");
		return env;
	}
	jvmGetJVMs = (GetCreatedJavaVMs_t*)GetProcAddress( theJavaDll, "JNI_GetCreatedJavaVMs" );
	jvmCreateJVM = (CreateJavaVM_t*)GetProcAddress( theJavaDll, "JNI_CreateJavaVM" );
// find out how many JVM's are running (should be 0 or 1) 
	jsize bufLen = 10; 
	jint nVMs = -2;
	theJVM = (JavaVM *) malloc( sizeof(JavaVM) * bufLen ); 
	debugln("Obtaining theJVM.. ");
// load the JVM dynamically
	if (theJavaDll != NULL) {
		if (jvmGetJVMs != NULL && jvmCreateJVM != NULL) {
			debugln("jvmGetJVMs.. ");
			jsize status = jvmGetJVMs( &theJVM, bufLen, &nVMs); 
			if (status == JNI_OK) {
				debugln("There are %d VM(s) already running ", nVMs );
				if (nVMs>0) {
					theJVM->GetEnv((void **)&env,JNI_VERSION_1_4);
				}
			} else {
				debugln("jvmGetJVMs status = %d \n" ,status);
				return env;
				//goto doReturn;
			} 
			// if there is no VM running yet, launch one 
			if (nVMs == 0) {
				if (env) return env;
				char* cp = (char*)malloc( 8192 ); 
				sprintf(cp, "-Djava.class.path=%s;base\\classlib;.", getenv("CLASSPATH" ) );
				debugln("CLASSPATH=%s",cp);
				int nOpt = 0;
				options[nOpt++].optionString = cp; // user classes 
				// set native library path 
				options[nOpt++].optionString = "-Djava.library.path=.;c:\\doom3\\base\\lib;c:\\doom3\\base"; 
				// print JNI-related messages 
				options[nOpt++].optionString = "-verbose:jni"; 
				options[nOpt++].optionString = "-Xnoclassgc"; 
				options[nOpt++].optionString = "-Xmx1000m"; 
				//options[nOpt++].optionString = "-Xcheck:jni";
				// disable JIT 
				//options[nOpt++].optionString = "-Djava.compiler = NONE";
				vm_args.version = JNI_VERSION_1_6;
				vm_args.options = options;
				vm_args.nOptions = nOpt;
				vm_args.ignoreUnrecognized = JNI_TRUE;

				// Note that in the Java 2 SDK, there is no longer any need to call JNI_GetDefaultJavaVMInitArgs. 
				int status = jvmCreateJVM(&theJVM,(void **)&env, &vm_args);
				if (status == JNI_OK) {
					// JVM created okay? 
					debugln("Launching VM okay. \n" ); 
					return env;
				} else {
					debugln("Error starting VM: %d ", status ); 
					return env;
				}
			}
		}
	}
	debugln("JAVA VM IS OK!");
	return env;
}


jobject getGame(JNIEnv*env) {
	return CallCLRMethod(NativeDoomServer,getGameLocal,"()Ldaxclr/doom/IGameLocal;",NULL);
}
jstring chars_to_jstring(JNIEnv * env,const char* source, bool stringNull = true) {

	if (source) {
		return((jstring)env->NewStringUTF(source));
	} else {
		if (stringNull) {
			return((jstring)env->NewStringUTF((char * )"NULL"));
		} else
			return NULL;
	}
}

/*
 * Class: daxclr_doom_IGameLocal
 * Method: scriptNumber
 * Signature: (Ljava/lang/String;ILjava/lang/String;)I
 */
const function_t *scriptSearch(const char* target, idEntity* check, const char* fnname);

idEntity* int2Entity(int i);

JNIEXPORT jint JNICALL JAVA_scriptNumber(JNIEnv *env, jobject clazz, jstring target, jint entnum, jstring name) {
	const function_t* func = scriptSearch(jstring_to_chars(env,target),int2Entity(entnum),jstring_to_chars(env,name));
	if (isScriptValid(func)) {
		//int len = gameLocal.program.functions.Num();
		int len = MAX_FUNCS;
		for (int i=0;i<len;i++) {
			if (func==gameLocal.program.GetFunction(i)) {
				return i+1;
			}
		}
	}
	return 0;
}

jobjectArray cmdArgsToStringArray(JNIEnv* env,const idCmdArgs &args) {
	int len = args.Argc();
	jobjectArray jargs = env->NewObjectArray(len,EnsureJavaClass(Object,"java/lang/String"),0);
	for (int i=0;i<len;i++) {
		env->SetObjectArrayElement(jargs,i,chars_to_jstring(env,args.Argv(i)));
	}
	return jargs;
}

void CLR_CmdExec( const idCmdArgs &args ) {
	JNIEnv* env = JNU_GetEnv();
	jarray strArray = cmdArgsToStringArray(env,args);
	jobject result = CallCLRMethod(NativeDoomServer,invokeCommand,"([Ljava/lang/Object;)Ljava/lang/Object;",strArray);
	gameLocalPrintf(jobject_to_chars(env,result));
}

/*
 * Class: daxclr_doom_IGameLocal
 * Method: commandAdd
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL JAVA_commandAdd(JNIEnv *env, jobject clazz, jstring name) {
	idCmdArgs* cmdargs = new idCmdArgs(jstring_to_chars(env,name),false);
	idStr comment = "Java defined command ";
	comment.Append(cmdargs->Args(0,-1,true));
	debugln(comment.c_str());
	cmdSystem->AddCommand( cmdargs->Argv(0) , CLR_CmdExec, CMD_FL_GAME, comment.c_str());
}

jobject toJavaNull(JNIEnv* env) {
	return NULL;
}

jobject idEntityToObject(JNIEnv* env, idEntity* entity) {
	if (!isValid(entity)) return NULL;
	return CallCLRMethod(NativeDoomServer,toEntity,"(I)Ldaxclr/doom/IEntity;",entity->entityNumber+1);
}
jobject idThreadToObject(JNIEnv* env, idThread* entity) {
	if (!isValid(entity)) return NULL;
	return CallCLRMethod(NativeDoomServer,toThread,"(I)Ldaxclr/doom/ISys;",entity->GetThreadNum());
}

jobject idClassToObject(JNIEnv *env,idClass* entity) {
	if (!entity) return NULL;
	idEntity *ent = NULL;
	idThread *thrd = NULL;
	if (entity->GetType()->IsType( idEntity::Type )){
      ent = reinterpret_cast<idEntity*>(entity);
	  return idEntityToObject(env,ent);
	}
	if (entity->GetType()->IsType( idThread::Type )){
      thrd = reinterpret_cast<idThread*>(entity);
	  return idThreadToObject(env,thrd);
	}
	return CallCLRMethod(NativeDoomServer,toIdClass,"(J)Ldaxclr/doom/IClass;",(jlong)entity);
}


idVarDef* objectToVarDef(JNIEnv *env,jobject obj);
jobject idCmdArgsToObjectArray(JNIEnv *env,const idCmdArgs &args);

idVarDef* InvokeJava(JNIEnv *env,const char *fnclass,const char *fnname, idClass* scope, idThread* thread, const idCmdArgs &args) {

	jmethodID method_invokeJavaFn = 
		FindCLRMethod(NativeDoomServer,invokeJavaFn,
		"(Ljava/lang/String;Ljava/lang/String;Ldaxclr/doom/IClass;Ldaxclr/doom/ISys;[Ljava/lang/Object;)Ljava/lang/Object;");
	//idVarDef* returnDef = new idVarDef();
	if (!method_invokeJavaFn) {
		debugln("method_invokeJavaFn == NULL");
		return gameLocal.program.returnDef;
	}
	return objectToVarDef(env,env->CallObjectMethod(
		object_NativeDoomServer,
		method_invokeJavaFn,
		chars_to_jstring(env,fnclass),
		chars_to_jstring(env,fnname),
		idClassToObject(env,scope),
		idThreadToObject(env,thread),
		idCmdArgsToObjectArray(env,args)));
}

/*
 * Class: daxclr_doom_IGameLocal
 * Method: evalDoom
 * Signature: (Ljava/lang/String;)Ljava/lang/Object;
 */
idEntity* SearchEntity(const char* str);
JNIEXPORT jlong JNICALL JAVA_resolveVarDef(JNIEnv *env, jobject clazz, jstring name) {
	if (name==NULL)	return NULL;
	const char* str = jstring_to_chars(env,name);
	idEntity* check = SearchEntity(str);
	return (jlong)check;
}

/*
 * Class: daxclr_doom_evalConsole
 * Method: evalConsole
 * Signature: (Ljava/lang/String;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL JAVA_invokeDoomConsole(JNIEnv *env, jobject clazz, jstring cmd) {
	cmdSystem->BufferCommandText( CMD_EXEC_NOW, jstring_to_chars(env,cmd));
	return cmd;//TODO
}

/*
 * Class: daxclr_doom_IGameLocal
 * Method: entityNumber
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL JAVA_entityNumber(JNIEnv *env, jobject clazz, jstring name) {
	idEntity* check = SearchEntity(jstring_to_chars(env,name));
	if (isValid(check)) {
		return check->entityNumber+1;
	}
	return 0;
}
jobject vec3ToObject(JNIEnv *env,idVec3 vect) {
	return CallCLRMethod(NativeDoomServer,toPoint3D,"(FFF)Ldaxclr/doom/IVector;",(vect.x,vect.y,vect.z));
}

jobject vec4ToObject(JNIEnv *env,idVec4 vect) {
	return CallCLRMethod(NativeDoomServer,toColor,"(FFFF)Ldaxclr/doom/IVector;",(vect.x,vect.y,vect.z,vect.w));
}

jobject vecStringToObject(JNIEnv *env,idStr vect) {
	return CallCLRMethod(NativeDoomServer,toVector,"(Ljava/lang/String;)Ldaxclr/doom/IVector;",chars_to_jstring(env,vect.c_str()));
}



//i am trying to write a idVec2* getMouseXY(); setMouseXY(idVec2*); idVec2* getLastClickedXY(); setPoint(idVec2* xy, idVec4* color) :P 
idUserInterface* getFullGUI() {
	idUserInterface* gui = NULL;
	idPlayer *player = gameLocal.GetLocalPlayer();
	if (!isValid(player)) return gui;
	gui = player->ActiveGui();
	if (isValid(gui)) return gui;
	gui = player->hud;
	if (isValid(gui)) return gui;
	gui = player->focusUI;
	if (isValid(gui)) return gui;
	gui = player->objectiveSystem;
	return gui;
}


void setPointXY(int x,int y, idVec4 color) {
// idPlayer *player = gameLocal.GetLocalPlayer();
// renderView_t* player->GetRenderView();
// renderView_t* player->Get();
	idUserInterface* gui = getFullGUI();
	if (!isValid(gui)) {
// gui->SetCursor(
	}
}

void setMouseXY(float x, float y) {
	idUserInterface* gui = getFullGUI();
	if (isValid(gui)) {
		gui->SetCursor(x,y);
		gui->DrawCursor();
	}
	idPlayer *player = gameLocal.GetLocalPlayer();
	if (isValid(player)) {
		gui = player->cursor;
		if (isValid(gui)) {
			//gui->
		}
	}
}

idVec2* getMouseXY() {
	idPlayer *player = gameLocal.GetLocalPlayer();
	if (!isValid(player)) return new idVec2(-1.0,-1.0);
	idUserInterface* gui = getFullGUI();
	if (isValid(gui)) {
		//gui->IsInteractive()
		return new idVec2(gui->CursorX(),gui->CursorY());
	}
	gui = player->cursor;
	if (isValid(gui)) {
		return new idVec2(gui->CursorX(),gui->CursorY());
	}
	return new idVec2(player->oldMouseX,player->oldMouseX);
}
//+set net_serverDedicated 0 +set net_LANServer 1 +editor +set com_allowConsole 1 +exec botserver.cfg
//DAXMOO,_D3SDK,__DOOM__,GAME_DLL,WIN32,_DEBUG,_WINDOWS
idVec2* getMouseClickedXY() {
	idPlayer *player = gameLocal.GetLocalPlayer();
	if (!isValid(player)) return new idVec2(-1.0,-1.0);
	return getMouseXY();
	//player->idThread::Event_GetButtons()
}

JNIEXPORT void JNICALL JAVA_setMouseXY(JNIEnv *env, jobject clazz,jfloat x, jfloat y) {
	debugln("set mouse %f %f",x,y);
	setMouseXY(x,y);
}
JNIEXPORT void JNICALL JAVA_setPixel(JNIEnv *env, jobject clazz,jfloat x, jfloat y,jfloat r, jfloat g,jfloat b, jfloat a) {
	debugln("set pixel %f,%f color %f/%f/%f %f",x,y,r,b,g,a);
}
JNIEXPORT void JNICALL JAVA_setMouseImage(JNIEnv *env, jobject clazz,jstring image) {
	debugln("set mouse image %s",jstring_to_chars(env,image));
}
JNIEXPORT jobject JNICALL JAVA_getLastXY(JNIEnv *env, jobject clazz) {
	idVec2* vv = getMouseXY();
	idVec3* v3 = new idVec3(-1.0,-1.0,-1.0);
	if (isValid(vv)) {
		v3->x = vv->x;
		v3->y = vv->y;
		v3->z = gameLocal.GetLocalPlayer()->usercmd.buttons;
	}
	return vec3ToObject(env,*v3);
}
JNIEXPORT jobject JNICALL JAVA_getLastClickedXY(JNIEnv *env, jobject clazz) {
	idVec2* vv = getMouseXY();
	idVec3* v3 = new idVec3(-1.0,-1.0,-1.0);
	if (isValid(vv)) {
		v3->x = vv->x;
		v3->y = vv->y;
		v3->z = gameLocal.GetLocalPlayer()->oldButtons;
	}
	return vec3ToObject(env,*v3);
}
JNIEXPORT jobject JNICALL JAVA_getLastClickedObject(JNIEnv *env, jobject clazz) {
	return JAVA_getLastClickedXY(env,clazz);
}
JNIEXPORT jobject JNICALL JAVA_getCurrentGUI(JNIEnv *env, jobject clazz) {
	idUserInterface* gui = getFullGUI();
	if (gui) {
		return chars_to_jstring(env,gui->Name());
	}
	return 0;
}
JNIEXPORT void JNICALL JAVA_setGUI(JNIEnv *env, jobject clazz,jstring name) {
	const char* guiname = jstring_to_chars(env,name);
	idUserInterface* gui = uiManager->FindGui(guiname,true,false,false);
	gui->Activate(true,1000);
}


JNIEXPORT void JNICALL JAVA_exceptionClear(JNIEnv *env, jobject clazz) {
	clearExceptions(JNU_GetEnv());
}



/*
 * Class: daxclr_doom_NativeManager
 * Method: entityClass
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL JAVA_entityClass(JNIEnv *env, jobject clazz, jint entnum) {
	idEntity* check = int2Entity(entnum);
	if (isValid(check)) {
		return chars_to_jstring(env,check->GetClassname());
	}
	return chars_to_jstring(env,"<noent>");
}
/*
 * Class: daxclr_doom_NativeManager
 * Method: entityType
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL JAVA_entityType(JNIEnv *env, jobject clazz, jint entnum) {
	idEntity* check = int2Entity(entnum);
	if (isValid(check)) {
		return chars_to_jstring(env,check->GetEntityDefName());
	}
	return chars_to_jstring(env,"<noent>");
}
/*
 * Class: daxclr_doom_NativeManager
 * Method: entityName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL JAVA_entityName(JNIEnv *env, jobject clazz, jint entnum) {
	idEntity* check = int2Entity(entnum);
	if (isValid(check)) {
		return chars_to_jstring(env,check->GetName());
	}
	return chars_to_jstring(env,"<noent>");
}




idThread* int2Thread(jint num) {
	return idThread::GetThread(num);
}

/*
 * Class: daxclr_doom_NativeManager
 * Method: threadClass
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL JAVA_threadClass(JNIEnv *env, jobject clazz, jint entnum) {
	idThread* check = int2Thread(entnum);
	if (isValid(check)) {
		return chars_to_jstring(env,check->GetClassname());
	}
	return chars_to_jstring(env,"<nothread>");
}
/*
 * Class: daxclr_doom_NativeManager
 * Method: threadType
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jobject JNICALL JAVA_threadEntity(JNIEnv *env, jobject clazz, jint entnum) {
	idThread* check = int2Thread(entnum);
	if (isValid(check)) {
		idEntity* ent = check->Get_Dax_Interpreter()->eventEntity;
		return idEntityToObject(env,ent);
	}
	return chars_to_jstring(env,"<nothread>");
}
/*
 * Class: daxclr_doom_NativeManager
 * Method: threadName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL JAVA_threadName(JNIEnv *env, jobject clazz, jint entnum) {
	idThread* check = int2Thread(entnum);
	if (isValid(check)) {
		return chars_to_jstring(env,check->GetThreadName());
	}
	return chars_to_jstring(env,"<nothread>");
}
idThread* threadForName(const char* scope) {
	idThread *thread = NULL;
	if (stricmp(scope,"sys")==0) return idThread::CurrentThread();
	idList<idThread *>  threads = idThread::GetThreads();
	int len = threads.Num();
	while (len-->0) {
		thread = threads[len];
		if (thread) {
			const char* name = thread->GetThreadName();
			if (stricmp(scope,name)==0) {
				return thread;
			}
		}
	}
	return NULL;
}

JNIEXPORT jint JNICALL JAVA_threadNumber(JNIEnv *env, jobject clazz, jstring name) {
	idThread* thread =threadForName(jstring_to_chars(env,name));
	if (thread)	return thread->GetThreadNum();
	return -1;
}

JNIEXPORT jlong JNICALL JAVA_threadPointer(JNIEnv *env, jobject clazz, jint entnum) {
	return(jlong)int2Thread(entnum);
}

JNIEXPORT jlong JNICALL JAVA_createThreadPointer(JNIEnv *env, jobject clazz, jstring jname) {
	const char* name = jstring_to_chars(env,jname);
	idThread* thread =threadForName(name);
	if (thread==NULL) {
		thread = new idThread();
		thread->SetThreadName(name);
	}
	return(jlong)thread;
}

JNIEXPORT jobject JNICALL JAVA_threadState(JNIEnv *env, jobject clazz, jint entnum) {
	idThread* thread = int2Thread(entnum);
	if (!thread) return chars_to_jstring(env,"<nothread>");
	return chars_to_jstring(env,thread->GetStateName());
}


JNIEXPORT jlong JNICALL dictToPointer(idDict* dict) {
	return (jlong)(void*)dict;
	/*
	JNIEnv* env = JNU_GetEnv(); 
	jobject jdict = env->AllocObject(class_IdDict);
	env->SetLongField(jdict,env->GetFieldID(class_IdDict,"pointer","J"),(jlong)((void*)dict));
	return jdict;*/
}

JNIEXPORT jobject JNICALL JAVA_allocateObject(JNIEnv *env, jobject clazz, jclass name) {
	return env->AllocObject(name);
}

JNIEXPORT jobject JNICALL JAVA_constructObject(JNIEnv *env, jobject cl, jclass clazz, jobject method, jobjectArray params) {
	int len = env->GetArrayLength(params);
	std::vector<jvalue> args;
	args.resize(len);
	for (int i=0;i<len;i++)	args[i].l = env->GetObjectArrayElement(params,i);
	return env->NewObjectA(clazz,env->FromReflectedMethod(method),&args.front());
}

JNIEXPORT jobject JNICALL JAVA_callObjectMethod(JNIEnv *env, jobject cl, jobject obj, jobject method, jobjectArray params) {
	int len = env->GetArrayLength(params);
	std::vector<jvalue> args;
	args.resize(len);
	for (int i=0;i<len;i++)	args[i].l = env->GetObjectArrayElement(params,i);
	return env->CallObjectMethodA(obj,env->FromReflectedMethod(method),&args.front());
}
JNIEXPORT jobject JNICALL JAVA_callStaticObjectMethod(JNIEnv *env, jobject cl, jclass clazz, jobject method, jobjectArray params) {
	int len = env->GetArrayLength(params);
	std::vector<jvalue> args;
	args.resize(len);
	for (int i=0;i<len;i++)	args[i].l = env->GetObjectArrayElement(params,i);
	return env->CallObjectMethodA(clazz,env->FromReflectedMethod(method),&args.front());
}
JNIEXPORT jobject JNICALL JAVA_callNonvirtualObjectMethod(JNIEnv *env, jobject cl, jclass clazz, jclass obj, jobject method, jobjectArray params) {
	int len = env->GetArrayLength(params);
	std::vector<jvalue> args;
	args.resize(len);
	for (int i=0;i<len;i++)	args[i].l = env->GetObjectArrayElement(params,i);
	return env->CallNonvirtualObjectMethodA(obj,clazz,env->FromReflectedMethod(method),&args.front());
}

JNIEXPORT void JNICALL JAVA_callVoidMethod(JNIEnv *env, jobject cl, jobject obj, jobject method, jobjectArray params) {
	int len = env->GetArrayLength(params);
	std::vector<jvalue> args;
	args.resize(len);
	for (int i=0;i<len;i++)	args[i].l = env->GetObjectArrayElement(params,i);
	env->CallVoidMethodA(obj,env->FromReflectedMethod(method),&args.front());
}
JNIEXPORT void JNICALL JAVA_callStaticVoidMethod(JNIEnv *env, jobject cl, jclass clazz, jobject method, jobjectArray params) {
	int len = env->GetArrayLength(params);
	std::vector<jvalue> args;
	args.resize(len);
	for (int i=0;i<len;i++)	args[i].l = env->GetObjectArrayElement(params,i);
	env->CallVoidMethodA(clazz,env->FromReflectedMethod(method),&args.front());
}
JNIEXPORT void JNICALL JAVA_callNonvirtualVoidMethod(JNIEnv *env, jobject cl, jclass clazz, jclass obj, jobject method, jobjectArray params) {
	int len = env->GetArrayLength(params);
	std::vector<jvalue> args;
	args.resize(len);
	for (int i=0;i<len;i++)	args[i].l = env->GetObjectArrayElement(params,i);
	env->CallNonvirtualVoidMethodA(obj,clazz,env->FromReflectedMethod(method),&args.front());
}

JNIEXPORT jobject JNICALL JAVA_toReflectedField(JNIEnv *env, jobject cl, jclass clazz, jstring jname, jstring jsig) {
	const char* name = jstring_to_chars(env,jname);
	const char* sig = jstring_to_chars(env,jsig);
	jfieldID fieldID = env->GetFieldID(clazz,name,sig);
	if (isValid(fieldID)) {
		return env->ToReflectedField(clazz,fieldID,JNI_FALSE);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,sig);
		if (isValid(fieldID)) {
			return env->ToReflectedField(clazz,fieldID,JNI_TRUE);
		} else {
			idStr ex = name;
			ex.Append(" ");
			ex.Append(sig);
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}

JNIEXPORT void JNICALL JAVA_setFieldAsObject(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jstring jsig, jobject val ) {
	const char* name = jstring_to_chars(env,jname);
	const char* sig = jstring_to_chars(env,jsig);
	jfieldID fieldID = env->GetFieldID(clazz,name,sig);
	if (isValid(fieldID)) {
		env->SetObjectField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,sig);
		if (isValid(fieldID)) {
			env->SetStaticObjectField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Object");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jobject JNICALL JAVA_getFieldAsObject(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jstring jsig ) {
	const char* name = jstring_to_chars(env,jname);
	const char* sig = jstring_to_chars(env,jsig);
	jfieldID fieldID = env->GetFieldID(clazz,name,sig);
	if (isValid(fieldID)) {
		return env->GetObjectField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,sig);
		if (isValid(fieldID)) {
			return env->GetStaticObjectField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" ");
			ex.Append(sig);
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}

JNIEXPORT void JNICALL JAVA_setFieldAsShort(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jshort val ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"S");
	if (isValid(fieldID)) {
		env->SetShortField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"S");
		if (isValid(fieldID)) {
			env->SetStaticShortField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Short");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jshort JNICALL JAVA_getFieldAsShort(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"S");
	if (isValid(fieldID)) {
		return env->GetShortField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"S");
		if (isValid(fieldID)) {
			return env->GetStaticShortField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" Short");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}

JNIEXPORT void JNICALL JAVA_setFieldAsBoolean(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jboolean val ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"Z");
	if (isValid(fieldID)) {
		env->SetBooleanField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"Z");
		if (isValid(fieldID)) {
			env->SetStaticBooleanField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Boolean");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jboolean JNICALL JAVA_getFieldAsBoolean(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"Z");
	if (isValid(fieldID)) {
		return env->GetBooleanField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"Z");
		if (isValid(fieldID)) {
			return env->GetStaticBooleanField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" Boolean");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}

JNIEXPORT void JNICALL JAVA_setFieldAsByte(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jbyte val ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"B");
	if (isValid(fieldID)) {
		env->SetByteField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"B");
		if (isValid(fieldID)) {
			env->SetStaticByteField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Byte");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jbyte JNICALL JAVA_getFieldAsByte(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"B");
	if (isValid(fieldID)) {
		return env->GetByteField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"B");
		if (isValid(fieldID)) {
			return env->GetStaticByteField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" Byte");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}

JNIEXPORT void JNICALL JAVA_setFieldAsChar(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jchar val ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"C");
	if (isValid(fieldID)) {
		env->SetCharField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"C");
		if (isValid(fieldID)) {
			env->SetStaticCharField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Char");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jchar JNICALL JAVA_getFieldAsChar(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"C");
	if (isValid(fieldID)) {
		return env->GetCharField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"C");
		if (isValid(fieldID)) {
			return env->GetStaticCharField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" Char");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}
JNIEXPORT void JNICALL JAVA_setFieldAsInt(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jint val ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"I");
	if (isValid(fieldID)) {
		env->SetIntField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"I");
		if (isValid(fieldID)) {
			env->SetStaticIntField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Int");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jint JNICALL JAVA_getFieldAsInt(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"I");
	if (isValid(fieldID)) {
		return env->GetIntField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"I");
		if (isValid(fieldID)) {
			return env->GetStaticIntField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" Int");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}
JNIEXPORT void JNICALL JAVA_setFieldAsFloat(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jfloat val ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"F");
	if (isValid(fieldID)) {
		env->SetFloatField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"F");
		if (isValid(fieldID)) {
			env->SetStaticFloatField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Float");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jfloat JNICALL JAVA_getFieldAsFloat(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"F");
	if (isValid(fieldID)) {
		return env->GetFloatField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"F");
		if (isValid(fieldID)) {
			return env->GetStaticFloatField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" Float");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}
JNIEXPORT void JNICALL JAVA_setFieldAsDouble(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jdouble val ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"D");
	if (isValid(fieldID)) {
		env->SetDoubleField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"D");
		if (isValid(fieldID)) {
			env->SetStaticDoubleField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Double");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jdouble JNICALL JAVA_getFieldAsDouble(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"D");
	if (isValid(fieldID)) {
		return env->GetDoubleField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"D");
		if (isValid(fieldID)) {
			return env->GetStaticDoubleField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" Double");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}
JNIEXPORT void JNICALL JAVA_setFieldAsLong(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname, jlong val ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"D");
	if (isValid(fieldID)) {
		env->SetLongField(obj,fieldID,val);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"D");
		if (isValid(fieldID)) {
			env->SetStaticLongField(clazz,fieldID,val);
		} else {
			idStr ex = name;
			ex.Append(" Long");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
		}
	}
}

JNIEXPORT jlong JNICALL JAVA_getFieldAsLong(JNIEnv *env, jobject cl, jclass clazz, jobject obj, jstring jname ) {
	const char* name = jstring_to_chars(env,jname);
	jfieldID fieldID = env->GetFieldID(clazz,name,"D");
	if (isValid(fieldID)) {
		return env->GetLongField(obj,fieldID);
	} else {
		fieldID = env->GetStaticFieldID(clazz,name,"D");
		if (isValid(fieldID)) {
			return env->GetStaticLongField(clazz,fieldID);
		} else {
			idStr ex = name;
			ex.Append(" Long");
			env->ThrowNew(class_NoSuchFieldException,ex.c_str());
			return NULL;
		}
	}
}



idDict* int2Dict(jlong pdict) {
	return(idDict*)pdict;
}

/*
 * Class: daxclr_doom_NativeManager
 * Method: getClassSpawnArg
 * Signature: (ILjava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT jlong JNICALL JAVA_classSpawnArgs(JNIEnv *env, jobject clazz, jstring name) {
	const idDeclEntityDef* check = gameLocal.FindEntityDef(jstring_to_chars(env,name),false);
	if (isValid(check)) {
		return dictToPointer((idDict*)&check->dict);
	} else {
		return NULL;
	}
}
JNIEXPORT jlong JNICALL JAVA_entitySpawnArgs(JNIEnv *env, jobject clazz, jint entnum) {
	idEntity* check = int2Entity(entnum);
	if (isValid(check)) {
		return dictToPointer(&check->spawnArgs);
	} else {
		return NULL;
	}
}
JNIEXPORT jlong JNICALL JAVA_threadSpawnArgs(JNIEnv *env, jobject clazz, jint tnum) {
	idThread* check = idThread::GetThread(tnum);
	if (isValid(check)) {
		return dictToPointer(&check->spawnArgs);
	} else {
		return NULL;
	}
}
/*
 * Class: daxclr_doom_IGameLocal
 * Method: deleteClassSpawnArg
 * Signature: (ILjava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT jobject JNICALL JAVA_removeSpawnArg(JNIEnv *env, jobject clazz, jlong pdict, jstring key) {
	idDict* dict = int2Dict(pdict);
	const char* ckey = jstring_to_chars(env,key);
	if (isValid(ckey)) {
		const char* ret = dict->GetString(ckey,(const char*)NULL);
		if (isValid(ret)) {
			dict->Delete(ckey);
			return chars_to_jstring(env,ret);
		}
	}
	return NULL;
}

/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: dictGet
 * Signature: (ILjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jstring JNICALL JAVA_getSpawnArg(JNIEnv* env, jclass clz, jlong pdict, jstring key, jstring val) {
	idDict* dict = int2Dict(pdict);
	const char* ckey = jstring_to_chars(env,key);
	if (isValid(ckey)) {
		const char* ret = dict->GetString(ckey,(const char*)NULL);
		if (isValid(ret))return chars_to_jstring(env,ret);
	}
	return val;
}

/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: dictSet
 * Signature: (ILjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jstring JNICALL JAVA_setSpawnArg(JNIEnv* env, jclass clz, jlong pdict, jstring key, jstring val) {
	idDict* dict = int2Dict(pdict);
	const char* ckey = jstring_to_chars(env,key);
	const char* cval = jstring_to_chars(env,val);
	jstring value = chars_to_jstring(env,dict->GetString(ckey,NULL),false);
	dict->Set(ckey,cval);
	return value;
}


JNIEXPORT jlong JNICALL JAVA_allocateIdDict(JNIEnv* env, jclass clz) {
	return(jlong)(void*)(new idDict());
}


JNIEXPORT void JNICALL JAVA_deletePointer(JNIEnv* env, jclass clz, jlong pdict) {
	delete (void*)pdict;
}

/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: dictSet
 * Signature: (ILjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobjectArray JNICALL JAVA_getSpawnKeys(JNIEnv* env, jclass clz, jlong pdict) {
	idDict* dict = int2Dict(pdict);
	if (isValid(dict)) {
		int len = dict->GetNumKeyVals();
		jobjectArray jargs = env->NewObjectArray(len,class_String,0);
		for (int i=0;i<len;i++) {
			env->SetObjectArrayElement(jargs,i,chars_to_jstring(env,dict->GetKeyVal(i)->GetKey()));
		}
		return jargs;
	}
	return 0;
}


/****************************************************************************
 * Java declaration:
 * native public void handleException(); 
 *
 * Causes an exception in the JVM, but detects it and supresses it
 *
*/
JNIEXPORT void JNICALL Java_Native_handleException(JNIEnv *env, jobject obj) {
	jfieldID fid;
	jclass scope;
	jthrowable ex;
	// Get the class to which this object belongs
	scope = env->GetObjectClass(obj);
	// Attempt to get the ID of the 'nonexistent' member, which, not by 
	// chance, doesn't exist!
	fid = env->GetFieldID(scope, "nonexistent", "Ljava/lang/String;");
	// Check for ex
	ex = env->ExceptionOccurred();
	// ex is non-zero if an ex occurred
	if (ex) {
		// Just display a message
		debugln("Exception handled in Main.cpp: %i", ex);
		// Clear the ex so the JVM doesn't react to it

		// If you want to display a message about the ex,
		// in addition to clearing it, you call DescribeException.
		// This call will print out a description of the ex
		// and then clear the ex
		//
		// env->DescribeException();
	}
}

/****************************************************************************
 * Java declaration:
 * native public void printWXYZ(); 
 *
 * Prints out four members of the Native object, w, x, y, and z. This
 * function acts sort of like the traditional 'toString' methid in Java.
 * The members are declared in Native.java as such:
 *
 * public String w; // public Object
 * public int x; // public 
 * private int y; // private (no protection here)
 * public static int z; // public static
 *
 * The return value from each GetFieldID call should be checked. 
 * I don't check because I'm trying to keep the focus on the calls.
*/
JNIEXPORT void JNICALL Java_Native_printWXYZ(JNIEnv *env, jobject obj) {
	jint x, y, z;
	jstring w;
	jfieldID fid;
	jclass scope;
	// Get the class that obj belongs to
	scope = env->GetObjectClass(obj);
	// w is a String
	fid = env->GetFieldID(scope, "w", "Ljava/lang/String;");
	// Get the Object (String) w. Note the cast to a jstring object.
	// GetObject returns a jobject. C++ wants the cast.
	w = (jstring) env->GetObjectField(obj, fid);
	// x is a non-static public field
	fid = env->GetFieldID(scope, "x", "I");
	// Get the int
	x = env->GetIntField(obj, fid);
	// y is a non-static private field, same as public
	fid = env->GetFieldID(scope, "y", "I");
	// Get the int
	y = env->GetIntField(obj, fid);
	// z is a static public field, so call different methid
	fid = env->GetStaticFieldID(scope, "z", "I");
	// Get static field
	z = env->GetStaticIntField(scope, fid);
	// Convert Java string into char array for C++
	const char *buf = env->GetStringUTFChars(w, 0);
	// Sort of like the traditional 'toString' output
	debugln("[w = %s, x = %li, y = %li, z = %li]", buf, x, y, z);
	// Prevent memory leaks
	env->ReleaseStringUTFChars(w, buf);
}


idUserInterface* SearchInterface(const char* name) {
// virtual idUserInterface * FindGui( const char *qpath, bool32 autoLoad = false, bool32 needUnique = false, bool32 forceUnique = false ) = 0;
	idUserInterface* UI = uiManager->FindGui(name,true);
	return UI;
}

const idEventDef* GetEventDef(const function_t* func) {
	return func->eventdef;
}
char GetReturnType(const function_t* func) {
	return GetEventDef(func)->GetReturnType();
}
idTypeDef *GetTypeForEventArg( char argType ) {
	idTypeDef *type;
	switch (argType) {
	case D_EVENT_INTEGER :
		// this will get converted to int by the interpreter
		type = &type_float;
		break;
	case D_EVENT_FLOAT :
		type = &type_float;
		break;
	case D_EVENT_VECTOR :
		type = &type_vector;
		break;
	case D_EVENT_STRING :
		type = &type_string;
		break;
	case D_EVENT_ENTITY :
	case D_EVENT_ENTITY_NULL :
		type = &type_entity;
		break;
	case D_EVENT_VOID :
		type = &type_void;
		break;
	case D_EVENT_TRACE :
		// This data type isn't available from script
		type = NULL;
		break;
	default:
		// probably a typo
		type = NULL;
		break;
	}
	return type;
}
idTypeDef* GetReturnTypeDef(const function_t* func) {
	idCompiler compiler;
	return GetTypeForEventArg(GetReturnType(func));
}
void varDumpInfo(idVarDef* returnDef) {
	if (returnDef) {
		debugln("varDumpInfo (%p) for type: %s",returnDef,etypeToString(returnDef->Type()));
		etype_t typ = returnDef->Type();
		varEval_t ret = returnDef->value;
		switch (typ) {
		case ev_void:
			debugln("varDumpInfo (%p) = %s",returnDef,"VOID");
			break;
		case ev_string:
			debugln("varDumpInfo (%p) = %s",returnDef,ret.stringPtr);
			break;
		case ev_vector: {
				idVec3* vect = ret.vectorPtr;
				if (vect) {
					debugln("varDumpInfo (%p) = %f %f %f",returnDef,vect->x,vect->y,vect->z);
				} else {
					debugln("varDumpInfo (%p) = NULL vector",returnDef);
				}
				break;
			}
		case ev_float: {
				if (ret.floatPtr) {
					debugln("varDumpInfo (%p) = %f",returnDef,*ret.floatPtr);
				} else {
					debugln("varDumpInfo (%p) = NULL",returnDef);
				}
			}
			break;
		case ev_boolean:
			debugln("varDumpInfo (%p) = %d",returnDef,*ret.intPtr);
			break;
		case ev_entity: {
				idEntity* check = int2Entity(*ret.entityNumberPtr);
				if (isValid(check)) {
					debugln("varDumpInfo (%p) = %s",returnDef,check->GetName());
				} else {
					debugln("varDumpInfo (%p) = %s",returnDef,"$null_entity");
				}
				return;
			}
		default:
			debugln("varDumpInfo (%p) default = %d",returnDef,*ret.intPtr);
			break;
			//result = di.GetVariable( gameLocal.program.returnDef );
		}
	} else {
		debugln("varDumpInfo NULL");
	}
}
//-------------------------------------------------------------------------------------- 
// Description: Push a generic value onto the stack. 
// Parameters: The value to push onto the stack. 
// Returns: None. 
//-------------------------------------------------------------------------------------- 
/*template <typename t_Type> void idInterpreter::PushValue(const t_Type& k_rValue) {
 *((t_Type*)&localstack[localstackUsed]) = k_rValue; 
 localstackUsed += sizeof(k_rValue); 
}*/

idVarDef* GetVarDef(const char* type,const char* value) { // TODO make sure these "new"s get deleted later
	idVarDef* vardef = 0;
	if (idStr::Cmp(type,"void")==0) {
		return new idVarDef(&type_void);
	} else if (idStr::Cmp(type,"string")==0) {
		vardef = new idVarDef(&type_string); 
		vardef->SetString(value,true);
		return vardef;
	} else if (idStr::Cmp(type,"float")==0) {
		float floatval = 0;
		vardef = new idVarDef(&type_float);
		if (sscanf(value,"%f",&floatval)<1) {
			debugln("GetVarDef unparsable float = %s",value);
		}
		vardef->value.floatPtr = new float(floatval);
		return vardef;
	} else if (idStr::Cmp(type,"int")==0||idStr::Cmp(type,"integer")==0) {
		int intval = 0;
		vardef = new idVarDef(&type_boolean);
		if (sscanf(value,"%d",&intval)<1) {
			debugln("GetVarDef unparsable int = %s",value);
		}
		vardef->value.intPtr = new int(intval);
		return vardef;
	} else if (idStr::Cmp(type,"entity")==0) {
		vardef = new idVarDef(&type_entity);
		idEntity* check = SearchEntity(value);
		if (!check) {
			debugln("GetVarDef unparsable entity = %s",value);
			vardef->value.entityNumberPtr = new int(0);
		} else {
			vardef->value.entityNumberPtr = new int(check->entityNumber+1);
		}
		return vardef;
	} else if (idStr::Cmp(type,"vector")==0) {
		idVec3* vecval = new idVec3(0,0,0);	//TODO
		vardef = new idVarDef(&type_vector);
		debugln("PARSE VECTOR.......!!!");
		if (sscanf(value,"%f %f %f",&vecval->z,&vecval->y,&vecval->z)<3) {
			debugln("GetVarDef unparsable vector = %s",value);
		}
		vardef->value.vectorPtr = vecval;
		return vardef;
	} else {
		vardef = new idVarDef(&type_entity);
		idEntity* check = SearchEntity(value);
		if (!check) {
			debugln("GetVarDef unparsable %s = %s",type,value);
			vardef->value.entityNumberPtr = new int(0);
		} else {
			vardef->value.entityNumberPtr = new int(check->entityNumber+1);
		}
		return vardef;
	}
}

bool isJavaFn(JNIEnv *env, const char *scope,const char *fnname) {
	if (FindCLRMethod(NativeDoomServer,"isJavaFn","(Ljava/lang/String;Ljava/lang/String;)Z")) {
		jboolean res=env->CallBooleanMethod(EnsureJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer"),FindCLRMethod(NativeDoomServer,"isJavaFn","(Ljava/lang/String;Ljava/lang/String;)Z"),chars_to_jstring(env,scope),chars_to_jstring(env,fnname));
		return(res==JNI_TRUE);
	}

	debugln("debug: no isJavaFn to test '%s'",fnname);
	return false;
}
jobject idCmdArgsToObjectArray(JNIEnv *env,const idCmdArgs &args) {
	jobjectArray array = env->NewObjectArray(args.Argc(),class_String,chars_to_jstring(env,""));

	for (int i=0;i<args.Argc();i++) {
		env->SetObjectArrayElement(array,i,chars_to_jstring(env,args.Argv(i)));

	}
	return array;
}

idVarDef* objectToVarDef(JNIEnv *env,jobject obj) {
	idVarDef* varDef = 0;
	if (obj==NULL) {
		varDef = new idVarDef(&type_string);
		varDef->value.stringPtr = "NULL";
	} else if (env->IsInstanceOf(obj,EnsureJavaClass(Integer,"java/lang/Integer"))) {
		jint objval = env->CallIntMethod(object_NativeDoomServer,FindCLRMethod(NativeDoomServer,intValue,"(Ljava/lang/Object;)I") ,obj);
		varDef = new idVarDef(&type_float);
		varDef->value.floatPtr = new float(objval); 
		varDef->value.intPtr = new int(objval); 
// gameLocal.program.ReturnInteger(objval);
	} else if (env->IsInstanceOf(obj,EnsureJavaClass(Number,"java/lang/Number"))) {
		jfloat objval = env->CallFloatMethod(object_NativeDoomServer,FindCLRMethod(NativeDoomServer,floatValue,"(Ljava/lang/Object;)F" ),obj);
		varDef = new idVarDef(&type_float);
		varDef->value.floatPtr = new float(objval); 
		varDef->value.intPtr = new int(objval); 
// gameLocal.program.ReturnFloat(objval);
	} else if (env->IsInstanceOf(obj,EnsureJavaClass(IEntity,"daxclr/doom/IEntity"))) {
		jint objval = env->CallIntMethod(obj,env->GetMethodID(env->GetObjectClass(obj),"getEntityNumber","()I"));
		if (objval < 0)	objval = 0;
		varDef = new idVarDef(&type_entity);
		varDef->value.entityNumberPtr = new int(objval); 
		varDef->value.intPtr = new int(objval); 
		//gameLocal.program.ReturnEntity(int2Entity(objval));
	} else if (env->IsInstanceOf(obj,EnsureJavaClass(IScriptObject,"daxclr/java/IScriptObject"))) {
		jint objval = env->CallIntMethod(obj,env->GetMethodID(env->GetObjectClass(obj),"hashCode","()I"));
		varDef = new idVarDef(&type_entity);
		varDef->value.entityNumberPtr = new int(objval); 
		varDef->value.intPtr = new int(objval); 
		//gameLocal.program.ReturnEntity(int2Entity(objval));
	} else if (env->IsInstanceOf(obj,EnsureJavaClass(IVector,"daxclr/doom/IVector"))) {
		varDef = new idVarDef(&type_vector);
		//int size = env->CallIntMethod(EnsureJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer"),method_intValue,obj);
		jmethodID method_floatArrayValue = FindCLRMethod(NativeDoomServer,floatArrayValue,"(Ljava/lang/Object;I)F");
		float fx = env->CallFloatMethod(object_NativeDoomServer,method_floatArrayValue,obj,0);
		float fy = env->CallFloatMethod(object_NativeDoomServer,method_floatArrayValue,obj,1);
		float fz = env->CallFloatMethod(object_NativeDoomServer,method_floatArrayValue,obj,2);

		varDef->value.vectorPtr = new idVec3(fx,fy,fz);
		debugln("VECTOR.......!!!");
		//varDef->value.vectorPtr = cvect;
		//gameLocal.program.ReturnVector(*varDef->value.vectorPtr);
	} else {
		varDef = new idVarDef(&type_string);
		const char* chars = jobject_to_chars(env, env->CallObjectMethod(object_NativeDoomServer,FindCLRMethod(NativeDoomServer,toString,"(Ljava/lang/Object;)Ljava/lang/String;"),obj));
		varDef->value.stringPtr = const_cast<char*>(chars);
		//gameLocal.program.ReturnString(varDef->value.stringPtr);
	}
	return varDef;
}



jobject floatToObject(JNIEnv *env,float val) {
	return CallCLRMethod(NativeDoomServer,toFloat,"(F)Ljava/lang/Float;",val);
}

jobject intToObject(JNIEnv *env,int val) {
	return CallCLRMethod(NativeDoomServer,toInteger,"(I)Ljava/lang/Integer;",val);
}

jobject varToObject(JNIEnv *env,idVarDef* result) {
	if (!isValid(result)) return intToObject(env,0);// chars_to_jstring(env,"VOID");
	etype_t rtype = result->Type();
	switch (rtype) {
	case ev_void: {
			debugln("varToObject Void ");
			return intToObject(env,1); // chars_to_jstring(env,"VOID");
		}
	case ev_string: {
			debugln("varToObject stringPtr %s",result->value.stringPtr);
			return chars_to_jstring(env,result->value.stringPtr);
		}
	case ev_boolean: {
			int tempint = *result->value.intPtr;
			debugln("varToObject intPtr %i",tempint);
			return intToObject(env,tempint);
		}
	case ev_vector: {
			idVec3 tempvec = *result->value.vectorPtr;
			debugln("varToObject tempvec %f %f %f",tempvec.x,tempvec.y,tempvec.z);
			return vec3ToObject(env,tempvec);
		}
	case ev_float: {
			if (isValid(result->value.floatPtr)) {
				float f = *result->value.floatPtr;
				debugln("varToObject floatPtr %f",f);
				return floatToObject(env,f);
			}
			if (isValid(result->value.intPtr)) {
				float f = *((float*)result->value.intPtr);
				debugln("varToObject floatPtr->intPtr %f",f);
				return floatToObject(env,f);
			}
			debugln("varToObject 1000.0f");
			return floatToObject(env,1000.0f);
		}
	case ev_entity: {
			if (result->value.entityNumberPtr) {
				return idEntityToObject(env,int2Entity(*result->value.entityNumberPtr));
			} else {
				return idEntityToObject(env,int2Entity(*result->value.intPtr));
			}
		}
	default: {
			int retval=0;
			if (result->value.intPtr) {
				retval = *result->value.intPtr;
			}
			return chars_to_jstring(env,va("%s_%d",etypeToString(result->Type()),retval));
		}
	}
} 





void CLR_Call( const idCmdArgs &args ) {
	if (args.Argc() < 3) {
		debugln("jcall <scope> <cmdname> <args>");//jcall global talk cyc_bot_1 "hello there"
		return;
	}
	CLR_CmdExec(* new idCmdArgs(args.Args(1,-1,true),true));
}

void Daxmoo_CLR_Event_sayFrom( int clientNum, bool team, const char *name, const char *text, const char *sound ) {
	if (isValid(theJVM)) {
		JNIEnv* env = JNU_GetEnv();

		jmethodID jsay = FindCLRMethod(NativeDoomServer,"eventInGameSay","(ZLjava/lang/String;Ljava/lang/String;)V");
		if (jsay) env->CallVoidMethod(EnsureJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer"),jsay,team,chars_to_jstring(env,name),chars_to_jstring(env,text));

	}
}


void CLR_LoadAssembly( const idCmdArgs &args ) {
	if (args.Argc() < 2) {
		debugln("jload <typeNamespace> <assemblyName>");
		return;
	}
	JNIEnv* env = JNU_GetEnv();
	jmethodID method_NativeDoomServer_loadPlugin = FindCLRMethod(NativeDoomServer,loadPlugin,"(Ljava/lang/String;)Z");

	env->CallBooleanMethod(object_NativeDoomServer,method_NativeDoomServer_loadPlugin,chars_to_jstring(env,args.Args()));

// debugln(theDaxinterpreter.LoadNetType(args.Argv(1),args.Argv(2)));
}

void CLR_StartAssembly( const idCmdArgs &args ) {
	if (args.Argc() < 1) {
		debugln("jstart <assemblyName>");
		return;
	}
	JNIEnv* env = JNU_GetEnv();

	jmethodID method_startPlugin =0;
	env->CallVoidMethod(EnsureJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer"),method_startPlugin,chars_to_jstring(env,args.Args()));

// debugln(theDaxinterpreter.LoadNetType(args.Argv(1),args.Argv(2)));
}
void CLR_CmdRegister( const idCmdArgs &args ) {
	if (args.Argc() < 4) {
		debugln("jbind <cmdname> <assembly> <function> <comment>");
		return;
	}
	cmdSystem->AddCommand( args.Argv(1), CLR_CmdExec, CMD_FL_GAME, args.Args(1));
// debugln(theDaxinterpreter.BindCommand(args.Argv(1),args.Argv(2),args.Argv(3),args.Args(4)));
}
bool java_destroy_vm() {

	if (theJVM) {
		theJVM->DestroyJavaVM();
	}
	return true;
}

idStr scriptSignature(const function_t* func );
idStr scriptFullname(const function_t* func );
void CLR_List( const idCmdArgs &args ) {
	const char* match = "*";
	if (args.Argc()>0) {
		match = args.Argv(1);
	}
	//debugln("MAX_FUNCS=%d gameLocal.program.functions.Num()=%d",MAX_FUNCS,gameLocal.program.functions.Num());
	idStr fn = "";
	int len = MAX_FUNCS;
	//len = gameLocal.program.functions.Num();
	for (int i = 0;i<len;i++) {
		function_t* func = gameLocal.program.GetFunction(i);
		if (isScriptValid(func)) {
			fn.Empty();
			fn.Append(scriptSignature(func));
			if (fn.Length()>4) {
				if (idStr::Filter(match,scriptFullname(func),true)) {
// if(fn.Filter(match)) {
					debugln("%s.",fn.c_str());
				}
			}
		}
	}
}
// DAXMOO_JNI
//idInterpreter* theDaxInterpreter;
// 
void CLR_isImplemented( const idCmdArgs &args ) {
	JNIEnv *env = JNU_GetEnv();
	if (args.Argc()<2) {
		gameLocalPrintf("isJavaFn <scope>*<cmdname>");
	} else {
		if (isJavaFn(env,args.Argv(1),args.Argv(2))) {
			gameLocalPrintf("isJavaFn(%s,%s).",args.Argv(1),args.Argv(2));
		} else {
			gameLocalPrintf("!(isJavaFn(%s,%s)).",args.Argv(1),args.Argv(2));
		}
	}
}

const function_t* LocateFunction(idEntity* check, const char* fnname) {
	const function_t* func = 0;
	if (check) {
		func = check->scriptObject.GetFunction(fnname);
	}
	if (!func) {
		func = gameLocal.program.FindFunction(fnname);
	}
	//const idEventDef* evdef = idEventDef::FindEvent(fnname);
	//func = new function_t();
	return func;
}

int scriptArity(const function_t* func) {
	if (isScriptValid(func)) {
		const idTypeDef *ftype = func->type;
		const idEventDef *evdef = func->eventdef;
		//   idVarDef *fdef = func->def;
		if (ftype) {
			return ftype->NumParameters();
		}
		if (evdef) {
			return strlen(evdef->GetArgFormat());
		}
	}
	return -1;
}
/*
 * Class: daxclr_doom_IGameLocal
 * Method: invokeScript
 * Signature: (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL JAVA_invokeDoomObject(JNIEnv *env, jobject clazz, jobject jscope, jstring jfnname, jobjectArray pargs) {
	const idCmdArgs* cargs = jarrayToCmdArgs(env,pargs);
	const char* scope = jobject_to_chars(env,jscope);
	const char* fnname = jstring_to_chars(env,jfnname);

	const idEventDef* eventDef = idEventDef::FindEvent(fnname);
	idEntity* check = SearchEntity(scope);
	idThread* thread = threadForName(scope);
	idClass* obj = check;
	idInterpreter* interp = 0;
	const function_t* func = 0;
	idVarDef* retval = 0;

	if (obj==NULL) {
		obj = thread;
	}
	if (check) {
		func = check->scriptObject.GetFunction(fnname);
	}
	if (!func) {
		func = gameLocal.program.FindFunction(fnname);
	}

	if (!func && !eventDef) {
		env->ThrowNew(class_NoSuchMethodException,fnname);
	}
	if (thread) {
		interp = thread->Get_Dax_Interpreter();
	} else {
		interp = new idInterpreter();
	}

	if (eventDef) {
		if (obj->RespondsTo(*eventDef)) {
			retval = interp->InvokeEvent(obj,eventDef,*cargs);
		} else {

		}
	}

	if (!retval && func) {
		if (scriptArity(func) > cargs->Argc()+1) {
			idCmdArgs* copyargs = new idCmdArgs();
			copyargs->AppendArg(scope);
			int len = cargs->Argc();
			for (int i=0;i<len;i++) {
				copyargs->AppendArg(cargs->Argv(i));
			}
			cargs = copyargs;
		}
		retval = interp->InvokeScript(func,*cargs);
	}

	return varToObject(env,retval);
}



/*
c:\doom3\src\daxmoo\DaxInterpreter.cpp(2183) : error C2065: 'JAVA_getPointer' : undeclared identifier
c:\doom3\src\daxmoo\DaxInterpreter.cpp(2184) : error C2065: 'JAVA_threadName' : undeclared identifier
c:\doom3\src\daxmoo\DaxInterpreter.cpp(2185) : error C2065: 'JAVA_threadNumber' : undeclared identifier
c:\doom3\src\daxmoo\DaxInterpreter.cpp(2186) : error C2065: 'JAVA_threadEntity' : undeclared identifier
c:\doom3\src\daxmoo\DaxInterpreter.cpp(2187) : error C2065: 'JAVA_threadState' : undeclared identifier
c:\doom3\src\daxmoo\DaxInterpreter.cpp(2189) : error C2065: 'JAVA_createPointer' : undeclared identifier

 * Class: daxclr_doom_IGameLocal
 * Method: invokeEntity
 * Signature: (ILjava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL JAVA_invokeEntity(JNIEnv *env, jobject clazz, jint entnum, jstring jfnname, jobjectArray pargs) {
	idEntity* check = int2Entity(entnum);
	return JAVA_invokeDoomObject(env,clazz,chars_to_jstring(env,check->GetName()),jfnname,pargs);
}
/*
 * Class: daxclr_doom_IGameLocal
 * Method: invokeEntity
 * Signature: (ILjava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL JAVA_invokeThread(JNIEnv *env, jobject clazz, jint tnum, jstring jfnname, jobjectArray pargs) {
	idThread* check = idThread::GetThread(tnum);
	return JAVA_invokeDoomObject(env,clazz,chars_to_jstring(env,check->GetThreadName()),jfnname,pargs);
}
/*
 * Class: daxclr_doom_IGameLocal
 * Method: invokeFunction
 * Signature: (I[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL JAVA_invokeFunction(JNIEnv *env, jobject clazz, jint functnum, jobjectArray pargs) {
	//int len = 0;
	//jobjectArray ret = 0;
	const idCmdArgs* cargs = jarrayToCmdArgs(env,pargs);
	const function_t* func = int2Function(functnum);
	idInterpreter interp;
	return varToObject(env,interp.InvokeScript(func,*cargs));
}
/*
 * Class: daxclr_doom_IGameLocal
 * Method: isMapLoaded
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL JAVA_isMapLoaded(JNIEnv *env, jobject clazz) {

	return( gameLocal.FindEntity("world") != NULL );
}
/*
 * Class: daxclr_doom_IGameLocal
 * Method: scriptParameterType
 * Signature: (II)Ljava/lang/String;
 */
const char* scriptReturnType(const function_t* func);
const char* scriptParameterType(const function_t* func, int n);
JNIEXPORT jstring JNICALL JAVA_scriptParameterType(JNIEnv *env, jobject clazz, jint num, jint arg) {
	const function_t* func = int2Function(num);

	if (arg<0) {
		return chars_to_jstring(env,scriptReturnType(func));
	}
	return chars_to_jstring(env,scriptParameterType(func,arg));
}
const char* scriptName(const function_t* func);
const char* scriptParameterName(const function_t* func, int n);
JNIEXPORT jstring JNICALL JAVA_scriptParameterName(JNIEnv *env, jobject clazz, jint num, jint arg) {
	const function_t* func = int2Function(num);

	if (arg<0) {
		return chars_to_jstring(env,scriptName(func));
	}
	return chars_to_jstring(env,scriptParameterName(func,arg));
}
/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: scriptArity
 * Signature: (I)I
 */
int scriptArity(const function_t* func);
JNIEXPORT jint JNICALL JAVA_scriptArity(JNIEnv *env, jobject clazz, jint num) {
	const function_t* func = int2Function(num);
	return scriptArity(func);
}

/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: scriptName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL JAVA_scriptName(JNIEnv *env, jobject clazz, jint num) {
	return chars_to_jstring(env,scriptName(int2Function(num)));
}
JNIEXPORT jstring JNICALL JAVA_scriptReturnType(JNIEnv *env, jobject clazz, jint num) {
	return chars_to_jstring(env,scriptReturnType(int2Function(num)));
}
JNIEXPORT jstring JNICALL JAVA_scriptFullname(JNIEnv *env, jobject clazz, jint num) {
	return chars_to_jstring(env,scriptFullname(int2Function(num)));
}
/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: scriptName
 * Signature: (I)Ljava/lang/String;
 */
const char* scriptClass(const function_t* func);
JNIEXPORT jstring JNICALL JAVA_scriptClass(JNIEnv *env, jobject clazz, jint num) {
	return chars_to_jstring(env,scriptClass(int2Function(num)));
}

/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: scriptSignature
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL JAVA_scriptSignature(JNIEnv *env, jobject clazz, jint num) {
	return chars_to_jstring(env,scriptSignature(int2Function(num)).c_str());
}

/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: respondsTo
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL JAVA_respondsTo(JNIEnv *env, jobject clazz, jlong ppointer, jstring jfnname) {
	idClass* check = (idClass*)ppointer;
	const char* fnname = jstring_to_chars(env,jfnname);
	if (!isValid(check)) env->ThrowNew(class_NoSuchMethodException,va("reposndsTo on a null '%s' not found", fnname));
	return check->RespondsTo(*idEventDef::FindEvent(jstring_to_chars(env,jfnname)));
}

JNIEXPORT jobject JNICALL JAVA_invokeEvent(JNIEnv *env, jobject clazz, jlong ppointer, jstring jfnname, jobjectArray pargs ) {
	idClass* check = (idClass*)ppointer;
	const char* fnname = jstring_to_chars(env,jfnname);
	if (!isValid(check)) env->ThrowNew(class_NoSuchMethodException,va("invokeEvent on a null '%s' not found", fnname));

	const idCmdArgs* cargs = jarrayToCmdArgs(env,pargs);
	idInterpreter interp;

	const idEventDef* ev = idEventDef::FindEvent( fnname );
	if (!ev) {
		env->ThrowNew(class_NoSuchMethodException,va("Event '%s' not found", fnname ));
		return toJavaNull(env);
	}

	if (!check->RespondsTo(*ev)) {
		env->ThrowNew(class_NoSuchMethodException,va("Event '%s' not found on IClass type %s", fnname , check->GetType()));
		return toJavaNull(env);
	}

	idVarDef* retval = interp.InvokeEvent(check,ev,*cargs);	// CallEvent

	if (!isValid(retval)) debugln("bad retval from %s\n",fnname);
	return varToObject(env,retval);
}

/*
 * Class: daxclr_doom_NativeDoomServer
 * Method: gameLocalPrint
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL JAVA_printLocal(JNIEnv *env, jobject clazz, jstring output) {
	gameLocalPrintf("%s",jstring_to_chars(env,output));
}
JNIEXPORT void JNICALL JAVA_debugLocal(JNIEnv *env, jobject clazz, jstring output) {
	debugln("%s",jstring_to_chars(env,output));
}

JNIEXPORT jlong JNICALL JAVA_entityPointer(JNIEnv *env, jobject clazz, jint entnum) {
	return(jlong)int2Entity((int)entnum);
}

bool jSpawnEntityDef(JNIEnv* env, idDict &args,idEntity **ent, bool setDefaults ) {
	const char *classname;
	const char *spawn;
	idTypeInfo *cls;
	idClass *obj;
	idStr error;
	const char *name;



	if (args.GetString("name", "", &name )) {
		sprintf( error, " on '%s'", name);
	}

	args.GetString("classname", NULL, &classname );

	const idDeclEntityDef *def = gameLocal.FindEntityDef( classname, false );

	if (!def) {
		debugln("Unknown classname '%s'%s.", classname, error.c_str() );
		return false;
	}

	args.SetDefaults( &def->dict );

	// check if we should spawn a class object
	args.GetString("spawnclass", NULL, &spawn );
	if (spawn) {

		cls = idClass::GetClass( spawn );
		if (!cls) {
			debugln("Could not spawn '%s'. Class '%s' not found %s.", classname, spawn, error.c_str() );
			return false;
		}

		obj = cls->CreateInstance();
		if (!obj) {
			debugln("Could not spawn '%s'. Instance could not be created %s.", classname, error.c_str() );
			return false;
		}

		idTypeInfo *type;

		type = obj->GetType();
		obj->CallSpawnFunc( type );

		*ent = static_cast<idEntity *>(obj);



		// if ( ent && obj->IsType( idEntity::Type ) ) {

		// }

		return true;
	}

	// check if we should call a script function to spawn
	args.GetString("spawnfunc", NULL, &spawn );
	if (spawn) {
		const function_t *func = gameLocal.program.FindFunction( spawn );
		if (!func) {
			debugln("Could not spawn '%s'. Script function '%s' not found%s.", classname, spawn, error.c_str() );
			return false;
		}
		idThread *thread = new idThread( func );
		thread->DelayedStart( 0 );
		return true;
	}
	// CycSpawn
	debugln("%s doesn't include a spawnfunc or spawnclass%s.", classname, error.c_str() );
	return false;
}

static idEntity* makeNewEntityNow(JNIEnv *env,const char *classname,const char *name) {
	if (false) {
		cmdSystem->BufferCommandText( CMD_EXEC_NOW, va("spawn %s",classname));
		return NULL;
	}
	// const char *locname = jstring_to_chars(env,jlocationname); idEntity *locent = NULL;
	idPlayer *player = gameLocal.GetLocalPlayer(); 
	float yaw = player->viewAngles.yaw;
	idVec3 org = player->GetPhysics()->GetOrigin() + idAngles( 0, yaw, 0 ).ToForward() * 80 + idVec3( 0, 0, 1 ); 
// gameLocal.spawnArgs;
	gameLocal.spawnArgs.Set("classname", classname); 
	gameLocal.spawnArgs.Set("angle", va("%f", yaw + 180 ) ); 
	gameLocal.spawnArgs.Set("origin", org.ToString()); 
	gameLocal.spawnArgs.Set("name", name); 
	gameLocal.Printf("\nmaking %s \n",classname); 

	idEntity *ent; //gameLocal.FindEntity(name);
	if (!jSpawnEntityDef(env, gameLocal.spawnArgs,&ent,true )) {
		gameLocal.Printf("\nwarning not made %s\n",name);
		return NULL;
	}
	return ent;
}


JNIEXPORT jlong JNICALL JAVA_createEntityPointer(JNIEnv *env, jobject clazz, jstring jclassname, jstring jentityname, jstring jlocationname) {
	const char *classname = jstring_to_chars(env,jclassname); 

	const char *name = jstring_to_chars(env,jentityname); 
	// const char *locname = jstring_to_chars(env,jlocationname); idEntity *locent = NULL;

	idEntity *ent = makeNewEntityNow(env,classname,name);
	if (ent) {
		/*
		locent = gameLocal.FindEntity(locname);
		if (locent) {
		org = locent->GetPhysics()->GetOrigin();
		ent->SetOrigin(org);
		} else {
		if (sscanf(locname,"%f %f %f",&org.x,&org.y,&org.z)==3) {
		ent->SetOrigin(org);
		}
		ent->spawnArgs.Set("name", name );
		ent->SetName(name );
		} 
		*/
		gameLocal.Printf("\nsuccessfully made %s\n",name);
		return(jlong)ent;
	} else {
		gameLocal.Printf("\ncouldn't make %s\n",name);
		return 0;
	}
}

/* Allows it to be called more then once */
//if(EnsureJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer")) return true;
/*
Java Type Native Type Description 
-----------------------------------
boolean jboolean unsigned 8 bits 
byte jbyte signed 8 bits 
char jchar unsigned 16 bits 
short jshort signed 16 bits 
int jint signed 32 bits 
float jfloat 32 bits 
long jlong signed 64 bits 
double jdouble 64 bits 
void void N/A 

*/
/* 

class_jboolean,"java/lang/Boolean");
class_jbyte,"java/lang/Byte");
class_jchar,"java/lang/Char");
class_jshort,"java/lang/Short");
class_jint,"java/lang/Integer");
class_jlong,"java/lang/Long");
class_jfloat,"java/lang/Float");
*/

	#define RegisterNativeCLR(CCLASS,NAME,SIG) RegisterNativeCLR0(env,class_##CCLASS, #CCLASS, #NAME, SIG, (void*)&JAVA_##NAME)
void RegisterNativeCLR0(JNIEnv* env,jclass cls,char* cname,char* name,char* sig, void* fun) {
	clearExceptions(env);
	const JNINativeMethod nativeMthod = {name,sig,fun};
	int result = env->RegisterNatives(cls,&nativeMthod, 1);
	if (result!=JNI_OK) {
		debugln("ERROR: JNI: Cannot register native (%d) %s.%s%s",result,cname,nativeMthod.name,nativeMthod.signature);
		clearExceptions(env);
	} else {
		debugln("JNI: Registered native (%d) %s.%s%s",result,cname,nativeMthod.name,nativeMthod.signature);
	}
}

	#define ReloadJavaClass(CCLASS,STRING) ((class_##CCLASS=NULL)?EnsureJavaClass(CCLASS,STRING):EnsureJavaClass(CCLASS,STRING)) 


static int JavaSystemState = 0;

jobject initJavaSystemCompleted(JNIEnv* env, jobject stub) {
	debugln("initJavaSystemCompleted started");
	if (stub==NULL) {
		if (object_IGameLocal==NULL) JavaSystemState = 3;
	}	else 	{
		object_IGameLocal = stub;
		JavaSystemState = 4;
	}
	if (object_IGameLocal!=NULL) {
		class_IGameLocal = env->GetObjectClass(stub);
    	JavaSystemState = 5;
	}


	//invoke_object_method = FindCLRMethod(ScriptContext,"invokeJavaFn","(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
	//create_object_method = FindCLRMethod(ScriptContext,"createObject","(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
	//new_object_method = FindCLRMethod(ScriptContext,"newObject","(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;");
	//forget_object_method = FindCLRMethod(ScriptContext,"forgetObject","(I)Z");
	//get_object_field_method = FindCLRMethod(ScriptContext,"getObjectField","(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;");
	//set_object_field_method = FindCLRMethod(ScriptContext,"setObjectField","(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;");
	//find_class_method = FindCLRMethod(ScriptContext,"forName","(Ljava/lang/String;)Ljava/lang/Class;");
	//findObject_method = FindCLRMethod(ScriptContext,"findObject","(Ljava/lang/String;)Ljava/lang/Object;");
	//create_iter_obj = FindCLRMethod(ScriptContext,"createCollection","(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;");

//	method_IGameLocal_start = env->GetMethodID(EnsureJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer"),"start","()V");


/*	if (method_IGameLocal_start == 0) {
			debugln("Cannot FindCLRMethodID: IGameLocal.start()");
		} else {
			debugln("FindCLRMethodID (method_IGameLocal_start)");
			env->CallVoidMethod(class_DoomConsoleChannel,method_IGameLocal_start);
			debugln("ran method_IGameLocal_start");
		}
	debugln("startCLR complete");*/
	return stub;
}

JNIEXPORT jobject JNICALL JAVA_initCompletedCallback(JNIEnv *env, jobject clazz, jobject stub) {
	return initJavaSystemCompleted(attachEnv(env),stub);
}

void initJavaSystem(JNIEnv* env) {
	JavaSystemState = 1;
	debugln("initJavaSystem for C++ starting");
	//ReloadJavaClass(IScriptObject,"daxclr/java/IScriptObject");
	//ReloadJavaClass(IClass,"daxclr/doom/idclass/IClass");
	//ReloadJavaClass(IEntity,"daxclr/doom/idclass/IEntity");
	//ReloadJavaClass(ISys,"daxclr/doom/idclass/ISys");

	EnsureJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer");
	clearExceptions(env);

	RegisterNativeCLR(NativeDoomServer,debugLocal,"(Ljava/lang/String;)V");
	RegisterNativeCLR(NativeDoomServer,printLocal,"(Ljava/lang/String;)V");
	RegisterNativeCLR(NativeDoomServer,setPixel,"(FFFFFF)V"); 
	RegisterNativeCLR(NativeDoomServer,setMouseXY,"(FF)V"); 
	RegisterNativeCLR(NativeDoomServer,setMouseImage,"(Ljava/lang/String;)V"); 
	RegisterNativeCLR(NativeDoomServer,getLastClickedObject,"()Ljava/lang/Object;");
	RegisterNativeCLR(NativeDoomServer,getCurrentGUI,"()Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,setGUI,"(J)Z"); 

	//TODO	RegisterNativeCLR(NativeDoomServer,findGUI,"(Ljava/lang/String;)J"); 
	RegisterNativeCLR(NativeDoomServer,getLastClickedXY,"()Ldaxclr/doom/IVector;");
	RegisterNativeCLR(NativeDoomServer,getLastXY,"()Ldaxclr/doom/IVector;");
	RegisterNativeCLR(NativeDoomServer,isMapLoaded,"()Z");


	//ReloadJavaClass(IdDeclManager,"daxclr/doom/IdDeclManager");

	//ReloadJavaClass(ScriptContext,"daxclr/java/ScriptContext");

	RegisterNativeCLR(NativeDoomServer,deletePointer,"(J)V");

	//ReloadJavaClass(IClass,"daxclr/doom/IClass");
	RegisterNativeCLR(NativeDoomServer,respondsTo,"(JLjava/lang/String;)Z");
	RegisterNativeCLR(NativeDoomServer,invokeEvent,"(JLjava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");

	//ReloadJavaClass(IEntity,"daxclr/doom/IEntity");
	RegisterNativeCLR(NativeDoomServer,entitySpawnArgs,"(I)J");
	RegisterNativeCLR(NativeDoomServer,entityPointer,"(I)J");
	RegisterNativeCLR(NativeDoomServer,entityName,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,entityNumber,"(Ljava/lang/String;)I");
	RegisterNativeCLR(NativeDoomServer,entityClass,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,entityType,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,invokeEntity,"(ILjava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeDoomServer,createEntityPointer,"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)J");

	//ReloadJavaClass(ISys,"daxclr/doom/ISys");
	RegisterNativeCLR(NativeDoomServer,threadSpawnArgs,"(I)J");
	RegisterNativeCLR(NativeDoomServer,threadPointer,"(I)J");
	RegisterNativeCLR(NativeDoomServer,threadName,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,threadNumber,"(Ljava/lang/String;)I");
	RegisterNativeCLR(NativeDoomServer,threadEntity,"(I)I");
	RegisterNativeCLR(NativeDoomServer,threadState,"(I)Ljava/lang/Object;");
	RegisterNativeCLR(NativeDoomServer,invokeThread,"(ILjava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeDoomServer,createThreadPointer,"(Ljava/lang/String;)J");

	//ReloadJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer");

	//ReloadJavaClass(NativeDoomServer,"daxclr/ext/NativeDoomServer");
	//ReloadJavaClass(IVector,"daxclr/doom/IVector");

	RegisterNativeCLR(NativeDoomServer,allocateIdDict,"()J");
	RegisterNativeCLR(NativeDoomServer,getSpawnKeys,"(J)[Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,setSpawnArg,"(JLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,getSpawnArg,"(JLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,removeSpawnArg,"(JLjava/lang/String;)Ljava/lang/String;");

	RegisterNativeCLR(NativeDoomServer,scriptName,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,scriptFullname,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,scriptClass,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,scriptSignature,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,scriptParameterType,"(II)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,scriptReturnType,"(I)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,scriptParameterName,"(II)Ljava/lang/String;");
	RegisterNativeCLR(NativeDoomServer,scriptNumber,"(Ljava/lang/String;Ljava/lang/String;)I");
	RegisterNativeCLR(NativeDoomServer,scriptArity,"(I)I");

	RegisterNativeCLR(NativeDoomServer,classSpawnArgs,"(Ljava/lang/String;)J");
	RegisterNativeCLR(NativeDoomServer,commandAdd,"(Ljava/lang/String;Ljava/lang/String;)V");

	RegisterNativeCLR(NativeDoomServer,invokeFunction,"(I[Ljava/lang/Object;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeDoomServer,invokeDoomObject,"(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeDoomServer,invokeDoomConsole,"(Ljava/lang/String;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeDoomServer,resolveVarDef,"(Ljava/lang/String;)J");

	RegisterNativeCLR(NativeDoomServer,initCompletedCallback,"(Ldaxclr/doom/IGameLocal;)Ldaxclr/doom/INativeServer;");

//	RegisterNativeCLR(NativeDoomServer,defineFunction,"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I");

//	RegisterNativeCLR(NativeDoomServer,defineEvent,"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I");


		

/*
	ReloadJavaClass(NativeManager,"daxclr/java/NativeManager");

	RegisterNativeCLR(NativeManager,allocateObject,"(Ljava/lang/Class;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeManager,constructObject,"(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeManager,callObjectMethod,"(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeManager,callStaticObjectMethod,"(Ljava/lang/Class;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeManager,callNonvirtualObjectMethod,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeManager,callVoidMethod,"(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)V");
	RegisterNativeCLR(NativeManager,callStaticVoidMethod,"(Ljava/lang/Class;Ljava/lang/reflect/Method;[Ljava/lang/Object;)V");
	RegisterNativeCLR(NativeManager,callNonvirtualVoidMethod,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)V");
	RegisterNativeCLR(NativeManager,getFieldAsObject,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
	RegisterNativeCLR(NativeManager,setFieldAsObject,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
	RegisterNativeCLR(NativeManager,getFieldAsDouble,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)D");
	RegisterNativeCLR(NativeManager,setFieldAsDouble,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;D)V");
	RegisterNativeCLR(NativeManager,getFieldAsInt,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)I");
	RegisterNativeCLR(NativeManager,setFieldAsInt,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;I)V");
	RegisterNativeCLR(NativeManager,getFieldAsByte,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)B");
	RegisterNativeCLR(NativeManager,setFieldAsByte,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;B)V");
	RegisterNativeCLR(NativeManager,getFieldAsLong,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)J");
	RegisterNativeCLR(NativeManager,setFieldAsLong,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;J)V");
	RegisterNativeCLR(NativeManager,getFieldAsShort,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)S");
	RegisterNativeCLR(NativeManager,setFieldAsShort,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;S)V");
	RegisterNativeCLR(NativeManager,getFieldAsBoolean,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)Z");
	RegisterNativeCLR(NativeManager,setFieldAsBoolean,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;Z)V");
	RegisterNativeCLR(NativeManager,getFieldAsFloat,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)F");
	RegisterNativeCLR(NativeManager,setFieldAsFloat,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;F)V");
	RegisterNativeCLR(NativeManager,getFieldAsChar,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)C");
	RegisterNativeCLR(NativeManager,setFieldAsChar,"(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;C)V");
*/
	/*ReloadJavaClass(Method,"java/lang/reflect/Method");
	ReloadJavaClass(Class,"java/lang/Class");
	ReloadJavaClass(Object,"java/lang/Object");
	ReloadJavaClass(String,"java/lang/String");
	ReloadJavaClass(Number,"java/lang/Number");
	ReloadJavaClass(Integer,"java/lang/Integer");
	ReloadJavaClass(NoSuchFieldException,"java/lang/NoSuchFieldException");
	ReloadJavaClass(NoSuchMethodException,"java/lang/NoSuchMethodException");
*/


	/*debugln("initJavaSystem running init()");
	JavaSystemState = 2;
	if (method_IGameLocal_start == NULL) {
		method_IGameLocal_start = FindCLRMethod(DoomConsoleChannel,"init","()V");
		if (method_IGameLocal_start == 0) {
			debugln("Cannot FindCLRMethodID (method_IGameLocal_start)");
		} else {
			debugln("FindCLRMethodID (method_IGameLocal_start)");
			env->CallVoidMethod(class_DoomConsoleChannel,method_IGameLocal_start);
			debugln("ran method_IGameLocal_start");
		}
	}
	*/

/*	FindCLRMethod(NativeDoomServer,toEntity,"(I)Ldaxclr/doom/IEntity;");
	FindCLRMethod(NativeDoomServer,loadPlugin,"(Ljava/lang/String;)Z");
	FindCLRMethod(NativeDoomServer,startPlugin,"(Ljava/lang/String;)Z");
	FindCLRMethod(NativeDoomServer,evalJavaCommand,"([Ljava/lang/Object;)Z");

	FindCLRMethod(NativeDoomServer,invokeJavaFn,"(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");

//	method_toNull = FindCLRMethod(ScriptContext,"toNull","()Ljava/lang/Object;");

	FindCLRMethod(NativeDoomServer,toObject,"(Ljava/lang/String;)Ljava/lang/Object;");
	FindCLRMethod(NativeDoomServer,toString,"(Ljava/lang/Object;)Ljava/lang/String;");
	FindCLRMethod(NativeDoomServer,floatValue,"(Ljava/lang/Object;)F");
	FindCLRMethod(NativeDoomServer,floatArrayValue,"(Ljava/lang/Object;I)F");
	FindCLRMethod(NativeDoomServer,intValue,"(Ljava/lang/Object;)I");
	FindCLRMethod(NativeDoomServer,toInteger,"(I)Ljava/lang/Integer;");
	FindCLRMethod(NativeDoomServer,toFloat,"(F)Ljava/lang/Float;");
	FindCLRMethod(NativeDoomServer,toVector,"(FFF)Ldaxclr/doom/IVector;");
*/
	//EnsureJavaClass(IDoomServer,"daxclr/doom/IDoomServer");

	
	clearExceptions(env);
	object_NativeDoomServer = env->NewObject(class_NativeDoomServer,env->GetMethodID(class_NativeDoomServer, "<init>", "()V"));
//	jmethodID method_getSelf = NULL ; //env->GetMethodID(class_NativeDoomServer,"init","()V");
	clearExceptions(env);
//	if (method_getSelf)  
//	{
  //    	env->CallVoidMethod(class_NativeDoomServer,method_getSelf);
     	debugln("initJavaSystem completed now waiting for initCompletedCallback() from java");
//	} else {
 //     	debugln("init() not found");
//	}	JavaSystemState = 3;
	clearExceptions(env);
}

JNIEnv* java_sync_vm() {
	debugln("java_sync_vm begining");
	JNIEnv* env = java_create_vm();

	if (!isValid(env)) {
		debugln("JNIEnv == NULL (Bad)");
		return env;
	} else {
		debugln("JNIEnv != NULL (Good)");
	}
	if (object_DoomNativeConsole==NULL)
	{
		}
	if (JavaSystemState==0) {
		initJavaSystem(env);
	}
	return env;
}

void Daxmoo_CLR_OnGameLoad() {
	#ifdef DAXMOO_CLR_INIT
	java_sync_vm();
	#endif //DAXMOO_CLR
	// if (true) return;
	idToken token;
	idToken token2;
	if (declManager) {
		int num = declManager->GetNumDecls(DECL_ENTITYDEF); 
		for (int i = 0 ;i<num;i++) { // becasue there is support for editing software that edits the contents of idDeclEntityDef then i call a reparse they provided safe functions for it but somehow fogot to give non const access.. 
			//idDeclEntityDef* decl = static_cast<idDeclEntityDef*>(declManager->DeclByIndex(DECL_ENTITYDEF,i,false)); 
			idDeclEntityDef * decl = static_cast<idDeclEntityDef *>( const_cast<idDecl *>( declManager->DeclByIndex( DECL_ENTITYDEF, i, false ) ) );
			if (!decl) continue;
			int insertAtChar = decl->GetTextLength();
			char* declText = new char[insertAtChar*2+1024]; declText[0]='\0'; declText[insertAtChar]='\0';
			decl->GetText(declText);
			if (!*declText)	continue;
			if (strstr(declText,"\"inherit")>0) {
				//idDict dict;// = decl->dict;
				idLexer src;
				src.LoadMemory( declText, decl->GetTextLength(), decl->GetFileName(), decl->GetLineNum() );
				src.SetFlags( DECL_LEXER_FLAGS );
				src.SkipUntilString("{" );
				insertAtChar = src.GetFileOffset()+1;
				int inherits = 0;
				while (1) {
					if (!src.ReadToken( &token )) break;
					if (!token.Icmp("}" )) {
						/*
						const char* defname = decl->GetName();
						// dict.Set("classname",defname);
						insertAtChar = src.GetFileOffset()-1;
						int sz = dict.GetNumKeyVals();
						for (int kvi=0;kvi<sz;kvi++) {
						const idKeyValue* kv = dict.GetKeyVal(kvi);
						if (kv!=NULL) {
						const char* key = kv->GetKey().c_str();
						const char* value = kv->GetValue().c_str();
						if (value) sprintf(&declText[insertAtChar]," \"%s\" \"%s\"\n",key,value);
						else sprintf(&declText[insertAtChar]," \"%s\" \"\"\n",key);
						insertAtChar += strlen(&declText[insertAtChar]);
						}
						}
						sprintf(&declText[insertAtChar],"}\n");
						decl->Invalidate();
						decl->SetText(declText);
						decl->EnsureNotPurged();
						*/
						break;
					}
					if (!token.IcmpPrefix("inherit")) {
						if (!src.ReadToken( &token2 )) {
							src.Warning("Unexpected end of file" );
							break;
						}
						//const char* parentName = token2.c_str();
						//char* charsAllocd = new char[100];
						//sprintf(charsAllocd,"mud_%s%d",token.c_str(),++inherits);
						//good lord, this is what *worked*
						// (&(*(const_cast<idDeclEntityDef*>(decl))).dict)->Set(token,token2);
						//(const_cast<idDict*>(&(*(static_cast<const idDeclEntityDef *>(declManager->DeclByIndex(DECL_ENTITYDEF,i,false)))).dict))->Set(token,token2);
						(const_cast<idDict*>(&decl->dict))->Set(token,token2);

						/* const idDeclEntityDef *parent = static_cast<const idDeclEntityDef *>( declManager->FindType(DECL_ENTITYDEF, parentName, false ) );
						if (!parent) {
						common->Warning("Unknown entity definition '%s'", token2.c_str() );
						continue;
						} else if (parent->GetState() == DS_DEFAULTED) {
						common->Warning("inherited entity definition '%s' defaulted", token2.c_str() );
						continue;
						} else { // parser.Warning("'%s' inheriting '%s'", token.c_str(),token2.c_str() );
						//dict.SetDefaults(&parent->dict );
						//decl->dict.SetDefaults(&parent->dict );
						}*/
					}
				}
				//decl->dict = dict;
				// decl->dict.Set(token,token2);
			}
			delete declText;
		}
	}
}

void jinit2( const idCmdArgs &args ) {
	initJavaSystemCompleted(java_sync_vm(),0);
}

void CLR_Reload( const idCmdArgs &args ) {
	java_sync_vm();
}

void CLR_Exit( const idCmdArgs &args ) {
	if (theJVM) {
		theJVM->DetachCurrentThread();
		theJVM->DestroyJavaVM();
	}
	theJVM=NULL;
}

void Daxmoo_CLR_Init() {
	debugln("Daxmoo_CLR_Init()\n");

	// DaxObject* daxo = new DaxObject();
	// check to see if jvm loaded correctly. 
	// #ifdef DAXMOO_DOTNET
	// DaxControl* theControl = new DaxControl();
	// DaxObject* daxo = DaxObject::GetStaticObject();
	// daxo->SetControl(theControl);
	// #endif
	//daxo->SetGameLocal(&gameLocal);
	// if(!theDaxInterpreter) theDaxInterpreter = new idInterpreter();
	//theDaxinterpreter.InitializeComponent();
	// theDaxinterpreter.theStaticCLR = theDaxInterpreter; 
	// debugln("theStaticCLR = %p",theDaxinterpreter.theStaticCLR);
	// theDaxinterpreter.SetGameLocal(&gameLocal); 

	cmdSystem->AddCommand("jinit", CLR_Reload, CMD_FL_GAME, "jinit ensures JVM is running"); 
	cmdSystem->AddCommand("jinit2", jinit2, CMD_FL_GAME, "startCLR2 <scope>*<cmdname>"); 
	cmdSystem->AddCommand("jexit", CLR_Exit, CMD_FL_GAME, "jexit ensures JVM is unloaded"); 
	cmdSystem->AddCommand("jload", CLR_LoadAssembly, CMD_FL_GAME, "jload CycLBot daxclr.doom.agent.DoomIrcBot doomStart CycLBot sw2.de.quakenet.org #sscar.priv scarred"); 
	cmdSystem->AddCommand("jstart", CLR_StartAssembly, CMD_FL_GAME, "jstart CycLBot daxclr.doom.agent.DoomIrcBot doomStart CycLBot sw2.de.quakenet.org #sscar.priv scarred"); 
	cmdSystem->AddCommand("jbind", CLR_CmdRegister, CMD_FL_GAME, "jbind <cmdname> <assembly> <function> <comment>"); 
	cmdSystem->AddCommand("jcall", CLR_Call, CMD_FL_GAME, "jcall <scope> <cmdname> <args>"); 
	cmdSystem->AddCommand("jlist", CLR_List, CMD_FL_GAME, "jlist <scope>*<cmdname>");
	cmdSystem->AddCommand("isJavaFn", CLR_isImplemented, CMD_FL_GAME, "isJavaFn <scope>*<cmdname>"); 

	


// const idCmdArgs* initcmd = new idCmdArgs("jcall init c:\\doom3\\base\\boot.lisp",false);
// CLR_Call(*initcmd);
	// 
}

#endif// DAXMOO_JNI












