package Logik;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;

public class GameFileHandler {

    //TODO: EXAMPLE GAME
    private static final String EXAMPLE_GAME =
            "questionTime = 15;\n" +
                    "extraTime = 5;\n" +
                    "titleFontSize = 50;";

    private FileStream fileStream;

    GameFileHandler(){
        String filePath = takeUserInput();
        loadGameFile(filePath);
    }
    FileStream getFileStream(){
        return fileStream;
    }
    private void loadGameFile(String filePath){
        boolean successful = false;
        while(!successful){
            try{
                fileStream = new FileStream(filePath);
                successful = true;
            }catch(Exception e){
                filePath = takeUserInput("<html>Kan inte hitta <i>" + filePath + "</i></html>!\n\n" +
                        "<html>Exempelspel har skapats: <i>example.game</i></html>.\n" +
                        "Starta exempelspelet eller försök igen.", JOptionPane.INFORMATION_MESSAGE);
                createExampleGameFile();
            }
        }

    }
    private String takeUserInput(String message, int messageType){
        if (!message.isEmpty())
            message += "\n\n";
        String input = JOptionPane.showInputDialog(null, message + "Spelnamn: ", "Välj spel", messageType);
        input = input.toLowerCase();
        if (!input.endsWith(".game"))
            input += ".game";
        return input;
    }
    private String takeUserInput(){
        return takeUserInput("", JOptionPane.QUESTION_MESSAGE);
    }

    private void createExampleGameFile(){
        FileWriter writer = null;
        try {
            writer = new FileWriter("example.game");
            writer.write(EXAMPLE_GAME);
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
}
