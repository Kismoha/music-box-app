/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.storage;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import messages.Note;

/**
 *
 * @author kismo
 */
public class MusicStorage {

    public static int serialNumbering = 0;

    private final LinkedList<Music> storedMusic = new LinkedList<>();
    private final LinkedList<Aired> currentlyAiring = new LinkedList<>();

    public void addMusic(Music music) {
        boolean found = false;
        synchronized (storedMusic) {
            for (Music m : storedMusic) {
                if (m.getTitle().equals(music.getTitle())) {
                    storedMusic.remove(m);
                    storedMusic.add(music);
                    found = true;
                }
            }
            if (!found) {
                storedMusic.add(music);
            }
        }

    }

    public boolean isPlayed(int serial) {
        synchronized (currentlyAiring) {
            for (Aired a : currentlyAiring) {
                if (a.getSerial() == serial) {
                    return true;
                }
            }
        }
        return false;
    }

    public int[] getTempoAndTransposition(int serial) {
        int[] stuff = {0, 0};
        synchronized (currentlyAiring) {
            for (Aired a : currentlyAiring) {
                if (a.getSerial() == serial) {
                    stuff[0] = a.getTempo();
                    stuff[1] = a.getTransposition();
                    return stuff;
                }
            }
        }
        return stuff;
    }
    
    public void changeTempoAndTransposition(int serial, int tempo, int transposition) {
        synchronized (currentlyAiring) {
            for (Aired a : currentlyAiring) {
                if (a.getSerial() == serial) {
                    a.setTempo(tempo);
                    a.setTransposition(transposition);
                }
            }
        }
    }

    public synchronized Note getNote(int index, int note, int syllable) {
        Note temp = new Note("FIN");
        try {
            temp.setNote(storedMusic.get(index).getNote(note));
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            temp.setSyllable(storedMusic.get(index).getSyllable(syllable));
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            temp.setLength(storedMusic.get(index).getNote(note + 1));
        } catch (IndexOutOfBoundsException e) {
        }
        return temp;
    }

    public int getMusicIndexByTitle(String title) {
        int index = -1;
        synchronized (storedMusic) {
            for (Music m : storedMusic) {
                if (m.getTitle().equals(title)) {
                    index = storedMusic.indexOf(m);
                }
            }
        }
        return index;
    }

    public synchronized int playMusic(int index, int tempo, int transposition) {
        Aired airedMusic = new Aired(serialNumbering, index, tempo,
                transposition);
        increaseSerial();
        currentlyAiring.add(airedMusic);
        return --serialNumbering;
    }

    public void stopMusic(int serial) {
        synchronized (currentlyAiring) {
            for (Aired a : currentlyAiring) {
                if (a.getSerial() == serial) {
                    currentlyAiring.remove(a);
                }
            }
        }
    }

    public void addLyrics(String title, String lyrics) {
        synchronized (storedMusic) {
            for (Music music : storedMusic) {
                if (music.getTitle().equals(title)) {
                    music.setLyrics(Arrays.asList(lyrics.split(" ")));
                }
            }
        }
    }

    private synchronized void increaseSerial() {
        serialNumbering++;
    }
}
