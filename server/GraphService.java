package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GraphService extends Remote {
    // Method to process a batch of operations
    List<Integer> processBatch(List<String> operations) throws RemoteException;
    
    // Method to initialize the graph with initial edges
    void initializeGraph(List<String> edges) throws RemoteException;
    
    // Method to signal ready state
    void signalReady() throws RemoteException;
} 