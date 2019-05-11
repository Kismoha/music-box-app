/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.storage;

import java.util.LinkedList;

/**
 *
 * @author kismo
 */
public class MusicStorage {
    
    public static int serialNumbering = 0;
    
    private LinkedList<Music> storedMusic = new LinkedList<>();
    private LinkedList<Aired> currentlyAiring = new LinkedList<>();
    
    public synchronized void addMusic(Music music){
        storedMusic.add(music);
    }
    
    public synchronized void playMusic(int index){
        Aired airedMusic =  new Aired(serialNumbering,index, new Thread());
        increaseSerial();
        currentlyAiring.add(airedMusic);
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
