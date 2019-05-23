/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.Serializable;

/**
 *
 * @author kismo
 */
public class Command implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String title;
    private int tempo;
    private int transposition;
    private int serial;
    private String song;
    private String lyrics;

    public Command(String type, String title, int tempo, int transposition,
            int serial, String song, String lyrics) {
        this.type = type;
        this.title = title;
        this.tempo = tempo;
        this.transposition = transposition;
        this.serial = serial;
        this.song = song;
        this.lyrics = lyrics;
    }

    public Command(Command msg) {
        this.type = msg.getType();
        this.title = msg.getTitle();
        this.tempo = msg.getTempo();
        this.transposition = msg.getTransposition();
        this.serial = msg.getSerial();
        this.song = msg.getSong();
        this.lyrics = msg.getLyrics();
    }

    public Command() {
        this.type = "TYPE";
        this.title = "TITLE";
        this.tempo = -1;
        this.transposition = -1;
        this.serial = -1;
        this.song = "x";
        this.lyrics = "x";
    }
    
    public Command(String dataToConvert){
        String[] tmp = dataToConvert.split("_");
        this.type = tmp[0];
        this.title = tmp[1];
        this.tempo = Integer.parseInt(tmp[2]);
        this.transposition = Integer.parseInt(tmp[3]);
        this.serial = Integer.parseInt(tmp[4]);
        this.song = tmp[5];
        this.lyrics = tmp[6];
    }

    @Override
    public String toString() {
        return type + " " + title + " " + tempo + " " + transposition + " " + serial;
    }

    public String getConvertFormat() {
        return type + "_" + title + "_" + tempo + "_" + transposition
                + "_" + serial + "_" + song + "_" + lyrics;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public int getTransposition() {
        return transposition;
    }

    public void setTransposition(int transposition) {
        this.transposition = transposition;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

}
