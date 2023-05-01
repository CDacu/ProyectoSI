available(beer,fridge).

limit(beer,7).

tamPackBeer(3).

too_much(B) :-
   	.date(YY,MM,DD) &
   	.count(consumed(YY,MM,DD,_,_,_,B),QtdB) &
   	limit(B,Limit) &
   	QtdB >= Limit.

+!bring(owner,beer) :  too_much(beer) & limit(beer,L) & not healthMsg <- 
	.concat("The Department of Health does not allow me to give you more than ", L,
            " beers a day! I am very sorry about that!",M);
    .send(owner,tell,msg(M));
	+healthMsg;
	!bring(owner,beer).

+reponerCerveza <-
  	!checkSupermarkets;
 	.send(repartidor, achieve, go_at(repartidor,delivery));
   	.wait(500);
	!checkPrice;
	.wait(50);
   	!buySupermarketBeer;
	-reponerCerveza.

+!bring(owner,beer) :  not available(beer,fridge) & available(pincho,mesa) <- 
	!checkSupermarkets;
	.send(repartidor, achieve, go_at(repartidor,delivery));
	.wait(500);
	!checkPrice; 
	.wait(50);
	!buySupermarketBeer.

+!bring(owner,beer) : available(beer,fridge) & not available(pincho,mesa) <-
   	siguienteAperitivo;
	!checkSupermarkets;
	.send(repartidor, achieve, go_at(repartidor,delivery));
	.wait(2000);
	!checkPrice;
	.wait(50);
	!buySupermarketPincho.  

+!bring(owner,beer) : not available(beer,fridge) & not available(pincho,mesa) <-
   siguienteAperitivo;
   !checkSupermarkets;
   .send(repartidor, achieve, go_at(repartidor,delivery));
   .wait(500);
   !checkPrice;
   .wait(50);
   !buySupermarketBeer;
   !buySupermarketPincho.

+!bring(owner,beer) : too_much(beer) & healthMsg <-
   .wait(2000);
   !tellTime;
   !bring(owner,beer).

+!tellTime <-
   	.time(HH,NN,SS);
   	.concat("La hora es: ", HH,":",NN,":",SS, M);
   	.send(owner,tell,msg(M)).
   
+!bring(owner,beer) :  available(beer,fridge) & not too_much(beer) & stock(pincho,N) & N > 0 <- 
	!go_at(robot,fridge);
	open(fridge);
	get(beer);
    close(fridge);
    !go_at(robot,mesaPincho);
	get(pincho);
    !go_at(robot,owner);
    hand_in(beer);
    ?has(owner,beer);
    .date(YY,MM,DD); .time(HH,NN,SS);
    +consumed(YY,MM,DD,HH,NN,SS,beer).

+!bring(owner,beer) : available(beer,fridge) & not too_much(beer) & stock(pincho,N) & N = 0 <- 
	.wait(100); 
	!bring(owner,beer).

+!buySupermarketBeer : favBeer(Marca) & barataF(Product, Marca, Precio, Stock, Super) & cash(Dinero) & tamPackBeer(Qtd) <-

	if(Precio * Qtd <= Dinero){	
		if(Qtd <= Stock){												
			.send(Super, achieve, order(Product, Marca, Qtd));
   			!pay(Precio, Qtd, Super); 
		}else{
			Resto = Qtd - Stock;
			?barata(Product, MarcaB, PrecioB, StockB, Super);
			if(Stock == 0){
				.send(Super, achieve, order(Product, MarcaB, Resto));
				!pay(PrecioB, Resto, Super);
			}else{
				.send(Super, achieve, order(Product, Marca, Stock));
				!pay(Precio, Stock, Super);
				.send(Super, achieve, order(Product, MarcaB, Resto));
				!pay(PrecioB, Resto, Super);
			}
		}	
	}else{
	/*
		CantidadMaxF = Qtd;
		do{
			CantidadMaxF = CantidadMaxF - 1;
		}while(Precio * CantidadMaxF > Dinero);
		if(CantidadMaxF > 0){
			Resto = Qtd - CantidadMaxF;
			?barata(Product, MarcaB, PrecioB, StockB, Super);
			do{
				Resto = Resto - 1;
			}while(PrecioB * Resto > Dinero);
			if(Resto > 0){
				.send(Super, achieve, order(Product, Marca, CantidadMaxF));
				!pay(Precio, CantidadMaxF, Super);
				.send(Super, achieve, order(Product, MarcaB, Resto));
				!pay(PrecioB, Resto, Super);
			}else{
				.send(Super, achieve, order(Product, Marca, CantidadMaxF));
				!pay(Precio, CantidadMaxF, Super);
			}
		}else{
	*/
			?masbarata(Product, MarcaB, PrecioB, StockB, SuperB);
			if(PrecioB * Qtd <= Dinero){
				if(Qtd <= StockB){
					.send(SuperB, achieve, order(Product, MarcaB, Qtd));
					!pay(PrecioB, Qtd, SuperB);
				}else{
					Resto = Qtd - StockB;
					if(StockB == 0){
						.concat("No he podido comprar más cervezas, no tengo dinero suficiente para aquellas que estan disponibles",M);
					}else{
						.send(SuperB, achieve, order(Product, MarcaB, Resto));
						!pay(PrecioB, Resto, SuperB);
					}
				}
			}else{
			/*
				CantidadMaxB = Qtd;
				do{
					CantidadMaxB = CantidadMaxB - 1;
				}while(PrecioB * CantidadMaxB > Dinero);
				if(CantidadMaxB > 0){
					.send(SuperB, achieve, order(Product, MarcaB, CantidadMaxB));
					!pay(PrecioB, CantidadMaxB, SuperB).
				}else{
			*/
					.concat("No he podido comprar más cervezas, me quedan: ",Dinero,"€ y la cerveza más barata cuesta: ",Precio,"€",M);
					.send(owner, tell, msg(M));
			/*
				}
			}
			*/
		}
	}.

