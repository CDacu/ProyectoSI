last_order_id(1).

!establecerPrecios.

+!establecerPrecios : 	precioBase(tortilla, PrecioTortilla) 
               			& precioBase(empanada, PrecioEmpanada) 
						& precioBase(bocata, PrecioBocata)
						& precioBase(beer, 1906, Precio1906)
						& precioBase(beer, bock, PrecioBock)
						& precioBase(beer, estrella, PrecioEstrella) <-
	.random(T);
    +stock(tortilla, T*3 + PrecioTortilla, 3);
	.random(E);
    +stock(empanada, E*3 + PrecioEmpanada, 3);
	.random(J);
    +stock(bocata, J*3 + PrecioBocata, 3);
	.random(B1906);
    +stock(beer, 1906, B1906*3 + Precio1906, 8);
	.random(BE);
    +stock(beer, estrella, BE*3 + PrecioEstrella, 8);
	.random(BB);
    +stock(beer, bock, BB*3 + PrecioBock, 8).

+!establecerPrecios <-
     .wait(50);
     !establecerPrecios.

+!order(Product, Marca, Qtd) : stock(Product, Marca, Price, Stock) & Stock >= Qtd <- 
     ?last_order_id(N);
     OrderId = N + 1;
     -+last_order_id(OrderId);
     -stock(Product, Marca, Price, Stock);
     +stock(Product, Marca, Price, Stock-Qtd);
     .send(repartidor, achieve, delivered(Product, Marca, Qtd, OrderId));
	 if(Stock < 2){
     	!reponerStock(Product, Marca, 3);
	 }.
	 
+!order(Product, Marca, Qtd, MarcaB, QtdB) : stock(Product, Marca, Price, Stock) & Stock >= Qtd & stock(Product, MarcaB, PriceB, StockB) & StockB >= QtdB <- 
     ?last_order_id(N);
     OrderId = N + 1;
     -+last_order_id(OrderId);
     -stock(Product, Marca, Price, Stock);
     +stock(Product, Marca, Price, Stock-Qtd);
	 -stock(Product, MarcaB, PriceB, StockB);
     +stock(Product, MarcaB, PriceB, StockB-QtdB);
     .send(repartidor, achieve, delivered(Product, Marca, Qtd, OrderId));
	 if(Stock < 2){
     	!reponerStock(Product, Marca, 3);
	 }
	 if(StockB < 2){
     	!reponerStock(Product, MarcaB, 3);
	 }.

+!order(Product,Qtd) : stock(Product, Price, Stock) & Stock >= Qtd <- 
     ?last_order_id(N);
     OrderId = N + 1;
     -+last_order_id(OrderId);
     -stock(Product, Price, Stock);
     +stock(Product, Price, Stock-Qtd);
     .send(repartidor, achieve, delivered(Product, Qtd, OrderId));
	 if(Stock < 2){
	 	!reponerStock(Product, 3);
	 }.   

+!order(Product, Marca, Qtd)[source(X)] <-
    ?stock(Product, Marca, Price, Stock);
    .concat("No queda stock de cerveza de la marca ", Marca, ". El pedido realizado es de ", Qtd, " cervezas y en stock quedan: ", Stock, M);
    .send(owner, tell, msg(M)).

+!order(Product,Qtd)[source(X)] <-
    ?stock(Product, Price, Stock);
    .concat("No queda stock de ", Product, ". El pedido realizado es de ", Qtd, " y en stock quedan: ", Stock, M);
    .send(owner, tell, msg(M)).

+!reponerStock(Product, Qtd) : stock(Product, _ , Stock) & precioBase(Product, Precio) & money(Dinero) <-
	if(Qtd > 0){
		DineroActual = Dinero;
		if(DineroActual >= Precio * Qtd){
			-+money(Dinero - Precio * Qtd);
			.send(proveedor, achieve, pago(Precio * Qtd));
			-stock(Product, _, Stock);
			+stock(Product, _, Stock + Qtd);
		}else{
			!reponerStock(Product,Qtd-1);
		}
	}.

+!reponerStock(Product, Marca, Qtd) : stock(Product, Marca , _ , Stock) & precioBase(Product, Marca, Precio) & money(Dinero) <-
	if(Qtd > 0){
		DineroActual = Dinero;
		if(DineroActual >= Precio * Qtd){
			-+money(Dinero - Precio * Qtd);
			.send(proveedor, achieve, pago(Precio * Qtd));
			-stock(Product, Marca, _, Stock);
			+stock(Product, Marca, _, Stock + Qtd);
		}else{
			!reponerStock(Product, Marca, Qtd-1);
		}
	}.	

+!pago(Qtd) : money(Dinero) <-
     DineroActual = Dinero + Qtd;
	 -money(_);
     +money(DineroActual). 

+!tellPrice(X) <-
	.findall(priceBeer(Product, Marca, Precio, Qtd),stock(Product, Marca, Precio, Qtd),ListaBeer);
	.send(X, tell, ListaBeer);
	.findall(pricePincho(Product, Precio, Qtd),stock(Product, Precio, Qtd), ListaPinchos);
	.send(X, tell, ListaPinchos).

+tellPrice[source(X)] <-
     !tellPrice(X);
     -tellPrice[source(X)].
