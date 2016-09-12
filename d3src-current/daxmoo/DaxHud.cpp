// Copyright (C) 2005 Daxtron/Logicmoo 
//
#include "../idlib/precompiled.h"
//#include "../game/script/Script_Interpreter.h"
//#include "idInterpreter.h"
//#include "DaxControl.h"
#pragma hdrstop
#ifdef DAXMOO_TODO

float wasFloat(int f) {
    return *((float*)&f);
}
float wasInt(float f) {
    return *((int*)&f);
}

bool asBool(int f) {
    float fzero = 0.0f; int fizero = *((int*)&fzero);    //01111111000000000000000000000000
    unsigned int uzero = 0; int uizero = *((int*)&uzero);
    if (f==0) return false;
    if (f==fizero) return false;
    if (f==uizero) return false;
    return true;
}

EVDEFNAMEOBJ(idThread,dict,Create)() {
    idDict* state = new idDict();
    idThread::ReturnInt((unsigned int)state);
}
EVDEFNAMEOBJ(idThread,dict,Delete)(idDict* ui,  const char* a ) {
    ui->Delete(a);
}
EVDEFNAMEOBJ(idThread,dict,SetString)(idDict* ui,  const char* a, const char* b ) {
    ui->Set(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetInt)(idDict* ui,  const char* a,int b ) {
    ui->SetInt(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetFloat)(idDict* ui, const char* a,float b ) {
    ui->SetFloat(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetBool)(idDict* ui,  const char* a,bool32 b ) {
    ui->SetBool(a,asBool(b));
}
EVDEFNAMEOBJ(idThread,dict,SetVector)(idDict* ui,  const char* a,const idVec3 &b ) {
    ui->SetVector(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetVec4)(idDict* ui,  const char* a,const idVec4& b ) {
    ui->SetVec4(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetVec2)(idDict* ui,  const char* a,const idVec2& b ) {
    ui->SetVec2(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetAngles)(idDict* ui,  const char* a,const idAngles& b ) {
    ui->SetAngles(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetMatrix)(idDict* ui,  const char* a,const idMat3& b ) {
    ui->SetMatrix(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetDefaults)(idDict* ui, idDict* a ) {
    ui->SetDefaults(a);
}

EVDEFNAMEOBJ(idThread,dict,GetNumKeyVals)(idDict* ui) {
    idThread::ReturnInt(ui->GetNumKeyVals());
}
EVDEFNAMEOBJ(idThread,dict,GetAngles)(idDict* ui , const char* a) {
    idThread::ReturnInt((unsigned int)&ui->GetAngles(a));
}
EVDEFNAMEOBJ(idThread,dict,GetMatrix)(idDict* ui , const char* a) {
    idThread::ReturnInt((unsigned int)&ui->GetMatrix(a));
}
EVDEFNAMEOBJ(idThread,dict,GetString)(idDict* ui, const char* a) {
    idThread::ReturnString(ui->GetString(a));         // jobject
}
EVDEFNAMEOBJ(idThread,dict,GetInt)(idDict* ui, const char* a ) {
    idThread::ReturnInt(ui->GetInt(a));
}
EVDEFNAMEOBJ(idThread,dict,GetFloat)(idDict* ui, const char* a ) {
    idThread::ReturnFloat(ui->GetFloat(a));
}
EVDEFNAMEOBJ(idThread,dict,GetBool)(idDict* ui, const char* a ) {
    idThread::ReturnInt(ui->GetBool(a));
}

idEntity* entityForDict(idThread* th, const idDict &dict) {
    idEntity *ent;
    const char * classname = "func_static";
    th->spawnArgs.Set( "classname", classname );
    gameLocal.SpawnEntityDef( th->spawnArgs, &ent );
    th->spawnArgs.Clear();
    ent->spawnArgs = dict;
    return ent;
}

        #define idUI32 int

idUserInterface* intToGui(idUI32 ui) {
    return getFullGUI();
}

EVDEFNAMEOBJ(idThread,gui,Find)(const char* a,bool32 b,bool32 c,bool32 d) {
    idThread::ReturnInt((unsigned int)uiManager->FindGui(a,asBool(b),asBool(c),asBool(d)));
}
EVDEFNAMEOBJ(idThread,gui,Check)(const char* a) {
    idThread::ReturnInt(uiManager->CheckGui(a));
}

EVDEFNAMEOBJ(idThread,gui,Name)(idUI32 ui) {
    idThread::ReturnString(intToGui(ui)->Name());
}
EVDEFNAMEOBJ(idThread,gui,Comment)(idUI32 ui) {
    idThread::ReturnString(intToGui(ui)->Comment());
}
EVDEFNAMEOBJ(idThread,gui,IsInteractive)(idUI32 ui) {
    idThread::ReturnInt(intToGui(ui)->IsInteractive());
}
EVDEFNAMEOBJ(idThread,gui,IsUniqued)(idUI32 ui) {
    idThread::ReturnInt(intToGui(ui)->IsUniqued());
}
EVDEFNAMEOBJ(idThread,gui,SetUniqued)(idUI32 ui,bool32 a) {
    intToGui(ui)->SetUniqued(asBool(a));
}
EVDEFNAMEOBJ(idThread,gui,InitFromFile)(idUI32 ui,const char* a,bool32 b,bool32 c) {
    intToGui(ui)->InitFromFile(a,asBool(b),asBool(c));
}
EVDEFNAMEOBJ(idThread,gui,HandleNamedEvent)(idUI32 ui,  const char* a) {
    intToGui(ui)->HandleNamedEvent(a);
}
EVDEFNAMEOBJ(idThread,gui,Redraw)(idUI32 ui, int time) {
    intToGui(ui)->Redraw(time);
}
EVDEFNAMEOBJ(idThread,gui,DrawCursor)(idUI32 ui) {
    intToGui(ui)->DrawCursor();
}
EVDEFNAMEOBJ(idThread,gui,State)(idUI32 ui) {
    idEntity* ent = entityForDict(this,intToGui(ui)->State());
    idThread::ReturnEntity(ent);
}
EVDEFNAMEOBJ(idThread,gui,DeleteStateVar)(idUI32 ui,  const char* a ) {
    intToGui(ui)->DeleteStateVar(a);
}
EVDEFNAMEOBJ(idThread,gui,SetStateString)(idUI32 ui,  const char* a, const char* b ) {
    intToGui(ui)->SetStateString(a,b);
}
EVDEFNAMEOBJ(idThread,gui,SetStateInt)(idUI32 ui,  const char* a,int b ) {
    intToGui(ui)->SetStateInt(a,b);
}
EVDEFNAMEOBJ(idThread,gui,SetStateFloat)(idUI32 ui, const char* a,float b ) {
    intToGui(ui)->SetStateFloat(a,b);
}
EVDEFNAMEOBJ(idThread,gui,SetStateBool)(idUI32 ui,  const char* a,bool32 b ) {
    intToGui(ui)->SetStateBool(a,asBool(b));
}
EVDEFNAMEOBJ(idThread,gui,GetStateString)(idUI32 ui, const char* a) {
    idThread::ReturnString(intToGui(ui)->GetStateString(a));
}
EVDEFNAMEOBJ(idThread,gui,GetStateInt)(idUI32 ui, const char* a ) {
    idThread::ReturnInt(intToGui(ui)->GetStateInt(a));
}
EVDEFNAMEOBJ(idThread,gui,GetStateFloat)(idUI32 ui, const char* a ) {
    idThread::ReturnFloat(intToGui(ui)->GetStateFloat(a));
}
EVDEFNAMEOBJ(idThread,gui,GetStateBool)(idUI32 ui, const char* a ) {
    idThread::ReturnInt(intToGui(ui)->GetStateBool(a));
}
EVDEFNAMEOBJ(idThread,gui,StateChanged)(idUI32 ui, int a, bool32 b ) {
    intToGui(ui)->StateChanged(a,asBool(b));
}
EVDEFNAMEOBJ(idThread,gui,Activate)(idUI32 ui, bool32 a , int b ) {
    intToGui(ui)->Activate(asBool(a),b);
}
EVDEFNAMEOBJ(idThread,gui,Trigger)(idUI32 ui, int a ) {
    intToGui(ui)->Trigger(a);
}
EVDEFNAMEOBJ(idThread,gui,SetCursor)(idUI32 ui, float a ,float b ) {
    intToGui(ui)->SetCursor(a,b);
}
EVDEFNAMEOBJ(idThread,gui,CursorX)(idUI32 ui ) {
    idThread::ReturnFloat(intToGui(ui)->CursorY());
}

EVDEFNAMEOBJ(idThread,gui,CursorY)(idUI32 ui ) {
    idThread::ReturnFloat(intToGui(ui)->CursorY());
}





EVDEFNAMEOBJ(idThread,dict,Create)() {
    idDict* state = new idDict();
    idThread::ReturnInt((unsigned int)state);
}
EVDEFNAMEOBJ(idThread,dict,Delete)(idDict* ui,  const char* a ) {
    ui->Delete(a);
}
EVDEFNAMEOBJ(idThread,dict,SetString)(idDict* ui,  const char* a, const char* b ) {
    ui->Set(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetInt)(idDict* ui,  const char* a,int b ) {
    ui->SetInt(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetFloat)(idDict* ui, const char* a,float b ) {
    ui->SetFloat(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetBool)(idDict* ui,  const char* a,bool32 b ) {
    ui->SetBool(a,asBool(b));
}
EVDEFNAMEOBJ(idThread,dict,SetVector)(idDict* ui,  const char* a,const idVec3 &b ) {
    ui->SetVector(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetVec4)(idDict* ui,  const char* a,const idVec4& b ) {
    ui->SetVec4(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetVec2)(idDict* ui,  const char* a,const idVec2& b ) {
    ui->SetVec2(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetAngles)(idDict* ui,  const char* a,const idAngles& b ) {
    ui->SetAngles(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetMatrix)(idDict* ui,  const char* a,const idMat3& b ) {
    ui->SetMatrix(a,b);
}
EVDEFNAMEOBJ(idThread,dict,SetDefaults)(idDict* ui, idDict* a ) {
    ui->SetDefaults(a);
}

EVDEFNAMEOBJ(idThread,dict,GetNumKeyVals)(idDict* ui) {
    idThread::ReturnInt(ui->GetNumKeyVals());
}
EVDEFNAMEOBJ(idThread,dict,GetAngles)(idDict* ui , const char* a) {
    idThread::ReturnInt((unsigned int)&ui->GetAngles(a));
}
EVDEFNAMEOBJ(idThread,dict,GetMatrix)(idDict* ui , const char* a) {
    idThread::ReturnInt((unsigned int)&ui->GetMatrix(a));
}
EVDEFNAMEOBJ(idThread,dict,GetString)(idDict* ui, const char* a) {
    idThread::ReturnString(ui->GetString(a));         // jobject
}
EVDEFNAMEOBJ(idThread,dict,GetInt)(idDict* ui, const char* a ) {
    idThread::ReturnInt(ui->GetInt(a));
}
EVDEFNAMEOBJ(idThread,dict,GetFloat)(idDict* ui, const char* a ) {
    idThread::ReturnFloat(ui->GetFloat(a));
}
EVDEFNAMEOBJ(idThread,dict,GetBool)(idDict* ui, const char* a ) {
    idThread::ReturnInt(ui->GetBool(a));
}
#endif