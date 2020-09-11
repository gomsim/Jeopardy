/**
 * @author Simon Gombrii
 * Welcome to Jeopardy! A game about fun and joy!
 */

import Logik.Program;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Start {

    public static void main(String[] args){
        String game = readGameFile();
        new Program(game);
    }
    private static String readGameFile(){
        String gameString;
        try{
            gameString = Files.readString(Path.of("game.txt"), StandardCharsets.UTF_8);
        }catch(IOException e){
            JOptionPane.showMessageDialog(null,"Spelfil saknas. Tänk på att spelfilen med namnet \"game.txt\" måste ligga i samma mapp som jar-filen Jeopardy!","Spelfil saknas!",JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Invalid gamefile!");
        }
        return gameString;
    }
}
