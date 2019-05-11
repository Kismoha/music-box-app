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
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final LinkedList<Command> messages;
    private Thread incoming;
    private Thread player;
    private boolean isClientActive;

    public ClientConnection(ConnectionsManager mgr, MusicStorage storage, Socket client) throws IOException {
        this.client = client;
        out = new ObjectOutputStream(this.client.getOutputStream());
        in = new ObjectInputStream(this.client.getInputStream());
        this.mgr = mgr;
        this.storage = storage;
        this.mgr.addConn(this);
        messages = new LinkedList<>();
    }

    public void startCommunication() {
        incoming = new Thread(() -> {
            isClientActive = true;
            boolean running = true;
            while (running) {
                try {
                    Command inMsg = (Command) in.readObject();
                    commandHandler(inMsg);
                } catch (IOException ex) {
                    System.out.println("Communication problem");
                    running = false;
                } catch (ClassNotFoundException ex) {
                    System.out.println("Message problem");
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
                System.out.println("PLAYING " + serial);
                player = new Thread(() -> {
                    int noteCounter = 0;
                    int syllableCounter = 0;
                    while (storage.isPlayed(serial) && isClientActive) {
                        try {
                            int[] properties = storage.getTempoAndTransposition(serial);
                            Note note = storage.getNote(index, noteCounter,
                                    syllableCounter);
                            if (note.getNote().equals("FIN")) {
                                out.writeObject(note);
                                out.flush();
                                break;
                            } else if (note.getNote().equals("REP")) {
                                int repeat = 0;
                                int repeatFrom = 0;
                                String[] temp = note.getLength().split(";");
                                repeat = Integer.parseInt(temp[1]);
                                repeatFrom = Integer.parseInt(temp[0]);
                                for (int i = repeat; i > 0; i--) {
                                    for (int j = repeatFrom; j > 0; j--) {
                                        Note rep = storage.getNote(index,
                                                noteCounter - j * 2,
                                                syllableCounter - j + 1);
                                        out.writeObject(rep);
                                        out.flush();
                                        Thread.sleep(properties[0] * Integer.parseInt(rep.getLength()));
                                    }
                                }
                                noteCounter += 2;
                                syllableCounter++;
                            } else if (note.getNote().equals("R")) {
                                Thread.sleep(properties[0] * Integer.parseInt(note.getLength()));
                                noteCounter += 2;
                            } else {
                                out.writeObject(note);
                                out.flush();
                                Thread.sleep(properties[0] * Integer.parseInt(note.getLength()));
                                noteCounter += 2;
                                syllableCounter++;
                            }

                        } catch (IOException ex) {
                            System.out.println("Exception while trying to write to client");
                            isClientActive = false;
                        } catch (InterruptedException e) {
                            System.out.println("Exception while trying to write to client");
                            isClientActive = false;
                            try {
                                out.writeObject(new Note("FIN"));
                                out.flush();
                            } catch (IOException ex) {
                                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                                isClientActive = false;
                            }
                        }
                    }
                    storage.stopMusic(serial);
                    System.out.println("out thread end");
                });
                player.start();
                break;
            case "change":
                storage.changeTempoAndTransposition(command.getSerial(),
                        command.getTempo(), command.getTransposition());
                break;
            case "stop":
                if (player.isAlive()) {
                    player.interrupt();
                    try {
                        out.writeObject(new Note("FIN"));
                        out.flush();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                        isClientActive = false;
                    }
                }
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
