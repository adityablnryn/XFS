package peer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface Peer extends Remote{
    public void ping() throws RemoteException;
    public FileDownloadBundle download(String fileName) throws RemoteException;
    public Set<String> getListOfFiles(boolean rebindTrackingServer) throws RemoteException;
    public int getLoad() throws RemoteException;
}
