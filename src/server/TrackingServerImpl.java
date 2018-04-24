package server;

import java.io.File;
import java.io.PrintStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrackingServerImpl extends UnicastRemoteObject implements TrackingServer {

    private int nextPeerId = 0;
    private ConcurrentHashMap<String, Set<Integer>> filePeersMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> peerAddressMap = new ConcurrentHashMap<>();
    private File peerListFile = new File("./src/server/peerListFile.txt");

    public TrackingServerImpl() throws RemoteException {
        try {
            Naming.rebind("ts", this);
            getPeerListFromFile();
            System.out.println("INFO: Tracking server bound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNextPeerId() {
        synchronized (this) {
            return nextPeerId++;
        }
    }

    public boolean addPeer(int id, String url) {
        peerAddressMap.put(id, url);
        return false;
    }

    public List<String> find(String filename) {
        //create a list with all relevant peer urls
        // Step 1 - filePeersMap.get(filename)
        // Step 2 - for each element in step 1, peerAddressMap.get()
        return null;
    }

    public boolean updateFileListForClient(int peerId, List<String> files) {
        // look at each file and add peer Id to the set (brute force)
        return false;
    }

    // TODO - update method to write id and url
    private void writePeerToFile(){
        System.out.println("INFO: Writing list to file");
        try {
            PrintStream fileStream = new PrintStream(peerListFile);
            /*for(String peer: peers) {
                fileStream.println(peer);
            }*/
            fileStream.close();
            System.out.println("INFO: Peer list successfully written into file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO - update the map
    private void getPeerListFromFile(){
        System.out.println("INFO: Reading from peerListFile");
        /*try{
            Scanner scanner = new Scanner(new FileReader("./src/server/peerListFile.txt"));
            while(scanner.hasNextLine()){
                peers.add(scanner.nextLine());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }*/
    }
}