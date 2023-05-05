!tellPrice.

+!tellPrice <-
	.findall(precioBase(Beer, MarcaB, PrecioB),precio(Beer, MarcaB, PrecioB),ListaBeer);
	.send(mercadona, tell, ListaBeer);
	.send(gadis, tell, ListaBeer);
	
	.findall(precioBase(Pincho, PrecioP),precio(Pincho, PrecioP), ListaPinchos);
	.send(mercadona, tell, ListaPinchos);
	.send(gadis, tell, ListaPinchos).

+!pago(Qtd) : money(Dinero) <-
    -money(Dinero);
    +money(Dinero+Qtd).
