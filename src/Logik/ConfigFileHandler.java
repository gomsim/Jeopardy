package Logik;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that reads 'config.txt' upon startup and configures the game accordingly.
 * Recreates a config file from DEFAULT_CONFIG if file is broken or missing.
 */
class ConfigFileHandler {

    private static final String DEFAULT_CONFIG =
            "questionTime = 15;\n" +
            "extraTime = 5;\n" +
            "titleFontSize = 50;";

    int questionTime, extraTime;
    int titleFontSize;

    ConfigFileHandler(){
        loadConfigurationFile();
    }
    private void loadConfigurationFile(){
        FileStream iterator;
        try{
            iterator = new FileStream("config.txt");
            interpretConfigText(iterator);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Konfigurationsfil återskapas från standardvärden.","Konfigurationsfil korrupt eller saknas!",JOptionPane.WARNING_MESSAGE);
            createDefaultConfigFile();
            loadConfigurationFile();
        }
    }
    private void interpretConfigText(FileStream iterator){
        while(iterator.hasNext()){
            String[] currentAttribute = iterator.next().split("=");
            String field = currentAttribute[0].trim();
            String value = currentAttribute[1].trim();

            switch (field){
                case "questionTime":
                    questionTime = Integer.parseInt(value);
                    break;
                case "extraTime":
                    extraTime = Integer.parseInt(value);
                    break;
                case "titleFontSize":
                    titleFontSize = Integer.parseInt(value);
                    break;
                default:
                    throw new IllegalStateException("Invalid field: " + field);
            }
        }
    }
    private void createDefaultConfigFile(){
        FileWriter writer = null;
        try {
            writer = new FileWriter("config.txt");
            writer.write(DEFAULT_CONFIG);
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
