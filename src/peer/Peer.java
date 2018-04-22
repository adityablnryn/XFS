package peer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Peer extends Remote{
    public void ping() throws RemoteException;
}
