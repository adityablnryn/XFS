package server;

import peer.XFSPeer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TrackingServer extends Remote{
    public int addPeer() throws RemoteException;
}
