/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.main;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kismo
 */
public class OctaveCalculator {

    private final Map<String, Integer> basicValues;

    public OctaveCalculator() {
        basicValues = new HashMap<>();
        fillBasicValues();
    }

    private void fillBasicValues() {
        basicValues.put("C", 12);
        basicValues.put("D", 14);
        basicValues.put("E", 16);
        basicValues.put("F", 17);
        basicValues.put("G", 19);
        basicValues.put("A", 21);
        basicValues.put("B", 23);
    }

    public int getMidiValue(String note, int UserTransposition) {
        if(note.contains("R")){
            return -1;
        }
        String key;
        int selfTransposition = 0;
        int offset = note.contains("b") ? -1 : note.contains("#") ? 1 : 0;
        if(note.contains("/")){
            String[] temp = note.split("/");
            key = temp[0];
            selfTransposition = Integer.parseInt(temp[1]);
        }else{
            key = note;
        }
        
        return (basicValues.get(key.substring(0, 1)) +
                (selfTransposition + UserTransposition) * 12) + offset;
    }

}
