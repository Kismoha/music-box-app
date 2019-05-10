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
import java.util.LinkedList;
import messages.Message;

/**
 *
 * @author kismo
 */
public class ClientConnection implements AutoCloseable {

    private final ConnectionsManager mgr;
    private final Socket client;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final LinkedList<Message> messages;
    private Thread incoming;
    private Thread outgoing;
    private boolean isClientActive;

    public ClientConnection(ConnectionsManager mgr, Socket client) throws IOException {
        this.client = client;
        out = new ObjectOutputStream(this.client.getOutputStream());
        in = new ObjectInputStream(this.client.getInputStream());
        this.mgr = mgr;
        this.mgr.addConn(this);
        messages = new LinkedList<>();
    }

    public void startCommunication() {
        incoming = new Thread(() -> {
            isClientActive = true;
            boolean running = true;
            while (running) {
                Message inMsg = new Message();
                try {
                    inMsg = (Message) in.readObject();
                } catch (IOException ex) {
                    System.out.println("Communication problem");
                    running = false;
                } catch (ClassNotFoundException ex) {
                    System.out.println("Message problem");
                    running = false;
                }
                final Message outMsg = new Message(inMsg);
                mgr.forEachConn((ClientConnection c) -> {
                    synchronized (c.messages) {
                        c.messages.add(outMsg);
                        c.messages.notifyAll();
                    }
                });
            }

            synchronized (messages) {
                isClientActive = false;
                messages.clear();
                messages.notifyAll();
            }

            try {
                outgoing.join();
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

        outgoing = new Thread(() -> {
            while (isClientActive) {
                synchronized (messages) {
                    try {
                        messages.wait();
                    } catch (InterruptedException ex) {
                        System.out.println("wait Interrupted");
                    }
                    while (!messages.isEmpty() && isClientActive) {
                        try {
                            out.writeObject(messages.getFirst());
                            out.flush();
                            messages.removeFirst();
                        } catch (IOException ex) {
                            System.out.println("Exception while trying to write to client");
                            isClientActive = false;
                        }
                    }
                }
            }
            System.out.println("out thread end");
        });
        outgoing.start();
    }

    @Override
    public void close() throws Exception {
        out.close();
        in.close();
        client.close();
        mgr.remConn(this);
    }
}
