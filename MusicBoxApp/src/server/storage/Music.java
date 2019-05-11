/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.storage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kismo
 */
public class Music {
    private String title;
    private ArrayList<String> notes;
    private ArrayList<String> lyrics;
    
    public Music(String title, List arrayAsList){
        this.title = title;
        this.notes = new ArrayList<>(arrayAsList);
        this.lyrics = new ArrayList<>();
    }
    
    public String getNote(int index){
        String note = "FIN";
        try{
            note = notes.get(index);
        }catch(IndexOutOfBoundsException e){
            System.out.println("No note");
        }
        return note;
    }
    
    public String getSyllable(int index){
        String syllable = "???";
        try{
            syllable = lyrics.get(index);
        }catch(IndexOutOfBoundsException e){
            System.out.println("No syllable");
        }
        return syllable;
    }

    public ArrayList<String> getLyrics() {
        return lyrics;
    }

    public void setLyrics(ArrayList<String> lyrics) {
        this.lyrics = lyrics;
    }
    
    

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }
    
    
}
