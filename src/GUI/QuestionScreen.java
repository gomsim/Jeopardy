package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Represents the screen shown upon opening a question card. Immediately upon
 * opening it starts a countdown, visualised in the upper right hand corner of
 * the screen.
 * When less than 5 secons are left of the countdown the screen gets gradually
 * darker.
 */

public class QuestionScreen extends JPanel {

    private MainScreen mainScreen;

    private static final int MARGIN = 100;
    private static final int FRAME_WIDTH = 15;
    private static final int CORNER_SIZE = 250;
    private static final int COIN_SIZE = CORNER_SIZE/2;

    private int backOpacity;
    private double cardHeight;
    private double coinAnimationCycle;
    private int countdown;
    private boolean countingDown = true;
    private boolean firstTime = true;
    private Thread countdownThread;

    QuestionScreen(String question, int width, int height, MainScreen mainScreen){
        this.mainScreen = mainScreen;

        setUpView(width, height);
        setInvisibleCursor();

        addFormattedQuestionText(question);

        startOpeningAnimation();
    }

    /* Methods and helper methods related to construction */

    private void setUpView(int width, int height){
        addKeyListener(new KeyPadListener());
        setFocusable(true);
        setLayout(null);
        setBounds(0,0,width,height);
        setBackground(new Color(0,0,0,0));
        requestFocus();
        addMouseListener(new MouseCatcher());
    }

    private void setInvisibleCursor(){
        Toolkit toolkit = getToolkit();
        Cursor transparent = toolkit.createCustomCursor(toolkit.getImage(""), new Point(), "trans");
        setCursor(transparent);
    }

    /**
     * Makes the question text appear with the right width and number of lines
     * as required by the size of the question card in the center of the screen.
     */
    private void addFormattedQuestionText(String question){
        Font font = new Font("Areal", Font.ITALIC + Font.BOLD,50);
        FontMetrics metrics = getFontMetrics(font);
        int numLines = (metrics.stringWidth(question)+100)/(getWidth()-MARGIN*4) + 1;

        double y = (numLines - 1) * -0.5;

        ArrayList<String> lines = split(question,getWidth()-MARGIN*4,metrics);
        
        for (int i = 0; i < numLines; i++){
            JLabel label = new JLabel(lines.get(i));
            label.setFont(font);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setBounds(MARGIN*2,(int)(metrics.getHeight()*y) + i*metrics.getHeight() ,getWidth()-MARGIN*4,getHeight());
            label.setForeground(MainScreen.TEXT_COLOR);
            add(label);
        }
    }

    /**
     * Helps the addFormattedQuestionText() method by formatting the question text.
     * @param string to be formatted
     * @param width of the card for the question to be rendered on top of
     * @param metrics to measure the width and height of the font being used
     * @return the question text in an array if lines to be printed on screen
     */
    private ArrayList<String> split(String string, int width, FontMetrics metrics){
        return splitHelper(new ArrayList<>(),string,width,metrics);
    }
    private ArrayList<String> splitHelper(ArrayList<String> accumulator, String remainder, int width, FontMetrics metrics){
        //TODO: If single words in a question are longer than the width, then the function throws a stackOverflowException
        if (metrics.stringWidth(remainder) + 100 < width){
            accumulator.add(remainder);
            return accumulator;
        }
        int i = 0;
        while (metrics.stringWidth(remainder.substring(0,i++)) + 100 < width){}
        while (remainder.charAt(i--) != ' '){}
        accumulator.add(remainder.substring(0,++i));
        return splitHelper(accumulator,remainder.substring(i),width,metrics);
    }

