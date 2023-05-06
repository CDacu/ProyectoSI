// ----------------------- CREENCIAS ROBOT ---------------------------------------------------------------------------//

available(beer,fridge).

limit(beer,7).

tamPackBeer(3).

// ----------------------- FUNCIONES LLEVAR COMIDA AL OWNER ----------------------------------------------------------//

too_much(B) :-
   	.date(YY,MM,DD) &
   	.count(consumed(YY,MM,DD,_,_,_,B),QtdB) &
   	limit(B,Limit) &
   	QtdB >= Limit.

+!bring(owner,beer) :  too_much(beer) & limit(beer,L) & not healthMsg <- 
	.concat("The Department of Health does not allow me to give you more than ", L," beers a day! I am very sorry about that!",M);
    .send(owner,tell,msg(M));
	+healthMsg;
	!bring(owner,beer).

+!bring(owner,beer) :  not available(beer,fridge) & available(pincho,fridge) <-
	.println("Busco reponer cervezas, no quedan");
	!checkSupermarkets;
	.wait(500);
	!checkPrice; 
	.wait(50);
	!buySupermarketBeer.

+!bring(owner,beer) : available(beer,fridge) & not available(pincho,fridge) & not yaPedidoPincho <-
	.println("Busco reponer pinchos, no quedan");
	+yaPedidoPincho;
   	siguienteAperitivo;
	!checkSupermarkets;
	.wait(500);
	!checkPrice;
	.wait(50);
	!buySupermarketPincho.  

+!bring(owner,beer) : not available(beer,fridge) & not available(pincho,fridge) <-
	.println("Busco reponer cervezas y pincho, no quedan");
	siguienteAperitivo;
	!checkSupermarkets;
	.wait(500);
	!checkPrice;
	.wait(50);
	!buySupermarketBeer;
	!buySupermarketPincho.

+!bring(owner,beer) : too_much(beer) & healthMsg <-
   	.wait(5000);
   	!tellTime;
	!bring(owner,beer).

+!bring(owner,beer) :  available(beer,fridge) & not too_much(beer) & available(pincho,fridge) & not carringPlato & not preparingPincho & not vaciandoLavavajillas <- 
	+carringPlato;
	X = math.round(math.random(200));
	.wait(X);
	!traer(owner,beer).
	
+!traer(owner,beer) : not working <-
	+working;
	!go_at(fridge);
	open(fridge);
	get(beer);
	get(pincho);
	close(fridge);
    !go_at(closeownerchair);
    hand_in(beer);
	!go_at(lavavajillas);
	anadirPlatosLavavajillas(1);
    .date(YY,MM,DD); .time(HH,NN,SS);
    +consumed(YY,MM,DD,HH,NN,SS,beer);
	-working;
	-carringPlato.

+!traer(owner,beer) <- true.

+!bring(owner,beer) <-
	X = math.round(math.random(200));
	.wait(X);
	!bring(owner,beer).
	
//-!bring(_,_) <- 
//	.current_intention(I);
//	.print("Failed to achieve goal '!has(_,_)'. Current intention is: ",I).	

// ----------------------- FUNCIONES PREPARAR PINCHOS --------------------------------------------------------------------// 

+!prepararPincho(Product) : not carringPlato & not vaciandoLavavajillas <-
	+preparingPincho;
    !go_at(alacena);
    quitarPlatosAlacena(5);
    !go_at(fridge);
    .wait(1000);
    prepararPincho(Product);
	-preparingPincho.

+!prepararPincho(Product) <-
	.wait(100);
	!prepararPincho(Product).

// ----------------------- FUNCIONES VACIAR LAVAVAJILLAS ------------------------------------------------------------------// 
	
+lavavajillasLleno <-
	!vaciar_lavavajillas.
	
+!vaciar_lavavajillas : not carringPlato & not preparingPincho <-
	+vaciandoLavavajillas;
  	!go_at(lavavajillas);
	.wait(500);
	vaciar_lavavajillas;
	!go_at(alacena);
	.wait(500);
	anadirPlatosAlacena(5);
	-vaciandoLavavajillas;
	-lavavajillasLleno.
	
+!vaciar_lavavajillas <-
	.wait(100);
	!vaciar_lavavajillas.

// ----------------------- FUNCIONES COMPRA EN EL SUPERMERCADO --------------------------------------------------------------// 	

