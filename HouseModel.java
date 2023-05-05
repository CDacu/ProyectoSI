import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.ArrayList;
import java.util.logging.Logger;

/** class that implements the Model of Domestic Robot application */
public class HouseModel extends GridWorldModel {

    static Logger logger = Logger.getLogger(HouseModel.class.getName());

    // constants for the grid objects
    
    public static final int ALACENA         = 8;
    public static final int FRIDGE          = 16;
    public static final int OWNERCHAIR      = 32;
    public static final int DELIVERY        = 64;
    public static final int BIN             = 128;
    public static final int LAVAVAJILLAS    = 256;  
    public static final int OBSTACLE        = 1024;

    // the grid size
    public static final int GSize = 11;

    boolean fridgeOpen = false;         // whether the fridge is open
    boolean carryingBeer = false;       // whether the robot is carrying beer
    int sipCount = 0;                   // how many sip the owner did
    int availableBeers = 3;             // how many beers are available
    int pinchosTortilla = 0;            // how many pinchos de Tortilla are available
    int pinchosEmpanada = 0;            // how many pinchos de Empanada are available
    int pinchosBocata = 0;               // how many pinchos de Bocata are available
    int cansInBin = 0;                  // how many cans are in the rubbish bin
    int platosEnLavavajillas = 0;       // how many platos are in the Lavavajillas
    int platosEnAlacena = 15;           // how many platos are in the Alacena
	boolean repartiendo = false;
	boolean quemandoBasura = false;

    ArrayList<Location> lAgentes = new ArrayList<Location>();

    Location lFridge        = new Location(0, 0);
    Location lOwner         = new Location(GSize-1, GSize-1);
    Location lOwnerChair    = new Location(GSize-1, GSize-1);
    Location lDelivery      = new Location(0, GSize-1);
    Location lRepartidor    = new Location(1, GSize-1);
    Location lRobot         = new Location(GSize-1, GSize/2);
    Location lBin           = new Location(GSize-1, 0);
    Location lCan       = new Location(GSize-1, GSize/2);
    Location lBasurero      = new Location(GSize-1, 2);
    Location lLavavajillas  = new Location(2, 0);
    Location lAlacena       = new Location(1, 0); 
    Location lCocinero      = new Location(0, 2);
    Location lIncinerador   = new Location(GSize - 2, 0);

    ArrayList<Location> lObstaculos = new ArrayList<Location>();
    Location lObstaculo1 = newObstacle();
    Location lObstaculo2 = newObstacle();
    Location lObstaculo3 = newObstacle();
    Location lObstaculo4 = newObstacle();
    Location lObstaculo5 = newObstacle();
	Location lObstaculo6 = newObstacle();
	Location lObstaculo7 = newObstacle();

    Location closeTolFridge 	= new Location(0, 0);
    Location closeTolOwner  	= new Location(GSize-1, GSize-2);
    Location closeTolBin    	= new Location(GSize-1,1);
	Location closeTolAlacena = new Location(1, 1);

    //robot percetps
    boolean atFridge            = false;
    boolean atOwner             = false;
    boolean atDelivery          = false;
    boolean atBase              = false;
    boolean robotAtBin          = false;
	boolean atLavavajillas  	   = false;
	boolean atAlacena       	   = false;
    int recyclingAgent          = 2;
    int siguienteAperitivo      = 0;

    //repartidor percepts
    boolean repartidorAtFridge      = false;
    boolean repartidorAtDelivery    = false;
    boolean repartidorAtBase        = false;

    //basurero percepts
    boolean basureroAtBin       = false;
    boolean robotAtCan      = false;
    boolean basureroAtCan   = false;
    boolean basureroAtBase      = false;

    //owner percepts
    boolean ownerAtBin          = false;
    boolean ownerAtCan      = false;
    boolean ownerAtOwnerChair   = false;

    //cocinero percepts
    boolean cocineroAtFridge        = false;
    boolean cocineroAtOwner         = false;
    boolean cocineroAtLavavajillas  = false;
    boolean cocineroAtBase          = false;
    boolean cocineroAtAlacena       = false;

    //beercan belief
    boolean beerCanShow             = false;

    // Matrix used for findBestPath
    Integer[][] matrix = new Integer[GSize][GSize];

