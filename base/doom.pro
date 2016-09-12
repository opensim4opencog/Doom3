
% ['base/doom.pro'],init.

dynamic(F/A):-!,functor(X,F,A),dynamic(X).
dynamic(X):-!,numbervars(X,0,_),asserta(X),retract(X).

object_alias(X,Y):-object_alias1(X,Y),!.
object_alias(X,X).

alias_object(X,Y):-object_alias(Y,X),!.

result_unify(X,X):-!.
result_unify(X,Y):-alias_object(X,Y),!.
result_unify(o(X),X):-!.
result_unify(X,o(X)):-!.

varsOF(A,B):-vars_of1(A,C),sort(C,B),!.
% vars_of1(Var,[[_|Var]]):-var(Var),!.
vars_of1(Var,[Var]):-var(Var),!.
vars_of1([],[]):-!. % makes faster?
vars_of1([H|T],Vars):-vars_of1(H,V1s),vars_of1(T,V2s),append(V1s,V2s,Vars),!.
vars_of1(P,Vars):-P=..[_,H|T],!,vars_of1(H,V1s),vars_of1(T,V2s),append(V1s,V2s,Vars),!.
vars_of1(_,[]).

dotvars(X,YY):-varsOF(X,Y),add_dotkey(Y,YY),!.

add_dotkey([],[]).
add_dotkey([H|T],[[_|H]|TT]):-add_dotkey(T,TT).

testdoom :-doom_eval(getHealth(cyc_bot_1),_X).
testdoom :-doom_eval(setHealth(cyc_bot_1,100),_X).
testcyc:-cycquery(isa('Person',X),'BaseKB'),writeq(isa('Person',X)),nl,fail.
testcyc:-cycquery(relationAllExists(P,'Person',X),'BaseKB'),writeq(relationAllExists(P,'Person',X)),nl,fail.
testcyc:-cycassert(isa('BillClinton','HomoSapiens'),'BaseKB'),fail.
testcyc:-cycquery(isa('BillClinton','HomoSapiens'),'BaseKB'),fail.
testcyc:-cycretract(isa('BillClinton','HomoSapiens'),'BaseKB'),fail.
testcyc:-!.


doom_method(MethArgs,Result):-doom(X),invoke_java_method(X,MethArgs,Result).
game_eval(MethArgs,Result):-doom_method(evalForJinni(MethArgs),ResultR),result_unify(ResultR,Result).
doom_eval(MethArgs,Result):-doom_method(evalForJinni(MethArgs),ResultR),result_unify(ResultR,Result).

doom_named(MethArgs,Result):-doom_eval(findObject(MethArgs),Result).
cycquery(P,Mt):-varsOF(P,Vars),cycquery(P,Mt,Vars).
	cycquery(P,Mt,[]):-!,doom_eval(cycquery(P,Mt),['!NIL']),!.
	cycquery(P,Mt,Vars):-add_dotkey(Vars,VVs),doom_eval(cycquery(P,Mt),R),!,member(VVs,R).
	
cycqueryvars(P,Mt):-varsOF(P,Vars),cycqueryvars(P,Mt,Vars).
	cycqueryvars(P,Mt,[]):-!,doom_eval(cycquery(P,Mt),['!NIL']),!.
	cycqueryvars(P,Mt,Vars):-add_dotkey(Vars,VVs),doom_eval(cycqueryvars(Vars,P,Mt),R),!,member(VVs,R).

cycassert(P,Mt):-nonAssertble(P,Mt),!.
cycassert(forward(forward(P)),Mt):-doom_eval(cycassertforward(P,Mt),_R),!.
cycassert(forward(P),Mt):-doom_eval(cycassertforward(P,Mt),_R),!.
cycassert(P,Mt):- doom_eval(cycassert(P,Mt),_R),!.

%cycretract(P,Mt):-ground(P),!,doom_eval(cycretract(P,Mt),_R).
cycretract(P,Mt):-cycquery(P,Mt),doom_eval(cycretract(P,Mt),_R).
cycretractall(P,Mt):-cycquery(P,Mt),doom_eval(cycretract(P,Mt),_R),fail.
cycretractall(_,_):-!.


%% (doom_eval (faceEntity cyc_bot_1 player1) ?X)

cycload(isa):-!.
cycload(genls):-!.
cycload(Constant):-doom(X),invoke_java_method(X,assertConstantToJinni(Constant),_Y).

assertCyc(CycList):-sterm2pterm(CycList,PTerm),writeln(assertCyc(PTerm)),assertIfNew(PTerm).
assertCyc(CycList,MT):-writeln(MT:CycList).
queryCyc(CycList):-sterm2pterm(CycList,PTerm),writeln(PTerm),catch(PTerm,_,fail).
removeCyc(CycList):-sterm2pterm(CycList,PTerm),writeln(PTerm),retractall(PTerm).

assertIfNew(ist('UniversalVocabularyMt',_)):-!.
assertIfNew(ist('EnglishParaphraseMt',_)):-!.
assertIfNew(ist('EnglishMt',_)):-!.
assertIfNew(PTerm):-catch(PTerm,_,fail),!.
assertIfNew(PTerm):-catch(assert(PTerm),_,true),!.

atomFix('Point3Fn','v').
atomFix(P,P).
sterm2pterm(PTerm,PTerm):-var(PTerm).
sterm2pterm([Cyc,List],List):-atom(Cyc),Cyc=='QUOTE',!.
sterm2pterm([Cyc|List],List):-atom(Cyc),Cyc=='QUOTE',!.
sterm2pterm([Cyc|List],List):-atom(Cyc),Cyc=='!QUOTE',!.
sterm2pterm([Cyc|List],PTerm):-atom(Cyc),atomFix(Cyc,P),sterm2ptermList(List,UList),PTerm=..[P|UList],!.
sterm2pterm(C,P):-atomFix(C,P),!.
sterm2pterm(PTerm,PTerm):-!.
sterm2ptermList([],[]).
sterm2ptermList([H|T],[HH|TT]):-sterm2pterm(H,HH),sterm2ptermList(T,TT).


nonAssertble(arity(Pred,_),_):-predBuiltin(Pred).
nonAssertble(isa(Pred,_),_):-predBuiltin(Pred).
predBuiltin(arity).
predBuiltin(isa).
predBuiltin(ist).
predBuiltin('Predicate').
predBuiltin('arg1Isa').
predBuiltin('argIsa').


% ['base/doom']
% cycunify(isa('Person',X),'BaseKB')
% cycunify(relationAllExists(P,'Person',X),'BaseKB')


		
%        doom_eval(n(9),X).
obj_field(OO,F,VO):-
         object_alias(OO,O),
         %object_fields
         doom_eval(getObjectField(O,F),Value),alias_object(Value,VO).

after_init:-catch(doom(_),_,fail),!.
after_init:-dynamic(object_alias1/2).
after_init:-new_java_object('daxclr.ext.PrologAPI',X),assert(doom(X)),asserta(object_alias1(doom,X)).
%after_init:-loadPlanningDomain.
%after_init:-no_initCycKb.


reverseSafe([],[]):-!.
reverseSafe(Vs,VVs):-reverse(Vs,VVs),!.

evalAPI(X,Vs,Z):-reverseSafe(Vs,VVs),doEvalAPI(X,VVs,Z),!.


isPrologAPI('doom-eval',prolog).
isPrologAPI('DOOM-EVAL',prolog).
isPrologAPI(_,fail).


doEvalAPI(X,Y,Z):-writeq(doEvalAPI(X,Y,Z)),nl,fail.
%doom_eval(entity_faceEntity(cyc_bot_1,player1),1)
% (doom-eval (entity_faceEntity cyc_bot_1 player1))
% doEvalAPI([doom_eval,X],_,R):-sterm2pterm(X,Y),doom_eval(Y,R),!.
%[entity_faceEntity,cyc_bot_1,player1]
doEvalAPI(N,_,N):-number(N),!.
doEvalAPI(N,_,N):-atom(N),!.
doEvalAPI(['doom-eval',X],_,[R]):-sterm2pterm(X,Y),writeq(sterm2pterm(X,Y)),nl,catch(doom_eval(Y,R),_,fail),!.
doEvalAPI(['DOOM-EVAL',X],_,[R]):-sterm2pterm(X,Y),writeq(sterm2pterm(X,Y)),nl,catch(doom_eval(Y,R),_,fail),!.
doEvalAPI(['PlusFn',X,Y],_,[O]):-R is X + Y,swrite(R,O),!.
doEvalAPI(['TheList','doomValue'|List],Vs,R):-doomValue(List,Vs,R),!.
doEvalAPI(['TheList','invokePlanner'|List],Vs,R):-invokePlanner(List,Vs,R),!.
doEvalAPI(['TheList'|List],_,List):-!.
doEvalAPI([_,_,'NIL'],_,0.0):-!.

doEvalAPI(G,VVs,R):-doEval(G,VVs,R),writeq(doEval(G,VVs,R)),nl,!.

doEval([Atom,L],_,O):-atom_concat(F,'-pbu',Atom),!,Call=..[F,L,R],safesetof(R,Call,O).
doEval([Atom,R],_,O):-atom_concat(F,'-pub',Atom),!,Call=..[F,L,R],safesetof(L,Call,O).
doEval([Atom,[L,R]],_,O):-atom_concat(F,'-pbb',Atom),!,Call=..[F,L,R],safesetof([L,R],Call,O).
doEval([Atom,_],_,O):-atom_concat(F,'-puu',Atom),!,Call=..[F,L,R],safesetof([L,R],Call,O).
doEval([P|L],_,X):- C=..[P|L], catch(doom_eval(C,XX),_,fail),not(P=XX),!,formatOut(XX,X).
doEval(P,_,[undefined,P]).


notNull(X):-isNull(X),!,fail.
notNull(_).
notZero('$null'):-!,fail.
notZero(R):-R>0,!.

isNull(X):-var(X),!,fail.
isNull([]).
isNull('').
isNull('$null').
isNull('NIL').
isNull(0.0).
isNull(0).

formatOut(X,X):-atom(X),!.
formatOut(X,X).

translateSet(X,X):-var(X),!.
translateSet([[]],'NIL'):-!.
translateSet([],'NIL'):-!.
translateSet(A,A).

toWhat(X,string,_):-isNull(X),!,fail.
toWhat(s(X),string,s(X)):-!.
%toWhat(What,string,s(What)):-atom(What),name(What,[A,B|C]),!.
toWhat(What,string,s(What)):-atom(What),!. %name(What,[A,B|C]),!.
toWhat([A,B|C],string,s(What)):-name(What,[A,B|C]),!.
toWhat(ABC,_,ABC).

		     


safesetof(X,Y,Z):-setof(X,catch(Y,_,fail),R),!,translateSet(R,Z).
%safesetof(X,Y,Z):-Y=..[P,A,B],
safesetof(_,_,'NIL').

entityMissing(X,Y):-retractall(getEntitySetCache(_)),cycquery(doomPropertyNext(X,isa,Y),'DoomCurrentStateMt'),not(isEntity(X)).

doomPropertyNext(X,Y,Z):-Mt=Mt,X=X,Y=Y,Z=Z,Query=doomPropertyNext(X,Y,Z),cycquery(Query,'EverythingPSC'),cycretractall(Query,Mt).

entityMissing:-entityMissing(X,Y),catch(createInDoom(X,Y),_,fail),fail.
entityMissing.

setPropertyNext:-entityMissing,setPropertyNext2.
setPropertyNext2:-doomPropertyNext(X,Y,Z).
setPropertyNext2.

%;;(preconditionFor-Props (and (isa ?J22 doom:idAI) (doom:pathControl ?J39 ?J30) (possesses ?J22 ?J39) (inRegion ?J22 ?J31) (pathBetween ?J30 ?J31 ?J32)) (possible (doom:doomAction UnlockingALock ?J22 ?J30)))
%;; logic+moo ==  first order predicate logic + moo like doom system

createInDoom(Name,Class):-nonvar(Name),nonvar(Class),doom_eval(invokeObject(doom,makeEntity,Class,Name),_).

difEnts(X,Y):-isEntity(X),isEntity(Y),not(X=Y).
%,not(doomAttached(X,Y)).

doomNear(X,Y):-difEnts(X,Y),doom_eval(invokeObject(doom,invokeScript,"",isCloseTo,args(X,Y)),R),notZero(R).
doomCanSee(X,Y):-difEnts(X,Y),doom_eval(invokeObject(doom,invokeScript,"",entity_canSee,args(X,Y)),R),notZero(R).
doomTouches(X,Y):-isEntity(X),not(doomAttached(X,_)),isEntity(Y),doom_eval(invokeObject(doom,invokeScript,"",entity_isTouching,args(X,Y)),R),notZero(R).
doomAttached(X,Y):-difEnts(X,Y),doom_eval(invokeObject(doom,invokeScript,"",entity_isBoundTo,args(X,Y)),R),notZero(R).