+!buySupermarketPincho : siguienteAperitivo(Pincho) & barato(Pincho, Precio, Stock, Super) & cash(Dinero) <-

	if(Dinero >= Precio){
		if(Stock >= 1){
		.send(Super, achieve, order(Pincho, 1));
   		!pay(Precio, 1, Super);
		}
	}else{
		?masbarato(PinchoB, PrecioB, StockB, SuperB);
		if(Dinero >= PrecioB){
			if(StockB >= 1){
				.send(SuperB, achieve, order(PinchoB, 1));
   				!pay(PrecioB, 1, SuperB);
			}else{
				.concat("No he podido comprar más pinchos, no tengo dinero suficiente para aquellos que estan disponibles",M);
   				.send(owner, tell, msg(M));
			}
		}else{
			.concat("No he podido comprar más aperitios, me quedan: ",Dinero,"€ y el aperitivo más barato cuesta: ",PrecioB,"€",M);
   			.send(owner, tell, msg(M));
		}
	}.

-!bring(_,_) <- 
	.current_intention(I);
	.print("Failed to achieve goal '!has(_,_)'. Current intention is: ",I).

+!pay(Precio, Qtd, SuperBarato) <-
   ?cash(OldCash);
   Pago = Qtd * Precio;
   Cash = OldCash - Pago;
   -+cash(Cash);
   .send(SuperBarato, achieve, pago(Pago)).

+!go_at(robot,P) : at(robot,P) <- true.
+!go_at(robot,P) : not at(robot,P) <- 
	move_towards(P);
    !go_at(robot,P).

+!recycle <-
   .send(cocinero,achieve,recogerPlato);
   decideRandomRecycler;
   .send(owner,tell,robotRecycling);
   ?recyclingAgent(Ag);
   .send(Ag,achieve,recycle);
   -recyclingAgent(Ag);
   .send(owner,achieve,get(beer)).

+delivered(beer,Brand,Qtd,OrderId) : available(beer,fridge) <- 
	+available(beer,fridge);
	-delivered(beer,Brand,Qtd,OrderId).

+delivered(beer,Brand,Qtd,OrderId) <- 
	+available(beer,fridge);
    !bring(owner,beer);
    -delivered(beer,Brand,Qtd,OrderId).

+delivered(Aperitivo,Qtd,OrderId) <- 
	.send(cocinero,achieve,prepararPincho(Aperitivo));
    +available(pincho,mesa);
    !bring(owner,beer);
    -delivered(Aperitivo,Qtd,OrderId).

+stock(beer,0) : available(beer,fridge) <- 
	-available(beer,fridge).

+stock(beer,N) :  N > 0 & not available(beer,fridge) <- 
   -available(beer,fridge);
   +available(beer,fridge).

+stock(pincho,0) : available(pincho,mesa) <-
   -available(pincho,mesa);
   -stock(pincho,0).

+stock(pincho,N) : N > 0 & not available(pincho,mesa) <-
   -available(pincho,mesa);
   +available(pincho,mesa);
   -stock(pincho,N).

+!checkSupermarkets <-
	.abolish(priceBeer(_,_,_,_));
	.abolish(barataF(_,_,_,_,_));
	.abolish(barata(_,_,_,_,_));
	.abolish(masbarata(_,_,_,_,_));
	.send(mercadona, tell, tellPriceBeer);
	.send(gadis, tell, tellPriceBeer);

   	.abolish(pricePincho(_,_,_));
   	.abolish(barato(_,_,_,_));
   	.send(mercadona, tell, tellPricePincho);
	.send(gadis, tell, tellPricePincho).   

+!checkPrice <-
	!checkPriceBeer(beer);
	!checkPricePinchoT;
	!checkPricePinchoE;
	!checkPricePinchoJ;
	!checkPricePincho.

+!checkPriceBeer(Product) : favBeer(Marca) <-
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

+!checkPricePinchoT <-
	.findall(PrecioT, pricePincho(tortilla, PrecioT, StockT), ListTortilla);
	.min(ListTortilla, PrecioTortilla);
	?pricePincho(tortilla, PrecioTortilla, StockT)[source(SuperT)];
	+barato(tortilla, PrecioTortilla, StockT, SuperT).

+!checkPricePinchoE <-
	.findall(PrecioE, pricePincho(empanada, PrecioE, StockE), ListEmpanada);
	.min(ListEmpanada, PrecioEmpanada);
	?pricePincho(empanada, PrecioEmpanada, StockE)[source(SuperE)];
	+barato(empanada, PrecioEmpanada, StockE, SuperE).	
	
+!checkPricePinchoJ <-
	.findall(PrecioJ, pricePincho(jamon, PrecioJ, StockJ), ListJamon);
	.min(ListJamon, PrecioJamon);
	?pricePincho(jamon, PrecioJamon, StockJ)[source(SuperJ)];
	+barato(jamon, PrecioJamon, StockJ, SuperJ).
	
+!checkPricePincho <-
	.findall(Precio, pricePincho(Product, Precio, Stock), List);
	.min(List, PrecioBarato);
	?pricePincho(Product, PrecioBarato, Stock)[source(Super)];
	+masbarato(Product, PrecioBarato, Stock, Super).
	
+?time(T) <-  time.check(T).