+reponerCerveza : not enProceso <-
	.println("Busco reponer cervezas");
	+enProceso;
  	!checkSupermarkets;
   	.wait(500);
	!checkPrice;
	.wait(50);
   	!buySupermarketBeer;
	-enProceso;
	-reponerCerveza.
	
+reponerCerveza : true <- true.

+!buySupermarketBeer : favBeer(Marca) & barataF(Product, Marca, Precio, Stock, Super) & money(DineroInicial) & tamPackBeer(Qtd) <-
	Dinero = DineroInicial;
	if(Precio * Qtd <= Dinero){	
		if(Qtd <= Stock){
			.println("Realizo un pedido de ", Qtd, " cervezas ", Marca);
			.send(Super, achieve, order(Product, Marca, Qtd));
   			!pagar(Precio, Qtd, Super); 
		}else{
			Resto = Qtd - Stock;
			?barata(Product, MarcaB, PrecioB, StockB, Super);
			if(Stock > 0){
				.println("Realizo un pedido de ", Stock, " cervezas ", Marca, "y ",Resto," ",MarcaB);
				.send(Super, achieve, order(Product, Marca, Stock, MarcaB, Resto));
				!pagar(Precio, Stock, Super);
				!pagar(PrecioB, Resto, Super);
			}else{
				?masbarata(Product, MarcaC, PrecioC, StockC, SuperC);
				if(StockC >= Qtd){
					.println("Realizo un pedido de ", Qtd, " cervezas ", MarcaC);
					.send(SuperC, achieve, order(Product, MarcaC, Qtd));
					!pagar(PrecioC, Qtd, SuperC);
				}else{
					.concat("No he podido comprar más Cervezas, no tienen Stock Suficiente",M);
					.send(owner, tell, msg(M));
				}
			}
		}
	}else{
		?masbarata(Product, MarcaC, PrecioC, StockC, SuperC);
		if(PrecioC * Qtd <= Dinero){
			if(StockC >= Qtd){
				.println("Realizo un pedido de ", Qtd, " cervezas ", MarcaC);
				.send(SuperC, achieve, order(Product, MarcaC, Qtd));
				!pagar(PrecioC, Qtd, SuperC);
			}else{
				.concat("No he podido comprar más Cervezas, no tienen Stock Suficiente de aquellas que me puedo permitir",M);
				.send(owner, tell, msg(M));
			}
		}else{
			.concat("No he podido comprar más cerveza, me quedan: ",Dinero,"€ y la más barata cuesta: ",PrecioC*Qtd ,"€",M);
   			.send(owner, tell, msg(M));
		}	
	}.

+!buySupermarketPincho : siguienteAperitivo(Pincho) & barato(Pincho, Precio, Stock, Super) & money(DineroInicial) <-
	Dinero = DineroInicial;
	if(Dinero >= Precio){
		if(Stock >= 1){
		.println("Realizo un pedido de 1 pincho de ", Pincho);
		.send(Super, achieve, order(Pincho, 1));
   		!pagar(Precio, 1, Super);
		}
	}else{
		?masbarato(PinchoB, PrecioB, StockB, SuperB);
		if(Dinero >= PrecioB){
			if(StockB >= 1){
				.println("Realizo un pedido de 1 pincho de ", PinchoB);
				.send(SuperB, achieve, order(PinchoB, 1));
   				!pagar(PrecioB, 1, SuperB);
			}else{
				.concat("No he podido comprar más pinchos, no tengo dinero suficiente para aquellos que estan disponibles",M);
   				.send(owner, tell, msg(M));
			}
		}else{
			.concat("No he podido comprar más aperitios, me quedan: ",Dinero,"€ y el aperitivo más barato cuesta: ",PrecioB,"€",M);
   			.send(owner, tell, msg(M));
		}
	}.

// ----------------------- FUNCIONES GESTION DE DINERO --------------------------------------------------------------------// 	
	
+!pagar(Precio, Qtd, Super) : money(DineroInicial) <-
   Pago = Qtd * Precio;
   Dinero = DineroInicial - Pago;
   -money(_);
   +money(Dinero);
   .send(Super, achieve, pago(Pago)).

// ----------------------- FUNCIONES GESTION DE STOCK ---------------------------------------------------------------------//   
   
+delivered(beer,Marca,Qtd,OrderId) : available(beer,fridge) <- 
	-delivered(beer,Marca,Qtd,OrderId).

+delivered(beer,Marca,Qtd,OrderId) : not carringPlato <- 
	+available(beer,fridge);
    !bring(owner,beer);
    -delivered(beer,Marca,Qtd,OrderId).
	
