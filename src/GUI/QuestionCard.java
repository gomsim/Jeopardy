package GUI;

import javax.swing.*;
import java.awt.*;

public class QuestionCard extends JPanel {

    private static final int MARGIN = 5;
    private static final int FRAME_WIDTH = 5;

    private boolean turned;
    private boolean hovered;
    /**
     * x and y positions as represented on the board grid in MainScreen.
     */
    private int x, y;


    QuestionCard(int x, int y){
        this.x = x;
        this.y = y;

        setBackground(new Color(0,0,0,0));
    }

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);

        int cardHeight = getHeight()-MARGIN*2;

        Graphics2D g2d = (Graphics2D)graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        renderCard(graphics, cardHeight);
        renderDigit(graphics);
        renderSheen(graphics);
        renderHighlight(graphics, cardHeight);
    }

    /* Private methods related to rendering the card */

    private void renderCard(Graphics graphics, int cardHeight){
        graphics.setColor(MainScreen.FRAME_COLOR);
        graphics.fillRoundRect(
                MARGIN,MARGIN,
                getWidth() - MARGIN*2,
                cardHeight,
                cardHeight,
                cardHeight);

        graphics.setColor(MainScreen.FILLER_COLOR);
        graphics.fillRoundRect(
                MARGIN+FRAME_WIDTH,
                MARGIN+FRAME_WIDTH,
                getWidth() - (MARGIN+FRAME_WIDTH)*2,
                getHeight() - (MARGIN+FRAME_WIDTH)*2,
                cardHeight-FRAME_WIDTH,
                cardHeight-FRAME_WIDTH);
    }

    private void renderDigit(Graphics graphics){
        graphics.setFont(calculateFont());
        int fontHeight = graphics.getFontMetrics(graphics.getFont()).getHeight();
        int fontWidth = graphics.getFontMetrics(graphics.getFont()).getWidths()[1];

        graphics.setColor(MainScreen.TEXT_COLOR);
        graphics.drawString((y + 1) + "",getWidth()/2-(int)(fontWidth/2.4)-(y>=9?(int)(fontWidth/2.4):0),getHeight()/2+fontHeight/4);
    }

    /**
     * @return the right font size to be used on the QuestionCard.
     */
    private Font calculateFont(){
        Font font = new Font("Areal", Font.ITALIC + Font.BOLD,200);
        while(getFontMetrics(font).getHeight() > getHeight() - MARGIN*2 - FRAME_WIDTH*2){
            font = new Font("Areal", Font.ITALIC + Font.BOLD, font.getSize() - 1);
        }

        return font;
    }

    /**
     * Renders the diagonal fields upon the Question card, representing sheen.
     */
    private void renderSheen(Graphics graphics){
        int[] xPointsHovered = new int[] {(int)((getWidth()/3)*1.8),(int)((getWidth()/3)*2.3),(int)((getWidth()/3)*1.8),(int)((getWidth()/3)*1.3)};
        int[] xPointsNormal = new int[] {(int)((getWidth()/3)*2),(int)((getWidth()/3)*2.5),(getWidth()/3)*2,(int)((getWidth()/3)*1.5)};
        int[] xPointsSidestripe = new int[] {(int)((getWidth()/10)*3),(int)((getWidth()/10)*3.3),(int)((getWidth()/10)*1.8),(int)((getWidth()/10)*1.5)};
        int[] yPoints = new int[] {MARGIN+FRAME_WIDTH,MARGIN+FRAME_WIDTH,getHeight()-(MARGIN+FRAME_WIDTH),getHeight()-(MARGIN+FRAME_WIDTH)};

        graphics.setColor(new Color(207,189,126,50));
        if (!hovered){
            graphics.fillPolygon(xPointsNormal, yPoints, yPoints.length);
            graphics.fillPolygon(xPointsSidestripe, yPoints, yPoints.length);
        }else{
            graphics.fillPolygon(xPointsHovered,yPoints,yPoints.length);
        }
    }

    /**
     * Renders a dark or a bright highlight, depending on whether the QuestionCard
     * is hovered over or already used.
     */
    private void renderHighlight(Graphics graphics, int cardHeight){
        if (turned){
            graphics.setColor(new Color(0,0,0,100));
            graphics.fillRoundRect(MARGIN,MARGIN,getWidth() - MARGIN*2,cardHeight,cardHeight,cardHeight);
        }else if (hovered){
            graphics.setColor(new Color(255,255,255,50));
            graphics.fillRoundRect(MARGIN,MARGIN,getWidth() - MARGIN*2,cardHeight,cardHeight,cardHeight);
        }
    }

    /* Package private methods called from the MainScreen (mainScreen) class */

    int getPosX(){return x;}
    int getPosY(){return y;}
    void setTurned(boolean flag){
        turned = flag;
    }
    boolean isTurned(){
        return turned;
    }
    void setHovered(boolean flag){
        hovered = flag;
    }
}
