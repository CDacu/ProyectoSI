+!delivered(beer, Marca, Qtd, OrderId) : not trabajando <-	
	+trabajando;
	repartidorLlega;
	!go_at(delivery);
	.wait(200);
    !go_at(fridge);
    .send(robot, tell, delivered(beer, Marca, Qtd, OrderId));
    deliverBeer(beer, Marca, Qtd);
    .wait(200);
    !go_at(repartidorBase);
	repartidorSeVa;
	-trabajando.
	
+!delivered(beer, Marca, Qtd, OrderId) <-
	.wait(200);
	!delivered(beer, Marca, Qtd, OrderId).
	
+!delivered(Aperitivo, Qtd, OrderId) : not trabajando  <-
	+trabajando;
	repartidorLlega;
	!go_at(delivery);
	.wait(200);
    !go_at(fridge);
    .send(robot, tell, delivered(Aperitivo, Qtd, OrderId));
    .wait(200);
    !go_at(repartidorBase);
	repartidorSeVa;
	-trabajando.
	
+!delivered(beer, Qtd, OrderId) <-
	.wait(200);
	!delivered(beer, Qtd, OrderId).
    
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
	
+!go_right <- move_towards(right).
	
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

+!go_left <- move_towards(left).
	
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

+!go_up <- move_towards(up).	
	
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
	
+!go_down <- move_towards(down).	
	
+!go_right2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX+1, MY) <-	
	move_towards(right).

+!go_right2 <- true.

+!go_left2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX-1, MY) <-	
	move_towards(left).

+!go_left2 <- true.

+!go_up2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX, MY-1) <-	
	move_towards(up).

+!go_up2 <- true.

+!go_down2 : .my_name(MyName) & position(MyName,MX, MY) & not position(obstaculo, MX, MY+1) <-	
	move_towards(down).

+!go_down2 <- true.
