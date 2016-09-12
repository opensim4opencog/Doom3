% Ett program som l|ser enklare planning-problem kodade i STRIPS.
% Programmet utnytjar best-first.


:- op(1120,xfy,[pre,del,add]).

% Start och Goal {r listor av vad som skall g{lla f|re repektive efter,
% Path {r den operatorsekvens som beh|vs f|r att komma fr}n Start till
% Goal.

plan(Start,Goal,Path) :-
	X=(Start,[]),
	eval_heuristic(X1,X,Goal),
	plan1([X1],Goal,[],Path1),
	reverse(Path,Path1).


% Best-first s|kning.

plan1(Agenda,Goal,_,Path) :-
	goal(Agenda,Goal,Path),!.

plan1(Agenda,Goal,History,Path) :-
	choose_best(State,Agenda,History),
        bagof1(X,next(State,X),Xs),
	extend(Agenda,Xs,Agenda1,Goal),
	nl,
	plan1(Agenda1,Goal,[State|History],Path).


% V{lj det b{sta tillst}nd som uppn}ts, som det inte har s|kts fr}n.
% Agenda {r sorterad s} att det b{sta tillst}ndet ligger f|rst.

choose_best(State,Agenda,History) :-
	member(State,Agenda),
	\+(in_history(State,History)).


% Skapar med en operator ett nytt tillst}nd utifr}n det gamla.

next((State,_Value,Path),(State1,[Head|Path])) :-
	(Head pre P del D add A),
	preconditions(P,State),
	delete_state(State,D,State0),
	add_state(State0,A,State1).


% Sorterar in  en lista av tillst}nd i Agenda.

extend(Agenda,[],Agenda,_).

extend(Agenda,[X|Xs],Agenda1,Goal) :-
	eval_heuristic(X1,X,Goal),
	extend1(Agenda,X1,Agenda0),
	extend(Agenda0,Xs,Agenda1,Goal).


% Sorterar in ett tillst}nd i Agenda.

extend1([],State,[State]) :- print_l(State).

extend1([X|Xs],State,[X|Xs]) :-	    % Strunta i tillst}ndet om det redan finns
        same_state(State,X),!.

extend1([X|Xs],State,[State,X|Xs]) :-
        better(State,X),!,   % l{gg tillst}ndet efter alla med samma v{rde
	print_l(State).

extend1([X|Xs],State,[X|Ys]) :-
	extend1(Xs,State,Ys).


% M}let {r uppn}t om alla krav i Goal {r uppfyllda.

goal(Agenda,Goal,Path) :-
	member((State,0,Path),Agenda),
	true_in_state(Goal,State).


% Sann om State redan finns med i History.

in_history((State,_,_),History) :-
	member((State1,_,_),History),
	same_state1(State,State1).


% Testar s} att alla vilkor {r uppfylda. Om vilkoret {r p} formen [V] s}
% exekveras V, not(V) {r sann om V inte finns med i databasen.

preconditions((P1,P2),State) :- !,
	preconditions(P1,State),
	preconditions(P2,State).

preconditions([P],_State) :- !,
	call(P).

preconditions(not(P),State) :- !,
	\+(member(P,State)).

preconditions(P,State) :-
	member(P,State).


% Tar bort deletelistan fr}n tillst}ndet.

delete_state(State,(D1,D2),State0) :- !,
	delete_state(State,D1,State1),
	delete_state(State1,D2,State0).

delete_state(State,[D],State) :- !,
	call(D).

delete_state(State,D,State0) :-
	member_rest(D,State,State0).


% L{gger till addlistan till tillst}ndet.

add_state(State,(A1,A2),State0) :- !,
	add_state(State,A1,State1),
	add_state(State1,A2,State0).

add_state(State,[A],State) :- !,
	call(A).

add_state(State,A,[A|State]).


% Kollar s} att tv} tillst}nd har exakt samma delar.

same_state((State,_,_),(State1,_,_)) :-
	same_state1(State,State1).


same_state1([],[]).

same_state1([X|Xs],State) :-
	member_rest(X,State,State1),
	same_state1(Xs,State1).


true_in_state([],_).

true_in_state([X|Xs],Y) :-
	member(X,Y),
	true_in_state(Xs,Y).


make_list(A,[A]) :- functor(A,_,_).

make_list((A1,A2),L) :-
	make_list(A1,L1),
	make_list(A2,L2),
	append(L1,L2,L).


% R{knar ut summan av v{rdena p} alla skilnader mellan State och Goal.

eval_heuristic((State,Value,Path),(State,Path),Goal) :-
	bagof1(X,difference(State,Goal,X),Xs),
	sum(Xs,Value).


better((_,Value,_),(_,Value1,_)) :-
	Value < Value1.


member_rest(E,[E|R],R).
member_rest(E,[X|R],[X|S]) :- member_rest(E,R,S).

member_tail(E,[E|R],R).
member_tail(E,[_|R],S) :- member_tail(E,R,S).

member(E,[E|_]).                  
member(E,[_|R]) :- member(E,R).   

bagof1(X,P,S) :- bagof(X,P,S),!.
bagof1(_,_,[]).

setof1(X,P,S) :- setof(X,P,S),!.
setof1(_,_,[]).
	
append([],L,L).                  
append([X|L1],L2,[X|L3]) :- append(L1,L2,L3).

reverse(L,R) :- reverse1(L,[],R).
reverse1([],R,R).
reverse1([L|Ls],A,R) :- reverse1(Ls,[L|A],R).

sum([],0).
sum([X|Xs],S) :- sum(Xs,S1), S is X+S1.





print_l((_,Value,Path)) :-
        reverse(Path,Path1),
	print(Path1),
	print(Value),
	nl.


	
