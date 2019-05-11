/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.storage;

/**
 *
 * @author kismo
 */
public class Aired {
    private int serial;
    private int storageIndex;
    private int tempo;
    private int transposition;

    public Aired(int serial, int storageIndex, int tempo, int transposition) {
        this.serial = serial;
        this.storageIndex = storageIndex;
        this.tempo = tempo;
        this.transposition = transposition;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public int getStorageIndex() {
        return storageIndex;
    }

    public void setStorageIndex(int storageIndex) {
        this.storageIndex = storageIndex;
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
}
