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
    public static final Literal rc = Literal.parseLiteral("recycleCan");
    public static final Literal tbc = Literal.parseLiteral("throwBeerCan");
	
	public static final Literal clf = Literal.parseLiteral("lavavajillasLleno");
	public static final Literal ibf = Literal.parseLiteral("papeleraLlena");
	
	public static final Literal rll = Literal.parseLiteral("repartidorLlega");
	public static final Literal rsv = Literal.parseLiteral("repartidorSeVa");
	
	
	//public static final Literal posFridge = Literal.parseLiteral("position(fridge,0,0)");
	public static final Literal posCloseFridge = Literal.parseLiteral("position(fridge,0,1)");
	public static final Literal posOwnerChair = Literal.parseLiteral("position(ownerchair,10,10)");
	public static final Literal posCloseOwnerChair = Literal.parseLiteral("position(closeownerchair,10,9)");
	public static final Literal posDelivery = Literal.parseLiteral("position(delivery,0,10)");
	//public static final Literal posBin = Literal.parseLiteral("position(bin,10,0)");
	public static final Literal posCloseBin = Literal.parseLiteral("position(bin,10,1)");
	//public static final Literal posLavavajillas = Literal.parseLiteral("position(lavavajillas,2,0)");
	public static final Literal posCloseLavavajillas = Literal.parseLiteral("position(lavavajillas,2,1)");
	//public static final Literal posAlacena = Literal.parseLiteral("position(alacena,1,0)");
	public static final Literal posCloseAlacena = Literal.parseLiteral("position(alacena,1,1)");
	
	
	//LITERAL NO UTILIZADOS---------------------------------------------------------------------
    // robot literals
    public static final Literal af = Literal.parseLiteral("at(robot,fridge)");
    public static final Literal ao = Literal.parseLiteral("at(robot,owner)");
    public static final Literal ad = Literal.parseLiteral("at(robot,delivery)");
    public static final Literal ab = Literal.parseLiteral("at(robot,base)");
    public static final Literal abin = Literal.parseLiteral("at(robot,bin)");
    public static final Literal abc = Literal.parseLiteral("at(robot,beercan)");
	public static final Literal alv = Literal.parseLiteral("at(robot,lavavajillas)");
	public static final Literal alc = Literal.parseLiteral("at(robot,alacena)");

    // repartidor literals
    public static final Literal raf = Literal.parseLiteral("at(repartidor,fridge)");
    public static final Literal rad = Literal.parseLiteral("at(repartidor,delivery)");
    public static final Literal rab = Literal.parseLiteral("at(repartidor,repartidorBase)");

    // basurero literals
    public static final Literal babin = Literal.parseLiteral("at(basurero,bin)");
    public static final Literal babc = Literal.parseLiteral("at(basurero,beercan)");
    public static final Literal bab = Literal.parseLiteral("at(basurero,basureroBase)");

    // incierador literals

    // owner literals
    public static final Literal oabin = Literal.parseLiteral("at(owner,bin)");
    public static final Literal oabc = Literal.parseLiteral("at(owner,beercan)");
    public static final Literal oaoc = Literal.parseLiteral("at(owner,ownerChair)");
	
	//LITERAL NO UTILIZADOS---------------------------------------------------------------------

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
			
			//addPercept(agent, posFridge);
			addPercept(agent, posCloseFridge);
			addPercept(agent, posOwnerChair);
			addPercept(agent, posCloseOwnerChair);
			addPercept(agent, posDelivery);
			//addPercept(agent, posBin);
			addPercept(agent, posCloseBin);
			//addPercept(agent, posLavavajillas);
			addPercept(agent, posCloseLavavajillas);
			//addPercept(agent, posAlacena);
			addPercept(agent, posCloseAlacena);
			
			if(model.beerCanShow){
				addPercept(agent, Literal.parseLiteral("position(beercan," + model.getAgPos(1).x + "," + model.getAgPos(1).y + ")"));	
			}
			
			addPercept(agent, Literal.parseLiteral("position(robot," + model.getAgPos(0).x + "," + model.getAgPos(0).y + ")"));
			addPercept(agent, Literal.parseLiteral("position(basurero," + model.getAgPos(2).x + "," + model.getAgPos(2).y + ")"));
			addPercept(agent, Literal.parseLiteral("position(owner," + model.getAgPos(3).x + "," + model.getAgPos(3).y + ")"));
			addPercept(agent, Literal.parseLiteral("position(incinerador," + model.getAgPos(5).x + "," + model.getAgPos(5).y + ")"));		//No tiene sentido, no se mueve
			addPercept(agent, Literal.parseLiteral("position(repartidor," + model.getAgPos(6).x + "," + model.getAgPos(6).y + ")"));
		}

        if (model.recyclingAgent == 2){
            addPercept("robot", Literal.parseLiteral("recyclingAgent(basurero)"));
        } else if (model.recyclingAgent == 3) {
            addPercept("robot", Literal.parseLiteral("recyclingAgent(owner)"));
        }

        if (model.siguienteAperitivo == 0){
            addPercept("robot", Literal.parseLiteral("siguienteAperitivo(tortilla)"));
        } else if (model.siguienteAperitivo == 1){
            addPercept("robot", Literal.parseLiteral("siguienteAperitivo(empanada)"));
        } else if (model.siguienteAperitivo == 2){
            addPercept("robot", Literal.parseLiteral("siguienteAperitivo(jamon)"));
        }

        if (model.platosEnLavavajillas >= 5) {
            addPercept("robot", clf);
        }

        if (model.availableBeers > 0) {
            addPercept("robot", Literal.parseLiteral("stock(beer," + model.availableBeers + ")"));
        } else {
            addPercept("robot", Literal.parseLiteral("stock(beer,0)"));
        }
        
        if ( model.fridgeOpen && model.availableBeers == 1) {
            addPercept("robot", Literal.parseLiteral("reponerCerveza"));
        }
        
        if (model.pinchosTortilla > 0) {
            addPercept("robot", Literal.parseLiteral("stock(pincho," + model.pinchosTortilla + ")"));
        } else if (model.pinchosEmpanada > 0) {
            addPercept("robot", Literal.parseLiteral("stock(pincho," + model.pinchosEmpanada + ")"));
        } else if (model.pinchosJamon > 0) {
            addPercept("robot", Literal.parseLiteral("stock(pincho," + model.pinchosJamon + ")"));
        } else {
            addPercept("robot", Literal.parseLiteral("stock(pincho,0)"));
        }

        if (model.sipCount > 0) {
            addPercept("robot", hob);
            addPercept("owner", hob);
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
			/*
            Location dest = null;
            
            if (l.equals("fridge")) {
                dest = model.lFridge;
            } else if (l.equals("owner")) {
                dest = model.lOwner;
            } else if (l.equals("delivery")) {
                dest = model.lDelivery;
            } else if (l.equals("base")) {
                dest = model.lRobot;
            } else if (l.equals("bin")) {
                dest = model.lBin;
            } else if (l.equals("beercan")) {
                dest = model.lBeerCan;
            } else if (l.equals("basureroBase")) {
                dest = model.lBasurero;
            } else if (l.equals("ownerChair")) {
                dest = model.lOwnerChair;
            } else if (l.equals("alacena"))  {
                dest = model.closeTolAlacena;
            } else if (l.equals("lavavajillas")) {
                dest = model.lLavavajillas;
            } else if (l.equals("repartidorBase")) {
                dest = model.lRepartidor;
            }

			*/
			
            if (ag.equals("robot")) {
                agent = 0;
            } else if (ag.equals("basurero")) {
                agent = 2;
            } else if (ag.equals("owner")) {
                agent = 3;
            } else if (ag.equals("repartidor")) {
                agent = 6;
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
            result = model.repartidorPop();
			
		} else if (action.equals(rsv) & ag.equals("repartidor")) {
            result = model.repartidorPop();

        } else if (action.getFunctor().equals("deliverBeer") & (ag.equals("supermarket") || (ag.equals("robot"))
                || (ag.equals("gadis")) || (ag.equals("mercadona")) || ag.equals("repartidor"))) {
            // wait 3 seconds to finish "deliver"
            try {
                Thread.sleep(1500);
                result = model.addBeer((int) (Integer.parseInt(action.getTerm(2).toString()))); // action.getTerm(2)).solve()
            } catch (Exception e) {
                logger.info("Failed to execute action deliverBeer!" + e);
            }
        
        } else if (action.getFunctor().equals("empty_bin") & ag.equals("incinerador")) {
            // wait 1 second to throw the rubbish
            try {
                Thread.sleep(1000);
                result = model.empty_bin();
            } catch (Exception e) {
                logger.info("Failed to execute action recycleCan! " + e);
            }

        } else if (action.getFunctor().equals("decideRandomRecycler") & (ag.equals("robot") || ag.equals("owner"))) {
            result = model.decideRandomRecycler();

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
            result = model.recycleCan();

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
