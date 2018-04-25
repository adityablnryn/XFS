package peer;

import server.TrackingServer;

import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class XFSPeer extends UnicastRemoteObject implements Peer {

    private static int numPeers = 0;

    private int peerId;
    private File dir;
    private File files[];
    private int load = 0;
    private String trackingServerURL = "//localhost/ts";
    private String peerURL;

    //TODO: Implement reading latency matrix from file
    private HashMap<String,Float> latencyMap = new HashMap<>();

    public XFSPeer() throws RemoteException{
        try{
            TrackingServer ts = (TrackingServer) Naming.lookup(trackingServerURL);

            peerId = ts.getNextPeerId();
            if(peerId != -1){
                peerURL = "peer" + peerId;
                Naming.rebind(peerURL, this);
                dir  = new File("./src/peer/data/peer" + peerId);
                dir.mkdir();
                populateLatencyMap();
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

    /**
     *
     * @param fileName
     * @return
     */

    public FileDownloadBundle download(String fileName){

        try {
            preDownload();
            TrackingServer ts = (TrackingServer) Naming.lookup(trackingServerURL);
            Set<String> availablePeers = ts.find(fileName);
            String optimalPeer = selectOptimalPeer(availablePeers);
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

    /**
     * To select the optimal peer based on load and latency using min-max normalization
     * @param availablePeers
     * @return
     */
    private String selectOptimalPeer(Set<String> availablePeers){
        int   minLoad = Integer.MAX_VALUE,
                maxLoad = Integer.MIN_VALUE;
        String minPeer = "";
        float minLatency = Float.MAX_VALUE, maxLatency = Float.MIN_VALUE, minScore = Float.MAX_VALUE;
        try {
            /*
            First calculate the min and max values for latency and load among the available peers
             */

            for(String peer : availablePeers){
                Peer p = (Peer) Naming.lookup(peer);
                int currLoad = p.getLoad();
                float currLatency = latencyMap.get(peer);
                minLoad = Math.min(minLoad,currLoad);
                minLatency = Math.min(minLatency,currLatency);
                maxLoad = Math.max(maxLoad,currLoad);
                maxLatency = Math.max(maxLatency,currLatency);
            }

            /*
            Using the above values, calculate the score using normalized values
            and select the peer with the lowest score.
             */

            for(String peer : availablePeers){
                Peer p = (Peer) Naming.lookup(peer);
                int currLoad = p.getLoad();
                float currLatency = latencyMap.get(peer);
                float normalizedLoad = minMaxNormalize((float) currLoad,(float) minLoad,(float) maxLoad);
                float normalizedLatency = minMaxNormalize(currLatency,minLatency,maxLatency);
                float currScore = normalizedLatency + normalizedLoad ;
                if(currScore < minScore){
                    minScore = currScore;
                    minPeer = peer;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return minPeer;

    }

    private void populateLatencyMap(){
        try{
            Scanner scanner = new Scanner(new FileReader("./src/peer/latency.txt"));
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String fields[] = line.split(";");
                if(fields[0].equals(peerURL)){
                    latencyMap.put(fields[1],Float.parseFloat(fields[2]));
                }
            }
            System.out.println("INFO: Latency map successfully populated");
        }
        catch (Exception e){
            e.printStackTrace();
        }
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

    private float minMaxNormalize(float value, float min, float max){
        return (float) (value - min)/(max - min);
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