doomFacesDirection(X,R):-isEntity(X),doom_eval(invokeObject(doom,invokeScript,"",entity_getAngles,args(X)),R).
doomSpacePoint(X,R):-isEntity(X),doom_eval(invokeObject(doom,invokeScript,"",entity_getWorldOrigin,args(X)),R).
doomInRegion(X,Y):-isEntity(X),doom_eval(invokeObject(doom,invokeScript,"",inRegion,args(X)),Y),notNull(Y).
doomInstance(X,Y):-isEntity(X),decoposeName(X,Y).
decoposeName('DoomItemFn'(X),Y):-!,decoposeName(X,Y).
decoposeName('DoomClassFn'(X),Y):-!,decoposeName(X,Y).
decoposeName('DoomPropertyFn'(X),Y):-!,decoposeName(X,Y).
decoposeName(o(X),Y):-!,decoposeName(X,Y).
decoposeName(X,Y):-atom(X),atom_concat('doom:',Mid,X),!,decoposeName(Mid,Y).
decoposeName(X,Y):-toWhat(X,string,Y),!.


typeDefiningProp(skin).
typeDefiningProp(model).
typeDefiningProp(classname).
typeDefiningProp(spawnclass).

getPropertyName(spawnclass,entity_getSpawnclass,string).
getPropertyName(skin,entity_getSkin,string).
getPropertyName(model,entity_getModel,string).
getPropertyName(classname,entity_getClassname,string).
getPropertyName(spawnclass,getClassDef,string).
getPropertyName(name,entity_getName,string).

getPropertyName(origin,entity_getWorldOrigin,vector).
getPropertyName(orientation,entity_getAngles,vector).
getPropertyName(color,entity_getColor,vector).
getPropertyName(size,entity_getSize,vector).

%getPropertyName(model,getClassname).
isEntity(E):-getEntitySet(List),member(o(E),List).
isLocation(L):-setof(X,(isLocation2(X),not(atom_concat('id',_,X))),LL),member(L,LL).
isLocation2(X):-doomType(X,"idLocation").
isLocation2(X):-doomType(X,"idLight").
isLocation2(X):-doomType(X,"info_location").

getEntitySet(List):-catch(getEntitySetCache(List),_,fail),!.
getEntitySet(List):-doom_eval(getEntitySet(doom),X),doom_eval(toArray(X),List),assert(getEntitySetCache(List)),!.


doomType(Who,Result):-setof([Who,Result],doomType2(Who,Result),List),member([Who,Result],List).
doomType2(Who,Result):- isEntity(Who),
		typeDefiningProp(Prop),getPropertyName(Prop,Name,Type),not(Type=vector),queryDoomProperty(Who,Name,What),toWhat(What,Type,Result).      	
			

doomValue(W,What):-isEntity(W),getPropertyName(N,Getter,Type),name(N,NS),What=['BindingFn',NS,R],queryDoomProperty(W,N,Value),toWhat(Value,Type,R).
doomSpawnArgs(W,What):-isEntity(W),getPropertyName(N,Getter,Type),name(N,NS),What=['BindingFn',NS,R],queryDoomProperty(W,N,Value),toWhat(Value,Type,R).

queryDoomProperty(Who,P,What):-getPropertyName(P,N,T),not(P = N ),!,queryDoomProperty(Who,N,What).
queryDoomProperty(Who,Prop,What):- P=..[Prop,Who],catch(doom_eval(P,What),_,fail),notNull(What).

doomValue(List,_VVs,[R]):-sterm2pterm(List,Call),doom_eval(Call,R).
invokePlanner([Mt,Task],_VVs,[Plan]):-planFor(Task,Mt,Plan).
		
getPlanner(X):-catch(thePlanner(X),_,fail),!.
getPlanner(X):- doom_eval(getPlanner(getCycAPI(prologapi)),X),asserta(thePlanner(X)),!.

planFor(Task,Mt,Plan):-getPlanner(X),doom_eval(invokeObject(X,setMt,Mt),_),doom_eval(invokeObject(X,setTask,Task),_),doom_eval(getPlan(X),Plan),!.
testplanner:-planFor([doFuelDevice,'Trucker001','SemiTrailer-Truck-001'],'DoomSharedPlanningMt',Plan),writeq(Plan),nl,!.
testplanner2:-planFor(lit(doFuelDevice('Trucker001','SemiTrailer-Truck-001')),'DoomSharedPlanningMt',Plan),writeq(Plan),nl,!.



doEvalAPI(X,_):-writeq(X),nl,fail.
doEvalAPI(throw(X),threw(X)):-!,throw(X).
doEvalAPI([make],[made]):-make.
doEvalAPI([X|Y],P):-sterm2pterm([X|Y],O),doEvalAPI(O,P),!.
doEvalAPI(X,Results):-
	varsOF(X,Vars),
	%add_dotkey(Vars,Dots),
	setof(Vars,catch(X,_,fail),Results),!.
	
doEvalAPI(X,echo(X)):-!.

'!='(X,X).     

init:-after_init,fail.
init:-!.


make :-['base/doom.pro'],init,!.
%%test:-doom_eval(name(o(cyc_bot_1)),R)

rmake:-make,no_initCycKb.

% RATIO 1-5 WITH SICSTUS: work 'above-Generally' indexing or rewrite add, del etc in "war"
% WARPLAN : a system for generating plans by D.H.D. Warren

% to run with  BinProlog add:

% :- op(400,xfy,*).
% :- op(500,yfx,+).


plans(C,say(impossible(C))) :- unless(consistent(C,true)), !.
plans(C,T) :-nl,nl, write('Goal: '),write(C),nl,time(M0), plan(C,true,T,T1), time(M1), nl, output(T1), nl,
   Time is M1-M0, write(Time), write(' microsecs.'), nl.

time(T) :- statistics(runtime,[T,_]).

output(T):-var(T),!,writeq(T), write('.'), nl.
output(T+U) :- !, output1(T), write(U), write('.'), nl.
output(T) :- write(T), write('.'), nl.

output1(T):-var(T),!,writeq(T), write('.'), nl.
output1(T+U) :- !, output1(T), write(U), write(';'), nl.
output1(T) :- write(T), write(';'), nl.

plan(X*C,P,T,T2) :- !, solve(X,P,T,P1,T1), plan(C,P1,T1,T2).
plan(X,P,T,T1) :- solve(X,P,T,_,T1).

solve(X,P,T,P,T) :- always(X).
solve(X,P,T,P1,T) :- holds(X,T), and_(X,P,P1).
solve(X,P,T,X*P,T1) :- add(X,U), achieve(X,U,P,T,T1).

achieve(_,U,P,T,T1+U ) :- 
   preserves(U,P),
   can(U,C),
   consistent(C,P),
   plan(C,P,T,T1),
   preserves(U,P).
achieve(X,U,P,T+V,T1+V) :- 
   preserved(X,V),
   retrace(P,V,P1),
   achieve(X,U,P1,T,T1),
   preserved(X,V).

holds(X,_+V) :- add(X,V).
holds(X,T+V) :- !,preserved(X,V),holds(X,T),preserved(X,V).
holds(X,T) :- given(T,X).

preserved(X,V) :- mkground(X*V,0,_), del(X,V), !, fail.
preserved(_,_).

preserves(U,X*C) :- preserved(X,U), preserves(U,C).
preserves(_,true).

retrace(P,V,P2) :- 
   can(V,C),
   retrace1(P,V,C,P1),
   conjoin(C,P1,P2).

retrace1(X*P,V,C,P1) :- add(Y,V), equiv(X,Y), !, retrace1(P,V,C,P1).
retrace1(X*P,V,C,P1) :- elem(Y,C), equiv(X,Y), !, retrace1(P,V,C,P1).
retrace1(X*P,V,C,X*P1) :- retrace1(P,V,C,P1).
retrace1(true,_,_,true).

consistent(C,P) :- 
   mkground(C*P,0,_),
   imposs(S),
   unless(unless(intersect(C,S))),
   implied(S,C*P), 
   !, fail.
consistent(_,_).

and_(X,P,P) :- elem(Y,P), equiv(X,Y), !.
and_(X,P,X*P).

conjoin(X*C,P,X*P1) :- !, conjoin(C,P,P1).
conjoin(X,P,X*P).

elem(X,Y*_) :- elem(X,Y).
elem(X,_*C) :- !, elem(X,C).
elem(X,X).

intersect(S1,S2) :- elem(X,S1), elem(X,S2).

implied(S1*S2,C) :- !, implied(S1,C), implied(S2,C).
implied(X,C) :- elem(X,C).
implied(X,_) :- plan_true(X).

notequal(X,Y) :- unless(X=Y),unless(X=qqq(_)),unless(Y=qqq(_)).

equiv(X,Y) :- unless(nonequiv(X,Y)).

nonequiv(X,Y) :- mkground(X*Y,0,_), X=Y, !, fail.
nonequiv(_,_).

mkground(qqq(N1),N1,N2) :- !, N2 is N1+1.
mkground(qqq(_),N1,N1) :- !.
mkground(X,N1,N2) :- X =.. [_|L], mkgroundlist(L,N1,N2).

mkgroundlist([X|L],N1,N3) :- mkground(X,N1,N2), mkgroundlist(L,N2,N3).
mkgroundlist([],N1,N1).

unless(X) :-  plan_true(X), !, fail.
unless(_).


add(X,Y):-plan_add(X,Y).
del(X,Y):-plan_del(X,Y).
can(X,Y):-plan_can(X,Y).
given(X,Y):-plan_given(X,Y).
always(Y):-plan_always(Y).
imposs(X):-imposs_db(X).

plan_true(nonequiv(X,Y)):-!,nonequiv(X,Y).
plan_true(consistent(X,Y)):-!,consistent(X,Y).
plan_true(unless(X)):-!,unless(X).
plan_true(intersect(X,Y)):-!,intersect(X,Y).
plan_true(X):-writeq(eXXXXXXXXternal(X)),nl,catch(X,_,fail).


% First STRIPS World

%imposs_db(_):-!.
imposs_db(_) :- fail.  % To stop Quintus complaining.


test:-test1,test3,test4,test2.
plan_given( start(cyc_bot(1)), origin(cyc_bot(1),v(1,1,5))).

plan_given( start(cyc_bot(1)), origin(box(N), v(1,1,N))) :- range(N,1,3).
plan_given( start(cyc_bot(1)), objectFoundInLocation(box(N),info_location(1))) :- range(N,1,3).

% plan_given( start(cyc_bot(1)), origin(box(N), v(1,1,N))) :- range(N,4,6).
% plan_given( start(cyc_bot(1)), objectFoundInLocation(box(N),info_location(1))) :- range(N,4,6).

plan_given( start(cyc_bot(1)), objectFoundInLocation(cyc_bot(1),info_location(1))).
plan_given( start(cyc_bot(1)), onfloor(cyc_bot(1))).
plan_given( start(cyc_bot(1)), status(lightswitch(1),off)).

plan_always( portalConnectsRegions(D,R1,R2)) :- portalConnectsRegions_ext(D,R1,R2).
plan_always( portalConnectsRegions(D,R2,R1)) :- portalConnectsRegions_ext(D,R1,R2).
plan_always( objectFoundInLocation(D,R1)) :- plan_always(portalConnectsRegions(D,_,R1)).
plan_always( portable(cyc_bot(1),box(_))).
plan_always( locinroom(v(1,1,6),info_location(4))).
plan_always( objectFoundInLocation(lightswitch(1),info_location(1))).
plan_always( origin(lightswitch(1),v(1,1,4))).
plan_always(X):-useIst(_,X).

portalConnectsRegions_ext(door(N),info_location(N),info_location(5)) :- range(N,1,4).

range(M,M,_).
range(M,L,N) :- L < N, L1 is L+1, range(M,L1,N).


test1 :- plans( status(lightswitch(1),on), start(cyc_bot(1))).
test2 :- plans( nextto(box(1),box(2)) * nextto(box(2),box(3)), start(cyc_bot(1))).
test3 :- plans( origin(cyc_bot(1),v(1,1,6)), start(cyc_bot(1))).
test4 :- plans( nextto(box(2),box(3)) * nextto(box(3),door(1)) *
		status(lightswitch(1),on) * nextto(box(1),box(2)) *
		objectFoundInLocation(cyc_bot(1),info_location(2)), start(cyc_bot(1))).
      %test5.


