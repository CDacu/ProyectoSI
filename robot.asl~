available(beer,fridge).

limit(beer,7).

tamPackBeer(3).

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

+reponerCerveza <-
  	!checkSupermarkets;
 	.send(repartidor, achieve, go_at(delivery));
   	.wait(500);
	!checkPrice;
	.wait(50);
   	!buySupermarketBeer;
	-reponerCerveza.

+!bring(owner,beer) :  not available(beer,fridge) & available(pincho,fridge) <- 
	!checkSupermarkets;
	.send(repartidor, achieve, go_at(delivery));
	.wait(500);
	!checkPrice; 
	.wait(50);
	!buySupermarketBeer.

+!bring(owner,beer) : available(beer,fridge) & not available(pincho,fridge) <-
   	siguienteAperitivo;
	!checkSupermarkets;
	.send(repartidor, achieve, go_at(delivery));
	.wait(500);
	!checkPrice;
	.wait(50);
	!buySupermarketPincho.  

+!bring(owner,beer) : not available(beer,fridge) & not available(pincho,fridge) <-
   siguienteAperitivo;
   !checkSupermarkets;
   .send(repartidor, achieve, go_at(delivery));
   .wait(500);
   !checkPrice;
   .wait(50);
   !buySupermarketBeer;
   !buySupermarketPincho.

+!bring(owner,beer) : too_much(beer) & healthMsg <-
   .wait(5000);
   !tellTime;
   !bring(owner,beer).

+!tellTime <-
   	.time(HH,NN,SS);
   	.concat("La hora es: ", HH,":",NN,":",SS, M);
   	.send(owner,tell,msg(M)).
 
+!bring(owner,beer) : carringPlato <- 
	.wait(100); 
	!bring(owner,beer).		

+!bring(owner,beer) :  available(beer,fridge) & not too_much(beer) & available(pincho,fridge) & not carringPlato <- 
	+carringPlato;
	!go_at(fridge);
	open(fridge);
	get(beer);
	get(pincho);
	close(fridge);
    !go_at(ownerchair);
    hand_in(beer);
	!go_at(lavavajillas);
	anadirPlatosLavavajillas(1);
    .date(YY,MM,DD); .time(HH,NN,SS);
    +consumed(YY,MM,DD,HH,NN,SS,beer);
	-carringPlato.
	
//-!bring(_,_) <- 
//	.current_intention(I);
//	.print("Failed to achieve goal '!has(_,_)'. Current intention is: ",I).	
	
+!prepararPincho(Product) <-
    !go_at(alacena);
    quitarPlatosAlacena(5);
    !go_at(fridge);
    .wait(1000);
    prepararPincho(Product).	

+!buySupermarketBeer : favBeer(Marca) & barataF(Product, Marca, Precio, Stock, Super) & money(Dinero) & tamPackBeer(Qtd) <-
	if(Precio * Qtd <= Dinero){	
		if(Qtd <= Stock){												
			.send(Super, achieve, order(Product, Marca, Qtd));
   			!pagar(Precio, Qtd, Super); 
		}else{
			Resto = Qtd - Stock;
			?barata(Product, MarcaB, PrecioB, StockB, Super);
			if(Stock > 0){
				.send(Super, achieve, order(Product, Marca, Stock));
				!pagar(Precio, Stock, Super);
				.send(Super, achieve, order(Product, MarcaB, Resto));
				!pagar(PrecioB, Resto, Super);
			}else{
				?masbarata(Product, MarcaC, PrecioC, StockC, SuperC);
				if(StockC >= Qtd){
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

+!buySupermarketPincho : siguienteAperitivo(Pincho) & barato(Pincho, Precio, Stock, Super) & money(Dinero) <-

	if(Dinero >= Precio){
		if(Stock >= 1){
		.send(Super, achieve, order(Pincho, 1));
   		!pagar(Precio, 1, Super);
		}
	}else{
		?masbarato(PinchoB, PrecioB, StockB, SuperB);
		if(Dinero >= PrecioB){
			if(StockB >= 1){
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

+!pagar(Precio, Qtd, SuperBarato) : money(Dinero) <-
   Pago = Qtd * Precio;
   DineroActual = Dinero - Pago;
   -+money(DineroActual);
   .send(SuperBarato, achieve, pago(Pago)).

+delivered(beer,Marca,Qtd,OrderId) : available(beer,fridge) <- 
	-delivered(beer,Marca,Qtd,OrderId).

+delivered(beer,Marca,Qtd,OrderId) <- 
	+available(beer,fridge);
    !bring(owner,beer);
    -delivered(beer,Marca,Qtd,OrderId).
	
+delivered(Aperitivo,Qtd,OrderId) <-
	!prepararPincho(Aperitivo);
    +available(pincho,fridge);
    !bring(owner,beer);
    -delivered(Aperitivo,Qtd,OrderId).

+stock(beer,N) <-
	if(N = 0){
	    -available(beer,fridge);
	}else{
		-+available(beer,fridge);
	}
	-stock(beer,N).
	
+stock(pincho,N) <-
	if(N = 0){
	    -available(pincho,fridge);
	}else{
		-+available(pincho,fridge);
	}
	-stock(pincho,N).

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
	
+lavavajillasLleno <-
  	!go_at(lavavajillas);
	.wait(500);
	vaciar_lavavajillas;
	!go_at(robot,alacena);
	.wait(500);
	anadirPlatosAlacena(5);
	-lavavajillasLleno.
	
+?time(T) <- time.check(T).

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
