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

+!bring(owner,beer) <- 
	+carringPlato;
	!go_at(robot,fridge);
	open(fridge);
	get(beer);
	get(pincho);
	close(fridge);
    !go_at(robot,owner);
    hand_in(beer);
	!go_at(robot,lavavajillas);
	anadirPlatosLavavajillas(1);
    //?has(owner,beer);
    .date(YY,MM,DD); .time(HH,NN,SS);
    +consumed(YY,MM,DD,HH,NN,SS,beer);
	-carringPlato.
	
// if I have not beer finish and leave the empty can, in other case while I have beer, sip
+!drink(beer) : not has(owner,beer) & not yaElegido <-
	+yaElegid;
	.random(X);
	if(X > 0.5){
		throwBeerCan;
		.send(basurero,achieve,recycle);
		.send(owner,achieve,get(beer));
	}else{
		.send(owner,achieve,recycle);
		.send(owner,achieve,get(beer));
	}.

+!drink(beer) : not has(owner,beer) & yaElegido <-
	.wait(100);
	!drink(beer).
	
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