    /* Methods to render the screen's various elements */

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);

        Graphics2D g2d = (Graphics2D)graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        renderBackground(graphics);
        renderCard(graphics);
        renderCoin(graphics);
    }

    private void renderBackground(Graphics graphics){
        graphics.setColor(new Color(0,0,0,backOpacity));
        graphics.fillRect(0,0,getWidth(),getHeight());
    }
    private void renderCard(Graphics graphics){
        graphics.setColor(MainScreen.FRAME_COLOR);
        graphics.fillRoundRect(MARGIN,(int)(getHeight()/2-cardHeight/2),getWidth()-MARGIN*2,(int)cardHeight,CORNER_SIZE,CORNER_SIZE);

        graphics.setColor(MainScreen.FILLER_COLOR);
        graphics.fillRoundRect(MARGIN+FRAME_WIDTH,(int)(getHeight()/2-cardHeight/2+FRAME_WIDTH),getWidth()-MARGIN*2-FRAME_WIDTH*2,(int)cardHeight-FRAME_WIDTH*2,CORNER_SIZE-FRAME_WIDTH,CORNER_SIZE-FRAME_WIDTH);
    }
    private void renderCoin(Graphics graphics){
        //Setting up correct shade of coin to emphasise 3D rotation
        float shade = (float)coinAnimationCycle;
        if (shade > 1)
            shade = 1;
        else if (shade < 0)
            shade = 0;
        shade = shade/2+0.5f;

        //Setting coin colour depending on time left
        if (countdown < 5){
            graphics.setColor(new Color(shade,0,0,1));
        }else{
            graphics.setColor(new Color((int)(MainScreen.FILLER_COLOR.getRed()*shade),(int)(MainScreen.FILLER_COLOR.getGreen()*shade),(int)(MainScreen.FILLER_COLOR.getBlue()*shade)));
        }

        //render coin
        graphics.fillOval(getWidth()-FRAME_WIDTH*2-COIN_SIZE/2-(int)(COIN_SIZE* coinAnimationCycle)/2,FRAME_WIDTH,(int)(COIN_SIZE* coinAnimationCycle),COIN_SIZE);

        //Transform (squish, to simulate 3D rotation) digit-text , then render on coin
        graphics.setColor(new Color((int)(MainScreen.TEXT_COLOR.getRed()*shade),(int)(MainScreen.TEXT_COLOR.getGreen()*shade),(int)(MainScreen.TEXT_COLOR.getBlue()*shade)));
        Font digitFont = new Font("Areal", Font.BOLD + Font.ITALIC,50);
        int fontWidth = getFontMetrics(digitFont).getWidths()[1];
        int digitY = (COIN_SIZE/4)*3;
        int digitXPos;
        double translateX;
        AffineTransform transform = new AffineTransform();
        if (countdown >= 9){ //double digit
            digitXPos = getWidth()-COIN_SIZE;
            translateX = -fontWidth*coinAnimationCycle + fontWidth;
        }else{ //single digit
            digitXPos = getWidth()-COIN_SIZE + 15;
            translateX = -((double) fontWidth/2)*coinAnimationCycle + 20/*Actually half a char width of font, but this looks better*/;
        }
        transform.translate(translateX,0);
        transform.scale(coinAnimationCycle,1);
        graphics.setFont(digitFont.deriveFont(transform));
        graphics.drawString(countdown + 1 + "",digitXPos,digitY);
    }

    /* Methods controlling the animation flow of various graphicsl components */

    private void startOpeningAnimation(){
        new Timer(8, new ActionListener() {
            double cardDist;

            @Override
            public void actionPerformed(ActionEvent event) {

                cardDist = (getHeight()-MARGIN*2) - cardHeight;
                cardHeight += cardDist / 4;

                backOpacity += 10;

                if (Math.abs(cardDist) <= 5){
                    cut(event);
                }
            }
            private void cut(ActionEvent event){
                ((Timer)event.getSource()).stop();
                requestFocus();
                firstTime = false;
            }
        }).start();
    }

    void animateCountdown(int sec){
        if (!firstTime)
            backOpacity = 180;
        countdownThread = new Thread(() -> {
            countingDown = true;
            coinAnimationCycle = 0;
            int timeLeftMillis = sec * 1000;
            int delay = 10;

            while ((timeLeftMillis -= delay) > 0){
                double coinDist = (double)((timeLeftMillis % 1000) - 500) / 12000;
                coinAnimationCycle += coinDist;
                countdown = timeLeftMillis/1000;

                if (timeLeftMillis % 1000 == 0){
                    coinAnimationCycle = 0;
                    if (timeLeftMillis < 6000){
                        backOpacity += 10;
                    }
                }

                try{
                    Thread.sleep(delay-1);
                }catch(InterruptedException e){
                    System.out.println("CoinTimer interrupted by game host, the person");
                    countingDown = false;
                    return;
                }
            }
            countingDown = false;
        });
        countdownThread.start();
    }

    void stopCountdown(){
        countdownThread.interrupt();
        countingDown = false;
    }

    private class KeyPadListener extends KeyAdapter{
        public void keyPressed(KeyEvent event){
            if (event.getKeyCode() == KeyEvent.VK_ENTER){
                System.out.println("ENTER");
                mainScreen.questionClosed();
            }else if (event.getKeyCode() == KeyEvent.VK_SPACE){
                System.out.println("SPACE");
                if (countingDown){
                    mainScreen.countdownPaused();
                }else{
                    stopCountdown();
                    mainScreen.secondTimerRequested();
                }
            }
        }
    }
    private static class MouseCatcher extends MouseAdapter{}
}
