stack(X,Y)
    pre clear(Y),holding(X)
    del clear(Y),holding(X)
    add armempty,on(X,Y),clear(X).

unstack(X,Y)
    pre on(X,Y),clear(X),armempty
    del on(X,Y),clear(X),armempty
    add holding(X),clear(Y).

pickup(X)
    pre clear(X),ontable(X),armempty
    del clear(X),ontable(X),armempty
    add holding(X).

putdown(X)
    pre holding(X)
    del holding(X)
    add ontable(X),armempty,clear(X).






difference(D,G,2) :-
	member(on(X,Y),D),
	member(on(X,Z),G),
	Y \== Z.

difference(D,G,1) :-
	member(clear(X),D),
	\+(member(clear(X),G)).
	
difference(D,G,1) :-
	member(clear(X),G),
	\+(member(clear(X),D)).

difference(D,G,2) :-
	member(ontable(X),D),
	\+(member(ontable(X),G)).
	
difference(D,G,2) :-
	member(ontable(X),G),
	\+(member(ontable(X),D)).

	



test1 :-
   plan(
	[on(b,a),ontable(a),ontable(c),ontable(d),armempty,
	clear(b),clear(c),clear(d)],
	[on(c,a),on(b,d),ontable(a),ontable(d),clear(c),clear(b)],
	_Plan).

test2 :-
   plan(
	[armempty,on(c,a),ontable(a),clear(c),ontable(b),clear(b)],
	[on(a,b),on(b,c),ontable(c),clear(a)],
	_Plan).
