+!prepararPincho(Product) <-
    !go_at(cocinero, alacena);
    quitarPlatosAlacena(5);
    !go_at(cocinero,fridge);
    .wait(100);
    !go_at(cocinero,mesaPincho);
    prepararPincho(Product);
    !go_at(cocinero,cocineroBase).

+lavavajillasLleno <-
  !go_at(cocinero,lavavajillas);
  .wait(100);
  vaciar_lavavajillas;
  !go_at(cocinero,alacena);
  .wait(100);
  anadirPlatosAlacena(5);
  -lavavajillasLleno.
  
+!recogerPlato <-
  !go_at(cocinero,owner);
  .wait(100);
  !go_at(cocinero,lavavajillas);
  anadirPlatosLavavajillas(1);
  !go_at(cocinero,cocineroBase).

+!go_at(cocinero,P) : at(cocinero,P) <- true.	
+!go_at(cocinero,P) : not at(cocinero,P) <- 
	move_towards(P);
    !go_at(cocinero,P).
