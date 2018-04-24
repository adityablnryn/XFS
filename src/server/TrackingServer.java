package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface TrackingServer extends Remote{
    public int getNextPeerId() throws RemoteException;
    public Set<Integer> getPeerListForFile(String fileName) throws RemoteException;
    public boolean addPeer(int id, String url) throws RemoteException;
    public List<String> find(String filename) throws RemoteException;
    public boolean updateFileListForClient(int peerId, List<String> files) throws RemoteException;
}
