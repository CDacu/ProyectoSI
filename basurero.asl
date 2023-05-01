+!recycle <-
  	!go_at(basurero,beercan);
	!go_at(basurero,bin);
	recycleCan;
	!go_at(basurero,basureroBase).

+!go_at(basurero,P) : at(basurero,P) <- true.
+!go_at(basurero,P) : not at(basurero,P) <- 
	move_towards(P);
    !go_at(basurero,P).
