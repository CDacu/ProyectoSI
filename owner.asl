// ----------------------- FUNCIONES INCIALES --------------------------------------------------------------------------- //

!get(beer).
!setMoneyRobot(100).
!setFavBeer(1906).
!aburrimiento.

+!setMoneyRobot(Qtd) <-
   	.send(robot, tell, money(Qtd)).

+!setFavBeer(Marca) <-
   	.send(robot, tell, favBeer(Marca)).
   
// ----------------------- FUNCIONES BEBER ------------------------------------------------------------------------------ //

+has(owner,beer) <- 
	!drink(beer).

-has(owner,beer) : not recogiendoLata <- 
	!get(beer).

+!get(beer) : ~couldDrink(beer) <-
   	.println("Owner ha bebido demasiado por hoy.").

+!get(beer) <-
	.send(robot, achieve, bring(owner,beer)).
	
// if I have not beer finish and leave the empty can, in other case while I have beer, sip
+!drink(beer) : not has(owner,beer) & not yaElegido <-
	+yaElegido;
	X = math.round(math.random(1));
	if(X == 0){
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
	
// ----------------------- FUNCIONES ABURRIMIENTO ------------------------------------------------------------------------ //

+!aburrimiento <- 
	X = math.round(math.random(4000));
	.wait(X+4000);
	.send(robot, achieve, tellTime);
	!aburrimiento.

// ----------------------- FUNCIONES RECOGER LATA ----------------------------------------------------------------------- //	
	
+!recogerLata <-
   	!go_at(bin);
	tirarLata;
	!go_at(ownerchair);
	-recogiendoLata.
   
+msg(M)[source(Ag)]	<- 
	.print("Message from ",Ag,": ",M);
	-msg(M)[source(Ag)].

// ----------------------- MOVIMIENTO JASON ---------------------------------------------------------------------------------- //
	
+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MX == DX & MY == DY <-
    .println("HE LLEGADO A MI DESTINO ", Destino).

+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MX < DX <-
	!go_right;
    !go_at(Destino).
	
+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MX > DX <-
    !go_left;
    !go_at(Destino).

+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MY < DY <-
    !go_down;
    !go_at(Destino).

+!go_at(Destino) : .my_name(MyName) & position(MyName,MX, MY) & position(Destino, DX, DY) & MY > DY <-
    !go_up;
    !go_at(Destino).

+!go_at(Destino) : not position(Destino,_,_) <-
    .println("HA SUCEDIDO UN ERROR, NO PUEDO LLEGAR A MI DESTINO ", Destino).
	
+!go_right : .my_name(MyName) & position(MyName,MX, MY) & position(obstaculo, MX+1, MY) <-
	MY2 = MY;
	if(MY2 == 0){
		!go_down;
		!go_right2;		
	}else{
		if(MY2 == 10){
			!go_up;
			!go_right2;
		}else{
			X = math.round(math.random(1));
			if(X == 0){
				!go_up;
				!go_right2;
			}else{
				!go_down;
				!go_right2;
			}
		}
	}.
	
+!go_right : .my_name(MyName) & position(MyName,MX, MY) & MX < 10 <- 
	move_towards(right).

+!go_right <- true.
	
+!go_left : .my_name(MyName) & position(MyName,MX, MY) & position(obstaculo, MX-1, MY) <-
	MY2 = MY;
	if(MY2 == 0){
		!go_down;
		!go_left2;		
	}else{
		if(MY2 == 10){
			!go_up;
			!go_left2;
		}else{
			X = math.round(math.random(1));
			if(X == 0){
				!go_up;
				!go_left2;
			}else{
				!go_down;
				!go_left2;
			}
		}
	}.

+!go_left : .my_name(MyName) & position(MyName,MX, MY) & MX > 0 <- 
	move_towards(left).
	
+!go_left <- true.
	
+!go_up : .my_name(MyName) & position(MyName,MX, MY) & position(obstaculo, MX, MY-1) <-
	MX2 = MX;
		if(MX2 == 10){
		!go_left;
		!go_up2;		
	}else{
		if(MX2 == 0){
			!go_right;
			!go_up2;
		}else{
			X = math.round(math.random(1));
			if(X == 0){
				!go_left;
				!go_up2;
			}else{
				!go_right;
				!go_up2;
			}
		}
	}.

+!go_up : .my_name(MyName) & position(MyName,MX, MY) & MY > 0 <- 
	move_towards(up).	
	
+!go_up <- true.
	
+!go_down : .my_name(MyName) & position(MyName,MX, MY) & position(obstaculo, MX, MY+1) <-
	MX2 = MX;
	if(MX2 == 10){
		!go_left;
		!go_down2;
		
	}else{
		if(MX2 == 0){
			!go_right;
			!go_down2;	
		}else{
			X = math.round(math.random(1));
			if(X == 0){
				!go_left;
				!go_down2;
			}else{
				!go_right;
				!go_down2;
			}
		}
	}.
	
+!go_down : .my_name(MyName) & position(MyName,MX, MY) & MY < 10 <- 
	move_towards(down).

+!go_down <- true.	
	
+!go_right2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX+1, MY) <-	
	move_towards(right).
	
+!go_right2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX+1, MY) & MX < 10 <- 
	move_towards(right).

+!go_right2 <- true.

+!go_left2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX-1, MY) <-	
	move_towards(left).
	
+!go_left2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX-1, MY) & MX > 0 <- 
	move_towards(left).

+!go_left2 <- true.

+!go_up2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX, MY-1) <-	
	move_towards(up).
	
+!go_up2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX, MY-1) & MY > 0 <- 
	move_towards(down).

+!go_up2 <- true.

+!go_down2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX, MY+1) <-	
	move_towards(down).
	
+!go_down2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX, MY+1) & MY < 10 <- 
	move_towards(down).

+!go_down2 <- true.
