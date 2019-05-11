/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.connectionHandlers.ClientConnection;
import server.connectionHandlers.ConnectionsManager;
import server.storage.MusicStorage;

/**
 *
 * @author kismo
 */
public class MusicBox {
    
    public static final int PORT = 40000;
    
    public static void main(String[] args){
        
        ConnectionsManager mgr = new ConnectionsManager();
        MusicStorage storage = new MusicStorage();
        
        try(ServerSocket server = new ServerSocket(PORT);){
            while(true){
                ClientConnection conn = new ClientConnection(mgr,storage,server.accept());
                conn.startCommunication();
            }
        } catch (IOException ex) {
            System.out.println("IO ex");
        }
        
    }
}