writeln(X):-var(X),!,writeq(step(X)),nl.
writeln(X+Y):-writeln(X),writeln(Y),!.
writeln(X*Y):-writeln(X),writeln(Y),!.
writeln(X):-writeq(step(X)),nl.
seq(S,S).

plans(X,Y,_):-plans(X,Y).
findPlans(Task,Plan):-plans(Task,Plan).

 % TRY THE FOLLOWING QUERIES with pop_t.pl:
test10:-plans('possesses'('cyc_bot_1','moveable_mop_1'),_P,3).
test11:-plans(possesses(cyc_bot_1,moveable_mop_1),_P,3).
test12:-plans(objectFoundInLocation(moveable_mop_1,room_lab2),_P,7).
test13:-plans((possesses(cyc_bot_1,item_burger) * objectFoundInLocation(cyc_bot_1,room_lab2)),_P,9).
test14:-plans((objectFoundInLocation(cyc_bot_1,room_lab2) * possesses(cyc_bot_1,item_burger)),_P,9).
test21:-plans(possesses(cyc_bot_1,moveable_mop_1),P,3),botDo(P).
test22:-plans(objectFoundInLocation(moveable_mop_1,room_lab2),P,7),botDo(P).
test23:-plans((possesses(cyc_bot_1,item_burger) * objectFoundInLocation(cyc_bot_1,room_lab2)),P,9),botDo(P).
test24:-plans((objectFoundInLocation(cyc_bot_1,room_lab2) * possesses(cyc_bot_1,item_burger)),P,9),botDo(P).


% test117:-cycAssertBuffer('DoomSharedPlanningMt':goals(cyc_bot_1,(possesses(cyc_bot_1,item_burger) and objectFoundInLocation(cyc_bot_1,room_lab2)))).


botDo(_):-!.
/*
botDo([]):-!.
botDo([H|T]):-!,botDo(H),sleep(1),botDo(T).
botDo(CX):-writeq(CX),nl,fail.
botDo('Now'):-!.
botDo('Tomorrow-Indexical'):-!.
botDo(doomAction('WalkingOnTwoLegs', Bot, From, To)):-gotoItem(Bot,To,_).
botDo(doomAction('HoldingAnObject', Bot, Obj, Where)):-d3Prolog(take(Bot,Obj),CX),gotoIfTooFar(CX,Bot,Obj).
botDo(doomAction('Abandon', Bot, Obj, Where)):-!,d3Prolog(drop(Bot,Obj)).
botDo(doomAction('UnlockingALock', Bot, Obj)):-!,d3Prolog([unlock,Bot,Obj],CX),gotoIfTooFar(CX,Bot,Obj).

gotoIfTooFar(too_far_away,Bot,To):-!,gotoItem(Bot,To,_).
gotoIfTooFar(_,Bot,To).
*/



useIst(X,Y):-catch(ist(X,Y),_,fail).
useIst(X,methodForAction(Act,Method)):-useIst(X,'preconditionForMethod'(_State,methodForAction(Act,Method))).

plan_add(State,Act):-useIst(_,'effectOfAction-Props'(Act,State)).
plan_add( origin(cyc_bot(1),P), 	goto1(cyc_bot(1),P,_)).
plan_add( nextto(cyc_bot(1),X),	gonear(cyc_bot(1),X,_)).
plan_add( nextto(X,Y), 	carry(cyc_bot(1),X,Y,_)).
plan_add( nextto(Y,X),	carry(cyc_bot(1),X,Y,_)).
plan_add( status(S,on),	turnon(cyc_bot(1),S)).
plan_add( 'above-Generally'(cyc_bot(1),B),	climbon(cyc_bot(1),B)).
plan_add( onfloor(cyc_bot(1)),		climboff(cyc_bot(1),_)).
plan_add( objectFoundInLocation(cyc_bot(1),R2), 	gothru(cyc_bot(1),_,_,R2)).

plan_del(State,Act):-useIst(_,'effectOfAction-Props'(Act,not(State))).
plan_del( nextto(cyc_bot(1),X), carry(cyc_bot(1),X,_,_)) :- !, fail.
plan_del( nextto(cyc_bot(1),B), climbon(cyc_bot(1),B)) :- !, fail.
plan_del( nextto(cyc_bot(1),B), climboff(cyc_bot(1),B)) :- !, fail.
plan_del( nextto(Z,cyc_bot(1)),U) :- !, plan_del(nextto(cyc_bot(1),Z),U).

plan_del( onfloor(cyc_bot(1)),climbon(cyc_bot(1),_)).
plan_del( objectFoundInLocation(cyc_bot(1),_), gothru(cyc_bot(1),_,_,_)).
plan_del( status(S,_), turnon(cyc_bot(1),S)).

plan_del( origin(X,_),U) :- plan_moved(X,U).
plan_del( nextto(X,_),U) :- plan_moved(X,U).
plan_del( nextto(_,X),U) :- plan_moved(X,U).
plan_del( 'above-Generally'(X,_),U) :- plan_moved(X,U).

plan_moved( cyc_bot(1), goto1(cyc_bot(1),_,_)).
plan_moved( cyc_bot(1), gonear(cyc_bot(1),_,_)).
plan_moved( cyc_bot(1), carry(cyc_bot(1),_,_,_)).
plan_moved( X, carry(cyc_bot(1),X,_,_)).
plan_moved( cyc_bot(1), climbon(cyc_bot(1),_)).
plan_moved( cyc_bot(1), climboff(cyc_bot(1),_)).
plan_moved( cyc_bot(1), gothru(cyc_bot(1),_,_,_)).


plan_can(Act,State):-useIst(_,'preconditionForMethod'(State,methodForAction(Act,_Method))).
plan_can(Act,State):-useIst(_,'preconditionFor-Props'(State,Act)).

plan_can( goto1(cyc_bot(1),P,R), locinroom(P,R) *  objectFoundInLocation(cyc_bot(1),R) * onfloor(cyc_bot(1))).
plan_can( gonear(cyc_bot(1),X,R), objectFoundInLocation(X,R) * objectFoundInLocation(cyc_bot(1),R) * onfloor(cyc_bot(1))).
plan_can( carry(cyc_bot(1),X,Y,R),
	portable(cyc_bot(1),X) * objectFoundInLocation(Y,R) * objectFoundInLocation(X,R) * nextto(cyc_bot(1),X) * onfloor(cyc_bot(1))).
plan_can( turnon(cyc_bot(1),lightswitch(S)),
	'above-Generally'(cyc_bot(1),box(1)) * nextto(box(1), lightswitch(S))).
plan_can( climbon(cyc_bot(1),box(B)), nextto(cyc_bot(1),box(B)) * onfloor(cyc_bot(1))).
plan_can( climboff(cyc_bot(1),box(B)), 'above-Generally'(cyc_bot(1),box(B))).
plan_can( gothru(cyc_bot(1),D,R1,R2),
	portalConnectsRegions(D,R1,R2) * objectFoundInLocation(cyc_bot(1),R1) * nextto(cyc_bot(1),D) * onfloor(cyc_bot(1))).
	


planning_pred('preconditionFor-Props').
planning_pred('preconditionForMethod').
planning_pred('actionSequence').
planning_pred('methodForAction').
planning_pred('planForTask').
planning_pred('effectOfActionIf-Props').
planning_pred('effectOfAction-Props').

loadPlanningDomain:-planning_pred(X),cycload(X),fail.
loadPlanningDomain:-!.


once1(X):-catch(X,_,true),!.

no_initCycKb.
no_initCycKb:-no_initCyc(Mt,CycL),once1(cycassert(forward(CycL),Mt)),fail.


% ===================================================================
%  Predicates need and Assertion Mt
% ===================================================================
% :-dynamic(mtForPred/2).
	    
isDoomSlot(solid).


makeCycL((A * B),Mt):-makeCycL(A,Mt),makeCycL(B,Mt).
makeCycL((A + B),Mt):-makeCycL(A,Mt),makeCycL(B,Mt).
makeCycL(P,Mt):-P=..[H|List],member(H,[and,or,implies,not]),!,makeCycL(List,Mt).
makeCycL(P,Mt):-P=..[H|List],name_startswith('plan_',H),!,makeCycL(List,Mt).
makeCycL([],_):-!.
makeCycL([P|L],Mt):-makeCycL(P,Mt),makeCycL(L,Mt),!.
makeCycL(CycL,Mt):-makePredicate(CycL,Mt),CycL=..[_|ARGS],makeArgs(ARGS,Mt).
makeCycL(_CycL,_Mt).
makePredicate(P,_Mt):-compound(P),functor(P,N,A),
	cycassertuniv(isa(N,'FixedArityRelation')),
	cycassertuniv(isa(N,'Predicate')),
	cycassertuniv(arity(N,A)).
makePredicate(_,_).
cycassertuniv(A):-cycassert(A,'UniversalVocabularyMt').

makeArgs([],_):-!.
makeArgs([A|RGS],Mt):-makeArg(A,Mt),makeArgs([A|RGS],Mt).

makeArg(P,_Mt):-compound(P),functor(P,N,A),
	cycassertuniv(isa(N,'FixedArityRelation')),
	cycassertuniv(isa(N,'Function-Denotational')),
	cycassertuniv(arity(N,A)).
makeArg(_,_).



cycassert(CycL):-mtForCycL(CycL,Mt),makeCycL(CycL,Mt),cycassert(CycL,Mt).
cycretract(CycL):-mtForCycL(CycL,Mt),cycretract(CycL,Mt).
cycretractall(CycL):-mtForCycL(CycL,Mt),cycretractall(CycL,Mt).
cycquery(CycL):-cycquery(CycL,'InferencePSC').


mtForCycL(isa(_,'Collection'),'UniversalVocabularyMt'):-!.
mtForCycL(isa(_,'Microtheory'),'UniversalVocabularyMt'):-!.
mtForCycL(isa(_,'Predicate'),'UniversalVocabularyMt'):-!.
mtForCycL(arity(_,_),'UniversalVocabularyMt'):-!.
mtForCycL(ist(Mt,_),Mt).
mtForCycL(Out,Mt):-predOfCycL(Out,Pred),!,getMtForPred(Pred,Mt),!.

predOfCycL(SENT,Y):-member(OP,[forward,and,':',or,implies,not]),SENT=..[OP|LIST],!,member(A,LIST),predOfCycL(A,Y),!.
predOfCycL(CX,Y):-functor(CX,Y,_).


%:-dynamic_transparent(mtForPred/2).
defaultAssertMt('DoomCurrentStateMt').

getMtForPred(X,Y):-mtForPred(X,Y),!.
getMtForPred(genlMt,'BaseKB').
getMtForPred(CycL,Mt):-nonvar(CycL),functor(CycL,Pred,_),isRegisterCycPred(Mt,Pred,_),!.
getMtForPred(_CycL,Mt):-defaultAssertMt(Mt).

isRegisterCycPred(_,_,_):-!,fail.





cycKb(ist(Mt,CycL)):-no_initCyc(Mt,CycL),ensureMtBound(Mt,CycL).

ensureMtBound(Mt,_):-nonvar(Mt),!.
ensureMtBound(Mt,CycL):-mtForCycL(CycL,Mt),!.


name_startswith(Start,All):-atom_concat(Start,_,All),!.

atom_concat(Start,End,All):-nonvar(All),nonvar(Start),name(All,AS),name(Start,SS),append(SS,ES,AS),name(End,ES),!.
atom_concat(Start,End,All):-nonvar(All),nonvar(End),name(All,AS),name(End,ES),append(SS,ES,AS),name(Start,SS),!.
atom_concat(Start,End,All):-nonvar(Start),nonvar(End),name(Start,SS),name(End,ES),append(SS,ES,AS),name(All,AS),!.

mtForPred(arity,'UniversalVocabularyMt').
mtForPred(isa,'DoomVocabularyMt').
mtForPred(genls,'DoomVocabularyMt').
mtForPred(Slot,'DoomCurrentStateMt'):-isDoomSlot(Slot),!.
mtForPred(Const,Mt):-atom(Const),atom_concat('doom:',Check,Const),mtForPred(Check,Mt),!.
mtForPred(Const,Mt):-atom(Const),atom_concat('Doom:',Check,Const),!,mtForPred(Check,Mt),!.
mtForPred(doomPropertyNext,'DoomCurrentStateMt').
mtForPred(ARG,'DoomVocabularyMt'):-name_startswith(comment,ARG).
mtForPred(ARG,'DoomVocabularyMt'):-name_startswith(arity,ARG).
mtForPred(ARG,'DoomVocabularyMt'):-name_startswith(arg,ARG).
mtForPred(ARG,'DoomVocabularyMt'):-name_startswith(entityDef,ARG).
mtForPred(ARG,'DoomVocabularyMt'):-name_startswith(genl,ARG).
mtForPred(ARG,'DoomVocabularyMt'):-name_startswith(doom,ARG).
mtForPred(ARG,'DoomVocabularyMt'):-name_startswith(d3,ARG).
mtForPred(ARG,'DoomSharedPlanningMt'):-name_startswith(pre,ARG).
mtForPred(ARG,'DoomSharedPlanningMt'):-name_startswith(post,ARG).
mtForPred(ARG,'DoomSharedPlanningMt'):-name_startswith('plan_',ARG).
mtForPred(ARG,'DoomSharedPlanningMt'):-name_startswith(cond,ARG).
mtForPred(ARG,'DoomCurrentStateMt'):-name_startswith(do,ARG).
mtForPred(ARG,'DoomCurrentStateMt'):-name_startswith(loc,ARG).
mtForPred(ARG,'DoomCurrentStateMt'):-name_startswith(obj,ARG).
mtForPred(_ALL,'DoomCurrentStateMt').


