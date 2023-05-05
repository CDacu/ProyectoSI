import jason.environment.grid.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/** class that implements the View of Domestic Robot application */
public class HouseView extends GridWorldView {

    HouseModel hmodel;

    public HouseView(HouseModel model) {
        super(model, "Domestic Robot", 700);
        hmodel = model;
        defaultFont = new Font("Arial", Font.BOLD, 11); // change default font
        setVisible(true);
        // repaint(); Necesario quitarlo para que no parpadee
    }

    /** draw application objects */
    @Override
    public void draw(Graphics g, int x, int y, int object) {
        Location lRobot = hmodel.getAgPos(0);
        Location lOwner = hmodel.getAgPos(3);
        super.drawAgent(g, x, y, Color.lightGray, -1);
        String o;
        switch (object) {
            case HouseModel.FRIDGE:
                super.drawAgent(g, x, y, Color.white, -1);
                if (lRobot.equals(hmodel.lFridge)) {
                    super.drawAgent(g, x, y, Color.yellow, -1);
                }
                g.setColor(Color.black);
				
				if(hmodel.pinchosTortilla > 0){
                    o = "F: B(" + hmodel.availableBeers + ") T(" + hmodel.pinchosTortilla + ")";
                } else if (hmodel.pinchosEmpanada > 0){
                    o = "F: B(" + hmodel.availableBeers + ") E(" + hmodel.pinchosEmpanada + ")";
                } else if (hmodel.pinchosBocata > 0){
                    o = "F: B(" + hmodel.availableBeers + ") J(" + hmodel.pinchosBocata + ")";
                } else {
                    o = "F: B(" + hmodel.availableBeers + ")";
                }
                drawString(g, x, y, defaultFont, o);
                break;
            case HouseModel.OWNERCHAIR:
                o = "OwnerChair";
                if (lOwner.equals(hmodel.lOwnerChair)) {
                    super.drawAgent(g, x, y, Color.pink, -1);
                    o = "Owner";
                    if (hmodel.sipCount > 0) {
                        o += " (" + hmodel.sipCount + ")";
                    }
                } else if (lRobot.equals(hmodel.lOwnerChair)) {
                    super.drawAgent(g, x, y, Color.yellow, -1);
                }
                g.setColor(Color.black);
                drawString(g, x, y, defaultFont, o);
                break;
            case HouseModel.DELIVERY:
                super.drawAgent(g, x, y, Color.green, -1);
                if (lRobot.equals(hmodel.lDelivery)) {
                    super.drawAgent(g, x, y, Color.yellow, -1);
                }
                g.setColor(Color.black);
                drawString(g, x, y, defaultFont, "Delivery");
                break;
            case HouseModel.BIN:
                if (lRobot.equals(hmodel.lBin)) {
                    super.drawAgent(g, x, y, Color.yellow, -1);
                }
                g.setColor(Color.black);
                drawString(g, x, y, defaultFont, "Bin(" + hmodel.cansInBin + "/5)");
                break;
            case HouseModel.LAVAVAJILLAS:
                super.drawAgent(g, x, y, Color.white, -1);
                g.setColor(Color.black);
                drawString(g, x, y, defaultFont, "PS(" + hmodel.platosEnLavavajillas + "/5)");
                break;
            case HouseModel.ALACENA:
                o = "PL(" + hmodel.platosEnAlacena + "/15)";
                g.setColor(Color.black);
                drawString(g, x, y, defaultFont, o);
                break;
            case HouseModel.OBSTACLE: 
                super.drawAgent(g, x, y, Color.DARK_GRAY, -1);
                g.setColor(Color.black);
                drawString(g, x, y, defaultFont, "Obstacle");
                break;
        }
        // repaint(); Necesario quitarlo para que no parpadee
    }

    /*
     * El drawagent con los agentes movibles ocurre cuando sus posiciones son
     * actualizadas, por esto es que si el
     * robot pasa por encima de la lata esta desaparecerá del grid hasta que se
     * vuelva a actualizar su posición.
     */
    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        String o;
        switch (id) {
            case 0:
                Location lRobot = hmodel.getAgPos(0);
                if (!lRobot.equals(hmodel.lOwnerChair) && !lRobot.equals(hmodel.lFridge)
                        && !lRobot.equals(hmodel.getAgPos(3))) {
                    c = Color.yellow;
                    if (hmodel.carryingBeer)
                        c = Color.orange;
                    super.drawAgent(g, x, y, c, -1);
                    g.setColor(Color.black);
                    super.drawString(g, x, y, defaultFont, "Robot");
                }
                break;
            case 1:
                Location lCan = hmodel.getAgPos(1);
                if (!lCan.equals(hmodel.lOwnerChair) && !lCan.equals(hmodel.lFridge)) {
                    c = Color.green;
                    super.drawAgent(g, x, y, c, -1);
                    g.setColor(Color.black);
                    super.drawString(g, x, y, defaultFont, "Can");
                }
                break;
            case 2:
                Location lBasurero = hmodel.getAgPos(2);
                c = Color.blue;
                super.drawAgent(g, x, y, c, -1);
                g.setColor(Color.black);
                super.drawString(g, x, y, defaultFont, "Basurero");
                break;
            case 3:
                Location lOwner = hmodel.getAgPos(3);
                c = Color.pink;
                super.drawAgent(g, x, y, c, -1);
                g.setColor(Color.black);
                o = "Owner";
                if (hmodel.sipCount > 0) {
                    o += " (" + hmodel.sipCount + ")";
                }
                super.drawString(g, x, y, defaultFont, o);
                break;
            case 5:
                Location lIncinerador = hmodel.getAgPos(5);
				if(hmodel.quemandoBasura){
					c = Color.red;
				}else{
					c = new Color(0xC0DBEA);
				}
                super.drawAgent(g, x, y, c, -1);
                g.setColor(Color.black);
                o = "Incinerador";
                super.drawString(g, x, y, defaultFont, o);
                break;
            case 6:
                Location lRepartidor = hmodel.getAgPos(6);
				if(hmodel.repartiendo){
                c = new Color(0xB5F1CC);
                super.drawAgent(g, x, y, c, -1);
                g.setColor(Color.black);
                o = "Repartidor";
                super.drawString(g, x, y, defaultFont, o);
				}
                break;
        }
    }

    @Override
    public void drawEmpty(Graphics g, int x, int y) {
        g.setColor(new Color(0xEEEEEE));
        g.fillRect(x * cellSizeW + 1, y * cellSizeH + 1, cellSizeW - 1, cellSizeH - 1);
        g.setColor(Color.lightGray);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
    }
}
