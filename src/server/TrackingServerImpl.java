package server;

import peer.Peer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TrackingServerImpl extends UnicastRemoteObject implements TrackingServer {

    private final String PEER_LIST_PATH = "./src/server/peerListFile.txt";
    private final String DATA_SEPARATION = ":";
    private int nextPeerId = 0;
    private ConcurrentHashMap<String, Set<Integer>> filePeersMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> peerAddressMap = new ConcurrentHashMap<>();

    public TrackingServerImpl() throws RemoteException {
        try {
            Naming.rebind("ts", this);
            getPeerListFromFile();
            populateFilePeersMap();
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
        writePeerAddressMapToFile();
        return false;
    }

    public Set<String> find(String fileName) {
        Set<String> peers = new HashSet<>();
        if (filePeersMap.containsKey(fileName)) {
            for (int peer : filePeersMap.get(fileName)) {
                peers.add(peerAddressMap.get(peer));
            }
            return peers;
        }
        System.out.println("ERROR: File not present in any peer");
        return peers;
    }


    public Set<String> getFileList() {
        return filePeersMap.keySet();
    }

    public boolean updateFileListForClient(int peerId, Set<String> files) {
        // look at each file and add peer Id to the set (brute force)
        for(String file : files) {
            if(!filePeersMap.containsKey(file)) {
                filePeersMap.put(file, new HashSet<>());
            }
            filePeersMap.get(file).add(peerId);
        }
        return true;
    }


    /*
     * Function used to re-populate file-peer mapping after tracking server crash
     */
    private void populateFilePeersMap() {
        Peer peer;
        for(Map.Entry<Integer, String> entry : peerAddressMap.entrySet()) {
            try {
                peer = (Peer) Naming.lookup(entry.getValue());
                updateFileListForClient(entry.getKey(), peer.getListOfFiles(true));
                System.out.println("RECOVERY: Updated File list for peer "+entry.getKey());
            } catch (Exception e) {
                peerAddressMap.remove(entry.getKey());
                System.out.println("RECOVERY: Peer "+entry.getKey()+" not available");
            }
        }
    }


    /**
     * Function to write the Peer Map to a file.
     */
    private void writePeerAddressMapToFile() {
        try {
            Files.write(Paths.get(PEER_LIST_PATH), () -> peerAddressMap.entrySet().stream()
                    .<CharSequence>map(e -> e.getKey() + DATA_SEPARATION + e.getValue())
                    .iterator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to read from the file and populate the Peer Map.
     */
    private void getPeerListFromFile() {
        try (Stream<String> lines = Files.lines(Paths.get(PEER_LIST_PATH))) {
            lines.filter(line -> line.contains(DATA_SEPARATION)).forEach(
                    line -> peerAddressMap.putIfAbsent(Integer.valueOf(line.split(DATA_SEPARATION)[0]), line.split(DATA_SEPARATION)[1])
            );
        } catch (IOException e) {
            System.out.println("INFO: No Peer List File Found");
        }
    }
}