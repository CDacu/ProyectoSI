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

    // robot literals
    public static final Literal af = Literal.parseLiteral("at(robot,fridge)");
    public static final Literal ao = Literal.parseLiteral("at(robot,owner)");
    public static final Literal ad = Literal.parseLiteral("at(robot,delivery)");
    public static final Literal ab = Literal.parseLiteral("at(robot,base)");
    public static final Literal abin = Literal.parseLiteral("at(robot,bin)");
    public static final Literal abc = Literal.parseLiteral("at(robot,beercan)");
	public static final Literal alv = Literal.parseLiteral("at(robot,lavavajillas)");
	public static final Literal alc = Literal.parseLiteral("at(robot,alacena)");
	public static final Literal clf = Literal.parseLiteral("lavavajillasLleno");

    // repartidor literals
    public static final Literal raf = Literal.parseLiteral("at(repartidor,fridge)");
    public static final Literal rad = Literal.parseLiteral("at(repartidor,delivery)");
    public static final Literal rab = Literal.parseLiteral("at(repartidor,repartidorBase)");
	public static final Literal rll = Literal.parseLiteral("repartidorLlega");
	public static final Literal rsv = Literal.parseLiteral("repartidorSeVa");

    // basurero literals
    public static final Literal babin = Literal.parseLiteral("at(basurero,bin)");
    public static final Literal babc = Literal.parseLiteral("at(basurero,beercan)");
    public static final Literal bab = Literal.parseLiteral("at(basurero,basureroBase)");

    // incierador literals
    public static final Literal ibf = Literal.parseLiteral("papeleraLlena");

    // owner literals
    public static final Literal oabin = Literal.parseLiteral("at(owner,bin)");
    public static final Literal oabc = Literal.parseLiteral("at(owner,beercan)");
    public static final Literal oaoc = Literal.parseLiteral("at(owner,ownerChair)");

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
        // clear the percepts of the agents
        clearPercepts("robot");
        clearPercepts("owner");
        clearPercepts("basurero");
        clearPercepts("incinerador");
        clearPercepts("repartidor");

        // get the robot location
        Location lRobot = model.getAgPos(0);

        // add agent location to its percepts
        // ROBOT percepts
        if (model.atFridge) {
            addPercept("robot", af);
        }
        if (model.atOwner) {
            addPercept("robot", ao);
        }

        if (model.atDelivery) {
            addPercept("robot", ad);
        }

        if (model.atBase) {
            addPercept("robot", ab);
        }

        if (model.robotAtBin) {
            addPercept("robot", abin);
        }

        if (model.robotAtBeerCan) {
            addPercept("robot", abc);
        }

		if (model.atLavavajillas) {
            addPercept("robot", alv);
        }
		
		if (model.atLavavajillas) {
            addPercept("robot", alc);
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

        // REPARTIDOR percepts

        if (model.repartidorAtFridge) {
            addPercept("repartidor", raf);
        }

        if (model.repartidorAtDelivery) {
            addPercept("repartidor", rad);
        }

        if (model.repartidorAtBase) {
            addPercept("repartidor", rab);
        }

        // BASURERO percetps
        if (model.basureroAtBin) {
            addPercept("basurero", babin);
        }

        if (model.basureroAtBeerCan) {
            addPercept("basurero", babc);
            model.beerCanShow = false;
        }

        if (model.basureroAtBase) {
            addPercept("basurero", bab);
        }

        //INCINERADOR Percepts

        if (model.cansInBin >= 5) {
            addPercept("incinerador", ibf);
        }

        // OWNER Percepts
        if (model.ownerAtBin) {
            addPercept("owner", oabin);
        }

        if (model.ownerAtBeerCan) {
            addPercept("owner", oabc);
            model.beerCanShow = false;
        }

        if (model.ownerAtOwnerChair) {
            addPercept("owner", oaoc);
        }

        if (model.platosEnLavavajillas >= 5) {
            addPercept("robot", clf);
        }

        // add beer "status" the percepts
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
            String l = action.getTerm(0).toString();
            Location dest = null;
            int agent = 0;
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
                result = model.moveTowards(dest, agent);
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
