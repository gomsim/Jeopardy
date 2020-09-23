package GUI;

import Logik.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Class responsible for showing the game screen on as few as 1 device to as
 * many as infinitely many devices. One instance per device.
 * Performance is not guaranteed.
 *
 * It shows category titles on a grid, followed horizontally by their
 * corresponding question cards.
 */

public class MainScreen extends JFrame {

    static final Color FRAME_COLOR = new Color(50,151,168);
    static final Color FILLER_COLOR = new Color(247,229,156);
    static final Color TEXT_COLOR = new Color(245,194,66);
    private static final Color NIGHT_SKY_COLOR = new Color(2,82,122);
    private static final int TITLE_SPACE = 1;

    private Program program;
    private String[] categories;
    private QuestionCard cardLastShown;
    private QuestionScreen questionScreenBeingShown;

    private QuestionCard[][] questionCards;

    public MainScreen(Program program, String[] categories, int[] categorySizes, int maxCategorySize, GraphicsDevice device){
        super(device.getDefaultConfiguration());
        this.program = program;
        this.categories = categories;
        this.questionCards = new QuestionCard[categories.length][maxCategorySize];

        setUpWindow();

        //Contents:
        BackPanel backPanel = new BackPanel(maxCategorySize + TITLE_SPACE,categories.length);
        add(backPanel);
        setCategoryTitles(categories, backPanel);
        populateBoard(categorySizes,maxCategorySize,questionCards,backPanel);

        setVisible(true);
    }

    /* Private methods used during construction */

