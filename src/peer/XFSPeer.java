package peer;

import server.TrackingServer;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

public class XFSPeer extends UnicastRemoteObject implements Peer {

    private static int numPeers = 0;

    private int peerId;
    private File dir;
    private File files[];
    private int load = 0;
    private String trackingServerURL = "//localhost/ts";

    public XFSPeer() throws RemoteException{
        try{
            TrackingServer ts = (TrackingServer) Naming.lookup(trackingServerURL);

            peerId = ts.getNextPeerId();
            if(peerId != -1){
                Naming.rebind("peer" + peerId, this);
                dir  = new File("./src/peer/data/peer" + peerId);
                dir.mkdir();
                System.out.println("INFO: Peer " + numPeers++ + " bound successfully");
            }
            else{
                System.out.println("ERROR: Unable to create peer");
            }
        }
        catch (Exception e){
            System.out.println("ERROR: Peer " + numPeers + " failed to bind");
            e.printStackTrace();
        }
    }

    public void ping(){
        System.out.println("INFO: Peer " + this.peerId + ": PING!");
    }

    public Set<String> getListOfFiles() {
        // return list of files
        return null;
    }

    public File[] getFiles() {
        files = dir.listFiles();
        return files;
    }

    public int getLoad() {
        return load;
    }

    private synchronized void preDownload(){
        load++;
    }

    private synchronized void postDownload(){
        load--;
    }

    public void download(String fileName){

        try {
            preDownload();
            // implement this
            postDownload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // implement UI here - Aditya
    }

}
