/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.connectionHandlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.Command;
import messages.Note;
import client.main.OctaveCalculator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import server.storage.Music;
import server.storage.MusicStorage;

/**
 *
 * @author kismo
 */
public class ClientConnection implements AutoCloseable {

    private final ConnectionsManager mgr;
    private final MusicStorage storage;
    private final Socket client;
    private final PrintWriter out;
    private final BufferedReader in;
    private final LinkedList<Command> messages;
    private Thread incoming;
    private Thread player;
    private boolean isClientActive;
    private final OctaveCalculator midiCalc;

    public ClientConnection(ConnectionsManager mgr, MusicStorage storage, Socket client) throws IOException {
        this.client = client;
        out = new PrintWriter(this.client.getOutputStream());
        in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        this.mgr = mgr;
        this.storage = storage;
        this.mgr.addConn(this);
        messages = new LinkedList<>();
        midiCalc = new OctaveCalculator();
    }

    public void startCommunication() {
        incoming = new Thread(() -> {
            isClientActive = true;
            boolean running = true;
            while (running) {
                try {
                    String asd = in.readLine();
                    System.out.println(asd);
                    Command inMsg = new Command(asd);
                    commandHandler(inMsg);
                } catch (IOException ex) {
                    System.out.println("Communication problem");
                    running = false;
                }
            }

            synchronized (messages) {
                isClientActive = false;
                messages.clear();
                messages.notifyAll();
            }

            try {
                player.join();
            } catch (InterruptedException ex) {
                System.out.println("Join interrupted");
            }

            try {
                close();
            } catch (Exception ex) {
                System.out.println("Close error");
            }

            System.out.println("in thread end");
        });
        incoming.start();
    }

    private void commandHandler(Command command) {
        switch (command.getType()) {
            case "add":
                storage.addMusic(new Music(command.getTitle(),
                        Arrays.asList(command.getSong().split(" "))));
                break;
            case "addlyrics":
                storage.addLyrics(command.getTitle(), command.getLyrics());
                break;
            case "play":
                int index = storage.getMusicIndexByTitle(command.getTitle());
                int serial = storage.playMusic(index, command.getTempo(),
                        command.getTransposition());
                //Playing serial message
                //Note initNote = new Note("playing " + serial);
                out.println("playing " + serial);
                out.flush();
                player = new Thread(() -> {
                    int noteCounter = 0;
                    int syllableCounter = 0;
                    while (storage.isPlayed(serial) && isClientActive) {
                        try {
                            int[] properties = storage.getTempoAndTransposition(serial);
                            Note note = storage.getNote(index, noteCounter,
                                    syllableCounter);
                            if (note.getNote().equals("FIN")) {
                                break;
                            } else if (note.getNote().equals("REP")) {
                                int repeat = 0;
                                int repeatFrom = 0;
                                String[] temp = note.getLength().split(";");
                                repeat = Integer.parseInt(temp[1]);
                                repeatFrom = Integer.parseInt(temp[0]);
                                for (int i = repeat; i > 0; i--) {
                                    for (int j = repeatFrom; j > 0; j--) {
                                        properties = storage.getTempoAndTransposition(serial);
                                        Note repNote = storage.getNote(index,
                                                noteCounter - j * 2,
                                                syllableCounter - j + 1);
                                        if (repNote.getNote().equals("R")) {
                                            Thread.sleep(properties[0] * Integer.parseInt(repNote.getLength()));
                                            continue;
                                        }
                                        repNote.setMidiValue(midiCalc.getMidiValue(repNote.getNote(), properties[1]));
                                        repNote.setActualLength(properties[0] * Integer.parseInt(repNote.getLength()));
                                        out.println(repNote.getDataToConvert());
                                        out.flush();
                                        Thread.sleep(properties[0] * Integer.parseInt(repNote.getLength()));
                                    }
                                }
                                noteCounter += 2;
                                syllableCounter++;
                            } else if (note.getNote().equals("R")) {
                                Thread.sleep(properties[0] * Integer.parseInt(note.getLength()));
                                noteCounter += 2;
                            } else {
                                note.setMidiValue(midiCalc.getMidiValue(note.getNote(), properties[1]));
                                note.setActualLength(properties[0] * Integer.parseInt(note.getLength()));
                                out.println(note.getDataToConvert());
                                out.flush();
                                Thread.sleep(properties[0] * Integer.parseInt(note.getLength()));
                                noteCounter += 2;
                                syllableCounter++;
                            }

                        } catch (InterruptedException e) {
                            System.out.println("Exception while trying to write to client");
                            isClientActive = false;
                            out.flush();
                        }
                    }
                    storage.stopMusic(serial);
                    out.println(new Note(false,"FIN").getDataToConvert());
                    out.flush();
                    System.out.println("out thread end");
                });
                player.start();
                break;
            case "change":
                storage.changeTempoAndTransposition(command.getSerial(),
                        command.getTempo(), command.getTransposition());
                break;
            case "stop":
                /*if (player.isAlive()) {
                    player.interrupt();
                    out.println(new Note(false,"FIN").getDataToConvert());
                    out.flush();
                }*/
                storage.stopMusic(command.getSerial());
                break;
            case "exit":

                break;
            default:
                System.out.println("Command type error.");
        }
    }

    private boolean isActualNote(String note) {
        if (note.contains("/")) {
            return true;
        }
        if (note.equals("REP") || note.equals("R")) {
            return false;
        }
        try {
            Integer.parseInt(note);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    @Override
    public void close() throws Exception {
        out.close();
        in.close();
        client.close();
        mgr.remConn(this);
    }
}
