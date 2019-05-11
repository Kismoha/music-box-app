/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import messages.Command;
import messages.Note;
import server.main.MusicBox;

/**
 *
 * @author kismo
 */
public class MusicBoxClient implements AutoCloseable {

    private Socket me;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Scanner sysIn;
    private Thread toServer;
    private Thread fromServer;

    public MusicBoxClient() throws IOException, InterruptedException, Exception {
        sysIn = new Scanner(System.in);
        createSocket();
        createStreams();
        start();
    }

    private void start() throws InterruptedException, Exception {
        ///////////////////// TOSERVER ////////////////////////
        ///////////////////////////////////////////////////////
        toServer = new Thread(() -> {
            boolean running = true;
            try {
                while (running) {

                    Command msg;
                    do {
                        System.out.println("Command:");
                        msg = commandHandler(sysIn.next());
                    } while (msg.getType().equals("TYPE"));

                    if (msg.getType().equals("exit")) {
                        running = false;
                        toServer.interrupt();
                        break;
                    }

                    out.writeObject(msg);
                    out.flush();

                    System.out.println("Message sent.");
                }
            } catch (IOException ex) {
                System.out.println("IOException while trying to write to server.");
                running = false;
            }
        });
        ///////////////////////////////////////////////////////
        ///////////////////// TOSERVER ////////////////////////

        ////////////// FROMSERVER ////////////////////////
        //////////////////////////////////////////////////
        fromServer = new Thread(() -> {
            boolean running = true;
            while (running) {
                try {
                    Note msg = (Note) in.readObject();
                    System.out.println(msg.toString());
                } catch (IOException ex) {
                    System.out.println("IOException while reading from server");
                    running = false;
                    toServer.interrupt();
                } catch (ClassNotFoundException ex) {
                    System.out.println("ClassNotFoundException"
                            + "while reading from server");
                    running = false;
                    toServer.interrupt();
                }
            }
        });
        //////////////////////////////////////////////////
        ////////////// FROMSERVER ////////////////////////

        toServer.start();
        fromServer.start();

        toServer.join();
        fromServer.join();

        close();
    }

    private void createSocket() throws IOException {
        me = new Socket("localhost", MusicBox.PORT);
    }

    private void createStreams() throws IOException {
        out = new ObjectOutputStream(me.getOutputStream());
        in = new ObjectInputStream(me.getInputStream());
    }

    private Command commandHandler(String command) {
        Command msg = new Command();
        switch (command) {
            case "add":
                msg.setType(command);
                msg.setTitle(titleReader());
                msg.setSong(songReader());
                break;
            case "addlyrics":
                msg.setType(command);
                msg.setTitle(titleReader());
                msg.setLyrics(lyricsReader());
                break;
            case "play":
                msg.setType(command);
                msg.setTempo(tempoReader());
                msg.setTransposition(tranpositionReader());
                msg.setTitle(titleReader());
                break;
            case "change":
                msg.setType(command);
                msg.setSerial(serialReader());
                msg.setTempo(tempoReader());
                msg.setTransposition(tranpositionReader());
                break;
            case "stop":
                msg.setType(command);
                msg.setSerial(serialReader());
                break;
            case "exit":
                msg.setType(command);
                break;
            default:
                System.out.println("There's no such command. "
                        + "Available commands: add, addlyrics, "
                        + "play, stop, change, exit");
        }
        return msg;
    }

    private String songReader() {
        String song = "SONG";
        do {
            System.out.println("Please enter the song");
            System.out.println("Warning: There's no verification on the entered"
                    + " song, so it should be correct. Otherwise the"
                    + " playing of the song will stop at the incorrect note!");
            song = sysIn.nextLine();
        } while (song.equals("") || song.equals("SONG"));
        return song;
    }
    
    private String lyricsReader() {
        String lyrics = "LYRICS";
        do {
            System.out.println("Please enter the lyrics");
            System.out.println("Warning: There's no verification on the entered"
                    + " lyrics, so it should be correct. Otherwise the"
                    + " syllables might end up at the wrong note");
            lyrics = sysIn.nextLine();
        } while (lyrics.equals(""));
        return lyrics;
    }

    private String titleReader() {
        String title = "TITLE";
        do {
            System.out.println("Please enter a title");
            title = sysIn.next();
        } while (title.equals("TITLE") || title.equals(""));
        return title;
    }

    private int tempoReader() {
        int tempo = -1;
        do {
            System.out.println("Please enter a tempo");
            tempo = sysIn.nextInt();
        } while (tempo < 1);
        return tempo;
    }

    private int tranpositionReader() {
        int transpoition = -1;
        do {
            System.out.println("Please enter a tranposition");
            transpoition = sysIn.nextInt();
        } while (transpoition < -100 || transpoition > 100);
        return transpoition;
    }

    private int serialReader() {
        int serial = -1;
        do {
            System.out.println("Please enter a serial");
            serial = sysIn.nextInt();
        } while (serial < 0);
        return serial;
    }

    @Override
    public void close() throws Exception {
        out.close();
        in.close();
        me.close();
        sysIn.close();
    }

    public static void main(String[] args) {
        try {
            new MusicBoxClient();
        } catch (InterruptedException ex) {
            System.out.println("InterruptedException while running");
        } catch (Exception ex) {
            System.out.println("Excpetion while running");
        }
    }
}
