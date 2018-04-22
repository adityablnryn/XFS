import peer.XFSPeer;
import peer.Peer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {

    public static void main(String[] args) {
	// write your code here
        try {
            LocateRegistry.createRegistry(1099);
            XFSPeer peer1 = new XFSPeer();
            XFSPeer peer2 = new XFSPeer();

            Peer p1 = (Peer) Naming.lookup("peer0");
            p1.ping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