    public HouseModel() {
        // create a 11x11 grid with seven mobile agents
        super(GSize, GSize, 7);

        setAgPos(0, lRobot);
        setAgPos(1, lCan);
        setAgPos(2, lBasurero);
        setAgPos(3, lOwner);
        setAgPos(4, lCocinero);
        setAgPos(5, lIncinerador);
        setAgPos(6, lRepartidor);

        // Adding agents to initial position
        add(FRIDGE, lFridge);
        add(OWNERCHAIR, lOwnerChair);
        add(DELIVERY, lDelivery);
        add(BIN, lBin);
        add(LAVAVAJILLAS, lLavavajillas);
        add(ALACENA, lAlacena);

        //Adding Obstacles to position
        add(OBSTACLE, lObstaculo1);
        add(OBSTACLE, lObstaculo2);
        add(OBSTACLE, lObstaculo3);
        add(OBSTACLE, lObstaculo4);
        add(OBSTACLE, lObstaculo5);
		add(OBSTACLE, lObstaculo6);
		add(OBSTACLE, lObstaculo7);

    }

    boolean openFridge() {     
        fridgeOpen = true;
        return true;
    }

    boolean closeFridge() {
        fridgeOpen = false;
        return true;
    }
	
	boolean getBeer() {
        if (fridgeOpen && availableBeers > 0) {
            availableBeers--;
            carryingBeer = true;
            if (view != null)
                view.update(lFridge.x, lFridge.y);
            return true;
        } else {
            return false;
        }
    }

    boolean addBeer(int n) {
        availableBeers += n;
        if (view != null)
            view.update(lFridge.x, lFridge.y);
        return true;
    }

    boolean handInBeer() {
        if (carryingBeer) {
            sipCount = 10;
            carryingBeer = false;
            if (view != null)
                view.update(lOwner.x, lOwner.y);
            return true;
        } else {
            return false;
        }
    }

    boolean sipBeer() {
        if (sipCount > 0) {
            sipCount--;
            if (view != null)
                view.update(lOwner.x, lOwner.y);
            return true;
        } else {
            return false;
        }
    }

    boolean decideRandomRecycler() {
        double random = Math.random();
        if(random < 0.5) {
            recyclingAgent = 3; //Owner
            logger.info("Agente elegido para recoger la lata: OWNER");
        } else {
            recyclingAgent = 2; //Basurero
            logger.info("Agente elegido para recoger la lata: BASURERO");
        }
        return true;
    }

    boolean siguienteAperitivo() {
        double random = Math.random() * 2;  //There are 3 options
        siguienteAperitivo = (int) random;
        return true;
    }

    boolean quitarPlatosAlacena(int platos) {
        platosEnAlacena = platosEnAlacena - platos;
        return true;
    }

    boolean anadirPlatosAlacena(int platos) {
        platosEnAlacena += platos;
        return true;
    }

    boolean anadirPlatosLavavajillas(int platos) {
        platosEnLavavajillas += platos;
        return true;
    }

    boolean vaciar_lavavajillas() {
        anadirPlatosLavavajillas(platosEnLavavajillas);
        platosEnLavavajillas = 0;
        return true;
    }

    boolean prepararPincho(String pincode) {
        
        if(pincode.equals("tortilla")) {
            pinchosTortilla += 5;

        } else if (pincode.equals("empanada")) {
            pinchosEmpanada += 5;

        } else if (pincode.equals("bocata")) {
            pinchosBocata += 5;
        }

        return true;
    }

    boolean getPincho() {
        if(pinchosTortilla > 0){
            pinchosTortilla --;
        } else if (pinchosEmpanada > 0) {
            pinchosEmpanada --;
        } else if (pinchosBocata > 0) {
            pinchosBocata --;
        }
        return true;
    }


    boolean tirarLata() {
        	cansInBin++;
		if (view != null)
            view.update(lBin.x, lBin.y);
    	    return true;
    }

    boolean vaciar_papelera() {
        cansInBin = 0;
		quemandoBasura = false;
        if (view != null)
            view.update(lBin.x, lBin.y);
        return true;
    }
	
	boolean repartidorPop(){
		repartiendo = !repartiendo;
		return true;
	}

