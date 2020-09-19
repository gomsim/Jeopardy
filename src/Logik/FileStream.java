package Logik;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStream {

    private String[] tokens;
    private int counter;

    FileStream(String filePath) throws IOException{
        String contents = readFile(filePath);
        contents = contents.replace(System.lineSeparator(),"");
        tokens = contents.split(";");
    }

    private static String readFile(String filePath) throws IOException{
        String gameString;
        gameString = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
        return gameString;
    }

    String next(){
        return tokens[counter++];
    }
    boolean hasNext(){
        return counter < tokens.length;
    }
}
