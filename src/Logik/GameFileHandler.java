package Logik;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Class that reads gamefile of the player's choosing and starts that game.
 * Can generate a file 'example.game' from EXAMPLE_GAME.
 */
class GameFileHandler {

    private FileStream fileStream;

    GameFileHandler(){
        ensureGamesFolder();
        String fileName = showGameChoicePane();
        loadGameFile(fileName);
    }
    FileStream getFileStream(){
        return fileStream;
    }
    private void loadGameFile(String fileName){
        try{
            fileStream = new FileStream("games/" + fileName);
        }catch(Exception e){
            createExampleGameFile();
        }
    }
    private String showGameChoicePane(){
        String[] gameNames = gamesAsList();

        Object choice = JOptionPane.showInputDialog(null, "", "Välj spel", JOptionPane.QUESTION_MESSAGE,null,gameNames,gameNames[0]);

        if (choice.toString().equals("example")){
            createExampleGameFile();
        }

        return choice.toString() + ".game";
    }

    /**
     * @return a String array representing the game files currently in the games directory.
     */
    private String[] gamesAsList(){
        File directory = new File("games");
        FilenameFilter filter = (dir,name) -> name.endsWith(".game") && !name.equals("example.game");
        String[] gameFiles = directory.list(filter);

        for (int i = 0; i < gameFiles.length; i++)
            gameFiles[i] = gameFiles[i].replace(".game","");

        gameFiles = Arrays.copyOf(gameFiles, gameFiles.length + 1);
        gameFiles[gameFiles.length - 1] = "example";

        return gameFiles;
    }
    
    private void ensureGamesFolder(){
        File directory = new File("games");
        if (!directory.exists())
            directory.mkdir();
    }

    private void createExampleGameFile(){
        FileWriter writer = null;
        try {
            writer = new FileWriter("games/example.game");
            writer.write(EXAMPLE_GAME.replaceAll("\n",System.lineSeparator()));
            writer.close();
        } catch (IOException er) {
            er.printStackTrace();
        }finally{
            try{
                if (writer != null)
                    writer.close();
            }catch (IOException er){
                er.printStackTrace();
            }
        }
    }

    private static final String EXAMPLE_GAME =
                    "*Explanation;\n" +
                    "#A category declaration is indicated by the preceding *-sign.;\n" +
                    "\n" +
                    "Question 1: Try comparing in-game representation with game file.\n" +
                    "    #Comment. Comments are be preceded by the #-sign. Comments don't show in-game.;\n" +
                    "Question 2: Short and concise questions are the best.\n" +
                    "    #Comments can, for example, contain quriosa or the answer to a question.;\n" +
                    "Question 3: \"You can use quotes\".\n" +
                    "    #Each entry in the file is separated by a semi-colon.;\n" +
                    "Question 4: A \n" +
                    "question \n" +
                    "written \n" +
                    "as \n" +
                    "several \n" +
                    "lines \n" +
                    "still \n" +
                    "interprets \n" +
                    "as \n" +
                    "a \n" +
                    "single \n" +
                    "line.;\n" +
                    "Question 5: A question cannot contain the number/tag sign, because that would make the remaining text... #disappear.\n" +
                    "    #It simply becomes a comment.;\n" +
                    "#Question 6: Starting a question with a #-sign hides it from the game entirely;\n" +
                    "\n" +
                    "*Suggestion;\n" +
                    "Question 1\n" +
                    "    #Answer 1;\n" +
                    "Question 2\n" +
                    "    #Answer 2;\n" +
                    "Question 3\n" +
                    "    #Answer 3;\n" +
                    "Question 4\n" +
                    "    #Answer 4;\n" +
                    "Question 5\n" +
                    "    #Answer 5;\n" +
                    "\n" +
                    "*Suggestion (same);\n" +
                    "Question 1\n" +
                    "    #Answer 1;\n" +
                    "Question 2\n" +
                    "    #Answer 2;\n" +
                    "Question 3\n" +
                    "    #Answer 3;\n" +
                    "Question 4\n" +
                    "    #Answer 4;\n" +
                    "Question 5\n" +
                    "    #Answer 5;";
}
