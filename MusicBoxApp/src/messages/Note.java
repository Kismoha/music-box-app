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
public class Note implements Serializable {
    
    private String note;
    private int midiValue;
    private String length;
    private String syllable;
    private int actualLength;

    private static final long serialVersionUID = 1L;

    public Note(String note, String syllable, String length) {
        this.note = note;
        this.length = length;
        this.syllable = syllable;
    }

    public Note(Note note) {
        this.note = note.getNote();
        this.syllable = note.getSyllable();
        this.length = note.getLength();
    }

    public Note(String fin) {
        this.note = fin;
        this.syllable = "???";
        this.length = "0";
        this.actualLength = 0;
    }
    
    @Override
    public String toString() {
        return note + " " + syllable;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    
    
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSyllable() {
        return syllable;
    }

    public void setSyllable(String syllable) {
        this.syllable = syllable;
    }

    public int getMidiValue() {
        return midiValue;
    }

    public void setMidiValue(int midiValue) {
        this.midiValue = midiValue;
    }

    public int getActualLength() {
        return actualLength;
    }

    public void setActualLength(int actualLength) {
        this.actualLength = actualLength;
    }
    
}