+delivered(beer,Marca,Qtd,OrderId) <- 
	+available(beer,fridge);
    -delivered(beer,Marca,Qtd,OrderId).
	
+delivered(Aperitivo,Qtd,OrderId) <-
	!prepararPincho(Aperitivo);
	-yaPedidoPincho;
    +available(pincho,fridge);
    !bring(owner,beer);
    -delivered(Aperitivo,Qtd,OrderId).

+stock(beer,N) <-
	if(N = 0){
	    -available(beer,fridge);
	}else{
		+available(beer,fridge);
	}
	-stock(beer,N).
	
+stock(pincho,N) <-
	if(N = 0){
	    -available(pincho,fridge);
	}else{
		+available(pincho,fridge);
	}
	-stock(pincho,N).	
	
// ----------------------- FUNCIONES COMPROBAR PRECIOS ----------------------------------------------------------------------------- //	
	
+!checkSupermarkets <-
	.abolish(priceBeer(_,_,_,_));
	.abolish(barataF(_,_,_,_,_));
	.abolish(barata(_,_,_,_,_));
	.abolish(masbarata(_,_,_,_,_));
   	.abolish(pricePincho(_,_,_));
   	.abolish(barato(_,_,_,_));
   	.send(mercadona, tell, tellPrice);
	.send(gadis, tell, tellPrice).   

+!checkPrice <-
	!checkPrice(beer);
	!checkPricePincho.

+!checkPrice(Product) : favBeer(Marca) <-
	.findall(Precio, priceBeer(Product, Marca, Precio, Stock), ListMarcaFav);
	.min(ListMarcaFav, MenorPrecio);
	?priceBeer(Product, Marca, MenorPrecio, Stock)[source(Super)];
	+barataF(Product, Marca, MenorPrecio, Stock, Super);

	.findall(PrecioB, priceBeer(Product, MarcaB, PrecioB, StockB), ListBarata);
	.min(ListBarata, PrecioBarata);
	?priceBeer(Product, MarcaB, PrecioB, StockB)[source(SuperB)];
	+masbarata(Product, MarcaB, PrecioB, StockB, SuperB);

	.findall(PrecioM, priceBeer(Product, MarcaM, PrecioM, StockM)[source(mercadona)], ListBarataMercadona);
	.min(ListBarataMercadona, PrecioBarataM);
	?priceBeer(Product, MarcaM, PrecioM, StockM)[source(mercadona)];
	+barata(Product, MarcaM, PrecioM, StockM, mercadona);

	.findall(PrecioG, priceBeer(Product, MarcaG, PrecioG, StockG)[source(gadis)], ListBarataGadis);
	.min(ListBarataGadis, PrecioBarataG);
	?priceBeer(Product, MarcaG, PrecioG, StockG)[source(gadis)];
	+barata(Product, MarcaG, PrecioG, StockG, gadis).

+!checkPricePincho <-
	.findall(PrecioT, pricePincho(tortilla, PrecioT, StockT), ListTortilla);
	.min(ListTortilla, PrecioTortilla);
	?pricePincho(tortilla, PrecioTortilla, StockT)[source(SuperT)];
	+barato(tortilla, PrecioTortilla, StockT, SuperT);
	
	.findall(PrecioE, pricePincho(empanada, PrecioE, StockE), ListEmpanada);
	.min(ListEmpanada, PrecioEmpanada);
	?pricePincho(empanada, PrecioEmpanada, StockE)[source(SuperE)];
	+barato(empanada, PrecioEmpanada, StockE, SuperE);	

	.findall(PrecioJ, pricePincho(bocata, PrecioJ, StockJ), ListBocata);
	.min(ListBocata, PrecioBocata);
	?pricePincho(Bocata, PrecioBocata, StockJ)[source(SuperJ)];
	+barato(bocata, PrecioBocata, StockJ, SuperJ);

	.findall(Precio, pricePincho(Product, Precio, Stock), List);
	.min(List, PrecioBarato);
	?pricePincho(Product, PrecioBarato, Stock)[source(Super)];
	+masbarato(Product, PrecioBarato, Stock, Super).

// ----------------------- FUNCIONES TELL TIME ------------------------------------------------------------------------------- //

+!tellTime : true <-
   	.time(HH,NN,SS);
   	.concat("La hora es: ", HH,":",NN,":",SS, M);
   	.send(owner,tell,msg(M)).

+?time(T) <- time.check(T).

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
