import peer.XFSPeer;
import peer.Peer;
import server.TrackingServer;
import server.TrackingServerImpl;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {

    public static void main(String[] args) {
	// write your code here
        try {
            LocateRegistry.createRegistry(1099);
            TrackingServerImpl ts = new TrackingServerImpl();

            XFSPeer peer1 = new XFSPeer();
            XFSPeer peer2 = new XFSPeer();
            XFSPeer peer3 = new XFSPeer();
            XFSPeer peer4 = new XFSPeer();
            XFSPeer peer5 = new XFSPeer();
            XFSPeer peer6 = new XFSPeer();
            XFSPeer peer7 = new XFSPeer();
            XFSPeer peer8 = new XFSPeer();
            XFSPeer peer9 = new XFSPeer();
            XFSPeer peer10 = new XFSPeer();

            Peer p1 = (Peer) Naming.lookup("peer0");
            p1.ping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
