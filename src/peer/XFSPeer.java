package peer;

import server.TrackingServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class XFSPeer extends UnicastRemoteObject implements Peer {

    private int peerId;
    private File rootDir;
    private Set<String> fileList; //TODO - use this variable
    private int load = 0;
    private String trackingServerURL = "//localhost/ts";
    private TrackingServer ts;
    private String peerURL;

    //TODO: Implement reading latency matrix from file
    private HashMap<String,Float> latencyMap = new HashMap<>();

    public XFSPeer() throws RemoteException {
        try{
            ts = (TrackingServer) Naming.lookup(trackingServerURL);

            peerId = ts.getNextPeerId();
            if(peerId != -1){
                peerURL = "peer" + peerId; //TODO - update peerURL
                Naming.rebind(peerURL, this);
                rootDir  = new File("./src/peer/data/peer" + peerId);
                rootDir.mkdir();
                populateLatencyMap();
                ts.addPeer(this.peerId, peerURL);
                System.out.println("INFO: Peer " + peerId + " bound successfully");
            }
            else{
                System.out.println("ERROR: Unable to create peer");
            }
        }
        catch (Exception e){
            System.out.println("ERROR: Peer " + peerId + " failed to bind");
            e.printStackTrace();
        }
    }

    public void ping(){
        System.out.println("INFO: Peer " + this.peerId + ": PING!");
    }

    public Set<String> getListOfFiles() {
        return fileList;
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
            // find relevant file
            Path path = Paths.get("file_path_here"); //TODO - add file path

            // convert contents to byte array
            byte[] fileContents = Files.readAllBytes(path);

            // create wrapper class
            FileDownloadBundle fileDownloadBundle = new FileDownloadBundle();
            fileDownloadBundle.fileName = fileName;
            fileDownloadBundle.fileContents = fileContents;
            Checksum checksum = new CRC32();
            checksum.update(fileContents,0,fileContents.length);
            fileDownloadBundle.checksum = checksum.getValue();

            // return
            postDownload();
            return fileDownloadBundle;
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

    private boolean getFileFromPeers(String fileName) {
        try {
            Set<String> availablePeers = ts.find(fileName);
            String optimalPeer = selectOptimalPeer(availablePeers);
            Peer peerWithFile = (Peer) Naming.lookup(optimalPeer);
            FileDownloadBundle fileDownloadBundle = peerWithFile.download(fileName);
            //TODO - add checksum verification
            FileOutputStream fos = new FileOutputStream("path_here"+fileDownloadBundle.fileName); //TODO - add path
            fos.write(fileDownloadBundle.fileContents);
            fileList.add(fileDownloadBundle.fileName);
            ts.updateFileListForClient(this.peerId, this.fileList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private float minMaxNormalize(float value, float min, float max){
        return (value - min)/(max - min);
    }

    public static void main(String[] args) {
        try {
            XFSPeer thisPeer = new XFSPeer();
            while (true) {
                System.out.println("1 - GetList of files & 2 - Download file");
                System.out.print("Enter your choice: ");
                Scanner in = new Scanner(System.in);
                int choice = in.nextInt();
                switch (choice){
                    case 1:
                        //TODO - Get Files and print
                        break;
                    case 2:
                        //TODO - Get Files and print
                        System.out.print("Enter name of file to download: ");
                        String fileToDownload = in.nextLine();
                        thisPeer.getFileFromPeers(fileToDownload);
                        break;
                    default:
                        System.out.println("Invalid Choice");
                }
                System.out.println("\n\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
