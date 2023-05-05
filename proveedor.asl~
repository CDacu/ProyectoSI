!tellPriceBeer.
!tellPricePincho.

+!tellPriceBeer <-
	.findall(precioProveedor(Product, Marca, Price),price(Product, Marca, Price),ListaBeer);
	.send(mercadona, tell, ListaBeer);
	.send(gadis, tell, ListaBeer).

+!tellPricePincho <-
	.findall(precioProveedor(Product, Price),price(Product, Price), ListaPinchos);
	.send(mercadona, tell, ListaPinchos);
	.send(gadis, tell, ListaPinchos).

+!pago(Qtd) : cash(Dinero) <-
    -cash(Dinero);
    +cash(Dinero+Qtd).