cycMt('DoomCurrentStateMt').
cycMt('DoomVocabularyMt').
cycMt('DoomCurrentStateMt').
cycMt('DoomSharedPlanningMt').
cycMt('DoomVocabularyMt').




/*
no_initCyc('DoomVocabularyMt',isa('InstancePredicate','Collection')).
no_initCyc('DoomVocabularyMt',isa('InstancePredicate','ClarifyingCollectionType')).
no_initCyc('DoomVocabularyMt',isa('InstancePredicate','AtemporalNecessarilyEssentialCollectionType')).
no_initCyc('DoomVocabularyMt',isa('InstancePredicate','PredicateType')).
no_initCyc('DoomVocabularyMt',genls('InstancePredicate','Predicate')).
no_initCyc('DoomVocabularyMt',genls('InstancePredicate','ObjectPredicate')).
no_initCyc('DoomVocabularyMt',genls('InstancePredicate','BinaryPredicate')).

no_initCyc('DoomVocabularyMt',isa('InstanceBooleanPredicate','Collection')).
no_initCyc('DoomVocabularyMt',isa('InstanceSpeedPredicate','Collection')).
no_initCyc('DoomVocabularyMt',genls('InstanceBooleanPredicate','InstancePredicate')).
no_initCyc('DoomVocabularyMt',genls('InstanceSpeedPredicate','InstancePredicate')).
no_initCyc('DoomVocabularyMt',forward(implies(isa(P,'InstancePredicate'),and(singleEntryFormatInArgs(P,2),arity(P,2),isa(P,'BinaryPredicate'),isa(P,'StrictlyFunctionalPredicate'),arg1Isa(P,'Instance'))))).
no_initCyc('DoomVocabularyMt',isa(classname,'InstancePredicate')).
%genls('InstancePredicate','GenericValuePredicate')).
no_initCyc('DoomVocabularyMt',isa(doomclass,'InstancePredicate')).
no_initCyc('DoomVocabularyMt',forward(implies(isa(X,'InstancePredicate'),and(arity(X,2),isa(X,'SituationPredicate'),arg1Isa(X,'Instance'),arg2Isa(X,'Thing'),arg2Format(X,'SingleEntry'))))).
*/

no_initCyc('HumanActivitiesMt',energySourceTypeForDeviceType('RoadVehicle-DieselEngine','DieselFuel')).   
%==================================================
% INTERFACE PREDICATE SETUP
%==================================================


no_initCyc('UniversalVocabularyMt',isa('DoomEvalFn','Function')).
no_initCyc('UniversalVocabularyMt',isa('DoomEvalFn','EvaluatableRelation')).
no_initCyc('UniversalVocabularyMt',isa('DoomEvalFn','VariableArityFunction')).
no_initCyc('UniversalVocabularyMt',resultIsa('DoomEvalFn','Thing')).
no_initCyc('UniversalVocabularyMt',evaluationDefn('DoomEvalFn','SubLQuoteFn'('!CYC-DOOM-EVAL-FN'))).

							   
no_initCyc('UniversalVocabularyMt',genlPreds('doomAction','doomTrue')).

%==================================================
% INTERFACE MAPPING SETUP PREDICATES
%==================================================

no_initCyc('UniversalVocabularyMt',isa(programGetCommand,'BinaryPredicate')).
no_initCyc('UniversalVocabularyMt',comment(programGetCommand,"Relates a #$doom:DoomRelation to a #$CharacterString this would be equivalent to (#$synonymousExternalConcept :ARG1 #$DoomCurrentStateMt :ARG3)")).
no_initCyc('UniversalVocabularyMt',arg1Isa(programGetCommand,'DoomRelation')).
no_initCyc('UniversalVocabularyMt',arg2Isa(programGetCommand,'CharacterString')).
no_initCyc('DoomCurrentStateMt',programGetCommand(sees,"canSee")).

no_initCyc('UniversalVocabularyMt',isa(programSetCommand,'BinaryPredicate')).
no_initCyc('UniversalVocabularyMt',comment(programSetCommand,"Relates a #$doom:DoomRelation to a #$CharacterString this would be equivalent to (#$synonymousExternalConcept :ARG1 #$DoomVocabularyMt :ARG3)")).
no_initCyc('UniversalVocabularyMt',arg1Isa(programSetCommand,'DoomRelation')).
no_initCyc('UniversalVocabularyMt',arg2Isa(programSetCommand,'CharacterString')).
no_initCyc('DoomCurrentStateMt',programSetCommand(sees,"watch")).


no_initCyc('DoomVocabularyMt',isa(programDataTypeString,'BinaryPredicate')).
no_initCyc('DoomVocabularyMt',arg1Isa(programDataTypeString,'DoomRelation')).
no_initCyc('DoomVocabularyMt',arg2Isa(programDataTypeString,'CharacterString')).

no_initCyc('DoomVocabularyMt',isa(programDataTypeNAUT,'BinaryPredicate')).
no_initCyc('DoomVocabularyMt',arg1Isa(programDataTypeNAUT,'DoomRelation')).
no_initCyc('DoomVocabularyMt',arg2Isa(programDataTypeNAUT,'Function')).
%%no_initCyc('DoomVocabularyMt',genlPreds(sizeOf,sizeOfO)).

%no_initCyc('DoomVocabularyMt',genlPreds(enemyOf,considersAsEnemy)).


%==================================================
% INTERFACE MAPPING SETUP
%==================================================

%AspatialInformationStore

no_initCyc('DoomVocabularyMt',isa('locatedAtPoint-Spatial','DoomRelation')).
no_initCyc('DoomVocabularyMt',isa(orientation,'DoomRelation')).
no_initCyc('DoomVocabularyMt',isa(facesObject,'DoomRelation')).
no_initCyc('DoomVocabularyMt',isa(nearestObjectOfTypeTo,'DoomRelation')).
no_initCyc('DoomVocabularyMt',isa(earthCoordinatesOfObject,'DoomRelation')).

no_initCyc('DoomVocabularyMt',isa(nearestObjectOfTypeTo,'DoomRelation')).
no_initCyc('DoomVocabularyMt',isa(nearestObjectOfTypeTo,'DoomRelation')).
no_initCyc('DoomVocabularyMt',isa(nearestObjectOfTypeTo,'DoomRelation')).
no_initCyc('DoomVocabularyMt',isa(nearestObjectOfTypeTo,'DoomRelation')).

no_initCyc('DoomVocabularyMt',programGetCommand('orientation',"getAngles")).
no_initCyc('DoomVocabularyMt',programGetCommand('orientation',"getAngles")).
no_initCyc('DoomVocabularyMt',callableVector('orientation',"getAngles")).
no_initCyc('DoomVocabularyMt',programDataTypeString('orientation',"IdVector")).
no_initCyc('DoomVocabularyMt',programDataTypeNAUT('orientation','Angle2Fn')).

no_initCyc('DoomVocabularyMt',programGetCommand('locatedAtPoint-Spatial',"getWorldOrigin")).
no_initCyc('DoomVocabularyMt',programSetCommand('locatedAtPoint-Spatial',"setOrigin")).
no_initCyc('DoomVocabularyMt',programDataTypeString('locatedAtPoint-Spatial',"IdVector")).
no_initCyc('DoomVocabularyMt',programDataTypeNAUT('locatedAtPoint-Spatial','Point3Fn')).

%==================================================
% ACTION PREDICATE SETUP
%==================================================


no_initCyc('UniversalVocabularyMt',genls('InstancePredicate','StrictlyFunctionalPredicate')).
no_initCyc('UniversalVocabularyMt',genls('InstancePredicate','BinaryPredicate')).
no_initCyc('UniversalVocabularyMt',isa('InstancePredicate','PredicateType')).

        %implies(isa(PRED,'BinaryPredicate'),arity(PRED,2))).


no_initCyc('UniversalVocabularyMt',isa(pathControl,'BinaryPredicate')).
no_initCyc('UniversalVocabularyMt',arg1Isa(pathControl,'PhysicalDevice')).
no_initCyc('UniversalVocabularyMt',arg2Isa(pathControl,'Path-Simple')).


