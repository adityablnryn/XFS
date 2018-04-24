package server;

import peer.XFSPeer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TrackingServer extends Remote{
    public int getNextPeerId() throws RemoteException;
    public boolean addPeer(int id, String url) throws RemoteException;
    public List<String> find(String filename) throws RemoteException;
    public boolean updateFileListForClient(int peerId, List<String> files) throws RemoteException;
}
