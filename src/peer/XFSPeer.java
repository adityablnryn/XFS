package peer;

import server.TrackingServer;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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

    public FileDownloadBundle download(String fileName){

        try {
            preDownload();
            // TODO:
            // read from relevant file
            // convert contents to byte array
            // create wrapper class
            // return
            postDownload();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean getFileFromPeer(String url, String filename) {
        try {
            Peer peerWithFile = (Peer) Naming.lookup(url);
            FileDownloadBundle fileDownloadBundle = peerWithFile.download(filename);
            // TODO: extract contents, write to file, update tracking servers
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        // implement UI here - Aditya
        try {
            XFSPeer thisPeer = new XFSPeer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
