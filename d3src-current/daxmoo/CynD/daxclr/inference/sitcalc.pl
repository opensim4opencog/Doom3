%% FILE sitcalc.pl 
%% SYSTEM ktmet
%% CREATED  TA-960212
%% REVISED  TA-030401

%  A Planner based on the Situation Calculus


%% Implementation is dependent on 
%% the coroutining facilities in SICStus Prolog, 
%% The predicate dif/2 tests for non unifiablity,
%% but delays the goal if not decidable.

:-prolog_flag(redefine_warnings,_,off). %% TA-030401

:-op(900,xfy,&).
:-op(800,fx,not).


%% Axioms for planning

holds(Condition1 & Condition2, State) :- !,
    holds(Condition1,State),
    holds(Condition2,State).

holds(not Condition,S) :- !,
    \+ holds(Condition,S).

holds(Condition,_) :-
     always(Condition),           % axiomatic,
     !.                           % situation independent

holds(Condition1,State) :-              
    implied(Condition1,Conditions),   % holds in same situation             
    !,
    holds(Conditions,State).


holds(Condition,do(State,Action)) :-
    holds(Condition,State),           % true already
    invariant(Condition,Action).      % no action made it false

holds(Condition,do(State,Action)) :- 
    consequence(Condition,Action,Preconditions),  % an action made it true 
    holds(Preconditions,State).


holds(Condition,_) :-
    fact(Condition).              % true in all situations

holds(Condition,start) :-
    given(Condition).             % initial configuration


always(true).
always(dif(X,Y)) :- dif(X,Y).     % axiom of difference

fact(true).       % to avoid SICStus existence errors for fact/1
implied(true,_).  % and implied/2, if not defined by the user



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% Axioms for search strategy
%  Depth First Iterative Deepening

try(_,start) :- write('. ').


try(N,do(State,_)) :-
   M is N-1,
   M >= 0,
   try(M,State).


solve(G,S) :-                    Max = 7, %% TA-030424
    nl,
    write('Trying: '),nl,
    try(Max,S), 
    holds(G,S),
    !,
    nl,nl,
    write(S),nl.

hi :- debug,run.

run :-
    statistics(runtime,_),

    nl,write('GIVEN: '),nl,nl,
    listall(X,given(X)),

    write('GOAL: '),nl,nl,
    listall(X,goal(X)),
    nl,
    goal(G),
    solve(G,_),
    statistics(runtime,[_,MS]),
    nl,write('time(ms) '),write(MS),
    nl,nl,nl.


test(G) :-
    statistics(runtime,_),
    solve(G,_),
    statistics(runtime,[_,MS]),
    nl,write('time(ms) '),write(MS),nl.

listall(X,P) :- P,write(X),nl,false;nl,nl.


% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % 

% An extra utility in Prolog:
% waits untill the arguments sufficiently instantiated and
% checks if the main functor of Term is different from Fun
%
% Intended for use in invariant/2, e.g.
% invariant(<property>, Action) :- difname(Action, <action_name>). 

difname(Term, Fun) :- 
	when(nonvar(Term), (functor(Term,F,_), dif(F, Fun)) ).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