    boolean throwBeerCan() {
        lCan = getAgPos(1);
        boolean notValid = true;
        do {
            notValid = false;
            lCan = new Location((int) (Math.random() * 4 + 1), (int) (Math.random() * 4 + 1));

            for (Location lObs : lObstaculos) {
                if (sameLocation(lObs, lCan)) {
                    notValid = true;
                }
            }
        } while (notValid);
        //logger.info("can position settled : [" + lCan.x + "," + lCan.y + "]");
        setAgPos(1, lCan);

        beerCanShow = true;

        if (view != null)
            view.update(lCan.x, lCan.y);
        return true;
    }
	
	Location newObstacle() {
        Location lnewObs;
        boolean repetir;
        do {
            repetir = false;
            lnewObs = new Location((int) (Math.random() * 6 + 2), (int) (Math.random() * 6 + 2));
            for (Location lObs : lObstaculos) {
                if (sameLocation(lnewObs, lObs)) {
                    repetir = true;
                }
            }
        } while (repetir);
        lObstaculos.add(lnewObs);
        return lnewObs;
    }
	
	boolean sameLocation(Location r1, Location r2) {

        if ((r1.x - r2.x == 0) && (r1.y - r2.y == 0)) {
            return true;
        }
        return false;
    }
	
	boolean moveTowards(String mov, int agent) {
		
		Location r1 = getAgPos(agent);
		if(mov.equals("up")){
			r1.y--;
		}else if(mov.equals("down")){
			r1.y++;
		}else if(mov.equals("left")){
			r1.x--;
		}else if(mov.equals("right")){
			r1.x++;
		}
		
		setAgPos(agent, r1);
		
		if (view != null) {
            	view.update(lFridge.x,lFridge.y);
           	view.update(lOwner.x,lOwner.y);
            	view.update(lDelivery.x,lDelivery.y);
			view.update(lBin.x,lBin.y);
			view.update(lLavavajillas.x,lLavavajillas.y);
        }
        return true;
	}
	
	// POR DEBAJO DE ESTO ES EL MOVE TOWARDS VIEJO--------------------------------------------------------------------------
	// POR DEBAJO DE ESTO ES EL MOVE TOWARDS VIEJO--------------------------------------------------------------------------
	// POR DEBAJO DE ESTO ES EL MOVE TOWARDS VIEJO--------------------------------------------------------------------------
	// POR DEBAJO DE ESTO ES EL MOVE TOWARDS VIEJO--------------------------------------------------------------------------
	
