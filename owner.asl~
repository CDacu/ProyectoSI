!get(beer).
!setMoneyRobot(100).
!setFavBeer(1906).
!aburrimiento.

+has(owner,beer) <- 
	!drink(beer).

-has(owner,beer) : not recogiendoLata <- 
	!get(beer).

+!setMoneyRobot(Qtd) <-
   .send(robot, tell, money(Qtd)).

+!setFavBeer(Marca) <-
   .send(robot, tell, favBeer(Marca)).

+!get(beer) : ~couldDrink(beer) <-
   .println("Owner ha bebido demasiado por hoy.").

+!get(beer) <-
	.send(robot, achieve, bring(owner,beer)).
	
// if I have not beer finish and leave the empty can, in other case while I have beer, sip
+!drink(beer) : not has(owner,beer) & not yaElegido <-
	+yaElegido;
	.random(X);
	X1 = X * 10;
	+resultado(X1);
	if(X1 > 5){
		.println("El owner decide lanzar la lata");
		throwBeerCan;
		+recogiendoLata;
		.send(basurero,achieve,recogerLata);
		.send(owner,achieve,get(beer));
	}else{
		.println("El owner decide reciclar la lata");
		+recogiendoLata;
		!recogerLata;
		.send(owner,achieve,get(beer));
	}
	-yaElegido.

+!drink(beer) : not has(owner,beer) & yaElegido <-
	.wait(100);
	!drink(beer).
	
+!drink(beer) : has(owner,beer) <- 
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

+!recogerLata <-
   !go_at(bin);
   tirarLata;
   !go_at(ownerchair);
   -recogiendoLata.
   
+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MX == DX & MY == DY <-
    .println("HE LLEGADO A MI DESTINO ", Destino).

+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MX < DX <-
    move_towards(right);
    !go_at(Destino).

+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MX > DX <-
    move_towards(left);
    !go_at(Destino).

+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MY < DY <-
    move_towards(down);
    !go_at(Destino).

+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MY > DY <-
    move_towards(up);
    !go_at(Destino).

+!go_at(Destino) : not position(Destino,_,_) <-
    .println("HA SUCEDIDO UN ERROR, NO PUEDO LLEGAR A MI DESTINO ", Destino).
