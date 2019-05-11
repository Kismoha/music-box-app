/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.storage;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author kismo
 */
public class MusicStorage {
    
    public static int serialNumbering = 0;
    
    private final LinkedList<Music> storedMusic = new LinkedList<>();
    private final LinkedList<Aired> currentlyAiring = new LinkedList<>();
    
    public synchronized void addMusic(Music music){
        boolean found = false;
        for(Music m : storedMusic){
            if(m.getTitle().equals(music.getTitle())){
                storedMusic.remove(m);
                storedMusic.add(music);
                found = true;
            }
        }
        if(!found){
            storedMusic.add(music);
        }
    }
    
    public synchronized void playMusic(int index, int tempo, int transposition){
        Aired airedMusic =  new Aired(serialNumbering,index,tempo,
        transposition, new Thread());
        increaseSerial();
        currentlyAiring.add(airedMusic);
    }
    
    public synchronized void addLyrics(String title,String lyrics){
        for(Music music : storedMusic){
            if(music.getTitle().equals(title)){
                music.setLyrics(Arrays.asList(lyrics.split(" ")));
            }
        }
    }
    
    public synchronized void stopMusic(int serial){
        for(Aired a : currentlyAiring){
            if(a.getSerial() == serial){
                currentlyAiring.remove(a);
            }
        }
    }
    
    private void increaseSerial(){
        serialNumbering++;
    }
}
