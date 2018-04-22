package peer;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class XFSPeer extends UnicastRemoteObject implements Peer {

    private static int numPeers = 0;

    private int peerId;
    private File dir;

    public XFSPeer() throws RemoteException{
        try{
            peerId = numPeers;
            Naming.rebind("peer" + numPeers, this);
            dir  = new File("./src/peer/data/peer1");
            dir.mkdir();
            System.out.println("INFO: Peer " + numPeers++ + " bound successfully");
        }
        catch (Exception e){
            System.out.println("ERROR: Peer " + numPeers + " failed to bind");
            e.printStackTrace();
        }
    }

    public void ping(){
        System.out.println("INFO: Peer " + this.peerId + ": PING!");
    }



}
