+delivered(beer, Marca, Qtd, OrderId) <-
	repartidorLlega;
    !go_at(repartidor, fridge);
    .send(robot, tell, delivered(beer, Marca, Qtd, OrderId));
    deliverBeer(beer, Marca, Qtd);
    .wait(200);
    !go_at(repartidor,repartidorBase);
	repartidorSeVa;
    -delivered(beer, Marca, Qtd, OrderId).

+delivered(Aperitivo, Qtd, OrderId) <-
	repartidorLlega;
    !go_at(repartidor, fridge);
    .send(robot, tell, delivered(Aperitivo, Qtd, OrderId));
    .wait(200);
    !go_at(repartidor, repartidorBase);
	repartidorSeVa;
    -delivered(Aperitivo, Qtd, OrderId).

+!go_at(repartidor,P) : at(repartidor,P) <- true.
+!go_at(repartidor,P) : not at(repartidor,P) <- 
	move_towards(P);
    !go_at(repartidor,P).
