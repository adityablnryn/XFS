package server;

import peer.XFSPeer;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class TrackingServerImpl extends UnicastRemoteObject implements TrackingServer {

    private int numPeers = 0;
    private Map<String, String> filesToClient = new HashMap<>();
    private Set<String> peers = new HashSet<>();
    private File peerListFile = new File("./src/server/peerListFile.txt");

    public TrackingServerImpl() throws RemoteException {
        try {
            Naming.rebind("ts", this);
            readFromFile();
            System.out.println("INFO: Tracking server bound");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public int addPeer() {
        int peerId = numPeers++;
        try {
            peers.add("peer" + peerId);
            writeToFile();
            System.out.println("INFO: Peer " + peerId + " added to tracking server");
            return peerId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void writeToFile(){
        System.out.println("INFO: Writing list to file");
        try {
            PrintStream fileStream = new PrintStream(peerListFile);
            for(String peer: peers) {
                fileStream.println(peer);
            }
            fileStream.close();
            System.out.println("INFO: Peer list successfully written into file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFromFile(){
        System.out.println("INFO: Reading from peerListFile");
        try{
            Scanner scanner = new Scanner(new FileReader("./src/server/peerListFile.txt"));
            while(scanner.hasNextLine()){
                peers.add(scanner.nextLine());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void finalize(){
        peerListFile.delete();
    }
}