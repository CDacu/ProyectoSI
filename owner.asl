!get(beer).
!setCashRobot(100).
!setFavBeer(1906).
!aburrimiento.

+has(owner,beer) <- 
	!drink(beer).

-has(owner,beer) : not robotRecycling <- 
	!get(beer).

+!setCashRobot(Qtd) : Qtd >= 0 <-
   .send(robot, tell, cash(Qtd)).

+!setFavBeer(Brand) <-
   .send(robot, tell, favBeer(Brand)).

//Si el owner no tiene cerveza la pide
+!get(beer) : ~couldDrink(beer) <-
   .println("Owner ha bebido demasiado por hoy.").

+!get(beer) <- 
	.send(robot, achieve, bring(owner,beer)).

// if I have not beer finish and leave the empty can, in other case while I have beer, sip
+!drink(beer) : not has(owner,beer) <- 
	throwBeerCan;
	.send(robot, achieve, recycle).
      
+!drink(beer) <- 
	sip(beer);
	.println("Owner está bebiendo cerveza");
	!drink(beer).

+!aburrimiento <- 
	.random(X); 
	.wait(X*5000+5000);
	.send(robot, achieve, tellTime);
	!aburrimiento.

+msg(M)[source(Ag)]	<- 
	.print("Message from ",Ag,": ",M);
	-msg(M)[source(Ag)].

+robotRecycling <- 
	.send(robot, unachieve, bring(owner,beer)).

+!recycle <-
   !go_at(owner,beercan);
   !go_at(owner,bin);
   recycleCan;
   !go_at(owner,ownerChair).
   
+!go_at(owner,P) : at(owner,P) <- true.
+!go_at(owner,P) : not at(owner,P) <- 
	move_towards(P);
	!go_at(owner,P).

