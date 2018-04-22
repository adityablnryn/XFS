package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TrackingServerImpl extends UnicastRemoteObject implements TrackingServer {
    public TrackingServerImpl() throws RemoteException{

    }
}
