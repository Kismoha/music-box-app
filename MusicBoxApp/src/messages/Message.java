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
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String title;
    private int tempo;
    private int transposition;
    private int serial;

    public Message(String type, String title, int tempo, int transposition, int serial) {
        this.type = type;
        this.title = title;
        this.tempo = tempo;
        this.transposition = transposition;
        this.serial = serial;
    }

    public Message(Message msg) {
        this.type = msg.getType();
        this.title = msg.getTitle();
        this.tempo = msg.getTempo();
        this.transposition = msg.getTransposition();
        this.serial = msg.getSerial();
    }
    
    public Message() {
        this.type = "TYPE";
        this.title = "TITLE";
        this.tempo = -1;
        this.transposition = -1;
        this.serial = -1;
    }

    public String toString(){
        return type + " " + title + " " + tempo + " " + transposition + " " + serial;
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
    
    
}
