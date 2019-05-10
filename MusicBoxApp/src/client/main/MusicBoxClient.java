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
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.Message;
import server.main.MusicBox;

/**
 *
 * @author kismo
 */
public class MusicBoxClient implements AutoCloseable {

    private Socket me;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread toServer;
    private Thread fromServer;

    public MusicBoxClient() throws IOException, InterruptedException, Exception {
        createSocket();
        createStreams();
        start();
    }

    public void start() throws InterruptedException, Exception {
        toServer = new Thread(() -> {
            boolean running = true;
            try (Scanner sysIn = new Scanner(System.in)) {
                while (running) {
                    Message msg = new Message();
                    System.out.println("Command:");
                    msg.setType(sysIn.next());
                    System.out.println("Content:");
                    msg.setContent(sysIn.next());

                    if (msg.getType().equals("exit")) {
                        running = false;
                        break;
                    }

                    out.writeObject(msg);
                    out.flush();

                    System.out.println("Message sent.");
                }
            } catch (IOException ex) {
                System.out.println("IOException while trying to write to server.");
                ex.printStackTrace();
                running = false;
            }
        });

        fromServer = new Thread(() -> {
            boolean running = true;
            while (running) {
                try {
                    Message msg = (Message) in.readObject();
                    System.out.println("Üzenet: " + msg.getType() + " "
                            + msg.getContent());
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

    @Override
    public void close() throws Exception {
        out.close();
        in.close();
        me.close();
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
