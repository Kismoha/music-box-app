/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.connectionHandlers;

import java.util.LinkedList;

/**
 *
 * @author kismo
 */
public class ConnectionsManager {
    
    interface IterationFunction {
        void apply(ClientConnection c);
    }
    
    private LinkedList<ClientConnection> connections = new LinkedList<>();
    
    public synchronized void addConn(ClientConnection c){
        connections.add(c);
    }
    
    public synchronized void remConn(ClientConnection c){
        connections.remove(c);
    }
    
    public synchronized void forEachConn(IterationFunction f){
        for(ClientConnection c : connections){
            f.apply(c);
        }
    }
    
}
