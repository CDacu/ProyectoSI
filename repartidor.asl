+delivered(beer, Marca, Qtd, OrderId) <-
	repartidorLlega;
    !go_at(fridge);
    .send(robot, tell, delivered(beer, Marca, Qtd, OrderId));
    deliverBeer(beer, Marca, Qtd);
    .wait(200);
    !go_at(repartidorBase);
	repartidorSeVa;
    -delivered(beer, Marca, Qtd, OrderId).

+delivered(Aperitivo, Qtd, OrderId) <-
	repartidorLlega;
    !go_at(fridge);
    .send(robot, tell, delivered(Aperitivo, Qtd, OrderId));
    .wait(200);
    !go_at(repartidorBase);
	repartidorSeVa;
    -delivered(Aperitivo, Qtd, OrderId).

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
