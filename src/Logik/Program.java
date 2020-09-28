package Logik;

import GUI.AnimationHandler;
import GUI.MainScreen;

import java.awt.*;

public class Program {

    private int questionTime;
    private int extraTime;

    private QCardsDB database = new QCardsDB();
    private MainScreen[] graphicalInterfaces;

    private AnimationHandler animationHandler;

    public Program(){
        ConfigFileHandler config = new ConfigFileHandler();
        questionTime = config.questionTime;
        extraTime = config.extraTime;

        setupDB(new GameFileHandler().getFileStream());
        setupGuis();
        animationHandler = new AnimationHandler(graphicalInterfaces);
        for (int i = 0; i < graphicalInterfaces.length; i++){
            animationHandler.bakeOpeningAnimation(i);
        }
    }

    /* Private methods used during set up */

    /**
     * Sets up a GUI for each device (computer screen) connected.
     */
    private void setupGuis(){
        graphicalInterfaces = new MainScreen[gameScreens().length];

        String[] categories = database.getCategories();
        int[] categorySizes = database.getNumQuestions();
        int largestCategorySize = database.getLargestCategorySize();
        for (int i = 0; i < graphicalInterfaces.length; i++)
            graphicalInterfaces[i] = new MainScreen(this, categories, categorySizes, largestCategorySize,gameScreens()[i]);
    }

    /**
     * Parses the string representation of the game to be played and stores each
     * question into its corresponding category to be fetched later.
     * @param iterator supplying the string representation of the game to be played.
     */
    private void setupDB(FileStream iterator){
        String currentCategory = null;
        System.out.println("Questions, if any, not used in the game:");
        while(iterator.hasNext()){
            String current = iterator.next();
            String question = current;
            if (current.contains("#")){
                question = current.split("#")[0];
            }
            if (question.isEmpty()){
                //Doesn't store in database
                System.out.println("\n\tCategory: "+currentCategory+"\n\tQuestion: " + current.split("#")[1]);
            }else if (representsCategory(question)){
                currentCategory = question.substring(1);
            }else{
                try {
                    database.add(currentCategory, question);
                }catch(NullPointerException e){
                    throw new IllegalStateException("Question without category");
                }
            }
        }
        System.out.println("\n" + database);
    }

    /**
     * Detects what devices (computer screens) are connected.
     * @return the devices being connected
     */
    private GraphicsDevice[] gameScreens(){
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return environment.getScreenDevices();
    }

    /**
     * Checks whether a token in the game string representation represents a
     * category (or a question).
     */
    private boolean representsCategory(String string){
        return string.startsWith("*");
    }


    /*Private and public methods relating to controlling the game, called from this class or the MainScreen class*/
    /**
     * The countdown to be used upon showing a question for the first time.
     */
    private void startFirstCountdown(){
        for (MainScreen gui: graphicalInterfaces){
            gui.showCountdown(questionTime);
        }
    }

    /**
     * The countdown to be used upon restarting the countdown for others to try
     * answering the question after one has failed.
     */
    public void startSecondCountdown(){
        for (MainScreen gui: graphicalInterfaces){
            gui.showCountdown(extraTime);
        }
    }
    public void pauseCountdown(){
        for (MainScreen gui: graphicalInterfaces){
            gui.pauseCountdown();
        }
    }
    public void showCard(String category, int question){
        String questionText = database.get(category, question);
        for (MainScreen gui: graphicalInterfaces){
            gui.showQuestionScreen(questionText, category, question);
        }
        animationHandler.playOpeningAnimation();
        //startFirstCountdown();
    }
    public void closeCard(){
        for (MainScreen gui: graphicalInterfaces){
            gui.closeQuestionScreen();
        }
    }
}