no_initCyc('DoomSharedPlanningMt',isa(doHailACab,'SimpleActionPredicate')).
no_initCyc('DoomSharedPlanningMt',isa(doPayFare,'SimpleActionPredicate')).
no_initCyc('DoomSharedPlanningMt',isa(doRide,'SimpleActionPredicate')).
no_initCyc('DoomSharedPlanningMt',isa(doTravelTo,'ComplexActionPredicate')).
no_initCyc('DoomSharedPlanningMt',isa(doWaitForABusTo,'SimpleActionPredicate')).
no_initCyc('DoomSharedPlanningMt',isa(doWalk,'SimpleActionPredicate')).
no_initCyc('DoomSharedPlanningMt',arg1Isa(busRoute,'Bus-RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',arg1Isa(doHailACab,'Person')).
no_initCyc('DoomSharedPlanningMt',arg1Isa(doPayFare,'Person')).
no_initCyc('DoomSharedPlanningMt',arg1Isa(doRide,'Person')).
no_initCyc('DoomSharedPlanningMt',arg1Isa(doTravelTo,'Person')).
no_initCyc('DoomSharedPlanningMt',arg1Isa(doWaitForABusTo,'Person')).
no_initCyc('DoomSharedPlanningMt',arg1Isa(doWalk,'Person')).
no_initCyc('DoomSharedPlanningMt',arg1Isa(pocketMoney,'Person')).
no_initCyc('DoomSharedPlanningMt',arg2Isa(busRoute,'Place')).
no_initCyc('DoomSharedPlanningMt',arg2Isa(doHailACab,'TaxiCab')).
no_initCyc('DoomSharedPlanningMt',arg2Isa(doPayFare,'RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',arg2Isa(doRide,'RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',arg2Isa(doTravelTo,'Place')).
no_initCyc('DoomSharedPlanningMt',arg2Isa(doWaitForABusTo,'Bus-RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',arg2Isa(doWalk,'Place')).
no_initCyc('DoomSharedPlanningMt',arg2Isa(pocketMoney,'MonetaryValue')).
no_initCyc('DoomSharedPlanningMt',arg3Isa(busRoute,'Place')).
no_initCyc('DoomSharedPlanningMt',arg3Isa(doPayFare,'Place')).
no_initCyc('DoomSharedPlanningMt',arg3Isa(doRide,'Place')).
no_initCyc('DoomSharedPlanningMt',arg3Isa(doWaitForABusTo,'Place')).
no_initCyc('DoomSharedPlanningMt',arg3Isa(doWalk,'Place')).
no_initCyc('DoomSharedPlanningMt',arg4Isa(doPayFare,'Place')).
no_initCyc('DoomSharedPlanningMt',arg4Isa(doRide,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(busRoute,1,'Bus-RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',argIsa(busRoute,2,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(busRoute,3,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(doHailACab,1,'Person')).
no_initCyc('DoomSharedPlanningMt',argIsa(doHailACab,2,'TaxiCab')).
no_initCyc('DoomSharedPlanningMt',argIsa(doPayFare,1,'Person')).
no_initCyc('DoomSharedPlanningMt',argIsa(doPayFare,2,'RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',argIsa(doPayFare,3,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(doPayFare,4,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(doRide,1,'Person')).
no_initCyc('DoomSharedPlanningMt',argIsa(doRide,2,'RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',argIsa(doRide,3,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(doRide,4,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(doTravelTo,1,'Person')).
no_initCyc('DoomSharedPlanningMt',argIsa(doTravelTo,2,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(doWaitForABusTo,1,'Person')).
no_initCyc('DoomSharedPlanningMt',argIsa(doWaitForABusTo,2,'Bus-RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',argIsa(doWaitForABusTo,3,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(doWalk,1,'Person')).
no_initCyc('DoomSharedPlanningMt',argIsa(doWalk,2,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(doWalk,3,'Place')).
no_initCyc('DoomSharedPlanningMt',argIsa(pocketMoney,1,'Person')).
no_initCyc('DoomSharedPlanningMt',argIsa(pocketMoney,2,'MonetaryValue')).


no_initCyc('DoomCurrentStateMt',isa(cyc_bot_1,'IndividualAgent')).
no_initCyc('DoomCurrentStateMt',isa(cyc_bot_1,'IntelligentAgent')).

no_initCyc('DoomCurrentStateMt',isa(player1,'IndividualAgent')).
no_initCyc('DoomCurrentStateMt',isa(player1,'IntelligentAgent')).

%==================================================
% INITIAL SITUATION
%==================================================
no_initCyc('DoomCurrentStateMt',isa('room_start','GeographicalRegion')).
no_initCyc('DoomCurrentStateMt',isa('room_mail','GeographicalRegion')).
no_initCyc('DoomCurrentStateMt',isa('room_storage','GeographicalRegion')).
no_initCyc('DoomCurrentStateMt',isa('room_o103','GeographicalRegion')).
no_initCyc('DoomCurrentStateMt',isa('room_lab2','GeographicalRegion')).
no_initCyc('DoomCurrentStateMt',isa('room_o111','GeographicalRegion')).

% no_initCyc('DoomCurrentStateMt',isa('item_burger','Cheeseburger')).
% no_initCyc('DoomCurrentStateMt',isa('furnishing_table','KitchenTable')).



no_initCyc('DoomCurrentStateMt',objectFoundInLocation('cyc_bot_1','room_start')).
no_initCyc('DoomCurrentStateMt',objectFoundInLocation(item_burger,room_storage)).
no_initCyc('DoomCurrentStateMt',objectFoundInLocation(moveable_mop_1,room_mail)).
no_initCyc('DoomCurrentStateMt',adjacentTo('room_start',room_o103)).
no_initCyc('DoomCurrentStateMt',adjacentTo(room_o103,'room_start')).
no_initCyc('DoomCurrentStateMt',adjacentTo('room_start',room_storage)).
no_initCyc('DoomCurrentStateMt',adjacentTo(room_storage,'room_start')).
no_initCyc('DoomCurrentStateMt',adjacentTo('room_start',room_o111)).
no_initCyc('DoomCurrentStateMt',adjacentTo(room_o111,'room_start')).
no_initCyc('DoomCurrentStateMt',adjacentTo(room_o103,room_mail)).
no_initCyc('DoomCurrentStateMt',adjacentTo(room_mail,room_o103)).
no_initCyc('DoomCurrentStateMt',adjacentTo(room_lab2,'room_start')).

no_initCyc('UniversalVocabularyMt' ,isa(moveable_mop_1,'PhysicalDevice')).

no_initCyc('DoomCurrentStateMt',pathBetween(func_door_8,room_o103,room_lab2)).
no_initCyc('DoomCurrentStateMt',pathControl(moveable_mop_1,func_door_8)).
% no_initCyc('DoomCurrentStateMt',isa('cyc_bot_1',cyc_bot)).



no_initCyc('DoomVocabularyMt',isa('doomPropertyNext','TernaryPredicate')).
no_initCyc('DoomVocabularyMt',arity('doomPropertyNext',3)).

no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_start',isa,"info_location")). 
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_start','locatedAtPoint-Spatial','Point3Fn'(520,0,0))).

no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_o103',isa,"info_location")). 
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_o103','locatedAtPoint-Spatial','Point3Fn'(520,-744.0,0))).

no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_o111',isa,"info_location")). 
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_o111','locatedAtPoint-Spatial','Point3Fn'(520,-1512.0,0))).

no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_lab2',isa,"info_location")). 
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_lab2','locatedAtPoint-Spatial','Point3Fn'(520,1192.0,0))).

no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_mail',isa,"info_location")). 
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_mail','locatedAtPoint-Spatial','Point3Fn'(352,-40.0,0))).

no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_storage',isa,"info_location")).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('room_storage','locatedAtPoint-Spatial','Point3Fn'(-656.0,1192,0))).

no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('item_burger',isa,"moveable_burger")).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('item_burger','locatedAtPoint-Spatial','Point3Fn'(-656.0,1192,0))).

no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('furnishing_table',isa,"moveable_ktable")).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'('furnishing_table','locatedAtPoint-Spatial','Point3Fn'(744.0,136,0))).


/* 
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("info_location",520,0,'room_start')).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("info_location",520,-744.0,room_o103)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("info_location",520,-1512.0,room_o111)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("info_location",520,1192,room_lab2)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("info_location",352,-40.0,room_mail)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("info_location",-656.0,1192,room_storage)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("moveable_burger",-656,1192,item_burger)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("moveable_chair1",520,120,moveable_chair1_1)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("moveable_chair2",620,120,moveable_chair2_1)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("moveable_chair5",720,120,moveable_chair5_1)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("moveable_burgerboxopen",1020,120,moveable_burgerboxopen_1)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("moveable_ktable",744,136,moveable_ktable_1)).
no_initCyc('DoomVocabularyMt', 'doomPropertyNext'("moveable_mop",352,-40.0,moveable_mop_1)).
no_initCyc('DoomCurrentStateMt', 'doomPropertyNext'("cyc_bot",112,0,'cyc_bot_1')).
			      */

% ACTIONS
% doomAction('WalkingOnTwoLegs',Ag,Pos,Pos_1) is the action : Ag moves from Pos to Pos_1
no_initCyc('DoomSharedPlanningMt', 'preconditionFor-Props'((and(isa(Ag,'Person'),and(adjacentTo(Pos,Pos_1) , objectFoundInLocation(Ag,Pos)))),possible(doomAction('WalkingOnTwoLegs',Ag,Pos,Pos_1)))).
no_initCyc('DoomSharedPlanningMt', 'effectOfAction-Props'(doomAction('WalkingOnTwoLegs',Ag,_,Pos_1),objectFoundInLocation(Ag,Pos_1))).
no_initCyc('DoomSharedPlanningMt', 'effectOfAction-Props'(doomAction('WalkingOnTwoLegs',Ag,Pos,_),not(objectFoundInLocation(Ag,Pos)))).

% doomAction('HoldingAnObject',Ag,Obj,Pos) is the action : agent Ag picks up Obj
% when they are both inRegion position Pos.
no_initCyc('DoomSharedPlanningMt', 'preconditionFor-Props'(and(isa(Ag,'Person'),and(different(Ag,Obj),and(objectFoundInLocation(Obj,Pos) , inRegion(Ag,Pos)))),possible(doomAction('HoldingAnObject',Ag,Obj,Pos)))).
no_initCyc('DoomSharedPlanningMt', 'effectOfAction-Props'(doomAction('HoldingAnObject',Ag,Obj,_Pos), possesses(Ag,Obj))).
no_initCyc('DoomSharedPlanningMt', 'effectOfAction-Props'(doomAction('HoldingAnObject',_Ag,Obj,Pos), not(objectFoundInLocation(Obj,Pos)))).

% doomAction('AbandoningSomething',Ag,Obj,Pos) is the action : Ag puts down Obj inRegion Pos
no_initCyc('DoomSharedPlanningMt', 'preconditionFor-Props'(and(isa(Ag,'Person'),and(different(Ag,Obj),and(inRegion(Ag,Pos) , possesses(Ag,Obj)))),possible(doomAction('AbandoningSomething',Ag,Obj,Pos)))).
no_initCyc('DoomSharedPlanningMt', 'effectOfAction-Props'(doomAction('AbandoningSomething',_Ag,Obj,Pos),objectFoundInLocation(Obj,Pos))).
no_initCyc('DoomSharedPlanningMt', 'effectOfAction-Props'(doomAction('AbandoningSomething',Ag,Obj,_Pos),not(possesses(Ag,Obj)))).

% doomAction('UnlockingALock',Ag,Door) is the action : agent Ag unlocks Door
no_initCyc('DoomSharedPlanningMt', 'preconditionFor-Props'(and(isa(Ag,'Person'),and( pathBetween(Door,R1,_), and(pathControl(Key,Door),and(possesses(Ag,Key),inRegion(Ag,R1))))),possible(doomAction('UnlockingALock',Ag,Door)))).
no_initCyc('DoomSharedPlanningMt', 'effectOfAction-Props'(doomAction('UnlockingALock',_Ag,Door),pathState(Door,'PathOpen'))).

% DERIVED RELATIONS
no_initCyc('DoomSharedPlanningMt', 'preconditionFor-Props'(objectFoundInLocation(Obj,Pos),inRegion(Obj,Pos))).
no_initCyc('DoomSharedPlanningMt', 'preconditionFor-Props'(and(isa(Ag,'Person'),and(different(Ag , Obj),and(possesses(Ag,Obj), inRegion(Ag,Pos)))),inRegion(Obj,Pos))).
no_initCyc('DoomSharedPlanningMt', 'preconditionFor-Props'(and(pathBetween(Door,R1,P_2) , pathState(Door,'PathOpen')),adjacentTo(R1,P_2))).


no_initCyc('DoomVocabularyMt',isa('doomPropertyNext','TernaryPredicate')).
no_initCyc('DoomVocabularyMt',isa('Point3Fn','TernaryFunction')).
no_initCyc('DoomVocabularyMt',isa('Point3Fn','UnreifiableFunction')).
no_initCyc('DoomVocabularyMt',resultIsa('Point3Fn','Point')).
no_initCyc('DoomVocabularyMt',resultIsa('Point3Fn','GeographicalPlace-0D')).
no_initCyc('DoomVocabularyMt',resultIsa('Angle2Fn','OrientationVector')).
no_initCyc('DoomVocabularyMt',resultIsa('Point3Fn','PositionVector')).

%resultIsa('Point3Fn','SpatialThing-Localized')).
%resultIsa('Point3Fn','SpatialThing')).
no_initCyc('UniversalVocabularyMt',arg1Isa('Point3Fn','RealNumber')).
no_initCyc('UniversalVocabularyMt',arg2Isa('Point3Fn','RealNumber')).
no_initCyc('UniversalVocabularyMt',arg3Isa('Point3Fn','RealNumber')).
no_initCyc('UniversalVocabularyMt',comment('Point3Fn',"A ?point in a Cartesian coordinate system.")).



no_initCyc('DoomVocabularyMt',isa('ModelFn','ReifiableFunction')).
no_initCyc('DoomVocabularyMt',arg2Isa('ModelFn','Thing')).
/*

no_initCyc('DoomVocabularyMt',isa('setDoomArgs','TernaryPredicate')).

no_initCyc('DoomVocabularyMt',isa('RotationFn','ReifiableFunction')).
no_initCyc('DoomVocabularyMt',arity('RotationFn',9)).
no_initCyc('DoomVocabularyMt',resultIsa('RotationFn','SpatialThing-Localized')).
no_initCyc('DoomVocabularyMt',resultIsa('RotationFn','SpatialThing')).
no_initCyc('UniversalVocabularyMt',arg1Isa('RotationFn','RealNumber')).
no_initCyc('UniversalVocabularyMt',arg2Isa('RotationFn','RealNumber')).
no_initCyc('UniversalVocabularyMt',arg3Isa('RotationFn','RealNumber')).
no_initCyc('UniversalVocabularyMt',arg4Isa('RotationFn','RealNumber')).
no_initCyc('UniversalVocabularyMt',arg5Isa('RotationFn','RealNumber')).
no_initCyc('UniversalVocabularyMt',arg6Isa('RotationFn','RealNumber')).
no_initCyc('UniversalVocabularyMt',argIsa('RotationFn',7,'RealNumber')).
no_initCyc('UniversalVocabularyMt',argIsa('RotationFn',8,'RealNumber')).
no_initCyc('UniversalVocabularyMt',argIsa('RotationFn',9,'RealNumber')).
no_initCyc('UniversalVocabularyMt',comment('RotationFn',"A ?rotation in a Cartesian coordinate system.")).



no_initCyc('DoomVocabularyMt',isa('CompassProperty','Collection')).
no_initCyc('DoomVocabularyMt',isa('CompassProperty','PredicateType')).
no_initCyc('DoomVocabularyMt',genls('CompassProperty','BinaryPredicate')).
no_initCyc('DoomVocabularyMt',isa(nearestEastOf,'CompassProperty')).
no_initCyc('DoomVocabularyMt',isa(nearestWestOf,'CompassProperty')).
no_initCyc('DoomVocabularyMt',isa(nearestNorthOf,'CompassProperty')).
no_initCyc('DoomVocabularyMt',isa(nearestSouthOf,'CompassProperty')).
no_initCyc('DoomVocabularyMt',isa(nearestNorthWestOf,'CompassProperty')).
no_initCyc('DoomVocabularyMt',isa(nearestNorthEastOf,'CompassProperty')).
no_initCyc('DoomVocabularyMt',isa(nearestSouthEastOf,'CompassProperty')).
no_initCyc('DoomVocabularyMt',isa(nearestSouthWestOf,'CompassProperty')).
no_initCyc('DoomVocabularyMt',isa('exactlyLocatedAt-Spatial','CompassProperty')).

no_initCyc('DoomVocabularyMt',inverseBinaryPredicateOf(nearestNorthOf,nearestSouthOf)).
no_initCyc('DoomVocabularyMt',inverseBinaryPredicateOf(nearestNorthOf,nearestSouthOf)).
no_initCyc('DoomVocabularyMt',inverseBinaryPredicateOf(nearestSouthWestOf,nearestNorthEastOf)).
no_initCyc('DoomVocabularyMt',inverseBinaryPredicateOf(nearestSouthEastOf,nearestNorthWestOf)).

%and(nearestNorthOf(O1,O2),
no_initCyc('DoomVocabularyMt',isa(locatedXYZ,'QuaternaryPredicate')).
no_initCyc('DoomVocabularyMt',arity(locatedXYZ,4)).
argIsa(locatedXYZ,1,'SpatialThing')).
argIsa(locatedXYZ,2,'RealNumber')).
argIsa(locatedXYZ,3,'RealNumber')).
argIsa(locatedXYZ,4,'RealNumber')).
%argFormat(locatedXYZ,1,'SingleEntry')).

comment(locatedXYZ,"Is a #$QuaternaryPredicate Representing the (x,y,z) #$Point3Fn of #$locatedAtPoint-Spatial")).

*/
%equiv(locatedXYZ(O,X,Y,Z),'locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)))).


no_initCyc('UniversalVocabularyMt',arg2Format('locatedAtPoint-Spatial','SingleEntry')).

/*
no_initCyc('DoomVocabularyMt',isa('DurableGood','DoomClass')).
no_initCyc('DoomVocabularyMt',isa('Artifact-HumanCreated','DoomClass')).
'DoomVocabularyMt' :isa('SmallArm-Weapon','DoomClass')).
'DoomVocabularyMt' :isa('Agent-Generic','DoomClass')).
*/


%implies(locatedXYZ(O,X,Y,Z),termOfUnit('Point3Fn'(X,Y,Z),'Point3Fn'(X,Y,Z)))).

%implies(and(locatedXYZ(O,X,Y,Z),termOfUnit(POINTFN,'Point3Fn'(X,Y,Z))),'locatedAtPoint-Spatial'(O,POINTFN))).

  /*
implies(and(locatedXYZ(O,X,Y,Z),nearestSouthEastOf(O,T)),locatedXYZ(T,'PlusFn'(50,X),'PlusFn'(50,Y),Z))).
implies(and(locatedXYZ(O,X,Y,Z),nearestSouthWestOf(O,T)),locatedXYZ(T,'PlusFn'(-50,X),'PlusFn'(50,Y),Z))).
implies(and(locatedXYZ(O,X,Y,Z),nearestNorthWestOf(O,T)),locatedXYZ(T,'PlusFn'(-50,X),'PlusFn'(-50,Y),Z))).
implies(and(locatedXYZ(O,X,Y,Z),nearestNorthEastOf(O,T)),locatedXYZ(T,'PlusFn'(50,X),'PlusFn'(-50,Y),Z))).
implies(and(locatedXYZ(O,X,Y,Z),nearestSouthOf(O,T)),locatedXYZ(T,X,'PlusFn'(50,Y),Z))).
implies(and(locatedXYZ(O,X,Y,Z),nearestNorthOf(O,T)),locatedXYZ(T,X,'PlusFn'(-50,Y),Z))).
implies(and(locatedXYZ(O,X,Y,Z),nearestEastOf(O,T)),locatedXYZ(T,'PlusFn'(50,X),Y,Z))).
implies(and(locatedXYZ(O,X,Y,Z),nearestWestOf(O,T)),locatedXYZ(T,'PlusFn'(-50,X),Y,Z))).
implies(and(locatedXYZ(O,X,Y,Z),cospatial(O,T)),locatedXYZ(T,X,Y,Z))).


implies(and('locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)),nearestSouthEastOf(O,T)),'locatedAtPoint-Spatial'(T,'Point3Fn'('PlusFn'(50,X),'PlusFn'(50,Y),Z)))).
implies(and('locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)),nearestSouthWestOf(O,T)),'locatedAtPoint-Spatial'(T,'Point3Fn'('PlusFn'(-50,X),'PlusFn'(50,Y),Z)))).
implies(and('locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)),nearestNorthWestOf(O,T)),'locatedAtPoint-Spatial'(T,'Point3Fn'('PlusFn'(-50,X),'PlusFn'(-50,Y),Z)))).
implies(and('locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)),nearestNorthEastOf(O,T)),'locatedAtPoint-Spatial'(T,'Point3Fn'('PlusFn'(50,X),'PlusFn'(-50,Y),Z)))).
implies(and('locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)),nearestSouthOf(O,T)),'locatedAtPoint-Spatial'(T,'Point3Fn'(X,'PlusFn'(50,Y),Z)))).
implies(and('locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)),nearestNorthOf(O,T)),'locatedAtPoint-Spatial'(T,'Point3Fn'(X,'PlusFn'(-50,Y),Z)))).
implies(and('locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)),nearestEastOf(O,T)),'locatedAtPoint-Spatial'(T,'Point3Fn'('PlusFn'(50,X),Y,Z)))).
implies(and('locatedAtPoint-Spatial'(O,'Point3Fn'(X,Y,Z)),nearestWestOf(O,T)),'locatedAtPoint-Spatial'(T,'Point3Fn'('PlusFn'(-50,X),Y,Z)))).
implies(and('locatedAtPoint-Spatial'(O,P),cospatial(O,T)),'locatedAtPoint-Spatial'(T,P))).
  */


	    /*

% =========================================================
% VenueSetup / VenueInstance
% =========================================================
no_initCyc('DoomVocabularyMt',isa('VenueInstance','Collection')).
no_initCyc('DoomVocabularyMt',genls('VenueInstance','Instance')).
no_initCyc('DoomVocabularyMt',isa('VenueSetup','Collection')).
no_initCyc('DoomVocabularyMt',isa('VenueSetup','CollectionType')).
%genls('VenueSetup','DoomClass')).

%implies(and(isa(SC,'VenueSetup'),isa(SI,SC),isa(SI,'VenueInstance'),isa(OC,'DoomClass'),relationAllExists(P,SC,OC)),thereExists(O,and(isa(O,OC),holds(P,SI,O))))).

% =========================================================
% FourWayDinning 
% =========================================================
no_initCyc('DoomVocabularyMt',isa('FourWayDinning','Collection')).
no_initCyc('DoomVocabularyMt',isa('FourWayDinning','VenueSetup')).
no_initCyc('DoomVocabularyMt',genls('FourWayDinning','DiningRoom-Home')).

no_initCyc('DoomVocabularyMt',isa('item_table_center','Collection')).
no_initCyc('DoomVocabularyMt',isa('item_chair_north','Collection')).
no_initCyc('DoomVocabularyMt',isa('item_chair_south','Collection')).
no_initCyc('DoomVocabularyMt',isa('item_chair_east','Collection')).
no_initCyc('DoomVocabularyMt',isa('item_chair_west','Collection')).

no_initCyc('DoomVocabularyMt',genls('item_table_center','Table-PieceOfFurniture')).
no_initCyc('DoomVocabularyMt',genls('item_chair_north','DiningRoomChair')).
no_initCyc('DoomVocabularyMt',genls('item_chair_south','DiningRoomChair')).
no_initCyc('DoomVocabularyMt',genls('item_chair_east','DiningRoomChair')).  
no_initCyc('DoomVocabularyMt',genls('item_chair_west','DiningRoomChair')).
no_initCyc('DoomVocabularyMt',genls('item_Food_sw','Food')).


no_initCyc('DoomVocabularyMt', relationAllExists('exactlyLocatedAt-Spatial','FourWayDinning','item_table_center')).
no_initCyc('DoomVocabularyMt', relationAllExists(nearestNorthOf,'FourWayDinning','item_chair_north')).
no_initCyc('DoomVocabularyMt', relationAllExists(nearestSouthOf,'FourWayDinning','item_chair_south')).
no_initCyc('DoomVocabularyMt', relationAllExists(nearestWestOf,'FourWayDinning','item_chair_west')).
no_initCyc('DoomVocabularyMt', relationAllExists(nearestEastOf,'FourWayDinning','item_chair_east')).



no_initCyc('DoomVocabularyMt', 'locatedAtPoint-Spatial'('FourWayDinning_1','Point3Fn'(-72,64,0.25))).
no_initCyc('DoomVocabularyMt', isa('FourWayDinning_1','FourWayDinning')).

no_initCyc('DoomVocabularyMt', implies(and(isa(SC,'VenueSetup'),isa(SI,SC)),isa(SI,'VenueInstance'))).





% chair (MeaningInSystemFn WordNet-1997Version "N00393476")






no_initCyc('DoomVocabularyMt',isa('IdClass','Collection')).
% no_initCyc('DoomVocabularyMt',isa('IdClass','CollectionType')).

no_initCyc('DoomVocabularyMt',isa('IdPlayer','IdClass')).
no_initCyc('DoomVocabularyMt',isa('IdLight','IdClass')).
no_initCyc('DoomVocabularyMt',isa('IdItem','IdClass')).
no_initCyc('DoomVocabularyMt',isa('IdWeapon','IdClass')).

no_initCyc('DoomVocabularyMt',genls('IdWeapon','SmallArm-Weapon')).
no_initCyc('DoomVocabularyMt',genls('IdItem','Artifact')).
no_initCyc('DoomVocabularyMt',genls('IdMovable','Artifact')).
no_initCyc('DoomVocabularyMt',genls('IdPlayer','Agent-Generic')).
no_initCyc('DoomVocabularyMt',genls('IdAI','Agent-Generic')).

no_initCyc('DoomVocabularyMt',genls('weapon_pistol','Handgun')).
no_initCyc('DoomVocabularyMt',genls('Pistol','Handgun')).
*/
%no_initCyc('DoomVocabularyMt',genls('IdLight','LightingDevice')).
%no_initCyc('DoomVocabularyMt',genls('IdPlayer','Person')).





no_initCyc('UniversalVocabularyMt',isa(doomCollection,'BinaryPredicate')).


no_initCyc('UniversalVocabularyMt',isa(doomCollection,'BinaryPredicate')).
no_initCyc('UniversalVocabularyMt',arg1Isa(doomCollection,'Thing')).
no_initCyc('UniversalVocabularyMt',arg2Isa(doomCollection,'CharacterString')).

%implies(and(isa(OBJ,'Instance'),isa(OBJ,COL),doomCollection(COL,TYPE)),doomObject(OBJ,TYPE))).


/*

ammo_cells_large_mp
ArtilleryShell ammo_shells_large_mp
item_armor_security_mp
weapon_chaingun_mp
weapon_plasmagun_mp

%isa(missingIsa,'BinaryPredicate')).
%implies(not('no_initCyc-Asserted'('DoomCurrentStateMt',isa(OBJ,COL))),missingIsa(OBJ,COL))).

%isa('no_initCyc-Missing','BinaryPredicate')).
%equiv(not('no_initCyc-Asserted'('DoomCurrentStateMt',isa(OBJ,COL))),('no_initCyc-Missing'('DoomCurrentStateMt',isa(OBJ,COL))))).
%equiv(('no_initCyc-Asserted'('DoomCurrentStateMt',isa(OBJ,COL))),not('no_initCyc-Missing'('DoomCurrentStateMt',isa(OBJ,COL))))).


%no_initCyc('UniversalVocabularyMt',arg1Isa(doomPropertyNext,'Instance')).
%no_initCyc('UniversalVocabularyMt',arg1Isa(doomObject,'Instance')).
%no_initCyc('UniversalVocabularyMt',arg2Isa(doomObject,'DoomClass')).

*/

              
no_initCyc('DoomVocabularyMt',doomCollection('Person',"human_marine_pistol")).
no_initCyc('DoomVocabularyMt',doomCollection('Terrorist',"human_marine_machinegun")).
no_initCyc('DoomVocabularyMt',doomCollection('Agent-Generic',"cyc_bot")).
no_initCyc('DoomVocabularyMt',doomCollection('Person',"cyc_bot")).
no_initCyc('DoomVocabularyMt',doomCollection('Path-Simple',"idDoor")).
no_initCyc('DoomVocabularyMt',doomCollection('Doorway',"idDoor")).
no_initCyc('DoomVocabularyMt', doomCollection('DiningRoomChair',"moveable_kitchenchair")).
no_initCyc('DoomVocabularyMt', doomCollection('Table-PieceOfFurniture',"moveable_Table_centercart1")).
no_initCyc('DoomVocabularyMt', doomCollection('LightingDevice',"light")).
no_initCyc('DoomVocabularyMt', doomCollection('MachineGun',"weapon_machinegun_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('BodyArmor',"item_armor_shard_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('Vest-Bulletproof',"item_armor_shard_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('MissileLauncher',"weapon_rocketlauncher_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('Shotgun',"weapon_shotgun_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('HandGrenade',"weapon_handgrenade_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('Handgun',"weapon_pistol_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('MedicalDevice',"item_medkit_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('MedicalDevice',"item_medkit_small_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('AmmunitionBelt',"ammo_belt_small_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('AmmunitionBelt',"ammo_clip_large_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('ProjectileShell-Blast',"ammo_rockets_small_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('ProjectileShell-Blast',"ammo_rockets_large_mp")).
no_initCyc('DoomVocabularyMt', doomCollection('Scientist',"skins/characters/npcs/body_labcoat_lantern.skin")).
no_initCyc('DoomVocabularyMt', doomCollection('Scientist',"alphalabs2_scientist1")).
no_initCyc('DoomVocabularyMt', doomCollection('Scientist',"alphalabs2_scientist1")).
no_initCyc('DoomVocabularyMt',doomCollection('ScrapMetal',"debris_barrelpiece")).  %Dirt
no_initCyc('DoomVocabularyMt',doomCollection('ScrapMetal',"debris_barrelpiece2")).
no_initCyc('DoomVocabularyMt',doomCollection('ContainerLid',"debris_barreltop")).
no_initCyc('DoomVocabularyMt',doomCollection('ContainerLid',"debris_barreltop2")).
no_initCyc('DoomVocabularyMt',doomCollection('BarrelContainer',"moveable_barrel1")).
no_initCyc('DoomVocabularyMt',doomCollection('BarrelContainer',"moveable_barrel2")).
no_initCyc('DoomVocabularyMt',doomCollection('BarrelContainer',"moveable_barrel3")).
no_initCyc('DoomVocabularyMt',doomCollection('BarrelContainer',"moveable_base_barrel")).
no_initCyc('DoomVocabularyMt',doomCollection('Boulder',"moveable_base_boulder")).
no_initCyc('DoomVocabularyMt',doomCollection('Brick',"moveable_base_brick")).
no_initCyc('DoomVocabularyMt',doomCollection('DominoesDoom',"moveable_base_domino")).
no_initCyc('DoomVocabularyMt',doomCollection('SpatialThing-Localized',"moveable_base_fixed")).
no_initCyc('DoomVocabularyMt',doomCollection('Vial',"moveable_beaker")).
no_initCyc('DoomVocabularyMt',doomCollection('LaptopComputer',"moveable_blaptop")).
no_initCyc('DoomVocabularyMt',doomCollection('Bottle',"moveable_bottle1")).
no_initCyc('DoomVocabularyMt',doomCollection('Cheeseburger',"moveable_burger")).
no_initCyc('DoomVocabularyMt',doomCollection('PlasticFoodContainer',"moveable_burgerboxclose")).
no_initCyc('DoomVocabularyMt',doomCollection('PlasticFoodContainer',"moveable_burgerboxopen")).
no_initCyc('DoomVocabularyMt',doomCollection('LiquidStorageTank',"moveable_burningbarrel")).
no_initCyc('DoomVocabularyMt',doomCollection('LiquidStorageTank',"moveable_burningtank")).
no_initCyc('DoomVocabularyMt',doomCollection('Canister',"moveable_cannister")).
no_initCyc('DoomVocabularyMt',doomCollection('WaxedCardboardCarton',"moveable_cartonbox1")).
no_initCyc('DoomVocabularyMt',doomCollection('CardboardBox',"moveable_cartonbox2")).
no_initCyc('DoomVocabularyMt',doomCollection('CardboardCanister',"moveable_cartonbox3")).
no_initCyc('DoomVocabularyMt',doomCollection('BoxTheContainer',"moveable_cartonbox4")).
no_initCyc('DoomVocabularyMt',doomCollection('Crate',"moveable_cartonbox5")).
no_initCyc('DoomVocabularyMt',doomCollection('Crate',"moveable_cartonbox6")).
no_initCyc('DoomVocabularyMt',doomCollection('BoxTheContainer',"moveable_cartonbox7")).
no_initCyc('DoomVocabularyMt',doomCollection('BoxTheContainer',"moveable_cartonbox8")).
no_initCyc('DoomVocabularyMt',doomCollection('SwivelChair',"moveable_chair1")).
no_initCyc('DoomVocabularyMt',doomCollection('ArmChair',"moveable_chair2")).
no_initCyc('DoomVocabularyMt',doomCollection('DiningRoomChair',"moveable_chair5")).
no_initCyc('DoomVocabularyMt',doomCollection('Can',"moveable_cokecan")).
no_initCyc('DoomVocabularyMt',doomCollection('ComputerWorkstation',"moveable_compcart")).
no_initCyc('DoomVocabularyMt',doomCollection('Computer',"moveable_computer")).
no_initCyc('DoomVocabularyMt',doomCollection('Cone',"moveable_cone")).
no_initCyc('DoomVocabularyMt',doomCollection('DeskLamp',"moveable_desklamp")).
no_initCyc('DoomVocabularyMt',doomCollection('Diamond-Gem',"moveable_diamondbox")).
no_initCyc('DoomVocabularyMt',doomCollection('Diamond',"moveable_diamondbox_sm")).
no_initCyc('DoomVocabularyMt',doomCollection('BarrelContainer',"moveable_explodingbarrel")).
no_initCyc('DoomVocabularyMt',doomCollection('LiquidStorageTank',"moveable_explodingtank")).
no_initCyc('DoomVocabularyMt',doomCollection('VerticalFileCabinet-PieceOfFurniture',"moveable_filecabinet1")).
no_initCyc('DoomVocabularyMt',doomCollection('FireExtinguisher',"moveable_fireext")).
no_initCyc('DoomVocabularyMt',doomCollection('DrinkingGlass',"moveable_foamcup")).
no_initCyc('DoomVocabularyMt',doomCollection('ElectronicDevice',"moveable_gizmo1")).
no_initCyc('DoomVocabularyMt',doomCollection('ComputerHardwareItem',"moveable_gizmo2")).
no_initCyc('DoomVocabularyMt',doomCollection('CustomModification',"moveable_gizmo3")).
no_initCyc('DoomVocabularyMt',doomCollection('Brick',"moveable_guardian_brick")).
no_initCyc('DoomVocabularyMt',doomCollection('ComputerMonitor-Color',"moveable_hangingmonitor")).
no_initCyc('DoomVocabularyMt',doomCollection('HypodermicSyringe',"moveable_infusion")).
no_initCyc('DoomVocabularyMt',doomCollection('Lantern',"moveable_item_lantern_world")).
no_initCyc('DoomVocabularyMt',doomCollection('ComputerKeyboard',"moveable_keyboard1")).
no_initCyc('DoomVocabularyMt',doomCollection('DiningRoomChair',"moveable_kitchenchair")).
no_initCyc('DoomVocabularyMt',doomCollection('EatingTable',"moveable_ktable")).
no_initCyc('DoomVocabularyMt',doomCollection('LaptopComputer',"moveable_laptop")).
no_initCyc('DoomVocabularyMt',doomCollection('SafeTheLocker',"moveable_metalbox1")).
no_initCyc('DoomVocabularyMt',doomCollection('Microscope',"moveable_microscope")).
no_initCyc('DoomVocabularyMt',doomCollection('ComputerMonitor-Color',"moveable_monitor")).
no_initCyc('DoomVocabularyMt',doomCollection('FlatPanelDisplay',"moveable_monitorflip")).
no_initCyc('DoomVocabularyMt',doomCollection('Mop',"moveable_mop")).
no_initCyc('DoomVocabularyMt',doomCollection('Bucket',"moveable_mopbucket")).
no_initCyc('DoomVocabularyMt',doomCollection('Chair-PieceOfFurniture',"moveable_normchair")).
no_initCyc('DoomVocabularyMt',doomCollection('Paper',"moveable_paperwad")).
no_initCyc('DoomVocabularyMt',doomCollection('TIPersonalComputer',"moveable_pc1")).
no_initCyc('DoomVocabularyMt',doomCollection('CellularTelephone',"moveable_phone")).
no_initCyc('DoomVocabularyMt',doomCollection('GarbageCan',"moveable_plasticbin")).
no_initCyc('DoomVocabularyMt',doomCollection('PlasticBox',"moveable_plasticbinmini")).
no_initCyc('DoomVocabularyMt',doomCollection('PlasticJar',"moveable_plasticjar1")).
no_initCyc('DoomVocabularyMt',doomCollection('Jar',"moveable_plasticjar2")).
no_initCyc('DoomVocabularyMt',doomCollection('Cooler-Container',"moveable_spigotcan")).
no_initCyc('DoomVocabularyMt',doomCollection('ComputerStand',"moveable_tablecart1")).
no_initCyc('DoomVocabularyMt',doomCollection('ShoppingCart',"moveable_tablecart2")).
no_initCyc('DoomVocabularyMt',doomCollection('GliderChair',"moveable_tech_chair1")).
no_initCyc('DoomVocabularyMt',doomCollection('Wastebasket',"moveable_trashcan01")).
no_initCyc('DoomVocabularyMt',doomCollection('ServingTray',"moveable_tray")).
no_initCyc('DoomVocabularyMt',doomCollection('FloorLamp',"moveable_utilitylamp")).
no_initCyc('DoomVocabularyMt',doomCollection('Wrench',"moveable_wrench")).
no_initCyc('DoomVocabularyMt',doomCollection('MarinePersonnel',"model_mp_marine")).
no_initCyc('DoomVocabularyMt',doomCollection('HomoSapiens',"idPlayer")).
no_initCyc('DoomVocabularyMt',doomCollection('Agent-Generic',"idAI")).
no_initCyc('DoomVocabularyMt',doomCollection('MarinePersonnel',"player_doommarine_mp")).
 

no_initCyc('DoomCurrentStateMt',forward(implies(and(doomCollection(Col1,String),doomType(Obj,String)),ist('DoomCurrentStateMt',isa(Obj,Col1))))).

no_initCyc('UniversalVocabularyMt',isa(doomType,'Predicate')).
no_initCyc('UniversalVocabularyMt',isa(doomType,'BinaryPredicate')).
%no_initCyc('UniversalVocabularyMt',arg1Isa(doomType,'Instance')).
%no_initCyc('UniversalVocabularyMt',arg2Isa(doomType,'CharacterString')).
%no_initCyc('UniversalVocabularyMt',genlPreds(doomType,isa)).

% no_initCyc('DoomVocabularyMt',doomType(cyc_bot_1,"cyc_bot")).


no_initCyc('DoomVocabularyMt',forward(implies(and(isa(Obj,'Instance'),doomCollection(Col,String),doomType(Obj,String)),isa(Obj,Col)))).
no_initCyc('DoomCurrentStateMt',forward(implies(and(doomCollection(Col,String),doomPropertyNext(Obj,isa,String)),ist('DoomCurrentStateMt',isa(Obj,Col))))).
%%no_initCyc('DoomVocabularyMt',forward(implies(and(doomCollection(Col,String),doomType(Obj,String)),isa(Obj,Col)))).


                      /*
no_initCyc('UniversalVocabularyMt' ,arity(classname,2)).
no_initCyc('UniversalVocabularyMt' ,arity(doomclass,2)).
no_initCyc('BaseKB' ,genlPreds(classname,isa)).
no_initCyc('BaseKB' ,genlPreds(doomclass,isa)).

no_initCyc('DoomVocabularyMt',arity('locatedAtPoint-Spatial',2)).
no_initCyc('DoomVocabularyMt',implies(origin(OBJ,VAL),and('locatedAtPoint-Spatial'(OBJ,VAL),isa(OBJ,'Instance')))).
%equiv(doomPropertyNext(OBJ,origin,'Point3Fn'(X,Y,Z)),and(and(latitude(OBJ,X),longitude(OBJ,Y)),altitudeAboveSeaLevel(OBJ,Z)))).

%altitudeAboveSeaLevel
%implies(doomPropertyNext(OBJ,'model',What),isa(OBJ,'CollectionFn'(What)))).

forward(implies(doomPropertyNext(OBJ,doomclass,VAL),and(isa(OBJ,VAL),isa(VAL,'IdClass'))))).

forward(implies(doomPropertyNext(OBJ,editor_usage,VAL),comment(OBJ,VAL)))).
forward(implies(doomPropertyNext(OBJ,editor_usage1,VAL),comment(OBJ,VAL)))).
forward(implies(doomPropertyNext(OBJ,editor_usage2,VAL),comment(OBJ,VAL)))).


no_initCyc('DoomCurrentStateMt',implies(doomPropertyNext(OBJ,noclipmodel,0),no_initCyc('DoomVocabularyMt',isa(OBJ,'SolidTangibleThing')))).
no_initCyc('DoomCurrentStateMt',implies(doomPropertyNext(OBJ,noclipmodel,1),no_initCyc('DoomVocabularyMt',isa(OBJ,'IntangibleExistingThing')))).
no_initCyc('DoomCurrentStateMt',implies(doomPropertyNext(OBJ,classname,CLASS),entityDef(CLASS))).

arg3Format('doomPropertyNext','SingleEntry')).

no_initCyc('DoomCurrentStateMt',implies(doomclass(_OBJ,CLASS),isa(CLASS,'DoomClass'))).
no_initCyc('DoomCurrentStateMt',implies(classname(_OBJ,CLASS),isa(CLASS,'DoomClass'))).
no_initCyc('DoomCurrentStateMt',implies(model(_OBJ,CLASS),isa(CLASS,'Model'))).
%no_initCyc('DoomCurrentStateMt',implies(skin(OBJ,CLASS),genls(CLASS,'Skin'))).
no_initCyc('DoomCurrentStateMt' ,equiv(solid(OBJ,0),nonsolid(OBJ,1))).
no_initCyc('DoomCurrentStateMt' ,equiv(nonsolid(OBJ,0),solid(OBJ,1))).
no_initCyc('DoomCurrentStateMt' ,equiv(nonsolid(OBJ,1),and(not(isa(OBJ,'SolidTangibleThing')),isa(OBJ,'PartiallyTangible')))).
no_initCyc('DoomCurrentStateMt' ,equiv(solid(OBJ,1),and(not(isa(OBJ,'EmptyRegion-Generic')),isa(OBJ,'SolidTangibleThing')))).
                                 */

%no_initCyc('DoomSharedPlanningMt',implies('sentence-Implies'(A,B),'preconditionFor-Props'(A,B))).
no_initCyc('DoomVocabularyMt',forward(implies(doomCollection(C,T),and(isa(C,'DoomClass'))))). %,implies(doomType(O,T),isa(O,C))))).

no_initCyc('BaseKB',isa('DoomSharedPlanningMt','PlanningDomainMicrotheory')).
no_initCyc('DoomSharedPlanningMt',isa(player1,'Person')).
no_initCyc('DoomSharedPlanningMt',isa('Bus1','Bus-RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',isa('Bus2','Bus-RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',isa('Bus3','Bus-RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',isa('room_start','OutdoorLocation')).
no_initCyc('DoomSharedPlanningMt',isa('room_start','Sunny')).
no_initCyc('DoomSharedPlanningMt',isa(cyc_bot_1,'Person')).
no_initCyc('DoomSharedPlanningMt',isa('Park1','OutdoorLocation')).
no_initCyc('DoomSharedPlanningMt',isa('Suburb1','OutdoorLocation')).
no_initCyc('DoomSharedPlanningMt',isa('TaxiCab','RoadVehicleTypeByUse')).
no_initCyc('DoomSharedPlanningMt',genls('TaxiCab','Automobile')).
no_initCyc('DoomSharedPlanningMt',genls('TaxiCab','RoadVehicle')).
no_initCyc('DoomSharedPlanningMt',genls('TaxiCab','SomethingExisting')).
no_initCyc('DoomSharedPlanningMt',isa('Taxi1','TaxiCab')).
no_initCyc('DoomSharedPlanningMt',isa('Uptown1','OutdoorLocation')).
no_initCyc('DoomSharedPlanningMt',busRoute('Bus1','room_start','Park1')).
no_initCyc('DoomSharedPlanningMt',busRoute('Bus2','room_start','Uptown1')).
no_initCyc('DoomSharedPlanningMt',busRoute('Bus3','room_start','Suburb1')).
no_initCyc('DoomSharedPlanningMt',comment(doRide,'(doRide PERS VEH FROM TO) means that a person PERS rides with the #$RoadVehicle VEH from #$Place FROM to location TO.')).
no_initCyc('DoomSharedPlanningMt',distanceBetween('room_start','Park1','Kilometer'(2))).
no_initCyc('DoomSharedPlanningMt',distanceBetween('room_start','Suburb1','Kilometer'(12))).
no_initCyc('DoomSharedPlanningMt',distanceBetween('room_start','Uptown1','Kilometer'(8))).
no_initCyc('DoomSharedPlanningMt',implies(and(greaterThan(_514,'Dollar-UnitedStates'(1)),pocketMoney(_520,_514),objectFoundInLocation(_520,_524),busRoute(_529,_524,_530)),methodForAction(doTravelTo(_520,_530),actionSequence('TheList'(doWaitForABusTo(_520,_529,_530),doPayFare(_520,_529,_524,_530),doRide(_520,_529,_524,_530)))))).
no_initCyc('DoomSharedPlanningMt',implies(and(isa(_772,'Person'),objectFoundInLocation(_772,_776),greaterThan('Kilometer'(0.5),_786),distanceBetween(_776,_791,_786)),methodForAction(doTravelTo(_772,_791),actionSequence('TheList'(doWalk(_772,_776,_791)))))).
no_initCyc('DoomSharedPlanningMt',implies(and(objectFoundInLocation(_1417,_1418),pocketMoney(_1417,_1422),greaterThan(_1422,'Dollar-UnitedStates'('PlusFn'(_1431,1.5))),objectFoundInLocation(_1439,_1418),distanceBetween(_1418,_1444,'Kilometer'(_1431))),methodForAction(doTravelTo(_1417,_1444),actionSequence('TheList'(doHailACab(_1417,_1439),doRide(_1417,_1439,_1418,_1444),doPayFare(_1417,_1439,_1418,_1444)))))).
no_initCyc('DoomSharedPlanningMt',implies(and(weather(_998,'Sunny'),objectFoundInLocation(_1002,_998),isa(_1002,'Person'),greaterThan('Kilometer'(3),_1011),distanceBetween(_998,_1016,_1011)),methodForAction(doTravelTo(_1002,_1016),actionSequence('TheList'(doWalk(_1002,_998,_1016)))))).
no_initCyc('DoomSharedPlanningMt',objectFoundInLocation(player1,'room_start')).
no_initCyc('DoomSharedPlanningMt',objectFoundInLocation(cyc_bot_1,'room_start')).
no_initCyc('DoomSharedPlanningMt',objectFoundInLocation('Taxi1','room_start')).
no_initCyc('DoomSharedPlanningMt',pocketMoney(player1,'Dollar-UnitedStates'(12))).
no_initCyc('DoomSharedPlanningMt',pocketMoney(cyc_bot_1,'Dollar-UnitedStates'(6))).
no_initCyc('DoomSharedPlanningMt',weather('room_start','Sunny')).

						     /*
no_initCyc('DoomVocabularyMt',isa(callableBoolean,'BinaryPredicate')).
no_initCyc('DoomVocabularyMt',isa(callableBoolean,'MacroRelation')).
no_initCyc('DoomVocabularyMt',arg1Isa(callableBoolean,'Predicate')).
no_initCyc('DoomVocabularyMt',arg2Isa(callableBoolean,'CharacterString')).

no_initCyc('DoomVocabularyMt',isa(callableVector,'BinaryPredicate')).
no_initCyc('DoomVocabularyMt',isa(callableVector,'MacroRelation')).
no_initCyc('DoomVocabularyMt',arg1Isa(callableVector,'Predicate')).
no_initCyc('DoomVocabularyMt',arg2Isa(callableVector,'CharacterString')).

no_initCyc('DoomVocabularyMt',isa(callableEntity,'BinaryPredicate')).
no_initCyc('DoomVocabularyMt',isa(callableEntity,'MacroRelation')).
no_initCyc('DoomVocabularyMt',arg1Isa(callableEntity,'Predicate')).
no_initCyc('DoomVocabularyMt',arg2Isa(callableEntity,'CharacterString')).

no_initCyc('DoomVocabularyMt',isa(callableIsaWhen,'BinaryPredicate')).
no_initCyc('DoomVocabularyMt',isa(callableIsaWhen,'MacroRelation')).
no_initCyc('DoomVocabularyMt',arg1Isa(callableIsaWhen,'Predicate')).
no_initCyc('DoomVocabularyMt',arg2Isa(callableIsaWhen,'CharacterString')).


no_initCyc('DoomVocabularyMt',implies(callableBoolean(Pred,String),ist('DoomCurrentStateMt',implies(doomEval('TheList'("doomValue",String,X,Y),1),[Pred,X,Y])))).
no_initCyc('DoomVocabularyMt',implies(callableVector(Pred,String),ist('DoomCurrentStateMt',implies(doomEval('TheList'("doomValue",String,X),Y),[Pred,X,Y])))).
no_initCyc('DoomVocabularyMt',implies(callableEntity(Pred,String),ist('DoomCurrentStateMt',implies(doomEval('TheList'("doomValue",String,X),Y),[Pred,X,Y])))).

no_initCyc('DoomVocabularyMt',expansion(callableBoolean,ist('DoomCurrentStateMt',implies(doomEval('TheList'("doomValue",':ARG2',X,Y),1),[':ARG1',X,Y])))).
no_initCyc('DoomVocabularyMt',expansion(callableVector,ist('DoomCurrentStateMt',implies(doomEval('TheList'("doomValue",':ARG2',X),Y),[':ARG1',X,Y])))).
no_initCyc('DoomVocabularyMt',expansion(callableEntity,ist('DoomCurrentStateMt',implies(doomEval('TheList'("doomValue",':ARG2',X),Y),[':ARG1',X,Y])))).
no_initCyc('DoomVocabularyMt',expansion(callableIsaWhen,implies(doomEval('TheList'("doomValue",':ARG2',X),1),isa(X,':ARG1')))).


no_initCyc('DoomVocabularyMt',callableIsaWhen('Transparent',"isInvisible")).
no_initCyc('DoomVocabularyMt',callableBoolean('sees',"canSee")).
no_initCyc('DoomVocabularyMt',callableVector('locatedAtPoint-Spatial',"getWorldOrigin")).
%no_initCyc('DoomVocabularyMt',callableEntity('',"getWorldOrigin")).


no_initCyc('DoomVocabularyMt',genlPreds(locatedAtPoint-Spatial,'locatedAtPoint-Spatial')).
no_initCyc('DoomVocabularyMt',genlPreds(anglesOf,'orientation')).

no_initCyc('DoomVocabularyMt',genlPreds(entnameOf,'properNameStrings')).
no_initCyc('DoomVocabularyMt',genlPreds(nameOf,'properNameStrings')).
no_initCyc('DoomVocabularyMt',isa(invisibleOf,'InstanceBooleanPredicate')).
no_initCyc('DoomVocabularyMt',isa(hiddenOf,'InstanceBooleanPredicate')).
no_initCyc('DoomVocabularyMt',isa(lockedOf,'InstanceBooleanPredicate')).
no_initCyc('DoomVocabularyMt',isa(movingOf,'InstanceBooleanPredicate')).
no_initCyc('DoomVocabularyMt',isa(openOf,'InstanceBooleanPredicate')).
no_initCyc('DoomVocabularyMt',isa(atrestOf,'InstanceBooleanPredicate')).
no_initCyc('DoomVocabularyMt',isa(neverDormantOf,'InstanceBooleanPredicate')).

no_initCyc('DoomVocabularyMt',genlPreds(currentYawOf,'direction-Pointing')).
no_initCyc('DoomVocabularyMt',arg2Isa(currentYawOf,'TerrestrialDirection')).

no_initCyc('DoomVocabularyMt',isa(rotatingOf,'InstanceSpeedPredicate')).
no_initCyc('DoomVocabularyMt',isa(flySpeedOf,'InstanceSpeedPredicate')).
no_initCyc('DoomVocabularyMt',isa(linearVelocityOf,'InstanceSpeedPredicate')).
no_initCyc('DoomVocabularyMt',isa(turnDeltaOf,'InstanceSpeedPredicate')).
no_initCyc('DoomVocabularyMt',isa(turnDeltaOf,'InstanceSpeedPredicate')).

						*/


                                               

