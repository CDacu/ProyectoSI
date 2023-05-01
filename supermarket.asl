last_order_id(1).

!tellPrice.
!randomizarPrecios.

+!randomizarPrecios : 	precioProveedor(tortilla, PrecioTortilla) 
               			& precioProveedor(empanada, PrecioEmpanada) 
						& precioProveedor(jamon, PrecioJamon)
						& precioProveedor(beer, 1906, Precio1906)
						& precioProveedor(beer, bock, PrecioBock)
						& precioProveedor(beer, estrella, PrecioEstrella) <-
     +stock(tortilla, 5 + PrecioTortilla, 3);
     +stock(empanada, 5 + PrecioEmpanada, 3);
     +stock(jamon, 5 + PrecioJamon, 3);
     +stock(beer, 1906, 3 + Precio1906, 8);
     +stock(beer, estrella, 3 + PrecioEstrella, 8);
     +stock(beer, bock, 3 + PrecioBock, 8).

+!randomizarPrecios : true <-
     .wait(50);
     !randomizarPrecios.

+!order(Product, Marca, Qtd)[source(_)] : stock(Product, Marca, Price, Stock) & Stock >= Qtd <- 
     ?last_order_id(N);
     OrderId = N + 1;
     -+last_order_id(OrderId);
     -stock(Product, Marca, Price, Stock);
     +stock(Product, Marca, Price, Stock-Qtd);
     .send(repartidor, tell, delivered(Product, Marca, Qtd, OrderId));
     !reponerStock(Product, Marca).

+!order(Product,Qtd)[source(_)] : stock(Product, Price, Stock) & Stock >= Qtd <- 
     ?last_order_id(N);
     OrderId = N + 1;
     -+last_order_id(OrderId);
     -stock(Product, Price, Stock);
     +stock(Product, Price, Stock-Qtd);
     .send(repartidor, tell, delivered(Product, Qtd, OrderId));
     !reponerStock(Product).

+!order(Product, Marca, Qtd)[source(X)] <-
    ?stock(Product, Marca, Price, Stock);
    .concat("No queda stock de cerveza de la marca ", Marca, ". El pedido realizado es de ", Qtd, " cervezas y en stock quedan: ", Stock, M);
    .send(owner, tell, msg(M)).

+!order(Product,Qtd)[source(X)] <-
    ?stock(Product, Price, Stock);
    .concat("No queda stock de ", Product, ". El pedido realizado es de ", Qtd, " y en stock quedan: ", Stock, M);
    .send(owner, tell, msg(M)).

+!reponerStock(Product) : stock(Product, _ , Qtd) & Qtd < 3 & precioProveedor(Product, Precio) & cash(Dinero) & Dinero >= Precio <-
     -cash(Dinero);
     +cash(Dinero - Precio);
     .send(proveedor, achieve, pago(Precio));
     -stock(Product, _ , Qtd);
     +stock(Product, _ , Qtd+1);
     !reponerStock(Product).   

+!reponerStock(Product, Marca) : stock(Product, Marca , _ , Qtd) & Qtd < 3 & precioProveedor(Product, Marca, Precio) & cash(Dinero) & Dinero >= Precio <-
     -cash(Dinero);
     +cash(Dinero - Precio);
     .send(proveedor, achieve, pago(Precio));
     -stock(Product, _ , _ , Qtd);
     +stock(Product, _ , _ , Qtd+1);
     !reponerStock(Product, Marca).

+!reponerStock(_) <- true.

+!reponerStock(beer, _) <- true.

+!pago(Qtd) <-
     ?cash(OldCash);
     Cash = OldCash + Qtd;
     -+cash(Cash). 

+!tellPrice <-
     !tellPriceBeer;
     !tellPricePincho.

+!tellPriceBeer <-
	.findall(priceBeer(Product, Marca, Price, Qtd),stock(Product, Marca, Price, Qtd),ListaBeer);
	.send(robot, tell, ListaBeer).

+!tellPricePincho <-
	.findall(pricePincho(Product, Price, Qtd),stock(Product, Price, Qtd), ListaPinchos);
	.send(robot, tell, ListaPinchos).

+tellPrice[source(X)] <-
	!tellPriceBeer;
	!tellPricePincho;
	-tellPrice[source(X)].

+tellPriceBeer[source(X)] <-
     !tellPriceBeer;
     -tellPriceBeer[source(X)].

+tellPricePincho[source(X)] <-
     !tellPricePincho;
     -tellPricePincho[source(X)].
