#pragma once

#ifdef assert
#undef assert
#define assert(X) if (X){}else assertln("Assert Failed: %s\n%s(%d) ",#X,__FILE__,__LINE__ )
#endif 

#include "DaxCommon.h"

/*
class DaxInterpreter : idInterpreter {

public:


    idVarDef* InvokeScript(const char* cls, const char *fnname, const idCmdArgs &args);
    idVarDef* InvokeJava(const char* cls, const char *fnname, const idCmdArgs &args);

    //static DaxInterpreter* theDaxInterpreter;

    //static const idEventDef* DaxInterpreter::GetEventDef(const function_t functn);
    //static char DaxInterpreter::GetReturnType(const function_t functn);
    //static idTypeDef* DaxInterpreter::GetReturnTypeDef(const function_t functn);
    //static const char* DaxInterpreter::GetParamTypes(const function_t functn) ;
    //int CallFunctionCmdArgs( const function_t k_pfnScript, const idCmdArgs &args, int argNum=0);
    //void CallFunction(const function_t k_pfnScript, idStr strFormat, va_list& rArgumentList ); 
    //void CallFunctionVoid(const function_t k_pfnScript, idStr strFormat, va_list& rArgumentList ); 

    DaxInterpreter(void) {
    }
    ~DaxInterpreter(void) {
    }


protected: 

    void PushInt(int nValue); 
    void PushFloat(float fValue); 
    void PushVector(const idVec3& k_rv3Value); 
    void PushEntity(const idEntity* k_pEntity); 
    int PushCmdArgs(idStr strFormat, const idCmdArgs &args, int argNum = 0);

protected: 
    template <typename t_Type> 
    void PushValue(const t_Type& k_rValue); 
};

*/
//extern DaxInterpreter* theDaxInterpreter;

//}
#endif //!_DAXINTERPRETER_H_
#endif // DAXMOO_JNI



