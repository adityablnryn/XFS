package test;

import peer.Peer;
import peer.XFSPeer;

import java.rmi.RemoteException;

public class PeerDownloaderThread extends Thread {

    private Peer peer;
    public PeerDownloaderThread (Peer peerDownload) {
        this.peer = peerDownload;
    }
    @Override
    public void run() {
        try {
            this.peer.download("hello.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
