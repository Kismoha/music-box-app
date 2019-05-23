/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import messages.Command;
import messages.Note;
import server.main.MusicBox;

/**
 *
 * @author kismo
 */
public class MusicBoxClient implements AutoCloseable {

    private Socket me;
    private PrintWriter out;
    private BufferedReader in;
    private final Scanner sysIn;
    private Thread toServer;
    private Thread fromServer;
    private MidiChannel instrument;
    private Synthesizer synt;

    public MusicBoxClient() throws IOException, InterruptedException, Exception {
        sysIn = new Scanner(System.in);
        createSocket();
        createStreams();
        synt = MidiSystem.getSynthesizer();
        synt.open();
        instrument = synt.getChannels()[0];
        start();
    }

    private void start() throws InterruptedException, Exception {
        ///////////////////// TOSERVER ////////////////////////
        ///////////////////////////////////////////////////////
        toServer = new Thread(() -> {
            boolean running = true;

            while (running) {
                Command msg;
                do {
                    System.out.println("Command:");
                    msg = commandHandler(sysIn.nextLine());
                } while (msg.getType().equals("TYPE"));

                if (msg.getType().equals("exit")) {
                    running = false;
                    toServer.interrupt();
                    break;
                }

                out.println(msg.getConvertFormat());
                out.flush();

                System.out.println("Message sent.");
            }
        });
        ///////////////////////////////////////////////////////
        ///////////////////// TOSERVER ////////////////////////

        ////////////// FROMSERVER ////////////////////////
        //////////////////////////////////////////////////
        fromServer = new Thread(() -> {
            boolean running = true;
            int prevNote = -1;
            while (running) {
                try {
                    Note msg = new Note(true,in.readLine());
                    if (msg.getNote().contains("playing")) {
                        System.out.println(msg.getNote());
                    } else if (msg.getNote().equals("FIN")) {
                        System.out.println("FIN");
                    } else {
                        System.out.println(msg.getSyllable());
                        try {
                            instrument.noteOff(prevNote);
                        } catch (NullPointerException e) {

                        }
                        System.out.println(msg.getMidiValue());
                        instrument.noteOn(msg.getMidiValue(), msg.getActualLength());
                        prevNote = msg.getMidiValue();
                    }
                } catch (IOException ex) {
                    System.out.println("IOException while reading from server");
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
        out = new PrintWriter(me.getOutputStream());
        in = new BufferedReader(new InputStreamReader(me.getInputStream()));
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
            song = sysIn.nextLine();
        } while (song.equals("") || song.equals("SONG"));
        return song;
    }

    private String lyricsReader() {
        String lyrics = "LYRICS";
        do {
            System.out.println("Please enter the lyrics");
            lyrics = sysIn.nextLine();
        } while (lyrics.equals(""));
        return lyrics;
    }

    private String titleReader() {
        String title = "TITLE";
        do {
            System.out.println("Please enter a title");
            title = sysIn.nextLine();
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
