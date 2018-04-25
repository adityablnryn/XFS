package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface TrackingServer extends Remote{
    public int getNextPeerId() throws RemoteException;
    public boolean addPeer(int id, String url) throws RemoteException;
    public Set<String> find(String filename) throws RemoteException;
    public Set<String> getFileList() throws RemoteException;
    public boolean updateFileListForClient(int peerId, Set<String> files) throws RemoteException;
}
