import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.Location;

import java.util.logging.Logger;

public class HouseEnv extends Environment {

    // common literals
    public static final Literal of = Literal.parseLiteral("open(fridge)");
    public static final Literal cf = Literal.parseLiteral("close(fridge)");
    public static final Literal gb = Literal.parseLiteral("get(beer)");
    public static final Literal gp = Literal.parseLiteral("get(pincho)");
    public static final Literal hb = Literal.parseLiteral("hand_in(beer)");
    public static final Literal sb = Literal.parseLiteral("sip(beer)");
    public static final Literal hob = Literal.parseLiteral("has(owner,beer)");
    public static final Literal rc = Literal.parseLiteral("tirarLata");
    public static final Literal tbc = Literal.parseLiteral("throwBeerCan");
	
	public static final Literal clf = Literal.parseLiteral("lavavajillasLleno");
	public static final Literal ibf = Literal.parseLiteral("papeleraLlena");
	
	public static final Literal rll = Literal.parseLiteral("repartidorLlega");
	public static final Literal rsv = Literal.parseLiteral("repartidorSeVa");
	
	// Posiciones que los agentes pueden atravesar
	public static final Literal posCloseFridge = Literal.parseLiteral("position(fridge,0,1)");
	public static final Literal posCloseOwnerChair = Literal.parseLiteral("position(closeownerchair,10,9)");
	public static final Literal posCloseBin = Literal.parseLiteral("position(bin,8,1)");
	public static final Literal posCloseLavavajillas = Literal.parseLiteral("position(lavavajillas,2,1)");
	public static final Literal posCloseAlacena = Literal.parseLiteral("position(alacena,1,1)");
	
	//Solo el owner puede atravesar esta posicion
	public static final Literal posOwnerChair = Literal.parseLiteral("position(ownerchair,10,10)");
	public static final Literal posOwnerChairObs = Literal.parseLiteral("position(obstaculo,10,10)");
	
	//Solo el repartidor puede atravesar esta posicion
	public static final Literal posDelivery = Literal.parseLiteral("position(delivery,0,10)");
	public static final Literal posDeliveryObs = Literal.parseLiteral("position(obstaculo,0,10)");
	public static final Literal posRepartidorBase = Literal.parseLiteral("position(repartidorBase,1,10)");
	public static final Literal posRepartidorBaseObs = Literal.parseLiteral("position(obstaculo,1,10)");
	
	//Solo el basurero puede atravesar esta posicion
	public static final Literal posBasureroBase = Literal.parseLiteral("position(basureroBase,10,0)");
	public static final Literal posBasureroBaseObs = Literal.parseLiteral("position(obstaculo,10,0)");
	                                                                                  
	// Posiciones no atravesables
	public static final Literal posFridge = Literal.parseLiteral("position(obstaculo,0,0)");
	public static final Literal posIncinerador = Literal.parseLiteral("position(obstaculo,7,0)");
	public static final Literal posAlacena = Literal.parseLiteral("position(obstaculo,1,0)");
	public static final Literal posLavavajillas = Literal.parseLiteral("position(obstaculo,2,0)");
	public static final Literal posBin = Literal.parseLiteral("position(obstaculo,8,0)");  

    static Logger logger = Logger.getLogger(HouseEnv.class.getName());

    HouseModel model; // the model of the grid

    @Override
    public void init(String[] args) {
        model = new HouseModel();

        if (args.length == 1 && args[0].equals("gui")) {
            HouseView view = new HouseView(model);
            model.setView(view);
        }

        updatePercepts();
    }