	/*	

    // Comprueba si hay obstaculos a 1 de distancia en cruz
    boolean obstacleNear(Location r1) {
        for (Location lObs : lObstaculos) {
            if ((Math.abs(lObs.x - r1.x) + Math.abs(lObs.y - r1.y)) == 1) {
                return true;
            }
        }
        return false;
    }

    // Comprueba si hay "algo" a 1 de distancia en cruz
    boolean locationsNear(Location r1, Location dest) {
        if ((Math.abs(dest.x - r1.x) + Math.abs(dest.y - r1.y)) == 1) {
            return true;
        }
        return false;
    }

    // Comprueba si hay "algo" a 2 de distancia en cruz y a 1 en X
    //      X
    //    X X X
    //  X X 0 X X
    //    X X X
    //      X
    boolean obstacleNearDouble(Location r1) {
        for (Location lObs : lObstaculos) {
            if ((Math.abs(lObs.x - r1.x) + Math.abs(lObs.y - r1.y)) == 1) {
                return true;
            } else if (sameLocation(new Location(r1.x - 1, r1.y - 1), lObs)) {
                return true;
            } else if (sameLocation(new Location(r1.x - 1, r1.y + 1), lObs)) {
                return true;
            } else if (sameLocation(new Location(r1.x + 1, r1.y + 1), lObs)) {
                return true;
            } else if (sameLocation(new Location(r1.x + 1, r1.y - 1), lObs)) {
                return true;
            }
        }
        return false;
    }

    // Comprueba si hay obstaculos en sus siguiente movimiento
    boolean obstacleInTheWay(Location origin, Location dest) {

        if (Math.abs(origin.x - dest.x) > Math.abs(origin.y - dest.y)) {
            if (origin.x < dest.x) {
                origin = new Location(origin.x + 1, origin.y);
            } else if (origin.x > dest.x) {
                origin = new Location(origin.x - 1, origin.y);
            }
        } else {
            if (origin.y < dest.y) {
                origin = new Location(origin.x, origin.y + 1);
            } else if (origin.y > dest.y) {
                origin = new Location(origin.x, origin.y - 1);
            }
        }

        for (Location lObs : lObstaculos) {
            if (sameLocation(origin, lObs)) {
                return true;
            }
        }

        for (Location lAg : lAgentes) {
            if (sameLocation(origin, lAg)) {
                return true;
            }
        }

        return false;
    }

    boolean locationOutOfBounds(Location r1) {
        if (r1.x < 0 || r1.x >= GSize || r1.y < 0 || r1.y >= GSize) {
            return true;
        }
        return false;
    }

    void emptyMatrix() {
        for (int i = 0; i < GSize; i++) { // Fill matrix with 0s
            for (int j = 0; j < GSize; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    int findBestPath(int agent, Location origin, Location dest, int length) {

        if (locationOutOfBounds(origin)) {
            return Integer.MAX_VALUE;

        } else if (matrix[origin.x][origin.y] == 1) {
            //logger.info("Ya probado: [" + origin.x + "," + origin.y + "]");
            return Integer.MAX_VALUE;

        } else {
            matrix[origin.x][origin.y] = 1;
        }

        if (sameLocation(origin, dest)) {
            //logger.info("Length: " + length);
            return length;
        } else if (obstacleInTheWay(origin, dest)) {

            Location lRight = new Location(origin.x + 1, origin.y);
            Location lLeft = new Location(origin.x - 1, origin.y);
            Location lUp = new Location(origin.x, origin.y - 1);
            Location lDown = new Location(origin.x, origin.y + 1);

            boolean coincideLeft = false;
            boolean coincideRight = false;
            boolean coincideUp = false;
            boolean coincideDown = false;

            for (Location lObs : lObstaculos) {
                if (sameLocation(lLeft, lObs)) {
                    coincideLeft = true;
                }
                if (sameLocation(lRight, lObs)) {
                    coincideRight = true;
                }
                if (sameLocation(lDown, lObs)) {
                    coincideDown = true;
                }
                if (sameLocation(lUp, lObs)) {
                    coincideUp = true;
                }
            }

            //Evitar agentes
            for (Location lAg : lAgentes) {
                if (sameLocation(lLeft, lAg)) {
                    coincideLeft = true;
                }
                if (sameLocation(lRight, lAg)) {
                    coincideRight = true;
                } 
                if (sameLocation(lDown, lAg)) {
                    coincideDown = true;
                }
                if (sameLocation(lUp, lAg)) {
                    coincideUp = true;
                }
            }

            int leftLength = Integer.MAX_VALUE;
            int rightLength = Integer.MAX_VALUE;
            int upLength = Integer.MAX_VALUE;
            int downLength = Integer.MAX_VALUE;

            if (!coincideLeft) {
                leftLength = findBestPath(agent, lLeft, dest, length + 1);
            }
            if (!coincideRight) {
                rightLength = findBestPath(agent, lRight, dest, length + 1);
            }
            if (!coincideUp) {
                upLength = findBestPath(agent, lUp, dest, length + 1);
            }
            if (!coincideDown) {
                downLength = findBestPath(agent, lDown, dest, length + 1);
            }

            int min = Math.min(Math.min(Math.min(leftLength, rightLength), upLength), downLength);

            if (min == leftLength) {
                //logger.info("Choose Left - [" + lLeft.x + "," + lLeft.y + "]");
            } else if (min == rightLength) {
                //logger.info("Choose: Right - [" + lRight.x + "," + lRight.y + "]");
            } else if (min == upLength) {
                //logger.info("Choose: Up - [" + lUp.x + "," + lUp.y + "]");
            } else if (min == downLength) {
                //logger.info("Choose: Down - [" + lDown.x + "," + lDown.y + "]");
            }

            return min;

        } else {
            Location lNew = new Location(GSize, GSize);
            if (Math.abs(origin.x - dest.x) > Math.abs(origin.y - dest.y)) {
                if (origin.x < dest.x) {
                    lNew = new Location(origin.x + 1, origin.y);
                } else if (origin.x > dest.x) {
                    lNew = new Location(origin.x - 1, origin.y);
                }
            } else {
                if (origin.y < dest.y) {
                    lNew = new Location(origin.x, origin.y + 1);
                } else if (origin.y > dest.y) {
                    lNew = new Location(origin.x, origin.y - 1);
                }
            }
            //logger.info("Choose: [" + lNew.x + "," + lNew.y + "]");
            int returnLength = findBestPath(agent, lNew, dest, length + 1);
            return returnLength;
        }

    }

    boolean moveTowards(Location dest, int agent) {

        Location lOriginalDest = dest;

        // En realidad no son Agentes, son los objetivos de los Agentes
        lAgentes = new ArrayList<Location>();
        lAgentes.add(lFridge);
        lAgentes.add(lDelivery);
        lAgentes.add(lBin);
        lAgentes.add(lLavavajillas);
        lAgentes.add(lAlacena);
        
        for (int i = 0; i < 7; i++) {
            if (i != agent && i != 1) { // Not himself or the Can
                lAgentes.add(getAgPos(i));
            }
        }
        Location lnewDest = dest;

        for (int x = -1; x <= 1; x += 1) {
            for (int y = -1; y <= 1; y += 1) {
                lnewDest = new Location(lOriginalDest.x + x, lOriginalDest.y + y);
                boolean posible = true;

                if (!((Math.abs(lnewDest.x - lOriginalDest.x) + Math.abs(lnewDest.y - lOriginalDest.y)) == 1)){
                    //logger.info("Destino no cambiado a [" + lnewDest.x + "," + lnewDest.y + "]");
                    posible = false;
                }

                if(locationOutOfBounds(lnewDest)){
                    //logger.info("Destino no cambiado (por outOfBounds) a [" + lnewDest.x + "," + lnewDest.y + "]");
                    posible = false;
                }

                for (Location lObs : lObstaculos) {
                    if (sameLocation(lnewDest, lObs)) {
                        //logger.info("Destino no cambiado (por obstaculos) a [" + lnewDest.x + "," + lnewDest.y + "]");
                        posible = false;
                    }
                }
                for (Location lAg : lAgentes) {
                    if (sameLocation(lnewDest, lAg)) {
                        //logger.info("Destino no cambiado (por agentes) a [" + lnewDest.x + "," + lnewDest.y + "]");
                        posible = false;
                    }
                }

                if (posible) {
                    //logger.info("DESTINO CAMBIADO ");
                    dest = lnewDest;
                }
            }
        }
        
        //Excepciones
        if(sameLocation(lOriginalDest, getAgPos(1))){
            //logger.info("era la lata");
            dest = lOriginalDest;

        } else if (agent == 3 && sameLocation(lOriginalDest, lOwnerChair)){
            dest = lOriginalDest;

        } else if (agent == 4 && sameLocation(lOriginalDest, lCocinero)) {
            dest = lOriginalDest;

        } else if (agent == 2 && sameLocation(lOriginalDest, lBasurero)) {
            dest = lOriginalDest;
        
        }
         
        //logger.info("Destino: [" + dest.x + "," + dest.y + "]");
        return moveTowardsPart2(dest, agent);
    }

    boolean moveTowardsPart2(Location dest, int agent) {
        Location r1 = getAgPos(agent);
        if (obstacleInTheWay(r1, dest)) {

            Location lRight = new Location(r1.x + 1, r1.y);
            Location lLeft = new Location(r1.x - 1, r1.y);
            Location lUp = new Location(r1.x, r1.y - 1);
            Location lDown = new Location(r1.x, r1.y + 1);

            boolean coincideLeft = false;
            boolean coincideRight = false;
            boolean coincideUp = false;
            boolean coincideDown = false;

            //Evitar obstáculos
            for (Location lObs : lObstaculos) {
                if (sameLocation(lLeft, lObs)) {
                    coincideLeft = true;
                    //logger.info("Left blocked");
                }
                if (sameLocation(lRight, lObs)) {
                    coincideRight = true;
                    //logger.info("Right blocked");
                }
                if (sameLocation(lDown, lObs)) {
                    coincideDown = true;
                }
                if (sameLocation(lUp, lObs)) {
                    coincideUp = true;
                }
            }

            //Evitar agentes
            for (Location lAg : lAgentes) {
                if (sameLocation(lLeft, lAg)) {
                    coincideLeft = true;
                }
                if (sameLocation(lRight, lAg)) {
                    coincideRight = true;
                } 
                if (sameLocation(lDown, lAg)) {
                    coincideDown = true;
                }
                if (sameLocation(lUp, lAg)) {
                    coincideUp = true;
                }
            }

            int leftLength = Integer.MAX_VALUE;
            int rightLength = Integer.MAX_VALUE;
            int upLength = Integer.MAX_VALUE;
            int downLength = Integer.MAX_VALUE;

            if (!coincideLeft) {
                //logger.info("Trying left");
                emptyMatrix();
                leftLength = findBestPath(agent, lLeft, dest, 1);
                //logger.info("Left Length: " + leftLength);
            }
            if (!coincideRight) {
                //logger.info("Trying right");
                emptyMatrix();
                rightLength = findBestPath(agent, lRight, dest, 1);
                //logger.info("Right Length: " + rightLength);
            }
            if (!coincideUp) {
                //logger.info("Trying up");
                emptyMatrix();
                upLength = findBestPath(agent, lUp, dest, 1);
                //logger.info("Up Length: " + upLength);
            }
            if (!coincideDown) {
                //logger.info("Trying down");
                emptyMatrix();
                downLength = findBestPath(agent, lDown, dest, 1);
                //logger.info("Down Length: " + downLength);
            }

            int min = Math.min(Math.min(Math.min(leftLength, rightLength), upLength), downLength);

            if (min == leftLength) {
                r1.x--;
                //logger.info("Choosen path: Left");
            } else if (min == rightLength) {
                r1.x++;
                //logger.info("Choosen path: Right");
            } else if (min == upLength) {
                r1.y--;
                //logger.info("Choosen path: Up");
            } else if (min == downLength) {
                r1.y++;
                //logger.info("Choosen path: Down");
            }

        } else {
            if (Math.abs(r1.x - dest.x) > Math.abs(r1.y - dest.y)) { // Its choosing the coordinate its more far away
                                                                     // and
                                                                     // prioritizing it
                if (r1.x < dest.x)
                    r1.x++;
                else if (r1.x > dest.x)
                    r1.x--;
            } else {
                if (r1.y < dest.y)
                    r1.y++;
                else if (r1.y > dest.y)
                    r1.y--;
            }
        }

        setAgPos(agent, r1); // move the robot in the grid

        Location rIncinerador = getAgPos(5);
        setAgPos(5, rIncinerador);

        if(CanShow) {
            Location rCan = getAgPos(1);
            setAgPos(1, rCan);
        }

        switch (agent) {
            case 0: // robot
                atOwner = locationsNear(r1, lOwner);
                atFridge = locationsNear(r1, lFridge);                
                atDelivery = locationsNear(r1, lDelivery);
                atBase = r1.equals(lRobot);
			    atLavavajillas = locationsNear(r1, lLavavajillas);
			    atAlacena = r1.equals(closeTolAlacena);
                robotAtBin = r1.equals(lBin);
                robotAtCan = r1.equals(lCan);
                break;
            case 2: // basurero
                basureroAtBin = locationsNear(r1, lBin);
                basureroAtCan = r1.equals(lCan);
                basureroAtBase = r1.equals(lBasurero);
                break;
            case 3: // owner
                ownerAtBin = locationsNear(r1, lBin);
                ownerAtCan = r1.equals(lCan);
                ownerAtOwnerChair = r1.equals(lOwnerChair);
                break;
            case 4 : //cocinero
                cocineroAtFridge = locationsNear(r1, lFridge);
                cocineroAtOwner = locationsNear(r1, lOwner);
                cocineroAtBase = r1.equals(lCocinero);
                cocineroAtAlacena = locationsNear(r1, lAlacena);
                break;
            case 6:  //repartidor
                repartidorAtFridge = locationsNear(r1, lFridge);
                repartidorAtDelivery = locationsNear(r1, lDelivery);
                repartidorAtBase = r1.equals(lRepartidor);
                break;

        }

        // repaint the fridge and owner locations
        if (view != null) {
            view.update(lFridge.x, lFridge.y);
            view.update(lOwnerChair.x, lOwnerChair.y);
            view.update(lDelivery.x, lDelivery.y);
            view.update(lBin.x, lBin.y);
            view.update(lAlacena.x, lAlacena.y);
            view.update(lLavavajillas.x, lLavavajillas.y);
        }
        return true;
    }
	*/
}

