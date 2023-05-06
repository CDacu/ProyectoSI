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

    boolean fridgeOpen = false;
    boolean carryingBeer = false;       
    int sipCount = 0;                   
    int availableBeers = 3;             
    int pinchosTortilla = 0;            
    int pinchosEmpanada = 0;           
    int pinchosBocata = 0;              
    int cansInBin = 0;                 
    int platosEnLavavajillas = 0;       
    int platosEnAlacena = 15;           
	int siguienteAperitivo = 0;
	
	boolean repartiendo = false;
	boolean quemandoBasura = false; 
    boolean beerCanShow = false;

    Location lFridge        = new Location(0, 0);
    Location lOwner         = new Location(GSize-1, GSize-1);
    Location lOwnerChair    = new Location(GSize-1, GSize-1);
    Location lDelivery      = new Location(0, GSize-1);
    Location lRepartidor    = new Location(1, GSize-1);
    Location lRobot         = new Location(GSize-1, GSize/2);
    Location lBin           = new Location(GSize-3, 0);
    Location lCan       	= new Location(GSize-1, GSize/2);
    Location lBasurero      = new Location(GSize-1, 0);
    Location lLavavajillas  = new Location(2, 0);
    Location lAlacena       = new Location(1, 0); 
    Location lCocinero      = new Location(0, 2);
    Location lIncinerador   = new Location(GSize - 4, 0);

    ArrayList<Location> lObstaculos = new ArrayList<Location>();
    Location lObstaculo1 = newObstacle();
    Location lObstaculo2 = newObstacle();
    Location lObstaculo3 = newObstacle();
    Location lObstaculo4 = newObstacle();
    Location lObstaculo5 = newObstacle();
	Location lObstaculo6 = newObstacle();
	Location lObstaculo7 = newObstacle();

    public HouseModel() {
        // create a 11x11 grid with seven mobile agents
        super(GSize, GSize, 6);

        setAgPos(0, lRobot);
        setAgPos(1, lCan);
        setAgPos(2, lBasurero);
        setAgPos(3, lOwner);
        setAgPos(4, lRepartidor);
		setAgPos(5, lIncinerador);

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

    boolean siguienteAperitivo() {
        double random = Math.random() * 2;
        siguienteAperitivo = (int) random;
        return true;
    }

    boolean quitarPlatosAlacena(int platos) {
        platosEnAlacena -= platos;
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
		if (view != null){
			view.update(lIncinerador.x, lIncinerador.y);
		}
		try{
			Thread.sleep(2000);
		}catch (Exception e) {
			logger.info("Failed to execute action tirarLata! " + e);
		}
        cansInBin = 0;
		quemandoBasura = false;
        if (view != null)
			view.update(lIncinerador.x, lIncinerador.y);
            	view.update(lBin.x, lBin.y);
        return true;
    }
	
	boolean repartidorLlega(){
		repartiendo = true;
		return true;
	}
	
	boolean repartidorSeVa(){
		repartiendo = false;
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
	
	int getObstaculoPosX (int obstaculo) {
		return lObstaculos.get(obstaculo).x;
	}
	
	int getObstaculoPosY (int obstaculo) {
		return lObstaculos.get(obstaculo).y;
	}
	
	boolean moveTowards(String mov, int agent) {
		
		Location r1 = getAgPos(agent);
		if(mov.equals("up")){
			if(r1.y == 0){
				return false;
			}else{
				r1.y--;
			}	
		}else if(mov.equals("down")){
			if(r1.y == 10){
				return false;
			}else{
				r1.y++;
			}	
		}else if(mov.equals("left")){
			if(r1.x == 0){
				return false;
			}else{
				r1.x--;
			}	
		}else if(mov.equals("right")){
			if(r1.x == 10){
				return false;
			}else{
				r1.x++;
			}	
		}
		
		setAgPos(agent, r1);
		
		if (view != null) {
            view.update(lFridge.x,lFridge.y);
           	view.update(lOwner.x,lOwner.y);
            view.update(lDelivery.x,lDelivery.y);
			view.update(lBin.x,lBin.y);
			view.update(lLavavajillas.x,lLavavajillas.y);
			view.update(lRepartidor.x, lRepartidor.y);
			view.update();
        }
        return true;
	}
}