    /** creates the agents percepts based on the HouseModel */
    void updatePercepts() {
		
		String[] agentList = {"robot", "owner", "basurero", "repartidor"};
		
		for(String agent : agentList){
			
			clearPercepts(agent);
			
			// Pos atravesables
			addPercept(agent, posCloseFridge);
			addPercept(agent, posCloseOwnerChair);
			addPercept(agent, posCloseBin);
			addPercept(agent, posCloseLavavajillas);
			addPercept(agent, posCloseAlacena);
			
			//Pos no atravesables
			addPercept(agent, posIncinerador);
			addPercept(agent, posFridge);
			addPercept(agent, posAlacena);
			addPercept(agent, posLavavajillas);
			addPercept(agent, posBin);
			
			addPercept(agent, Literal.parseLiteral("position(robot," + model.getAgPos(0).x + "," + model.getAgPos(0).y + ")"));
			
			if(model.beerCanShow){
				addPercept(agent, Literal.parseLiteral("position(can," + model.getAgPos(1).x + "," + model.getAgPos(1).y + ")"));	
			}
			
			addPercept(agent, Literal.parseLiteral("position(basurero," + model.getAgPos(2).x + "," + model.getAgPos(2).y + ")"));
			addPercept(agent, Literal.parseLiteral("position(owner," + model.getAgPos(3).x + "," + model.getAgPos(3).y + ")"));
			addPercept(agent, Literal.parseLiteral("position(repartidor," + model.getAgPos(4).x + "," + model.getAgPos(4).y + ")"));
			
			for(int i=0; i < 7; i++){
				addPercept(agent, Literal.parseLiteral("position(obstaculo,"+model.getObstaculoPosX(i)+","+model.getObstaculoPosY(i)+")"));
			}
		}
		
		addPercept("robot", posOwnerChairObs);
		addPercept("basurero", posOwnerChairObs);
		addPercept("repartidor", posOwnerChairObs);
		addPercept("owner", posOwnerChair);
			
		addPercept("robot", posDeliveryObs);
		addPercept("owner", posDeliveryObs);
		addPercept("basurero", posDeliveryObs);
		addPercept("repartidor", posDelivery);
		
		addPercept("robot", posRepartidorBaseObs);
		addPercept("owner", posRepartidorBaseObs);
		addPercept("basurero", posRepartidorBaseObs);
		addPercept("repartidor", posRepartidorBase);
		
		addPercept("robot", posBasureroBaseObs);
		addPercept("owner", posBasureroBaseObs);
		addPercept("basurero", posBasureroBase);
		addPercept("repartidor", posBasureroBaseObs);
		
        if (model.siguienteAperitivo == 0){
            addPercept("robot", Literal.parseLiteral("siguienteAperitivo(tortilla)"));
        } else if (model.siguienteAperitivo == 1){
            addPercept("robot", Literal.parseLiteral("siguienteAperitivo(empanada)"));
        } else if (model.siguienteAperitivo == 2){
            addPercept("robot", Literal.parseLiteral("siguienteAperitivo(bocata)"));
        }	
		
		if(model.cansInBin >= 5){
			addPercept("incinerador",ibf);
		}
        if (model.platosEnLavavajillas >= 5) {
            addPercept("robot", clf);
        }
         
		addPercept("robot", Literal.parseLiteral("stock(beer," + model.availableBeers + ")"));
     
        if ( model.fridgeOpen && model.availableBeers == 1) {
            addPercept("robot", Literal.parseLiteral("reponerCerveza"));
        }
        if (model.pinchosTortilla > 0) {
            addPercept("robot", Literal.parseLiteral("stock(pincho," + model.pinchosTortilla + ")"));
        } else if (model.pinchosEmpanada > 0) {
            addPercept("robot", Literal.parseLiteral("stock(pincho," + model.pinchosEmpanada + ")"));
        } else if (model.pinchosBocata > 0) {
            addPercept("robot", Literal.parseLiteral("stock(pincho," + model.pinchosBocata + ")"));
        } else {
            addPercept("robot", Literal.parseLiteral("stock(pincho,0)"));
        }

        if (model.sipCount > 0) {
            addPercept("robot", hob);
            addPercept("owner", hob);
        }
		if(model.sipCount == 0){
			removePercept("robot", hob);
			removePercept("owner", hob);
		}
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        System.out.println("[" + ag + "] doing: " + action);
        boolean result = false;
        if (action.equals(of) & ag.equals("robot")) { // of = open(fridge)
            result = model.openFridge();
			
        } else if (action.equals(cf) & ag.equals("robot")) { // cf = close(fridge)
            result = model.closeFridge();
			
        } else if (action.getFunctor().equals("move_towards")
                & (ag.equals("robot") || ag.equals("basurero") || ag.equals("owner") || ag.equals("repartidor"))) {
					
            String movimiento = action.getTerm(0).toString();
			int agent = 0;
			
            if (ag.equals("robot")) {
                agent = 0;
            } else if (ag.equals("basurero")) {
                agent = 2;
            } else if (ag.equals("owner")) {
                agent = 3;
            } else if (ag.equals("repartidor")) {
                agent = 4;
            }
			
            try {
                result = model.moveTowards(movimiento, agent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (action.equals(gb) & ag.equals("robot")) {
            result = model.getBeer();

        } else if (action.equals(gp) & ag.equals("robot")) {
            result = model.getPincho();

        } else if (action.equals(hb) & ag.equals("robot")) {
            result = model.handInBeer();

        } else if (action.equals(sb) & ag.equals("owner")) {
            result = model.sipBeer();
			
		} else if (action.equals(rll) & ag.equals("repartidor")) {
            result = model.repartidorLlega();
			
		} else if (action.equals(rsv) & ag.equals("repartidor")) {
            result = model.repartidorSeVa();

        } else if (action.getFunctor().equals("deliverBeer") & (ag.equals("supermarket") || (ag.equals("robot"))
                || (ag.equals("gadis")) || (ag.equals("mercadona")) || ag.equals("repartidor"))) {
            // wait 3 seconds to finish "deliver"
            try {
                Thread.sleep(1500);
                result = model.addBeer((int) (Integer.parseInt(action.getTerm(2).toString()))); // action.getTerm(2)).solve()
            } catch (Exception e) {
                logger.info("Failed to execute action deliverBeer!" + e);
            }
        
        } else if (action.getFunctor().equals("vaciar_papelera") & ag.equals("incinerador")) {
            // wait 1 second to throw the rubbish
            try {
				model.quemandoBasura = true;
                //Thread.sleep(3000);
                result = model.vaciar_papelera();
            } catch (Exception e) {
                logger.info("Failed to execute action tirarLata! " + e);
            }

        } else if (action.getFunctor().equals("siguienteAperitivo") & ag.equals("robot")) {
            result = model.siguienteAperitivo();

        } else if (action.getFunctor().equals("prepararPincho") & ag.equals("robot")) {
            String pincho = action.getTerm(0).toString();
            result = model.prepararPincho(pincho);

        } else if (action.getFunctor().equals("anadirPlatosAlacena") & ag.equals("robot")) {
            int platos = Integer.parseInt(action.getTerm(0).toString());
            result = model.anadirPlatosAlacena(platos);
        
        } else if (action.getFunctor().equals("quitarPlatosAlacena") & ag.equals("robot")) {
            int platos = Integer.parseInt(action.getTerm(0).toString());
            result = model.quitarPlatosAlacena(platos);
        
        } else if (action.getFunctor().equals("anadirPlatosLavavajillas") & ag.equals("robot")) {
            int platos = Integer.parseInt(action.getTerm(0).toString());
            result = model.anadirPlatosLavavajillas(platos);

        } else if (action.getFunctor().equals("vaciar_lavavajillas") & ag.equals("robot")) {
            result = model.vaciar_lavavajillas();

        } else if (action.equals(rc) & (ag.equals("robot") || ag.equals("basurero") || ag.equals("owner"))) {
            result = model.tirarLata();

        } else if (action.equals(tbc) & ag.equals("owner")) {
            result = model.throwBeerCan();

        } else {
            logger.info("Failed to execute action " + action);
        }

        if (result) {
            updatePercepts();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        return result;
    }
}
