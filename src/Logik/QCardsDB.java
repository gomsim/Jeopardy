package Logik;

import java.util.*;

public class QCardsDB {

    private TreeMap<String, LinkedList<String>> table = new TreeMap<>();
    private int maxNumQuestions;

    void add(String category, String question){
        if (!table.containsKey(category))
            addCategory(category);
        addQuestion(category,question);
    }
    int getMaxQuestions(){
        return maxNumQuestions;
    }
    int[] getNumQuestions(){
        int[] result = new int[table.size()];
        int i = 0;
        for (LinkedList<String> category: table.values())
            result[i++] = category.size();
        return result;
    }
    String get(String category, int question){
        return table.get(category).get(question);
    }
    String[] getCategories(){
        return table.keySet().toArray(new String[table.size()]);
    }

    private void addCategory(String name){
        table.put(name,new LinkedList<>());
    }
    private void addQuestion(String category, String question){
        table.get(category).add(question);
        maxNumQuestions = Math.max(table.get(category).size(), maxNumQuestions);
    }

    public String toString(){
        String printResult = "";
        for (Map.Entry<String, LinkedList<String>> entry: table.entrySet()){
            printResult += "----- " + entry.getKey() + " -----\n";
            for (String q: entry.getValue()){
                printResult += "\t\""+ q +"\"\n";
            }
            printResult += "\n";
        }
        return printResult;
    }
}