    private void setUpWindow(){
        setFocusable(true);
        setTitle("JEOPARDY!!!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        requestFocus();
    }
    private void setCategoryTitles(String[] categories, JPanel backPanel){
        for (int i = 0; i < categories.length; i++){
            TitleCard label = new TitleCard(categories[i]);
            backPanel.add(label);
        }
    }
    private void populateBoard(int[] categorySizes, int maxSize, QuestionCard[][] questionCards, JPanel backPanel){
        CardMouseListener listener = new CardMouseListener();

        for (int y = 0; y < maxSize; y++){
            for (int x = 0; x < categorySizes.length; x++){
                if (y < categorySizes[x]){
                    QuestionCard card = new QuestionCard(x,y);
                    card.addMouseListener(listener);
                    backPanel.add(card);
                    questionCards[x][y] = card;
                }else{ // Adds empty filler objects to even out the grid for categories with fewer questions than others.
                    backPanel.add(new FillerCard());
                }
            }
        }
    }

    /* Public methods called from the Program (controller) class */

    /**
     * Shows a QuestionScreen as an overlay atop this MainScreen.
     */
    public void showQuestionScreen(String question, String category, int y){
        cardLastShown = questionCards[getCategoryPos(category)][y];

        GlassScreen glassScreen = new GlassScreen();
        SwingUtilities.getRootPane(this).setGlassPane(glassScreen);
        QuestionScreen questionScreen = new QuestionScreen(question, getWidth(), getHeight(),this);
        glassScreen.setVisible(true);
        glassScreen.add(questionScreen);
        questionScreenBeingShown = questionScreen;
    }
    public void closeQuestionScreen(){
        questionScreenBeingShown.stopCountdown();
        cardLastShown.setTurned(true);
        SwingUtilities.getRootPane(this).getGlassPane().setVisible(false);
    }
    public void showCountdown(int sec){
        questionScreenBeingShown.animateCountdown(sec);
    }
    public void pauseCountdown(){
        questionScreenBeingShown.stopCountdown();
    }

    /* Package private methods called from the QuestionScreen */

    void questionClosed(){
        program.closeCard();
    }
    void secondTimerRequested(){
        program.startSecondCountdown();
    }
    void countdownPaused(){
        program.pauseCountdown();
    }

    /* Private methods and classes mostly used for:
    * 1. Rendering the background
    * 2. Opening questions onto a QuestionScreen
    * 3. Listening for mouse input
    * 4. Helper methods for coordinating the question cards
    */

    /**
     * Getting the horizontal position of a category on the board grid.
     */
    private int getCategoryPos(String category){
        int result = 0;
        for (int i = 0; i < categories.length; i++){
            if (category.equals(categories[i]))
                result = i;
        }
        return result;
    }

    /**
     * Filler objects used to fill out the board grid in case any categories of
     * questions don't have as many questions as the others. This makes it possible,
     * however unnecessary, to have categories of different sizes.
     */
    private static class FillerCard extends JPanel{
        private FillerCard(){
            setBackground(new Color(0,0,0,0));
        }
    }

    /**
     * Functions as label on which to show category titles. The reason for not
     * just using JLabels is that tha text has to be transformed (squished)
     * before rendering in case it doesn't fit on the panel.
     */
    private static class TitleCard extends JPanel{
        private String text;
        private boolean firstTime = true;
        private int x, y;
        private static final int MARGIN = 25;

        TitleCard(String text){
            this.text = text;
            setBackground(new Color(0,0,0,0));
            setFont(new Font("Areal", Font.BOLD + Font.ITALIC,50));
        }

        protected void paintComponent(Graphics graphics){
            graphics.setColor(TEXT_COLOR);
            if (firstTime){
                FontMetrics metrics = getFontMetrics(getFont());
                int stringWidth = metrics.stringWidth(text);
                y = getHeight()/2 + metrics.getHeight()/4;
                x = getWidth()/2 - stringWidth/2;
                if (stringWidth > getWidth() + MARGIN * 2){
                    AffineTransform transform = new AffineTransform();
                    transform.scale((double)(getWidth()-MARGIN*2)/stringWidth,1);
                    setFont(getFont().deriveFont(transform));
                    x = MARGIN;
                }
                firstTime = false;
            }
            graphics.drawString(text,x,y);
        }
    }
    private class CardMouseListener implements MouseListener{
        private Object pressed;

        public void mousePressed(MouseEvent event){
            pressed = event.getSource();
        }
        public void mouseReleased(MouseEvent event){
            if (event.getSource().equals(pressed)){
                QuestionCard card = (QuestionCard)pressed;
                if (!card.isTurned())
                    program.showCard(categories[card.getPosX()],card.getPosY());
            }
        }
        public void mouseEntered(MouseEvent event){
            QuestionCard card = (QuestionCard)event.getSource();
            if (!card.isTurned()){
                card.setHovered(true);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }
        public void mouseExited(MouseEvent event){
            QuestionCard card = (QuestionCard)event.getSource();
            card.setHovered(false);
            setCursor(Cursor.getDefaultCursor());
        }
        public void mouseClicked(MouseEvent event)/* Do nothing */ {}
    }

    /**
     * Used to overlay the QuestionScreen atop of this MainScreen.
     */
    private static class GlassScreen extends JPanel{
        GlassScreen(){
            setLayout(null);
            setOpaque(false);
        }
    }

    /**
     * Big class. Should probably be in a separate file. In any case, it's
     * responsible for rendering the background. It contains rendering and updating
     * of the stars in the background as well as the occasional fallen star.
     * It's all mumbo jumbo, don't read it.
     */
    private static class BackPanel extends JPanel{

        private Random rand = new Random();
        private int maxStarSize = 3;
        private double[] starSizes = new double[100];
        private Star[] stars = new Star[1000];
        private StarFall starFall;
        private double starFallFrequency = 0.001;

        private Timer starTimer;

        BackPanel(int maxCategorySize, int numCategories){
            setLayout(new GridLayout(maxCategorySize,numCategories));
            setBackground(NIGHT_SKY_COLOR);

            for (int i = 0; i < starSizes.length; i++)
                starSizes[i] = rand.nextDouble()*maxStarSize;

            for (int i = 0; i < stars.length; i++){
                int x = rand.nextInt(Toolkit.getDefaultToolkit().getScreenSize().width);
                int y = rand.nextInt(Toolkit.getDefaultToolkit().getScreenSize().height);
                int size = rand.nextInt(starSizes.length);

                stars[i] = new Star(x,y,size);
            }

            starTimer = new Timer(5, new ActionListener() {
                int counter;
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (counter++ % 10 == 0)
                        updateStars();
                    if (starFall == null && rand.nextDouble() < starFallFrequency) //will be lowered
                        starFall = new StarFall(rand.nextInt(Toolkit.getDefaultToolkit().getScreenSize().width));
                    repaint();
                }
            });
            starTimer.start();
        }

        protected void paintComponent(Graphics graphics){
            super.paintComponent(graphics);

            Graphics2D g2d = (Graphics2D)graphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(Color.WHITE);

            for (Star star: stars)
                star.paint(graphics);

            if (starFall != null)
                starFall.updateAndPaint(graphics);
        }
        private void updateStars(){
            for (int i = 0; i < starSizes.length; i++)
                starSizes[i] += rand.nextDouble()/3;
        }
        private class Star{
            private int x, y, source;

            Star(int x, int y, int source){
                this.x = x;
                this.y = y;
                this.source = source;
            }

            void paint(Graphics graphics){
                int size = (int)Math.abs(starSizes[source]%(maxStarSize*2)-maxStarSize);
                graphics.fillOval(x,y,size,size);
                if (maxStarSize - size < 1.1 && rand.nextDouble() < 0.01){
                    graphics.drawLine(x,y-2,x,y+2);
                    graphics.drawLine(x-2,y,x+2,y);
                }
            }
        }
        private class StarFall {
            int x;
            int y = -10;
            int velX = -50;
            int velY = 50;
            int size = 1;

            StarFall(int startX){
                x = startX;
            }
            void updateAndPaint(Graphics graphics){
                x += velX;
                y += velY;
                graphics.fillOval(x,y,size,size);
                graphics.drawLine(x,y,x-velX,y-velY);
                if (y - velY > Toolkit.getDefaultToolkit().getScreenSize().height)
                    starFall = null;
            }
        }
    }
}
